package terminals.service;

import org.json.JSONObject;
import terminals.models.Department;

import java.util.List;
import java.util.Map;

public interface DepartmentsService {

    List<Department> findAllDepartments();

    Map<String, String> addDepartment(String departmentName);

    Map<String, String> renameDepartment(String departmentToRename, String departmentNewName);

    Map<String, String> deleteDepartment(String departmentToDelete);

}
