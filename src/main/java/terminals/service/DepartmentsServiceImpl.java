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
import java.util.List;

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
    public JSONObject findAllDepartments() {
        List<Department> listOfDepartments = (List<Department>) departmentRepository.findAll();
        listOfDepartments.sort(Comparator.comparing(Department::getDepartment));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("listOfDeparts", listOfDepartments);
        return jsonObject;
    }


    @Transactional
    @Override
    public JSONObject addDepartment(JSONObject request) {
        String departmentName = request.getString("addDepartment");
        Department existingDepartment = departmentRepository.findByDepartment(departmentName);
        String resultMessage = DEPARTMENT_EXISTS;
        if (existingDepartment == null) {
            Department department = new Department(departmentName);
            departmentRepository.save(department);
            resultMessage = OK;
        }
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("departmentAddResult", resultMessage);
        return jsonResult;
    }

    @Transactional
    @Override
    public JSONObject renameDepartment(JSONObject request) {
        String oldDepName = request.getString("updateDepartment");
        String newDepName = request.getString("newDepartmentName");

        String resultMessage = OK;
        Department existingDepartment = departmentRepository.findByDepartment(newDepName);
        if (existingDepartment != null) {
            resultMessage = DEPARTMENT_EXISTS;
        } else {
            Department oldDepartment = departmentRepository.findByDepartment(oldDepName);
            oldDepartment.setDepartment(newDepName);
            departmentRepository.save(oldDepartment);
        }
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("departmentRenameResult", resultMessage);
        return jsonResult;
    }

    @Transactional
    @Override
    public JSONObject deleteDepartment(JSONObject request) {
        request = request.getJSONObject("deleteDepartment");
        String resultMessage = OK;
        String departmentName = request.getString("department");
        Department existingDepartment = departmentRepository.findByDepartment(departmentName);

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

        JSONObject jsonResult = new JSONObject();
        jsonResult.put("departmentDeleteResult", resultMessage);
        return jsonResult;
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
