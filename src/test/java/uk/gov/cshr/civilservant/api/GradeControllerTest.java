package uk.gov.cshr.civilservant.api;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.cshr.civilservant.domain.Grade;
import uk.gov.cshr.civilservant.domain.Organisation;
import uk.gov.cshr.civilservant.repository.OrganisationRepository;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class GradeControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private GradeController controller;

    @Mock
    private OrganisationRepository organisationRepository;

    @Before
    public void setup() {
        initMocks(this);
        mockMvc = standaloneSetup(controller).build();
    }

    @Test
    public void shouldReturnEmptyListForUnknownOrganisation() throws Exception {

        when(organisationRepository.findByCode("unknown")).thenReturn(Optional.empty());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/grades?organisation=unknown")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[*]", hasSize(0)));
    }

    @Test
    public void shouldReturnGradesForOrganisation() throws Exception {

        Organisation organisation = new Organisation("code", "name");
        organisation.addGrade(new Grade("G7", "Grade 7"));

        when(organisationRepository.findByCode("org")).thenReturn(Optional.of(organisation));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/grades?organisation=org")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[*]", hasSize(1)))
                .andExpect(jsonPath("$.results[0].code", equalTo("G7")))
                .andExpect(jsonPath("$.results[0].name", equalTo("Grade 7")));
    }
}
