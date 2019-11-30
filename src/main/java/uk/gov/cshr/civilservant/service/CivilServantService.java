package uk.gov.cshr.civilservant.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.domain.Grade;
import uk.gov.cshr.civilservant.domain.Interest;
import uk.gov.cshr.civilservant.domain.Profession;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;
import uk.gov.cshr.civilservant.repository.GradeRepository;
import uk.gov.cshr.civilservant.repository.InterestRepository;
import uk.gov.cshr.civilservant.repository.OrganisationalUnitRepository;
import uk.gov.cshr.civilservant.repository.ProfessionRepository;
import uk.gov.cshr.civilservant.service.exception.GeneralServiceException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CivilServantService {

    private final CivilServantRepository civilServantRepository;
    private final ProfessionRepository professionRepository;
    private final OrganisationalUnitRepository organisationalUnitRepository;
    private final GradeRepository gradeRepository;
    private final InterestRepository interestRepository;

    private enum ProfileItem {
        fullName, emailAddress, organisationUnit, otherAreasOfWork, profession, interests, grade
    }

    @Autowired
    public CivilServantService(CivilServantRepository civilServantRepository, ProfessionRepository professionRepository, OrganisationalUnitRepository organisationalUnitRepository, GradeRepository gradeRepository, InterestRepository interestRepository) {
        this.civilServantRepository = civilServantRepository;
        this.professionRepository = professionRepository;
        this.organisationalUnitRepository = organisationalUnitRepository;
        this.gradeRepository = gradeRepository;
        this.interestRepository = interestRepository;
    }

    /**
     * Update learner's profile based on given update details.
     * @param civilServant the Civil Servant
     * @param profileUpdate A mapping of profile item to new values. Only one mapping is expected.
     *                      For each profile item its mapping is expected below: <br/><br/>
     *                      fullName -> "John Big" <br/>
     *                      organisationUnit -> "12" <br/>
     *                      otherAreasOfWork -> "2,6,15" <br/>
     *                      profession -> '2'  (i.e. primary area of work) <br/>
     *                      interests -> '3,5,7' <br/>
     *                      grade -> '5' <br/>
     *                      emailAddress (Not sure)
     * @return true if the profile is updated successfully <br/>
     *         false if GeneralServiceException is being thrown
     */
    public boolean update(CivilServant civilServant, Map<String, String> profileUpdate) {
        checkAndThrowIllegalArgumentException(profileUpdate);
        Pair<String, String> profileUpdatePair = new ImmutablePair
                                    (profileUpdate.keySet().iterator().next(),profileUpdate.values().iterator().next());
        ProfileItem updateItem = ProfileItem.valueOf(profileUpdate.keySet().iterator().next());
        log.debug("Updating {} to {}....", profileUpdatePair.getKey(), profileUpdatePair.getValue());
        try {
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
                case emailAddress:
                    updateEmailAddress(civilServant, profileUpdatePair.getValue());
                    break;
            }
            civilServantRepository.saveAndFlush(civilServant);
            return true;
        } catch (GeneralServiceException gse) {
            log.error(String.format("Caught GeneralServiceException while updating CS profile: %s",
                                        gse.getMessage()), gse);
        }
        return false;
    }

    private void updateEmailAddress(CivilServant civilServant, String newEmail) {
        // not sure how email update in profile is handled
        throw new UnsupportedOperationException("updateEmailAddress is not implemented yet");
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
        // comma-seperated profession ids
        String[] newProfessionIds = newAreasOfWork.split(",");
        if (newProfessionIds.length > 0) {
            Set<Profession> newProfessions =  findProfessions (newProfessionIds);
            civilServant.setOtherAreasOfWork(newProfessions);
        }
    }

    private Set<Profession> findProfessions(String[] professionIds) {
        return Arrays.stream(professionIds)
                .map(professionId-> professionRepository.getOne(Long.parseLong(professionId)))
                .collect(Collectors.toSet());
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

    private void updateInterests(CivilServant civilServant, String newInterestIds) {
        String[] interestIds = newInterestIds.split(",");
        if (interestIds.length > 0){
            Set<Interest> newInterests = findInterests(interestIds);
            civilServant.setInterests(newInterests);
        }
    }

    private Set<Interest> findInterests(final String[] interestIds) {
        final Set<Interest> interests = new HashSet<>(interestIds.length);
        for (String interestId: interestIds) {
            Optional<Interest> interstOptional = interestRepository.findById(Long.parseLong(interestId));
            interstOptional.orElseThrow(()->
                new GeneralServiceException(String.format("Interest %s can not be found !", interestId))
            );
            interests.add(interstOptional.get());
        }
        return interests;
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

    private void updateFullName(final CivilServant civilServant, final String newName) {
        civilServant.setFullName(newName);
    }

    private void checkAndThrowIllegalArgumentException(final Map<String, String> entry) {
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
