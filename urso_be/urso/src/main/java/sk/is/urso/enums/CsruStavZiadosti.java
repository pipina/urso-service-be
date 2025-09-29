package sk.is.urso.enums;

import lombok.Getter;

public enum CsruStavZiadosti {

    PREBIEHA_SPRACOVANIE("PREBIEHA_SPRACOVANIE"),
    SPRACOVANIE_USPESNE_UKONCENE("SPRACOVANIE_USPESNE_UKONCENE"),
    SPRACOVANIE_UKONCENE_S_CHYBOU("SPRACOVANIE_UKONCENE_S_CHYBOU"),
    NEZNAMA_POZIADAVKA("NEZNAMA_POZIADAVKA"),
    SPRACOVANIE_UKONCENE_S_UPOZORNENIM("SPRACOVANIE_UKONCENE_S_UPOZORNENIM"),
    NEZNAMY("NEZNAMY");

    @Getter
    private final String value;

    public static CsruStavZiadosti fromValue(String value) {
        CsruStavZiadosti[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            CsruStavZiadosti b = var1[var3];
            if (b.value.equals(value)) {
                return b;
            }
        }

        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    CsruStavZiadosti(String value) {
        this.value = value;
    }
}
