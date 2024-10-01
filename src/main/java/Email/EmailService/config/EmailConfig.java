package Email.EmailService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.Session;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import java.util.Properties;

//@Configuration
public class EmailConfig {

//    @Bean
//    public JavaMailSender javaMailSender() {
//        Properties props = new Properties();
//        props.put("mail.smtp.host", "192.168.18.2");  // Replace with your SMTP server
//        props.put("mail.smtp.port", "25");             // Replace with your SMTP port
//        props.put("mail.username","ABL Asset Management <no-reply@ablfunds.com>");
//        props.put("mail.transport.smtp.ssl.protocols", "TLSv1.2");
//        props.put("mail.smtp.auth", "false");
//        props.put("mail.smtp.starttls.enable", "false"); // Enable STARTTLS if required
//        props.put("mail.debug", "true");               // Enable debug output
//        props.put("mail.smtp.ssl.enable","false");
//        props.put("mail.transport.protocol", "smtp");
//       // props.put("mail.smtp.auth.ntlm.domain","ABLAMC");
////        spring.mail.properties.mail.smtp.starttls.enable=false
////        spring.mail.properties.mail.smtp.ssl.enable=false
//       // spring.mail.properties.mail.transport.protocol=smtp
//
//
//        // Create a Session with authentication
//        Session session = Session.getDefaultInstance(props, new Authenticator() {
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication("muhammad.affan", "032178774711@aN");
//            }
//        });
//
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        mailSender.setSession(session);  // Set the session with authentication
//
//
//
//        // Set additional properties if needed
////        Properties mailProps = mailSender.getJavaMailProperties();
////        mailProps.put("mail.transport.protocol", "smtp");
////        mailProps.put("mail.smtp.auth", "true");
////        mailProps.put("mail.smtp.starttls.enable", "true");
////        mailProps.put("mail.debug", "true");
//
//        return mailSender;
//    }
}
