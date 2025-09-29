package sk.is.urso.reg.sql;

public class IsNotNull extends Condition {

    public IsNotNull(Alias alias, String name) {
        super(new Field(alias, name), null);
    }

    @Override
    public String toString() {
        return left + " is not null ";
    }

}
