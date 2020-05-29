package uk.gov.cshr.civilservant.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cshr.civilservant.domain.AgencyToken;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.dto.AgencyTokenResponseDto;
import uk.gov.cshr.civilservant.dto.OrganisationalUnitDto;
import uk.gov.cshr.civilservant.dto.factory.OrganisationalUnitDtoFactory;
import uk.gov.cshr.civilservant.exception.*;
import uk.gov.cshr.civilservant.repository.OrganisationalUnitRepository;
import uk.gov.cshr.civilservant.service.identity.IdentityService;

import java.util.*;

@Slf4j
@Service
@Transactional
public class OrganisationalUnitService extends SelfReferencingEntityService<OrganisationalUnit, OrganisationalUnitDto> {

    private OrganisationalUnitRepository repository;
    private AgencyTokenService agencyTokenService;
    private IdentityService identityService;

    public OrganisationalUnitService(OrganisationalUnitRepository organisationalUnitRepository, OrganisationalUnitDtoFactory organisationalUnitDtoFactory, AgencyTokenService agencyTokenService, IdentityService identityService) {
        super(organisationalUnitRepository, organisationalUnitDtoFactory);
        this.repository = organisationalUnitRepository;
        this.agencyTokenService = agencyTokenService;
        this.identityService = identityService;
    }

    public List<OrganisationalUnit> getOrganisationWithParents(String code) {
        List<OrganisationalUnit> organisationalUnitList = new ArrayList<>();
        getOrganisationalUnitAndParent(code, organisationalUnitList);

        return organisationalUnitList;
    }

    public List<OrganisationalUnit> getOrganisationWithChildren(String code) {
        List<OrganisationalUnit> organisationalUnitList = new ArrayList<>();
        getOrganisationalUnitAndChildren(code, organisationalUnitList);

        return organisationalUnitList;
    }

    public List<OrganisationalUnit> getOrganisationsForDomain(String domain, String uid) {
        // if agency token person return filtered list
        // else return all/everything
        boolean isAgencyTokenDomain = agencyTokenService.isDomainInAgency(domain);

        if(isAgencyTokenDomain) {
            log.debug("is an agency token domain, returning filtered organisation list");
            AgencyToken agencyToken = agencyTokenService.getAgencyTokenByUid(uid)
                    .orElseThrow(() -> new TokenDoesNotExistException());

            OrganisationalUnit organisationalUnit = repository.findOrganisationByAgencyToken(agencyToken)
                    .orElseThrow(() -> new NoOrganisationsFoundException((domain)));

            return getOrganisationWithChildren(organisationalUnit.getCode());
        } else {
            log.debug("Getting all organisations");
            List<OrganisationalUnit> organisationalUnits = repository.findAll();
            return organisationalUnits;
        }
    }

    public List<OrganisationalUnit> getAll() {
        return repository.findAll();
    }

    private List<OrganisationalUnit> getOrganisationalUnitAndChildren(String code, List<OrganisationalUnit> organisationalUnits) {
        repository.findByCode(code).ifPresent(organisationalUnit -> {
            organisationalUnits.add(organisationalUnit);
            getChildren(organisationalUnit, organisationalUnits);
        });

        return organisationalUnits;
    }

    private List<OrganisationalUnit> getOrganisationalUnitAndParent(String code, List<OrganisationalUnit> organisationalUnits) {
        repository.findByCode(code).ifPresent(organisationalUnit -> {
            organisationalUnits.add(organisationalUnit);
            getParent(organisationalUnit, organisationalUnits);
        });

        return organisationalUnits;
    }

    private void getParent(OrganisationalUnit organisationalUnit, List<OrganisationalUnit> organisationalUnits) {
        Optional<OrganisationalUnit> parent = Optional.ofNullable(organisationalUnit.getParent());
        parent.ifPresent(parentOrganisationalUnit -> getOrganisationalUnitAndParent(parentOrganisationalUnit.getCode(), organisationalUnits));
    }

    private void getChildren(OrganisationalUnit organisationalUnit, List<OrganisationalUnit> organisationalUnits) {
        if (organisationalUnit.hasChildren()) {
            List<OrganisationalUnit> listOfChildren = organisationalUnit.getChildren();
            listOfChildren.stream().forEach(childOrganisationalUnit -> getOrganisationalUnitAndChildren(childOrganisationalUnit.getCode(), organisationalUnits));
        }
    }

    public Optional<OrganisationalUnit> getOrganisationalUnit(Long id) {
        return repository.findById(id);
    }

    public List<OrganisationalUnit> getOrganisationsNormalised() {
        return repository.findAllNormalised();
    }

    public OrganisationalUnit setAgencyToken(OrganisationalUnit organisationalUnit, AgencyToken agencyToken) {
        if (organisationalUnit.getAgencyToken() != null) {
            throw new TokenAlreadyExistsException(organisationalUnit.getId().toString());
        }

        organisationalUnit.setAgencyToken(agencyToken);

        return repository.save(organisationalUnit);
    }

    public OrganisationalUnit updateAgencyToken(OrganisationalUnit organisationalUnit, AgencyToken newToken) {
        AgencyToken currentToken = organisationalUnit.getAgencyToken();

        if (currentToken == null) {
             throw new TokenDoesNotExistException(organisationalUnit.getId().toString());
        }

        currentToken.setAgencyDomains(newToken.getAgencyDomains());
        currentToken.setCapacity(newToken.getCapacity());
        currentToken.setToken(newToken.getToken());

        return repository.save(organisationalUnit);
    }

    public OrganisationalUnit deleteAgencyToken(OrganisationalUnit organisationalUnit) {
        AgencyToken agencyToken = organisationalUnit.getAgencyToken();

        organisationalUnit.setAgencyToken(null);

        agencyTokenService.deleteAgencyToken(agencyToken);

        return repository.save(organisationalUnit);
    }

    public List<String> getOrganisationalUnitCodes() {
        return repository.findAllCodes();
    }

    @Transactional
    public Optional<OrganisationalUnit> get(Long id) {
        return repository.findById(id);
    }

    public AgencyTokenResponseDto getAgencyToken(Long organisationalUnitId) throws CSRSApplicationException {
        AgencyToken agencyToken = getOrganisationalUnit(organisationalUnitId)
                .map(OrganisationalUnit::getAgencyToken)
                .orElseThrow(TokenDoesNotExistException::new);

        return agencyTokenService.getAgencyTokenResponseDto(agencyToken);
    }

}
