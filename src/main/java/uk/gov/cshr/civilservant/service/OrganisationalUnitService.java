package uk.gov.cshr.civilservant.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cshr.civilservant.domain.CivilServantOrganisationReportingPermission;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.dto.OrganisationalUnitDto;
import uk.gov.cshr.civilservant.dto.factory.OrganisationalUnitDtoFactory;
import uk.gov.cshr.civilservant.repository.OrganisationalReportingPermissionRepository;
import uk.gov.cshr.civilservant.repository.OrganisationalUnitRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrganisationalUnitService extends SelfReferencingEntityService<OrganisationalUnit, OrganisationalUnitDto> {

    private OrganisationalUnitRepository repository;
    private OrganisationalReportingPermissionRepository organisationalReportingPermissionRepository;

    public OrganisationalUnitService(OrganisationalUnitRepository organisationalUnitRepository,
                                     OrganisationalUnitDtoFactory organisationalUnitDtoFactory,
                                     OrganisationalReportingPermissionRepository organisationalReportingPermissionRepository) {
        super(organisationalUnitRepository, organisationalUnitDtoFactory);
        this.repository = organisationalUnitRepository;
        this.organisationalReportingPermissionRepository = organisationalReportingPermissionRepository;
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

    public List<String> getOrganisationalUnitCodes() {
        return repository.findAllCodes();
    }

    @Transactional
    public Optional<OrganisationalUnit> get(Long id) {
        return repository.findById(id);
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

    private void getChildren(OrganisationalUnit organisationalUnit, List<OrganisationalUnit> organisationalUnits) {
        if (organisationalUnit.hasChildren()) {
            List<OrganisationalUnit> listOfChildren = organisationalUnit.getChildren();
            listOfChildren.stream().forEach(childOrganisationalUnit -> getOrganisationalUnitAndChildren(childOrganisationalUnit.getCode(), organisationalUnits));
        }
    }

    public Optional<OrganisationalUnit> getOrganisationalUnit(Long id) {
        return repository.findById(id);
    }

    public List<String> getOrganisationalUnitCodesForIds(List<String> organisationIds) {
        return repository.findAllOrganisationCodesForIds(organisationIds);
    }

    public List<Long> getOrganisationIdWithChildrenIds(List<String> listOrganisationCodes) {
        List<Long> listOrganisationIds = new ArrayList<>();
        listOrganisationCodes.forEach(x -> getOrganisationWithChildren(x).forEach(y -> listOrganisationIds.add(y.getId())));
        return listOrganisationIds;
    }

    public void addOrganisationReportingPermission(Long id, List<Long> organisationIds) {
        saveOrUpdate(id, organisationIds);
    }

    public void updateOrganisationReportingPermission(Long id, List<Long> organisationIds) {
        organisationalReportingPermissionRepository.deleteReportingPermissionById(id);
        saveOrUpdate(id, organisationIds);
    }

    private void saveOrUpdate(Long id, List<Long> organisationIds) {
        List<CivilServantOrganisationReportingPermission> list = new ArrayList<>();
        organisationIds.forEach(x -> list.add(new CivilServantOrganisationReportingPermission(id, x)));
        organisationalReportingPermissionRepository.saveAll (list);
    }

    public void deleteOrganisationReportingPermission(Long uid) {
        organisationalReportingPermissionRepository.deleteReportingPermissionById(uid);
    }
}
