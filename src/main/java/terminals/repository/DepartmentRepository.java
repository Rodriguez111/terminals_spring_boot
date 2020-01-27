package terminals.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import terminals.models.Department;

@Repository
public interface DepartmentRepository extends CrudRepository<Department, Integer> {

    Department findByDepartment(String departmentName);

}
