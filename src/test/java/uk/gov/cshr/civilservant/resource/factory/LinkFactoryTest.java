package uk.gov.cshr.civilservant.resource.factory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkBuilder;
import uk.gov.cshr.civilservant.domain.CivilServant;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LinkFactoryTest {

    @Mock
    private RepositoryEntityLinks repositoryEntityLinks;

    @InjectMocks
    private LinkFactory linkFactory;

    @Test
    public void shouldReturnRelationshipLink() {
        long id = 99L;
        String relationship = "profession";

        CivilServant civilServant = new CivilServant();
        civilServant.setId(id);

        LinkBuilder linkBuilder = mock(LinkBuilder.class);
        Link link = mock(Link.class);

        when(repositoryEntityLinks.linkFor(CivilServant.class)).thenReturn(linkBuilder);
        when(linkBuilder.slash(id)).thenReturn(linkBuilder);
        when(linkBuilder.slash(relationship)).thenReturn(linkBuilder);
        when(linkBuilder.withRel(relationship)).thenReturn(link);

        Link result = linkFactory.createRelationshipLink(civilServant, relationship);
        assertEquals(link, result);

        verify(repositoryEntityLinks).linkFor(CivilServant.class);
        verify(linkBuilder).slash(id);
        verify(linkBuilder).slash(relationship);
        verify(linkBuilder).withRel(relationship);
    }

    @Test
    public void shouldReturnSelfLink() {
        long id = 99L;
        CivilServant civilServant = new CivilServant();
        civilServant.setId(id);

        Link link = mock(Link.class);
        Link withSelfLink = mock(Link.class);

        when(repositoryEntityLinks.linkToSingleResource(CivilServant.class, id)).thenReturn(link);
        when(link.withSelfRel()).thenReturn(withSelfLink);

        Link result = linkFactory.createSelfLink(civilServant);

        assertEquals(withSelfLink, result);
    }
}