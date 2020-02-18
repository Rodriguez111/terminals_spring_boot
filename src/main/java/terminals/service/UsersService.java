package terminals.service;

import org.json.JSONObject;
import terminals.models.User;

import java.util.List;
import java.util.Map;

public interface UsersService {

    List<User> findAllUsers();

    List<User> findActiveUsers();

    long getCountOfAllUsers();

    int getCountOfActiveUsers();

    User findUserById(String id);

    Map<String, String> addUser(Map<String, String> user);

    Map<String, String> updateUser(String id, Map<String, String> user);

    Map<String, String> deleteUser(String id);
}
