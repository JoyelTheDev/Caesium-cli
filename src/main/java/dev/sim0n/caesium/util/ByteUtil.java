package dev.sim0n.caesium.util;

import dev.sim0n.caesium.util.classwriter.CaesiumClassWriter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

public final class ByteUtil {

    private ByteUtil() {}

    /**
     * Converts a byte array to a {@link ClassNode}
     */
    public static ClassNode parseClassBytes(byte[] bytes) {
        ClassReader reader = new ClassReader(bytes);
        ClassNode classNode = new ClassNode();
        reader.accept(classNode, 0);
        return classNode;
    }

    /**
     * Converts a {@link ClassNode} to a byte array
     */
    public static byte[] getClassBytes(ClassNode classNode) {
        CaesiumClassWriter classWriter = new CaesiumClassWriter(CaesiumClassWriter.COMPUTE_FRAMES);
        classWriter.newUTF8("caesium");
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    /**
     * Converts bytes to KB
     */
    public static double bytesToKB(long bytes) {
        return bytes / 1024D;
    }
}
