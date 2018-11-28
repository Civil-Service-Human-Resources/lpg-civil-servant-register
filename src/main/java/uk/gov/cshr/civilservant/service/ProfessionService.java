package uk.gov.cshr.civilservant.service;

import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.stereotype.Service;
import uk.gov.cshr.civilservant.domain.Profession;
import uk.gov.cshr.civilservant.repository.ProfessionRepository;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProfessionService {
    private final ProfessionRepository professionRepository;

    private final RepositoryEntityService<Profession> repositoryEntityService;

    public ProfessionService(ProfessionRepository professionRepository, RepositoryEntityService<Profession> repositoryEntityService) {
        this.professionRepository = professionRepository;
        this.repositoryEntityService = repositoryEntityService;
    }

    /**
    * This will return all Parent professions with any children as a list
    */
    public List<Profession> getParentProfessions() {
        return professionRepository.findAllByOrderByNameAsc()
                .stream()
                .filter(profession -> !profession.hasParent())
                .collect(Collectors.toList());
    }

    /**
    * This will return all Professions as a map.
    * In the map, the key will be the href of the profession which is obtained using {@link RepositoryEntityLinks} in {@link RepositoryEntityService}.
    * The value will represent the profession name, but formatted to include any parents recursively using {@link ProfessionService#formatName(Profession)}.
    * Finally, this is map is sorted by value into a LinkedHashMap
    */
    public Map<String, String> getProfessionsMapSortedByValue() {
        return professionRepository.findAll().stream()
                .collect(Collectors.toMap(profession -> repositoryEntityService.getUri(Profession.class, profession), this::formatName))
                .entrySet()
                .stream()
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (u, v) -> {
                            throw new IllegalStateException(String.format("Duplicate key %s", u));
                        },
                        LinkedHashMap::new));    }
    /**
    * Format the name of an profession to be prefixed with parental hierarchy.
    *
    * e.g. Parent | Child | Subchild
    */
    private String formatName(Profession profession) {
        Profession currentNode = profession;

        String name = currentNode.getName();

        while (currentNode.hasParent()) {
            currentNode = currentNode.getParent();

            StringBuilder sb = new StringBuilder();
            name = sb.append(currentNode.getName())
                    .append(" | ")
                    .append(name)
                    .toString();
        }

        return name;
    }

}
