package uk.gov.cshr.civilservant.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.domain.Identity;
import uk.gov.cshr.civilservant.service.identity.IdentityFromService;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class LineManagerServiceTest {

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private LineManagerService lineManagerService;


    @Test
    public void shouldFindExistingIdentity() {
        assertNotNull(lineManagerService.checkLineManager("learner@domain.com"));
    }

    @Test
    public void shouldNotFindNonExistantIdentity() {
        assertNull(lineManagerService.checkLineManager("test@doesnotexist"));
    }

    @Test
    public void shouldNotAllowToBeOwnManager() {
        final Identity identity = new Identity("3c706a70-3fff-4e7b-ae7f-102c1d46f569");
        final CivilServant civilServant = new CivilServant(identity);

        final IdentityFromService lineManager = lineManagerService.checkLineManager("learner@domain.com");
        assertNull(lineManagerService.UpdateAndNotifyLineManager(civilServant, lineManager, "test@test.com"));
    }

    @Test
    public void shouldAllowUpdatingOfLineManager() {
        final Identity identity = new Identity("test@test.com");
        CivilServant civilServant = new CivilServant(identity);

        final IdentityFromService lineManager = lineManagerService.checkLineManager("learner@domain.com");

        Boolean triedToUpdate = false;
        try {
            civilServant = lineManagerService.UpdateAndNotifyLineManager(civilServant, lineManager, "manager@test.com");
        } catch (AuthenticationCredentialsNotFoundException authException) {
            triedToUpdate = true; // this will be triggered if identity is told to update civilservant linemanager
        }
        assertTrue(triedToUpdate);
    }

}
