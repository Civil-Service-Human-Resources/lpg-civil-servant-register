package uk.gov.cshr.civilservant.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.domain.Identity;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;
import uk.gov.cshr.civilservant.service.LineManagerService;
import uk.gov.cshr.civilservant.service.identity.IdentityFromService;
import uk.gov.cshr.civilservant.service.identity.IdentityService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CivilServantControllerTest {

    private MockMvc mockMvc;

    private CivilServantController controller;

    @Mock
    private LineManagerService lineManagerService;

    @Mock
    private CivilServantRepository civilServantRepository;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private RepositoryEntityLinks repositoryEntityLinks;

    @Before
    public void setup() {
        initMocks(this);
        controller = new CivilServantController(civilServantRepository, repositoryEntityLinks, lineManagerService);
        mockMvc = standaloneSetup(controller).build();
    }

    @Test
    public void shouldReturnNotFoundIfNoLineManager() throws Exception {
        when(civilServantRepository.findByPrincipal()).thenReturn(Optional.of(createCivilServant("uid")));
        mockMvc.perform(
                MockMvcRequestBuilders.patch("/civilServants/manager?email=bogus@domain.com")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnNotProcessableIfNoDetailForLoggedInUser() throws Exception {

        when(lineManagerService.checkLineManager("learner@domain.com")).thenReturn(new IdentityFromService());

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/civilServants/manager?email=learner@domain.com")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(422));
    }

    @Test
    public void shouldReturnBadRequestIfSettingLineManagerToYourself() throws Exception {

        IdentityFromService lineManager = new IdentityFromService();
        lineManager.setUid("uid");

        when(lineManagerService.checkLineManager("learner@domain.com")).thenReturn(lineManager);

        CivilServant civilServant = createCivilServant("uid");

        when(civilServantRepository.findByIdentity("uid")).thenReturn(Optional.of(civilServant));
        when(civilServantRepository.findByPrincipal()).thenReturn(Optional.of(civilServant));

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/civilServants/manager?email=learner@domain.com")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnOkAndUpdateCivilServant() throws Exception {

        IdentityFromService lineManagerIdentity = new IdentityFromService();
        lineManagerIdentity.setUid("mid");
        lineManagerIdentity.setUsername("manager@domain.com");

        when(lineManagerService.checkLineManager("learner@domain.com")).thenReturn(lineManagerIdentity);

        CivilServant lineManager = createCivilServant("mid");
        CivilServant civilServant = createCivilServant("uid");

        ReflectionTestUtils.setField(civilServant, "id", 1L);

        when(civilServantRepository.findByIdentity("mid")).thenReturn(Optional.of(lineManager));
        when(civilServantRepository.findByPrincipal()).thenReturn(Optional.of(civilServant));
        when(identityService.getEmailAddress(lineManager)).thenReturn(lineManagerIdentity.getUsername());

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/civilServants/manager?email=learner@domain.com")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lineManagerEmailAddress").value("manager@domain.com"));

        verify(civilServantRepository).save(any());
        verify(lineManagerService).notifyLineManager(any(), any(), any());
    }

    private CivilServant createCivilServant(String uid) {
        Identity identity = new Identity(uid);
        return new CivilServant(identity);
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public IdentityService identityService() {
            return mock(IdentityService.class);
        }
    }
}
