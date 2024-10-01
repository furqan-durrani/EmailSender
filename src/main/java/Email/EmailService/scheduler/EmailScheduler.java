package Email.EmailService.scheduler;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import org.apache.log4j.LogManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.apache.log4j.Logger;


@Component
public class EmailScheduler {


    private final Logger logger = LogManager.getLogger(EmailScheduler.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    //private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    String emailId = "no-reply@ablfunds.com";




    @Scheduled(fixedRate = 3000)
    public void SendTransEmailNotifycation() {
        if (flag_email_alert != null && flag_email_alert.equals("true")) {
            System.out.println("Email service for Online payment running........");
            //logger.info("flag_email_alert is true, Email service for Online payment running........");

            List<Map<String, Object>> data = null;
            try {
                data = jdbcTemplate.queryForList("select id TRANS_ID,\n"
                        + "       notification_record_date,\n"
                        + "       notification_send_time,\n"
                        + "       notification_send_to E_MAIL,\n"
                        + "       notification_text,\n"
                        + "       notification_trans_type,\n"
                        + "       notification_trans_id,\n"
                        + "       notification_subject,\n"
                        + "       log_id,\n"
                        + "       sending_status,\n"
                        + "       nvl(attempts, 0) attempts ,\n"
                        + "       reason,\n"
                        + "       notification_type,\n"
                        + "       notification_folio_number\n"
                        + "  from UNIT_TRANS_NOTIFICATION_QUEUE\n"
                        + " where notification_trans_type = 'SALE'\n"
                        + "   and notification_subject = 'ABL - Online Payment' and sending_status='P' ");
                //logger.info("Data query executed successfully");

                for (Map<String, Object> dt : data) {
                    logger.info("Processing data for TRANS_ID: " + dt.get("TRANS_ID"));

                    if (isValidString(dt.get("TRANS_ID")) && isValidString(dt.get("E_MAIL")) && isValidString(dt.get("NOTIFICATION_TEXT")) &&
                            isValidString(dt.get("NOTIFICATION_SUBJECT")) && isValidString(dt.get("ATTEMPTS"))) {
                        sendSimpleMessage(dt.get("TRANS_ID").toString(), dt.get("E_MAIL").toString(), dt.get("notification_text").toString(), dt.get("notification_subject").toString(), dt.get("attempts").toString());
                        logger.info("Email sent for TRANS_ID: " + dt.get("TRANS_ID"));
                    } else {
                        jdbcTemplate.update("update UNIT_TRANS_NOTIFICATION_QUEUE set attempts='" + (Integer.parseInt(dt.get("attempts").toString()) + 1) + "' where id = '" + dt.get("TRANS_ID").toString() + "'");
                        logger.info("Updated attempts for TRANS_ID: " + dt.get("TRANS_ID"));
                    }
                }
            } catch (Exception e) {
                generateErrorLog(e.toString());
                logger.error("Error occurred: " + e.toString());
            }
        } else {
            System.out.println("Email service for Online payment Stopped........");
            logger.info("flag_email_alert is not true, Email service for Online payment Stopped.");
        }
    }


    private boolean isValidString(Object str) {
        return str != null && !str.toString().equals("") ? true : false;
    }

    public void SendMobileAlerts() {

        System.out.println("SendMobileAlerts running........");
        //logger.info("SendMobileAlerts running........");
        List<Map<String, Object>> data = null;
        try {
        } catch (Exception e) {
           // log.info(e.toString());
            generateErrorLog(e.toString());
        }
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    public JavaMailSender emailSender;

    public void sendSimpleMessage(String trans_id, String to, String notification_text, String subject, String attempts) {
        int attempt = Integer.parseInt(attempts) + 1;
        String[] bcc = new String[]{"Operation@ablfunds.com","furqan.durrani@ablfunds.com"};
        logger.info("Preparing to send email for TRANS_ID: " + trans_id);

        try {
            emailSender.send(new MimeMessagePreparator() {
                public void prepare(MimeMessage mimeMessage) throws MessagingException {
                    MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                    message.setFrom("ABL FUNDS <no-reply@ablfunds.com>");
                    message.setTo(to);
                    message.setCc(new InternetAddress("ISD-Contactus@ablfunds.com"));
                    message.setBcc(bcc);
                    message.setSubject(subject);
                    message.setText(notification_text, true);
                    logger.info("Email prepared for TRANS_ID: " + trans_id);
                    // Log email details
                    logger.info("Subject: " + subject);
                    logger.info("To: " + to);
                    logger.info("CC: ISD-Contactus@ablfunds.com");
                    logger.info("BCC: " + String.join(", ", bcc));
                    logger.info("Body: " + notification_text);
                }
            });
            jdbcTemplate.update("update UNIT_TRANS_NOTIFICATION_QUEUE set sending_status = 'S', attempts='" + attempt + "' where id = '" + trans_id + "'");
            logger.info("Email sent and database updated for TRANS_ID: " + trans_id);
        } catch (Exception e) {
            generateErrorLog(e.toString());
            logger.error("Error occurred while sending email for TRANS_ID: " + trans_id + " Error: " + e.toString());
            jdbcTemplate.update("update UNIT_TRANS_NOTIFICATION_QUEUE set attempts='" + attempt + "' where id = '" + trans_id + "'");
            logger.info("Database updated with failed attempt for TRANS_ID: " + trans_id);
        }
    }

    private void generateErrorLog(String err) {
        logger.info("Generating error log email.");
        try {
            emailSender.send(new MimeMessagePreparator() {
                public void prepare(MimeMessage mimeMessage) throws MessagingException {
                    MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                    message.setFrom("no-reply@ablfunds.com");
                    message.setTo("Hod.it@ablfunds.com");
                    message.setSubject("1LINK alert service error");
                    message.setText(err, true);
                    logger.info("Error log email prepared.");
                }
            });
        } catch (Exception e) {
            logger.error("Error occurred while sending error log email. Error: " + e.toString());
        }
    }

    private String getInvestmentTemplate() {
        logger.info("Generating investment template.");
        String tmpl = "<html>\n"
                + "\n"
                + "<body>\n"
                + "\n"
                + "The following online request has been sent to ABL Asset Management at 12/04/2019 01:04 AM<div style=\" width:700px;border: 1px solid;text-align:center; background-color:#CCCCCC\">\n"
                + "Online Request Detail\n"
                + "</div>\n"
                + "<div style=\"width:700px; border-left:1px solid; border-right:1px solid;border-bottom:1px solid;\">\n"
                + "  <table>\n"
                + "        <tr><td>Request Type:</td><td>Online Purchase of Unit</td></tr>\n"
                + "        <tr><td>Date:</td><td>12/04/2019</td></tr>\n"
                + "        <tr><td>Account Title:</td><td>Mr ADEEL ATHER</td></tr>\n"
                + "        <tr><td>Folio Number:</td><td>13114</td></tr>\n"
                + "        <tr><td>Transaction ID:</td><td>94181</td></tr>\n"
                + "        <tr><td>Contact No:</td><td>03008660868</td></tr>\n"
                + "        <tr><td>Fund:</td><td>ABL CASH FUND</td></tr>\n"
                + "        <tr><td>Consumer Number:</td><td>18204089441702868205</td></tr>\n"
                + "        <tr><td>Amount:</td><td>250000/-</td></tr>\n"
                + "\n"
                + "  </table>\n"
                + "</div><br/><br/><div style=\"font-size:12px\">Disclaimer:<br/><br/>\n"
                + "The information transmitted is intended only for the person or entity to which it is addressed and may contain confidential and/or privileged material. Any review, retransmission, dissemination or other use of, or taking of any action in reliance upon, this information by persons or entities other than the intended recipient is prohibited. If you received this in error, please contact the sender and delete the material from any computer. Please note that any views or opinions presented in this email are solely those of the author and do not necessarily represent those of the company. Finally, the recipient shall check this email and any attachments for the presence of viruses. The company accepts no liability for any damage caused by any virus transmitted by this email.</div>\n"
                + "\n"
                + "</body>\n"
                + "\n"
                + "</html>";
        logger.info("Investment template generated." + tmpl);
        return tmpl;
    }


    @Value("${service.email.alert.online}")
    private String flag_email_alert;
}
