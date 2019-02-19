package uk.gov.cshr.civilservant.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cshr.civilservant.dto.CivilServantDto;
import uk.gov.cshr.civilservant.mapping.RoleMapping;
import uk.gov.cshr.civilservant.service.ReportService;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/report")
public class ReportController {
    private static final Logger LOG = LoggerFactory.getLogger(ReportController.class);

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @RoleMapping("ORGANISATION_REPORTER")
    @GetMapping("/civilServants")
    public ResponseEntity<Map<String, CivilServantDto>> listAllCivilServantsByOrganisation(Principal principal) {
        return ResponseEntity.ok(reportService.getCivilServantMapByUserOrganisation(principal.getName()));
    }

    @RoleMapping("PROFESSION_REPORTER")
    @GetMapping("/civilServants")
    public ResponseEntity<Map<String, CivilServantDto>> listAllCivilServantsByProfession(Principal principal) {
        return ResponseEntity.ok(reportService.getCivilServantMapByUserProfession(principal.getName()));
    }

    @RoleMapping("CSHR_REPORTER")
    @GetMapping("/civilServants")
    public ResponseEntity<Map<String, CivilServantDto>> listAllCivilServants() {
        return ResponseEntity.ok(reportService.getCivilServantMap());
    }

    @RoleMapping("KPMG_SUPPLIER_REPORTER")
    @GetMapping("/civilServants")
    public ResponseEntity<Map<String, CivilServantDto>> listAllCivilServantsBySupplier(Principal principal) {
        return ResponseEntity.ok(reportService.getCivilServantMapByUserSupplier(principal.getName()));
    }


    @GetMapping("/civilServants")
    public ResponseEntity<Map<String, CivilServantDto>> unauthorised(Principal principal) {
        // default to returning a 403 if none of the above roles are found.
        LOG.debug(String.format("Unauthorised. Required role not found in %s", principal));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
