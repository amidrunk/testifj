package org.testifj.lang.decompile.impl;

import org.testifj.lang.classfile.ByteCode;
import org.testifj.lang.decompile.DecompilerConfiguration;
import org.testifj.lang.decompile.DecompilerExtension;
import org.testifj.lang.model.Cast;
import org.testifj.lang.model.ElementType;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.impl.CastImpl;
import org.testifj.util.Priority;

import java.lang.reflect.Type;

public final class TypeCheckDecompilerExtensions {

    public static void configure(DecompilerConfiguration.Builder configurationBuilder) {
        assert configurationBuilder != null : "Configuration builder can't be null";

        configurationBuilder.on(ByteCode.pop)
                .withPriority(Priority.HIGH)
                .when((context, byteCode) -> context.peek().getElementType() == ElementType.CAST)
                .then(discardImplicitCast());

        configurationBuilder.on(ByteCode.checkcast).then(checkcast());
    }

    /**
     * This handles a special case where a checkcast instruction is inserted when a generic method is
     * called e.g. in a void-method. The return value of the method will be discarded, which will be reduced
     * to a statement by the decompiler. However, a cast is not a valid statement, so it need to be
     * discarded to be reducable. It also has the advantage of matching the user's actual code.
     *
     * @return An extension for discarding implicit casts when
     */
    public static DecompilerExtension discardImplicitCast() {
        return (context,codeStream,byteCode) -> {
            final Cast cast = (Cast) context.pop();

            context.push(cast.getValue());
        };
    }

    public static DecompilerExtension checkcast() {
        return (context,codeStream,byteCode) -> {
            final String targetTypeName = context.getMethod().getClassFile().getConstantPool().getClassName(codeStream.nextUnsignedShort());
            final Type targetType = context.resolveType(targetTypeName);
            final Expression castExpression = context.pop();

            context.push(new CastImpl(castExpression, targetType));
        };
    }

}
