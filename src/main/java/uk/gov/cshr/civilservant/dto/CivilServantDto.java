package uk.gov.cshr.civilservant.dto;

import lombok.Data;

import java.util.List;

@Data
public class CivilServantDto {
    private String id;
    private String uid;
    private String name;
    private String email;
    private String organisation;
    private String profession;
    private List<String> otherAreasOfWork;
    private String grade;
    private boolean forceOrgFlag;

    public CivilServantDto(Long id, String name, String organisation, String profession, String uid, String grade, boolean forceOrgFlag) {
        this.id = id.toString();
        this.name = name;
        this.organisation = organisation;
        this.profession = profession;
        this.uid = uid;
        this.grade = grade;
        this.forceOrgFlag = forceOrgFlag;
    }

    public CivilServantDto() {
    }
}
