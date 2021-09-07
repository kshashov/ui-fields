package org.github.kshashov.uifields;

import com.google.auto.service.AutoService;
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
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);

            annotatedElements.forEach(element -> {
                VariableElement variableElement = (VariableElement) element;
//                DeclaredType declaredType = (DeclaredType) element.asType();
                String className = ((TypeElement) variableElement.getEnclosingElement()).getQualifiedName().toString();
                if (!classFields.containsKey(className)) {
                    classFields.put(className, new LinkedList<>());
                }

                classFields.get(className).add(variableElement);
            });
        }

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

        String simpleClassName = className.substring(lastDot + 1);
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

            out.print("public enum ");
            out.print(fieldsSimpleClassName);
            out.println(" {");
            out.println();

//            out.print("    private ");
//            out.print(simpleClassName);
//            out.print(" object = new ");
//            out.print(simpleClassName);
//            out.println("();");
//            out.println();
//
//            out.print("    public ");
//            out.print(simpleClassName);
//            out.println(" build() {");
//            out.println("        return object;");
//            out.println("    }");
//            out.println();

            fields.forEach(element -> {
                UIField annotation = element.getAnnotation(UIField.class);
                String fieldTitle = annotation.title();
                String fieldField = annotation.field();
                if (fieldField.isEmpty()) {
                    fieldField = element.getSimpleName().toString();
                }

                out.print("    ");
                out.print(fieldField.toUpperCase());
                out.print("(");
                out.print("\"");
                out.print(fieldField);
                out.print("\",");
                out.print("\"");
                out.print(fieldTitle);
                out.println("\"),");
            });

            out.println("    ;");

            out.println("");

            out.println("    private final String handle;");
            out.println("    private final String title;");
            out.println();

            out.print("    ");
            out.print(fieldsSimpleClassName);
            out.println("(String handle, String title) {");
            out.println("        this.handle = handle;");
            out.println("        this.title = title;");
            out.println("    }");

            out.println();

            out.println("    public String getHandle() {");
            out.println("        return handle;");
            out.println("    }");
            out.println();

            out.println("    public String getTitle() {");
            out.println("        return title;");
            out.println("    }");

            out.println("}");
        }
    }
}
