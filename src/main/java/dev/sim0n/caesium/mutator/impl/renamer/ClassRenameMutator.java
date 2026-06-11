package dev.sim0n.caesium.mutator.impl.renamer;

import dev.sim0n.caesium.manager.ClassManager;
import dev.sim0n.caesium.mutator.ClassMutator;
import dev.sim0n.caesium.util.wrapper.impl.ClassWrapper;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.SimpleRemapper;
import org.objectweb.asm.tree.ClassNode;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ClassRenameMutator extends ClassMutator {

    private final Map<String, String> mappings = new HashMap<>();

    @Override
    public void handle(ClassWrapper wrapper) {
        if (wrapper.libraryNode)
            return;

        String originalName = wrapper.node.name;

        if (originalName.contains("$"))
            return;

        if (originalName.equals("module-info") || originalName.equals("package-info"))
            return;

        String newName = buildNewName(originalName, getRandomName());

        mappings.put(originalName, newName);
        ++counter;
    }

    private String buildNewName(String originalName, String randomPart) {
        int lastSlash = originalName.lastIndexOf('/');
        if (lastSlash == -1)
            return randomPart;

        return originalName.substring(0, lastSlash + 1) + randomPart;
    }

    @Override
    public void handleFinish() {
        if (mappings.isEmpty()) {
            logger.info("Renamed 0 classes");
            return;
        }

        ClassManager classManager = caesium.getClassManager();
        SimpleRemapper remapper = new SimpleRemapper(mappings);

        Map<ClassWrapper, String> original = new HashMap<>(classManager.getClasses());

        original.forEach((wrapper, fileName) -> {
            ClassNode renamed = new ClassNode();
            ClassRemapper classRemapper = new ClassRemapper(renamed, remapper);
            wrapper.node.accept(classRemapper);

            wrapper.node.name        = renamed.name;
            wrapper.node.superName   = renamed.superName;
            wrapper.node.interfaces  = renamed.interfaces;
            wrapper.node.fields      = renamed.fields;
            wrapper.node.methods     = renamed.methods;
            wrapper.node.innerClasses = renamed.innerClasses;
            wrapper.node.outerClass  = renamed.outerClass;
            wrapper.node.outerMethod = renamed.outerMethod;

            String newFileName = mappings.getOrDefault(
                    fileName.replace(".class", ""),
                    fileName.replace(".class", "")
            ) + ".class";

            classManager.getClasses().remove(wrapper);
            classManager.getClasses().put(wrapper, newFileName);
        });

        logger.info("Renamed {} classes", counter);
    }

    public Map<String, String> getMappings() {
        return Collections.unmodifiableMap(mappings);
    }
}
