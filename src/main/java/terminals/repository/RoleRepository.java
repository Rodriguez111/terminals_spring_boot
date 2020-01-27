package terminals.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import terminals.models.Role;

import java.util.List;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {

    Role findByRole(String role);

    List<Role> findByRoleNot(String role);

}
