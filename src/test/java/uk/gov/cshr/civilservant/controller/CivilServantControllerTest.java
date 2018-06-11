package uk.gov.cshr.civilservant.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.domain.Identity;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;
import uk.gov.cshr.civilservant.service.LineManagerService;
import uk.gov.cshr.civilservant.service.identity.IdentityFromService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
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
    private RepositoryEntityLinks repositoryEntityLinks;

    @Before
    public void setup() {
        initMocks(this);
        controller = new CivilServantController(civilServantRepository, repositoryEntityLinks, lineManagerService);
        mockMvc = standaloneSetup(controller).build();
    }

    @Test
    public void shouldReturnNotFoundIfNoLineManager() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.patch("/civilServants/manager?email=bogus@domain.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnNotProcessableIfNoDetailForLoggedInUser() throws Exception {

        when(lineManagerService.checkLineManager("learner@domain.com")).thenReturn(new IdentityFromService());

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/civilServants/manager?email=learner@domain.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(422));
    }

    @Test
    public void shouldReturnBadRequestIfSettingLineManagerToYourself() throws Exception {

        IdentityFromService lineManager = new IdentityFromService();
        lineManager.setUid("uid");

        when(lineManagerService.checkLineManager("learner@domain.com")).thenReturn(lineManager);

        Identity identity = new Identity("uid");
        CivilServant civilServant = new CivilServant(identity);

        when(civilServantRepository.findByPrincipal()).thenReturn(Optional.of(civilServant));

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/civilServants/manager?email=learner@domain.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnOkAndUpdateCivilServant() throws Exception {

        IdentityFromService lineManager = new IdentityFromService();
        lineManager.setUid("mid");
        lineManager.setUsername("manager@domain.com");

        when(lineManagerService.checkLineManager("learner@domain.com")).thenReturn(lineManager);

        Identity identity = new Identity("uid");
        CivilServant civilServant = new CivilServant(identity);

        ReflectionTestUtils.setField(civilServant, "id", 1L);

        when(civilServantRepository.findByPrincipal()).thenReturn(Optional.of(civilServant));

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/civilServants/manager?email=learner@domain.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lineManagerUid").value("mid"))
                .andExpect(jsonPath("$.lineManagerEmail").value("manager@domain.com"));

        verify(civilServantRepository).save(any());
        verify(lineManagerService).notifyLineManager(any(), any(), any());
    }
}
