package uk.gov.cshr.civilservant.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class CivilServantService {

    private final CivilServantRepository civilServantRepository;

    private enum ProfileItem {
        fullName, emailAddress, organisationUnit, otherAreasOfWork, profession, interests, grade, lineManager
    }

    @Autowired
    public CivilServantService(CivilServantRepository civilServantRepository) {
        this.civilServantRepository = civilServantRepository;
    }

    public boolean update(CivilServant civilServant, Map<String, String> profileUpdate) {
        checkAndThrowIllegalArgumentException(profileUpdate);
        Pair<String, String> profileUpdatePair = new ImmutablePair
                                    (profileUpdate.keySet().iterator().next(),profileUpdate.values().iterator().next());
        ProfileItem updateItem = ProfileItem.valueOf(profileUpdate.keySet().iterator().next());
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

    private void updateDepartment(CivilServant civilServant, String newDepartment) {
    }

    private void updateOtherAreasOfWork(CivilServant civilServant, String newAreasOfWork) {

    }

    private void updatePrimaryAreaOfWork(CivilServant civilServant, String newProfession) {
        civilServant.getProfession().ifPresent(profession -> {
            profession.setName(newProfession);
        });
    }

    //todo: a set of interests
    private void updateInterests(CivilServant civilServant, String newInterests) {
        civilServant.getInterests();
    }

    private void updateGrade(final CivilServant civilServant, final String newGrade) {
        civilServant.getGrade().ifPresent(gradeEntity-> {
            gradeEntity.setName(newGrade);
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
                    String.format("Only one profile update expected but received {} while updating CivilServant! ",
                            entry.size()));
        }

        String itemName = entry.keySet().iterator().next();

        boolean nodeFound = Arrays.stream(ProfileItem.values())
                .anyMatch(profileItem -> profileItem.name().equalsIgnoreCase(itemName));
        if (!nodeFound) {
            throw new IllegalArgumentException(
                    String.format("Item {} is not recognized as part of CivilServant hence can not be updated", itemName));
        }

    }
}
