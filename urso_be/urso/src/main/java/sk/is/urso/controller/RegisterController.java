package sk.is.urso.controller;

import io.swagger.annotations.ApiParam;
import org.alfa.exception.IException;
import org.alfa.service.ListRequestService;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import sk.is.urso.common.regconfig.plugin.v1.RegisterEntryField;
import sk.is.urso.common.regconfig.plugin.v1.XsdFile;
import sk.is.urso.config.Registers;
import sk.is.urso.reg.AbstractRegPlugin;
import sk.is.urso.reg.model.RegisterId;
import sk.is.urso.rest.api.RegisterApi;
import sk.is.urso.rest.model.FormioSchemaTyp;
import sk.is.urso.rest.model.RegisterDetailGdpr;
import sk.is.urso.rest.model.RegisterList;
import sk.is.urso.rest.model.RegisterListRequest;
import sk.is.urso.rest.model.RegisterListRequestFilter;
import sk.is.urso.rest.model.RegisterOutputDetail;
import sk.is.urso.service.RegisterService;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@RestController
public class RegisterController implements RegisterApi, IException {

	private static final String FORMIO_SCHEMAS = "formio_schemas";
	private static final String JSON_EXTENSION = ".json";

	@Autowired
	private RegisterService registerService;

	@Autowired
	private ListRequestService listRequestService;

	@Autowired
	private Registers registers;

	@Value("${registers-file-path}")
	private String registersFilePath;

	@Value("${tmp-file-path}")
	private String tmpPath;

	private final ModelMapper modelMapper = new ModelMapper();

	/**
	 * Služba vyhľadá registre podľa zadaného filtra
	 *
	 * @param request filter
	 * @return {@link RegisterList} zoznam vyhľadaných registrov
	 */
	@Override
	public ResponseEntity<RegisterList> registerFilterPost(RegisterListRequest request) {
		return listRequestService.filter(request,
										 RegisterListRequestFilter.class,
										 (f, lr) -> registerService.findAll(f, lr, registers));
	}

	/**
	 * Služba vyhľadá verziu registra na základe zadaných údajov.
	 *
	 * @param registerId Id registra (required)
	 * @param verziaRegistraId Id verzie registra (required)
	 * @return {@link RegisterOutputDetail} detail vyhľadaného registra
	 */
	@Override
	public ResponseEntity<RegisterOutputDetail> registerRegisterIdVerziaRegistraIdGet(String registerId, Integer verziaRegistraId) {

		try {
			final RegisterId register = new RegisterId(registerId, verziaRegistraId);
			final AbstractRegPlugin plugin = registers.getPlugin(register);

			RegisterOutputDetail registerOutputDetail = modelMapper.map(registerService.prepareRegister(plugin), RegisterOutputDetail.class);
			for (RegisterEntryField entryField : plugin.getPluginConfig().getField()) {
				registerOutputDetail.addPoliaItem(registerService.prepareRegisterField(entryField));
			}
			registerOutputDetail.setGdpr(modelMapper.map(plugin.getPluginConfig().getGdprParams(), RegisterDetailGdpr.class));
			registerOutputDetail.setGdprRelevantny(plugin.getPluginConfig().getGdprParams().isIsGdprRelevant());

			return new ResponseEntity<>(registerOutputDetail, HttpStatus.OK);

		} catch(Exception ex) {
			throw toException("Požiadavku nebolo možné spracovať", ex);
		}
	}

	/**
	 * Služba vytvorí XSD pre definíciu XML dát.
	 *
	 * @param registerId Id registra (required)
	 * @param verziaRegistraId Id verzie registra (required)
	 * @return {@link Resource} XSD pre definíciu XML dát
	 */
	@Override
	public ResponseEntity<Resource> registerRegisterIdVerziaRegistraIdXsdGet(String registerId, Integer verziaRegistraId) {

		try {
			final RegisterId register = new RegisterId(registerId, verziaRegistraId);
			final String subject = register.toString();
			final AbstractRegPlugin plugin = registers.getPlugin(register);

			List<XsdFile> xsdFiles = plugin.getPluginConfig().getXsdFile().stream().filter(XsdFile::isIsExternal).collect(Collectors.toList());

			if (xsdFiles.size() == 1) {
				return  new ResponseEntity<>(new FileSystemResource(tmpPath + File.separator + xsdFiles.get(0).getPath()), HttpStatus.OK);
			}

			File zipFile = registerService.createZipFile(subject, registersFilePath, tmpPath, xsdFiles);

			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.set("Content-Disposition", "attachment; filename="+ zipFile.getName());
			httpHeaders.set("Content-Type", "application/zip");
			return new ResponseEntity<>(new FileSystemResource(tmpPath + File.separator + zipFile.getName()), httpHeaders, HttpStatus.OK);

		} catch(Exception ex) {
			throw toException("Chyba pri vyhľadávaní xsd schémy registra", ex);
		}
	}

	/**
	 * Služba vytvorí formio schému pre definíciu formio JSON dát.
	 *
	 * @param registerId Id registra (required)
	 * @param verziaRegistraId Id verzie registra (required)
	 * @param formioSchemaTyp Typ formio schémy na generovanie (create, read, update) (required)
	 * @return formio schéma
	 */
	@Override
	public ResponseEntity<Object> registerRegisterIdVerziaRegistraIdFormioFormioSchemaTypGet(String registerId, Integer verziaRegistraId, @ApiParam(value = "Type of formio schema to generate - for create, read or update", required=true, allowableValues = "CREATE, READ, UPDATE") FormioSchemaTyp formioSchemaTyp) {
		try {
			final RegisterId register = new RegisterId(registerId, verziaRegistraId);
			final AbstractRegPlugin plugin = registers.getPlugin(register);

			ExecutorService es = Executors.newFixedThreadPool(1);

			File file = new File(tmpPath + File.separator + FORMIO_SCHEMAS + File.separator + plugin.getFullInternalRegisterId() + "_" + FormioSchemaTyp.CREATE + JSON_EXTENSION);
			Future<String> future =  es.submit(() -> FileUtils.readFileToString(file, StandardCharsets.UTF_8));

			String schema;
			try{
				schema = future.get(30, TimeUnit.SECONDS);
			} catch(TimeoutException e) {
				future.cancel(true);
				throw toException("Požiadavka trvá príliš dlho!", e);
			}

			JSONObject jsonObject = new JSONObject(schema);
			return new ResponseEntity<>(jsonObject.toMap(), HttpStatus.OK);

		} catch(Exception ex) {
			throw toException("Chyba pri generovaní formio schémy registra", ex);
		}
	}

	@Override
	public Optional<NativeWebRequest> getRequest() {
		return RegisterApi.super.getRequest();
	}
}
