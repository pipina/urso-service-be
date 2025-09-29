package sk.is.urso.reg.sql;

public class Like extends Condition {

    public Like(Alias alias, String name, String value) {
        super(new Field(alias, name), new Value(value));
    }

    @Override
    public String toString() {
        return "lower(" + left + ") like " + right;
    }

}
