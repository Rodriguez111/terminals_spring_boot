package terminals.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ViewController {
    @RequestMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @RequestMapping("/")
    public String showMainPageIfAuthorized() {
        return "login";
    }

    @RequestMapping("/main")
    protected String generateMainPage() {
        return "main";
    }

    @RequestMapping("/users")
    protected String generateUsersMainPage() {
        return "/usersview/main_users";
    }

    @RequestMapping("/update_user")
    protected String generateUserUpdatePage(@RequestParam(name = "id") String id) {
        return "/usersview/update_user";
    }

    @RequestMapping("/add_user")
    protected String generateUserCreatePage() {
        return "/usersview/add_user";
    }

    @RequestMapping("/terminals")
    protected String generateTerminalsMainPage() {
        return "/terminalsview/main_terminals";
    }

    @RequestMapping("/update_terminal")
    protected String generateTerminalUpdatePage(@RequestParam(name = "id") String id) {
        return "/terminalsview/update_terminal";
    }

    @RequestMapping("/add_terminal")
    protected String generateTerminalCreatePage() {
        return "/terminalsview/add_terminal";
    }

    @RequestMapping("/displaybarcodes")
    protected String generateBarcodePage() {
        return "/terminalsview/display_barcodes";
    }

    @RequestMapping("/departs")
    protected String generateDepartmentsMainPage() {
        return "/departmentsview/main_departments";
    }

    @RequestMapping("/add_department")
    protected String generateDepartmentsCreatePage() {
        return "/departmentsview/add_department";
    }

    @RequestMapping("/regs")
    protected String generateRegistrationsPage() {
        return "/registrationsview/registrations";
    }
}