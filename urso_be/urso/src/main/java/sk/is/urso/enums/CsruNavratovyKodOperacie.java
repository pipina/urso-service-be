package sk.is.urso.enums;

import lombok.Getter;

public enum CsruNavratovyKodOperacie {

    OK("OK"),
    CHYBA_OVERENIA_OPRAVNENI("CHYBA_OVERENIA_OPRAVNENI"),
    CHYBA_VALIDACIE_VSTUPNYCH_PARAMETROV("CHYBA_VALIDACIE_VSTUPNYCH_PARAMETROV"),
    INTERNA_CHYBA("INTERNA_CHYBA"),
    NEPLATNE_ID_POZIADAVKY("NEPLATNE_ID_POZIADAVKY"),
    NEZNAMY("NEZNAMY");

    @Getter
    private final String value;

    public static CsruNavratovyKodOperacie fromValue(String value) {
        CsruNavratovyKodOperacie[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            CsruNavratovyKodOperacie b = var1[var3];
            if (b.value.equals(value)) {
                return b;
            }
        }

        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    CsruNavratovyKodOperacie(String value) {
        this.value = value;
    }
}
