package uk.gov.cshr.civilservant.domain;

import org.springframework.data.rest.core.config.Projection;

import java.util.Optional;
import java.util.Set;

@Projection(name = "allCivilServantDetails", types = {CivilServant.class})
public interface AllCivilServantDetails {

    String getFullName();

    Optional<OrganisationalUnit> getOrganisationalUnit();

    Optional<Grade> getGrade();

    Optional<Profession> getProfession();

    Set<Profession> getOtherAreasOfWork();

    String getLineManagerUid();
}
