package com.presbo.presboservice.messaging.sender;

import com.presbo.presboservice.dto.req.GetReportReqDto;
import com.presbo.presboservice.dto.res.GetReportResDto;
import com.presbo.presboservice.utility.MessagingUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetReportMsgSender {

    @Autowired
    MessagingUtility messagingUtility;

    public GetReportResDto getReportMessage(String exchange, String routingKey, GetReportReqDto data) throws Exception {


        GetReportResDto result = (GetReportResDto) messagingUtility.convertSendAndReceiveAsTypeAndRemoveTypeIdHeader(exchange, routingKey, data, GetReportResDto.class);

        return result;

    }

}
