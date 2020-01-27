package terminals.service;

import org.json.JSONObject;

public interface UsersService {

    JSONObject findAllUsers();

    JSONObject findActiveUsers();

    JSONObject getCountOfAllUsers();

    JSONObject getCountOfActiveUsers();

    JSONObject findUserById(int id);

    JSONObject addUser(JSONObject jsonObject);

    JSONObject updateUser(JSONObject jsonObject);

    JSONObject deleteUser(int id);
}
