package uk.gov.cshr.civilservant.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.domain.OrganisationalUnit;
import uk.gov.cshr.civilservant.dto.OrganisationalUnitDto;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;
import uk.gov.cshr.civilservant.service.OrganisationalUnitService;
import uk.gov.cshr.civilservant.utils.MockMVCFilterOverrider;
import uk.gov.cshr.civilservant.utils.JsonUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    @MockBean
    private OrganisationalUnitService organisationalUnitService;

    @MockBean
    private CivilServantRepository civilServantRepository;

    @Before
    public void overridePatternMappingFilterProxyFilter() throws IllegalAccessException {
        MockMVCFilterOverrider.overrideFilterOf(mockMvc, "PatternMappingFilterProxy" );
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
    public void shouldAddOrganisationReportingPermission() throws Exception {
        String uid = "uid1";
        CivilServant civilServant = new CivilServant();
        civilServant.setId(1L);
        Optional<CivilServant> civilServantOptional = Optional.of(civilServant);
        List<String> listOrgCode = Arrays.asList("orgCode1","orgCode2");
        List<Long> listOrgIdWithChildrenIds = Arrays.asList(1L, 2L);
        List<String> listOrgId = Arrays.asList("orgId1", "orgId2");
        when(civilServantRepository.findByIdentity(uid)).thenReturn(civilServantOptional);
        when(organisationalUnitService.getOrganisationalUnitCodesForIds(anyList())).thenReturn(listOrgCode);
        when(organisationalUnitService.getOrganisationIdWithChildrenIds(anyList())).thenReturn(listOrgIdWithChildrenIds);

        String requestBody = JsonUtils.asJsonString(listOrgId);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/organisationalUnits/addOrganisationReportingPermission/uid1")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk());

        verify(organisationalUnitService, times(1))
                .addOrganisationReportingPermission(civilServant.getId(), listOrgIdWithChildrenIds);
    }

    @Test
    public void shouldUpdateOrganisationReportingPermission() throws Exception {
        String uid = "uid1";
        CivilServant civilServant = new CivilServant();
        civilServant.setId(1L);
        Optional<CivilServant> civilServantOptional = Optional.of(civilServant);
        List<String> listOrgCode = Arrays.asList("orgCode1","orgCode2");
        List<Long> listOrgIdWithChildrenIds = Arrays.asList(1L, 2L);
        List<String> listOrgId = Arrays.asList("orgId1", "orgId2");
        when(civilServantRepository.findByIdentity(uid)).thenReturn(civilServantOptional);
        when(organisationalUnitService.getOrganisationalUnitCodesForIds(anyList())).thenReturn(listOrgCode);
        when(organisationalUnitService.getOrganisationIdWithChildrenIds(anyList())).thenReturn(listOrgIdWithChildrenIds);

        String requestBody = JsonUtils.asJsonString(listOrgId);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/organisationalUnits/updateOrganisationReportingPermission/uid1")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk());

        verify(organisationalUnitService, times(1))
                .updateOrganisationReportingPermission(civilServant.getId(), listOrgIdWithChildrenIds);
    }

    @Test
    public void shouldDeleteOrganisationReportingPermission() throws Exception {
        String uid = "uid1";
        CivilServant civilServant = new CivilServant();
        civilServant.setId(1L);
        Optional<CivilServant> civilServantOptional = Optional.of(civilServant);
        when(civilServantRepository.findByIdentity(uid)).thenReturn(civilServantOptional);

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/organisationalUnits/deleteOrganisationReportingPermission/uid1"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(organisationalUnitService, times(1))
                .deleteOrganisationReportingPermission(civilServant.getId());
    }
}
