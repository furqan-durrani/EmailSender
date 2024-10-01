package Email.EmailService.repository;


import Email.EmailService.Model.SmsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmsModelRepository extends JpaRepository<SmsModel,Long> {

}