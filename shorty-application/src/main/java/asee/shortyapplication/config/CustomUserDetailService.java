package asee.shortyapplication.config;

import asee.shortyapplication.authentication.dao.IUserDAO;
import asee.shortycore.models.authentication.UserModel;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final IUserDAO userDAO;

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
