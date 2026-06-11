package dev.sim0n.caesium.mutator.impl;

import dev.sim0n.caesium.mutator.ClassMutator;
import dev.sim0n.caesium.util.wrapper.impl.ClassWrapper;
import dev.sim0n.caesium.util.wrapper.impl.MethodWrapper;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LineNumberNode;

import java.util.stream.Stream;

public class LineNumberMutator extends ClassMutator {

    /**
     * 0 = remove
     * 1 = scramble
     */
    private int type = 0;

    public void setType(int type) { this.type = type; }

    @Override
    public void handle(ClassWrapper wrapper) {
        switch (type) {
            case 0:
                wrapper.node.sourceFile = null;
                break;
            case 1:
                wrapper.node.sourceFile = getRandomName();
                break;
        }

        wrapper.methods.stream()
                .filter(MethodWrapper::hasInstructions)
                .forEach(method -> {
                    InsnList instructions = method.getInstructions();

                    Stream.of(instructions.toArray())
                            .filter(LineNumberNode.class::isInstance)
                            .map(LineNumberNode.class::cast)
                            .forEach(lineNumber -> {
                                switch (type) {
                                    case 0:
                                        instructions.remove(lineNumber);
                                        break;
                                    case 1:
                                        lineNumber.line = Math.abs(random.nextInt(2000));
                                        break;
                                }
                                ++counter;
                            });
                });
    }

    @Override
    public void handleFinish() {
        String output = "";
        switch (type) {
            case 0: output = "Removed {} line numbers"; break;
            case 1: output = "Scrambled {} line numbers"; break;
        }
        logger.info(output, counter);
    }
}
