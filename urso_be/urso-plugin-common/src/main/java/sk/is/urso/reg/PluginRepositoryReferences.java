package sk.is.urso.reg;


public interface PluginRepositoryReferences <T, K> extends PluginRepository<T,K> {

    boolean existsByZaznamIdAndPocetReferenciiGreaterThan(AbstractRegEntityData abstractRegEntityData, int pocet);
}
