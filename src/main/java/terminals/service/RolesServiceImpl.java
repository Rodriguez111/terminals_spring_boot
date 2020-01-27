package terminals.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import terminals.models.Role;
import terminals.repository.RoleRepository;

import java.util.List;

@Service
public class RolesServiceImpl implements RolesService {

    private RoleRepository roleRepository;

    @Override
    public JSONObject findAllRoles() {
        List<Role> roles = (List<Role>) roleRepository.findByRoleNot("root");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("listOfRoles", roles);
        return jsonObject;
    }

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
}
