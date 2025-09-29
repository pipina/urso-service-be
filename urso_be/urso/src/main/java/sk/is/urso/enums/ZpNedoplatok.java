package sk.is.urso.enums;

import lombok.Getter;

public enum ZpNedoplatok {

    A("A"),
    N("N"),
    C("C"),
    NEZNAMY("NEZNAMY");

    @Getter
    private final String value;

    public static ZpNedoplatok fromValue(String value) {
        ZpNedoplatok[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            ZpNedoplatok b = var1[var3];
            if (b.value.equals(value)) {
                return b;
            }
        }

        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    ZpNedoplatok(String value) {
        this.value = value;
    }
}
