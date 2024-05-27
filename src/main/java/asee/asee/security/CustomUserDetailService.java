package asee.asee.security;

import asee.asee.administration.models.UserEntity;
import asee.asee.administration.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final IUserRepository userRepository;

    @Autowired
    public CustomUserDetailService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String accountId) throws UsernameNotFoundException {
        UserEntity user = userRepository.findById(accountId).orElseThrow(() ->
                new UsernameNotFoundException("Username not found."));

        return new User(user.getAccountId(), user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("USER")));
    }
}
