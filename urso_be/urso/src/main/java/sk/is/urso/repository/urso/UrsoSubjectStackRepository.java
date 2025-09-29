package sk.is.urso.repository.urso;

import org.alfa.repository.EntityRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sk.is.urso.model.SpStavZiadost;
import sk.is.urso.model.UrsoSubjectStack;

import java.util.List;

@Repository
public interface UrsoSubjectStackRepository extends EntityRepository<UrsoSubjectStack, Integer> {

    @Query(value="SELECT * FROM urso_subject_stack WHERE stav = 'PREBIEHA' AND typ_nedoplatku = 'SP'", nativeQuery = true)
    List<UrsoSubjectStack> findAllSpWhereStavEqualsPrebieha();

    @Query(value="SELECT * FROM urso_subject_stack WHERE stav = 'PREBIEHA' AND typ_nedoplatku = 'ZP'", nativeQuery = true)
    List<UrsoSubjectStack> findAllZpWhereStavEqualsPrebieha();

    boolean existsByRequestId(Long requestId);
}
