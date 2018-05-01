package uk.gov.cshr.civilservant.api;

import org.springframework.hateoas.ResourceSupport;
import uk.gov.cshr.civilservant.domain.CivilServant;

import javax.validation.constraints.NotEmpty;

import static com.google.common.base.Preconditions.checkArgument;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

public class CivilServantResource extends ResourceSupport {

    @NotEmpty
    private String fullName;

    private GradeResource grade;

    private OrganisationResource organisation;

    public CivilServantResource() {
    }

    public CivilServantResource(CivilServant civilServant) {
        checkArgument(civilServant != null);

        this.fullName = civilServant.getFullName();

        if (civilServant.getGrade() != null) {
            this.grade = new GradeResource(civilServant.getGrade());
        }
        if (civilServant.getOrganisation() != null) {
            this.organisation = new OrganisationResource(civilServant.getOrganisation());
        }

        add(linkTo(CivilServantController.class).withSelfRel());
    }

    public String getFullName() {
        return fullName;
    }

    public OrganisationResource getOrganisation() {
        return organisation;
    }

    public GradeResource getGrade() {
        return grade;
    }
}
