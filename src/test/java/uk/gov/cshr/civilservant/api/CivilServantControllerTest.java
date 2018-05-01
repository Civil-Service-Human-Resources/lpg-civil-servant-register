package uk.gov.cshr.civilservant.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.domain.Identity;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;
import uk.gov.cshr.civilservant.repository.IdentityRepository;

import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class CivilServantControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CivilServantRepository civilServantRepository;

    @Autowired
    private IdentityRepository identityRepository;

    @Before
    public void setup() {
        initMocks(this);
        mockMvc = webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithAnonymousUser
    public void shouldReturnUnauthorisedIfNotAuthenticated() throws Exception {

        mockMvc.perform(
                get("/civil-servant")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void shouldReturnCivilServantDetailsIfAuthenticated() throws Exception {

        mockMvc.perform(
                get("/civil-servant")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser("uid")
    public void shouldChangeCivilServantFullNameWhenUpdated() throws Exception {

        final String fullName = "newFullName";

        mockMvc.perform(
                put("/civil-servant")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"fullName\": \"" + fullName + "\" }"))
                .andDo(print())
                .andExpect(status().isNoContent());

        Optional<Identity> identity = identityRepository.findByUid("uid");
        assertThat(identity.isPresent(), is(true));

        Optional<CivilServant> optionalCivilServant = civilServantRepository.findByIdentity(identity.get());
        assertThat(optionalCivilServant.isPresent(), is(true));

        CivilServant civilServant = optionalCivilServant.get();

        assertThat(civilServant.getFullName(), equalTo(fullName));
    }
}
