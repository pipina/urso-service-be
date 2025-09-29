package sk.is.urso.util;

import lombok.SneakyThrows;
import net.javacrumbs.shedlock.core.LockProvider;
import org.alfa.exception.CommonException;
import org.alfa.exception.IException;
import org.alfa.utils.XmlUtils;
import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import sk.is.urso.config.EncryptionConfig;
import sk.is.urso.csru.fo.EncryptedDataType;
import sk.is.urso.model.csru.api.sync.common.DataPlaceholderCType;

import javax.crypto.Cipher;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBElement;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

@Component
public class EncryptionUtils implements IException {

//    private PublicKey publicKey;
//    private PrivateKey privateKey;

    @Autowired
    EncryptionConfig encryptionConfig;


//    @Autowired
//    @Qualifier("getMapper")
//    private ModelMapper modelMapper;
//
//    @Autowired
//    @Qualifier("publicKey")
//    private PublicKey publicKey;
//
//    @Autowired
//    @Qualifier("privateKey")
//    private PrivateKey privateKey;

    private final ThreadLocal<Cipher> encryptCipher = new ThreadLocal<>(){
        @SneakyThrows
        @Override
        protected Cipher initialValue()
        {
            Cipher cipher = Cipher.getInstance(EncryptionConfig.ALGORITHM);
//            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            cipher.init(Cipher.ENCRYPT_MODE, encryptionConfig.getPublicKey());
            return cipher;
        }
    };

    public byte[] rsaEncrypt(String input) {
        try {

            byte[] inputArray = input.getBytes();
            int inputLength = inputArray.length;
            int maxEncryptBlock = 245;
            int offSet = 0;
            byte[] resultBytes = {};
            byte[] cache;

            while (inputLength - offSet > 0) {
                if (inputLength - offSet > maxEncryptBlock) {
                    cache = encryptCipher.get().doFinal(inputArray, offSet, maxEncryptBlock);
                    offSet += maxEncryptBlock;
                } else {
                    cache = encryptCipher.get().doFinal(inputArray, offSet, inputLength - offSet);
                    offSet = inputLength;
                }
                resultBytes = Arrays.copyOf(resultBytes, resultBytes.length + cache.length);
                System.arraycopy(cache, 0, resultBytes, resultBytes.length - cache.length, cache.length);
            }
            return resultBytes;

        } catch (Exception ex) {
            throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, "Chyba pri šifrovaní dát", ex);
        }
    }

    public Document decrypt(String xml) throws Exception {

        org.apache.xml.security.Init.init();
        PrivateKey rsaKey = encryptionConfig.getPrivateKey();
        Document doc = XmlUtils.parse(xml);

        Element ekEl = (Element) doc.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptedKey").item(0);

        Element edEl = (Element) doc.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptedData").item(0);
        ((Element) edEl.getParentNode()).setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:env", "http://schemas.xmlsoap.org/soap/envelope/");
        ((Element) edEl.getParentNode()).setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        ((Element) edEl.getParentNode()).setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        ((Element) edEl.getParentNode()).setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:soap", "http://www.w3.org/2003/05/soap-envelope");

        XMLCipher cipher2 = XMLCipher.getInstance();
        cipher2.init(XMLCipher.DECRYPT_MODE, null);
        EncryptedData ed = cipher2.loadEncryptedData(doc, edEl);

        XMLCipher cipher = XMLCipher.getInstance();
        cipher.init(XMLCipher.UNWRAP_MODE, rsaKey);
        EncryptedKey encryptedKey = cipher.loadEncryptedKey(doc, ekEl);

        Key symmetricKey = cipher.decryptKey(encryptedKey, ed.getEncryptionMethod().getAlgorithm());
        cipher2.init(XMLCipher.DECRYPT_MODE, symmetricKey);
        return cipher2.doFinal(doc, edEl);
    }



    private static final String MOCK_RESPONSE = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><Payload><EncryptedData xmlns=\"http://www.w3.org/2001/04/xmlenc#\" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:ns3=\"http://www.egov.sk/mvsr/Ekr/Podanie.1.0\" xmlns:ns4=\"http://www.egov.sk/mvsr/Ekr/Zasielka.1.0\" Type=\"http://www.w3.org/2001/04/xmlenc#Element\">\n" +
            "    <EncryptionMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#aes128-cbc\"/>\n" +
            "    <ns2:KeyInfo>\n" +
            "        <EncryptedKey>\n" +
            "            <EncryptionMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p\"/>\n" +
            "            <ns2:KeyInfo>\n" +
            "                <ns2:X509Data>\n" +
            "                    <ns2:X509IssuerSerial>\n" +
            "                        <ns2:X509IssuerName>CN=b2burso2testrfo</ns2:X509IssuerName>\n" +
            "                        <ns2:X509SerialNumber>622610353523491623209006754760902183474996463936</ns2:X509SerialNumber>\n" +
            "                    </ns2:X509IssuerSerial>\n" +
            "                </ns2:X509Data>\n" +
            "            </ns2:KeyInfo>\n" +
            "            <CipherData>\n" +
            "                <CipherValue>kLDp9AMsG6sbwlDEXNryKq8w3t8paZAKZsfYKuSVyPdhQkauqZ0Umr3QwbmSzfFswlkiP2NaPMqVm7YkWLR/aPhKweyvnHZMq7gNRQkOpadTNWHxFWK3l7LfbSabnp9eyuizt0b/2fDSrxcsA6GslPDN5UTPVO/jntyC2z6bRUNYAWBgfO0FQABN/85ecKm+b/HeLkcjxIufV2GW4eawG525pXnaMnexcIoGEY6c89Mqor7REcICkwV+yyQN8ZTfwkQUSqYzE4q9OFmLJ6rMTnMgSw2Z5+kqDE4SZvzTXg0fiY+hkOCp06GeK13a8K213ktz/+h70ceVVIIQE0UOSQ==</CipherValue>\n" +
            "            </CipherData>\n" +
            "        </EncryptedKey>\n" +
            "    </ns2:KeyInfo>\n" +
            "    <CipherData>\n" +
            "        <CipherValue>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</CipherValue>\n" +
            "    </CipherData>\n" +
            "</EncryptedData></Payload>";

    public Document decryptResponse(DataPlaceholderCType dataPlaceholder) {
        try {
        	if(dataPlaceholder==null)
        		return null;
        	
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document doc = dbf.newDocumentBuilder().newDocument();
            Node rootNode = doc.createElement("Payload");

            String xml = XmlUtils.xmlToString((JAXBElement<EncryptedDataType>) dataPlaceholder.getAny());
            Element xmlNode = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .parse(new ByteArrayInputStream(xml.getBytes()))
                    .getDocumentElement();

            rootNode.appendChild(doc.importNode(xmlNode, true));
            doc.appendChild(rootNode);
            return decrypt(XmlUtils.xmlToString(doc));
//            return decrypt(MOCK_RESPONSE);
        }
        catch (Exception ex){
            throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, "Chyba pri dešifrovaní dát", ex);
        }
    }
}
