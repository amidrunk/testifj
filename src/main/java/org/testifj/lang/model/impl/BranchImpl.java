package org.testifj.lang.model.impl;

import org.testifj.lang.model.Branch;
import org.testifj.lang.model.Expression;
import org.testifj.lang.model.OperatorType;

public final class BranchImpl implements Branch {

    private final Expression leftOperand;

    private final OperatorType operatorType;

    private final Expression rightOperand;

    private final int targetPc;

    public BranchImpl(Expression leftOperand, OperatorType operatorType, Expression rightOperand, int targetPc) {
        assert leftOperand != null : "Left operand can't be null";
        assert operatorType != null : "Operator type can't be null";
        assert rightOperand != null : "Right operand can't be null";
        assert targetPc >= 0 : "Target PC must be positive";

        this.leftOperand = leftOperand;
        this.operatorType = operatorType;
        this.rightOperand = rightOperand;
        this.targetPc = targetPc;
    }

    @Override
    public Expression getLeftOperand() {
        return leftOperand;
    }

    @Override
    public OperatorType getOperatorType() {
        return operatorType;
    }

    @Override
    public Expression getRightOperand() {
        return rightOperand;
    }

    @Override
    public int getTargetPC() {
        return targetPc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BranchImpl branch = (BranchImpl) o;

        if (targetPc != branch.targetPc) return false;
        if (!leftOperand.equals(branch.leftOperand)) return false;
        if (operatorType != branch.operatorType) return false;
        if (!rightOperand.equals(branch.rightOperand)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = leftOperand.hashCode();
        result = 31 * result + operatorType.hashCode();
        result = 31 * result + rightOperand.hashCode();
        result = 31 * result + targetPc;
        return result;
    }

    @Override
    public String toString() {
        return "BranchImpl{" +
                "leftOperand=" + leftOperand +
                ", operatorType=" + operatorType +
                ", rightOperand=" + rightOperand +
                ", targetPc=" + targetPc +
                '}';
    }
}
