package terminals.web;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomSecurityUser extends User {
    // Here we add the extra fields of our user.
    private String name;
    private String surName;

    private static final long serialVersionUID = 1L;

    public CustomSecurityUser(String login,
                              String password,
                              Collection<GrantedAuthority> authorities,
                              String name, String surName) {
        super(login, password, authorities);
        this.name = name;
        this.surName = surName;
    }

    public String getName() {
        return name;
    }

    public String getSurName() {
        return surName;
    }

}
