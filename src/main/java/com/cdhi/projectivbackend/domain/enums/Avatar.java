package com.cdhi.projectivbackend.domain.enums;

public enum Avatar {

    A0(0, "AVATAR 0"),
    A1(1, "AVATAR 1"),
    A2(2, "AVATAR 2"),
    A3(3, "AVATAR 3"),
    A4(4, "AVATAR 4"),
    A5(5, "AVATAR 5"),
    A6(6, "AVATAR 6"),
    A7(7, "AVATAR 7"),
    A8(8, "AVATAR 8"),
    A9(9, "AVATAR 9"),
    A10(10, "AVATAR 10");

    private int cod;
    private String description;

    Avatar(int cod, String description) {
        this.cod = cod;
        this.description = description;
    }

    public int getCod() {
        return cod;
    }

    public String getDescription () {
        return description;
    }

    public static Avatar toEnum(Integer cod) {
        if (cod == null) {
            return null;
        }

        for (Avatar x : Avatar.values()) {
            if (cod.equals(x.getCod())) {
                return x;
            }
        }
        throw new IllegalArgumentException("Invalid Id: " + cod);
    }
}
