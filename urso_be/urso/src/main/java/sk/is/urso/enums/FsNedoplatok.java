package sk.is.urso.enums;

import lombok.Getter;

public enum FsNedoplatok {

    MA_NEDOPLATOK("MA_NEDOPLATOK"),
    NEMA_NEDOPLATOK("NEMA_NEDOPLATOK"),
    CHYBA("CHYBA"),
    NEZNAMY("NEZNAMY");

    @Getter
    private final String value;

    public static FsNedoplatok fromValue(String value) {
        FsNedoplatok[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            FsNedoplatok b = var1[var3];
            if (b.value.equals(value)) {
                return b;
            }
        }

        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    FsNedoplatok(String value) {
        this.value = value;
    }
}
