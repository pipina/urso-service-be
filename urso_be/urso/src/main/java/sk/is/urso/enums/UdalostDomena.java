package sk.is.urso.enums;

import lombok.Getter;

public enum UdalostDomena {

    CISELNIK("ciselnik"),
    HODNOTA_CISELNIKA("hodnota-ciselnika"),
    REGISTER("register"),
    HODNOTA_REGISTRA("hodnota-registra");

    @Getter
    private final String value;

    public static UdalostDomena fromValue(String value) {
        UdalostDomena[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            UdalostDomena b = var1[var3];
            if (b.value.equals(value)) {
                return b;
            }
        }

        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    UdalostDomena(String value) {
        this.value = value;
    }
}
