package terminals.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import terminals.service.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class JsonController {
    private UsersService usersService;
    private RolesService rolesService;
    private DepartmentsService departmentsService;
    private TerminalsService terminalsService;

    @RequestMapping(value = "/json", method = RequestMethod.POST)
    protected void getJsonData(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader br = req.getReader();
        StringBuilder sb = new StringBuilder();
        String read = "";
        while ((read = br.readLine()) != null) {
            sb.append(read);
        }
        String requestFromClient = sb.toString();
        JSONObject result = new JSONObject();
        if (requestFromClient.equals("getAllDepartments")) {
            result = departmentsService.findAllDepartments();
            int i = 0;
        } else if (requestFromClient.contains("addDepartment")) {
            JSONObject jsonFromClient = new JSONObject(requestFromClient);
            result = departmentsService.addDepartment(jsonFromClient);
        } else if (requestFromClient.contains("updateDepartment")) {
            JSONObject jsonFromClient = new JSONObject(requestFromClient);
            result = departmentsService.renameDepartment(jsonFromClient);
        } else if (requestFromClient.contains("deleteDepartment")) {
            JSONObject jsonFromClient = new JSONObject(requestFromClient);
            result = departmentsService.deleteDepartment(jsonFromClient);
        } else if (requestFromClient.contains("getTerminalInfo")) {
            JSONObject jsonFromClient = new JSONObject(requestFromClient);
            int id = jsonFromClient.getInt("getTerminalInfo");
            result = terminalsService.findTerminalById(id);
        } else if (requestFromClient.equals("getListOfRoles")) {
            result = rolesService.findAllRoles();
        } else if (requestFromClient.contains("getUserInfo")) {
            JSONObject jsonFromClient = new JSONObject(requestFromClient);
            int id = jsonFromClient.getInt("getUserInfo");
            result = usersService.findUserById(id);
        } else if (requestFromClient.equals("getAllTerminals")) {
            result = terminalsService.findAllTerminals();
        } else if (requestFromClient.equals("getActiveTerminals")) {
            result = terminalsService.findActiveTerminals();
        } else if (requestFromClient.equals("getCountOfAllTerminals")) {
            result = terminalsService.getCountOfAllTerminals();
        } else if (requestFromClient.equals("getCountOfActiveTerminals")) {
            result = terminalsService.getCountOfActiveTerminals();
        } else if (requestFromClient.contains("deleteTerminal")) {
            JSONObject jsonFromClient = new JSONObject(requestFromClient);
            int id = jsonFromClient.getInt("deleteTerminal");
            result = terminalsService.deleteTerminal(id);
        } else if (requestFromClient.equals("getAllUsers")) {
            result = usersService.findAllUsers();
        } else if (requestFromClient.equals("getActiveUsers")) {
            result = usersService.findActiveUsers();
        } else if (requestFromClient.equals("getCountOfAllUsers")) {
            result = usersService.getCountOfAllUsers();
        } else if (requestFromClient.equals("getCountOfActiveUsers")) {
            result = usersService.getCountOfActiveUsers();
        } else if (requestFromClient.contains("deleteUser")) {
            JSONObject jsonFromClient = new JSONObject(requestFromClient);
            int id = jsonFromClient.getInt("deleteUser");
            result = usersService.deleteUser(id);
        } else if (requestFromClient.contains("addUser")) {
            JSONObject jsonFromClient = new JSONObject(requestFromClient);
            result = usersService.addUser(jsonFromClient);
        } else if (requestFromClient.contains("updateUser")) {
            JSONObject jsonFromClient = new JSONObject(requestFromClient);
            result = usersService.updateUser(jsonFromClient);
        } else if (requestFromClient.contains("addTerminal")) {
            JSONObject jsonFromClient = new JSONObject(requestFromClient);
            result = terminalsService.addTerminal(jsonFromClient);
        } else if (requestFromClient.contains("updateTerminal")) {
            JSONObject jsonFromClient = new JSONObject(requestFromClient);
            result = terminalsService.updateTerminal(jsonFromClient);
        }
        resp.setContentType("text/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter printWriter = resp.getWriter();
        printWriter.print(result);
        printWriter.flush();
    }

    @Autowired
    public void setUsersService(UsersService usersService) {
        this.usersService = usersService;
    }

    @Autowired
    public void setRolesService(RolesService rolesService) {
        this.rolesService = rolesService;
    }

    @Autowired
    public void setDepartmentsService(DepartmentsService departmentsService) {
        this.departmentsService = departmentsService;
    }

    @Autowired
    public void setTerminalsService(TerminalsService terminalsService) {
        this.terminalsService = terminalsService;
    }
}