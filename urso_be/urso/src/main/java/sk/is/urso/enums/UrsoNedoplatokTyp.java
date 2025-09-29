package sk.is.urso.enums;

import lombok.Getter;

public enum UrsoNedoplatokTyp {

    FS("FS"),
    SP("SP"),
    ZP("ZP");

    @Getter
    private final String value;

    public static UrsoNedoplatokTyp fromValue(String value) {
        UrsoNedoplatokTyp[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            UrsoNedoplatokTyp b = var1[var3];
            if (b.value.equals(value)) {
                return b;
            }
        }

        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    UrsoNedoplatokTyp(String value) {
        this.value = value;
    }
}
