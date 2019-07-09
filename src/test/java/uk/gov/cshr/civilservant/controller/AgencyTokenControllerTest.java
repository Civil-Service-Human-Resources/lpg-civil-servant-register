package uk.gov.cshr.civilservant.controller;

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
import uk.gov.cshr.civilservant.domain.AgencyToken;
import uk.gov.cshr.civilservant.service.AgencyTokenService;

import java.util.ArrayList;

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
    public void getAgencyTokensByOrganisationalUnit() throws Exception {
        Long organisationalUnitId = 1L;
        Iterable<AgencyToken> organisationalUnits = new ArrayList<>();

        when(agencyTokenService.getAgencyTokens(organisationalUnitId)).thenReturn(organisationalUnits);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/agencyTokens/organisationalUnits/" + organisationalUnitId)
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}

