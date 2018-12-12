package uk.gov.cshr.civilservant.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.hateoas.Resource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.domain.Identity;
import uk.gov.cshr.civilservant.resource.CivilServantResource;
import uk.gov.cshr.civilservant.service.ReportService;

import java.security.Principal;
import java.util.Arrays;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@WithMockUser(username = "user")
public class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    @Test
    @WithMockUser(username = "user", authorities = {"ORGANISATION_REPORTER"})
    public void shouldGetCivilServantsByUserOrganisationWithCorrectRole() throws Exception {
        CivilServant civilServant1 = new CivilServant(new Identity("1"));
        civilServant1.setFullName("User 1");
        CivilServant civilServant2 = new CivilServant(new Identity("2"));
        civilServant2.setFullName("User 2");

        when(reportService.listCivilServantsByUserOrganisation("user")).thenReturn(
                Arrays.asList(new Resource<>(new CivilServantResource(civilServant1)),
                        new Resource<>(new CivilServantResource(civilServant2))));

        mockMvc.perform(
                get("/report/civilServants").with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName", equalTo("User 1")))
                .andExpect(jsonPath("$[1].fullName", equalTo("User 2")));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"PROFESSION_REPORTER"})
    public void shouldGetCivilServantsByUserProfessionWithCorrectRole() throws Exception {
        CivilServant civilServant1 = new CivilServant(new Identity("1"));
        civilServant1.setFullName("User 1");
        CivilServant civilServant2 = new CivilServant(new Identity("2"));
        civilServant2.setFullName("User 2");

        when(reportService.listCivilServantsByUserProfession("user")).thenReturn(
                Arrays.asList(new Resource<>(new CivilServantResource(civilServant1)),
                        new Resource<>(new CivilServantResource(civilServant2))));

        mockMvc.perform(
                get("/report/civilServants").with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName", equalTo("User 1")))
                .andExpect(jsonPath("$[1].fullName", equalTo("User 2")));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"CSHR_REPORTER"})
    public void shouldGetAllCivilServants() throws Exception {
        CivilServant civilServant1 = new CivilServant(new Identity("1"));
        civilServant1.setFullName("User 1");
        CivilServant civilServant2 = new CivilServant(new Identity("2"));
        civilServant2.setFullName("User 2");

        when(reportService.listCivilServants()).thenReturn(
                Arrays.asList(new Resource<>(new CivilServantResource(civilServant1)),
                        new Resource<>(new CivilServantResource(civilServant2))));

        mockMvc.perform(
                get("/report/civilServants").with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName", equalTo("User 1")))
                .andExpect(jsonPath("$[1].fullName", equalTo("User 2")));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"INVALID_ROLE"})
    public void shouldReturn404WithIncorrectRole() throws Exception {
        CivilServant civilServant1 = new CivilServant(new Identity("1"));
        civilServant1.setFullName("User 1");
        CivilServant civilServant2 = new CivilServant(new Identity("2"));
        civilServant2.setFullName("User 2");

        when(reportService.listCivilServantsByUserOrganisation("user")).thenReturn(
                Arrays.asList(new Resource<>(new CivilServantResource(civilServant1)),
                        new Resource<>(new CivilServantResource(civilServant2))));

        mockMvc.perform(
                get("/report/civilServants").with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}