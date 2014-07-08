package org.testifj.lang.decompile.impl;

import org.testifj.lang.*;
import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.classfile.LocalVariable;
import org.testifj.lang.decompile.*;
import org.testifj.lang.model.*;

import java.util.Optional;

import static org.testifj.lang.classfile.ByteCode.*;
import static org.testifj.lang.model.AST.constant;

public final class VariableInstructions implements DecompilerDelegation {

    public void configure(DecompilerConfiguration.Builder configurationBuilder) {
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

    public static DecompilerDelegate load() {
        return (context,codeStream,byteCode) -> {
            load(context, codeStream.nextByte());
        };
    }

    public static DecompilerDelegate store() {
        return (context,codeStream,byteCode) -> {
            store(context, codeStream.nextByte());
        };
    }

    public static DecompilerDelegate istoren() {
        return (context,codeStream,byteCode) -> {
            store(context, byteCode - ByteCode.istore_0);
        };
    }

    public static DecompilerDelegate fstoren() {
        return (context,codeStream,byteCode) -> {
            store(context, byteCode - ByteCode.fstore_0);
        };
    }

    public static DecompilerDelegate dstoren() {
        return (context,codeStream,byteCode) -> {
            store(context, byteCode - ByteCode.dstore_0);
        };
    }

    public static DecompilerDelegate lstoren() {
        return (context,codeStream,byteCode) -> {
            store(context, byteCode - ByteCode.lstore_0);
        };
    }

    public static DecompilerDelegate astoren() {
        return (context,codeStream,byteCode) -> {
            store(context, byteCode - ByteCode.astore_0);
        };
    }

    public static DecompilerDelegate iloadn() {
        return (context,codeStream,byteCode) -> {
            load(context, byteCode - ByteCode.iload_0);
        };
    }

    public static DecompilerDelegate floadn() {
        return (context,codeStream,byteCode) -> {
            load(context, byteCode - ByteCode.fload_0);
        };
    }

    public static DecompilerDelegate dloadn() {
        return (context,codeStream,byteCode) -> {
            load(context, byteCode - ByteCode.dload_0);
        };
    }

    public static DecompilerDelegate lloadn() {
        return (context,codeStream,byteCode) -> {
            load(context, byteCode - ByteCode.lload_0);
        };
    }

    public static DecompilerDelegate aloadn() {
        return (context,codeStream,byteCode) -> {
            load(context, byteCode - ByteCode.aload_0);
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
