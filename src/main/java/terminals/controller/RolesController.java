package terminals.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import terminals.models.Department;
import terminals.models.Role;
import terminals.service.DepartmentsService;
import terminals.service.RolesService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/roles_controller")
public class RolesController {

    private RolesService rolesService;

    @GetMapping
    public List<Role> listOfDepartments() {
        return rolesService.findAllRoles();
    }

    @Autowired
    public void setRolesService(RolesService rolesService) {
        this.rolesService = rolesService;
    }
}
