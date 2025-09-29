package sk.is.urso.reg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
@Repository
public interface PluginRepository <T, K> extends JpaRepository<T, K>, JpaSpecificationExecutor<T> {
}
