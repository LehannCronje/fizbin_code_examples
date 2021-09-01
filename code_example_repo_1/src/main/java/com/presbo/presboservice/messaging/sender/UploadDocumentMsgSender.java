package com.presbo.presboservice.messaging.sender;

import com.presbo.presboservice.dto.req.UploadDocumentReqDto;
import com.presbo.presboservice.dto.res.UploadDocumentResDto;
import com.presbo.presboservice.utility.MessagingUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class UploadDocumentMsgSender {

    @Autowired
    MessagingUtility messagingUtility;

    public UploadDocumentResDto sendUploadMessage(String exchange, String routingKey, UploadDocumentReqDto data) throws Exception {


        UploadDocumentResDto result = (UploadDocumentResDto) messagingUtility.convertSendAndReceiveAsTypeAndRemoveTypeIdHeader(exchange, routingKey, data, UploadDocumentResDto.class);

        return result;

    }

}