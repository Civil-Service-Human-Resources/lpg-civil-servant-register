package uk.gov.cshr.civilservant.domain;

import org.springframework.data.rest.core.config.Projection;

import java.util.Set;

@Projection(name = "allCivilServantDetails", types = {CivilServant.class})
public interface AllCivilServantDetails {

    String getFullName();

    OrganisationalUnit getOrganisationalUnit();

    Grade getGrade();

    Profession getProfession();

    Set<Profession> getOtherAreasOfWork();

    String getLineManagerName();

    String getLineManagerEmailAddress();
}
