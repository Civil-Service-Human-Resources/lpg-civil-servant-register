package uk.gov.cshr.civilservant.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.lang.reflect.Method;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.annotation.AnnotationUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AnnotationUtils.class)
public class RoleMappingHandlerMappingTest {

    private RoleMappingHandlerMapping handlerMapping = new RoleMappingHandlerMapping();

    @Test
    public void shouldReturnRoleRequestConditionForMethodAnnotation() {
        String[] roles = {"test-role"};

        RoleMapping annotation = mock(RoleMapping.class);
        when(annotation.value()).thenReturn(roles);

        Method method = this.getClass().getMethods()[0];

        mockStatic(AnnotationUtils.class);

        when(AnnotationUtils.findAnnotation(method, RoleMapping.class)).thenReturn(annotation);

        assertEquals(new RoleRequestCondition(roles), handlerMapping.getCustomMethodCondition(method));
    }

    @Test
    public void shouldReturnNullIfAnnotationNotFoundOnMethod() {

        Method method = this.getClass().getMethods()[0];

        mockStatic(AnnotationUtils.class);

        when(AnnotationUtils.findAnnotation(method, RoleMapping.class)).thenReturn(null);

        assertNull(handlerMapping.getCustomMethodCondition(method));
    }

}