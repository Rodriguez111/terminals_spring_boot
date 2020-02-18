package terminals.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import terminals.models.Department;
import terminals.service.DepartmentsService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/departments_controller")
public class DepartmentsController {

    private DepartmentsService departmentsService;

    @GetMapping
    public List<Department> listOfDepartments() {
        return departmentsService.findAllDepartments();
    }

    @PostMapping
    public Map<String, String> create(@RequestBody String department){
        return departmentsService.addDepartment(department);
    }

    @PutMapping("{departToRename}")//departToRename передается в заголовке запроса
    public Map<String, String> update(@PathVariable String departToRename, @RequestBody String departmentNewName){
        return departmentsService.renameDepartment(departToRename, departmentNewName);
    }

    @DeleteMapping("{departToDelete}") //id передается в заголовке запроса
    public Map<String, String>  delete(@PathVariable String departToDelete) {
        return departmentsService.deleteDepartment(departToDelete);
    }

    @Autowired
    public void setDepartmentsService(DepartmentsService departmentsService) {
        this.departmentsService = departmentsService;
    }
}
