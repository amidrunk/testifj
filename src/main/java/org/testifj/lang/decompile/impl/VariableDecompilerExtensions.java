package org.testifj.lang.decompile.impl;

import org.testifj.lang.*;
import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.classfile.LocalVariable;
import org.testifj.lang.decompile.DecompilationContext;
import org.testifj.lang.decompile.DecompilerConfiguration;
import org.testifj.lang.decompile.DecompilerExtension;
import org.testifj.lang.model.AST;

import java.util.Optional;

import static org.testifj.lang.classfile.ByteCode.*;

public final class VariableDecompilerExtensions {

    public static void configure(DecompilerConfiguration.Builder configurationBuilder) {
        assert configurationBuilder != null : "Configuration builder can't be null";

        configurationBuilder.on(iload, lload, fload, dload, aload).then(load());
        configurationBuilder.on(istore, lstore, fstore, dstore, astore).then(store());
        configurationBuilder.on(iload_0, iload_3).then(iloadn());
        configurationBuilder.on(fload_0, fload_3).then(floadn());
        configurationBuilder.on(dload_0, dload_3).then(dloadn());
        configurationBuilder.on(lload_0, lload_3).then(lloadn());
        configurationBuilder.on(aload_0, aload_3).then(aloadn());
        configurationBuilder.on(istore_0, istore_3).then(istoren());
        configurationBuilder.on(fstore_0, fstore_3).then(fstoren());
        configurationBuilder.on(dstore_0, dstore_3).then(dstoren());
        configurationBuilder.on(lstore_0, lstore_3).then(lstoren());
        configurationBuilder.on(astore_0, astore_3).then(astoren());
    }

    public static DecompilerExtension load() {
        return (context,codeStream,byteCode) -> {
            load(context, codeStream.nextByte());
            return true;
        };
    }

    public static DecompilerExtension store() {
        return (context,codeStream,byteCode) -> {
            store(context, codeStream.nextByte());
            return true;
        };
    }

    public static DecompilerExtension istoren() {
        return (context,codeStream,byteCode) -> {
            store(context, byteCode - ByteCode.istore_0);
            return true;
        };
    }

    public static DecompilerExtension fstoren() {
        return (context,codeStream,byteCode) -> {
            store(context, byteCode - ByteCode.fstore_0);
            return true;
        };
    }

    public static DecompilerExtension dstoren() {
        return (context,codeStream,byteCode) -> {
            store(context, byteCode - ByteCode.dstore_0);
            return true;
        };
    }

    public static DecompilerExtension lstoren() {
        return (context,codeStream,byteCode) -> {
            store(context, byteCode - ByteCode.lstore_0);
            return true;
        };
    }

    public static DecompilerExtension astoren() {
        return (context,codeStream,byteCode) -> {
            store(context, byteCode - ByteCode.astore_0);
            return true;
        };
    }

    public static DecompilerExtension iloadn() {
        return (context,codeStream,byteCode) -> {
            load(context, byteCode - ByteCode.iload_0);
            return true;
        };
    }

    public static DecompilerExtension floadn() {
        return (context,codeStream,byteCode) -> {
            load(context, byteCode - ByteCode.fload_0);
            return true;
        };
    }

    public static DecompilerExtension dloadn() {
        return (context,codeStream,byteCode) -> {
            load(context, byteCode - ByteCode.dload_0);
            return true;
        };
    }

    public static DecompilerExtension lloadn() {
        return (context,codeStream,byteCode) -> {
            load(context, byteCode - ByteCode.lload_0);
            return true;
        };
    }

    public static DecompilerExtension aloadn() {
        return (context,codeStream,byteCode) -> {
            load(context, byteCode - ByteCode.aload_0);
            return true;
        };
    }

    private static void store(DecompilationContext context, int index) {
        final int pc = context.getProgramCounter().get();
        final Optional<LocalVariable> localVariableOptional = Methods.findLocalVariableForIndexAndPC(context.getMethod(), index, pc + 1);

        if (!localVariableOptional.isPresent()) {
            throw new LocalVariableNotAvailableException("No variable exists at index " + index + "(pc=" + pc + ")");
        }

        final LocalVariable localVariable = localVariableOptional.get();

        context.enlist(AST.set(index, localVariable.getName(), localVariable.getType(), context.pop()));
    }

    private static void load(DecompilationContext context, int index) {
        final int pc = context.getProgramCounter().get();
        final Optional<LocalVariable> localVariableOptional = Methods.findLocalVariableForIndexAndPC(context.getMethod(), index, pc);

        if (!localVariableOptional.isPresent()) {
            throw new LocalVariableNotAvailableException("No variable exists at index " + index);
        }

        final LocalVariable localVariable = localVariableOptional.get();

        context.push(AST.local(localVariable.getName(), localVariable.getType(), localVariable.getIndex()));
    }

}
