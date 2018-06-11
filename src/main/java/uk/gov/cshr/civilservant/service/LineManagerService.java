package uk.gov.cshr.civilservant.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.cshr.civilservant.domain.CivilServant;
import uk.gov.cshr.civilservant.repository.CivilServantRepository;
import uk.gov.cshr.civilservant.service.identity.IdentityFromService;
import uk.gov.cshr.civilservant.service.identity.IdentityService;
import uk.gov.service.notify.NotificationClientException;

import java.util.Optional;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

@Service
public class LineManagerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotifyService.class);

    private NotifyService notifyService;

    private IdentityService identityService;

    private CivilServantRepository civilServantRepository;

    @Value("${govNotify.template.lineManager}")
    private String govNotifyLineManagerTemplateId;

    @Autowired
    public LineManagerService(CivilServantRepository civilServantRepository, IdentityService identityService, NotifyService notifyService) {
        this.civilServantRepository = civilServantRepository;
        this.identityService = identityService;
        this.notifyService = notifyService;
    }

    public IdentityFromService checkLineManager(String email) {
        return identityService.findByEmail(email);
    }

    public void notifyLineManager(CivilServant civilServant, IdentityFromService lineManager, String email) {

        Optional<CivilServant> optionalLineManager = civilServantRepository.findByIdentity(lineManager.getUid());

        String learnerName = defaultIfNull(civilServant.getFullName(), "");
        String lineManagerName = optionalLineManager
                .map(lm -> Optional.of(lm.getFullName()))
                .orElse(Optional.of(""))
                .get();

        try {
            notifyService.notify(email, govNotifyLineManagerTemplateId, lineManagerName, learnerName);
        } catch (NotificationClientException nce) {
            LOGGER.error("Could not send Line Manager notification", nce);
        }
    }
}
