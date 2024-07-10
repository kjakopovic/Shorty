package asee.shortyapplication.config;

import asee.shortyapplication.shorty.dao.IUserDAO;
import asee.shortyapplication.shorty.interfaces.IUserService;
import asee.shortycore.models.authentication.UserModel;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//Potrebna je implementacija custom jwt authentication convertera jer
// moramo dodati prefix ROLE_ na role da springboot prepozna kao tocnu rolu

@Component
@AllArgsConstructor
public class CustomJwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    @Value("${jwt.auth.converter.principle-attribute}")
    private final String principleAttribute = "preferred_username";
    @Value("${jwt.auth.converter.client-id}")
    private final String clientId = "shorty-rest-api";

    private final String rolePrefix = "ROLE_";

    private final IUserDAO userDao;

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter
            = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        var authorities = Stream
            .concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractResourceRoles(jwt).stream()
            )
            .collect(Collectors.toSet());
        
        return new JwtAuthenticationToken(jwt, authorities, getPrincipalClaimName(jwt));
    }

    private String getPrincipalClaimName(Jwt jwt) {
        String username = jwt.getClaim(principleAttribute);

        var user = new UserModel();
        user.setAccountId(username);

        userDao.save(user);

        return username;
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        if (jwt.getClaim("resource_access") == null) {
            return Set.of();
        }

        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

        if (resourceAccess.get(clientId) == null) {
            return Set.of();
        }

        var resource = (Map<String, Object>) resourceAccess.get(clientId);

        var resourceRoles = (Collection<String>) resource.get("roles");

        return resourceRoles
                .stream()
                .map(x -> new SimpleGrantedAuthority(rolePrefix + x))
                .collect(Collectors.toSet());
    }
}
