package sk.is.urso.plugin;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import sk.is.urso.common.regconfig.plugin.v1.RegisterPluginConfig;
import sk.is.urso.common.regconfig.v1.RegisterPlugin;
import sk.is.urso.plugin.entity.RpoReg1DataEntity;
import sk.is.urso.plugin.entity.RpoReg1DataHistoryEntity;
import sk.is.urso.plugin.entity.RpoReg1DataReferenceEntity;
import sk.is.urso.plugin.entity.RpoReg1IndexEntity;
import sk.is.urso.plugin.entity.RpoReg1NaturalIdEntity;
import sk.is.urso.plugin.repository.RpoReg1DataHistoryRepository;
import sk.is.urso.plugin.repository.RpoReg1DataReferenceRepository;
import sk.is.urso.plugin.repository.RpoReg1DataRepository;
import sk.is.urso.plugin.repository.RpoReg1IndexRepository;
import sk.is.urso.plugin.repository.RpoReg1NaturalIdRepository;
import sk.is.urso.reg.AbstractRegPlugin;

@Getter
public class RpoReg1 extends AbstractRegPlugin {

    public static final String REGISTER_ID = "RPO";
    public static final String IDENTIFIER_VALUE = "identifierValue";

    @Autowired
    RpoReg1DataRepository dataRepo;
    @Autowired
    RpoReg1IndexRepository indexRepo;
    @Autowired
    RpoReg1DataReferenceRepository dataReferenceRepo;
    @Autowired
    RpoReg1DataHistoryRepository dataHistoryRepo;
    @Autowired
    RpoReg1NaturalIdRepository naturalIdRepository;

    public RpoReg1(RegisterPlugin info, RegisterPluginConfig plugin) {
        super(info, plugin, RpoReg1DataEntity.class, RpoReg1IndexEntity.class, RpoReg1DataReferenceEntity.class, RpoReg1DataHistoryEntity.class, RpoReg1NaturalIdEntity.class);
    }

    @Override
    public RpoReg1DataRepository getDataRepository() {
        return this.dataRepo;
    }

    @Override
    public RpoReg1IndexRepository getIndexRepository() {
        return this.indexRepo;
    }

    @Override
    public RpoReg1DataReferenceRepository getDataReferenceRepository() {
        return this.dataReferenceRepo;
    }

    @Override
    public RpoReg1DataHistoryRepository getDataHistoryRepository() {
        return this.dataHistoryRepo;
    }

    @Override
    public RpoReg1NaturalIdRepository getNaturalIdRepository() {
        return this.naturalIdRepository;
    }
}
