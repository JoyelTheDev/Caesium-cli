package dev.sim0n.caesium.util.wrapper.impl;

import dev.sim0n.caesium.util.wrapper.Wrapper;
import lombok.Getter;
import org.objectweb.asm.commons.CodeSizeEvaluator;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

public class MethodWrapper implements Wrapper {
    public final MethodNode node;

    public MethodWrapper(MethodNode node) {
        this.node = node;
    }

    public int getMaxSize() {
        CodeSizeEvaluator evaluator = new CodeSizeEvaluator(null);
        node.accept(evaluator);
        return evaluator.getMaxSize();
    }

    public boolean hasInstructions() {
        return node.instructions != null && node.instructions.size() > 0;
    }

    public InsnList getInstructions() {
        return node.instructions;
    }
}
