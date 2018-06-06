package uk.gov.cshr.civilservant.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

import java.util.HashMap;

@Service
@Transactional
public class NotifyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotifyService.class);
    private static final String NAME_PERSONALISATION = "name";
    private static final String LEARNER_PERSONALISATION = "learner";

    @Value("${govNotify.key}")
    private String govNotifyKey;

    public SendEmailResponse notify(String email,  String templateId, String name, String learner) throws NotificationClientException {

        HashMap<String, String> personalisation = new HashMap<>();
        personalisation.put(NAME_PERSONALISATION, name);
        personalisation.put(LEARNER_PERSONALISATION, learner);

        NotificationClient client = new NotificationClient(govNotifyKey);
        SendEmailResponse response = null;

        try {
            response = client.sendEmail(templateId, email, personalisation, "");
        } catch (NotificationClientException nce) {
            LOGGER.error("Error sending line manager notification: {}", nce);
           throw  nce;
        }

        LOGGER.info("Line Manager Notify email: {}", response.getBody());
        return response;
    }

}
