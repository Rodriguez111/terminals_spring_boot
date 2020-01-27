package terminals.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import terminals.models.Terminal;

@Repository
public interface TerminalRepository extends CrudRepository<Terminal, Integer> {

    Terminal findFirstByDepartmentId(int departmentId);

    Terminal findByRegId(String regId);

    Terminal findByInventoryId(String InventoryId);

    Terminal findBySerialId(String serialId);

    int countAllByTerminalIsActive(boolean isActive);

    int countAllByUserIsNotNull();

}
