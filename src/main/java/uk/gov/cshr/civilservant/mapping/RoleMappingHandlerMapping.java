package uk.gov.cshr.civilservant.mapping;

import java.lang.reflect.Method;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public class RoleMappingHandlerMapping extends RequestMappingHandlerMapping {
  @Override
  protected RequestCondition<RoleRequestCondition> getCustomTypeCondition(Class<?> type) {
    return createCondition(AnnotationUtils.findAnnotation(type, RoleMapping.class));
  }

  @Override
  protected RequestCondition<RoleRequestCondition> getCustomMethodCondition(Method method) {
    return createCondition(AnnotationUtils.findAnnotation(method, RoleMapping.class));
  }

  private RequestCondition<RoleRequestCondition> createCondition(RoleMapping annotation) {
    if (annotation != null) {
      return new RoleRequestCondition(annotation.value());
    }
    return null;
  }
}
