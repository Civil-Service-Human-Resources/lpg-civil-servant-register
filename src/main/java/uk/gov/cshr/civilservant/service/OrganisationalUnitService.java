package uk.gov.cshr.civilservant.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cshr.civilservant.domain.AgencyToken;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.dto.OrganisationalUnitDto;
import uk.gov.cshr.civilservant.dto.factory.OrganisationalUnitDtoFactory;
import uk.gov.cshr.civilservant.exception.TokenAlreadyExistsException;
import uk.gov.cshr.civilservant.exception.TokenDoesNotExistException;
import uk.gov.cshr.civilservant.repository.OrganisationalUnitRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrganisationalUnitService extends SelfReferencingEntityService<OrganisationalUnit, OrganisationalUnitDto> {

    private OrganisationalUnitRepository repository;
    private AgencyTokenService agencyTokenService;

    public OrganisationalUnitService(OrganisationalUnitRepository organisationalUnitRepository, OrganisationalUnitDtoFactory organisationalUnitDtoFactory, AgencyTokenService agencyTokenService) {
        super(organisationalUnitRepository, organisationalUnitDtoFactory);
        this.repository = organisationalUnitRepository;
        this.agencyTokenService = agencyTokenService;
    }

    public List<OrganisationalUnit> getOrganisationWithParents(String code) {
        List<OrganisationalUnit> organisationalUnitList = new ArrayList<>();
        getOrganisationalUnit(code, organisationalUnitList);

        return organisationalUnitList;
    }

    private List<OrganisationalUnit> getOrganisationalUnit(String code, List<OrganisationalUnit> organisationalUnits) {
        repository.findByCode(code).ifPresent(organisationalUnit -> {
            organisationalUnits.add(organisationalUnit);
            getParent(organisationalUnit, organisationalUnits);
        });

        return organisationalUnits;
    }

    private void getParent(OrganisationalUnit organisationalUnit, List<OrganisationalUnit> organisationalUnits) {
        Optional<OrganisationalUnit> parent = Optional.ofNullable(organisationalUnit.getParent());
        parent.ifPresent(parentOrganisationalUnit -> getOrganisationalUnit(parentOrganisationalUnit.getCode(), organisationalUnits));
    }

    public Optional<OrganisationalUnit> getOrganisationalUnit(Long id) {
        return repository.findById(id);
    }

    public List<OrganisationalUnit> getOrganisationsNormalised() {
        return repository.findAllNormalised();
    }

    @Transactional
    public Optional<OrganisationalUnit> get(Long id) {
        return repository.findById(id);
    }

    public List<String> getOrganisationalUnitCodes() {
        return repository.findAllCodes();
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
}
