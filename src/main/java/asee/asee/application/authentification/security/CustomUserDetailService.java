package asee.asee.application.authentification.security;

import asee.asee.application.authentification.dao.IUserDAO;
import asee.asee.application.authentification.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.NoSuchElementException;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final IUserDAO userDAO;

    @Autowired
    public CustomUserDetailService(IUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String accountId) throws UsernameNotFoundException {
        UserModel user;
        try {
            user = userDAO.findById(accountId);
        }catch (NoSuchElementException e) {
            throw new UsernameNotFoundException("Username not found.");
        }

        return new User(user.getAccountId(), user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("USER")));
    }
}
