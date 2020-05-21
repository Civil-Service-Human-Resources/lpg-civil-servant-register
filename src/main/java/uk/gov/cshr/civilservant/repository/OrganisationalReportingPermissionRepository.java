package uk.gov.cshr.civilservant.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cshr.civilservant.domain.CivilServantOrganisationReportingPermission;
import uk.gov.cshr.civilservant.domain.CivilServantOrganisationReportingPermissionPK;

import java.util.List;

@Repository
@RepositoryRestResource
public interface OrganisationalReportingPermissionRepository
        extends CrudRepository<CivilServantOrganisationReportingPermission, CivilServantOrganisationReportingPermissionPK> {

    @Query("select orp.organisationId from CivilServantOrganisationReportingPermission orp where orp.civilServantId = ?1")
    List<Long> findAllOrganisationIdByCivilServantId(Long id);

    @Transactional
    @Modifying
    @Query("delete from CivilServantOrganisationReportingPermission orp where orp.civilServantId = ?1")
    void deleteReportingPermissionById(Long id);
}
