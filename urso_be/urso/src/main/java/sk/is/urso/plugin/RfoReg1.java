package sk.is.urso.plugin;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import sk.is.urso.reg.AbstractRegPlugin;
import sk.is.urso.plugin.entity.*;
import sk.is.urso.plugin.repository.*;
import sk.is.urso.common.regconfig.plugin.v1.RegisterPluginConfig;
import sk.is.urso.common.regconfig.v1.RegisterPlugin;

@Getter
public class RfoReg1 extends AbstractRegPlugin {
	
	@Autowired
	RfoReg1DataRepository dataRepo;
	@Autowired
	RfoReg1IndexRepository indexRepo;
	@Autowired
	RfoReg1DataReferenceRepository dataReferenceRepo;
	@Autowired
	RfoReg1DataHistoryRepository dataHistoryRepo;
	@Autowired
	RfoReg1NaturalIdRepository naturalIdRepository;
	
	public RfoReg1(RegisterPlugin info, RegisterPluginConfig plugin) {
		super(info, plugin, RfoReg1DataEntity.class, RfoReg1IndexEntity.class, RfoReg1DataReferenceEntity.class, RfoReg1DataHistoryEntity.class, RfoReg1NaturalIdEntity.class);
	}

	@Override
	public RfoReg1DataRepository getDataRepository() {
		return this.dataRepo;
	}

	@Override
	public RfoReg1IndexRepository getIndexRepository() {
		return this.indexRepo;
	}

	@Override
	public RfoReg1DataReferenceRepository getDataReferenceRepository() {
		return this.dataReferenceRepo;
	}

	@Override
	public RfoReg1DataHistoryRepository getDataHistoryRepository() {
		return this.dataHistoryRepo;
	}
	
	@Override
	public RfoReg1NaturalIdRepository getNaturalIdRepository() {
		return this.naturalIdRepository;
	}
}
