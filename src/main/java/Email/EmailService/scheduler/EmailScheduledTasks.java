package Email.EmailService.scheduler;




//import com.ams.utility.SmsSender;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Email.EmailService.utility.SmsSender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EmailScheduledTasks {
    private final Logger log = LogManager.getLogger(EmailScheduledTasks.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    @Value("${service.sms.alert.ums}")
    private String flag_sms_alert;
    @Value("${service.email.alert.ums}")
    private String flag_email_alert;
    @Autowired
    SmsSender sms;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    public JavaMailSender emailSender;

    public EmailScheduledTasks() {
    }

    @Scheduled(fixedRate = 600000L)
    public void SendTransEmailNotifycation() {
        if (this.flag_email_alert != null && this.flag_email_alert.equals("true")) {
            System.out.println("Email service for UMS transactions running........");
            //log.info("Email service for UMS transactions running........");
            List<Map<String, Object>> data = null;

            try {
                data = this.jdbcTemplate.queryForList("select TRANS_ID,\n       c.e_mail,\n       c.client_name,\n       trans_type,\n       c.phone_mobile,\n       f.fund_name FNAME,\n       f.riskpriority FCAT,\n       to_char(sysdate, 'dd Mon YYYY') TDATE , ua.folio_number,\nnvl((select email_risk_flag from trans_notify_risk_config r where r.folio_number = ua.folio_number), 0) email_risk_flag \n  from trans_notify t, unit_sale us, unit_account ua, client c, fund f, payment_detail pd\nwhere t.trans_id = us.sale_id\n   and pd.payment_id = us.payment_id\n   and upper(pd.client_bank_acc) not like '%CGT%'\n   and us.fund_code = f.fund_code\n   and t.trans_type = 'SALE'\n   and us.folio_number = ua.folio_number\n   and ua.primary_client = c.client_code\n   and sale_date >= to_char(sysdate, 'dd-MON-YYYY') \n   and c.e_mail is not null and us.post = '0' \n   and t.sent in (0)union\nselect TRANS_ID,\n       c.e_mail,\n       c.client_name,\n       trans_type,\n       c.phone_mobile,\n       f.fund_name FNAME,\n       f.riskpriority FCAT,\n       to_char(sysdate, 'dd Mon YYYY') TDATE, ua.folio_number,  nvl((select email_risk_flag from trans_notify_risk_config r where r.folio_number = ua.folio_number), 0) email_risk_flag \n  from trans_notify    t,\n       unit_redemption ur,\n       unit_account    ua,\n       client          c,\n       fund            f\n where t.trans_id = ur.redemption_id\n   and ur.fund_code = f.fund_code\n   and t.trans_type = 'REDEMPTION'\n   and ur.folio_number = ua.folio_number\n   and ua.primary_client = c.client_code\n   and ur.redemption_date >= to_char(sysdate, 'dd-MON-YYYY') \n   and c.e_mail is not null and ur.post = '1' \n   and t.sent in (0)\nunion\nselect TRANS_ID,\n       c.e_mail,\n       c.client_name,\n       trans_type,\n       c.phone_mobile,\n       f.fund_name FNAME,\n       f.riskpriority FCAT,\n       to_char(sysdate, 'dd Mon YYYY') TDATE, ua.folio_number,  nvl((select email_risk_flag from trans_notify_risk_config r where r.folio_number = ua.folio_number), 0) email_risk_flag \n  from trans_notify t, unit_transfer us, unit_account ua, client c, fund f\n where t.trans_id = us.transfer_id\n   and us.to_fund_code = f.fund_code\n   and t.trans_type = 'CONVERSION'\n   and us.from_folio_no = ua.folio_number\n   and ua.primary_client = c.client_code\n   and transfer_date >= to_char(sysdate, 'dd-MON-YYYY') \n   and c.e_mail is not null and us.post = '1' \n   and t.sent in (0) \n");
                //log.info("Query executed to fetch data: " + data);
                System.out.println("Query executed to fetch data: " + data);
                Iterator var2 = data.iterator();

                while(true) {
                    while(var2.hasNext()) {
                        Map<String, Object> dt = (Map)var2.next();
                        log.info("Processing data: " + dt);

                        if (this.isValidString(dt.get("TRANS_ID")) && this.isValidString(dt.get("CLIENT_NAME")) && this.isValidString(dt.get("E_MAIL")) && this.isValidString(dt.get("TRANS_TYPE")) && this.isValidString(dt.get("FCAT"))) {
                            //this.processEmailMessage(dt.get("TRANS_ID").toString(), dt.get("CLIENT_NAME").toString(), dt.get("E_MAIL").toString(), dt.get("TRANS_TYPE").toString(), dt.get("FNAME").toString(), dt.get("FCAT") == null ? "Very Low" : dt.get("FCAT").toString(), dt.get("TDATE").toString(), dt.get("EMAIL_RISK_FLAG").toString(), dt.get("FOLIO_NUMBER").toString());
                            //log.info("Email message not processed successfully for TRANS_ID: " + dt.get("TRANS_ID").toString());
                        } else {
                            this.jdbcTemplate.update("update trans_notify set sent = 2 where trans_id = '" + dt.get("TRANS_ID").toString() + "'");
                            log.info("Invalid data detected, updated sent status to 2 for TRANS_ID: " + dt.get("TRANS_ID").toString());
                        }
                    }

                    return;
                }
            } catch (Exception var4) {
                var4.printStackTrace();
                this.generateErrorLog(var4.toString());
                log.error("Exception occurred: " + var4.toString());
            }
        } else {
            System.out.println("Email service for UMS transactions Stopped........");
            log.info("Email service for UMS transactions Stopped........");
        }

    }

    @Scheduled(
            fixedRate = 30000L
    )
    public void SendTransSmsNotifycation() {
        if (this.flag_sms_alert != null && this.flag_sms_alert.equals("true")) {
            System.out.println("Sms service for UMS transactions running........");
            //log.info("Sms service for UMS transactions running........");
            List<Map<String, Object>> data = null;

            try {
                data = this.jdbcTemplate.queryForList("select TRANS_ID,\n       c.e_mail,\n       c.client_name,\n       trans_type,\n       c.phone_mobile,\n       f.fund_name,\n       f.riskpriority,\n       to_char(sysdate, 'dd Mon YYYY') trans_date , ua.folio_number,  nvl((select sms_risk_flag from trans_notify_risk_config r where r.folio_number = ua.folio_number), 0) sms_risk_flag \n  from trans_notify t, unit_transfer us, unit_account ua, client c, fund f \n where t.trans_id = us.transfer_id\n   and t.trans_type = 'CONVERSION'\n   and us.from_folio_no = ua.folio_number\n   and ua.primary_client = c.client_code\n   and transfer_date >= to_char(sysdate, 'dd-MON-YYYY') \n   and c.phone_mobile is not null\n   and f.fund_code = us.to_fund_code\n   and nvl(t.sms_sent, 0) in (0) \n");
                //log.info("Query executed to fetch data for SMS: " + data);
                System.out.println("Query executed to fetch data for SMS: " + data);
                Iterator var2 = data.iterator();

                while(true) {
                    while(var2.hasNext()) {
                        Map<String, Object> dt = (Map)var2.next();
                        log.info("Processing SMS data: " + dt);

                        if (this.isValidString(dt.get("TRANS_ID")) && this.isValidString(dt.get("CLIENT_NAME")) && this.isValidString(dt.get("TRANS_TYPE")) && this.isValidString(dt.get("PHONE_MOBILE")) && this.isValidString(dt.get("RISKPRIORITY"))) {
                            this.processConvSms(dt.get("TRANS_ID").toString(), dt.get("CLIENT_NAME").toString(), "", dt.get("TRANS_TYPE").toString(), dt.get("PHONE_MOBILE").toString(), dt.get("FUND_NAME").toString(), dt.get("RISKPRIORITY").toString() + " Risk Profile", dt.get("TRANS_DATE").toString(), dt.get("SMS_RISK_FLAG").toString(), dt.get("FOLIO_NUMBER").toString());
                            log.info("SMS message processed successfully for TRANS_ID: " + dt.get("TRANS_ID").toString());
                        } else {
                            this.jdbcTemplate.update("update trans_notify set sms_sent = 2 where trans_id = '" + dt.get("TRANS_ID").toString() + "'");
                            log.info("Invalid SMS data detected, updated sms_sent status to 2 for TRANS_ID: " + dt.get("TRANS_ID").toString());
                        }
                    }

                    return;
                }
            } catch (Exception var4) {
                var4.printStackTrace();
                this.generateErrorLog(var4.toString());
                log.error("Exception occurred: " + var4.toString());
            }
        } else {
            System.out.println("SMS service for UMS transactions Stopped........");
            log.info("SMS service for UMS transactions Stopped........");
        }

    }

    public void processEmailMessage(String trans_id, String cName, String to, String transType, String fname, String fcat, String tdate, String flagRisk, String folioNumber) {
        String[] bcc = new String[]{"Operation@ablamc.com", "furqan.durrani@ablfunds.com"};
        //String[] bcc = new String[]{"Muhammad.musa@ablfunds.com"};
        log.info("Processing email message for TRANS_ID: " + trans_id);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            String body = "";
            String subject = "";
            if (transType.equals("SALE")) {
                subject = "Investment Alert";
                body = String.format(this.getInvestmentTemplate(flagRisk), fname, fcat, tdate, fcat, fcat);
                message.setBcc(bcc);
                log.info("Subject set to: " + subject);
                log.info("Body set to: " + body);
            } else if (transType.equals("REDEMPTION")) {
                subject = "Redemption Alert";
                body = String.format(this.getRedemptionTemplate(flagRisk), cName);
                log.info("Subject set to: " + subject);
                log.info("Body set to: " + body);
            } else if (transType.equals("CONVERSION")) {
                subject = "FoF / Conversion Alert";
                body = String.format(this.getConversionTemplate(flagRisk), fname, fcat, tdate, fcat, fcat);
                message.setBcc(bcc);
                log.info("Subject set to: " + subject);
                log.info("Body set to: " + body);
            }

            message.setFrom("contactus@ablamc.com");
            message.setTo(to.replace(" ", ""));
            message.setSubject(subject);
            message.setText(body);
            this.emailSender.send(message);
            log.info("Email sent to: " + to);
            this.jdbcTemplate.update("update trans_notify set sent = 1 where trans_id = '" + trans_id + "'");
            log.info("Updated sent status to 1 for TRANS_ID: " + trans_id);

            if (flagRisk != null && flagRisk.equals("0")) {
                this.jdbcTemplate.update("INSERT INTO trans_notify_risk_config SELECT '" + folioNumber + "', 0,0 FROM dual\nWHERE NOT EXISTS (SELECT NULL FROM trans_notify_risk_config WHERE folio_number = '" + folioNumber + "') ");
                this.jdbcTemplate.update("update trans_notify_risk_config set email_risk_flag = '1' WHERE folio_number = '" + folioNumber + "' ");
                log.info("Inserted/Updated risk configuration for FOLIO_NUMBER: " + folioNumber);
            }
        } catch (MailException var14) {
            var14.printStackTrace();
            this.generateErrorLog(var14.toString());
            log.error("MailException occurred: " + var14.toString());
            this.jdbcTemplate.update("update trans_notify set sent = 2 where trans_id = '" + trans_id + "'");
            log.info("Updated sent status to 2 for TRANS_ID: " + trans_id);
        }

    }

    public void processConvSms(String trans_id, String cName, String to, String transType, String mobile, String fname, String fcat, String tdate, String riskFlag, String folioNumber) {
        log.info("Processing SMS message for TRANS_ID: " + trans_id);
        try {
            String body = "";
            String subject = "";
            if (transType.equals("SALE")) {
                subject = "Investment Notification";
                body = String.format(this.getInvestmentTemplate(riskFlag), cName);
                log.info("Subject set to: " + subject);
                log.info("Body set to: " + body);
            } else if (transType.equals("REDEMPTION")) {
                subject = "Redemption Notification";
                body = String.format(this.getRedemptionTemplate(riskFlag), cName);
                log.info("Subject set to: " + subject);
                log.info("Body set to: " + body);
            } else if (transType.equals("CONVERSION")) {
                body = this.sms.ConversionSms(cName, mobile, "1", fname, fcat, tdate);
                this.jdbcTemplate.update("update trans_notify set sms_sent = 1, SMS_NOTIFICATION_TEXT = '" + body + "' where trans_id = '" + trans_id + "'");
                log.info("SMS sent: " + body);
                log.info("Updated sms_sent status to 1 for TRANS_ID: " + trans_id);
            }

            if (riskFlag != null && riskFlag.equals("0")) {
                this.jdbcTemplate.update("INSERT INTO trans_notify_risk_config SELECT '" + folioNumber + "', 0,0 FROM dual\nWHERE NOT EXISTS (SELECT NULL FROM trans_notify_risk_config WHERE folio_number = '" + folioNumber + "') ");
                this.jdbcTemplate.update("update trans_notify_risk_config set sms_risk_flag = '1' WHERE folio_number = '" + folioNumber + "' ");
                log.info("Inserted/Updated SMS risk configuration for FOLIO_NUMBER: " + folioNumber);
            }
        } catch (Exception var13) {
            log.info(var13.toString());
            this.generateErrorLog(var13.toString());
            log.error("Exception occurred: " + var13.toString());
            this.jdbcTemplate.update("update trans_notify set sms_sent = 2 where trans_id = '" + trans_id + "'");
            log.info("Updated sms_sent status to 2 for TRANS_ID: " + trans_id);
        }

    }

    private boolean isValidString(Object str) {
        boolean valid = str != null && !str.toString().equals("");
        log.info("isValidString check for: " + str + " - Result: " + valid);
        return valid;
    }

    private void generateErrorLog(String err) {
        try {
            this.emailSender.send((MimeMessagePreparator)(new Object()));
        } catch (Exception var3) {
            var3.printStackTrace();
            log.error("Exception occurred while generating error log: " + var3.toString());
        }

    }

    private String getInvestmentTemplate(String riskFlag) {
        String tmpl = "Thank you for investing in ABL Funds. Your investment request in %s marked as “%s Risk Profile” has been received on %s and is in process. \n\nThis email has been sent to confirm your intention to invest in “%s” risk profile fund managed by ABL Asset Management. If you have any objection and/or need further understanding related to investing in Funds with “%s” risk profile, please contact us within 24 hours through our sales/distributor representative or call us directly on 042-111225262.";
        if (riskFlag.equals("1")) {
            tmpl = "Thank you for investing in ABL Funds. Your investment request in %s marked as “%s Risk Profile” has been received on %s and is in process. \n\nThis email has been sent to confirm your intention to invest in “%s” risk profile fund managed by ABL Asset Management. If you have any objection and/or need further understanding related to investing in Funds with “%s” risk profile, please contact us within 24 hours through our sales/distributor representative or call us directly on 042-111225262.";
        }

        log.info("Investment template fetched with riskFlag: " + riskFlag);
        return tmpl;
    }

    private String getRedemptionTemplate(String riskFlag) {
        String tmpl = "Dear %s \nYour redemption has been processed today and your payment will be sent to you in next two working days.\nIf you have any queries, Please call us on 042-111-225-262";
        if (riskFlag.equals("1")) {
            tmpl = "Dear %s \n\nYour redemption has been processed today and your payment will be sent to you in next two working days.\nIf you have any queries, Please call us on 042-111-225-262";
        }

        log.info("Redemption template fetched with riskFlag: " + riskFlag);
        return tmpl;
    }

    private String getConversionTemplate(String riskFlag) {
        String tmpl = "Your conversion request to %s marked as “%s Risk Profile” has been received on %s and is in process. \n\nThis email has been sent to confirm your intention to convert your investment to “%s” risk profile fund managed by ABL Asset Management. If you have any objection and/or need further understanding related to conversion in Funds with “%s” risk profile, please contact us within 24 hours through our sales/distributor representative or call us directly on 042-111225262. ";
        if (riskFlag.equals("1")) {
            tmpl = "Your conversion request to %s marked as “%s Risk Profile” has been received on %s and is in process. \n\nThis email has been sent to confirm your intention to convert your investment to “%s” risk profile fund managed by ABL Asset Management. If you have any objection and/or need further understanding related to conversion in Funds with “%s” risk profile, please contact us within 24 hours through our sales/distributor representative or call us directly on 042-111225262. ";
        }

        log.info("Conversion template fetched with riskFlag: " + riskFlag);
        return tmpl;
    }

}
