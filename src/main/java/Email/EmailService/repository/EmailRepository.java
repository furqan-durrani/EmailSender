package Email.EmailService.repository;

import Email.EmailService.Model.EmailRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends JpaRepository<EmailRequest, Long> {

}
