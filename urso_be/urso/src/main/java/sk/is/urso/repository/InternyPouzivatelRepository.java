package sk.is.urso.repository;

import org.alfa.repository.EntityRepository;
import org.springframework.stereotype.Repository;
import sk.is.urso.model.InternyPouzivatel;

import java.util.Optional;

@Repository
public interface InternyPouzivatelRepository extends EntityRepository<InternyPouzivatel, Long> {
    Optional<InternyPouzivatel> findFirstByOrderByIdAsc();

    Optional<InternyPouzivatel> findOneByDomenovyUcet(String domenovyUcet);

}
