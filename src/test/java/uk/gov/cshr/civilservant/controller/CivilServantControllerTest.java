package uk.gov.cshr.civilservant.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.hateoas.Resource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.domain.Identity;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.dto.OrgCodeDTO;
import uk.gov.cshr.civilservant.dto.UpdateOrganisationDTO;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;
import uk.gov.cshr.civilservant.repository.OrganisationalUnitRepository;
import uk.gov.cshr.civilservant.resource.CivilServantResource;
import uk.gov.cshr.civilservant.resource.factory.CivilServantResourceFactory;
import uk.gov.cshr.civilservant.service.LineManagerService;
import uk.gov.cshr.civilservant.service.identity.IdentityFromService;
import uk.gov.cshr.civilservant.service.identity.IdentityService;
import uk.gov.cshr.civilservant.utils.JsonUtils;
import uk.gov.cshr.civilservant.utils.MockMVCFilterOverrider;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @MockBean
    private OrganisationalUnitRepository organisationalUnitRepository;

    @Captor
    private ArgumentCaptor<CivilServant> civilServantOrgToBeDeletedCaptor;

    @Before
    public void overridePatternMappingFilterProxyFilter() throws IllegalAccessException {
        MockMVCFilterOverrider.overrideFilterOf(mockMvc, "PatternMappingFilterProxy" );
    }

    @Test
    public void shouldReturnNotFoundIfNoLineManager() throws Exception {
        when(civilServantRepository.findByPrincipal()).thenReturn(Optional.of(createCivilServant("myuid")));
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
        lineManager.setUid("myuid");

        when(lineManagerService.checkLineManager("learner@domain.com")).thenReturn(lineManager);

        CivilServant civilServant = createCivilServant("myuid");

        when(civilServantRepository.findByIdentity("myuid")).thenReturn(Optional.of(civilServant));
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
        CivilServant civilServant = createCivilServant("myuid");

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
        civilServantResource.setLineManagerEmailAddress(lineManagerEmail);

        verify(civilServantRepository).save(any());
        verify(lineManagerService).notifyLineManager(any(), any(), any());
    }

    @Test
    public void givenOrgCodeExists_shouldReturnOk() throws Exception {

        CivilServant civilServant = createCivilServant("myuid");
        OrgCodeDTO dto = new OrgCodeDTO();
        dto.setCode("co");

        when(civilServantRepository.findByIdentity(eq("myuid"))).thenReturn(Optional.of(civilServant));
        when(civilServantResourceFactory.getCivilServantOrganisationalUnitCode(civilServant)).thenReturn(Optional.of(dto));

        mockMvc.perform(
                get("/civilServants/org")
                        .param("uid", "myuid")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("co"));
    }

    @Test
    public void givenCivilServantDoesNotExist_shouldReturnNotFound() throws Exception {

        when(civilServantRepository.findByIdentity(eq("myuid"))).thenReturn(Optional.empty());

        mockMvc.perform(
                get("/civilServants/org")
                        .param("uid", "myuid")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(civilServantRepository).findByIdentity(anyString());
        verify(civilServantResourceFactory, never()).getCivilServantOrganisationalUnitCode(any(CivilServant.class));
    }

    @Test
    public void givenOrgDoesNotExists_shouldReturnNotFound() throws Exception {

        CivilServant civilServant = createCivilServant("myuid");

        when(civilServantRepository.findByIdentity(eq("myuid"))).thenReturn(Optional.of(civilServant));
        when(civilServantResourceFactory.getCivilServantOrganisationalUnitCode(any(CivilServant.class))).thenReturn(Optional.empty());

        mockMvc.perform(
                get("/civilServants/org")
                        .param("uid", "myuid")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("null"));
    }

    @Test
    public void givenCivilServantExistsAndOrgUnitExists_whenUpdateOrganisation_shouldReturn204() throws Exception {

        UpdateOrganisationDTO requestDTO = new UpdateOrganisationDTO();
        requestDTO.setOrganisation("co");

        CivilServant civilServant = createCivilServant("myuid");
        OrganisationalUnit dto = new OrganisationalUnit();
        dto.setCode("co");

        when(civilServantRepository.findByPrincipal()).thenReturn(Optional.of(civilServant));
        when(organisationalUnitRepository.findByCode(eq("co"))).thenReturn(Optional.of(dto));
        when(civilServantRepository.save(eq(civilServant))).thenReturn(new CivilServant());

        mockMvc.perform(
                patch("/civilServants/org").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(JsonUtils.asJsonString(requestDTO))
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(civilServantRepository).findByPrincipal();
        verify(organisationalUnitRepository).findByCode(eq("co"));
        verify(civilServantRepository).save(eq(civilServant));
    }

    @Test
    public void givenCivilServantDoesNotExist_whenUpdateOrganisation_shouldReturn404() throws Exception {

        UpdateOrganisationDTO requestDTO = new UpdateOrganisationDTO();
        requestDTO.setOrganisation("co");

        when(civilServantRepository.findByPrincipal()).thenReturn(Optional.empty());

        mockMvc.perform(
                patch("/civilServants/org").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(JsonUtils.asJsonString(requestDTO))
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(civilServantRepository).findByPrincipal();
        verify(organisationalUnitRepository, never()).findByCode(anyString());
        verify(civilServantRepository, never()).save(any());
    }

    @Test
    public void givenOrgNotExist_whenUpdateOrganisation_shouldReturn404() throws Exception {

        UpdateOrganisationDTO requestDTO = new UpdateOrganisationDTO();
        requestDTO.setOrganisation("co");

        CivilServant civilServant = createCivilServant("myuid");

        when(civilServantRepository.findByPrincipal()).thenReturn(Optional.of(civilServant));
        when(organisationalUnitRepository.findByCode(eq("co"))).thenReturn(Optional.empty());

        mockMvc.perform(
                patch("/civilServants/org").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(JsonUtils.asJsonString(requestDTO))
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(civilServantRepository).findByPrincipal();
        verify(organisationalUnitRepository).findByCode(anyString());
        verify(civilServantRepository, never()).save(any());
    }

    @Test
    public void givenTechnicalErrorOccurs_whenUpdateOrganisation_shouldReturn500() throws Exception {

        UpdateOrganisationDTO requestDTO = new UpdateOrganisationDTO();
        requestDTO.setOrganisation("co");

        CivilServant civilServant = createCivilServant("myuid");
        OrganisationalUnit dto = new OrganisationalUnit();
        dto.setCode("co");

        when(civilServantRepository.findByPrincipal()).thenReturn(Optional.of(civilServant));
        when(organisationalUnitRepository.findByCode(eq("co"))).thenReturn(Optional.of(dto));
        when(civilServantRepository.save(any(CivilServant.class))).thenThrow(new RuntimeException());

        mockMvc.perform(
                patch("/civilServants/org").contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(JsonUtils.asJsonString(requestDTO))
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError());

        verify(civilServantRepository).findByPrincipal();
        verify(organisationalUnitRepository).findByCode(anyString());
        verify(civilServantRepository).save(any());
    }

    @Test
    public void givenCivilServantExists_whenDeleteOrganisation_shouldReturn204() throws Exception {

        CivilServant civilServant = createCivilServant("myuid");
        OrganisationalUnit existingOrg = new OrganisationalUnit();
        existingOrg.setCode("co");
        civilServant.setOrganisationalUnit(existingOrg);
        long expectedIDOfCivilServant = civilServant.getId();

        when(civilServantRepository.findByPrincipal()).thenReturn(Optional.of(civilServant));
        when(civilServantRepository.save(eq(civilServant))).thenReturn(new CivilServant());

        mockMvc.perform(
                delete("/civilServants/org")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(civilServantRepository).findByPrincipal();
        verify(civilServantRepository).save(civilServantOrgToBeDeletedCaptor.capture());

        CivilServant actualCivilServantWhosOrgHasBeenDeleted = civilServantOrgToBeDeletedCaptor.getValue();
        assertThat(actualCivilServantWhosOrgHasBeenDeleted.getId()).isEqualTo(expectedIDOfCivilServant);

        Optional<OrganisationalUnit> afterDeletionOrgUnit = actualCivilServantWhosOrgHasBeenDeleted.getOrganisationalUnit();
        assertThat(actualCivilServantWhosOrgHasBeenDeleted.getOrganisationalUnit()).isEmpty();
    }

    @Test
    public void givenCivilServantDoesNotExists_whenDeleteOrganisation_shouldReturn404() throws Exception {

        when(civilServantRepository.findByPrincipal()).thenReturn(Optional.empty());

        mockMvc.perform(
                delete("/civilServants/org")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(civilServantRepository).findByPrincipal();
        verify(civilServantRepository, never()).save(any(CivilServant.class));
    }

    @Test
    public void givenTechnicalErrorOccurs_whenDeleteOrganisation_shouldReturn500() throws Exception {

        CivilServant civilServant = createCivilServant("myuid");
        OrganisationalUnit existingOrg = new OrganisationalUnit();
        existingOrg.setCode("co");
        civilServant.setOrganisationalUnit(existingOrg);
        long expectedIDOfCivilServant = civilServant.getId();

        when(civilServantRepository.findByPrincipal()).thenReturn(Optional.of(civilServant));
        when(civilServantRepository.save(eq(civilServant))).thenThrow(new RuntimeException());

        mockMvc.perform(
                delete("/civilServants/org")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError());

        verify(civilServantRepository).findByPrincipal();
        verify(civilServantRepository).save(eq(civilServant));
    }

    @Test
    public void shouldReturnOkWhenRequestCivilServantByUid() throws Exception {
        String uid = "uid";
        String lineManagerEmail = "manager@domain.com";

        CivilServant civilServant = createCivilServant(uid);

        CivilServantResource civilServantResource = new CivilServantResource();
        civilServantResource.setLineManagerEmailAddress(lineManagerEmail);

        civilServant.setId(1L);

        when(civilServantRepository.findByIdentity(uid)).thenReturn(Optional.of(civilServant));
        when(civilServantResourceFactory.create(civilServant)).thenReturn(new Resource<>(civilServantResource));


        mockMvc.perform(
                get("/civilServants/" + uid).with(csrf())
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lineManagerEmailAddress").value("manager@domain.com"));
    }

    private CivilServant createCivilServant(String uid) {
        Identity identity = new Identity(uid);
        CivilServant cs = new CivilServant(identity);
        cs.setId(new Long(123));
        return cs;
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
