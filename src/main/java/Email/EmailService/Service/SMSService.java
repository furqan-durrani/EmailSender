package Email.EmailService.Service;


import Email.EmailService.Model.SmsModel;
import Email.EmailService.utility.SmsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import Email.EmailService.repository.SmsModelRepository;
//import client.sender;

import java.time.LocalDateTime;

@Service
public class SMSService {
    @Value("${userId}")
    String userId;
    @Value("${password}")
    String password;
//    @Autowired
//    private WebService40Soap webService40Soap;
    @Autowired
    private SmsModelRepository SmsModelRepository;

    @Autowired
    SmsSender smsSender;

    String response = "";
    public String sendSMS(SmsModel smsModel){
//        String userId = this.userId;
//        String password = this.password;
        String mobileNo = smsModel.getMobileNo();
        String msgId = smsModel.getMsgId();
        String sms = smsModel.getSms();
        String msgHeader = smsModel.getMsgHeader();
        String smsType = smsModel.getSmsType();
        String handsetPort = smsModel.getHandsetPort();
        String smsChannel = smsModel.getSmsChannel();
        String telco = smsModel.getTelco();

        System.out.println("Sending message");

        response = smsSender.Sms(sms, mobileNo, "1", "", "","");
        System.out.println("after Sending message");

        //String response = webService40Soap.sendSMS(userId, password, mobileNo, msgId, sms, msgHeader, smsType, handsetPort, smsChannel, telco);
        smsModel.setResponse(response);
        smsModel.setDateTime(LocalDateTime.now());
        //save to model
        SmsModelRepository.save(smsModel);
        return response;
    }
}