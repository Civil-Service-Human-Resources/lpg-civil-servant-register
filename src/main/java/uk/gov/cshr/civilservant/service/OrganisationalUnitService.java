package uk.gov.cshr.civilservant.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cshr.civilservant.domain.AgencyToken;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.dto.AgencyTokenResponseDto;
import uk.gov.cshr.civilservant.dto.OrganisationalUnitDto;
import uk.gov.cshr.civilservant.dto.factory.OrganisationalUnitDtoFactory;
import uk.gov.cshr.civilservant.exception.CSRSApplicationException;
import uk.gov.cshr.civilservant.exception.NoOrganisationsFoundException;
import uk.gov.cshr.civilservant.exception.TokenAlreadyExistsException;
import uk.gov.cshr.civilservant.exception.TokenDoesNotExistException;
import uk.gov.cshr.civilservant.repository.OrganisationalUnitRepository;
import uk.gov.cshr.civilservant.service.identity.IdentityService;

import java.util.*;
import java.util.stream.Collectors;

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

    public List<OrganisationalUnit> getOrganisationsForDomain(String domain) {
        boolean isWhitelistedUser = identityService.isDomainWhiteListed(domain);

        if(isWhitelistedUser) {
            log.info("Getting all organisations");
            List<OrganisationalUnit> organisationalUnits = repository.findAll();
            return organisationalUnits;
        }

        Iterable<AgencyToken> agencyTokens = agencyTokenService.getAllAgencyTokensByDomain(domain);

        if(agencyTokens.iterator().hasNext()) {
            log.info("Getting only organisations for domain: " + domain);
            Set<OrganisationalUnit> found = findOrganisationsForDomainForAgencyTokenUser(domain, agencyTokens);
            return found.stream().collect(Collectors.toList());
        } else {
            log.warn("user is not a whitelisted user or an agency token user");
            return Collections.emptyList();
        }
    }

    public List<OrganisationalUnit> getAll() {
        return repository.findAll();
    }

    private Set<OrganisationalUnit> findOrganisationsForDomainForAgencyTokenUser(String domain, Iterable<AgencyToken> agencyTokens) {
        // Each Organisational Unit has an AgencyToken.  1-to-1
        // Get all orgs
        // go through all and check if it contains the domain
        // if so put this org into the set to return.
        List<OrganisationalUnit> allOrgs = getAll();
        Set<OrganisationalUnit> orgUnitsWithAnAgencyTokenForThisDomain = allOrgs.stream()
                .filter(o -> o.getAgencyToken() != null)
                .filter(o -> !o.getAgencyToken().getAgencyDomains().isEmpty())
                .filter(o -> containsDomain(domain, o))
                .collect(Collectors.toSet());

        if(orgUnitsWithAnAgencyTokenForThisDomain.isEmpty()) {
            throw new NoOrganisationsFoundException(domain);
        }

        log.info("Found " + orgUnitsWithAnAgencyTokenForThisDomain.size() + " org units with an agency token that has this agency domain.");

        Set<OrganisationalUnit> matchingOrganisationalUnitsAndChildrenToBeReturned = new HashSet<>();
        List<OrganisationalUnit> list = new ArrayList<>();

        for (OrganisationalUnit ou : orgUnitsWithAnAgencyTokenForThisDomain) {
            List<OrganisationalUnit> currentAndKids = getOrganisationalUnitAndChildren(ou.getCode(), list);
            matchingOrganisationalUnitsAndChildrenToBeReturned.addAll(currentAndKids);
        }

        return matchingOrganisationalUnitsAndChildrenToBeReturned;
    }

    private boolean containsDomain(String domain, OrganisationalUnit o) {
        return o.getAgencyToken().getAgencyDomains().stream().anyMatch(ad -> ad.getDomain().equals(domain));
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
