package uk.gov.cshr.civilservant.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.domain.Identity;
import uk.gov.cshr.civilservant.exception.CSRSApplicationException;
import uk.gov.cshr.civilservant.exception.CivilServantNotFoundException;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static uk.gov.cshr.civilservant.utils.ApplicationConstants.NO_CIVIL_SERVANT_FOUND_ERROR_MESSAGE;

@RunWith(MockitoJUnitRunner.class)
public class CivilServantServiceTest {

    @Mock
    private CivilServantRepository civilServantRepository;

    @InjectMocks
    private CivilServantService classUnderTest;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldReturnCivilServantUid() throws CSRSApplicationException {
        Identity identity = new Identity("1");
        CivilServant cs = new CivilServant(identity);
        Optional<CivilServant> optionalCivilServant = Optional.of(cs);
        when(civilServantRepository.findByPrincipal()).thenReturn(optionalCivilServant);

        String actual = classUnderTest.getCivilServantUid();

        assertThat(actual, equalTo("1"));
    }

    @Test
    public void shouldThrowGeneralApplicationExceptionWithCauseOfCivilServantNotFoundExceptionWhenCivilServantHasNoIdentity() throws CSRSApplicationException {
        CivilServant cs = new CivilServant();
        Optional<CivilServant> optionalCivilServant = Optional.of(cs);
        when(civilServantRepository.findByPrincipal()).thenReturn(optionalCivilServant);

        expectedException.expect(CSRSApplicationException.class);
        expectedException.expectMessage(NO_CIVIL_SERVANT_FOUND_ERROR_MESSAGE);
        expectedException.expectCause(is(instanceOf(CivilServantNotFoundException.class)));

        String actual = classUnderTest.getCivilServantUid();

        assertThat(actual, isNull());
    }

    @Test
    public void shouldThrowGeneralApplicationExceptionWithCauseOfCivilServantNotFoundExceptionWhenCivilServantNotFound() throws CSRSApplicationException {
        when(civilServantRepository.findByPrincipal()).thenReturn(Optional.empty());

        expectedException.expect(CSRSApplicationException.class);
        expectedException.expectMessage(NO_CIVIL_SERVANT_FOUND_ERROR_MESSAGE);
        expectedException.expectCause(is(instanceOf(CivilServantNotFoundException.class)));

        String actual = classUnderTest.getCivilServantUid();

        assertThat(actual, isNull());
    }

    @Test
    public void shouldThrowGeneralApplicationExceptionWhenTechnicalError() throws CSRSApplicationException {
        RuntimeException runtimeException = new RuntimeException("broken");
        when(civilServantRepository.findByPrincipal()).thenThrow(runtimeException);

        expectedException.expect(CSRSApplicationException.class);
        expectedException.expectMessage(NO_CIVIL_SERVANT_FOUND_ERROR_MESSAGE);
        expectedException.expectCause(is(runtimeException));

        String actual = classUnderTest.getCivilServantUid();

        assertThat(actual, isNull());
    }

}
