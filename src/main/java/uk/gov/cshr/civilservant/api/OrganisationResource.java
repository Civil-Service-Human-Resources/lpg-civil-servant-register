package uk.gov.cshr.civilservant.api;

import org.springframework.hateoas.ResourceSupport;
import uk.gov.cshr.civilservant.domain.Organisation;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.toList;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

public class OrganisationResource extends ResourceSupport {

    private String name;

    private String code;

    private List<GradeResource> grades;

    public OrganisationResource() {
    }

    public OrganisationResource(Organisation organisation) {
        this(organisation, true);
    }

    public OrganisationResource(Organisation organisation, boolean summarise) {
        checkArgument(organisation != null);
        this.name = organisation.getName();
        this.code = organisation.getCode();
        if (!summarise) {
            this.grades = organisation.getGrades().stream()
                    .map(GradeResource::new)
                    .collect(toList());
        }
        add(linkTo(OrganisationController.class).slash(organisation.getId()).withSelfRel());
        add(linkTo(GradeController.class, "organisation").withRel("grades").expand(organisation.getId()));
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public List<GradeResource> getGrades() {
        return grades;
    }
}
