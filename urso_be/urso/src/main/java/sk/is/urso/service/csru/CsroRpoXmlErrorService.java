package sk.is.urso.service.csru;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sk.is.urso.model.csru.CsroRpoXmlError;
import sk.is.urso.repository.csru.CsroRpoXmlErrorRepository;

import java.sql.Timestamp;

@Service
public class CsroRpoXmlErrorService {

    @Autowired
    CsroRpoXmlErrorRepository csroRpoXmlErrorRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CsroRpoXmlError saveErrorXml(String xmlName) {
        CsroRpoXmlError csroRpoXmlError = new CsroRpoXmlError();
        csroRpoXmlError.setXmlName(xmlName);
        csroRpoXmlError.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        return csroRpoXmlErrorRepository.save(csroRpoXmlError);
    }
}
