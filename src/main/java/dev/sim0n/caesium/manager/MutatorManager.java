package dev.sim0n.caesium.manager;

import dev.sim0n.caesium.Caesium;
import dev.sim0n.caesium.config.CaesiumConfig;
import dev.sim0n.caesium.mutator.ClassMutator;
import dev.sim0n.caesium.mutator.impl.*;
import dev.sim0n.caesium.mutator.impl.renamer.*;
import dev.sim0n.caesium.mutator.impl.crasher.BadAnnotationMutator;
import dev.sim0n.caesium.mutator.impl.crasher.ImageCrashMutator;
import dev.sim0n.caesium.util.wrapper.impl.ClassWrapper;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MutatorManager {
    private final Caesium caesium = Caesium.getInstance();

    private final List<ClassMutator> mutators = new ArrayList<>();

    public MutatorManager() {
        mutators.add(new ClassFolderMutator());
        mutators.add(new ImageCrashMutator());
        mutators.add(new ShuffleMutator());
        mutators.add(new TrimMutator());
        mutators.add(new LineNumberMutator());
        mutators.add(new LocalVariableMutator());
        mutators.add(new StringMutator());
        mutators.add(new ClassRenameMutator());
        mutators.add(new MethodRenameMutator());
        mutators.add(new FieldRenameMutator());
        mutators.add(new ControlFlowMutator());
        mutators.add(new NumberMutator());
        mutators.add(new PolymorphMutator());
        mutators.add(new ReferenceMutator());
        mutators.add(new BadAnnotationMutator());
    }

    public void applyConfig(CaesiumConfig config) {
        CaesiumConfig.Mutators m = config.getMutators();

        applyEntry(ClassFolderMutator.class,   m.getClassFolder());
        applyEntry(ImageCrashMutator.class,    m.getImageCrash());
        applyEntry(ShuffleMutator.class,       m.getShuffle());
        applyEntry(ClassRenameMutator.class, m.getClassRename());
        applyEntry(MethodRenameMutator.class, m.getMethodRename());
        applyEntry(FieldRenameMutator.class, m.getFieldRename());
        applyEntry(TrimMutator.class,          m.getTrim());
        applyEntry(ControlFlowMutator.class,   m.getControlFlow());
        applyEntry(NumberMutator.class,        m.getNumber());
        applyEntry(PolymorphMutator.class,     m.getPolymorph());
        applyEntry(BadAnnotationMutator.class, m.getBadAnnotation());
        

        LineNumberMutator lnm = getMutator(LineNumberMutator.class);
        if (lnm != null && m.getLineNumber() instanceof CaesiumConfig.LineNumberEntry lne) {
            lnm.setEnabled(lne.isEnabled());
            lnm.setType(lne.getType());
        }

        LocalVariableMutator lvm = getMutator(LocalVariableMutator.class);
        if (lvm != null && m.getLocalVariable() instanceof CaesiumConfig.LocalVariableEntry lve) {
            lvm.setEnabled(lve.isEnabled());
            lvm.setType(lve.getType());
        }

        StringMutator sm = getMutator(StringMutator.class);
        if (sm != null) {
            sm.setEnabled(m.getString().isEnabled());
            if (m.getString() instanceof CaesiumConfig.StringEntry se && se.getExclusions() != null)
                sm.getExclusions().addAll(se.getExclusions());
        }

        ReferenceMutator rm = getMutator(ReferenceMutator.class);
        if (rm != null) {
            rm.setEnabled(m.getReference().isEnabled());
            if (m.getReference() instanceof CaesiumConfig.ReferenceEntry re) {
                rm.setType(re.getType());
                if (re.getExclusions() != null)
                    rm.getExclusions().addAll(re.getExclusions());
            }
        }
    }

    private void applyEntry(Class<? extends ClassMutator> clazz, CaesiumConfig.MutatorEntry entry) {
        ClassMutator mutator = getMutator(clazz);
        if (mutator != null) mutator.setEnabled(entry.isEnabled());
    }

    @SuppressWarnings("unchecked")
    public <T extends ClassMutator> T getMutator(Class<T> clazz) {
        return (T) mutators.stream()
                .filter(mutator -> mutator.getClass() == clazz)
                .findFirst()
                .orElse(null);
    }

    public void handleMutation(ClassWrapper clazz) {
        mutators.stream()
                .filter(ClassMutator::isEnabled)
                .forEach(mutator -> mutator.handle(clazz));
    }

    public void handleMutationFinish() {
        mutators.stream()
                .filter(ClassMutator::isEnabled)
                .forEach(mutator -> {
                    mutator.handleFinish();
                    caesium.separator();
                });
    }

    public List<ClassMutator> getMutators() { return mutators; }
}
