package uk.gov.cshr.civilservant.service.scheduler;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

public final class SecurityUtils {

    private SecurityUtils() {}

    protected static SecurityContext createSchedulerSecurityContext() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("INTERNAL");
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "schedule",
                "",
                authorities
        );
        context.setAuthentication(authentication);

        return context;
    }
}
