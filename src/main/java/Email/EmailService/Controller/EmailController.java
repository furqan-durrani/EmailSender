package Email.EmailService.Controller;

import Email.EmailService.Model.EmailRequest;
import Email.EmailService.Model.SmsModel;
import Email.EmailService.Service.EmailService;
import Email.EmailService.Service.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private SMSService smsService;

    @PostMapping("/Message")
    public String send (@RequestBody SmsModel smsModel){
        System.out.println("in sms, request received");
        String response =smsService.sendSMS(smsModel);
        if (response.equalsIgnoreCase("0")) {
            return "SMS sent successfully";
        } else {
            return "SMS send failed";
        }
    }

    @PostMapping("/send")
    public String sendEmail(@RequestBody EmailRequest emailRequest) {
        try {
            if (emailRequest == null) {
                return "Invalid request: emailRequest cannot be null.";
            }
        if (emailRequest.getFromAddress()==null){
                 return "Invalid request: fromAddress cannot be null.";
                }
            if ( emailRequest.getSubject() == null || emailRequest.getText() == null) {
                return "Invalid request:  'subject', and 'text' fields must not be null.";
            }
            if (emailRequest.getto()==null ||emailRequest.getto().isEmpty()){
                return "Mail to cannot be null or empty.";
            }
            String[] to=emailRequest.getto().toArray(new String[0]);

            String [] cc=new String[0];
            try {
                 cc = emailRequest.getCc() != null ? emailRequest.getCc().toArray(new String[0]) : new String[0];
            }
            catch (Exception e){
                System.out.println("Invalid CC address, proceeding without CC:"+e.getMessage());
            }
            String[] bcc = emailRequest.getBcc() != null ? emailRequest.getBcc().toArray(new String[0]) : new String[0];

            emailService.SendEmail(
                    emailRequest.getFromAddress(),
                    to,
                    emailRequest.getSubject(),
                    emailRequest.getText(),
                    cc,
                    bcc,
                    emailRequest.getFlag()

            );

            return "Email sent successfully";
        }
        catch (Exception e) {
            return "An error occurred while sending the email: " + e.getMessage();
        }
    }
}
