package uk.gov.cshr.civilservant.service.scheduler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.cshr.civilservant.service.QuizService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SchedulerTest {

    private static final Integer testWindow = Integer.valueOf(1095);

    @Mock
    QuizService quizService;

    @InjectMocks
    Scheduler scheduler;

    @Before
    public void setWindowValue() {
        ReflectionTestUtils.setField(scheduler, "retentionWindowInDays", testWindow);
    }

    @Test
    public void skillsDataRetention() {
        when(quizService.deleteQuizResultsCompletedBeforeDate(any())).thenReturn(1L);

        scheduler.skillsDataRetention();

        ArgumentCaptor<LocalDateTime> dateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(quizService).deleteQuizResultsCompletedBeforeDate(dateCaptor.capture());
        verifyNoMoreInteractions(quizService);

        LocalDateTime calculatedPassedDateTime = LocalDateTime.now().minusDays(testWindow);
        LocalDateTime passedDateTime = dateCaptor.getValue();

        long passedCalculatedDiff = calculatedPassedDateTime.toInstant(ZoneOffset.UTC).getEpochSecond() - passedDateTime.toInstant(ZoneOffset.UTC).getEpochSecond();

        // Calculated timestamp is created after execution so there should be a small difference, working in seconds so could be 0
        assertTrue(passedCalculatedDiff >= 0);

        // Check the difference was less than 5 seconds (should be less but add small gap)
        assertTrue(passedCalculatedDiff < 5);
    }
}