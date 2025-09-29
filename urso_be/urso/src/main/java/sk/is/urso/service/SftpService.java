package sk.is.urso.service;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.ProxyHTTP;
import com.jcraft.jsch.Session;
import org.alfa.exception.CommonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import sk.is.urso.util.LoggerUtils;

import java.nio.charset.StandardCharsets;

@Service
public class SftpService {

    private static final Logger logger = LoggerFactory.getLogger(SftpService.class);

    private static final String ERROR_GET_FILE_CONTENT = "Chyba pri načítaní obsahu súboru.";

    @Autowired
    private LoggerUtils loggerUtils;

    public String loadFileContent(String host, int port, String username, String password, boolean isProxyEnabled, String proxyHost, int proxyPort, String filePath) {
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channelSftp = null;
        try {
            session = jsch.getSession(username, host, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            if(isProxyEnabled) {
                session.setProxy(new ProxyHTTP(proxyHost, proxyPort));
            }
            session.connect();

            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            byte[] bytes = channelSftp.get(filePath).readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            loggerUtils.log(LoggerUtils.LogType.ERROR, logger, "[SFTP][Method - loadFileContent] Error - " + ex.getMessage() + ".");
            throw new CommonException(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_GET_FILE_CONTENT, ex);
        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }
}
