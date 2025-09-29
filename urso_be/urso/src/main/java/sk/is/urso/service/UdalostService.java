package sk.is.urso.service;

import org.alfa.exception.IException;
import org.alfa.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sk.is.urso.model.Udalost;
import sk.is.urso.repository.UdalostRepository;
import sk.is.urso.rest.model.UdalostDomenaEnum;
import sk.is.urso.rest.model.UdalostKategoriaEnum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UdalostService implements IException {

	@Autowired
	private UdalostRepository udalostRepository;

	@Autowired
	private UserInfoService userInfoService;

	public Optional<Udalost> findById(Long id) {
		return udalostRepository.findById(id);
	}
	
	public Udalost save(Udalost udalost) {
		return udalostRepository.save(udalost);
	}

	public Udalost update(Udalost udalost) {
		return udalostRepository.save(udalost);
	}
	
	public void delete (Udalost udalost) {
		udalostRepository.delete(udalost);
	}
	
	public Page<Udalost> findAll(Specification<Udalost> specification, PageRequest pageRequest){
		return udalostRepository.findAll(specification, pageRequest);
	}
	
	public List<Udalost> findAll(Specification<Udalost> specification){
		return udalostRepository.findAll(specification);
	}

	/**
	 * Vytvorí a uloží event s danou doménou, kategóriou. 
	 * User sa zoberie z requestu
	 * @param UdalostDomenaEnum doména eventu
	 * @param UdalostKategoriaEnum kategória eventu
	 * @return vytvorený a uložený event
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Udalost createEvent(UdalostDomenaEnum UdalostDomenaEnum, UdalostKategoriaEnum UdalostKategoriaEnum) {
		return createEventInternal(UdalostDomenaEnum, UdalostKategoriaEnum, userInfoService.getUserInfo().getLogin());
	}
	
	/**
	 * Vytvorí a uloží event s danou doménou, kategóriou a userom
	 * @param UdalostDomenaEnum doména eventu
	 * @param UdalostKategoriaEnum kategória eventu
	 * @param login user login
	 * @return vytvorený a uložený event
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Udalost createEvent(UdalostDomenaEnum UdalostDomenaEnum, UdalostKategoriaEnum UdalostKategoriaEnum, String login) {
		return createEventInternal(UdalostDomenaEnum, UdalostKategoriaEnum, login);
	}
	
	/*
	 * Implementovane kvoli java:S2229 zo SonarQube
	 */
	private Udalost createEventInternal(UdalostDomenaEnum UdalostDomenaEnum, UdalostKategoriaEnum UdalostKategoriaEnum, String login) {
		Udalost udalost = new Udalost();
		udalost.setPouzivatel(login);
//		udalost.setDomena(UdalostDomenaEnum.getValue()); TODO
//		udalost.setKategoria(UdalostKategoriaEnum.getValue());
		udalost.setDatumCasVytvorenia(LocalDateTime.now());
		return save(udalost);
	}

	/**
	 * Update event description and success
	 * 
	 * @param udalost   event to update
	 * @param message if null, event is considered succesfull, if not null, event is
	 *                considered not succesfull!
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateEvent(Udalost udalost, String message) {
		if (message == null) {
			udalost.setUspesna(true);
		} else {
			udalost.setUspesna(false);
			udalost.setPopis(message);
		}
		// we keep timestamp from event creation //event.setTimestamp(new
		// Timestamp(System.currentTimeMillis()));
		update(udalost);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateEventAndSuccess(Udalost udalost, String message) {
		udalost.setUspesna(true);
		udalost.setPopis(message);
		update(udalost);
	}
}