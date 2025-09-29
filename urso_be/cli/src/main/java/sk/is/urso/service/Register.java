package sk.is.urso.service;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "register", subcommands = Initialize.class, mixinStandardHelpOptions = true, description = "Operations on given register")
public class Register {

    @Parameters(description = "Register Id")
    public String registerId;
}
