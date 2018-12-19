package uk.gov.cshr.civilservant.dto;

import lombok.Data;

import java.util.List;

@Data
public class CivilServantDto {
    private String id;
    private String name;
    private String email;
    private String organisation;
    private String profession;
    private List<String> otherAreasOfWork;
    private String grade;
}
