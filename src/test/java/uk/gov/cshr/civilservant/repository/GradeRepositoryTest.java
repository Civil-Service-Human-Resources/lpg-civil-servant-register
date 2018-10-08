package uk.gov.cshr.civilservant.repository;


import com.google.common.collect.Iterables;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.cshr.civilservant.domain.Grade;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GradeRepositoryTest {

    @Autowired
    private GradeRepository gradeRepository;

    @Test
    public void shouldFindGradesWithNoOrganisation() {
        Iterable<Grade> results = gradeRepository.findByOrganisationalUnitIsNull();
        assertThat(Iterables.size(results), is(11));
    }
}
