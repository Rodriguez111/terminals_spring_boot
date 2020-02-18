package terminals.web;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.Lifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import terminals.models.User;
import terminals.repository.UserRepository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Collection;
import java.util.LinkedList;


@Component
public class AuthProviderImpl implements AuthenticationProvider {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String login = authentication.getName();

        User user = userRepository.findByUserLogin(login);
        if (user == null) {
            throw new UsernameNotFoundException("Пользователь с таким логином не найден");
        }
        String authPath = authentication.getCredentials().toString();

        if (user.getUserPassword().length() < 12) {
            if (!authPath.equals(user.getUserPassword())) {
                throw new BadCredentialsException("Неверный пароль");
            }
        } else {
            if (!passwordEncoder.matches(authPath, user.getUserPassword())) {
                throw new BadCredentialsException("Неверный пароль");
            }
        }
        if (user.getRole().getRole().equals("user")) {
            throw new BadCredentialsException("У " + login + " нет доступа в программу");
        }


        Collection<GrantedAuthority> grantedAuthorities = new LinkedList<GrantedAuthority>();
        grantedAuthorities.add(new SimpleGrantedAuthority(user.getRole().getRole()));
        CustomSecurityUser securityUser =  new CustomSecurityUser(user.getUserLogin(), user.getUserPassword(), grantedAuthorities, user.getUserName(), user.getUserSurname());

        return new UsernamePasswordAuthenticationToken(securityUser, null, grantedAuthorities);
    }


    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }


    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


}
