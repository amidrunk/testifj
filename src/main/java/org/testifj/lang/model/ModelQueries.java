package org.testifj.lang.model;

import com.sun.org.apache.xpath.internal.operations.Variable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public final class ModelQueries {

    public static Predicate<Expression> ofType(ElementType elementType) {
        assert elementType != null : "Element type can't be null";

        return expression -> expression != null && expression.getElementType() == elementType;
    }

    public static <E> Predicate<E> ofType(Class<E> type) {
        assert type != null : "Type can't be null";

        return type::isInstance;
    }

    public static Predicate<VariableAssignment> isAssignmentTo(LocalVariableReference localVariable) {
        assert localVariable != null : "Local variable can't be null";

        return instance -> instance != null
                && instance.getVariableName().equals(localVariable.getName())
                && instance.getVariableType().equals(localVariable.getType())
                && instance.getVariableIndex() == localVariable.getIndex();
    }

    public static Predicate<Cast> isCastTo(Class<?> targetType) {
        assert targetType != null : "Target type can't be null";
        return instance -> instance.getType().equals(targetType);
    }

    public static ModelQuery<Cast, Expression> castValue() {
        return cast -> cast == null ? Optional.empty() : Optional.of(cast.getValue());
    }

    public static ModelQuery<VariableAssignment, Expression> assignedValue() {
        return from -> from == null ? Optional.<Expression>empty() : Optional.of(from.getValue());
    }

    public static Predicate<VariableAssignment> assignedVariableTypeIs(Class<?> type) {
        assert type != null : "Type can't be null";

        return new Predicate<VariableAssignment>() {
            @Override
            public boolean test(VariableAssignment variableAssignment) {
                return variableAssignment != null && variableAssignment.getVariableType().equals(type);
            }
        };
    }

    public static ModelQuery<BinaryOperator, Expression> leftOperand() {
        return o -> o == null ? Optional.<Expression>empty() : Optional.of(o.getLeftOperand());
    }

    public static ModelQuery<BinaryOperator, Expression> rightOperand() {
        return o -> o == null ? Optional.<Expression>empty() : Optional.of(o.getRightOperand());
    }

    public static <S> Predicate<S> equalTo(S expectedValue) {
        return s -> Objects.equals(s, expectedValue);
    }

    public static Predicate<Increment> affixIsUndefined() {
        return source -> source != null && source.getAffix() == Affix.UNDEFINED;
    }


}
