package uk.gov.cshr.civilservant.controller;

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
}