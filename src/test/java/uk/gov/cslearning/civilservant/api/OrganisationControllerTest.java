package uk.gov.cslearning.civilservant.api;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.cslearning.civilservant.domain.Department;
import uk.gov.cslearning.civilservant.domain.Grade;
import uk.gov.cslearning.civilservant.domain.Organisation;
import uk.gov.cslearning.civilservant.repository.DepartmentRepository;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
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
    private DepartmentRepository departmentRepository;

    @Before
    public void setup() {
        initMocks(this);
        mockMvc = standaloneSetup(controller).build();
    }

    @Test
    public void shouldReturnEmptyListForUnknownOrganisation() throws Exception {

        when(departmentRepository.findByCode("unknown")).thenReturn(Optional.empty());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/organisations?department=unknown")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[*]", hasSize(0)));
    }

    @Test
    public void shouldReturnGradesForOrganisation() throws Exception {

        Department department = new Department("code", "name");
        department.addOrganisation(new Organisation("org", "Org"));

        when(departmentRepository.findByCode("code")).thenReturn(Optional.of(department));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/organisations?department=code")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[*]", hasSize(1)))
                .andExpect(jsonPath("$.results[0].code", equalTo("org")))
                .andExpect(jsonPath("$.results[0].name", equalTo("Org")));
    }
}
