package uk.gov.cshr.civilservant.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cshr.civilservant.domain.*;
import uk.gov.cshr.civilservant.dto.CivilServantReportDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@WithMockUser(authorities = "INTERNAL")
public class CivilServantRepositoryTest {

    @Autowired
    private CivilServantRepository civilServantRepository;

    @Autowired
    private IdentityRepository identityRepository;

    @Autowired
    private OrganisationalUnitRepository organisationalUnitRepository;

    @Autowired
    private ProfessionRepository professionRepository;

    @Autowired
    private OrganisationalReportingPermissionRepository reportingPermissionRepository;

    @Test
    public void shouldFindCivilServantByIdentity() {
        final Identity identity = new Identity("1");
        final CivilServant civilServant = new CivilServant(identity);

        identityRepository.save(identity);
        civilServantRepository.save(civilServant);

        Optional<CivilServant> civilServantOptional = civilServantRepository.findByIdentity(identity);
        assertTrue(civilServantOptional.isPresent());
    }

    @Test
    public void shouldNotFindCivilServantIfNotCreated() {

        final Identity identity = new Identity("2");

        identityRepository.save(identity);

        Optional<CivilServant> civilServant = civilServantRepository.findByIdentity(identity);
        assertFalse(civilServant.isPresent());
    }


    @Test
    public void shouldReturnCivilServantsByOrganisationalUnit() {
        OrganisationalUnit organisationalUnit1 = new OrganisationalUnit();
        organisationalUnit1.setName("Organisation1");
        organisationalUnit1.setCode("abc");

        organisationalUnitRepository.save(organisationalUnit1);

        OrganisationalUnit organisationalUnit2 = new OrganisationalUnit();
        organisationalUnit2.setName("Organisation2");
        organisationalUnit2.setCode("bcd");

        organisationalUnitRepository.save(organisationalUnit2);

        Identity identity1 = new Identity("1");
        CivilServant civilServant1 = new CivilServant(identity1);
        civilServant1.setOrganisationalUnit(organisationalUnit1);
        identityRepository.save(identity1);
        civilServantRepository.save(civilServant1);

        Identity identity2 = new Identity("2");
        CivilServant civilServant2 = new CivilServant(identity2);
        civilServant2.setOrganisationalUnit(organisationalUnit2);
        identityRepository.save(identity2);
        civilServantRepository.save(civilServant2);

        Identity identity3 = new Identity("3");
        CivilServant civilServant3 = new CivilServant(identity3);
        civilServant3.setOrganisationalUnit(organisationalUnit1);
        identityRepository.save(identity3);
        civilServantRepository.save(civilServant3);

        List<CivilServant> result = civilServantRepository.findAllByOrganisationalUnit(organisationalUnit1);

        assertEquals(2, result.size());
        assertEquals(civilServant1, result.get(0));
        assertEquals(civilServant3, result.get(1));
    }

    @Test
    @WithMockUser(authorities = {"INTERNAL", "PROFESSION_MANAGER"})
    public void shouldReturnCivilServantsByProfession() {
        Profession profession1 = new Profession("profession1");
        professionRepository.save(profession1);

        Profession profession2 = new Profession("profession2");
        professionRepository.save(profession2);

        Identity identity1 = new Identity("1");
        CivilServant civilServant1 = new CivilServant(identity1);
        civilServant1.setProfession(profession1);
        identityRepository.save(identity1);
        civilServantRepository.save(civilServant1);

        Identity identity2 = new Identity("2");
        CivilServant civilServant2 = new CivilServant(identity2);
        civilServant2.setProfession(profession2);
        identityRepository.save(identity2);
        civilServantRepository.save(civilServant2);

        Identity identity3 = new Identity("3");
        CivilServant civilServant3 = new CivilServant(identity3);
        civilServant3.setProfession(profession1);
        identityRepository.save(identity3);
        civilServantRepository.save(civilServant3);

        List<CivilServant> result = civilServantRepository.findAllByProfession(profession1);

        assertEquals(2, result.size());
        assertEquals(civilServant1, result.get(0));
        assertEquals(civilServant3, result.get(1));
    }


    @Test
    @WithMockUser(authorities = {"INTERNAL", "PROFESSION_MANAGER"})
    public void shouldFindAllByReportingOrganisationId() {
        OrganisationalUnit organisationalUnit1 = new OrganisationalUnit();
        organisationalUnit1.setName("Organisation1");
        organisationalUnit1.setCode("abc");
        organisationalUnitRepository.save(organisationalUnit1);

        OrganisationalUnit organisationalUnit2 = new OrganisationalUnit();
        organisationalUnit2.setName("Organisation2");
        organisationalUnit2.setCode("bcd");
        organisationalUnitRepository.save(organisationalUnit2);

        OrganisationalUnit organisationalUnit3 = new OrganisationalUnit();
        organisationalUnit3.setName("Organisation3");
        organisationalUnit3.setCode("cde");
        organisationalUnitRepository.save(organisationalUnit3);

        List<Long> listOrgIds = new ArrayList<>();
        List<OrganisationalUnit> orgUnits = organisationalUnitRepository.findAll();
        orgUnits.forEach(x -> listOrgIds.add(x.getId()));

        Identity identity1 = new Identity("1");
        identityRepository.save(identity1);
        CivilServant civilServant1 = new CivilServant(identity1);
        civilServant1.setOrganisationalUnit(organisationalUnit1);
        civilServantRepository.save(civilServant1);

        Identity identity2 = new Identity("2");
        identityRepository.save(identity2);
        CivilServant civilServant2 = new CivilServant(identity2);
        civilServant2.setOrganisationalUnit(organisationalUnit2);
        civilServantRepository.save(civilServant2);

        Identity identity3 = new Identity("3");
        identityRepository.save(identity3);
        CivilServant civilServant3 = new CivilServant(identity3);
        civilServant3.setOrganisationalUnit(organisationalUnit3);
        civilServantRepository.save(civilServant3);

        List<CivilServantReportDto> result = civilServantRepository.findAllByReportingOrganisationId(listOrgIds);

        assertTrue(result.size() > 2);
    }
}
