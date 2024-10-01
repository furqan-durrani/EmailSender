package Email.EmailService.Model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "EmailRequest")
public class EmailRequest {

    @Id

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_request_seq_gen")
    @SequenceGenerator(name = "email_request_seq_gen", sequenceName = "email_request_seq", allocationSize = 1)

    private int id;
    private LocalDateTime dateTime;
    @Column(name = "from_address")
    private String fromAddress;

    @Column(name = "to_address")
    private String to;

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Column(name = "subject")
    private String subject;

    @Column(name = "text")
    private String text;

    @Column(name = "cc")
    private String cc;  // Comma-separated string

    @Column(name = "bcc")
    private String bcc;  // Comma-separated string
    private String flag;

    // Getters and Setters
    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

//    public String getTo() {
//        return to;
//    }
//
//    public void setTo(String to) {
//        this.to = to;
//    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    public List<String> getto() {
        return to !=null ?Arrays.asList(to.split(",")):Collections.emptyList();
    }
    public void setto(List<String>fromAddress) {
        this.to=String.join(",",fromAddress);
    }
    public List<String> getCc() {
        return cc != null ? Arrays.asList(cc.split(",")) : Collections.emptyList();
    }

    public void setCc(List<String> cc) {
        this.cc = String.join(",", cc);
    }

    public List<String> getBcc() {
        return bcc != null ? Arrays.asList(bcc.split(",")) : Collections.emptyList();
    }

    public void setBcc(List<String> bcc) {
        this.bcc = String.join(",", bcc);
    }
}
