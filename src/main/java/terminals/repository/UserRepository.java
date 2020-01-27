package terminals.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import terminals.models.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    User findFirstByDepartmentId(int departmentId);

    User findByUserLogin(String login);

    int countAllByIsActive(boolean isActive);

    User findFirstByTerminalId(int terminalId);
}
