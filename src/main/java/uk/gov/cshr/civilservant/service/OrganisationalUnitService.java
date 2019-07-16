package uk.gov.cshr.civilservant.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.dto.OrganisationalUnitDto;
import uk.gov.cshr.civilservant.dto.factory.OrganisationalUnitDtoFactory;
import uk.gov.cshr.civilservant.repository.OrganisationalUnitRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrganisationalUnitService extends SelfReferencingEntityService<OrganisationalUnit, OrganisationalUnitDto> {

    private OrganisationalUnitRepository repository;

    public OrganisationalUnitService(OrganisationalUnitRepository organisationalUnitRepository, OrganisationalUnitDtoFactory organisationalUnitDtoFactory) {
        super(organisationalUnitRepository, organisationalUnitDtoFactory);
        this.repository = organisationalUnitRepository;
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

    public List<OrganisationalUnit> getOrganisationsNormalised() {
        return repository.findAllNormalised();
    }

}
