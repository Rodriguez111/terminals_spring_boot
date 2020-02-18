package terminals.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import terminals.models.Department;
import terminals.models.User;
import terminals.service.UsersService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users_controller")
public class UsersController {

    private UsersService usersService;

    @GetMapping
    @RequestMapping("/all")
    public List<User> listOfAllUsers() {
        return usersService.findAllUsers();
    }

    @GetMapping("/userinfo/{id}")
    public User getUser(@PathVariable String id) {
        return usersService.findUserById(id);
    }

    @GetMapping
    @RequestMapping("/active")
    public List<User> listOfActiveUsers() {
        return usersService.findActiveUsers();
    }

    @GetMapping
    @RequestMapping("/countall")
    public long getCountOfAllUsers() {
        return usersService.getCountOfAllUsers();
    }

    @GetMapping
    @RequestMapping("/countactive")
    public int getCountOfActiveUsers() {
        return usersService.getCountOfActiveUsers();
    }

    @PostMapping
    public Map<String, String> create(@RequestBody Map<String, String> newUser){
        return usersService.addUser(newUser);
    }

    @PutMapping("{id}")//departToRename передается в заголовке запроса
    public Map<String, String> update(@PathVariable String id, @RequestBody Map<String, String> userToUpdate){
        return usersService.updateUser(id, userToUpdate);
    }

    @DeleteMapping("{userId}") //id передается в заголовке запроса
    public Map<String, String>  delete(@PathVariable String userId) {
        return usersService.deleteUser(userId);
    }

    @Autowired
    public void setUsersService(UsersService usersService) {
        this.usersService = usersService;
    }
}
