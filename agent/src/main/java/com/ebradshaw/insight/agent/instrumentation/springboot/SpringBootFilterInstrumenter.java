package com.ebradshaw.insight.agent.instrumentation.springboot;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;

public class SpringBootFilterInstrumenter implements ClassFileTransformer {

    private String[] classes = new String[]{
            "org/springframework/boot/context/embedded/AnnotationConfigEmbeddedWebApplicationContext",
            "org/springframework/context/annotation/AnnotationConfigApplicationContext",
            "org/springframework/web/context/support/AnnotationConfigWebApplicationContext"
    };

    public SpringBootFilterInstrumenter() {
        this(new String[]{
                "org/springframework/boot/context/embedded/AnnotationConfigEmbeddedWebApplicationContext",
                "org/springframework/context/annotation/AnnotationConfigApplicationContext",
                "org/springframework/web/context/support/AnnotationConfigWebApplicationContext"
        });
    }

    SpringBootFilterInstrumenter(String[] classes) {
        this.classes = classes;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[]
            classfileBuffer) throws IllegalClassFormatException {
        for (String prefix : classes) {
            if (className.startsWith(prefix)) {
                return instrument(classfileBuffer, className);
            }
        }
        return classfileBuffer;
    }

    public byte[] instrument(byte[] originalBytes, String className) {
        try {
            ClassReader cr = new ClassReader(originalBytes);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
            ClassVisitor adapter = new RegisterSpringBootConfiguration(cw, className);
            cr.accept(adapter, ClassReader.SKIP_FRAMES);
            return cw.toByteArray();
        } catch (RuntimeException e) {
            throw e;
        } catch (Error e) {
            throw e;
        }
    }

    private static class RegisterSpringBootConfiguration extends ClassVisitor {

        private final String className;

        public RegisterSpringBootConfiguration(ClassVisitor cv, String className) {
            super(Opcodes.ASM5, cv);
            this.className = className;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc,
                                         String signature, String[] exceptions) {
            MethodVisitor mv =
                    cv.visitMethod(access, name, desc, signature, exceptions);

            if ((mv != null) && "<init>".equals(name)) {
                mv = new ConstructorMethodAdapter(mv, className);
            }
            return mv;
        }
    }

    private static class ConstructorMethodAdapter extends MethodVisitor {

        private final String className;

        public ConstructorMethodAdapter(MethodVisitor mv, String className) {
            super(Opcodes.ASM5, mv);
            this.className = className;
        }

        @Override
        public void visitInsn(int opcode) {
            if ((opcode == Opcodes.ARETURN) ||
                    (opcode == Opcodes.IRETURN) ||
                    (opcode == Opcodes.LRETURN) ||
                    (opcode == Opcodes.FRETURN) ||
                    (opcode == Opcodes.DRETURN)) {
                throw new RuntimeException(new UnmodifiableClassException(
                        "Constructors are supposed to return void"));
            }
            if (opcode == Opcodes.RETURN) {
                super.visitVarInsn(Opcodes.ALOAD, 0);                           // -> stack: ... this
                super.visitInsn(Opcodes.ICONST_1);                              // -> stack: ... this 1
                super.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Class");      // -> stack: ... this Class[1]{null}
                super.visitInsn(Opcodes.DUP);                                   // -> stack: ... this Class[1]{null} Class[1]{null}
                super.visitInsn(Opcodes.ICONST_0);                              // -> stack: ... this Class[1]{null} Class[1]{null} 0
                super.visitLdcInsn(Type.getType("Lcom/ebradshaw/insight/agent/servlet/InsightFilter;")); // -> stack: ... this
                // Class[1]{null} Class[1]{null} 0 InsightFilter.class
                super.visitInsn(Opcodes.AASTORE); // -> stack: ... this Class[1]{InsightFilter.class}
                super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, className, "register", "([Ljava/lang/Class;)V", false); // -> stack: ...
                super.visitVarInsn(Opcodes.ALOAD, 0); // -> stack: ... this                           // -> stack: ... this
                super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, className, "prepareRefresh", "()V", false); // -> stack: ...
            }
            super.visitInsn(opcode);
        }
    }


}
