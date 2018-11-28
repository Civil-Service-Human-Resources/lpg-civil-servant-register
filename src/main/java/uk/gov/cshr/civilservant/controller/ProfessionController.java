package uk.gov.cshr.civilservant.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cshr.civilservant.domain.Profession;
import uk.gov.cshr.civilservant.service.ProfessionService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/professions")
public class ProfessionController {
    private ProfessionService professionService;

    public ProfessionController(ProfessionService professionService) {
        this.professionService = professionService;
    }

    @GetMapping("/tree")
    public ResponseEntity<List<Profession>> listProfessionsAsTreeStructure() {
        List<Profession> professions = professionService.getParents();

        return ResponseEntity.ok(professions);
    }

    @GetMapping("/flat")
    public ResponseEntity<Map<String, String>> listProfessionsAsFlatStructure() {
        Map<String, String> professionsMap = professionService.getMapSortedByValue();

        return ResponseEntity.ok(professionsMap);
    }
}
