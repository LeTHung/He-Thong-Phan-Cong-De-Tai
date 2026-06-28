package com.ptit.doancnpm.view;

import com.ptit.doancnpm.app.MainApp;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ViewContractTest {

    @Test
    void everyFxmlReferencesExistingControllerFieldsAndHandlers() throws Exception {
        Path views = Path.of("src/main/resources/views");
        try (Stream<Path> files = Files.walk(views)) {
            for (Path file : files.filter(path -> path.toString().endsWith(".fxml")).toList()) {
                validateFxml(file);
            }
        }
    }

    @Test
    void everyMainAppViewConstantPointsToAResource() throws Exception {
        for (Field field : MainApp.class.getFields()) {
            if (Modifier.isStatic(field.getModifiers())
                    && field.getType() == String.class
                    && field.getName().endsWith("_VIEW")) {
                String resource = (String) field.get(null);
                assertNotNull(MainApp.class.getResource(resource),
                        () -> field.getName() + " trỏ tới resource không tồn tại: " + resource);
            }
        }
    }

    private void validateFxml(Path file) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        Element root = factory.newDocumentBuilder().parse(file.toFile()).getDocumentElement();
        String controllerName = root.getAttribute("fx:controller");
        assertTrue(!controllerName.isBlank(), () -> file + " thiếu fx:controller");

        Class<?> controller = Class.forName(controllerName);
        Set<String> fieldNames = new HashSet<>();
        for (Field field : controller.getDeclaredFields()) {
            fieldNames.add(field.getName());
        }
        Set<String> methodNames = new HashSet<>();
        for (Method method : controller.getDeclaredMethods()) {
            methodNames.add(method.getName());
        }

        validateNode(file, root, fieldNames, methodNames);
    }

    private void validateNode(
            Path file,
            Node node,
            Set<String> fieldNames,
            Set<String> methodNames) {
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            Node id = attributes.getNamedItem("fx:id");
            if (id != null) {
                assertTrue(fieldNames.contains(id.getNodeValue()),
                        () -> file + " tham chiếu fx:id không có field: " + id.getNodeValue());
            }
            for (int index = 0; index < attributes.getLength(); index++) {
                String value = attributes.item(index).getNodeValue();
                if (value.startsWith("#")) {
                    String handler = value.substring(1);
                    assertTrue(methodNames.contains(handler),
                            () -> file + " tham chiếu handler không tồn tại: " + handler);
                }
            }
        }

        for (int index = 0; index < node.getChildNodes().getLength(); index++) {
            validateNode(file, node.getChildNodes().item(index), fieldNames, methodNames);
        }
    }
}
