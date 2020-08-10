package uk.gov.cshr.civilservant.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public interface RegistryEntity extends Serializable {
    Long getId();
}
