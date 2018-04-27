package uk.gov.cslearning.civilservant.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.cslearning.civilservant.repository.CivilServantRepository;

import java.security.Principal;

import static com.google.common.base.Preconditions.checkArgument;

@RestController
@RequestMapping("/civil-servant")
public class CivilServantController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CivilServantController.class);

    private CivilServantRepository civilServantRepository;

    @Autowired
    public CivilServantController(CivilServantRepository civilServantRepository) {
        checkArgument(civilServantRepository != null);
        this.civilServantRepository = civilServantRepository;
    }

    @GetMapping
    public ResponseEntity<Principal> get(Principal principal) {
        LOGGER.debug("Getting civil servant details");
        return ResponseEntity.ok(principal);
    }
}
