package uk.gov.cshr.civilservant.resource;

import java.util.Set;

import lombok.Data;
import uk.gov.cshr.civilservant.domain.Grade;
import uk.gov.cshr.civilservant.domain.Interest;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.domain.Profession;

@Data
public class CivilServantResource {
  private String fullName;
  private Grade grade;
  private OrganisationalUnit organisationalUnit;
  private Profession profession;
  private Set<Profession> otherAreasOfWork;
  private Set<Interest> interests;
  private String lineManagerName;
  private String lineManagerEmailAddress;
  private Long userId;
}
