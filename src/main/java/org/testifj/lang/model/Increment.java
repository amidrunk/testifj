package org.testifj.lang.model;

public interface Increment extends StatementAndExpression {

    LocalVariableReference getLocalVariable();

    Expression getValue();

    Affix getAffix();

    default ElementType getElementType() {
        return ElementType.INCREMENT;
    }

}
