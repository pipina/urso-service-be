package sk.is.urso.enums;

import lombok.Getter;

public enum UrsoSubjectStav {

    PREBIEHA("PREBIEHA"),
    SPRACOVANE("SPRACOVANE"),
    CHYBA("CHYBA");

    @Getter
    private final String value;

    public static UrsoSubjectStav fromValue(String value) {
        UrsoSubjectStav[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            UrsoSubjectStav b = var1[var3];
            if (b.value.equals(value)) {
                return b;
            }
        }

        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    UrsoSubjectStav(String value) {
        this.value = value;
    }
}
