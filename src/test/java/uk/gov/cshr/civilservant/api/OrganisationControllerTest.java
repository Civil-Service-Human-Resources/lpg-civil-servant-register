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

import java.util.ArrayList;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class OrganisationControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private OrganisationController controller;

    @Mock
    private OrganisationRepository organisationRepository;

    @Before
    public void setup() {
        initMocks(this);
        mockMvc = standaloneSetup(controller).build();
    }

    @Test
    public void shouldReturnEmptyListForQueryMatchingNoOrganisations() throws Exception {

        final String query = "query";

        when(organisationRepository.findByNameStartsWithIgnoringCase(query)).thenReturn(new ArrayList<>());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/organisations?query={query}", query)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[*]", hasSize(0)));
    }

    @Test
    public void shouldReturnResourceSummaryForQueryMatchingOrganisations() throws Exception {

        final String query = "query";
        final String code = "code";
        final String name = "name";

        Organisation organisation = new Organisation(code, name);
        organisation.addGrade(new Grade("gc", "gn"));

        when(organisationRepository.findByNameStartsWithIgnoringCase(query)).thenReturn(newArrayList(organisation));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/organisations?query={query}", query)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[*]", hasSize(1)))
                .andExpect(jsonPath("$.results[0].code", equalTo(code)))
                .andExpect(jsonPath("$.results[0].name", equalTo(name)))
                .andExpect(jsonPath("$.results[0].grades", nullValue()));
    }

    @Test
    public void shouldReturnNotFoundForUnknownOrganisation() throws Exception {

        final Long id = 1L;

        when(organisationRepository.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/organisations/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnCompleteResourceForOrganisation() throws Exception {

        final Long id = 1L;
        final String code = "code";
        final String name = "name";
        final String gradeCode = "gradeCode";
        final String gradeName = "gradeName";

        Organisation organisation = new Organisation(code, name);
        organisation.addGrade(new Grade(gradeCode, gradeName));

        when(organisationRepository.findById(id)).thenReturn(Optional.of(organisation));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/organisations/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(code)))
                .andExpect(jsonPath("$.name", equalTo(name)))
                .andExpect(jsonPath("$.grades", hasSize(1)))
                .andExpect(jsonPath("$.grades[0].code", equalTo(gradeCode)))
                .andExpect(jsonPath("$.grades[0].name", equalTo(gradeName)));
    }
}
