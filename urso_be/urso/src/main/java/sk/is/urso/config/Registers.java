package sk.is.urso.config;

import lombok.AllArgsConstructor;
import lombok.ToString;
import org.alfa.exception.CommonException;
import org.springframework.http.HttpStatus;
import sk.is.urso.reg.AbstractRegPlugin;
import sk.is.urso.reg.model.RegisterId;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Obsahuje zoznam pluginov registrov a operacie nad tymto zoznamom a pluginmi
 */
@AllArgsConstructor
@ToString
public class Registers implements Iterable<AbstractRegPlugin> {

	private final Map<RegisterId, AbstractRegPlugin> registerPlugins;

	/**
	 * Vrati plugin pre dane id a verziu registra
	 * 
	 * @param reg id a verzia registra
	 * @return plugin pre dane id a verziu registra
	 * @throws CommonException ak register pre dane id a verziu neexistuje!
	 */
	public AbstractRegPlugin getPlugin(RegisterId reg) throws CommonException {
		AbstractRegPlugin plugin = this.registerPlugins.get(reg);
		if (plugin == null) {
			throw new CommonException(HttpStatus.NOT_FOUND, "Register s id = " + reg.getRegisterId() + ", verzia = " + reg.getVerziaRegistraId() + " neexistuje", null);
		}
		return plugin;
	}

	@Override
	public Iterator<AbstractRegPlugin> iterator() {
		return this.registerPlugins.values().iterator();
	}

	/**
	 * Returns set of ids for all registers
	 * @return  set of ids for all registers
	 */
	public Set<RegisterId> getIdSet() {
		return this.registerPlugins.keySet();
	}

	public List<AbstractRegPlugin> getPlugins() {
		return new ArrayList<>(this.registerPlugins.values());
	}

}
