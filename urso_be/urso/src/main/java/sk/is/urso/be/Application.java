package sk.is.urso.be;

//import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import sk.is.urso.util.EncryptionUtils;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@ComponentScan(basePackages = {"sk.is.urso", "sk.is.urso.model", "sk.is.urso.repository", "org.alfa"}, basePackageClasses = {EncryptionUtils.class})
@EnableJpaRepositories(basePackages = {"sk.is.urso.repository", "sk.is.urso.plugin.repository"})
@EntityScan(basePackages = {"sk.is.urso.model", "sk.is.urso.plugin.entity", "org.alfa.model"})
@EnableScheduling
//@EnableSchedulerLock(defaultLockAtMostFor = "${shedlock.most}")
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        SpringApplication.run(Application.class, args);
    }
}
