## UI Fields Annotation processor

### @UIField

Sometimes you may need to add a enum companion for your DTO, for example, to conveniently store localization codes for
fields. This library is an annotation processor that generates such enums. All you need to do is add it to dependencies and mark the
fields with the `UIField` annotation. 

`@UIField` has the following properties:
* `enumProperty` - title for enum property; by default it is the upper underscore version of the field name
* `title`, `caption` - just business logic related fields that will go to enum as they are; use them on your own

### Examples

#### Source

```java
public class Test {

    @UIField(title = "localized")
    private String field;
}

public class Test2 {

    @UIField(enumProperty = "testField", title = "testTitle")
    private String field;
}
```

#### Generated

```java
public enum TestFields {

    FIELD("field", "localized", ""),
    ;

    private final String handle;
    private final String title;
    private final String caption;

    TestFields(String handle, String title, String caption) {
        this.handle = handle;
        this.title = title;
        this.caption = caption;
    }

    public String getHandle() {
        return handle;
    }

    public String getTitle() {
        return title;
    }

    public String getCaption() {
        return caption;
    }
}

public enum Test2Fields {

    testField("field", "testTitle", ""),
    ;

    private final String handle;
    private final String title;
    private final String caption;

    Test2Fields(String handle, String title, String caption) {
        this.handle = handle;
        this.title = title;
        this.caption = caption;
    }

    public String getHandle() {
        return handle;
    }

    public String getTitle() {
        return title;
    }

    public String getCaption() {
        return caption;
    }
}
```
