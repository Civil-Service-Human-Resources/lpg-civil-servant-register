package uk.gov.cshr.civilservant.mapping;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.annotation.AnnotationUtils;
import uk.gov.cshr.civilservant.controller.ReportController;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AnnotationUtils.class)
public class RoleMappingHandlerMappingTest {

    private RoleMappingHandlerMapping handlerMapping = new RoleMappingHandlerMapping();

    @Test
    public void shouldReturnRoleRequestConditionForClassAnnotation() {
        String[] roles = {"test-role"};

        RoleMapping annotation = mock(RoleMapping.class);
        when(annotation.value()).thenReturn(roles);

        Class type = ReportController.class;

        mockStatic(AnnotationUtils.class);

        when(AnnotationUtils.findAnnotation(type, RoleMapping.class)).thenReturn(annotation);

        assertEquals(new RoleRequestCondition(roles), handlerMapping.getCustomTypeCondition(type));
    }

    @Test
    public void shouldReturnNullIfAnnotationNotFoundOnClass() {
        Class type = ReportController.class;

        mockStatic(AnnotationUtils.class);

        when(AnnotationUtils.findAnnotation(type, RoleMapping.class)).thenReturn(null);

        assertNull(handlerMapping.getCustomTypeCondition(type));
    }

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