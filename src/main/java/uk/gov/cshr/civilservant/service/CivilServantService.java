package uk.gov.cshr.civilservant.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.domain.Grade;
import uk.gov.cshr.civilservant.domain.Profession;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;
import uk.gov.cshr.civilservant.repository.GradeRepository;
import uk.gov.cshr.civilservant.repository.OrganisationalUnitRepository;
import uk.gov.cshr.civilservant.repository.ProfessionRepository;
import uk.gov.cshr.civilservant.service.exception.GeneralServiceException;

import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class CivilServantService {

    private final CivilServantRepository civilServantRepository;
    private final ProfessionRepository professionRepository;
    private final OrganisationalUnitRepository organisationalUnitRepository;
    private final GradeRepository gradeRepository;

    private enum ProfileItem {
        fullName, emailAddress, organisationUnit, otherAreasOfWork, profession, interests, grade, lineManager
    }

    @Autowired
    public CivilServantService(CivilServantRepository civilServantRepository, ProfessionRepository professionRepository, OrganisationalUnitRepository organisationalUnitRepository, GradeRepository gradeRepository) {
        this.civilServantRepository = civilServantRepository;
        this.professionRepository = professionRepository;
        this.organisationalUnitRepository = organisationalUnitRepository;
        this.gradeRepository = gradeRepository;
    }

    public boolean update(CivilServant civilServant, Map<String, String> profileUpdate) {
        checkAndThrowIllegalArgumentException(profileUpdate);
        Pair<String, String> profileUpdatePair = new ImmutablePair
                                    (profileUpdate.keySet().iterator().next(),profileUpdate.values().iterator().next());
        ProfileItem updateItem = ProfileItem.valueOf(profileUpdate.keySet().iterator().next());
        log.debug("Updating {} to {}....", profileUpdatePair.getKey(), profileUpdatePair.getValue());
        switch (updateItem) {
            case fullName:
                updateFullName(civilServant, profileUpdatePair.getValue());
                break;
            case profession:
                updatePrimaryAreaOfWork(civilServant, profileUpdatePair.getValue());
                break;
            case otherAreasOfWork:
                updateOtherAreasOfWork(civilServant, profileUpdatePair.getValue());
                break;
            case organisationUnit:
                updateDepartment(civilServant, profileUpdatePair.getValue());
                break;
            case grade:
                updateGrade(civilServant, profileUpdatePair.getValue());
                break;
            case interests:
                updateInterests(civilServant, profileUpdatePair.getValue());
                break;
            case emailAddress:break;
        }
        return true;
    }

    private void updateDepartment(CivilServant civilServant, String orgUnitId) {
        civilServant.getOrganisationalUnit().ifPresent(organisationalUnit -> {
            long newOrgUnitId = Long.parseLong(orgUnitId);
            if (organisationalUnit.getId() != newOrgUnitId) {
                civilServant.setOrganisationalUnit(organisationalUnitRepository.getOne(newOrgUnitId));
            }
        });
    }

    private void updateOtherAreasOfWork(CivilServant civilServant, String newAreasOfWork) {

    }

    private void updatePrimaryAreaOfWork(CivilServant civilServant, String professionId) {
        civilServant.getProfession().ifPresent(profession -> {
            long newProfessionId = Long.parseLong(professionId);
            if (profession.getId()!= newProfessionId){
                Profession newProfession = professionRepository.getOne(newProfessionId);
                civilServant.setProfession(newProfession);
            }
        });
    }

    //todo: a set of interests
    private void updateInterests(CivilServant civilServant, String newInterests) {
        civilServant.getInterests();
    }

    private void updateGrade(final CivilServant civilServant, final String gradeId) {
        civilServant.getGrade().ifPresent(grade-> {
            long newGradeId = Long.parseLong(gradeId);
            if (grade.getId() != newGradeId){
                Optional<Grade> gradeOptional = gradeRepository.findById(newGradeId);
                gradeOptional.ifPresent(civilServant::setGrade);
                gradeOptional.orElseThrow(()->
                        new GeneralServiceException(String.format("Can not find Grade with id %s ....", gradeId)));
            }
        });
    }

    private void updateFullName(CivilServant civilServant, String newName) {
        civilServant.setFullName(newName);
    }

    private void checkAndThrowIllegalArgumentException(Map<String, String> entry) {
        if (Objects.isNull(entry)){
            throw new IllegalArgumentException("Invalid profile update received while updating CivilServant! ");
        }

        if (entry.size()!=1){
            throw new IllegalArgumentException(
                    String.format("Only one profile update expected but received %s while updating CivilServant! ",
                            entry.size()));
        }

        String itemName = entry.keySet().iterator().next();

        boolean nodeFound = Arrays.stream(ProfileItem.values())
                .anyMatch(profileItem -> profileItem.name().equalsIgnoreCase(itemName));
        if (!nodeFound) {
            throw new IllegalArgumentException(
                    String.format("Item %s is not recognized as part of CivilServant hence can not be updated", itemName));
        }

    }
}
