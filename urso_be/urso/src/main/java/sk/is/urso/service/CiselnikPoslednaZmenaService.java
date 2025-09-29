package sk.is.urso.service;

import org.alfa.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import sk.is.urso.model.CiselnikPoslednaZmena;
import sk.is.urso.repository.CiselnikPoslednaZmenaRepository;

import java.util.Date;
import java.util.List;

@Service
public class CiselnikPoslednaZmenaService {

    @Autowired
    CiselnikPoslednaZmenaRepository ciselnikPoslednaZmenaRepository;

    public void save(String codelistCode, Date lastChange) {
        CiselnikPoslednaZmena ciselnikPoslednaZmena = new CiselnikPoslednaZmena();
        ciselnikPoslednaZmena.setKodCiselnika(codelistCode);
        if (lastChange != null) {
            ciselnikPoslednaZmena.setPoslednaZmena(lastChange);
        } else {
            ciselnikPoslednaZmena.setPoslednaZmena(new Date());
        }
        ciselnikPoslednaZmenaRepository.save(ciselnikPoslednaZmena);
    }

    public List<CiselnikPoslednaZmena> findAll() {
        return ciselnikPoslednaZmenaRepository.findAll();
    }

    public Date findLastDate() {
        List<CiselnikPoslednaZmena> enumerationLastChanges = ciselnikPoslednaZmenaRepository.findAll(Sort.by(Sort.Direction.ASC, "lastChange"));
        return enumerationLastChanges.size() > 0 ? enumerationLastChanges.get(0).getPoslednaZmena() : DateUtils.yesterdayDate();
    }

    public CiselnikPoslednaZmena findById(String codelistCode) {
        return ciselnikPoslednaZmenaRepository.findById(codelistCode).orElse(null);
    }
}
