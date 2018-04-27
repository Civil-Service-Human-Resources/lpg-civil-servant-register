package uk.gov.cslearning.civilservant.api;

import java.util.Collection;
import java.util.List;

public class Results<T> {

    private Collection<T> results;

    Results(Collection<T> results) {
        this.results = results;
    }

    public Collection<T> getResults() {
        return results;
    }
}
