package uk.gov.cshr.civilservant.service;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;


@RunWith(MockitoJUnitRunner.class)
public class CivilServantServiceTest {

    @InjectMocks
    private CivilServantService civilServantService;

    @Mock
    private CivilServantRepository civilServantRepository;


    @Test
    @Ignore
    public void shallUpdateOtherAreasOfWork() {
        final CivilServant currCivilServant =  buildACivilServant();
        final Pair<String, String> updateNode = new ImmutablePair<>("","");

      /*  when(civilServantRepository.save(currCivilServant)).thenReturn()
        Assert.isTrue(civilServantService.update(currCivilServant, ));*/
    }

    private CivilServant buildACivilServant() {
        return null;
    }
}
