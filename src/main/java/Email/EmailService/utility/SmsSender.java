package Email.EmailService.utility;

import Email.EmailService.scheduler.EmailScheduledTasks;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import client.sender;

@Component
public class SmsSender {


    @Async
    public String ConversionSms(String cName, String cellno, String id, String fname, String fcat, String tdate) {
        String cntct = "", body="";
        if (cellno != null) {
            cntct = cellno;
        }
        try {
            body = String.format(getConversionTemplate(), fname, fcat, tdate);
            cntct = cntct.charAt(0) == '0' ? "92" + cntct.substring(1, cntct.length()) : cntct;
            sender.send(cntct,body , id, proxyAddress == null || proxyAddress.isEmpty() ? "" : proxyAddress,
                    proxyPort == null || proxyPort.isEmpty() ? "" : proxyPort);
        } catch (Exception e) {
            logger.error("Error orrucred in sms sending. " + e.toString());
        }
        return body;
    }

    public String Sms(String Sms, String cellno, String id, String fname, String fcat, String tdate) {
        System.out.println("inside sms method message");
        String response = "";
        String cntct = "", body="";
        if (cellno != null) {
            cntct = cellno;
        }
        try {
            body = Sms;
            cntct = cntct.charAt(0) == '0' ? "92" + cntct.substring(1, cntct.length()) : cntct;
            System.out.println("Sending message 1");
            response = sender.send(cntct,body , id, proxyAddress == null || proxyAddress.isEmpty() ? "" : proxyAddress,
                    proxyPort == null || proxyPort.isEmpty() ? "" : proxyPort);

            System.out.println("response: " + response);
            return response;
        } catch (Exception e) {
            logger.error("Error orrucred in sms sending. " + e.toString());
        }
        return body;
    }

    private String getConversionTemplate() {
        String tmpl = "Your conversion request to %s marked as “%s” received on %s is in process. "
                + "For details, please call within 24 hours on 042-111225262.";
        return tmpl;
    }

    @Value("${service.sms.proxy}")
    private String proxyAddress;
    @Value("${service.sms.proxy.port}")
    private String proxyPort;
    private final Logger logger = LogManager.getLogger(EmailScheduledTasks.class);
}
