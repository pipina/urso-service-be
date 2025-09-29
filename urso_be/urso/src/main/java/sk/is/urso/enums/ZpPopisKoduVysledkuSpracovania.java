package sk.is.urso.enums;

import lombok.Getter;

public enum ZpPopisKoduVysledkuSpracovania {

    ZASLANE_UDAJE_OD_ZP("ZASLANE_UDAJE_OD_ZP"),
    NIE_JE_EVIDOVANY("NIE_JE_EVIDOVANY"),
    NIE_SU_EVIDOVANE_ZIADNE_UDAJE("NIE_SU_EVIDOVANE_ZIADNE_UDAJE"),
    NESULAD_SUBJEKTU_V_OBALKE_A_OBSAHU("NESULAD_SUBJEKTU_V_OBALKE_A_OBSAHU"),
    NESULAD_RC_ICO_IFO("NESULAD_RC_ICO_IFO"),
    NEEVIDOVANIY_PARTNER_PRE_POSKYTNUTIE_UDAJOV("NEEVIDOVANIY_PARTNER_PRE_POSKYTNUTIE_UDAJOV"),
    NEZNAMY("NEZNAMY");

    @Getter
    private final String value;

    public static ZpPopisKoduVysledkuSpracovania fromValue(String value) {
        ZpPopisKoduVysledkuSpracovania[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            ZpPopisKoduVysledkuSpracovania b = var1[var3];
            if (b.value.equals(value)) {
                return b;
            }
        }

        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    ZpPopisKoduVysledkuSpracovania(String value) {
        this.value = value;
    }
}
