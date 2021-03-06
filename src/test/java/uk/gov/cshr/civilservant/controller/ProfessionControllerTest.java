package uk.gov.cshr.civilservant.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.GsonBuilder;
import org.junit.Before;
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
import uk.gov.cshr.civilservant.domain.Profession;
import uk.gov.cshr.civilservant.service.ProfessionService;
import uk.gov.cshr.civilservant.utils.MockMVCFilterOverrider;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@WithMockUser(username = "user")
public class ProfessionControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private ProfessionService professionService;

  @Before
  public void overridePatternMappingFilterProxyFilter() throws IllegalAccessException {
    MockMVCFilterOverrider.overrideFilterOf(mockMvc, "PatternMappingFilterProxy");
  }

  @Test
  public void shouldReturnProfessionsAsTreeStructure() throws Exception {
    Profession parent1 = new Profession("Parent One");
    Profession child1 = new Profession("Child One");
    Profession child2 = new Profession("Child Two");
    parent1.setChildren(Arrays.asList(child1, child2));
    Profession parent2 = new Profession("Parent Two");

    when(professionService.getParents()).thenReturn(Arrays.asList(parent1, parent2));

    mockMvc
        .perform(get("/professions/tree").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name", equalTo("Parent One")))
        .andExpect(jsonPath("$[0].children[0].name", equalTo("Child One")))
        .andExpect(jsonPath("$[0].children[1].name", equalTo("Child Two")))
        .andExpect(jsonPath("$[1].name", equalTo("Parent Two")));
  }

  @Test
  public void shouldNotSaveProfessionIfNotProfessionManager() throws Exception {
    Map<String, String> profession = ImmutableMap.of("name", "new profession");

    String json = new GsonBuilder().create().toJson(profession);

    mockMvc
        .perform(
            post("/professions/").with(csrf()).content(json).accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(username = "user", authorities = "PROFESSION_MANAGER")
  public void shouldSaveProfessionProfessionManager() throws Exception {
    Map<String, String> profession = ImmutableMap.of("name", "new profession");

    String json = new GsonBuilder().create().toJson(profession);

    mockMvc
        .perform(
            post("/professions/").with(csrf()).content(json).accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isCreated());
  }
}
