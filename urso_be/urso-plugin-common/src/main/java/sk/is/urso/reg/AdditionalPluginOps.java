package sk.is.urso.reg;

import org.alfa.exception.CommonException;
import org.alfa.model.UserInfo;
import org.springframework.http.ResponseEntity;
import sk.is.urso.reg.model.ZaznamRegistraInputDetail;
import sk.is.urso.reg.model.ZaznamRegistraOutputDetail;
import sk.is.urso.reg.model.ZaznamRegistraReferencia;

import java.util.List;

/**
 * Inštancia tohto interface sa posiela do {@link AbstractRegPlugin} aby sa dali volať ďalšie operácie ktoré sú závislé na službách mimo pluginu.
 */
public interface AdditionalPluginOps {

	AbstractRegPlugin getPlugin(String registerId, Integer registerVersionId) throws CommonException;

	List<AbstractRegPlugin> getRegisterPlugins();

	ResponseEntity<ZaznamRegistraOutputDetail> zaznamRegistraPost(ZaznamRegistraInputDetail zaznamRegistraInputDetail, UserInfo userInfo);

	ResponseEntity<ZaznamRegistraOutputDetail> zaznamRegistraPut(ZaznamRegistraInputDetail zaznamRegistraInputDetail, UserInfo userInfo);

	ResponseEntity<ZaznamRegistraOutputDetail> zaznamRegistraRegisterIdVerziaRegistraIdZaznamIdGet(String registerId, Integer verziaRegistraId, Long zaznamId, UserInfo userInfo);

	ResponseEntity<ZaznamRegistraReferencia> zaznamRegistraReferenciaRegisterIdVerziaRegistraIdZaznamIdModulIdPost(String registerId, Integer verziaRegistraId, Long zaznamId, String modulId);

	ResponseEntity<ZaznamRegistraReferencia> zaznamRegistraReferenciaRegisterIdVerziaRegistraIdZaznamIdModulIdPut(String registerId, Integer verziaRegistraId, Long zaznamId, String modulId);
}
