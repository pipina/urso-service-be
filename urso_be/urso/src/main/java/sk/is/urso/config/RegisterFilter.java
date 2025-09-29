package sk.is.urso.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;
import sk.is.urso.rest.model.Register;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.alfa.utils.SearchUtils.collator;

@Configuration
@Data
public class RegisterFilter {
	private static final String VERSION_ID = "versionId";
	private static final String REGISTER_ID = "registerId";
	private static final String NAME = "name";
	private static final String DESCRIPTION = "description";
	private static final String IS_EXTERNAL = "isExternal";
	private static final String IS_VALIDATED = "isValidated";
	private static final String IS_IDENTIFICATION_USED = "isIdentificationUsed";
	private static final String IS_GDPR_RELEVANT = "isGdprRelevant";
	private static final String IS_ENABLED = "isEnabled";
	private static final String EFFECTIVE_FROM = "effectiveFrom";
	private static final String EFFECTIVE_TO = "effectiveTo";
	private static final String REGID_VERID = "regid_verid";

	final Map<String, Function<List<Register>,List<Register>>> ascRegisterSorting = new HashMap<>();
	final Map<String, Function<List<Register>,List<Register>>> descRegisterSorting = new HashMap<>();
	{
		ascRegisterSorting.put(VERSION_ID, list -> list.stream().sorted(Comparator.comparing(Register::getVerziaId)).collect(Collectors.toList()));
		ascRegisterSorting.put(REGISTER_ID, list -> {
			list.sort((v1, v2) -> collator.compare(v1.getRegisterId().toLowerCase(), v2.getRegisterId().toLowerCase()));
			return list;
		});
		ascRegisterSorting.put(NAME, list -> {
			list.sort((v1, v2) -> collator.compare(v1.getNazov().toLowerCase(), v2.getNazov().toLowerCase()));
			return list;
		});
		ascRegisterSorting.put(DESCRIPTION, list -> {
			list.sort((v1, v2) -> collator.compare(v1.getPopis().toLowerCase(), v2.getPopis().toLowerCase()));
			return list;
		});
		ascRegisterSorting.put(IS_EXTERNAL, list -> list.stream().sorted(Comparator.comparing(Register::getExterny)).collect(Collectors.toList()));
		ascRegisterSorting.put(IS_VALIDATED, list -> list.stream().sorted(Comparator.comparing(Register::getOvereny)).collect(Collectors.toList()));
		ascRegisterSorting.put(IS_IDENTIFICATION_USED, list -> list.stream().sorted(Comparator.comparing(Register::getIdentifikovany)).collect(Collectors.toList()));
		ascRegisterSorting.put(IS_GDPR_RELEVANT, list -> list.stream().sorted(Comparator.comparing(Register::getGdprRelevantny)).collect(Collectors.toList()));
		ascRegisterSorting.put(IS_ENABLED, list -> list.stream().sorted(Comparator.comparing(Register::getPovoleny)).collect(Collectors.toList()));
		ascRegisterSorting.put(EFFECTIVE_FROM, list -> list.stream().sorted(Comparator.comparing(Register::getPlatnostOd)).collect(Collectors.toList()));
		ascRegisterSorting.put(EFFECTIVE_TO, list -> {
			List<Register> registers;
			registers = list.stream().filter(register -> register.getPlatnostDo() != null).collect(Collectors.toList());
			List<Register> nullRegisters;
			nullRegisters = list.stream().filter(register -> register.getPlatnostDo() == null).collect(Collectors.toList());
			registers = registers.stream().sorted(Comparator.comparing(Register::getPlatnostDo)).collect(Collectors.toList());
			registers.addAll(nullRegisters);
			return registers;
		});
		ascRegisterSorting.put(REGID_VERID, list -> {
			list.sort((v1, v2) -> collator.compare(v1.getRegisterId().concat("_").concat(v1.getVerziaId().toString()), v2.getRegisterId().concat("_").concat(v2.getVerziaId().toString())));
			return list;
		});
		
		descRegisterSorting.put(VERSION_ID, list -> list.stream().sorted(Comparator.comparing(Register::getVerziaId).reversed()).collect(Collectors.toList()));
		descRegisterSorting.put(REGISTER_ID, list -> {
			list.sort((v1, v2) -> collator.compare(v2.getRegisterId().toLowerCase(), v1.getRegisterId().toLowerCase()));
			return list;
		});
		descRegisterSorting.put(NAME, list -> {
			list.sort((v1, v2) -> collator.compare(v2.getNazov().toLowerCase(), v1.getNazov().toLowerCase()));
			return list;
		});
		descRegisterSorting.put(DESCRIPTION, list -> {
			list.sort((v1, v2) -> collator.compare(v2.getPopis().toLowerCase(), v1.getPopis().toLowerCase()));
			return list;
		});
		descRegisterSorting.put(IS_EXTERNAL, list -> list.stream().sorted(Comparator.comparing(Register::getExterny).reversed()).collect(Collectors.toList()));
		descRegisterSorting.put(IS_VALIDATED, list -> list.stream().sorted(Comparator.comparing(Register::getPovoleny).reversed()).collect(Collectors.toList()));
		descRegisterSorting.put(IS_IDENTIFICATION_USED, list -> list.stream().sorted(Comparator.comparing(Register::getIdentifikovany).reversed()).collect(Collectors.toList()));
		descRegisterSorting.put(IS_GDPR_RELEVANT, list -> list.stream().sorted(Comparator.comparing(Register::getGdprRelevantny).reversed()).collect(Collectors.toList()));
		descRegisterSorting.put(IS_ENABLED, list -> list.stream().sorted(Comparator.comparing(Register::getPovoleny).reversed()).collect(Collectors.toList()));
		descRegisterSorting.put(EFFECTIVE_FROM, list -> list.stream().sorted(Comparator.comparing(Register::getPlatnostOd).reversed()).collect(Collectors.toList()));
		descRegisterSorting.put(EFFECTIVE_TO, list -> {
			List<Register> registers;
			registers = list.stream().filter(register -> register.getPlatnostDo() != null).collect(Collectors.toList());
			List<Register> nullRegisters;
			nullRegisters = list.stream().filter(register -> register.getPlatnostDo() == null).collect(Collectors.toList());
			registers = registers.stream().sorted(Comparator.comparing(Register::getPlatnostDo).reversed()).collect(Collectors.toList());
			nullRegisters.addAll(registers);
			return nullRegisters;
		});
		descRegisterSorting.put(REGID_VERID, list -> {
			list.sort((v1, v2) -> collator.compare(v2.getRegisterId().concat("_").concat(v2.getVerziaId().toString()), v1.getRegisterId().concat("_").concat(v1.getVerziaId().toString())));
			return list;
		});
    }
}
