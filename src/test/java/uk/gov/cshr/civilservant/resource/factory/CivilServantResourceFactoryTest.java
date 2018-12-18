package uk.gov.cshr.civilservant.resource.factory;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import uk.gov.cshr.civilservant.domain.*;
import uk.gov.cshr.civilservant.resource.CivilServantResource;
import uk.gov.cshr.civilservant.service.identity.IdentityService;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CivilServantResourceFactoryTest {
    @Mock
    private IdentityService identityService;

    @Mock
    private LinkFactory linkFactory;

    @InjectMocks
    private CivilServantResourceFactory factory;

    @Test
    public void shouldReturnCivilServantResource() {
        long id = 99L;
        String fullName = "full-name";
        Grade grade = new Grade("code", "name");
        Set<Interest> interests = ImmutableSet.of(new Interest("interest"));
        String lineManagerName = "line-manager";
        String lineManagerEmail = "line-manager@domain.com";
        CivilServant lineManager = new CivilServant();
        lineManager.setFullName(lineManagerName);
        Profession profession = new Profession("profession");
        OrganisationalUnit organisationalUnit = new OrganisationalUnit();

        CivilServant civilServant = new CivilServant();
        civilServant.setId(id);
        civilServant.setFullName(fullName);
        civilServant.setGrade(grade);
        civilServant.setInterests(interests);
        civilServant.setLineManager(lineManager);
        civilServant.setProfession(profession);
        civilServant.setOrganisationalUnit(organisationalUnit);

        when(identityService.getEmailAddress(lineManager)).thenReturn(lineManagerEmail);

        Link selfLink = mock(Link.class);
        when(linkFactory.createSelfLink(civilServant)).thenReturn(selfLink);

        Link organisationLink = mock(Link.class);
        when(linkFactory.createRelationshipLink(civilServant, "organisationalUnit"))
                .thenReturn(organisationLink);

        Link gradeLink = mock(Link.class);
        when(linkFactory.createRelationshipLink(civilServant, "grade"))
                .thenReturn(gradeLink);

        Link professionLink = mock(Link.class);
        when(linkFactory.createRelationshipLink(civilServant, "profession"))
                .thenReturn(professionLink);

        Resource<CivilServantResource> resource = factory.create(civilServant);

        CivilServantResource content = resource.getContent();

        assertTrue(resource.getLinks().contains(selfLink));
        assertTrue(resource.getLinks().contains(organisationLink));
        assertTrue(resource.getLinks().contains(gradeLink));
        assertTrue(resource.getLinks().contains(professionLink));

        assertEquals(fullName, content.getFullName());
        assertEquals(grade, content.getGrade());
        assertEquals(interests, content.getInterests());
        assertEquals(lineManagerName, content.getLineManagerName());
        assertEquals(lineManagerEmail, content.getLineManagerEmailAddress());
        assertEquals(organisationalUnit, content.getOrganisationalUnit());
        assertEquals(profession, content.getProfession());
    }
}