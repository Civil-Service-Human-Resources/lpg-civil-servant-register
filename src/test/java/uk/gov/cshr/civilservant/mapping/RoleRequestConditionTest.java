package uk.gov.cshr.civilservant.mapping;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SecurityContextHolder.class)
@PowerMockIgnore("javax.security.*")
public class RoleRequestConditionTest {

    @Test
    public void shouldCombineRoles() {

        RoleRequestCondition roleRequestCondition = new RoleRequestCondition("role1");
        RoleRequestCondition other = new RoleRequestCondition("role2");

        RoleRequestCondition result = roleRequestCondition.combine(other);

        assertEquals(new HashSet<>(Arrays.asList("role1", "role2")), result.getRoles());
    }

    @Test
    public void shouldReturnConditionIfRolesMatch() {
        String role = "test-role";
        HttpServletRequest request = mock(HttpServletRequest.class);

        mockStatic(SecurityContextHolder.class);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(SecurityContextHolder.getContext()).thenReturn(securityContext);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        GrantedAuthority grantedAuthority = mock(GrantedAuthority.class);
        when(grantedAuthority.getAuthority()).thenReturn(role);

        Collection<GrantedAuthority> authorities = Collections.singletonList(grantedAuthority);

        when((Collection<GrantedAuthority>) authentication.getAuthorities()).thenReturn(authorities);

        RoleRequestCondition roleRequestCondition = new RoleRequestCondition(role);

        assertEquals(roleRequestCondition, roleRequestCondition.getMatchingCondition(request));
    }

    @Test
    public void shouldReturnNullIfRolesDoNotMatch() {
        String role = "test-role";
        HttpServletRequest request = mock(HttpServletRequest.class);

        mockStatic(SecurityContextHolder.class);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(SecurityContextHolder.getContext()).thenReturn(securityContext);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        GrantedAuthority grantedAuthority = mock(GrantedAuthority.class);
        when(grantedAuthority.getAuthority()).thenReturn(role);

        Collection<GrantedAuthority> authorities = Collections.singletonList(grantedAuthority);

        when((Collection<GrantedAuthority>) authentication.getAuthorities()).thenReturn(authorities);

        RoleRequestCondition roleRequestCondition = new RoleRequestCondition("other role");

        assertNull(roleRequestCondition.getMatchingCondition(request));
    }

    @Test
    public void shouldCompareCondtionsByNumberOfRoles() {
        assertEquals(2, new RoleRequestCondition("role1", "role2", "role3").compareTo(
                new RoleRequestCondition("role1"), mock(HttpServletRequest.class)));

        assertEquals(-2, new RoleRequestCondition("role1").compareTo(
                new RoleRequestCondition("role1", "role2", "role3"), mock(HttpServletRequest.class)));
    }
}
