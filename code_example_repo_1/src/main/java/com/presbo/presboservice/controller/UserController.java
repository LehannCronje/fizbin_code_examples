package com.presbo.presboservice.controller;

import com.presbo.presboservice.dto.req.UpdateUserResourcesDto;
import com.presbo.presboservice.dto.req.UserReqDto;
import com.presbo.presboservice.dto.res.UserResDto;
import com.presbo.presboservice.entity.Role;
import com.presbo.presboservice.entity.User;
import com.presbo.presboservice.entity.UserRole;
import com.presbo.presboservice.repository.RoleRepository;
import com.presbo.presboservice.repository.UserRepository;
import com.presbo.presboservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    RoleRepository roleRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserService userService;

    @GetMapping("/bootstrap")
    public void bootstrapUsers(){
        this.createUserRoles();
        this.createTestUser("Lehann");
        this.createTestUser("Stefan");
        this.createTestUser("Willem");

    }

    @GetMapping("/test")
    public String testConnection(){

        return "Hello World!!";
    }

    @GetMapping("/me")
    public ResponseEntity currentUser(@AuthenticationPrincipal UserDetails userDetails) {
        Map<Object, Object> model = new HashMap<>();
        User user = userRepo.findByUsername(userDetails.getUsername()).orElse(null);
        try {
            model.put("username", userDetails.getUsername());
            model.put("roles", userDetails.getAuthorities().stream().map(a -> ((GrantedAuthority) a).getAuthority())
                    .collect(toList()));
            if(user.getOrganisation() != null){
                model.put("organisationId", user.getOrganisation().getId());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok(model);
    }

    @PostMapping("/create")
    public void createUser(@RequestBody UserReqDto userReqDto) {

        userService.createUser(userReqDto);

    }


    //Should be filtered by role that is passed through.
    @GetMapping("/mobile/org/{orgId}")
    public List<UserResDto> getAllMobileUsersByOrganisationId(@PathVariable("orgId") Long orgId){
        return userService.getAllMobileUsersByOrganisation(orgId);
    }

    //Mobile
    @PostMapping("/resources/add")
    public void updateResources(@RequestBody UpdateUserResourcesDto updateUserResourcesDto){

        if(!updateUserResourcesDto.getAddedResources().isEmpty()){
            userService.addResources(updateUserResourcesDto.getAddedResources(), updateUserResourcesDto.getUsername());
        }
        if(!updateUserResourcesDto.getRemovedResources().isEmpty()) {
            userService.removeResources(updateUserResourcesDto.getRemovedResources(), updateUserResourcesDto.getUsername());
        }

    }

    @GetMapping("/enable/{username}")
    public void enableUser(@PathVariable("username") String username){

        userService.enableUser(username);

    }

    @GetMapping("/disable/{username}")
    public void disableUser(@PathVariable("username") String username){

        userService.disableUser(username);

    }

    @PostMapping("/change-password")
    public ResponseEntity changePassword(@RequestBody UserReqDto userReqDto){
        userService.changePassword(userReqDto);
        return ok("Success");

    }

    private Long createTestUser(String username){

        User user = new User();
        user.setUsername(username);
        user.setPassword(this.passwordEncoder.encode("password"));
        user.setIsActive(true);

        Role usr = roleRepo.findByName("ROLE_USER").get();
        Role admin = roleRepo.findByName("ROLE_ADMIN").get();
        UserRole userRole = new UserRole();
        UserRole adminRole = new UserRole();

        userRole.setRole(usr);
        userRole.setUser(user);
        adminRole.setRole(admin);
        adminRole.setUser(user);

        user.setUserRoles(new ArrayList<>());
        List<UserRole> userRoles = user.getUserRoles();
        userRoles.add(userRole);
        userRoles.add(adminRole);
        user.setUserRoles(userRoles);

        return userRepo.save(user).getId();
    }

    private String createUserRoles(){
        Role admin = new Role();
        Role user = new Role();
        Role mobile = new Role();

        admin.setName("ROLE_ADMIN");
        user.setName("ROLE_USER");
        mobile.setName("ROLE_MOBILE");

        List<Role> userRoles = Arrays.asList(admin, user, mobile);

        roleRepo.saveAll(userRoles);
        return "success";
    }
}