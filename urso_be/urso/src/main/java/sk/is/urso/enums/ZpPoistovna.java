package sk.is.urso.enums;

import lombok.Getter;

public enum ZpPoistovna {

    VSEOBECNA_ZDRAVOTNA("VSEOBECNA_ZDRAVOTNA"),
    DOVERA("DOVERA"),
    UNION("UNION"),
    NEZNAMA("NEZNAMA");

    @Getter
    private final String value;

    public static ZpPoistovna fromValue(String value) {
        ZpPoistovna[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            ZpPoistovna b = var1[var3];
            if (b.value.equals(value)) {
                return b;
            }
        }

        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    ZpPoistovna(String value) {
        this.value = value;
    }
}
