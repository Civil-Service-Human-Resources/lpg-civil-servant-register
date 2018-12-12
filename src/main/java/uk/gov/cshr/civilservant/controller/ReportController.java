package uk.gov.cshr.civilservant.controller;

import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cshr.civilservant.mapping.RoleMapping;
import uk.gov.cshr.civilservant.resource.CivilServantResource;
import uk.gov.cshr.civilservant.service.ReportService;

import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @RoleMapping("ORGANISATION_REPORTER")
    @GetMapping("/civilServants")
    public ResponseEntity<List<Resource<CivilServantResource>>> listAllByOrganisation(Principal principal) {
        return ResponseEntity.ok(reportService.listCivilServantsByUserOrganisation(principal.getName()));
    }

    @RoleMapping("PROFESSION_REPORTER")
    @GetMapping("/civilServants")
    public ResponseEntity listAllByProfession(Principal principal) {
        return ResponseEntity.ok(reportService.listCivilServantsByUserProfession(principal.getName()));
    }
}
