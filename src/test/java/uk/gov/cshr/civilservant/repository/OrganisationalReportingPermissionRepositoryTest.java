package uk.gov.cshr.civilservant.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cshr.civilservant.domain.CivilServantOrganisationReportingPermission;
import uk.gov.cshr.civilservant.dto.CivilServantReportDto;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
@WithMockUser(authorities = "INTERNAL")
public class OrganisationalReportingPermissionRepositoryTest {

    @Autowired
    private OrganisationalReportingPermissionRepository repository;

    @Autowired
    private CivilServantRepository civilServantRepository;

    @Autowired
    private IdentityRepository identityRepository;

    @Test
    public void shouldReturnAllOrganisationIdByCivilServantId() {

        List<CivilServantReportDto> listCSDto = civilServantRepository.findAllNormalised();

        Long civilServantId = Long.valueOf(listCSDto.get(0).getId());

        CivilServantOrganisationReportingPermission entity1 = new CivilServantOrganisationReportingPermission(civilServantId, 1L);
        CivilServantOrganisationReportingPermission entity2 = new CivilServantOrganisationReportingPermission(civilServantId, 2L);
        List<CivilServantOrganisationReportingPermission> list = Arrays.asList(entity1, entity2);
        repository.saveAll(list);

        List<Long> orgIds = repository.findAllOrganisationIdByCivilServantId(civilServantId);
        assertTrue(orgIds != null);
        assertTrue(orgIds.size() >= 2);
    }

    @Test
    public void shouldDeleteReportingPermissionByIdCivilServantId() {

        List<CivilServantReportDto> listCSDto = civilServantRepository.findAllNormalised();

        Long civilServantId = Long.valueOf(listCSDto.get(0).getId());

        CivilServantOrganisationReportingPermission entity1 = new CivilServantOrganisationReportingPermission(civilServantId, 1L);
        CivilServantOrganisationReportingPermission entity2 = new CivilServantOrganisationReportingPermission(civilServantId, 2L);
        List<CivilServantOrganisationReportingPermission> list = Arrays.asList(entity1, entity2);
        repository.saveAll(list);

        repository.deleteReportingPermissionById(civilServantId);
        List<Long> listId = repository.findAllOrganisationIdByCivilServantId(civilServantId);
        assertTrue(listId.isEmpty());
    }
}