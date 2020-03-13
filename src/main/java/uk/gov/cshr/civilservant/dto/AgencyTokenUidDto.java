package uk.gov.cshr.civilservant.dto;

import lombok.Data;

@Data
public class AgencyTokenUidDto {
    private String uid;
    private int capacity;

    public AgencyTokenUidDto(String uid, int capacity) {
        this.uid = uid;
        this.capacity = capacity;
    }
}
