package uk.gov.cshr.civilservant.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.domain.Identity;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;
import uk.gov.cshr.civilservant.service.identity.IdentityFromService;
import uk.gov.cshr.civilservant.service.identity.IdentityService;

import java.util.Optional;

import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class LineManagerServiceTest {

    @InjectMocks
    private LineManagerService lineManagerService;

    @Mock
    private NotifyService notifyService;

    @Mock
    private IdentityService identityService;

    @Mock
    private CivilServantRepository civilServantRepository;

    @Test
    public void shouldFindExistingIdentity() {
        when(identityService.findByEmail("learner@domain.com")).thenReturn(new IdentityFromService());
        assertNotNull(lineManagerService.checkLineManager("learner@domain.com"));
    }

    @Test
    public void shouldNotFindNonExistantIdentity() {
        assertNull(lineManagerService.checkLineManager("test@doesnotexist"));
    }

    @Test
    public void shouldNotifyLineManager() throws Exception {

        final Identity learnerIdentity = new Identity("uid");
        final CivilServant learner = new CivilServant(learnerIdentity);
        learner.setFullName("learner");

        final Identity managerIdentity = new Identity("mid");
        final CivilServant manager = new CivilServant(managerIdentity);
        manager.setFullName("fullName");

        final IdentityFromService lineManager = new IdentityFromService();
        lineManager.setUid(managerIdentity.getUid());
        lineManager.setUsername("manager@domain.com");

        when(civilServantRepository.findByIdentity("mid")).thenReturn(Optional.of(manager));

        lineManagerService.notifyLineManager(learner, lineManager, "manager@domain.com");

        verify(notifyService).notify("manager@domain.com", null, "fullName", "learner");
    }

//    @Test
//    public void shouldNotAllowToBeOwnManager() {
//
//        final Identity identity = new Identity("uid");
//        final CivilServant civilServant = new CivilServant(identity);
//
//        final IdentityFromService lineManager = new IdentityFromService();
//        lineManager.setUid("uid");
//
//
//
//        assertThat(lineManagerService.notifyLineManager(civilServant, lineManager, "test@test.com"), nullValue());
//    }
//
//    @Test
//    public void shouldAllowUpdatingOfLineManager() {
//        final Identity identity = new Identity("test@test.com");
//        CivilServant civilServant = new CivilServant(identity);
//
//        final IdentityFromService lineManager = lineManagerService.checkLineManager("learner@domain.com");
//
//        Boolean triedToUpdate = false;
//        try {
//            civilServant = lineManagerService.notifyLineManager(civilServant, lineManager, "manager@test.com");
//        } catch (AuthenticationCredentialsNotFoundException authException) {
//            triedToUpdate = true; // this will be triggered if identity is told to update civilservant linemanager
//        }
//        assertTrue(triedToUpdate);
//    }

}
