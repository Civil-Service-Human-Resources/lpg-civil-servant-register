package uk.gov.cshr.civilservant.dto;

import lombok.Data;

@Data
public class CivilServantReportDto {
    private String id;
    private String uid;
    private String name;
    private String email;
    private String organisation;
    private String profession;
    private String otherAreasOfWork;
    private String grade;

    public CivilServantReportDto(Long id, String name, String organisation, String profession, String uid, String grade, String otherAreasOfWork) {
        this.id = id.toString();
        this.name = name;
        this.organisation = organisation;
        this.profession = profession;
        this.uid = uid;
        this.grade = grade;
        this.otherAreasOfWork = otherAreasOfWork;
    }

    public CivilServantReportDto() {
    }
}
