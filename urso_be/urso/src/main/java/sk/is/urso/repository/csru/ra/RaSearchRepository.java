package sk.is.urso.repository.csru.ra;

import org.alfa.repository.EntityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sk.is.urso.model.csru.ra.RaAddressSearch;

import java.util.List;

public interface RaSearchRepository extends EntityRepository<RaAddressSearch, Long> {

    @Query(value = "SELECT * FROM csru_ra_address_search e WHERE MATCH(e.fulltext_search) AGAINST(:searchTerm IN BOOLEAN MODE) LIMIT :limit", nativeQuery = true)
    List<RaAddressSearch> findAllBySearchTerm(@Param("searchTerm") String searchTerm, @Param("limit") int limit);
}
