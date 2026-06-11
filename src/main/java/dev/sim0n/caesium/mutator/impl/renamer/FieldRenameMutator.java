package dev.sim0n.caesium.mutator.impl.renamer;

import dev.sim0n.caesium.manager.ClassManager;
import dev.sim0n.caesium.mutator.ClassMutator;
import dev.sim0n.caesium.util.wrapper.impl.ClassWrapper;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class FieldRenameMutator extends ClassMutator {

    private static final String KEY_SEP = "#";

    private final Map<String, String> mappings = new HashMap<>();

    @Override
    public void handle(ClassWrapper wrapper) {
        if (wrapper.libraryNode)
            return;

        wrapper.fields.forEach(fieldWrapper -> {
            String key = wrapper.node.name + KEY_SEP + fieldWrapper.node.name + KEY_SEP + fieldWrapper.node.desc;
            mappings.computeIfAbsent(key, k -> {
                ++counter;
                return getRandomName();
            });
        });
    }

    @Override
    public void handleFinish() {
        if (mappings.isEmpty()) {
            logger.info("Renamed 0 fields");
            return;
        }

        ClassManager classManager = caesium.getClassManager();

        classManager.getClasses().keySet().forEach(wrapper -> {
            mappings.entrySet().stream()
                    .filter(e -> e.getKey().startsWith(wrapper.node.name + KEY_SEP))
                    .forEach(e -> {
                        String remainder = e.getKey().substring(wrapper.node.name.length() + 1);
                        int sep          = remainder.lastIndexOf(KEY_SEP);
                        String oldName   = remainder.substring(0, sep);
                        String desc      = remainder.substring(sep + 1);

                        wrapper.node.fields.stream()
                                .filter(f -> f.name.equals(oldName) && f.desc.equals(desc))
                                .forEach(f -> f.name = e.getValue());
                    });

            wrapper.methods.forEach(methodWrapper -> {
                if (!methodWrapper.hasInstructions())
                    return;

                Stream.of(methodWrapper.getInstructions().toArray())
                        .filter(FieldInsnNode.class::isInstance)
                        .map(FieldInsnNode.class::cast)
                        .forEach(fin -> {
                            String fieldKey = fin.owner + KEY_SEP + fin.name + KEY_SEP + fin.desc;
                            if (mappings.containsKey(fieldKey))
                                fin.name = mappings.get(fieldKey);
                        });
            });
        });

        logger.info("Renamed {} fields", counter);
    }
}
