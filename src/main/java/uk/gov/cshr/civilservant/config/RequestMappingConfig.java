package uk.gov.cshr.civilservant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import uk.gov.cshr.civilservant.mapping.RoleMappingHandlerMapping;

@Configuration
public class RequestMappingConfig extends WebMvcConfigurationSupport {

  @Override
  @Bean
  public RequestMappingHandlerMapping requestMappingHandlerMapping() {

    RoleMappingHandlerMapping mapping = new RoleMappingHandlerMapping();

    mapping.setOrder(Ordered.HIGHEST_PRECEDENCE);

    return mapping;
  }
}
