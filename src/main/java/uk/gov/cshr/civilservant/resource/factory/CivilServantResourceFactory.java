package uk.gov.cshr.civilservant.resource.factory;

import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Component;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.dto.OrgCodeDTO;
import uk.gov.cshr.civilservant.resource.CivilServantResource;
import uk.gov.cshr.civilservant.resource.OrganisationalUnitResource;
import uk.gov.cshr.civilservant.service.identity.IdentityService;

import java.util.Optional;

@Component
public class CivilServantResourceFactory {
    private final IdentityService identityService;
    private final LinkFactory linkFactory;

    public CivilServantResourceFactory(IdentityService identityService, LinkFactory linkFactory) {
        this.identityService = identityService;
        this.linkFactory = linkFactory;
    }

    public Resource<CivilServantResource> create(CivilServant civilServant) {

        CivilServantResource civilServantResource = new CivilServantResource();

        civilServantResource.setFullName(civilServant.getFullName());

        if (civilServant.getGrade().isPresent()) {
            civilServantResource.setGrade(civilServant.getGrade().get());
        }

        if (civilServant.getOrganisationalUnit().isPresent()) {
            civilServantResource.setOrganisationalUnit(civilServant.getOrganisationalUnit().get());
        }

        if (civilServant.getProfession().isPresent()) {
            civilServantResource.setProfession(civilServant.getProfession().get());
        }

        if (civilServant.getLineManager().isPresent()) {
            CivilServant lineManager = civilServant.getLineManager().get();

            civilServantResource.setLineManagerName(lineManager.getFullName());
            civilServantResource.setLineManagerEmailAddress(identityService.getEmailAddress(lineManager));
        }

        civilServantResource.setInterests(civilServant.getInterests());
        civilServantResource.setOtherAreasOfWork(civilServant.getOtherAreasOfWork());

        Resource<CivilServantResource> resource = new Resource<>(civilServantResource);

        resource.add(linkFactory.createSelfLink(civilServant));
        resource.add(linkFactory.createRelationshipLink(civilServant, "organisationalUnit"));
        resource.add(linkFactory.createRelationshipLink(civilServant, "grade"));
        resource.add(linkFactory.createRelationshipLink(civilServant, "profession"));

        return resource;
    }

    public Resource<OrganisationalUnitResource> getCivilServantOrganisationalUnit(CivilServant civilServant) {
        OrganisationalUnitResource organisationalUnitResource = new OrganisationalUnitResource();

        if (civilServant.getOrganisationalUnit().isPresent()) {
            organisationalUnitResource.setOrganisationalUnit(civilServant.getOrganisationalUnit().get());
        }

        Resource<OrganisationalUnitResource> resource = new Resource<>(organisationalUnitResource);
        return resource;
    }

    public Optional<OrgCodeDTO> getCivilServantOrganisationalUnitCode(CivilServant civilServant) {
        if(civilServant.getOrganisationalUnit().isPresent() && civilServant.getOrganisationalUnit().get().getCode() != null){
            OrgCodeDTO dto = new OrgCodeDTO();
            dto.setCode(civilServant.getOrganisationalUnit().get().getCode());
            return Optional.of(dto);
        } else {
            return Optional.empty();
        }
    }

}
