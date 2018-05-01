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
import uk.gov.cshr.civilservant.repository.GradeRepository;

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
    private GradeRepository gradeRepository;

    @Before
    public void setup() {
        initMocks(this);
        mockMvc = standaloneSetup(controller).build();
    }

    @Test
    public void shouldReturnNotFoundForUnknownGrade() throws Exception {

        final Long id = 1L;

        when(gradeRepository.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/grades/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnResourceForGrade() throws Exception {

        final Long id = 1L;
        final String code = "code";
        final String name = "name";

        Grade grade = new Grade(code, name);

        when(gradeRepository.findById(id)).thenReturn(Optional.of(grade));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/grades/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo(code)))
                .andExpect(jsonPath("$.name", equalTo(name)));
    }
}
