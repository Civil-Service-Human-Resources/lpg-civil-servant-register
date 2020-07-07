package uk.gov.cshr.civilservant.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DtoEntity implements Serializable {
    protected String name;
    protected long id;
    protected String href;
    protected String abbreviation;
    protected String formattedName;
}
