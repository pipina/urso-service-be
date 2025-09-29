package sk.is.urso.service.csru;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sk.is.urso.model.csru.CsruChange;
import sk.is.urso.repository.csru.CsruChangeRepository;
import sk.is.urso.rest.model.CsruResultStatusEnum;

import java.sql.Timestamp;
import java.util.Date;

@Service
public class CsruChangeService {

    @Autowired
    CsruChangeRepository csruChangeRepository;

    public CsruChange save(CsruChange csruChange) {
        return csruChangeRepository.save(csruChange);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createErrorChange(CsruChange csruChange, String type, String error) {
        csruChange.setEnd(new Timestamp(System.currentTimeMillis()));
        csruChange.setType(type);
        csruChange.setResultStatus(CsruResultStatusEnum.ERROR.getValue());
        csruChange.setErrorMsg(error);
        csruChangeRepository.save(csruChange);
    }

    public void createOkChange(CsruChange csruChange, Integer processedItems) {
        if (csruChange.getProcessedItems() == null) {
            csruChange.setProcessedItems(processedItems);
        }
        csruChange.setEnd(new Timestamp(System.currentTimeMillis()));
        csruChange.setResultStatus(CsruResultStatusEnum.OK.getValue());
        csruChangeRepository.save(csruChange);
    }

    public CsruChange initialChange(String type, Date dateFrom, Date dateTo) {
        CsruChange csruChange = new CsruChange();
        csruChange.setDateFrom(dateFrom);
        csruChange.setDateTo(dateTo);
        csruChange.setType(type);
        return csruChangeRepository.save(csruChange);
    }
}
