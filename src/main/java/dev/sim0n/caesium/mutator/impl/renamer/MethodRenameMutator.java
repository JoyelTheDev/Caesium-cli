package dev.sim0n.caesium.mutator.impl.renamer;

import dev.sim0n.caesium.manager.ClassManager;
import dev.sim0n.caesium.mutator.ClassMutator;
import dev.sim0n.caesium.util.wrapper.impl.ClassWrapper;
import dev.sim0n.caesium.util.wrapper.impl.MethodWrapper;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class MethodRenameMutator extends ClassMutator {

    private static final String KEY_SEP = "#";

    private final Map<String, String> mappings = new HashMap<>();

    @Override
    public void handle(ClassWrapper wrapper) {
        if (wrapper.libraryNode)
            return;

        wrapper.methods.stream()
                .filter(this::isRenameable)
                .forEach(method -> {
                    String key = wrapper.node.name + KEY_SEP + method.node.name + method.node.desc;
                    mappings.computeIfAbsent(key, k -> {
                        ++counter;
                        return getRandomName();
                    });
                });
    }

    private boolean isRenameable(MethodWrapper method) {
        String name = method.node.name;
        return !name.equals("<init>")
                && !name.equals("<clinit>")
                && !name.equals("main")
                && (method.node.access & ACC_NATIVE) == 0;
    }

    @Override
    public void handleFinish() {
        if (mappings.isEmpty()) {
            logger.info("Renamed 0 methods");
            return;
        }

        ClassManager classManager = caesium.getClassManager();

        classManager.getClasses().keySet().forEach(wrapper -> {
            String key = wrapper.node.name + KEY_SEP;

            mappings.entrySet().stream()
                    .filter(e -> e.getKey().startsWith(wrapper.node.name + KEY_SEP))
                    .forEach(e -> {
                        String nameDesc = e.getKey().substring(wrapper.node.name.length() + 1);
                        int descStart   = nameDesc.indexOf('(');
                        String oldName  = nameDesc.substring(0, descStart);
                        String desc     = nameDesc.substring(descStart);

                        wrapper.node.methods.stream()
                                .filter(m -> m.name.equals(oldName) && m.desc.equals(desc))
                                .forEach(m -> m.name = e.getValue());
                    });

            wrapper.methods.forEach(methodWrapper -> {
                if (!methodWrapper.hasInstructions())
                    return;

                Stream.of(methodWrapper.getInstructions().toArray())
                        .forEach(insn -> {
                            if (insn instanceof MethodInsnNode) {
                                MethodInsnNode min = (MethodInsnNode) insn;
                                String callKey = min.owner + KEY_SEP + min.name + min.desc;
                                if (mappings.containsKey(callKey))
                                    min.name = mappings.get(callKey);
                            } else if (insn instanceof InvokeDynamicInsnNode) {
                                InvokeDynamicInsnNode idin = (InvokeDynamicInsnNode) insn;
                                String callKey = idin.bsm.getOwner() + KEY_SEP + idin.name + idin.desc;
                                if (mappings.containsKey(callKey))
                                    idin.name = mappings.get(callKey);
                            }
                        });
            });
        });

        logger.info("Renamed {} methods", counter);
    }
}
