package uk.gov.cshr.civilservant.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.validation.Errors;
import uk.gov.cshr.civilservant.domain.AgencyToken;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.dto.AgencyTokenDTO;
import uk.gov.cshr.civilservant.dto.OrganisationalUnitDto;
import uk.gov.cshr.civilservant.service.OrganisationalUnitService;
import uk.gov.cshr.civilservant.utils.AgencyTokenTestingUtils;
import uk.gov.cshr.civilservant.utils.JsonUtils;
import uk.gov.cshr.civilservant.validation.AgencyTokenDTOValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@WithMockUser(username = "user")
public class OrganisationalUnitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean/*(name ="organisationalUnitService")*/
    private OrganisationalUnitService organisationalUnitService;

   //@MockBean
   // @SpyBean
    //private AgencyTokenDTOValidator agencyTokenDTOValidator;

    private String requestBodyAgencyTokenAsAString;

    private AgencyToken at;

    @Before
    public void setUp(){
        at = AgencyTokenTestingUtils.createAgencyToken();
        //dto = AgencyTokenTestingUtils.createAgencyTokenDTO();
    }

    @Test
    public void shouldReturnOkIfRequestingOrganisationalUnitTree() throws Exception {
        ArrayList<OrganisationalUnit> organisationalUnits = new ArrayList<>();

        when(organisationalUnitService.getParents()).thenReturn(organisationalUnits);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/organisationalUnits/tree")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnOkIfRequestingOrganisationalUnitFlat() throws Exception {
        List<OrganisationalUnitDto> organisationalUnitsList = new ArrayList<>();

        when(organisationalUnitService.getListSortedByValue()).thenReturn(organisationalUnitsList);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/organisationalUnits/flat")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void shouldSaveAgencyTokenIfValidAgencyTokenDTOIsProvided() throws Exception {
        OrganisationalUnit orgUnit = new OrganisationalUnit();
        orgUnit.setAbbreviation("NHSDUNDEE");
        orgUnit.setCode("NHSDUN");
        Optional<OrganisationalUnit> orgUnitOptional = Optional.of(orgUnit);

        when(organisationalUnitService.getOrganisationalUnit(anyLong())).thenReturn(orgUnitOptional);
        when(organisationalUnitService.setAgencyToken(eq(orgUnit), any(AgencyToken.class))).thenReturn(orgUnit);

        requestBodyAgencyTokenAsAString = JsonUtils.asJsonString(at);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/organisationalUnits/123/agencyToken").contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON).content(requestBodyAgencyTokenAsAString))
                .andDo(print())
                .andExpect(status().isCreated());

        //verify(agencyTokenDTOValidator, times(1)).validate(any(), any(Errors.class));

        //assertThat()
    }

    @Test
    public void shouldNotSaveAgencyTokenIfInvalidAgencyTokenDTOIsProvided() throws Exception {
       /* OrganisationalUnit orgUnit = new OrganisationalUnit();
        orgUnit.setAbbreviation("NHSDUNDEE");
        orgUnit.setCode("NHSDUN");

        when(organisationalUnitService.getOrganisationalUnit(anyLong())).thenReturn(Optional.of(orgUnit));
        when(organisationalUnitService.setAgencyToken(eq(orgUnit), any(AgencyToken.class))).thenReturn(orgUnit);*/

        at.setToken("");
        requestBodyAgencyTokenAsAString = JsonUtils.asJsonString(at);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/organisationalUnits/123/agencyToken").contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON).content(requestBodyAgencyTokenAsAString))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(organisationalUnitService, never()).getOrganisationalUnit(anyLong());

        //verify(agencyTokenDTOValidator, times(1)).validate(any(), any(Errors.class));

        //assertThat()
    }
}
