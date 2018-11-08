package uk.gov.cshr.civilservant.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.repository.OrganisationalUnitRepository;
import uk.gov.cshr.civilservant.service.OrganisationalUnitService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrganisationalUnitControllerTest {

    private MockMvc mockMvc;

    private OrganisationalUnitController organisationalUnitController;

    @Mock
    private OrganisationalUnitService organisationalUnitService;

    @Mock
    private OrganisationalUnitRepository organisationalUnitRepository;


    @Before
    public void setup() {
        initMocks(this);
        organisationalUnitController = new OrganisationalUnitController(organisationalUnitRepository, organisationalUnitService);
        mockMvc = standaloneSetup(organisationalUnitController).build();
    }

    @Test
    public void shouldReturnOkIfRequestingOrganisationalUnitTree() throws Exception {
        Iterable<OrganisationalUnit> organisationalUnits = new ArrayList<>();

        when(organisationalUnitRepository.findAll()).thenReturn(organisationalUnits);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/organisationalUnits/tree")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnOkIfRequestingOrganisationalUnitFlat() throws Exception {
        Iterable<OrganisationalUnit> organisationalUnits = new ArrayList<>();
        Map<String, String> organisationalUnitsMap = new LinkedHashMap<>();

        when(organisationalUnitRepository.findAll()).thenReturn(organisationalUnits);
        when(organisationalUnitService.getOrganisationalUnitsMap(organisationalUnits)).thenReturn(organisationalUnitsMap);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/organisationalUnits/flat")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
