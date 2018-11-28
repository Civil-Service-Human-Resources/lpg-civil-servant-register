package uk.gov.cshr.civilservant.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.LinkBuilder;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;

import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryEntityServiceTest {

    @Mock
    private RepositoryEntityLinks repositoryEntityLinks;

    @InjectMocks
    private RepositoryEntityService<OrganisationalUnit> repositoryEntityService;

    @Test
    public void shouldReturnUriStringFromOrg() throws URISyntaxException {
        OrganisationalUnit parentOrganisationalUnit1 = new OrganisationalUnit();
        parentOrganisationalUnit1.setName("ParentTest1");
        parentOrganisationalUnit1.setCode("PT1");
        parentOrganisationalUnit1.setAbbreviation("PT1");
        parentOrganisationalUnit1.setId(1L);

        LinkBuilder linkBuilder = Mockito.mock(LinkBuilder.class, RETURNS_DEEP_STUBS);

        when(repositoryEntityLinks.linkFor(OrganisationalUnit.class)).thenReturn(linkBuilder);
        when(linkBuilder.slash(1L).toUri()).thenReturn(new URI("PT1"));

        String uriFromOrganisationalUnit = repositoryEntityService.getUri(OrganisationalUnit.class, parentOrganisationalUnit1);
        assertThat(uriFromOrganisationalUnit, equalTo("PT1"));
    }

}