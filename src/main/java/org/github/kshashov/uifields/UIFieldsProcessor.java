package org.github.kshashov.uifields;

import com.google.auto.service.AutoService;
import com.google.common.base.CaseFormat;
import org.github.kshashov.uifields.api.UIField;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * https://www.baeldung.com/java-annotation-processing-builder
 */
@SupportedAnnotationTypes(
        "org.github.kshashov.uifields.api.UIField")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class UIFieldsProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<String, List<VariableElement>> classFields = new HashMap<>();

        // Populate map with (class name -> ui fields) pairs
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);

            annotatedElements.forEach(element -> {
                VariableElement variableElement = (VariableElement) element;
                String className = ((TypeElement) variableElement.getEnclosingElement()).getQualifiedName().toString();
                if (!classFields.containsKey(className)) {
                    classFields.put(className, new LinkedList<>());
                }

                classFields.get(className).add(variableElement);
            });
        }

        // Create a new {ClassName}Fields enum for each source class
        for (Map.Entry<String, List<VariableElement>> entry : classFields.entrySet()) {
            StringBuilder value = new StringBuilder();
            for (VariableElement element : entry.getValue()) {
                value.append(element.getSimpleName()).append(" (").append(element.getAnnotation(UIField.class).title()).append(")");
            }
            System.out.println(entry.getKey() + ": " + value);
            try {
                writeFieldsFile(entry.getKey(), entry.getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    private void writeFieldsFile(String className, List<VariableElement> fields) throws IOException {

        String packageName = null;
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }

        String fieldsClassName = className + "Fields";
        String fieldsSimpleClassName = fieldsClassName.substring(lastDot + 1);

        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(fieldsClassName);

        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {

            if (packageName != null) {
                out.print("package ");
                out.print(packageName);
                out.println(";");
                out.println();
            }

            out.println("import org.github.kshashov.uifields.api.UIFieldDeclaration;");
            out.println();

            out.print("public enum ");
            out.print(fieldsSimpleClassName);
            out.println(" implements UIFieldDeclaration{");
            out.println();

            fields.forEach(element -> {
                UIField annotation = element.getAnnotation(UIField.class);
                String fieldTitle = annotation.title();
                String fieldHandle = element.getSimpleName().toString();
                String fieldEnumProperty = annotation.enumProperty();
                String fieldCaption = annotation.caption();
                if (fieldEnumProperty.isEmpty()) {
                    fieldEnumProperty = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, fieldHandle);
                }

                out.print("    ");
                out.print(fieldEnumProperty);
                out.print("(");
                out.print("\"");
                out.print(fieldHandle);
                out.print("\",");
                out.print("\"");
                out.print(fieldTitle);
                out.print("\",");
                out.print("\"");
                out.print(fieldCaption);
                out.println("\"),");
            });

            out.println("    ;");

            out.println("");

            out.println("    private final String handle;");
            out.println("    private final String title;");
            out.println("    private final String caption;");
            out.println();

            out.print("    ");
            out.print(fieldsSimpleClassName);
            out.println("(String handle, String title, String caption) {");
            out.println("        this.handle = handle;");
            out.println("        this.title = title;");
            out.println("        this.caption = caption;");
            out.println("    }");

            out.println();

            out.println("    @Override");
            out.println("    public String getHandle() {");
            out.println("        return handle;");
            out.println("    }");
            out.println();

            out.println("    @Override");
            out.println("    public String getTitle() {");
            out.println("        return title;");
            out.println("    }");
            out.println();

            out.println("    @Override");
            out.println("    public String getCaption() {");
            out.println("        return caption;");
            out.println("    }");

            out.println("}");
        }
    }
}
