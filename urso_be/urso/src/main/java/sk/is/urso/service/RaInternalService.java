package sk.is.urso.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.is.urso.model.RaInternalInicialneZaznamy;
import sk.is.urso.repository.RaInternalInicialneZaznamyRepository;

import java.util.List;

@Service
public class RaInternalService {

    @Autowired
    private RaInternalInicialneZaznamyRepository raInternalInitializedrecordsRepository;

    public void increaseCount() {
        List<RaInternalInicialneZaznamy> list = findAll();
        RaInternalInicialneZaznamy raInternalInicialneZaznamy;
        if (!list.isEmpty()) {
            raInternalInicialneZaznamy = list.get(0);
            raInternalInicialneZaznamy.setPocet(raInternalInicialneZaznamy.getPocet() + 1L);
        } else {
            raInternalInicialneZaznamy = new RaInternalInicialneZaznamy();
            raInternalInicialneZaznamy.setPocet(1L);
        }
        save(raInternalInicialneZaznamy);
    }

    public List<RaInternalInicialneZaznamy> findAll() {
        return raInternalInitializedrecordsRepository.findAll();
    }

    public void save(RaInternalInicialneZaznamy raInternalInicialneZaznamy) {
        raInternalInitializedrecordsRepository.save(raInternalInicialneZaznamy);
    }
}
