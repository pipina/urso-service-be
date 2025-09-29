package sk.is.urso.service;

import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Component
public class InitializeRa extends AbstractInitialize {

    public static final String RA_INTERNAL_1 = "ra_internal_1";
    public int initializedFilesNum = 0;
    public int initializedRecordsNumPerFile = 0;
    public int initializedRecordsAll = 0;

    public static final String FILE_REGISTERS_CONFIGURATION = "registersConfig_v1.xml";
    public static final String XML_EXTENSION = ".xml";

    public InitializeRa(Initialize initialize) {
        super(initialize);
    }

    public void initialize(ZipFile zip) throws IOException, SAXException, ParserConfigurationException {
        Initialize.log.info("Initializing register RA_INTERNAL in CSV mode.");
        prepareCsvFiles(RA_INTERNAL_1);

        Enumeration<? extends ZipEntry> entries = zip.entries();
        var factory = SAXParserFactory.newInstance();
        var saxParser = factory.newSAXParser();
        var registerRaHandler = new RegisterRaHandler(this);
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            var xmlStream = zip.getInputStream(entry);
            try {
                initialize.allCount++;
                processRaDocument(saxParser, xmlStream, registerRaHandler, entry);
                initialize.successCount++;
            } catch (Exception e) {
                initialize.error("Error RA_INTERNAL.FileName = " + entry.getName().substring(entry.getName().indexOf("/") + 1) + "! "
                        + "Error stackTrace: " + Arrays.stream(e.getStackTrace()).findFirst() + ". Exception: ", e);
                e.printStackTrace();
                if (initialize.endAfterError) {
                    break;
                }
            }
        }
        closeCsvFiles();
    }

    private void processRaDocument(SAXParser saxParser, InputStream xmlStream, RegisterRaHandler registerRaHandler, ZipEntry entry) throws IOException, SAXException {
        saxParser.parse(xmlStream, registerRaHandler);
        xmlStream.close();

        Initialize.log.info("Initialized " + initializedFilesNum + " records from file " + entry.getName() + ".");
        initializedFilesNum = 0;
    }
}
