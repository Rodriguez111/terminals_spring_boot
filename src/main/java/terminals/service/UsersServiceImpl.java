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

    private void removeRecursiveDataFromUsers(List<User> users) {
        users.forEach(u -> {
            if (u.getTerminal() != null) {
                u.getTerminal().setUser(null);
            }
        });
    }

    private List<User> getListOfAllUsers() {
        List<User> listOfUsers = (List<User>) userRepository.findAll();
        removeRecursiveDataFromUsers(listOfUsers);
        sortUsersByFullName(listOfUsers);
        return listOfUsers;
    }

    private boolean loginExists (String login) {
        boolean result = false;
        User existingUser = userRepository.findByUserLogin(login);
        if (existingUser != null) {
            result = true;
        }
        return result;
    }

    private boolean loginExistsForUpdate (int id, String login) {
        boolean result = false;
        User existingUser = userRepository.findByByUserLoginForUpdate(login,id);
        if (existingUser != null) {
            result = true;
        }
        return result;
    }


    @Override
    public List<User> findAllUsers() {
        return getListOfAllUsers();
    }


    @Override
    public List<User> findActiveUsers() {
        List<User> users = getListOfAllUsers();
       return users.stream().filter(User::isActive).collect(Collectors.toList());
    }


    @Override
    public long getCountOfAllUsers() {
        return userRepository.count();
    }

    @Override
    public int getCountOfActiveUsers() {
        return userRepository.countAllByIsActive(true);
    }

    @Override
    public User findUserById(String id) {
        int userId = Integer.parseInt(id);
        User user = userRepository.findById(userId).get();
        List<User> users = new ArrayList<>();
        users.add(user);
        removeRecursiveDataFromUsers(users);
        return user;
    }

    @Transactional
    @Override
    public Map<String, String> addUser(Map<String, String> usersParams) {
        String resultMessage = "OK";
        String login = usersParams.get("login");
        String password = passwordEncoder.encode(usersParams.get("password"));
        String name = usersParams.get("name");
        String surname = usersParams.get("surname");
        Role role = roleRepository.findByRole(usersParams.get("role"));
        Department department = departmentRepository.findByDepartment(usersParams.get("department"));
        boolean isActive = usersParams.get("isActive").equals("true");
        if (loginExists(login)) {
            resultMessage = "Логин " + login + " уже существует";
        } else {
            User user = new User(login, password, name, surname, role, department, isActive);
            User userToAdd = setCurrentTimeStampForCreateDate(user);
            userRepository.save(userToAdd);
        }
        Map<String, String> result = new HashMap<>();
        result.put("userAddResult", resultMessage);
        return result;
    }

    @Transactional
    @Override
    public Map<String, String> updateUser(String id, Map<String, String> usersParams) {
        String resultMessage = "OK";
        int userId = Integer.parseInt(id);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            User updatedUser = updateUserFields(user, usersParams);
            if (user.equals(updatedUser)) {
                resultMessage = "Нет полей для обновления";
            } else if (loginExistsForUpdate(updatedUser.getId(), updatedUser.getUserLogin())) {
                resultMessage = "Пользователь с логином " + updatedUser.getUserLogin() + " уже существует";
            } else if (user.isActive() != updatedUser.isActive() && user.getTerminal() != null) {
                resultMessage = "Нельзя деактивировать пользователя, пока за ним числится терминал";
            } else if (user.getTerminal() != null
                    && ((user.getTerminal() != null && updatedUser.getTerminal() == null)
                    || (user.getDepartment() == null && updatedUser.getDepartment() != null)
                    || (user.getDepartment() != null && updatedUser.getDepartment() != null
                    && !user.getDepartment().equals(updatedUser.getDepartment())))) {
                resultMessage = "Нельзя сменить департамент пользователя, пока за ним числится терминал";
            } else {
                User userToUpdate = setCurrentTimeStampForUpdateDate(updatedUser);
                userRepository.save(userToUpdate);
            }
        }
        Map<String, String> result = new HashMap<>();
        result.put("userUpdateResult", resultMessage);
        return result;
    }

    @Transactional
    @Override
    public Map<String, String> deleteUser(String id) {
        String resultMessage = "Невозможно удалить пользователя, пока существуют зависимые записи";
        int userId = Integer.parseInt(id);
        if (!checkDependency(userId)) {
            Optional<User> optionalUser = userRepository.findById(userId);
            optionalUser.ifPresent(user -> userRepository.delete(user));
            resultMessage = "OK";
        }
        Map<String, String> result = new HashMap<>();
        result.put("userDeleteResult", resultMessage);
        return result;
    }

    private User updateUserFields(User user, Map<String, String> userParams) {
        User updatedUser = user.clone();
        if (validateField(userParams.get("login"))) {
            updatedUser.setUserLogin(userParams.get("login"));
        }
        if (validateField(userParams.get("password"))) {
            String newPassword = passwordEncoder.encode(userParams.get("password"));
            updatedUser.setUserPassword(newPassword);
        }
        if (validateField(userParams.get("name"))) {
            updatedUser.setUserName(userParams.get("name"));

        }
        if (validateField(userParams.get("surname"))) {
            updatedUser.setUserSurname(userParams.get("surname"));

        }
        if (!userParams.get("role").equals(user.getRole().getRole())) {
            Role role = roleRepository.findByRole(userParams.get("role"));
            updatedUser.setRole(role);

        }

        Department department = departmentRepository.findByDepartment(userParams.get("department"));
        updatedUser.setDepartment(department);

        updatedUser.setActive(userParams.get("isActive").equals("true"));
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