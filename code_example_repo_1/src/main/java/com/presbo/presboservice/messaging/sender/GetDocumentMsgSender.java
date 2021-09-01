package com.presbo.presboservice.messaging.sender;

import com.presbo.presboservice.dto.req.GetDocumentReqDto;
import com.presbo.presboservice.dto.req.ProjectFileReqDto;
import com.presbo.presboservice.dto.res.GetDocumentResDto;
import com.presbo.presboservice.dto.res.ProjectFileResDto;
import com.presbo.presboservice.utility.MessagingUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetDocumentMsgSender {

    @Autowired
    MessagingUtility messagingUtility;

    public GetDocumentResDto getDocumentMessage(String exchange, String routingKey, GetDocumentReqDto data) throws Exception {


        GetDocumentResDto result = (GetDocumentResDto) messagingUtility.convertSendAndReceiveAsTypeAndRemoveTypeIdHeader(exchange, routingKey, data, GetDocumentResDto.class);

        return result;

    }

}
