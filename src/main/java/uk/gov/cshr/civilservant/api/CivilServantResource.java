package uk.gov.cshr.civilservant.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cshr.civilservant.domain.CivilServant;

import javax.validation.constraints.NotEmpty;

import static com.google.common.base.Preconditions.checkArgument;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

public class CivilServantResource extends ResourceSupport {

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

    @JsonIgnore
    public Long getGradeId() {
        if (grade != null) {
            Link gradeIdLink = grade.getId();
            UriComponents uriComponents = UriComponentsBuilder.fromUriString(gradeIdLink.getHref()).build();
            if (uriComponents.getPathSegments().size() == 2) {
                return Long.decode(uriComponents.getPathSegments().get(1));
            }
        }
        return null;
    }

    @JsonIgnore
    public Long getOrganisationId() {
        if (organisation != null) {
            Link organisationIdLink = organisation.getId();
            UriComponents uriComponents = UriComponentsBuilder.fromUriString(organisationIdLink.getHref()).build();
            if (uriComponents.getPathSegments().size() == 2) {
                return Long.decode(uriComponents.getPathSegments().get(1));
            }
        }
        return null;
    }
}
