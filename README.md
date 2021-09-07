UI Fields Annotation processor

### Source
```java
public class Test {

    @UIField(title = "localized")
    private String field;
}

public class Test2 {

    @UIField(field = "testField", title = "testTitle")
    private String field;
}
```
### Generated
```java
public enum TestFields {

    FIELD("field","localized"),
    ;

    private final String handle;
    private final String title;

    TestFields(String handle, String title) {
        this.handle = handle;
        this.title = title;
    }

    public String getHandle() {
        return handle;
    }

    public String getTitle() {
        return title;
    }
}

public enum Test2Fields {

    TESTFIELD("testField","testTitle"),
    ;

    private final String handle;
    private final String title;

    Test2Fields(String handle, String title) {
        this.handle = handle;
        this.title = title;
    }

    public String getHandle() {
        return handle;
    }

    public String getTitle() {
        return title;
    }
}
```
