package terminals.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import terminals.models.Terminal;

@Repository
public interface TerminalRepository extends CrudRepository<Terminal, Integer> {

    Terminal findFirstByDepartmentId(int departmentId);

    Terminal findByRegId(String regId);

    @Query("SELECT t FROM Terminal t WHERE t.id <> :id AND regId = :regId")
    Terminal findByRegIdForUpdate(@Param("regId") String regId, @Param("id") int id);

    Terminal findByInventoryId(String inventoryId);

    @Query("SELECT t FROM Terminal t WHERE t.id <> :id AND inventoryId = :inventoryId")
    Terminal findByInventoryIdForUpdate(@Param("inventoryId") String regId, @Param("id") int id);

    Terminal findBySerialId(String serialId);

    @Query("SELECT t FROM Terminal t WHERE t.id <> :id AND serialId = :serialId")
    Terminal findBySerialIdForUpdate(@Param("serialId") String regId, @Param("id") int id);

    int countAllByTerminalIsActive(boolean isActive);

    int countAllByUserIsNotNull();

}
