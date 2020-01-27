package terminals.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import terminals.models.Registration;

import java.util.List;

@Repository
public interface RegistrationRepository extends CrudRepository<Registration, Integer>, JpaSpecificationExecutor<Registration> {

    Registration findFirstByUserId(int id);

    Registration findFirstByTerminalId(int id);

    Registration findFirstByAdminGaveOutId(int id);

    Registration findFirstByAdminGotId(int id);

    List<Registration> findAllByStartDateAfter(String dateAfter);

    Registration findByUserIdAndTerminalIdAndEndDateIsNull(int userId, int terminalId);

}
