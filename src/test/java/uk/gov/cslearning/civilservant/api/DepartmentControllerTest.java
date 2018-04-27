package uk.gov.cslearning.civilservant.api;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.cslearning.civilservant.domain.Department;
import uk.gov.cslearning.civilservant.repository.DepartmentRepository;

import java.util.ArrayList;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class DepartmentControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private DepartmentController controller;

    @Mock
    private DepartmentRepository departmentRepository;

    @Before
    public void setup() {
        initMocks(this);
        mockMvc = standaloneSetup(controller).build();
    }

    @Test
    public void shouldReturnAllDepartments() throws Exception {

        Department department = new Department("code", "name");

        Iterable<Department> departments = new ArrayList<Department>() {{
            add(department);
        }};

        when(departmentRepository.findAll()).thenReturn(departments);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/departments")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results[*]", hasSize(1)))
                .andExpect(jsonPath("$.results[0].code", equalTo("code")))
                .andExpect(jsonPath("$.results[0].name", equalTo("name")));
    }
}
