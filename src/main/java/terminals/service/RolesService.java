package terminals.service;

import org.json.JSONObject;
import terminals.models.Role;

import java.util.List;

public interface RolesService {
    List<Role> findAllRoles();
}
