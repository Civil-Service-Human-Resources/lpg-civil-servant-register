package uk.gov.cshr.civilservant.dto;

import lombok.Data;

@Data
public class OrganisationalUnitDto {
    private String name;
    private String url;
    private String code;
    private String formattedName;
}
