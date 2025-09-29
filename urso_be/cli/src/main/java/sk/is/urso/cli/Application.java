package sk.is.urso.cli;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import picocli.CommandLine;
import sk.is.urso.service.UrsoCli;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableJpaAuditing
@ComponentScan(basePackages = {"sk.is.urso"},
        basePackageClasses = {org.alfa.utils.DateUtils.class, org.alfa.utils.XmlUtils.class},
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {org.alfa.utils.HttpServletRequestUtils.class})
        })
@EntityScan(basePackages = {"sk.is.urso.model", "sk.is.urso.plugin.entity"})
@EnableJpaRepositories(basePackages = {"sk.is.urso.repository", "sk.is.urso.plugin.repository"})
public class Application extends SpringBootServletInitializer implements CommandLineRunner {

    private static final String UTF_8 = "UTF-8";

    @Autowired
    private UrsoCli ursoCli;

    @Autowired
    private CommandLine.IFactory factory;

    public static void main(String[] args) {
        System.setProperty("file.encoding", UTF_8);
        System.setProperty("sun.stderr.encoding", UTF_8);
        System.setProperty("sun.stdout.encoding", UTF_8);

        System.setProperty("java.net.preferIPv4Stack", "true");
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        return application.sources(Application.class);
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onContextRefreshed() {
    }

    @Override
    public void run(String... args) {
        int exitCode = new CommandLine(this.ursoCli, factory).execute(args);
        System.exit(exitCode);
    }

}
