package uk.gov.cshr.civilservant.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public interface RegistryEntity {
    Long getId();
}
