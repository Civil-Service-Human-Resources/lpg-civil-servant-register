package uk.gov.cshr.civilservant.api;

import org.springframework.hateoas.ResourceSupport;
import uk.gov.cshr.civilservant.domain.Grade;

import static com.google.common.base.Preconditions.checkArgument;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

public class GradeResource extends ResourceSupport {

    private String name;

    private String code;

    public GradeResource(Grade grade) {
        checkArgument(grade != null);
        this.name = grade.getName();
        this.code = grade.getCode();
        add(linkTo(GradeController.class).slash(grade.getId()).withSelfRel());
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
