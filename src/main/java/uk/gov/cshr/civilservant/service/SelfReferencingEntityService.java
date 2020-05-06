package uk.gov.cshr.civilservant.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cshr.civilservant.domain.SelfReferencingEntity;
import uk.gov.cshr.civilservant.dto.DtoEntity;
import uk.gov.cshr.civilservant.dto.factory.DtoFactory;
import uk.gov.cshr.civilservant.repository.SelfReferencingEntityRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class SelfReferencingEntityService<T extends SelfReferencingEntity, K extends DtoEntity> {
    private final DtoFactory<K, T> dtoFactory;
    private SelfReferencingEntityRepository<T> repository;

    SelfReferencingEntityService(SelfReferencingEntityRepository<T> repository, DtoFactory<K, T> dtoFactory) {
        this.repository = repository;
        this.dtoFactory = dtoFactory;
    }

    /**
     * This will return all parent entities with any children as a list
     */
    @Transactional(readOnly = true)
    public List<T> getParents() {
        return repository.findAllByOrderByNameAsc()
                .stream()
                .filter(org -> !org.hasParent())
                .collect(Collectors.toList());
    }

    /**
     * This will return all Dto entities as a list, sorted by formattedName
     */
    @Transactional(readOnly = true)
    public List<K> getListSortedByValue() {
        return repository.findAll()
                .stream()
                .map(o -> dtoFactory.create(o))
                .sorted(Comparator.comparing(K::getFormattedName))
                .collect(Collectors.toList());
    }
}
