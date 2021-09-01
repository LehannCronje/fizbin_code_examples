package com.presbo.presboservice.messaging.sender;

import com.presbo.presboservice.dto.req.GenerateReportReqDto;
import com.presbo.presboservice.dto.res.GenerateReportResDto;
import com.presbo.presboservice.utility.MessagingUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GenerateReportMsgSender {

    @Autowired
    MessagingUtility messagingUtility;

    public GenerateReportResDto sendGenerateReportMessage(String exchange, String routingKey, GenerateReportReqDto data) throws Exception {


        GenerateReportResDto result = (GenerateReportResDto) messagingUtility.convertSendAndReceiveAsTypeAndRemoveTypeIdHeader(exchange, routingKey, data, GenerateReportResDto.class);
        return result;

    }

}