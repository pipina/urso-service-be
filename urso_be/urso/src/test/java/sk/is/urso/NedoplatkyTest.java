package sk.is.urso;

import org.alfa.utils.XmlUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;
import sk.is.urso.be.Application;
import sk.is.urso.config.csru.CsruEndpoint;
import sk.is.urso.config.timers.NedoplatkyTimer;
import sk.is.urso.config.timers.UrsoSubjectUpdateTimer;
import sk.is.urso.controller.NedoplatkyController;
import sk.is.urso.enums.CsruNavratovyKodOperacie;
import sk.is.urso.enums.CsruStavZiadosti;
import sk.is.urso.enums.SpNedoplatok;
import sk.is.urso.enums.UrsoNedoplatokTyp;
import sk.is.urso.enums.UrsoSubjectStav;
import sk.is.urso.enums.ZpNedoplatok;
import sk.is.urso.enums.ZpPoistovna;
import sk.is.urso.enums.ZpPopisKoduVysledkuSpracovania;
import sk.is.urso.model.FsOsobaZaznam;
import sk.is.urso.model.SpOsobaZaznam;
import sk.is.urso.model.SpStavZiadost;
import sk.is.urso.model.SpVysledokKontroly;
import sk.is.urso.model.SpVystupnySubor;
import sk.is.urso.model.UrsoSubjectStack;
import sk.is.urso.model.ZpOsobaZaznam;
import sk.is.urso.model.ZpStavZiadost;
import sk.is.urso.model.ZpVysledokKontroly;
import sk.is.urso.model.ZpVystupnySubor;
import sk.is.urso.model.csru.api.async.GetConsolidatedDataServiceAsync.GetStatusResponseCType;
import sk.is.urso.model.csru.api.sync.GetConsolidatedDataServiceSync.GetConsolidatedDataResponseCType;
import sk.is.urso.model.urso.SetDlznici;
import sk.is.urso.model.urso.SetDlzniciObdobie;
import sk.is.urso.model.urso.SetDlzniciRefresh;
import sk.is.urso.repository.FsOsobaZaznamRepository;
import sk.is.urso.repository.SpOsobaZaznamRepository;
import sk.is.urso.repository.SpStavZiadostRepository;
import sk.is.urso.repository.ZpOsobaZaznamRepository;
import sk.is.urso.repository.ZpStavZiadostRepository;
import sk.is.urso.repository.urso.SetDlzniciObdobieRepository;
import sk.is.urso.repository.urso.SetDlzniciRefreshRepository;
import sk.is.urso.repository.urso.SetDlzniciRepository;
import sk.is.urso.repository.urso.UrsoSubjectStackRepository;
import sk.is.urso.rest.model.CsruDruhDaneAleboPohladavkyEnum;
import sk.is.urso.rest.model.CsruNavratovyKodOperacieEnum;
import sk.is.urso.rest.model.CsruNedoplatokChybovyKodEnum;
import sk.is.urso.rest.model.CsruNedoplatokEnum;
import sk.is.urso.rest.model.CsruStavZiadostiEnum;
import sk.is.urso.rest.model.InstituciaEnum;
import sk.is.urso.rest.model.Nedoplatok;
import sk.is.urso.rest.model.SubjektNedoplatokVstupnyDetail;
import sk.is.urso.rest.model.SubjektVystupnyDetail;
import sk.is.urso.service.SftpService;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class NedoplatkyTest {

    public static final String FS = "<GetConsolidatedDataResponse xmlns=\"http://csru.gov.sk/csru_getconsolidateddata_sync/v1.4\" xmlns:ns2=\"http://csru.gov.sk/common/v1.4\" xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">\r\n            <csruTransactionId>65f8f5b2-664c-4c2e-afa3-c4ecdbfd9fa5</csruTransactionId>\r\n            <ovmTransactionId>7fce168b-b1a7-4741-946f-36d2bfa28sh5</ovmTransactionId>\r\n            <ovmCorrelationId>ff407e56-f101-4f41-9958-e442a19793l5</ovmCorrelationId>\r\n            <resultCode>0</resultCode>\r\n            <consolidatedData>\r\n                <FSDanoveNedoplatky xmlns=\"http://csru.gov.sk/oe_nedoplatky/v1.0\">\r\n                    <DanSubj>\r\n                        <DIC>2022606509</DIC>\r\n                        <ICO>44162588</ICO>\r\n                        <ObchMenoNazov>.. TOPRO s. r. o.</ObchMenoNazov>\r\n                        <AdresaTrvPobytuSidla>\r\n                            <Ulica>Wuppert\\u00E1lska</Ulica>\r\n                            <OrientacneSupisneCislo>55</OrientacneSupisneCislo>\r\n                            <Obec>Ko\\u0161ice - mestsk\\u00E1 \\u010Das\\u0165 S\\u00EDdlisko KVP</Obec>\r\n                            <Psc>04023</Psc>\r\n                            <Stat>SK</Stat>\r\n                        </AdresaTrvPobytuSidla>\r\n                        <DanNedoplatokPohladavka>\r\n                            <DruhPohladavky>\r\n                                <Typ>NDS</Typ>\r\n                            </DruhPohladavky>\r\n                            <Datum>2024-06-30</Datum>\r\n                            <Nedoplatok>1</Nedoplatok>\r\n                            <ErrorCode>0</ErrorCode>\r\n                        </DanNedoplatokPohladavka>\r\n                        <DanNedoplatokPohladavka>\r\n                            <DruhPohladavky>\r\n                                <Typ>SPD</Typ>\r\n                            </DruhPohladavky>\r\n                            <Datum>2024-06-30</Datum>\r\n                            <Nedoplatok>0</Nedoplatok>\r\n                            <ErrorCode>0</ErrorCode>\r\n                        </DanNedoplatokPohladavka>\r\n                        <DanNedoplatokPohladavka>\r\n                            <DruhPohladavky>\r\n                                <Typ>COL</Typ>\r\n                            </DruhPohladavky>\r\n                            <Datum>2024-06-30</Datum>\r\n                            <Nedoplatok>0</Nedoplatok>\r\n                            <ErrorCode>0</ErrorCode>\r\n                        </DanNedoplatokPohladavka>\r\n                    </DanSubj>\r\n                </FSDanoveNedoplatky>\r\n            </consolidatedData>\r\n        </GetConsolidatedDataResponse>";
    public static final String SP = "<GetConsolidatedDataResponse xmlns=\"http://csru.gov.sk/csru_getconsolidateddata/v1.4\" xmlns:ns2=\"http://csru.gov.sk/common/v1.4\" xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">\r\n            <csruTransactionId>cff8bf43-fd35-4471-802c-009479f693e4</csruTransactionId>\r\n            <ovmTransactionId>326c33a2-a605-4857-af5a-fe6c07de6874</ovmTransactionId>\r\n            <ovmCorrelationId>326c33a2-a605-4857-af5a-fe6c07Fe7984</ovmCorrelationId>\r\n            <resultCode>0</resultCode>\r\n            <requestId>16798002</requestId>\r\n        </GetConsolidatedDataResponse>";
    public static final String ZP = "<GetConsolidatedDataResponse xmlns=\"http://csru.gov.sk/csru_getconsolidateddata/v1.4\" xmlns:ns2=\"http://csru.gov.sk/common/v1.4\" xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">\r\n            <csruTransactionId>d21ee469-4769-4d8b-bc67-1395a09a1c6c</csruTransactionId>\r\n            <ovmTransactionId>326c33a2-a605-4857-af5a-fe6c07de6877</ovmTransactionId>\r\n            <ovmCorrelationId>326c33a2-a605-4857-af5a-fe6c07Fe7987</ovmCorrelationId>\r\n            <resultCode>0</resultCode>\r\n            <requestId>16798772</requestId>\r\n        </GetConsolidatedDataResponse>";
    public static final String SP_STAV_ZIADOSTI = "<GetProgressResponse xmlns=\"http://csru.gov.sk/csru_getconsolidateddata/v1.4\" xmlns:ns2=\"http://csru.gov.sk/common/v1.4\" xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">\r\n            <csruTransactionId>208e977b-1817-4fd9-8f6e-ce6969f74a6a</csruTransactionId>\r\n            <ovmTransactionId>b109407b-53a4-4934-803d-6ecc44cc05a4</ovmTransactionId>\r\n            <ovmCorrelationId>f4bc265c-a672-4e60-a6a2-4f36a865d5c7</ovmCorrelationId>\r\n            <resultCode>0</resultCode>\r\n            <status>1</status>\r\n            <fileList>\r\n                <ns2:file>\r\n                    <ns2:path>/out/SP_NEDOPLATKY_OVERSI_16798002_20240717114446.xml</ns2:path>\r\n                </ns2:file>\r\n            </fileList>\r\n        </GetProgressResponse>";
    public static final String ZP_STAV_ZIADOSTI = "<GetStatusResponse xmlns=\"http://csru.gov.sk/csru_getconsolidateddata/v1.4\" xmlns:ns2=\"http://csru.gov.sk/common/v1.4\" xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">\r\n            <csruTransactionId>0652bff7-0ff7-4732-9747-a954829d3729</csruTransactionId>\r\n            <ovmTransactionId>59fe6c7f-8ebd-48ff-a2f3-c8bfd39763b8</ovmTransactionId>\r\n            <ovmCorrelationId>e3cd6221-aedd-4872-a525-9e50781c921c</ovmCorrelationId>\r\n            <resultCode>0</resultCode>\r\n            <status>1</status>\r\n            <fileList>\r\n                <ns2:file>\r\n                    <ns2:path>/out/ZP_ODVODY_16798772_20240717135017.xml</ns2:path>\r\n                </ns2:file>\r\n            </fileList>\r\n        </GetStatusResponse>";
    public static final String SP_OBSAH_SUBORU = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<ns2:checkArrearsResultServiceRes xmlns:ns2=\"http://v1_0.result.check.arrears.service.integration.socpoist.datalan.sk\" xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\">\r\n  <requestId>3d30d26a-406f-4284-b985-f48a105a55a8</requestId>\r\n  <resultInfo>\r\n    <csruStatus>0</csruStatus>\r\n    <osbStatusDescription>Neboli n\\u00C3\\u00A1jden\\u00C3\\u00AD \\u00C5\\u00BEiadni odv\\u00C3\\u00A1dzatelia.\r\n\r\nJVP: PO jednozna\\u00C4\\u008Dne identifikovan\\u00C3\\u00A1, nedoplatky/preplatky vypo\\u00C4\\u008D\\u00C3\\u00ADtan\\u00C3\\u00A9\r\nEvPohlad: Neidentifikovan\\u00C3\\u00A1 PO: Na z\\u00C3\\u00A1klade vstupn\\u00C3\\u00BDch \\u00C3\\u00BAdajov sa v IS EvPohlad nepodarilo n\\u00C3\\u00A1js\\u00C5\\u00A5 PO, neexistuj\\u00C3\\u00BAce I\\u00C4\\u0152O.</osbStatusDescription>\r\n  </resultInfo>\r\n  <created>2024-07-17T11:44:47.114+02:00</created>\r\n  <formXmlBase64>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</formXmlBase64>\r\n</ns2:checkArrearsResultServiceRes>\r\n";
    public static final String ZP_OBSAH_SUBORU = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n\r\n<tn:PlnenieOdvodovejPovinnosti xmlns:tn=\"http://csru.gov.sk/oe_zp_odvodova_povinnost/v1.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"OE_CSRU_OdvodovaPovinnostZP_v006.xsd\">\r\n  <tn:OdvodovaPovinnostZP>\r\n    <tn:PO>\r\n      <tn:ObchodneMeno>.. TOPRO s. r. o.</tn:ObchodneMeno>\r\n      <tn:Identifikator>\r\n        <tn:Typ>7</tn:Typ>\r\n        <tn:Hodnota>44162588</tn:Hodnota>\r\n      </tn:Identifikator>\r\n      <tn:Adresa>\r\n        <tn:TypAdresy>100101</tn:TypAdresy>\r\n        <tn:Ulica>WUPPERT\\u00C3\\u0081LSKA</tn:Ulica>\r\n        <tn:Orientacne-SupisneCislo>55</tn:Orientacne-SupisneCislo>\r\n        <tn:Obec>KO\\u00C5\\u00A0ICE</tn:Obec>\r\n        <tn:Psc>04023</tn:Psc>\r\n        <tn:NUTS>SK0425599981</tn:NUTS>\r\n        <tn:Stat>703</tn:Stat>\r\n      </tn:Adresa>\r\n    </tn:PO>\r\n    <tn:Evidencia>\r\n      <tn:SplnenaOdvodovaPovinnost>N</tn:SplnenaOdvodovaPovinnost>\r\n    </tn:Evidencia>\r\n    <tn:ZP>D\\u00C3\\u201DVERA zdravotn\\u00C3\\u00A1 pois\\u00C5\\u00A5ov\\u00C5\\u02C6a</tn:ZP>\r\n    <tn:DatZiad>2024-07-17</tn:DatZiad>\r\n    <tn:Datum>2024-07-17</tn:Datum>\r\n    <tn:VysledokSpracovaniaStav>0</tn:VysledokSpracovaniaStav>\r\n    <tn:JednacieCisloZP>ODVA185591989C34</tn:JednacieCisloZP>\r\n    <tn:ErrorCode>0</tn:ErrorCode>\r\n  </tn:OdvodovaPovinnostZP>\r\n  <tn:OdvodovaPovinnostZP>\r\n    <tn:PO>\r\n      <tn:ObchodneMeno>.. TOPRO s. r. o.</tn:ObchodneMeno>\r\n      <tn:Identifikator>\r\n        <tn:Typ>7</tn:Typ>\r\n        <tn:Hodnota>44162588</tn:Hodnota>\r\n      </tn:Identifikator>\r\n      <tn:Adresa>\r\n        <tn:TypAdresy>100101</tn:TypAdresy>\r\n        <tn:Ulica>WUPPERT\\u00C3\\u0081LSKA</tn:Ulica>\r\n        <tn:Orientacne-SupisneCislo>55</tn:Orientacne-SupisneCislo>\r\n        <tn:Obec>KO\\u00C5\\u00A0ICE-S\\u00C3\\u008DDLISKO KVP</tn:Obec>\r\n        <tn:Psc>04023</tn:Psc>\r\n        <tn:NUTS>SK042</tn:NUTS>\r\n        <tn:Stat>703</tn:Stat>\r\n      </tn:Adresa>\r\n    </tn:PO>\r\n    <tn:Evidencia>\r\n      <tn:SplnenaOdvodovaPovinnost>N</tn:SplnenaOdvodovaPovinnost>\r\n    </tn:Evidencia>\r\n    <tn:ZP>V\\u00C5\\u00A1ZP</tn:ZP>\r\n    <tn:DatZiad>2024-07-17</tn:DatZiad>\r\n    <tn:Datum>2024-07-17</tn:Datum>\r\n    <tn:VysledokSpracovaniaStav>0</tn:VysledokSpracovaniaStav>\r\n    <tn:JednacieCisloZP>1901542</tn:JednacieCisloZP>\r\n    <tn:VybavujeOsobaZP>vszp_data@vszp.sk</tn:VybavujeOsobaZP>\r\n    <tn:VybavujeEmailZP>vszp_data@vszp.sk</tn:VybavujeEmailZP>\r\n    <tn:ErrorCode>0</tn:ErrorCode>\r\n  </tn:OdvodovaPovinnostZP>\r\n  <tn:OdvodovaPovinnostZP>\r\n    <tn:ZP>UZP</tn:ZP>\r\n    <tn:DatZiad>2023-10-10</tn:DatZiad>\r\n    <tn:Datum>2024-07-17</tn:Datum>\r\n    <tn:VysledokSpracovaniaStav>1</tn:VysledokSpracovaniaStav>\r\n    <tn:JednacieCisloZP>ODP1644258</tn:JednacieCisloZP>\r\n    <tn:ErrorCode>0</tn:ErrorCode>\r\n  </tn:OdvodovaPovinnostZP>\r\n</tn:PlnenieOdvodovejPovinnosti>\r\n";

    private static final String FS_DATUM_NEDOPLATKU = "2024-06-30";
    private static final String ICO = "44162588";
    private static final String NAZOV_SPOLOCNOSTI = "..TOPRO s.r.o.";
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
    private static final Date CURRENT_DATE = new Date();

    @MockBean
    private CsruEndpoint csruEndpoint;

    @MockBean
    private SftpService sftpService;

    @Autowired
    private NedoplatkyController nedoplatkyController;

    @Autowired
    private NedoplatkyTimer nedoplatkyTimer;

    @Autowired
    private SpStavZiadostRepository spStavZiadostRepository;

    @Autowired
    private ZpStavZiadostRepository zpStavZiadostRepository;

    @Autowired
    private SetDlzniciRepository setDlzniciRepository;

    @Autowired
    private SetDlzniciObdobieRepository setDlzniciObdobieRepository;

    @Autowired
    private SetDlzniciRefreshRepository setDlzniciRefreshRepository;

    @Autowired
    private FsOsobaZaznamRepository fsOsobaZaznamRepository;

    @Autowired
    private UrsoSubjectStackRepository ursoSubjectStackRepository;

    @Autowired
    private SpOsobaZaznamRepository spOsobaZaznamRepository;

    @Autowired
    private ZpOsobaZaznamRepository zpOsobaZaznamRepository;

    @Autowired
    private UrsoSubjectUpdateTimer ursoSubjectUpdateTimer;

    @Value("${csru.zp.vybavuje-osoba}")
    private String zpVybavujeOsoba;

    @Value("${csru.zp.vybavuje-email}")
    private String zpVybavujeEmail;

    @Value("${csru.zp.vybavuje-telefon}")
    private String zpVybavujeTelefon;

    private void testNedoplatokFs(Nedoplatok nedoplatok, CsruNedoplatokChybovyKodEnum chybovyKodEnum,
                                  CsruNedoplatokEnum nedoplatokEnum, String datumNedoplatku,
                                  CsruDruhDaneAleboPohladavkyEnum druhPohladavkyEnum) {
        Assertions.assertEquals(chybovyKodEnum, nedoplatok.getChybovyKod());
        Assertions.assertEquals(nedoplatokEnum, nedoplatok.getNedoplatok());
        Assertions.assertEquals(LocalDate.parse(datumNedoplatku), nedoplatok.getDatumNedoplatku());
        Assertions.assertEquals(druhPohladavkyEnum, nedoplatok.getDruhDaneAleboPohladavky());
    }

    private void testVysledokKontrolyZp(ZpVysledokKontroly vysledokKontroly, ZpNedoplatok nedoplatokEnum, ZpPoistovna poistovnaEnum,
                                        CsruNavratovyKodOperacie chybovyKodEnum, ZpPopisKoduVysledkuSpracovania vysledokSpracovaniaEnum) {
        Assertions.assertEquals(nedoplatokEnum, vysledokKontroly.getNedoplatok());
        Assertions.assertEquals(poistovnaEnum, vysledokKontroly.getPoistovna());
        Assertions.assertEquals(chybovyKodEnum, vysledokKontroly.getNavratovyKod());
        Assertions.assertEquals(vysledokSpracovaniaEnum, vysledokKontroly.getVysledokSpracovania());
    }

    @Test
    @Transactional
    public void nedoplatkyFsTest() throws IOException, ParserConfigurationException, SAXException {
        GetConsolidatedDataResponseCType mockedFsResponse = XmlUtils.parseXml(FS, GetConsolidatedDataResponseCType.class);
        Mockito.when(csruEndpoint.sendGetConsolidatedDataSyncRequest(any())).thenReturn(mockedFsResponse);

        SubjektNedoplatokVstupnyDetail vstup = new SubjektNedoplatokVstupnyDetail();
        vstup.setIco(ICO);
        ResponseEntity<SubjektVystupnyDetail> response = nedoplatkyController.nedoplatkyInstituciaPost(InstituciaEnum.FS, vstup);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        SubjektVystupnyDetail body = response.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertEquals(CsruNavratovyKodOperacieEnum.OK, body.getNavratovyKodOperacie());
        Assertions.assertEquals(true, body.getMaNedoplatok());

        testNedoplatokFs(body.getNedoplatky().get(0), CsruNedoplatokChybovyKodEnum.OK, CsruNedoplatokEnum.MA_NEDOPLATOK,
                FS_DATUM_NEDOPLATKU, CsruDruhDaneAleboPohladavkyEnum.NDS);
        testNedoplatokFs(body.getNedoplatky().get(1), CsruNedoplatokChybovyKodEnum.OK, CsruNedoplatokEnum.NEMA_NEDOPLATOK,
                FS_DATUM_NEDOPLATKU, CsruDruhDaneAleboPohladavkyEnum.SPD);
        testNedoplatokFs(body.getNedoplatky().get(2), CsruNedoplatokChybovyKodEnum.OK, CsruNedoplatokEnum.NEMA_NEDOPLATOK,
                FS_DATUM_NEDOPLATKU, CsruDruhDaneAleboPohladavkyEnum.COL);
    }

    @Test
    @Transactional
    public void nedpolatkySPTest() throws IOException, ParserConfigurationException, SAXException {
        sk.is.urso.model.csru.api.async.GetConsolidatedDataServiceAsync.GetConsolidatedDataResponseCType mockSpResponse =
                XmlUtils.parseXml(SP, sk.is.urso.model.csru.api.async.GetConsolidatedDataServiceAsync.GetConsolidatedDataResponseCType.class);
        Mockito.when(csruEndpoint.sendGetConsolidatedDataAsyncRequest(any())).thenReturn(mockSpResponse);

        SubjektNedoplatokVstupnyDetail vstup = new SubjektNedoplatokVstupnyDetail();
        vstup.setIco(ICO);
        vstup.setNazovSpolocnosti(NAZOV_SPOLOCNOSTI);
        ResponseEntity<SubjektVystupnyDetail> response = nedoplatkyController.nedoplatkyInstituciaPost(InstituciaEnum.SP, vstup);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        SubjektVystupnyDetail body = response.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertEquals(CsruNavratovyKodOperacieEnum.OK, body.getNavratovyKodOperacie());
        Assertions.assertEquals(CsruStavZiadostiEnum.PREBIEHA_SPRACOVANIE, body.getStavZiadosti());
    }

    @Test
    @Transactional
    public void nedpolatkySPUpdateStavZiadostiTest() throws IOException, ParserConfigurationException, SAXException {
        nedpolatkySPTest();
        GetStatusResponseCType mockSpResponse = XmlUtils.parseXml(SP_STAV_ZIADOSTI, GetStatusResponseCType.class);
        Mockito.when(csruEndpoint.sendGetStatusRequest(any())).thenReturn(mockSpResponse);
        Mockito.when(sftpService.loadFileContent(anyString(), anyInt(), anyString(), anyString(), anyBoolean(), anyString(), anyInt(), anyString()))
                .thenReturn(SP_OBSAH_SUBORU);

        nedoplatkyTimer.updateStavyZiadostiSp();
        List<SpStavZiadost> stavZiadostList = spStavZiadostRepository.findAll();
        Assertions.assertEquals(1, stavZiadostList.size());

        SpStavZiadost stavZiadost = stavZiadostList.get(0);
        Assertions.assertEquals(CsruNavratovyKodOperacie.OK, stavZiadost.getNavratovyKodOperacie());
        Assertions.assertEquals(true, stavZiadost.getMaNedoplatok());
        Assertions.assertEquals(CsruStavZiadosti.SPRACOVANIE_USPESNE_UKONCENE, stavZiadost.getStav());

        SpOsobaZaznam osobaZaznam = stavZiadost.getOsobaZaznam();
        Assertions.assertNotNull(osobaZaznam);
        Assertions.assertEquals(ICO, osobaZaznam.getIco());
        Assertions.assertEquals(NAZOV_SPOLOCNOSTI, osobaZaznam.getNazovSpolocnosti());

        SpVystupnySubor vystupnySubor = stavZiadost.getVystupnySubor();
        Assertions.assertNotNull(vystupnySubor);
        Assertions.assertNotNull(vystupnySubor.getPath());

        SpVysledokKontroly vysledokKontroly = stavZiadost.getVysledokKontroly();
        Assertions.assertNotNull(vysledokKontroly);
        Assertions.assertNotNull(vysledokKontroly.getOsbStatusText());
        Assertions.assertEquals(SpNedoplatok.MA_NEDOPLATOK, vysledokKontroly.getNedoplatok());
    }

    @Test
    @Transactional
    public void nedpolatkyZPTest() throws IOException, ParserConfigurationException, SAXException {
        sk.is.urso.model.csru.api.async.GetConsolidatedDataServiceAsync.GetConsolidatedDataResponseCType mockSpResponse =
                XmlUtils.parseXml(ZP, sk.is.urso.model.csru.api.async.GetConsolidatedDataServiceAsync.GetConsolidatedDataResponseCType.class);
        Mockito.when(csruEndpoint.sendGetConsolidatedDataAsyncRequest(any())).thenReturn(mockSpResponse);

        SubjektNedoplatokVstupnyDetail vstup = new SubjektNedoplatokVstupnyDetail();
        vstup.setIco(ICO);
        vstup.setNazovSpolocnosti(NAZOV_SPOLOCNOSTI);
        ResponseEntity<SubjektVystupnyDetail> response = nedoplatkyController.nedoplatkyInstituciaPost(InstituciaEnum.ZP, vstup);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        SubjektVystupnyDetail body = response.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertEquals(CsruNavratovyKodOperacieEnum.OK, body.getNavratovyKodOperacie());
        Assertions.assertEquals(CsruStavZiadostiEnum.PREBIEHA_SPRACOVANIE, body.getStavZiadosti());
    }

    @Test
    @Transactional
    public void nedpolatkyZPUpdateStavZiadostiTest() throws IOException, ParserConfigurationException, SAXException {
        nedpolatkyZPTest();
        GetStatusResponseCType mockSpResponse = XmlUtils.parseXml(ZP_STAV_ZIADOSTI, GetStatusResponseCType.class);
        Mockito.when(csruEndpoint.sendGetStatusRequest(any())).thenReturn(mockSpResponse);
        Mockito.when(sftpService.loadFileContent(anyString(), anyInt(), anyString(), anyString(), anyBoolean(), anyString(), anyInt(), anyString()))
                .thenReturn(ZP_OBSAH_SUBORU);

        nedoplatkyTimer.updateStavyZiadostiZp();
        List<ZpStavZiadost> stavZiadostList = zpStavZiadostRepository.findAll();
        Assertions.assertEquals(1, stavZiadostList.size());

        ZpStavZiadost stavZiadost = stavZiadostList.get(0);
        Assertions.assertEquals(CsruNavratovyKodOperacie.OK, stavZiadost.getNavratovyKodOperacie());
        Assertions.assertEquals(true, stavZiadost.getMaNedoplatok());
        Assertions.assertEquals(CsruStavZiadosti.SPRACOVANIE_USPESNE_UKONCENE, stavZiadost.getStav());

        ZpOsobaZaznam osobaZaznam = stavZiadost.getOsobaZaznam();
        Assertions.assertNotNull(osobaZaznam);
        Assertions.assertEquals(ICO, osobaZaznam.getIco());
        Assertions.assertEquals(zpVybavujeOsoba, osobaZaznam.getVybavujeOsoba());
        Assertions.assertEquals(zpVybavujeTelefon, osobaZaznam.getVybavujeTelefon());
        Assertions.assertEquals(zpVybavujeEmail, osobaZaznam.getVybavujeEmail());

        ZpVystupnySubor vystupnySubor = stavZiadost.getVystupnySubor();
        Assertions.assertNotNull(vystupnySubor);
        Assertions.assertNotNull(vystupnySubor.getPath());

        List<ZpVysledokKontroly> vysledokKontrolyList = stavZiadost.getVysledkyKontrol();
        Assertions.assertNotNull(vysledokKontrolyList);
        Assertions.assertEquals(3, vysledokKontrolyList.size());

        testVysledokKontrolyZp(vysledokKontrolyList.get(0), ZpNedoplatok.N, ZpPoistovna.NEZNAMA, CsruNavratovyKodOperacie.OK,
                ZpPopisKoduVysledkuSpracovania.ZASLANE_UDAJE_OD_ZP);
        testVysledokKontrolyZp(vysledokKontrolyList.get(1), ZpNedoplatok.N, ZpPoistovna.NEZNAMA, CsruNavratovyKodOperacie.OK,
                ZpPopisKoduVysledkuSpracovania.ZASLANE_UDAJE_OD_ZP);
        testVysledokKontrolyZp(vysledokKontrolyList.get(2), ZpNedoplatok.NEZNAMY, ZpPoistovna.UNION, CsruNavratovyKodOperacie.OK,
                ZpPopisKoduVysledkuSpracovania.NIE_JE_EVIDOVANY);
    }

    @Test
    @Transactional
    public void updateFsAndSendSpZpRequestsFsMaNedoplatokTest() throws IOException, ParserConfigurationException, SAXException {
        SetDlznici setDlznici = new SetDlznici();
        setDlznici.setId(1L);
        setDlznici.setIco(ICO);
        setDlznici.setNazov(NAZOV_SPOLOCNOSTI);
        setDlznici.setSync(false);
        setDlzniciRepository.save(setDlznici);

        nedoplatkyFsTest();
        ursoSubjectUpdateTimer.updateFsAndSendSpZpRequests();

        SetDlznici dlznikResult = setDlzniciRepository.findAll().get(0);
        Assertions.assertEquals(setDlznici.getId(), dlznikResult.getId());
        Assertions.assertEquals(setDlznici.getIco(), dlznikResult.getIco());
        Assertions.assertEquals(setDlznici.getNazov(), dlznikResult.getNazov());
        Assertions.assertEquals(setDlznici.getSync(), dlznikResult.getSync());

        SetDlzniciRefresh dlznikRefreshResult = setDlzniciRefreshRepository.findAll().get(0);
        Assertions.assertNotNull(dlznikRefreshResult.getDateRefresh());
        Assertions.assertEquals(1, dlznikRefreshResult.getRecordModified());
        Assertions.assertEquals((short) 3, dlznikRefreshResult.getSource());
        Assertions.assertEquals(dlznikResult.getSetDlzniciRefresh().getId(), dlznikRefreshResult.getId());

        SetDlzniciObdobie dlznikObdobieResult = setDlzniciObdobieRepository.findAll().get(0);
        Assertions.assertEquals((short) 3, dlznikObdobieResult.getZdroj());
        Assertions.assertEquals(DATE_FORMATTER.format(CURRENT_DATE), DATE_FORMATTER.format(dlznikObdobieResult.getObdobieOd()));
        Assertions.assertNull(dlznikObdobieResult.getObdobieDo());
        Assertions.assertEquals(setDlznici.getId(), dlznikObdobieResult.getSetDlznici().getId());
    }

    @Test
    @Transactional
    public void updateFsAndSendSpZpRequestsFsUzNemaNedoplatokTest() throws IOException, ParserConfigurationException, SAXException {
        SetDlznici setDlznici = new SetDlznici();
        setDlznici.setId(1L);
        setDlznici.setIco(ICO);
        setDlznici.setNazov(NAZOV_SPOLOCNOSTI);
        setDlznici.setSync(false);
        setDlznici = setDlzniciRepository.save(setDlznici);

        SetDlzniciObdobie setDlzniciObdobie = new SetDlzniciObdobie();
        setDlzniciObdobie.setId(1L);
        setDlzniciObdobie.setSetDlznici(setDlznici);
        setDlzniciObdobie.setZdroj((short) 3);
        setDlzniciObdobie.setObdobieOd(new Date());
        setDlzniciObdobie = setDlzniciObdobieRepository.save(setDlzniciObdobie);

        setDlznici.getSetDlzniciObdobieList().add(setDlzniciObdobie);
        setDlznici = setDlzniciRepository.save(setDlznici);

        nedoplatkyFsTest();
        FsOsobaZaznam osoba = fsOsobaZaznamRepository.findAll().get(0);
        osoba.setMaNedoplatok(false);
        fsOsobaZaznamRepository.save(osoba);

        ursoSubjectUpdateTimer.updateFsAndSendSpZpRequests();

        SetDlznici dlznikResult = setDlzniciRepository.findAll().get(0);
        Assertions.assertEquals(setDlznici.getId(), dlznikResult.getId());
        Assertions.assertEquals(setDlznici.getIco(), dlznikResult.getIco());
        Assertions.assertEquals(setDlznici.getNazov(), dlznikResult.getNazov());
        Assertions.assertEquals(setDlznici.getSync(), dlznikResult.getSync());

        SetDlzniciRefresh dlznikRefreshResult = setDlzniciRefreshRepository.findAll().get(0);
        Assertions.assertNotNull(dlznikRefreshResult.getDateRefresh());
        Assertions.assertEquals(1, dlznikRefreshResult.getRecordModified());
        Assertions.assertEquals((short) 3, dlznikRefreshResult.getSource());
        Assertions.assertEquals(dlznikResult.getSetDlzniciRefresh().getId(), dlznikRefreshResult.getId());

        SetDlzniciObdobie dlznikObdobieResult = setDlzniciObdobieRepository.findAll().get(0);
        Assertions.assertEquals((short) 3, dlznikObdobieResult.getZdroj());
        Assertions.assertEquals(setDlzniciObdobie.getObdobieOd(), dlznikObdobieResult.getObdobieOd());
        Assertions.assertEquals(DATE_FORMATTER.format(CURRENT_DATE), DATE_FORMATTER.format(dlznikObdobieResult.getObdobieDo()));
        Assertions.assertEquals(setDlznici.getId(), dlznikObdobieResult.getSetDlznici().getId());
    }

    @Test
    @Transactional
    public void nedoplatkyRequestSpTest() throws IOException, ParserConfigurationException, SAXException {
        sk.is.urso.model.csru.api.async.GetConsolidatedDataServiceAsync.GetConsolidatedDataResponseCType mockSpResponse =
                XmlUtils.parseXml(SP, sk.is.urso.model.csru.api.async.GetConsolidatedDataServiceAsync.GetConsolidatedDataResponseCType.class);
        Mockito.when(csruEndpoint.sendGetConsolidatedDataAsyncRequest(any())).thenReturn(mockSpResponse);

        SetDlznici setDlznici = new SetDlznici();
        setDlznici.setId(1L);
        setDlznici.setIco(ICO);
        setDlznici.setNazov(NAZOV_SPOLOCNOSTI);
        setDlznici.setSync(false);
        setDlznici = setDlzniciRepository.save(setDlznici);

        ursoSubjectUpdateTimer.nedoplatkyRequestSp(setDlznici);

        UrsoSubjectStack ursoSubjectStack = ursoSubjectStackRepository.findAll().get(0);
        Assertions.assertEquals(setDlznici, ursoSubjectStack.getSetDlznici());
        Assertions.assertEquals(UrsoSubjectStav.PREBIEHA, ursoSubjectStack.getUrsoSubjectStav());
        Assertions.assertEquals(UrsoNedoplatokTyp.SP, ursoSubjectStack.getUrsoNedoplatokTyp());
        Assertions.assertEquals(DATE_FORMATTER.format(CURRENT_DATE), DATE_FORMATTER.format(ursoSubjectStack.getCasVytvorenia()));
    }

    @Test
    @Transactional
    public void nedoplatkyRequestZpTest() throws IOException, ParserConfigurationException, SAXException {
        sk.is.urso.model.csru.api.async.GetConsolidatedDataServiceAsync.GetConsolidatedDataResponseCType mockSpResponse =
                XmlUtils.parseXml(ZP, sk.is.urso.model.csru.api.async.GetConsolidatedDataServiceAsync.GetConsolidatedDataResponseCType.class);
        mockSpResponse.setRequestId(mockSpResponse.getRequestId());
        Mockito.when(csruEndpoint.sendGetConsolidatedDataAsyncRequest(any())).thenReturn(mockSpResponse);

        SetDlznici setDlznici = new SetDlznici();
        setDlznici.setId(1L);
        setDlznici.setIco(ICO);
        setDlznici.setNazov(NAZOV_SPOLOCNOSTI);
        setDlznici.setSync(false);
        setDlznici = setDlzniciRepository.save(setDlznici);

        ursoSubjectUpdateTimer.nedoplatkyRequestZp(setDlznici);

        UrsoSubjectStack ursoSubjectStack = ursoSubjectStackRepository.findAll().get(0);
        Assertions.assertEquals(setDlznici, ursoSubjectStack.getSetDlznici());
        Assertions.assertEquals(UrsoSubjectStav.PREBIEHA, ursoSubjectStack.getUrsoSubjectStav());
        Assertions.assertEquals(UrsoNedoplatokTyp.ZP, ursoSubjectStack.getUrsoNedoplatokTyp());
        Assertions.assertEquals(DATE_FORMATTER.format(CURRENT_DATE), DATE_FORMATTER.format(ursoSubjectStack.getCasVytvorenia()));
    }

    @Test
    @Transactional
    public void nedoplatkyResponseSpMaNedoplatokTest() throws IOException, ParserConfigurationException, SAXException {
        nedoplatkyRequestSpTest();

        GetStatusResponseCType mockSpResponse = XmlUtils.parseXml(SP_STAV_ZIADOSTI, GetStatusResponseCType.class);
        Mockito.when(csruEndpoint.sendGetStatusRequest(any())).thenReturn(mockSpResponse);
        Mockito.when(sftpService.loadFileContent(anyString(), anyInt(), anyString(), anyString(), anyBoolean(), anyString(), anyInt(), anyString()))
                .thenReturn(SP_OBSAH_SUBORU);

        nedoplatkyTimer.updateStavyZiadostiSp();
        SpOsobaZaznam osoba = spOsobaZaznamRepository.findAll().get(0);
        osoba.setStavZiadosti(new ArrayList<>());
        osoba.getStavZiadosti().add(spStavZiadostRepository.findAll().get(0));

        ursoSubjectUpdateTimer.nedoplatkyResponseSp();

        SetDlzniciRefresh dlznikRefreshResult = setDlzniciRefreshRepository.findAll().get(0);
        Assertions.assertNotNull(dlznikRefreshResult.getDateRefresh());
        Assertions.assertEquals(1, dlznikRefreshResult.getRecordModified());
        Assertions.assertEquals((short) 1, dlznikRefreshResult.getSource());

        SetDlzniciObdobie dlznikObdobieResult = setDlzniciObdobieRepository.findAll().get(0);
        Assertions.assertEquals((short) 1, dlznikObdobieResult.getZdroj());
        Assertions.assertEquals(DATE_FORMATTER.format(CURRENT_DATE), DATE_FORMATTER.format(dlznikObdobieResult.getObdobieOd()));
        Assertions.assertNull(dlznikObdobieResult.getObdobieDo());
    }

    @Test
    @Transactional
    public void nedoplatkyResponseSpUzNemaNedoplatokTest() throws IOException, ParserConfigurationException, SAXException {
        nedoplatkyRequestSpTest();

        SetDlznici setDlznici = setDlzniciRepository.findAll().get(0);

        SetDlzniciObdobie setDlzniciObdobie = new SetDlzniciObdobie();
        setDlzniciObdobie.setId(1L);
        setDlzniciObdobie.setSetDlznici(setDlznici);
        setDlzniciObdobie.setZdroj((short) 1);
        setDlzniciObdobie.setObdobieOd(new Date());
        setDlzniciObdobie = setDlzniciObdobieRepository.save(setDlzniciObdobie);

        setDlznici.getSetDlzniciObdobieList().add(setDlzniciObdobie);
        setDlznici = setDlzniciRepository.save(setDlznici);


        GetStatusResponseCType mockSpResponse = XmlUtils.parseXml(SP_STAV_ZIADOSTI, GetStatusResponseCType.class);
        Mockito.when(csruEndpoint.sendGetStatusRequest(any())).thenReturn(mockSpResponse);
        Mockito.when(sftpService.loadFileContent(anyString(), anyInt(), anyString(), anyString(), anyBoolean(), anyString(), anyInt(), anyString()))
                .thenReturn(SP_OBSAH_SUBORU);

        nedoplatkyTimer.updateStavyZiadostiSp();

        SpStavZiadost stavZiadost = spStavZiadostRepository.findAll().get(0);
        stavZiadost.setMaNedoplatok(false);

        SpOsobaZaznam osoba = spOsobaZaznamRepository.findAll().get(0);
        osoba.setStavZiadosti(new ArrayList<>());
        osoba.getStavZiadosti().add(stavZiadost);

        ursoSubjectUpdateTimer.nedoplatkyResponseSp();

        SetDlzniciRefresh dlznikRefreshResult = setDlzniciRefreshRepository.findAll().get(0);
        Assertions.assertNotNull(dlznikRefreshResult.getDateRefresh());
        Assertions.assertEquals(1, dlznikRefreshResult.getRecordModified());
        Assertions.assertEquals((short) 1, dlznikRefreshResult.getSource());

        SetDlzniciObdobie dlznikObdobieResult = setDlzniciObdobieRepository.findAll().get(0);
        Assertions.assertEquals((short) 1, dlznikObdobieResult.getZdroj());
        Assertions.assertEquals(setDlzniciObdobie.getObdobieOd(), dlznikObdobieResult.getObdobieOd());
        Assertions.assertEquals(DATE_FORMATTER.format(CURRENT_DATE), DATE_FORMATTER.format(dlznikObdobieResult.getObdobieDo()));
    }

    @Test
    @Transactional
    public void nedoplatkyResponseZpMaNedoplatokTest() throws IOException, ParserConfigurationException, SAXException {
        nedoplatkyRequestZpTest();

        GetStatusResponseCType mockSpResponse = XmlUtils.parseXml(ZP_STAV_ZIADOSTI, GetStatusResponseCType.class);
        Mockito.when(csruEndpoint.sendGetStatusRequest(any())).thenReturn(mockSpResponse);
        Mockito.when(sftpService.loadFileContent(anyString(), anyInt(), anyString(), anyString(), anyBoolean(), anyString(), anyInt(), anyString()))
                .thenReturn(ZP_OBSAH_SUBORU);

        nedoplatkyTimer.updateStavyZiadostiZp();
        ZpOsobaZaznam osoba = zpOsobaZaznamRepository.findAll().get(0);
        osoba.setStavZiadostList(new ArrayList<>());
        osoba.getStavZiadostList().add(zpStavZiadostRepository.findAll().get(0));

        ursoSubjectUpdateTimer.nedoplatkyResponseZp();

        SetDlzniciRefresh dlznikRefreshResult = setDlzniciRefreshRepository.findAll().get(0);
        Assertions.assertNotNull(dlznikRefreshResult.getDateRefresh());
        Assertions.assertEquals(1, dlznikRefreshResult.getRecordModified());
        Assertions.assertEquals((short) 2, dlznikRefreshResult.getSource());

        SetDlzniciObdobie dlznikObdobieResult = setDlzniciObdobieRepository.findAll().get(0);
        Assertions.assertEquals((short) 2, dlznikObdobieResult.getZdroj());
        Assertions.assertEquals(DATE_FORMATTER.format(CURRENT_DATE), DATE_FORMATTER.format(dlznikObdobieResult.getObdobieOd()));
        Assertions.assertNull(dlznikObdobieResult.getObdobieDo());
    }

    @Test
    @Transactional
    public void nedoplatkyResponseZpUzNemaNedoplatokTest() throws IOException, ParserConfigurationException, SAXException {
        nedoplatkyRequestZpTest();

        SetDlznici setDlznici = setDlzniciRepository.findAll().get(0);

        SetDlzniciObdobie setDlzniciObdobie = new SetDlzniciObdobie();
        setDlzniciObdobie.setId(1L);
        setDlzniciObdobie.setSetDlznici(setDlznici);
        setDlzniciObdobie.setZdroj((short) 2);
        setDlzniciObdobie.setObdobieOd(new Date());
        setDlzniciObdobie = setDlzniciObdobieRepository.save(setDlzniciObdobie);

        setDlznici.getSetDlzniciObdobieList().add(setDlzniciObdobie);
        setDlznici = setDlzniciRepository.save(setDlznici);


        GetStatusResponseCType mockSpResponse = XmlUtils.parseXml(ZP_STAV_ZIADOSTI, GetStatusResponseCType.class);
        Mockito.when(csruEndpoint.sendGetStatusRequest(any())).thenReturn(mockSpResponse);
        Mockito.when(sftpService.loadFileContent(anyString(), anyInt(), anyString(), anyString(), anyBoolean(), anyString(), anyInt(), anyString()))
                .thenReturn(ZP_OBSAH_SUBORU);

        nedoplatkyTimer.updateStavyZiadostiZp();

        ZpStavZiadost stavZiadost = zpStavZiadostRepository.findAll().get(0);
        stavZiadost.setMaNedoplatok(false);

        ZpOsobaZaznam osoba = zpOsobaZaznamRepository.findAll().get(0);
        osoba.setStavZiadostList(new ArrayList<>());
        osoba.getStavZiadostList().add(stavZiadost);

        ursoSubjectUpdateTimer.nedoplatkyResponseZp();

        SetDlzniciRefresh dlznikRefreshResult = setDlzniciRefreshRepository.findAll().get(0);
        Assertions.assertNotNull(dlznikRefreshResult.getDateRefresh());
        Assertions.assertEquals(1, dlznikRefreshResult.getRecordModified());
        Assertions.assertEquals((short) 2, dlznikRefreshResult.getSource());

        SetDlzniciObdobie dlznikObdobieResult = setDlzniciObdobieRepository.findAll().get(0);
        Assertions.assertEquals((short) 2, dlznikObdobieResult.getZdroj());
        Assertions.assertEquals(setDlzniciObdobie.getObdobieOd(), dlznikObdobieResult.getObdobieOd());
        Assertions.assertEquals(DATE_FORMATTER.format(CURRENT_DATE), DATE_FORMATTER.format(dlznikObdobieResult.getObdobieDo()));
    }
}
