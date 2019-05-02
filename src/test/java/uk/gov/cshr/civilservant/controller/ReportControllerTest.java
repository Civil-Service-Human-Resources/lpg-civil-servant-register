package uk.gov.cshr.civilservant.controller;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.cshr.civilservant.dto.CivilServantDto;
import uk.gov.cshr.civilservant.service.ReportService;

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
        CivilServantDto civilServant1 = new CivilServantDto();
        civilServant1.setName("User 1");
        CivilServantDto civilServant2 = new CivilServantDto();
        civilServant2.setName("User 2");

        when(reportService.getCivilServantMapByUserOrganisationNormalised("user")).thenReturn(
                ImmutableMap.of("1", civilServant1, "2", civilServant2));

        mockMvc.perform(
                get("/report/civilServants").with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.1.name", equalTo("User 1")))
                .andExpect(jsonPath("$.2.name", equalTo("User 2")));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"PROFESSION_REPORTER"})
    public void shouldGetCivilServantsByUserProfessionWithCorrectRole() throws Exception {
        CivilServantDto civilServant1 = new CivilServantDto();
        civilServant1.setName("User 1");
        CivilServantDto civilServant2 = new CivilServantDto();
        civilServant2.setName("User 2");

        when(reportService.getCivilServantMapByUserProfessionNormalised("user")).thenReturn(
                ImmutableMap.of("1", civilServant1,"2", civilServant2));

        mockMvc.perform(
                get("/report/civilServants").with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.1.name", equalTo("User 1")))
                .andExpect(jsonPath("$.2.name", equalTo("User 2")));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"CSHR_REPORTER"})
    public void shouldGetAllCivilServants() throws Exception {
        CivilServantDto civilServant1 = new CivilServantDto();
        civilServant1.setName("User 1");

        CivilServantDto civilServant2 = new CivilServantDto();
        civilServant2.setName("User 2");

        when(reportService.getCivilServantMapNormalised()).thenReturn(
                ImmutableMap.of("1", civilServant1,
                        "2", civilServant2));

        mockMvc.perform(
                get("/report/civilServants").with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.1.name", equalTo("User 1")))
                .andExpect(jsonPath("$.2.name", equalTo("User 2")));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"INVALID_ROLE"})
    public void shouldReturn404WithIncorrectRole() throws Exception {
        mockMvc.perform(
                get("/report/civilServants").with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}