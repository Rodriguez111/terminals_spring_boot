package terminals.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import terminals.models.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    User findFirstByDepartmentId(int departmentId);

    User findByUserLogin(String login);

    @Query("SELECT u FROM User u WHERE u.id <> :id AND userLogin = :login")
    User findByByUserLoginForUpdate(@Param("login") String regId, @Param("id") int id);

    int countAllByIsActive(boolean isActive);

    User findFirstByTerminalId(int terminalId);
}
