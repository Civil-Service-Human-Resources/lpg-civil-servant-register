package uk.gov.cshr.civilservant.controller;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.cshr.civilservant.domain.AgencyToken;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.dto.AgencyTokenDto;
import uk.gov.cshr.civilservant.dto.AgencyTokenResponseDto;
import uk.gov.cshr.civilservant.dto.OrganisationalUnitDto;
import uk.gov.cshr.civilservant.exception.CSRSApplicationException;
import uk.gov.cshr.civilservant.exception.TokenDoesNotExistException;
import uk.gov.cshr.civilservant.service.OrganisationalUnitService;
import uk.gov.cshr.civilservant.utils.AgencyTokenTestingUtils;
import uk.gov.cshr.civilservant.utils.JsonUtils;
import uk.gov.cshr.civilservant.utils.MockMVCFilterOverrider;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@WithMockUser(username = "user")
public class OrganisationalUnitControllerTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrganisationalUnitService organisationalUnitService;

    private String requestBodyAgencyTokenAsAString;

    private AgencyTokenDto dto;

    @Before
    public void overridePatternMappingFilterProxyFilter() throws IllegalAccessException {
        MockMVCFilterOverrider.overrideFilterOf(mockMvc, "PatternMappingFilterProxy" );
        dto = AgencyTokenTestingUtils.createAgencyTokenDTO();
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
    public void shouldReturnOkIfRequestingGetToken() throws Exception {
        AgencyTokenResponseDto responseDto = AgencyTokenTestingUtils.getAgencyTokenResponseDto();
        String expectedDomainName =  responseDto.getAgencyDomains().stream().findFirst().get().getDomain();
        when(organisationalUnitService.getAgencyToken(anyLong())).thenReturn(responseDto);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/organisationalUnits/123/agencyToken")
                        .accept(APPLICATION_JSON))
                .andExpect(jsonPath("$.token", equalTo(responseDto.getToken())))
                .andExpect(jsonPath("$.capacity", equalTo(responseDto.getCapacity())))
                .andExpect(jsonPath("$.capacityUsed", equalTo(responseDto.getCapacityUsed())))
                .andExpect(jsonPath("$.agencyDomains", hasSize(1)))
                .andExpect(jsonPath("$.agencyDomains[0].domain", equalTo(expectedDomainName)))
                .andExpect(status().isOk());

        verify(organisationalUnitService, times(1)).getAgencyToken(eq(123l));
    }

    @Test
    public void shouldThrowTokenDoesNotExistIfNoTokenFound() throws Exception {
        when(organisationalUnitService.getAgencyToken(anyLong())).thenThrow(new TokenDoesNotExistException());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/organisationalUnits/123/agencyToken")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(organisationalUnitService, times(1)).getAgencyToken(eq(123l));
    }

    @Test
    public void shouldThrowGeneralApplicationExceptionIfTechnicalErrorOccurs() throws Exception {
        RuntimeException expectedCause = new RuntimeException();
        when(organisationalUnitService.getAgencyToken(anyLong())).thenThrow(new CSRSApplicationException("something went wrong", expectedCause));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/organisationalUnits/123/agencyToken")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(organisationalUnitService, times(1)).getAgencyToken(eq(123l));
    }

    @Test
    public void shouldSaveAgencyTokenIfValidAgencyTokenDTOIsProvided() throws Exception {
        OrganisationalUnit orgUnit = new OrganisationalUnit();
        orgUnit.setAbbreviation("NHSDUNDEE");
        orgUnit.setCode("NHSDUN");
        Optional<OrganisationalUnit> orgUnitOptional = Optional.of(orgUnit);

        when(organisationalUnitService.getOrganisationalUnit(anyLong())).thenReturn(orgUnitOptional);
        when(organisationalUnitService.setAgencyToken(eq(orgUnit), any(AgencyToken.class))).thenReturn(orgUnit);

        requestBodyAgencyTokenAsAString = JsonUtils.asJsonString(dto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/organisationalUnits/123/agencyToken").contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON).content(requestBodyAgencyTokenAsAString))
                .andDo(print())
                .andExpect(status().isCreated());

        verify(organisationalUnitService, times(1)).getOrganisationalUnit(eq(123L));
        verify(organisationalUnitService, times(1)).setAgencyToken(eq(orgUnit), any(AgencyToken.class));
    }

    @Test
    public void shouldNotSaveAgencyTokenIfInvalidAgencyTokenDTOIsProvided_capacityLessThan1() throws Exception {
        // capacity must be between 1 and 1500, this should fail validation
        dto.setCapacity(0);
        requestBodyAgencyTokenAsAString = JsonUtils.asJsonString(dto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/organisationalUnits/123/agencyToken").contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON).content(requestBodyAgencyTokenAsAString))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(organisationalUnitService, never()).getOrganisationalUnit(anyLong());
        verify(organisationalUnitService, never()).setAgencyToken(any(OrganisationalUnit.class), any(AgencyToken.class));
    }

    @Test
    public void shouldNotSaveAgencyTokenIfInvalidAgencyTokenDTOIsProvided_capacityTooHigh() throws Exception {
        // must be at least 1 domain
        dto.setAgencyDomains(new HashSet<>());
        requestBodyAgencyTokenAsAString = JsonUtils.asJsonString(dto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/organisationalUnits/123/agencyToken").contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON).content(requestBodyAgencyTokenAsAString))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(organisationalUnitService, never()).getOrganisationalUnit(anyLong());
        verify(organisationalUnitService, never()).setAgencyToken(any(OrganisationalUnit.class), any(AgencyToken.class));
    }

    @Test
    public void shouldReturnOkIfRequestingAllCodesMap2() throws Exception {
        String code1 = "code1";
        String code2 = "code2";
        List<String> organisationalUnitsCodesList = Arrays.asList(code1, code2);

        OrganisationalUnit organisationalUnit1 = new OrganisationalUnit();
        organisationalUnit1.setCode(code1);

        OrganisationalUnit organisationalUnit2 = new OrganisationalUnit();
        organisationalUnit2.setCode(code2);

        OrganisationalUnit organisationalUnit3 = new OrganisationalUnit();
        organisationalUnit3.setCode(code1);

        OrganisationalUnit organisationalUnit4 = new OrganisationalUnit();
        organisationalUnit4.setCode(code2);

        List<OrganisationalUnit> organisationalUnitsParentsList1 = Arrays.asList(organisationalUnit1, organisationalUnit2);
        List<OrganisationalUnit> organisationalUnitsParentsList2 = Arrays.asList(organisationalUnit3, organisationalUnit4);

        when(organisationalUnitService.getOrganisationalUnitCodes()).thenReturn(organisationalUnitsCodesList);

        when(organisationalUnitService.getOrganisationWithParents(code1)).thenReturn(organisationalUnitsParentsList1);
        when(organisationalUnitService.getOrganisationWithParents(code2)).thenReturn(organisationalUnitsParentsList2);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/organisationalUnits/allCodesMap")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
