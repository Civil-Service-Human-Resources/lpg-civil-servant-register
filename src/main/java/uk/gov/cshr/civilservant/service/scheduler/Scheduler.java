package uk.gov.cshr.civilservant.service.scheduler;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import uk.gov.cshr.civilservant.service.QuizService;

import java.time.LocalDateTime;

@Component
@Slf4j
public class Scheduler {

    private final QuizService quizService;
    private final Integer retentionWindowInDays;

    @Autowired
    public Scheduler(QuizService quizService, @Value("${data-retention-windows.skills}") Integer retentionWindowInDays) {
        this.quizService = quizService;
        this.retentionWindowInDays = retentionWindowInDays;
    }

    @Scheduled(cron = "0 0 2 * * *")
    @SchedulerLock(name = "skillsDataRetention", lockAtMostFor = "PT30M")
    public void skillsDataRetention() {
        log.info("Executing Skills Data Retention Job");
        SecurityContextHolder.setContext(SecurityUtils.createSchedulerSecurityContext());
        long deleteCount = quizService.deleteQuizResultsCompletedBeforeDate(LocalDateTime.now().minusDays(retentionWindowInDays.longValue()));
        log.info("Completed Skills Data Retention Job, {} records deleted", deleteCount);
    }
}
