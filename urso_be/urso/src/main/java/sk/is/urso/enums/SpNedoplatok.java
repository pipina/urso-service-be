package sk.is.urso.enums;

import lombok.Getter;

public enum SpNedoplatok {

    MA_NEDOPLATOK("MA_NEDOPLATOK"),
    NEMA_NEDOPLATOK("NEMA_NEDOPLATOK"),
    MA_NEDOPLATOK_NESPLNENIE_POVINNOSTI("MA_NEDOPLATOK_NESPLNENIE_POVINNOSTI"),
    NIE_JE_V_EVIDENCII("NIE_JE_V_EVIDENCII"),
    NEKOMPLETNE_DATA_TECHNICKA_CHYBA("NEKOMPLETNE_DATA_TECHNICKA_CHYBA"),
    NEIDENTIFIKOVANA_OSOBA("NEIDENTIFIKOVANA_OSOBA"),
    NEZNAMY("NEZNAMY");

    @Getter
    private final String value;

    public static SpNedoplatok fromValue(String value) {
        SpNedoplatok[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            SpNedoplatok b = var1[var3];
            if (b.value.equals(value)) {
                return b;
            }
        }

        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    SpNedoplatok(String value) {
        this.value = value;
    }
}
