package terminals.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import terminals.models.Department;
import terminals.models.Terminal;
import terminals.models.User;
import terminals.repository.DepartmentRepository;
import terminals.repository.TerminalRepository;
import terminals.repository.UserRepository;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DepartmentsServiceImpl implements DepartmentsService {
    private final static String OK = "OK";
    private final static String DEPARTMENT_EXISTS = "Такой департамент уже существует";
    private final static String DEPARTMENT_NOT_EXISTS = "Такого департамента не существует";
    private final static String UNABLE_DELETE = "Невозможно удалить департамент, если существуют зависимые записи";

    private DepartmentRepository departmentRepository;
    private UserRepository userRepository;
    private TerminalRepository terminalRepository;

    @Override
    public List<Department> findAllDepartments() {
        List<Department> listOfDepartments = (List<Department>) departmentRepository.findAll();
        listOfDepartments.sort(Comparator.comparing(Department::getDepartment));
        return listOfDepartments;
    }

    @Transactional
    @Override
    public Map<String, String> addDepartment(String departmentName) {
        Department existingDepartment = departmentRepository.findByDepartment(departmentName);
        String resultMessage = DEPARTMENT_EXISTS;
        if (existingDepartment == null) {
            Department department = new Department(departmentName);
            departmentRepository.save(department);
            resultMessage = OK;
        }
        Map<String, String> result = new HashMap<>();
        result.put("departmentAddResult", resultMessage);
        return result;
    }

    @Transactional
    @Override
    public Map<String, String> renameDepartment(String oldDepName, String newDepName) {
        String resultMessage = OK;
        Department existingDepartment = departmentRepository.findByDepartment(newDepName);
        if (existingDepartment != null) {
            resultMessage = DEPARTMENT_EXISTS;
        } else {
            Department oldDepartment = departmentRepository.findByDepartment(oldDepName);
            oldDepartment.setDepartment(newDepName);
            departmentRepository.save(oldDepartment);
        }
        Map<String, String> result = new HashMap<>();
        result.put("departmentRenameResult", resultMessage);
        return result;
    }


    @Transactional
    @Override
    public Map<String, String> deleteDepartment(String departmentToDelete) {
        String resultMessage = OK;
        Department existingDepartment = departmentRepository.findByDepartment(departmentToDelete);
        if (existingDepartment == null) {
            resultMessage = DEPARTMENT_NOT_EXISTS;
        } else {
            int departmentId = existingDepartment.getId();
            if (checkDependency(departmentId)) {
                resultMessage = UNABLE_DELETE;
            } else {
                departmentRepository.delete(existingDepartment);
            }
        }
        Map<String, String> result = new HashMap<>();
        result.put("departmentDeleteResult", resultMessage);
        return result;
    }


    private boolean checkDependency(int departmentId) {
        return checkUsersDependency(departmentId) || checkTerminalsDependency(departmentId);
    }

    private boolean checkUsersDependency(int departmentId) {
        User user = userRepository.findFirstByDepartmentId(departmentId);
        return user != null;
    }

    private boolean checkTerminalsDependency(int departmentId) {
        Terminal terminal = terminalRepository.findFirstByDepartmentId(departmentId);
        return terminal != null;
    }

    @Autowired
    public void setDepartmentRepository(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setTerminalRepository(TerminalRepository terminalRepository) {
        this.terminalRepository = terminalRepository;
    }

}
