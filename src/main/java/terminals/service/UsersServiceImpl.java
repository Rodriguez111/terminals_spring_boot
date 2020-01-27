package terminals.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import terminals.models.*;
import terminals.repository.DepartmentRepository;
import terminals.repository.RegistrationRepository;
import terminals.repository.RoleRepository;
import terminals.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UsersServiceImpl implements UsersService {

    private UserRepository userRepository;

    private RoleRepository roleRepository;

    private DepartmentRepository departmentRepository;

    private RegistrationRepository registrationRepository;

    private PasswordEncoder passwordEncoder;


    private void sortUsersByFullName(List<User> users) {
        users.sort(new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                int result = 0;
                result = o1.getUserSurname().compareTo(o2.getUserSurname());
                if (result == 0) {
                    result = o1.getUserName().compareTo(o2.getUserName());
                }
                return result;
            }
        });
    }

    private void removeTerminalsFromUsersToAvoidRecursiveError(List<User> users) {
        users.forEach(u -> {
            if (u.getTerminal() != null) {
                u.getTerminal().setUser(null);
            }
        });
    }

    private List<User> getListOfAllUsers() {
        List<User> listOfUsers = (List<User>) userRepository.findAll();
        removeTerminalsFromUsersToAvoidRecursiveError(listOfUsers);
        sortUsersByFullName(listOfUsers);
        return listOfUsers;
    }


    @Override
    public JSONObject findAllUsers() {
        List<User> users = getListOfAllUsers();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("listOfUsers", users);
        return jsonObject;
    }

    @Override
    public JSONObject findActiveUsers() {
        List<User> users = getListOfAllUsers();
        List<User> resultList = users.stream().filter(User::isActive).collect(Collectors.toList());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("listOfUsers", resultList);
        return jsonObject;
    }

    @Override
    public JSONObject getCountOfAllUsers() {
        long count = userRepository.count();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("countOfAllUsers", count);
        return jsonObject;
    }

    @Override
    public JSONObject getCountOfActiveUsers() {
        int countOfActiveUsers = userRepository.countAllByIsActive(true);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("countOfActiveUsers", countOfActiveUsers);
        return jsonObject;
    }

    @Override
    public JSONObject findUserById(int id) {
        User user = userRepository.findById(id).get();
        List<User> users = new ArrayList<>();
        users.add(user);
        removeTerminalsFromUsersToAvoidRecursiveError(users);
        return new JSONObject(user);
    }

    @Transactional
    @Override
    public JSONObject addUser(JSONObject jsonObject) {
        String resultMessage = "OK";
        JSONObject params = jsonObject.getJSONObject("addUser");
        String login = params.getString("login");
        String password = passwordEncoder.encode(params.getString("password"));
        String name = params.getString("name");
        String surname = params.getString("surname");
        Role role = roleRepository.findByRole(params.getString("role"));
        Department department = departmentRepository.findByDepartment(params.getString("department"));
        boolean isActive = params.getBoolean("isActive");
        User existingUser = userRepository.findByUserLogin(login);
        if (existingUser != null) {
            resultMessage = "Логин существует";
        } else {
            User user = new User(login, password, name, surname, role, department, isActive);
            User userToAdd = setCurrentTimeStampForCreateDate(user);
            userRepository.save(userToAdd);
        }
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("userAddResult", resultMessage);
        return jsonResult;
    }

    @Transactional
    @Override
    public JSONObject updateUser(JSONObject jsonObject) {
        JSONObject params = jsonObject.getJSONObject("updateUser");
        String result = "OK";
        Optional<User> optionalUser = userRepository.findById(params.getInt("id"));
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            User updatedUser = updateUserFields(user, params);
            if (user.equals(updatedUser)) {
                result = "Нет полей для обновления";
            } else if (user.isActive() != updatedUser.isActive() && user.getTerminal() != null) {
                result = "Нельзя деактивировать пользователя, пока за ним числится терминал";
            } else if (user.getTerminal() != null
                    && ((user.getTerminal() != null && updatedUser.getTerminal() == null)
                    || (user.getDepartment() == null && updatedUser.getDepartment() != null)
                    || (user.getDepartment() != null && updatedUser.getDepartment() != null
                    && !user.getDepartment().equals(updatedUser.getDepartment())))) {
                result = "Нельзя сменить департамент пользователя, пока за ним числится терминал";
            } else {
                User userToUpdate = setCurrentTimeStampForUpdateDate(updatedUser);
                userRepository.save(userToUpdate);
            }
        }
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("userUpdateResult", result);
        return jsonResult;
    }

    @Transactional
    @Override
    public JSONObject deleteUser(int id) {
        String result = "Невозможно удалить пользователя, пока существуют зависимые записи";
        if (!checkDependency(id)) {
            Optional<User> optionalUser = userRepository.findById(id);
            optionalUser.ifPresent(user -> userRepository.delete(user));
            result = "OK";
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userDeleteResult", result);
        return jsonObject;
    }

    private User updateUserFields(User user, JSONObject jsonObject) {
        User updatedUser = user.clone();
        if (validateField(jsonObject.getString("login"))) {
            updatedUser.setUserLogin(jsonObject.getString("login"));
        }
        if (validateField(jsonObject.getString("password"))) {
            String newPassword = passwordEncoder.encode(jsonObject.getString("password"));
            updatedUser.setUserPassword(newPassword);
        }
        if (validateField(jsonObject.getString("name"))) {
            updatedUser.setUserName(jsonObject.getString("name"));

        }
        if (validateField(jsonObject.getString("surname"))) {
            updatedUser.setUserSurname(jsonObject.getString("surname"));

        }
        if (!jsonObject.getString("role").equals(user.getRole().getRole())) {
            Role role = roleRepository.findByRole(jsonObject.getString("role"));
            updatedUser.setRole(role);

        }

        Department department = departmentRepository.findByDepartment(jsonObject.getString("department"));
        updatedUser.setDepartment(department);

        updatedUser.setActive(jsonObject.getBoolean("isActive"));
        return updatedUser;
    }

    private boolean validateField(String value) {
        return value != null && !value.equals("");
    }

    private boolean checkDependency(int id) {
        boolean result = false;
        Registration registration = registrationRepository.findFirstByUserId(id);
        if (registration == null) {
            registration = registrationRepository.findFirstByAdminGaveOutId(id);
        }
        if (registration == null) {
            registration = registrationRepository.findFirstByAdminGotId(id);
        }
        if (registration != null) {
            result = true;
        }
        return result;
    }

    private User setCurrentTimeStampForCreateDate(User user) {
        String currentTimeStamp = generateCurrentTimeStamp();
        user.setCreateDate(currentTimeStamp);
        return user;
    }

    private User setCurrentTimeStampForUpdateDate(User user) {
        String currentTimeStamp = generateCurrentTimeStamp();
        user.setLastUpdateDate(currentTimeStamp);
        return user;
    }

    private String generateCurrentTimeStamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        return formatter.format(now);
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Autowired
    public void setDepartmentRepository(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Autowired
    public void setRegistrationRepository(RegistrationRepository registrationRepository) {
        this.registrationRepository = registrationRepository;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
}