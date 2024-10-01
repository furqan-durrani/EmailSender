package Email.EmailService.Service;

import Email.EmailService.Model.EmailRequest;
import Email.EmailService.repository.EmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
@Service


public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private EmailRepository emailRepository;
    public void SendEmail(String fromAddress, String [] to,
                          String subject, String text, String []cc, String []bcc, String flag) {
        if (to == null || subject == null || text == null) {
            throw new IllegalArgumentException("To, Subject, and Text fields must not be null.");
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom(fromAddress);
            message.setSubject(subject);
            message.setText(text);
            if (cc != null && cc.length > 0) {
                message.setCc(cc);
            }
            if (bcc != null && bcc.length > 0) {
                message.setBcc(bcc);
            }
            javaMailSender.send(message);

            EmailRequest emailRequest = new EmailRequest();
            emailRequest.setFromAddress(fromAddress);
            emailRequest.setto(Arrays.asList(to));
            emailRequest.setSubject(subject);
            emailRequest.setText(text);
            emailRequest.setFlag(flag);
            emailRequest.setCc(Arrays.asList(cc));
            emailRequest.setBcc(Arrays.asList(bcc));
            emailRequest.setDateTime(LocalDateTime.now());
            emailRepository.save(emailRequest);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}


