package sk.is.urso.enums;

import lombok.Getter;

public enum FsNedoplatokChybovyKod {

    OK("OK"),
    SUBJEKT_MA_DUPLICITU_V_EVIDENCII("SUBJEKT_MA_DUPLICITU_V_EVIDENCII"),
    NEZNAMY("NEZNAMY");

    @Getter
    private final String value;

    public static FsNedoplatokChybovyKod fromValue(String value) {
        FsNedoplatokChybovyKod[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            FsNedoplatokChybovyKod b = var1[var3];
            if (b.value.equals(value)) {
                return b;
            }
        }

        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    FsNedoplatokChybovyKod(String value) {
        this.value = value;
    }
}
