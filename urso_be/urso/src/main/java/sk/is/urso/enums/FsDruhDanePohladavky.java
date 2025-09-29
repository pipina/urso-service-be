package sk.is.urso.enums;

import lombok.Getter;

public enum FsDruhDanePohladavky {

    NDS("NDS"),
    SPD("SPD"),
    COL("COL"),
    NEZNAMY("NEZNAMY");

    @Getter
    private final String value;

    public static FsDruhDanePohladavky fromValue(String value) {
        FsDruhDanePohladavky[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            FsDruhDanePohladavky b = var1[var3];
            if (b.value.equals(value)) {
                return b;
            }
        }

        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    FsDruhDanePohladavky(String value) {
        this.value = value;
    }
}
