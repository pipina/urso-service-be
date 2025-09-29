package sk.is.urso.service;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.List;

@Component
@Command(name = "UrsoCli", subcommands = Register.class, mixinStandardHelpOptions = true, description = "IS URSO CLI")
public class UrsoCli {

    @Option(names = "--spring.config.location", description = "optional parameter to define location of configuration file")
    private String configLocation;
    @Option(names = "--spring.profiles.active", description = "optional parameter to define active profile")
    private List<String> profilesActive;
    @Option(names = "--spring.main.web-application-type", description = "Optional parameter to disable starting tomcat server in case of wrong configuration")
    private String webApplicationType;
}
