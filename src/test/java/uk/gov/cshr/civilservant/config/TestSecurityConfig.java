package uk.gov.cshr.civilservant.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@Configuration
@Profile("test")
public class TestSecurityConfig extends SecurityConfig {

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.stateless(false);
    }
}
