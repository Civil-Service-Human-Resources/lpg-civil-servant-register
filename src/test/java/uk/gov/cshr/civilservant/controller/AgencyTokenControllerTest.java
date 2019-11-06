package uk.gov.cshr.civilservant.controller;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.cshr.civilservant.domain.AgencyToken;
import uk.gov.cshr.civilservant.dto.UpdateSpacesForAgencyTokenDTO;
import uk.gov.cshr.civilservant.exception.NotEnoughSpaceAvailableException;
import uk.gov.cshr.civilservant.exception.TokenDoesNotExistException;
import uk.gov.cshr.civilservant.service.AgencyTokenService;
import uk.gov.cshr.civilservant.utils.JsonUtils;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@WithMockUser(username = "user")
public class AgencyTokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AgencyTokenService agencyTokenService;

    @Test
    public void shouldReturnOkIfRequestingAgencyTokens() throws Exception {
        Iterable<AgencyToken> agencyTokens = new ArrayList<>();

        when(agencyTokenService.getAllAgencyTokens()).thenReturn(agencyTokens);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/agencyTokens")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnOkIfRequestingAgencyTokensWithDomainParam() throws Exception {
        String domain = "example.com";
        Iterable<AgencyToken> agencyTokens = new ArrayList<>();

        when(agencyTokenService.getAllAgencyTokensByDomain(domain)).thenReturn(agencyTokens);

        mockMvc.perform(
                MockMvcRequestBuilders.get(String.format("/agencyTokens?domain=%s", domain))
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnOkIfRequestingAgencyTokensWithDomainTokenParams() throws Exception {
        AgencyToken agencyToken = new AgencyToken();
        String domain = "example.com";
        String token = "token123";

        when(agencyTokenService.getAgencyTokenByDomainAndToken(domain, token)).thenReturn(Optional.of(agencyToken));

        mockMvc.perform(
                MockMvcRequestBuilders.get(String.format("/agencyTokens?domain=%s&token=%s", domain, token))
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnNotFoundIfRequestingAgencyTokensWithDomainTokenParams() throws Exception {
        String domain = "example.com";
        String token = "token123";

        when(agencyTokenService.getAgencyTokenByDomainAndToken(domain, token)).thenReturn(Optional.empty());

        mockMvc.perform(
                MockMvcRequestBuilders.get(String.format("/agencyTokens?domain=%s&token=%s", domain, token))
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnOkIfRequestingAgencyTokensWithDomainTokenOrgParams() throws Exception {
        AgencyToken agencyToken = new AgencyToken();
        String domain = "example.com";
        String token = "token123";
        String code = "code";

        when(agencyTokenService.getAgencyTokenByDomainTokenAndOrganisation(domain, token, code)).thenReturn(Optional.of(agencyToken));

        mockMvc.perform(
                MockMvcRequestBuilders.get(String.format("/agencyTokens?domain=%s&token=%s&code=%s", domain, token, code))
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnNotFoundIfRequestingAgencyTokensWithDomainTokenOrgParams() throws Exception {
        String domain = "example.com";
        String token = "token123";
        String code = "code";

        when(agencyTokenService.getAgencyTokenByDomainTokenAndOrganisation(domain, token, code)).thenReturn(Optional.empty());

        mockMvc.perform(
                MockMvcRequestBuilders.get(String.format("/agencyTokens?domain=%s&token=%s&code=%s", domain, token, code))
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnOkIfValidAgencyTokenWithSpacesAvailable() throws Exception {
        String domain = "example.com";
        String token = "token123";
        String code = "code";

        UpdateSpacesForAgencyTokenDTO dto = new UpdateSpacesForAgencyTokenDTO();
        dto.setDomain(domain);
        dto.setToken(token);
        dto.setCode(code);

        AgencyToken agencyToken = new AgencyToken();
        when(agencyTokenService.updateAgencyTokenSpacesAvailable(domain, token, code, false)).thenReturn(Optional.of(agencyToken));

        mockMvc.perform(MockMvcRequestBuilders.put("/agencyTokens")
                .content(JsonUtils.asJsonString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnNotFoundIfAgencyTokenNotFound() throws Exception {
        String domain = "example.com";
        String token = "token123";
        String code = "code";

        UpdateSpacesForAgencyTokenDTO dto = new UpdateSpacesForAgencyTokenDTO();
        dto.setDomain(domain);
        dto.setToken(token);
        dto.setCode(code);

        when(agencyTokenService.updateAgencyTokenSpacesAvailable(anyString(), anyString(), anyString(), anyBoolean())).thenThrow(new TokenDoesNotExistException(token));

        mockMvc.perform(MockMvcRequestBuilders.put("/agencyTokens")
                .content(JsonUtils.asJsonString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnConflictIfAgencyTokenHasNoSpacesAvailable() throws Exception {
        String domain = "example.com";
        String token = "token123";
        String code = "code";

        UpdateSpacesForAgencyTokenDTO dto = new UpdateSpacesForAgencyTokenDTO();
        dto.setDomain(domain);
        dto.setToken(token);
        dto.setCode(code);

        when(agencyTokenService.updateAgencyTokenSpacesAvailable(anyString(), anyString(), anyString(), anyBoolean())).thenThrow(new NotEnoughSpaceAvailableException(token));

        mockMvc.perform(MockMvcRequestBuilders.put("/agencyTokens")
                .content(JsonUtils.asJsonString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    public void shouldReturnOkIfUserIsRemovedFromAValidAgencyToken() throws Exception {
        String domain = "example.com";
        String token = "token123";
        String code = "code";

        UpdateSpacesForAgencyTokenDTO dto = new UpdateSpacesForAgencyTokenDTO();
        dto.setDomain(domain);
        dto.setToken(token);
        dto.setCode(code);

        AgencyToken agencyToken = new AgencyToken();
        when(agencyTokenService.updateAgencyTokenSpacesAvailable(domain, token, code, false)).thenReturn(Optional.of(agencyToken));

        mockMvc.perform(MockMvcRequestBuilders.put("/agencyTokens")
                .content(JsonUtils.asJsonString(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnOkIfRequestingAgencyTokensWithDomainOrgParams() throws Exception {
        AgencyToken agencyToken = new AgencyToken();
        String domain = "example.com";
        String code = "code";

        when(agencyTokenService.getAgencyTokenByDomainAndOrganisation(domain, code)).thenReturn(Optional.of(agencyToken));

        mockMvc.perform(
                MockMvcRequestBuilders.get(String.format("/agencyTokens?domain=%s&code=%s", domain, code))
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnNotFoundIfRequestingAgencysWithDomainOrgParams() throws Exception {
        String domain = "example.com";
        String code = "code";

        when(agencyTokenService.getAgencyTokenByDomainAndOrganisation(domain, code)).thenReturn(Optional.empty());

        mockMvc.perform(
                MockMvcRequestBuilders.get(String.format("/agencyTokens?domain=%s&code=%s", domain, code))
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

}