package sk.is.urso.enums;

import lombok.Getter;

public enum UdalostKategoria {

    CREATE("create"),
    READ("read"),
    UPDATE("update"),
    DELETE("delete"),
    IMPORT("import"),
    EXPORT("export");

    @Getter
    private final String value;

    public static UdalostKategoria fromValue(String value) {
        UdalostKategoria[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            UdalostKategoria b = var1[var3];
            if (b.value.equals(value)) {
                return b;
            }
        }

        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    UdalostKategoria(String value) {
        this.value = value;
    }
}
