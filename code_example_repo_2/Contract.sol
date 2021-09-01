contract ERC20InvictusFund is OwnableUpgradeable, ERC20PresetMinterPauserUpgradeable, ERC2771ContextUpgradeable {
    using SafeERC20Upgradeable for ERC20Upgradeable;
    using SafeMathUpgradeable for uint256;

    bytes32 public constant BURNER_ROLE = keccak256('BURNER_ROLE');
    bytes32 public constant PRICING_ORACLE_ROLE = keccak256('PRICING_ORACLE_ROLE');
    bytes32 public constant ADJUSTABLE_ROLE = keccak256('ADJUSTABLE_ROLE');
    bytes32 public constant INVESTMENT_ORACLE_ROLE = keccak256('INVESTMENT_ORACLE_ROLE');
    
    enum Status{ QUEUED, PENDING_NEXT, DEALING_SUCCESS }

    //Add blockNumber to struct
    struct Investment {
        uint256 amount;
        uint256 timestamp;
        uint256 chainId;
        uint256 output;
        string investmentCurrency;
        string transactionHash;
        address participant;
    }

    //Add blockNumber to struct
    struct Redemption {
        uint256 amount;
        uint256 timestamp;
        string redemptionCurrency;
        address participant;
    }

    struct Currency{
        Price price;
        address contractAddress;
        uint256 decimals;
    }

    struct Price {
        uint256 numerator;
        uint256 denominator;
    }

    Redemption[] public redemptions;
    Investment[] public investments;

    //For tracking investmentOutputs
    //mapping of string(hash) => (output) => true/false
    mapping (string => mapping(uint256 => bool)) public investmentOutputs;
    mapping (string => Currency) public currencies;

    address public whitelistContract;
    uint256 public minTokenRedemption;
    uint256 public maxRedemptionsPerTx;
    uint256 public maxInvestmentsPerTx;
    uint256 public denominator;

    uint80 public cutOffTime;
    uint80 public dealingNonce;

    //Make sure all events emit the correct data
    //Add blockNumber
    event RedemptionRequest(uint256 amount, uint256 timestamp, string redemptionCurrency, address participant);
    //Add blockNumber
    event InvestmentRequest(uint256 amount, uint256 timestamp, string investmentCurrency, string transactionHash, address participant,  uint256 chainId, uint256 output);
    event Dealing(uint256 dealingNonce);
    event AddCurrency(string ticker);
    event UpdateCurrencyPrice(uint numerator, string ticker);
    event RedemptionEvent(uint256 eventId, uint256 TokenAmount, uint256 counterCurrencyAmount,string redemptionCurrency, address particpant, uint128 dealingNonce);
    event RedemptionFailed(uint256 eventId,uint256 TokenAmount, uint256 counterCurrencyAmount, string redemptionCurrency, address participant, uint256 dealingNonce);
    event InvestmentEvent(uint256 eventId, uint256 counterCurrencyAmount, uint256 tokenAmount,string investmentCurrency, string transactionHash, address participant, uint128 dealingNonce);
    event IncreaseTokenAmendment(address participant, uint256 amount, string reason, string txHash);
    event DecreaseTokenAmendment(address participant, uint256 amount, string reason, string txHash);
    event RedemptionsLeftInQueue(uint256 dealingNonce);
    event InvestmentsLeftInQueue(uint256 dealingNonce);
    event TokensClaimed(address indexed token, uint256 balance);
    event AddLiquidity(address indexed account, address indexed stableAddress, uint256 value);
    event RemoveLiquidity(address indexed account, uint256 value);


    ////////////////////////////////////////////////////////////////////// Initializer ////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @dev Initialize the contract.
     * @param name The name of the token.
     * @param symbol The symbol of the token, usually a shorter version of the name.
     * @param stableCurrencyNumerator The numerator for the initialized stableCurrency
     * @param stableContractInput The contract for initialized stable Currency
     * @param whitelistContractInput The whitelist contract address.
     * @param _cutOffTime The cut off time for invesmtents and redmeptions to be accepted for next dealing
     */
    function initializeInvictusFund(
        string memory name,
        string memory symbol,
        uint256 stableCurrencyNumerator,
        uint256 navPerTokenNumerator,
        uint256 stableCurrencyDecimals,
        address stableContractInput,
        address whitelistContractInput,
        address trustedForwarder,
        uint80 _cutOffTime
    ) public initializer {
        __ERC20PresetMinterPauser_init(name, symbol);
        __ERC2771Context_init(trustedForwarder);
        __Ownable_init();
        __Pausable_init();

        require(whitelistContractInput != address(0), "Invalid whitelist address");
        require(stableContractInput != address(0), "Invalid stablecoin address");
        whitelistContract = whitelistContractInput;
        
        minTokenRedemption = 1 ether;
        maxRedemptionsPerTx = 25;
        maxInvestmentsPerTx = 25;
        cutOffTime = _cutOffTime;
        dealingNonce = 0;
        denominator = 1 ether;

        string memory stableCurrency = ERC20Upgradeable(stableContractInput).symbol();

        //stable coin currency
        Price memory price;
        price.numerator = stableCurrencyNumerator;
        price.denominator = denominator;
        Currency memory currency;
        currency.contractAddress = stableContractInput;
        currency.price = price;
        currency.decimals = stableCurrencyDecimals;
        currencies[stableCurrency] = currency;
        //navPerToken
        Price memory navPerTokenPrice;
        navPerTokenPrice.numerator = navPerTokenNumerator;
        navPerTokenPrice.denominator = denominator;
        Currency memory navPerToken;
        navPerToken.price = navPerTokenPrice;
        navPerToken.decimals = 18;
        currencies[symbol] = navPerToken;
    }

    ////////////////////////////////////////////////////////////////////// External functions ////////////////////////////////////////////////////////////////////////////////////////////

    //Add redemptions to be processed during dealing
    function requestRedemption(uint256 withdrawAmount, string memory redemptionCurrency) external onlyWhitelisted {

        require(balanceOf(_msgSender()) >= withdrawAmount, 'Balance to low');
        require(withdrawAmount >= minTokenRedemption, 'Too few tokens');
        require(address(currencies[redemptionCurrency].contractAddress) != address(0), "Currency not supported");
        //reduce user token amount
        burn(withdrawAmount);
        redemptions.push(Redemption(withdrawAmount, block.timestamp, redemptionCurrency, _msgSender()));
        emit RedemptionRequest(withdrawAmount, block.timestamp, redemptionCurrency, _msgSender());
    }

    //Add investments to be processed during dealing
    function requestInvestment(uint256 amount, string memory investmentCurrency, string memory transactionHash, address participant, uint256 chainId , uint256 output) external onlyInvestmentOracle {
        require(currencies[investmentCurrency].price.numerator != 0, "Currency not supported");
        require(amount > 0, "investment cannot be zero");
        require(!investmentOutputs[transactionHash][output], "output not unique");
        
        investmentOutputs[transactionHash][output] = true;
        investments.push(Investment(amount, block.timestamp, chainId, output, investmentCurrency, transactionHash, participant));
        emit InvestmentRequest(amount, block.timestamp, investmentCurrency, transactionHash,  participant, chainId, output);
    }

    //Update currencies and deal redemptions and investments
    function dealing(string[] calldata _currencies, uint256[] calldata _rates) external onlyPricingOracle {
        require(_currencies.length > 0, "no currencies");
        require(_rates.length > 0, "no rates");
        updateCurrenciesPrices(_currencies, _rates);

        if(_handleInvestmentsDealing() && _handleRedemptionsDealing()){
            updateDealingNonce(dealingNonce+1);
            updateCutOfftime(cutOffTime + 1 days);
        }

        emit Dealing(dealingNonce);
    }

    /// Allows owner to claim any ERC20 tokens.
    function claimTokens(ERC20Upgradeable token) external onlyOwner {
        require(address(token) != address(0), 'Invalid address');
        uint256 balance = token.balanceOf(address(this));

        token.safeTransfer(owner(), token.balanceOf(address(this)));
        emit TokensClaimed(address(token), balance);
    }

    /**
     * @dev Allows the owner to burn a specific amount of tokens on a participant's behalf.
     * @param value The amount of tokens to be burned.
     */
    //Should this be the burner
    function burnForParticipant(address account, uint256 value) external onlyBurner {
        _burn(account, value);
    }

    /// Removes liquidity, allowing owner to transfer stable coin to the fund wallet.
    function removeLiquidity(uint256 amount, string memory ticker) external onlyPricingOracle {
        require(address(currencies[ticker].contractAddress) != address(0), "currency not supported");
        ERC20Upgradeable(currencies[ticker].contractAddress).safeTransfer(_msgSender(), amount);
        emit RemoveLiquidity(_msgSender(), amount);
    }

    //set the maximum number of redemptions to be processed per dealing transaction
    function setRedemptionsPerTx(uint256 newRedemptionsPerTx) external onlyOwner {
        require(newRedemptionsPerTx > 0, "param > 0");
        maxRedemptionsPerTx = newRedemptionsPerTx;
    }

    //set the maximum number of investements to be processed per dealing transaction
    function setMaxInvestmentsPerTx(uint256 newMaxInvestmentsPerTx) external onlyOwner {
        require(newMaxInvestmentsPerTx > 0, "param > 0");
        maxInvestmentsPerTx = newMaxInvestmentsPerTx;
    }

    // Add a stable currency with contractAddress (Usually a currency used for redeeming)
    function addTransferCurrency(address stableContractAddress, uint256 numerator, uint256 decimals) external onlyPricingOracle {
        require(numerator > 0, "numerator != 0");
        require(address(stableContractAddress) != address(0), "Invalid address");
        currencies[ERC20Upgradeable(stableContractAddress).symbol()] = Currency(Price(numerator, denominator), stableContractAddress, decimals);
        emit AddCurrency(ERC20Upgradeable(stableContractAddress).symbol());
    }

    //Add a stable currency without a contract (Usually currency used for investments)
    function addCurrency(string memory ticker, uint256 numerator, uint256 decimals) external onlyPricingOracle {
        require(numerator > 0, "numerator != 0");
        currencies[ticker] = Currency(Price(numerator, denominator), address(0), decimals);
        emit AddCurrency(ticker);
    }

    //Increase a user's token amount incase of incorrect dealing
    function increaseTokenAmendment(address participant, uint256 amount, string memory reason, string memory txHash) external onlyAdjustable {
        require(amount > 0, "mint != 0");
        mint(participant, amount);
        emit IncreaseTokenAmendment(participant, amount, reason, txHash);
    }

    //Decrease user token amount incase of incorrect dealing
    function decreaseTokenAmendment(address participant, uint256 amount, string memory reason, string memory txHash) external onlyAdjustable {
        require(amount > 0, "burnFrom != 0");
        //Should this be burnFrom
        burnFrom(participant, amount);
        emit DecreaseTokenAmendment(participant, amount, reason, txHash);
    }


    ////////////////////////////////////////////////////////////////////// Public functions ////////////////////////////////////////////////////////////////////////////////////////////

    function burnFrom(address account, uint256 amount) public virtual override onlyBurner{
        super.burnFrom(account, amount);
    }

    function isBurner(address participant) public view returns (bool){
        return hasRole(BURNER_ROLE, participant);
    }

    //Update currencies with parralel arrays: currencies and rates
    function updateCurrenciesPrices(string[] memory _currencies, uint256[] memory rates) public onlyPricingOracle{
        //require, can't set currency if it doesn't exist.
        for(uint256 i = 0 ; i < _currencies.length; i++){
            require(currencies[_currencies[i]].price.numerator != 0, "currency not supported");
            require(rates[i] > 0, "price != zero");
            currencies[_currencies[i]].price = Price(rates[i], denominator);
            emit UpdateCurrencyPrice(rates[i], _currencies[i]);
        }
    }

    //update the cuttof time for investments and redemptions to be dealt
    function updateCutOfftime(uint80 time) public onlyPricingOracle{
        cutOffTime = time;
    }

    //update dealing nonce
    function updateDealingNonce(uint80 _dealingNonce) public onlyPricingOracle{
        dealingNonce = _dealingNonce;
    }

    //transfer the amount of dealing
    function dealRedemption(uint256 eventId, address participant,uint256 counterCurrencyAmount, uint256 tokenAmount, string memory redemptionCurrency) public onlyPricingOracle {
        address stableContract = currencies[redemptionCurrency].contractAddress;
        if(ERC20Upgradeable(stableContract).balanceOf(address(this)) < counterCurrencyAmount){
            //should this rather be a notEnoughLiquidity event
            _mint(participant, tokenAmount);
            emit RedemptionFailed(eventId, tokenAmount, counterCurrencyAmount, redemptionCurrency, participant, dealingNonce);
            return;
        } else {
            ERC20Upgradeable(stableContract).safeTransfer(participant, counterCurrencyAmount);
            emit RedemptionEvent(eventId, tokenAmount, counterCurrencyAmount, redemptionCurrency, participant, dealingNonce);
        }
    }
    
    //mint the tokens of a dealing
    function dealInvestment(
        uint256 eventId,
        address participant, 
        uint256 counterCurrencyAmount,
        uint256 fundTokenAmount,
        string memory investmentCurrency,
        string memory transactionHash
        ) public onlyPricingOracle{
        _mint(participant, fundTokenAmount);
        emit InvestmentEvent(eventId, counterCurrencyAmount, fundTokenAmount, investmentCurrency, transactionHash, participant, dealingNonce);
    }

    ////////////////////////////////////////////////////////////////////// Internal functions ////////////////////////////////////////////////////////////////////////////////////////////

    //calculate the tokenAmount by accepting currencyAmount
    function calculateTokenAmount(string memory redemptionCurrency, uint256 counterCurrencyAmount) internal view returns(uint256) {
        Currency memory currency = currencies[redemptionCurrency];
        Currency memory contractToken = currencies[symbol()];
        return counterCurrencyAmount.mul(currency.price.numerator).div(contractToken.price.numerator);
    }

    //calculate currencyAount by accepting a tokenAmount
    function calculateCounterCurrencyAmount(string memory investmentCurrency, uint256 tokenAmount) internal view returns(uint256) {
        Currency memory currency = currencies[investmentCurrency];
        Currency memory contractToken = currencies[symbol()];
        uint256 tokens = ceil(tokenAmount.mul(contractToken.price.numerator).div(currency.price.numerator),10**(contractToken.decimals - currency.decimals));
        if(currency.decimals > contractToken.decimals){
            return tokens * (10**(currency.decimals - contractToken.decimals));
        }else {
            return tokens / (10**(contractToken.decimals - currency.decimals));
        }
    }
    
    //rounding values up by using round limit
    function ceil(uint value, uint roundLimit) internal pure  returns (uint r) {
        return ((value + roundLimit - 1) / roundLimit) * roundLimit;
    }

    function _beforeTokenTransfer(
        address _from,
        address _to,
        uint256 _amount
    ) internal virtual override {
        _checkWhitelist(_to);
        super._beforeTokenTransfer(_from, _to, _amount);
    }

    //Handle the redemptions during dealing
    function _handleRedemptionsDealing() internal returns (bool){
        uint256 numberOfRedemptions = MathUpgradeable.min(redemptions.length, maxRedemptionsPerTx);
        uint256 startingIndex = redemptions.length;
        uint256 endingIndex = redemptions.length.sub(numberOfRedemptions);
        uint256 tokens; 
        address participant;
        string memory redemptionCurrency;
        uint256 counterCurrencyAmount;
        Redemption memory redemption;
        address stableContract;
        for(uint i = startingIndex; i > endingIndex; i--){
            redemption = redemptions[i-1];
            if(redemption.timestamp < cutOffTime &&  InvictusWhitelist(whitelistContract).isWhitelisted(redemption.participant)){
                participant = redemption.participant;
                tokens = redemption.amount;
                redemptionCurrency = redemption.redemptionCurrency;
                redemptions.pop();
                counterCurrencyAmount = calculateCounterCurrencyAmount(redemptionCurrency, tokens);
                stableContract = currencies[redemptionCurrency].contractAddress;
                //dealRedmption
                dealRedemption(i-1,participant, counterCurrencyAmount, tokens, redemptionCurrency);
            }
        }
        uint256 nextStartingIndex = endingIndex <= 1 ? 0 : endingIndex - 1;
        if(redemptions.length != 0 && redemptions[nextStartingIndex].timestamp < cutOffTime){
            emit RedemptionsLeftInQueue(dealingNonce);
            return false;
        }
        return true;
    }

    //Handle the investments during dealing
    function _handleInvestmentsDealing() internal returns (bool){
        uint256 numberOfInvestments = MathUpgradeable.min(investments.length, maxInvestmentsPerTx);
        uint256 startingIndex = investments.length;
        uint256 endingIndex = investments.length.sub(numberOfInvestments);
        uint256 counterCurrencyAmount;
        address participant;
        string memory investmentCurrency;
        string memory transactionHash;
        uint256 fundTokenAmount;
        Investment memory investment;

        for(uint i = startingIndex; i > endingIndex; i --){
            investment = investments[i-1];
            if(investment.timestamp < cutOffTime &&  InvictusWhitelist(whitelistContract).isWhitelisted(investment.participant)){
                participant = investment.participant;
                counterCurrencyAmount = investment.amount;
                investmentCurrency = investment.investmentCurrency;
                transactionHash = investment.transactionHash;
                fundTokenAmount = calculateTokenAmount(investmentCurrency, counterCurrencyAmount);
                investments.pop();
                //deal investment
                dealInvestment(i-1,participant, counterCurrencyAmount, fundTokenAmount, investmentCurrency, transactionHash);     
            }
        }
        uint256 nextStartingIndex = endingIndex <= 1 ? 0 : endingIndex - 1;
        if(investments.length != 0 && investments[nextStartingIndex].timestamp < cutOffTime){
            emit InvestmentsLeftInQueue(dealingNonce);
            return false;
        }
        return true;
    }

    function _msgSender() internal view virtual override(ContextUpgradeable, ERC2771ContextUpgradeable) returns (address sender) {
        return ERC2771ContextUpgradeable._msgSender();
    }

    function _msgData() internal view virtual override(ContextUpgradeable, ERC2771ContextUpgradeable) returns (bytes calldata) {
        return ERC2771ContextUpgradeable._msgData();
    }

    function versionRecipient() external pure returns (string memory) {
        return "1";
    }

    function setTrustedForwarder(address trustedForwarder) public {
        _trustedForwarder = trustedForwarder;
    }

    ////////////////////////////////////////////////////////////////////// Modifiers functions ////////////////////////////////////////////////////////////////////////////////////////////
    
    modifier onlyWhitelisted() {
        _checkWhitelist(_msgSender());
        _;
    }

    modifier onlyBurner(){
        _roleRequire(BURNER_ROLE, 'ERC20InvictusFund: must have burner', _msgSender());
        _;
    }

    modifier onlyPricingOracle() {
        _roleRequire(PRICING_ORACLE_ROLE, 'ERC20InvictusFund: must have pricing oracle role', _msgSender());
        _;
    }

    modifier onlyAdjustable() {
        _roleRequire(ADJUSTABLE_ROLE, 'ERC20InvictusFund: must have adjustable role', _msgSender());
        _;
    }

    modifier onlyInvestmentOracle() {
        _roleRequire(INVESTMENT_ORACLE_ROLE, 'ERC20InvictusFund: must have investment oracle role', _msgSender());
        _;
    }

    //check the role with error msg
    function _roleRequire(bytes32 role ,string memory errorMsg, address participant) internal view {
        require(hasRole(role, participant), errorMsg);
    }

    //check if whitelisted
    function _checkWhitelist(address participant) internal view {
        require(
            InvictusWhitelist(whitelistContract).isWhitelisted(participant),
            'Must be whitelisted'
        );
    }
}