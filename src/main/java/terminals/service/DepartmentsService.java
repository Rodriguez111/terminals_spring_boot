package terminals.service;

import org.json.JSONObject;

public interface DepartmentsService {

    JSONObject findAllDepartments();

    JSONObject addDepartment(JSONObject request);

    JSONObject renameDepartment(JSONObject request);

    JSONObject deleteDepartment(JSONObject request);
}
