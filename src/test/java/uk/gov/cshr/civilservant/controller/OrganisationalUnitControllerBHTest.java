package uk.gov.cshr.civilservant.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
import uk.gov.cshr.civilservant.dto.AgencyTokenDTO;
import uk.gov.cshr.civilservant.dto.OrganisationalUnitDto;
import uk.gov.cshr.civilservant.dto.factory.OrganisationalUnitDtoFactory;
import uk.gov.cshr.civilservant.repository.OrganisationalUnitRepository;
import uk.gov.cshr.civilservant.service.AgencyTokenService;
import uk.gov.cshr.civilservant.service.OrganisationalUnitService;
import uk.gov.cshr.civilservant.utils.*;

import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@WithMockUser(username = "user")
public class OrganisationalUnitControllerBHTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrganisationalUnitService organisationalUnitService;

    @MockBean
    private AgencyTokenService agencyTokenService;

    @Captor
    private ArgumentCaptor<OrganisationalUnitDto> dtoArgumentCaptor;

    private String requestBodyAgencyTokenAsAString;

    private AgencyTokenDTO dto;

    private FamilyOrganisationUnits family;

    @MockBean
    private OrganisationalUnitDtoFactory organisationalUnitDtoFactory;

    @MockBean
    private OrganisationalUnitRepository organisationalUnitRepository;

    @Before
    public void overridePatternMappingFilterProxyFilter() throws IllegalAccessException {
        MockMVCFilterOverrider.overrideFilterOf(mockMvc, "PatternMappingFilterProxy");
        dto = AgencyTokenTestingUtils.createAgencyTokenDTO();
        family = new FamilyOrganisationUnits();
        when(organisationalUnitDtoFactory.create(any(OrganisationalUnit.class))).thenReturn(new OrganisationalUnitDto());
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
    public void givenOrgsExistForGivenCode_whenGetOrgsFlatByDomain_thenShouldReturnOkWithListOfOrgUnits() throws Exception {
        // given
        List<OrganisationalUnit> godFathersOnlyFamily = family.getFamily();
        when(organisationalUnitService.getOrganisationsForDomain(anyString())).thenReturn(godFathersOnlyFamily);
        when(organisationalUnitDtoFactory.create(any(OrganisationalUnit.class))).thenReturn(new OrganisationalUnitDto());

        int expectedListSize = godFathersOnlyFamily.size();

        // when
        mockMvc.perform(
                MockMvcRequestBuilders.get("/organisationalUnits/flat/code123/")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(expectedListSize)))
                .andExpect(status().isOk());

        // then
        verify(organisationalUnitService, times(1)).getOrganisationsForDomain(eq("code123"));
        verify(organisationalUnitDtoFactory, times(expectedListSize)).create(any(OrganisationalUnit.class));
    }

    @Test
    public void givenNoOrgsExistForGivenCode_whenGetOrgsFlatByDomain_thenShouldReturnNotFound404() throws Exception {
        // given
        when(organisationalUnitService.getOrganisationsForDomain(anyString())).thenReturn(new ArrayList<>());

        // when
        mockMvc.perform(
                MockMvcRequestBuilders.get("/organisationalUnits/flat/code123/")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // then
        verify(organisationalUnitService, times(1)).getOrganisationsForDomain(eq("code123"));
        verify(organisationalUnitDtoFactory, never()).create(any(OrganisationalUnit.class));
    }

    @Test
    public void shouldReturnOkIfRequestingAllCodesMap() throws Exception {
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
    public void shouldNotSaveAgencyTokenIfInvalidAgencyTokenDTOIsProvided_capacityUsedGreaterThanCapacity() throws
            Exception {
        // this should fail validation
        dto.setCapacity(0);
        dto.setCapacityUsed(100);
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
    public void shouldNotSaveAgencyTokenIfInvalidAgencyTokenDTOIsProvided_emptyDomains() throws Exception {
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

    @Test
    public void givenAValidCode_whenGetParent_shouldReturnListOfOrgUnits() throws Exception {
        String code0 = "code0";
        String code1 = "code1";

        OrganisationalUnit organisationalUnit0 = new OrganisationalUnit();
        organisationalUnit0.setCode(code0);

        OrganisationalUnit organisationalUnit1 = new OrganisationalUnit();
        organisationalUnit1.setCode(code1);

        List<OrganisationalUnit> organisationalUnitsList = Arrays.asList(organisationalUnit0, organisationalUnit1);

        when(organisationalUnitService.getOrganisationWithParents(code1)).thenReturn(organisationalUnitsList);

        mockMvc.perform(MockMvcRequestBuilders.get("/organisationalUnits/parent/code1")
                .accept(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].code", equalTo("code0")))
                .andExpect(jsonPath("$[1].code", equalTo("code1")))
                .andExpect(status().isOk());
    }

    @Test
    public void givenAnInvalidCode_whenGetParent_shouldReturnEmptyListOfOrgUnits() throws Exception {
        String code1 = "code1";

        List<OrganisationalUnit> organisationalUnitsList = new ArrayList<>();

        when(organisationalUnitService.getOrganisationWithParents(code1)).thenReturn(organisationalUnitsList);

        mockMvc.perform(MockMvcRequestBuilders.get("/organisationalUnits/parent/code1")
                .accept(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(status().isOk());
    }

    @Test
    public void givenAValidCode_whenGetChildren_shouldReturnListOfOrgUnits() throws Exception {
        String code0 = "code0";
        String code1 = "code1";

        OrganisationalUnit organisationalUnit0 = new OrganisationalUnit();
        organisationalUnit0.setCode(code0);

        OrganisationalUnit organisationalUnit1 = new OrganisationalUnit();
        organisationalUnit1.setCode(code1);

        List<OrganisationalUnit> organisationalUnitsList = Arrays.asList(organisationalUnit0, organisationalUnit1);

        when(organisationalUnitService.getOrganisationWithChildren(code1)).thenReturn(organisationalUnitsList);

        mockMvc.perform(MockMvcRequestBuilders.get("/organisationalUnits/children/code1")
                .accept(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].code", equalTo("code0")))
                .andExpect(jsonPath("$[1].code", equalTo("code1")))
                .andExpect(status().isOk());
    }

    @Test
    public void givenAnInvalidCode_whenGetChildren_shouldReturnEmptyListOfOrgUnits() throws Exception {
        String code1 = "code1";

        List<OrganisationalUnit> organisationalUnitsList = new ArrayList<>();

        when(organisationalUnitService.getOrganisationWithChildren(code1)).thenReturn(organisationalUnitsList);

        mockMvc.perform(MockMvcRequestBuilders.get("/organisationalUnits/children/code1")
                .accept(APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)))
                .andExpect(status().isOk());
    }

    private OrganisationalUnitDto createOrgUnitDTO(OrganisationalUnit org) {
        return organisationalUnitDtoFactory.create(org);
    }

}