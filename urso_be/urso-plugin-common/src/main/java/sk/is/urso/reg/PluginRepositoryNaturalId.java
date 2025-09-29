package sk.is.urso.reg;

public interface PluginRepositoryNaturalId <T extends AbstractRegEntityNaturalId> extends PluginRepository<T, String> {
    void deleteByZaznamIdId(Long zaznamId);
}
