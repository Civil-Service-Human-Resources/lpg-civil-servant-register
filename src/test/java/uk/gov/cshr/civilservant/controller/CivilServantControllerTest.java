package uk.gov.cshr.civilservant.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.hateoas.Resource;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.domain.Identity;
import uk.gov.cshr.civilservant.dto.OrgCodeDTO;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;
import uk.gov.cshr.civilservant.resource.CivilServantResource;
import uk.gov.cshr.civilservant.resource.factory.CivilServantResourceFactory;
import uk.gov.cshr.civilservant.service.LineManagerService;
import uk.gov.cshr.civilservant.service.identity.IdentityFromService;
import uk.gov.cshr.civilservant.service.identity.IdentityService;
import uk.gov.cshr.civilservant.utils.MockMVCFilterOverrider;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@WithMockUser(username = "user", authorities = "IDENTITY_DELETE")
public class CivilServantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LineManagerService lineManagerService;

    @MockBean
    private CivilServantRepository civilServantRepository;

    @MockBean
    private CivilServantResourceFactory civilServantResourceFactory;

    @Before
    public void overridePatternMappingFilterProxyFilter() throws IllegalAccessException {
        MockMVCFilterOverrider.overrideFilterOf(mockMvc, "PatternMappingFilterProxy" );
    }

    @Test
    public void shouldReturnNotFoundIfNoLineManager() throws Exception {
        when(civilServantRepository.findByPrincipal()).thenReturn(Optional.of(createCivilServant("uid")));
        mockMvc.perform(
                patch("/civilServants/manager?email=bogus@domain.com").with(csrf())
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnNotProcessableIfNoDetailForLoggedInUser() throws Exception {

        when(lineManagerService.checkLineManager("learner@domain.com")).thenReturn(new IdentityFromService());

        mockMvc.perform(
                patch("/civilServants/manager?email=learner@domain.com").with(csrf())
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
                patch("/civilServants/manager?email=learner@domain.com").with(csrf())
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnOkAndUpdateCivilServant() throws Exception {

        String lineManagerEmail = "manager@domain.com";

        IdentityFromService lineManagerIdentity = new IdentityFromService();
        lineManagerIdentity.setUid("mid");
        lineManagerIdentity.setUsername(lineManagerEmail);

        when(lineManagerService.checkLineManager("learner@domain.com")).thenReturn(lineManagerIdentity);

        CivilServant lineManager = createCivilServant("mid");
        CivilServant civilServant = createCivilServant("uid");

        CivilServantResource civilServantResource = new CivilServantResource();
        civilServantResource.setLineManagerEmailAddress(lineManagerEmail);

        civilServant.setId(1L);

        when(civilServantRepository.findByIdentity("mid")).thenReturn(Optional.of(lineManager));
        when(civilServantRepository.findByPrincipal()).thenReturn(Optional.of(civilServant));

        when(civilServantResourceFactory.create(civilServant)).thenReturn(new Resource<>(civilServantResource));

        mockMvc.perform(
                patch("/civilServants/manager?email=learner@domain.com").with(csrf())
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lineManagerEmailAddress").value("manager@domain.com"));

        verify(civilServantRepository).save(any());
        verify(lineManagerService).notifyLineManager(any(), any(), any());
    }

    @Test
    public void givenOrgCodeExists_shouldReturnOk() throws Exception {

        CivilServant civilServant = createCivilServant("uid");
        OrgCodeDTO dto = new OrgCodeDTO();
        dto.setCode("co");

        when(civilServantRepository.findByPrincipal()).thenReturn(Optional.of(civilServant));
        when(civilServantResourceFactory.getCivilServantOrganisationalUnitCode(civilServant)).thenReturn(Optional.of(dto));

        mockMvc.perform(
                get("/civilServants/orgcode")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("co"));

        verify(civilServantRepository).findByPrincipal();
        verify(civilServantResourceFactory).getCivilServantOrganisationalUnitCode(eq(civilServant));
    }

    @Test
    public void givenOrgExistsWithNoCode_shouldReturnOk() throws Exception {

        CivilServant civilServant = createCivilServant("uid");

        when(civilServantRepository.findByPrincipal()).thenReturn(Optional.of(civilServant));
        when(civilServantResourceFactory.getCivilServantOrganisationalUnitCode(civilServant)).thenReturn(Optional.empty());

        mockMvc.perform(
                get("/civilServants/orgcode")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(civilServantRepository).findByPrincipal();
        verify(civilServantResourceFactory).getCivilServantOrganisationalUnitCode(eq(civilServant));
    }

    @Test
    public void givenOrgDoesNotExistsWith_shouldReturnNotFound() throws Exception {

        CivilServant civilServant = createCivilServant("uid");

        when(civilServantRepository.findByPrincipal()).thenReturn(Optional.empty());

        mockMvc.perform(
                get("/civilServants/orgcode")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(civilServantRepository).findByPrincipal();
        verify(civilServantResourceFactory, never()).getCivilServantOrganisationalUnitCode(any(CivilServant.class));
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
