package sk.is.urso.service;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import org.alfa.exception.CommonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;
import sk.is.urso.cli.Application;
import sk.is.urso.config.ModelMapperConfig;
import sk.is.urso.plugin.repository.RfoReg1DataRepository;
import sk.is.urso.plugin.repository.RfoReg1IndexRepository;
import sk.is.urso.plugin.repository.RpoReg1DataRepository;
import sk.is.urso.plugin.repository.RpoReg1IndexRepository;
import sk.is.urso.plugin.repository.SubjectReg1DataRepository;
import sk.is.urso.plugin.repository.SubjectReg1IndexRepository;
import sk.is.urso.repository.HodnotaCiselnikaRepository;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Command(name = "initialize", mixinStandardHelpOptions = true, description = "Data initialization")
public class Initialize implements Runnable {

    public static final Logger log = LoggerFactory.getLogger(Application.class);//NOSONAR

    public static final String RPO = "RPO_1";

    public static final String RFO = "RFO_1";
    public static final String RA_INTERNAL = "RA_INTERNAL_1";

    public int allCount = 0;
    public int successCount = 0;
    public int failCount = 0;
    public int alreadyExistingCount = 0;

    @Value("${user.system.login}")
    public String userSystemLogin;

    @Value("${rainternal.registersconfig.path}")
    public String rainternalRegistersConfigPath;

    @Value("${rainternal.registerpluginfile.path}")
    public String rainternalRegisterPluginFilePath;

    @Autowired
    RpoReg1IndexRepository rpoIndexRepository;

    @Autowired
    RpoReg1DataRepository rpoDataRepository;

    @Autowired
    RfoReg1IndexRepository rfoIndexRepository;
    //
    @Autowired
    RfoReg1DataRepository rfoDataRepository;

    @Autowired
    SubjectReg1IndexRepository subjectIndexRepository;

    @Autowired
    HodnotaCiselnikaRepository hodnotaCiselnikaRepository;

    @Autowired
    DefaultListableBeanFactory beanFactory;

    @ParentCommand
    Register register;

    @Autowired
    SubjectReg1DataRepository subjectDataRepository;

    @Autowired
    ModelMapperConfig modelMapperConfig;

    @Option(names = {"-c", "--cislo"}, description = "optional parameter to initialize a specific record or range of records")
    public String cislo;

    @Option(names = {"-e", "--endAfterError"}, description = "optional parameter for stopping migration after first error", defaultValue = "false")
    public boolean endAfterError = false;

    @Option(names = {"-v", "--verbose"}, description = "If set, output is verbose (printing stack traces and so)", defaultValue = "false")
    public boolean verbose = false;

    @Option(names = {"-d", "--dry"}, description = "Dry run - perform standard operations but do not migrate anything", defaultValue = "false")
    public boolean dry = false;

    @Parameters(description = "Path to zip file", arity = "0..1")
    public String zipFilePath = null;

    @Option(names = {"-cfp", "--csvFilePath"}, description = "CSV folder path used at RA internal import.")
    public String csvFilePath;

    @Option(names = {"-crn", "--csvRecordsNum"}, description = "Number of records saved at one file.", defaultValue = "10000000")
    public String csvRecordsNum;

    public String getRegisterIdWithoutVersion() {
        CharSequence inputStr = register.registerId;
        String patternStr = "[1-9]";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.find()) {
            return register.registerId.substring(0, matcher.start() - 1);
        }
        return null;
    }

    @Override
    public void run() {
        switch (register.registerId) {
            case RPO -> initializeRpoXmlFile();
            case RFO -> initializeRfoXmlFile();
            case RA_INTERNAL -> initializeRaXmlFile();
            default ->
                    throw new CommonException(HttpStatus.BAD_REQUEST, register.registerId + " register cannot be initialized! Only " + RPO + ", " + RFO + ", " + RA_INTERNAL + " registers can be initialized!", null);
        }
    }

    private void initializeRfoXmlFile() {
        setZipFilePath();

        try (var zip = new ZipFile(zipFilePath)) {
            checkZip(zip);
            /*char[] password = System.getProperty("RFO_LIST_PASSWORD") != null ? System.getProperty("RFO_LIST_PASSWORD").toCharArray() : new char[0];
            if (password.length == 0) {
                var console = System.console();
                console.printf("Please enter a file password: ");
                password = console.readPassword();
            }
            zip.setPassword(password);*/

            List<FileHeader> fileHeaderList = getFileHeaderList(zip);

            if (fileHeaderList.size() > 0) {
                verifyPassword(zip, fileHeaderList.get(fileHeaderList.size() - 1));
            }

            InitializeRfo initializeRfo = new InitializeRfo(this);
            initializeRfo.initialize(fileHeaderList, zip);

            logFinalInfo();
        } catch (Exception e) {
            e.printStackTrace();
            error("Exception during initialization: ", e);
        }
    }

    public void initializeRpoXmlFile() {
        setZipFilePath();

        try (var zip = new ZipFile(zipFilePath)) {
            checkZip(zip);

            /*char[] password = System.getProperty("RPO_LIST_PASSWORD") != null ? System.getProperty("RPO_LIST_PASSWORD").toCharArray() : new char[0];
            if (password.length == 0) {
                var console = System.console();
                console.printf("Please enter a file password: ");
                password = console.readPassword();
            }
            zip.setPassword(password);
            */

            List<FileHeader> fileHeaderList = getFileHeaderList(zip);

            if (fileHeaderList.size() > 0) {
                verifyPassword(zip, fileHeaderList.get(fileHeaderList.size() - 1));
            }

            InitializeRpo initializeRpo = new InitializeRpo(this);
            initializeRpo.initialize(fileHeaderList, zip);

            logFinalInfo();
        } catch (Exception e) {
            e.printStackTrace();
            error("Exception during initialization: ", e);
        }
    }

    public void initializeRaXmlFile() {
        setZipFilePath();

        try (var zip = new java.util.zip.ZipFile(zipFilePath)) {

            InitializeRa initializeRa = new InitializeRa(this);
            initializeRa.initialize(zip);

            logFinalInfo();
        } catch (Exception e) {
            e.printStackTrace();
            error("Exception during initialization: ", e);
        }
    }

    private void logFinalInfo() {
        if (dry) {
            log.info("This was dry run, nothing was migrated!");
        }
        log.info("All initialized files: " + allCount);
        log.info("Successfully migrated files: " + successCount);
        log.info("Failed migrations: " + failCount);
        log.info("Already existing RPO data: " + alreadyExistingCount);
    }

    private void setZipFilePath() {
        if (zipFilePath == null) {
            zipFilePath = System.getProperty(getRegisterIdWithoutVersion() + "_LIST_PATH") != null ? System.getProperty(getRegisterIdWithoutVersion() + "_LIST_PATH") : "./RPOListCorporateBodies_008_2018-05-22-04-40-28-902.zip";
        }
    }

    private void checkZip(ZipFile zip) {
        if (!zip.getFile().exists()) {
            throw new CommonException(HttpStatus.NOT_FOUND, "Zip file doesn't exist!", null);
        }
    }

    private void verifyPassword(ZipFile zip, FileHeader fileHeader) throws IOException {
        try (ZipInputStream is = zip.getInputStream(fileHeader)) {
        }
        ;
    }

    public void error(String msg, Exception e) {
        if (verbose) {
            log.error(msg, e);
        } else {
            log.error(msg + e.getMessage());
        }
    }

    public List<FileHeader> getFileHeaderList(ZipFile zip) throws ZipException {
        List<FileHeader> fileHeaderList = zip.getFileHeaders();
        if (cislo != null) {
            fileHeaderList = fileHeaderList.stream().filter(file -> !file.isDirectory() && file.getFileName().split("/").length > 1).collect(Collectors.toList());
        }
        return fileHeaderList;
    }
}