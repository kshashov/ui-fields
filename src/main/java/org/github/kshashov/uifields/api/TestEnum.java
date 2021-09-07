package org.github.kshashov.uifields.api;

public enum TestEnum {
    TEST_ENUM("",""),;

    public final String name;
    public final String field;

    TestEnum(String name, String field) {
        this.name = name;
        this.field = field;
    }
}
