package uk.gov.cshr.civilservant.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class OrganisationalUnitDto extends DtoEntity {
  protected String formattedName;
  private String code;
}
