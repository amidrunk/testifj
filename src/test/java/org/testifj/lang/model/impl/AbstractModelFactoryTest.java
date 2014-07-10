package org.testifj.lang.model.impl;

import org.junit.Test;
import org.testifj.lang.classfile.LocalVariable;
import org.testifj.lang.classfile.ReferenceKind;
import org.testifj.lang.classfile.impl.LambdaImpl;
import org.testifj.lang.model.*;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.testifj.Expect.expect;
import static org.testifj.Given.given;
import static org.testifj.matchers.core.CollectionThatIs.collectionOf;
import static org.testifj.matchers.core.ObjectThatIs.equalTo;
import static org.testifj.matchers.core.OptionalThatIs.optionalOf;

public class AbstractModelFactoryTest {

    private final ElementMetaData metaData = mock(ElementMetaData.class);

    private final AbstractModelFactory modelFactory = new AbstractModelFactory() {
        @Override
        protected ElementMetaData createElementMetaData() {
            return metaData;
        }
    };

    @Test
    public void createConstantShouldCreateConstantWithParametersAndMetaData() {
        given(modelFactory.createConstant("foo", CharSequence.class)).then(it -> {
            expect(it.getConstant()).toBe("foo");
            expect(it.getType()).toBe(CharSequence.class);
            expect(it.getMetaData()).toBe(metaData);
        });
    }

    @Test
    public void createReturnValueShouldCreateElementWithParametersAndMetaData() {
        final Expression expectedValue = mock(Expression.class);

        given(modelFactory.createReturnValue(expectedValue)).then(it -> {
            expect(it.getValue()).toBe(expectedValue);
            expect(it.getMetaData()).toBe(metaData);
        });
    }

    @Test
    public void createInstanceAllocationShouldCreateElementWithParametersAndMetaData() {
        given(modelFactory.createInstanceAllocation(String.class)).then(it -> {
            expect(it.getType()).toBe(String.class);
            expect(it.getMetaData()).toBe(metaData);
        });
    }

    @Test
    public void createArrayLoadShouldCreateElementWithParametersAndMetaData() {
        final Expression array = mock(Expression.class, "array");
        final Expression index = mock(Expression.class, "index");

        given(modelFactory.createArrayLoad(array, index, String.class)).then(it -> {
            expect(it.getArray()).toBe(array);
            expect(it.getIndex()).toBe(index);
            expect(it.getType()).toBe(String.class);
            expect(it.getMetaData()).toBe(metaData);
        });
    }

    @Test
    public void createArrayStoreShouldCreateElementWithParametersAndMetaData() {
        final Expression array = mock(Expression.class, "array");
        final Expression index = mock(Expression.class, "index");
        final Expression value = mock(Expression.class, "value");

        given(modelFactory.createArrayStore(array, index, value)).then(it -> {
            expect(it.getArray()).toBe(array);
            expect(it.getIndex()).toBe(index);
            expect(it.getValue()).toBe(value);
            expect(it.getMetaData()).toBe(metaData);
        });
    }

    @Test
    public void createBinaryOperatorShouldCreateElementWithParametersAndMetaData() {
        final Expression leftOperand = mock(Expression.class, "leftOperand");
        final Expression rightOperand = mock(Expression.class, "rightOperand");

        given(modelFactory.createBinaryOperator(leftOperand, OperatorType.AND, rightOperand, int.class)).then(it -> {
            expect(it.getLeftOperand()).toBe(leftOperand);
            expect(it.getOperatorType()).toBe(OperatorType.AND);
            expect(it.getRightOperand()).toBe(rightOperand);
            expect(it.getType()).toBe(int.class);
            expect(it.getMetaData()).toBe(metaData);
        });
    }

    @Test
    public void createBranchShouldCreateElementWithMetaData() {
        final Expression leftOperand = mock(Expression.class, "leftOperand");
        final Expression rightOperand = mock(Expression.class, "rightOperand");

        given(modelFactory.createBranch(leftOperand, OperatorType.EQ, rightOperand, 1234)).then(it -> {
            expect(it.getLeftOperand()).toBe(leftOperand);
            expect(it.getOperatorType()).toBe(OperatorType.EQ);
            expect(it.getRightOperand()).toBe(rightOperand);
            expect(it.getTargetProgramCounter()).toBe(1234);
            expect(it.getMetaData()).toBe(metaData);
        });
    }

    @Test
    public void createTypeCastShouldCreateElementWithMetaData() {
        final Expression value = mock(Expression.class, "value");

        given(modelFactory.createTypeCast(value, String.class)).then(it -> {
            expect(it.getValue()).toBe(value);
            expect(it.getType()).toBe(String.class);
            expect(it.getMetaData()).toBe(metaData);
        });
    }

    @Test
    public void createCompareShouldCreateElementWithMetaData() {
        final Expression leftOperand = mock(Expression.class, "leftOperand");
        final Expression rightOperand = mock(Expression.class, "rightOperand");

        given(modelFactory.createCompare(leftOperand, rightOperand)).then(it -> {
            expect(it.getLeftOperand()).toBe(leftOperand);
            expect(it.getRightOperand()).toBe(rightOperand);
            expect(it.getMetaData()).toBe(metaData);
        });
    }

    @Test
    public void createFieldAssignmentShouldCreateElementWithMetaData() {
        final FieldReference fieldReference = mock(FieldReference.class, "fieldReference");
        final Expression value = mock(Expression.class, "value");

        given(modelFactory.createFieldAssignment(fieldReference, value)).then(it -> {
            expect(it.getFieldReference()).toBe(fieldReference);
            expect(it.getValue()).toBe(value);
            expect(it.getMetaData()).toBe(metaData);
        });
    }

    @Test
    public void createFieldReferenceShouldCreateElementWithMetaData() {
        final Expression targetInstance = mock(Expression.class, "targetInstance");

        given(modelFactory.createFieldReference(targetInstance, String.class, int.class, "foo")).then(it -> {
            expect(it.getTargetInstance()).toBe(optionalOf(targetInstance));
            expect(it.getDeclaringType()).toBe(String.class);
            expect(it.getFieldType()).toBe(int.class);
            expect(it.getFieldName()).toBe("foo");
            expect(it.getMetaData()).toBe(metaData);
        });
    }

    @Test
    public void createGotoShouldCreateElementWithMetaData() {
        given(modelFactory.createGoto(1234)).then(it -> {
            expect(it.getTargetProgramCounter()).toBe(1234);
            expect(it.getMetaData()).toBe(metaData);
        });
    }

    @Test
    public void createIncrementShouldCreateElementWithMetaData() {
        final LocalVariableReference localVariableReference = mock(LocalVariableReference.class, "localVariableReference");
        final Expression value = mock(Expression.class, "value");

        given(modelFactory.createIncrement(localVariableReference, value, int.class, Affix.POSTFIX)).then(it -> {
            expect(it.getLocalVariable()).toBe(localVariableReference);
            expect(it.getValue()).toBe(value);
            expect(it.getType()).toBe(int.class);
            expect(it.getAffix()).toBe(Affix.POSTFIX);
            expect(it.getMetaData()).toBe(metaData);
        });
    }

    @Test
    public void createLambdaShouldCreateLambdaWithMetaData() {
        final Expression self = mock(Expression.class, "self");
        final ReferenceKind referenceKind = ReferenceKind.INVOKE_VIRTUAL;
        final Class<Runnable> functionalInterface = Runnable.class;
        final String functionalMethodName = "run";
        final MethodSignature interfaceMethodSignature = MethodSignature.parse("()V");
        final String backingMethodName = "some$lambda";
        final MethodSignature backingMethodSignature = MethodSignature.parse("()V");
        final List enclosedVariables = Collections.emptyList();

        given(modelFactory.createLambda(Optional.of(self), referenceKind, functionalInterface, functionalMethodName, interfaceMethodSignature, String.class, backingMethodName, backingMethodSignature, enclosedVariables)).then(it -> {
            expect(it.getSelf()).toBe(optionalOf(self));
            expect(it.getReferenceKind()).toBe(referenceKind);
            expect(it.getFunctionalInterface()).toBe(functionalInterface);
            expect(it.getFunctionalMethodName()).toBe(functionalMethodName);
            expect(it.getInterfaceMethodSignature()).toBe(interfaceMethodSignature);
            expect(it.getDeclaringClass()).toBe(String.class);
            expect(it.getBackingMethodName()).toBe(backingMethodName);
            expect(it.getBackingMethodSignature()).toBe(backingMethodSignature);
            expect(it.getEnclosedVariables()).toBe(enclosedVariables);
            expect(it.getMetaData()).toBe(metaData);
        });
    }

    @Test
    public void createLocalVariableReferenceShouldCreateElementWithMetaData() {
        given(modelFactory.createLocalVariableReference("foo", String.class, 1234)).then(it -> {
            expect(it.getName()).toBe("foo");
            expect(it.getType()).toBe(String.class);
            expect(it.getIndex()).toBe(1234);
            expect(it.getMetaData()).toBe(metaData);
        });
    }

    @Test
    public void createMethodCallShouldCreateElementWithMetaData() {
        final MethodSignature methodSignature = MethodSignature.parse("()V");
        final Expression targetInstance = mock(Expression.class, "targetInstance");
        final Expression param1 = mock(Expression.class, "param1");

        given(modelFactory.createMethodCall(String.class, "foo", methodSignature, targetInstance, new Expression[]{param1}, int.class)).then(it -> {
            expect(it.getTargetType()).toBe(String.class);
            expect(it.getMethodName()).toBe("foo");
            expect(it.getSignature()).toBe(methodSignature);
            expect(it.getTargetInstance()).toBe(targetInstance);
            expect(it.getParameters()).toBe(collectionOf(param1));
            expect(it.getType()).toBe(int.class);
            expect(it.getMetaData()).toBe(metaData);
        });
    }

    @Test
    public void createNewArrayShouldCreateElementWithMetaData() {
        final Expression length = mock(Expression.class, "length");

        given(modelFactory.createNewArray(String[].class, String.class, length, Collections.emptyList())).then(it -> {
            expect(it.getType()).toBe(String[].class);
            expect(it.getComponentType()).toBe(String.class);
            expect(it.getLength()).toBe(length);
            expect(it.getInitializers()).toBe(Collections.emptyList());
            expect(it.getMetaData()).toBe(metaData);
        });
    }

    @Test
    public void createNewInstanceShouldCreateElementWithMetaData() {
        final MethodSignature signature = MethodSignature.parse("()Ljava/lang/String;");
        final Expression param1 = mock(Expression.class, "param1");

        given(modelFactory.createNewInstance(String.class, signature, Arrays.asList(param1))).then(it -> {
            expect(it.getType()).toBe(String.class);
            expect(it.getConstructorSignature()).toBe(signature);
            expect(it.getParameters()).toBe(collectionOf(param1));
            expect(it.getMetaData()).toBe(metaData);
        });
    }

    @Test
    public void createReturnShouldCreateElementWithMetaData() {
        given(modelFactory.createReturn()).then(it -> {
            expect(it.getMetaData()).toBe(metaData);
        });
    }

    @Test
    public void createUnaryOperatorShouldCreateElementWithMetaData() {
        final Expression operand = mock(Expression.class, "operand");

        given(modelFactory.createUnaryOperator(operand, OperatorType.NOT, int.class)).then(it -> {
            expect(it.getOperand()).toBe(operand);
            expect(it.getOperatorType()).toBe(OperatorType.NOT);
            expect(it.getType()).toBe(int.class);
            expect(it.getMetaData()).toBe(metaData);
        });
    }

    @Test
    public void createVariableAssignmentShouldCreateElementWithMetaData() {
        final Expression value = mock(Expression.class, "value");

        given(modelFactory.createVariableAssignment(value, 1234, "foo", String.class)).then(it -> {
            expect(it.getValue()).toBe(value);
            expect(it.getVariableIndex()).toBe(1234);
            expect(it.getVariableName()).toBe("foo");
            expect(it.getVariableType()).toBe(String.class);
            expect(it.getMetaData()).toBe(metaData);
        });
    }

    @Test
    public void createFromShouldNotAcceptNullOriginal() {
        expect(() -> modelFactory.createFrom(null)).toThrow(AssertionError.class);
    }

    @Test
    public void constantCanBeRecreated() {
        assertCreateFromValid(new ConstantImpl("foo", String.class));
    }

    @Test
    public void returnValueCanBeRecreated() {
        assertCreateFromValid(new ReturnValueImpl(mock(Expression.class)));
    }

    @Test
    public void unaryOperatorCanBeRecreated() {
        assertCreateFromValid(new UnaryOperatorImpl(mock(Expression.class), OperatorType.EQ, String.class));
    }

    @Test
    public void binaryOperatorCanBeRecreated() {
        assertCreateFromValid(new BinaryOperatorImpl(mock(Expression.class), OperatorType.EQ, mock(Expression.class), String.class));
    }

    @Test
    public void returnCanBeRecreated() {
        assertCreateFromValid(new ReturnImpl());
    }

    @Test
    public void variableReferenceCanBeRecreated() {
        assertCreateFromValid(new LocalVariableReferenceImpl("foo", String.class, 1234));
    }

    @Test
    public void methodCallCanBeRecreated() {
        assertCreateFromValid(new MethodCallImpl(String.class, "foo", mock(Signature.class), mock(Expression.class), new Expression[]{mock(Expression.class)}, int.class));
    }

    @Test
    public void fieldReferenceCanBeRecreated() {
        assertCreateFromValid(new FieldReferenceImpl(mock(Expression.class), String.class, int.class, "foo"));
    }

    @Test
    public void variableAssignmentCanBeRecreated() {
        assertCreateFromValid(new VariableAssignmentImpl(mock(Expression.class), 1234, "foo", String.class));
    }

    @Test
    public void lambdaCanBeRecreated() {
        assertCreateFromValid(new LambdaImpl(Optional.of(mock(Expression.class)), ReferenceKind.INVOKE_VIRTUAL,
                Runnable.class, "run", mock(Signature.class), String.class, "run$lambda", mock(Signature.class), Arrays.asList(mock(LocalVariableReference.class))));
    }

    @Test
    public void branchCanBeRecreated() {
        assertCreateFromValid(new BranchImpl(mock(Expression.class), OperatorType.EQ, mock(Expression.class), 1234));
    }

    @Test
    public void newCanBeRecreated() {
        assertCreateFromValid(new NewInstanceImpl(String.class, mock(Signature.class), Arrays.asList(mock(Expression.class))));
    }

    @Test
    public void newArrayCanBeRecreated() {
        assertCreateFromValid(new NewArrayImpl(String[].class, String.class, mock(Expression.class), Arrays.asList(mock(ArrayInitializer.class))));
    }

    @Test
    public void arrayStoreCanBeRecreated() {
        assertCreateFromValid(new ArrayStoreImpl(mock(Expression.class), mock(Expression.class), mock(Expression.class)));
    }

    @Test
    public void fieldAssignmentCanBeRecreated() {
        assertCreateFromValid(new FieldAssignmentImpl(mock(FieldReference.class), mock(Expression.class)));
    }

    @Test
    public void typeCastCanBeRecreated() {
        assertCreateFromValid(new TypeCastImpl(mock(Expression.class), String.class));
    }

    @Test
    public void arrayLoadCanBeRecreated() {
        assertCreateFromValid(new ArrayLoadImpl(mock(Expression.class), mock(Expression.class), String.class));
    }

    @Test
    public void incrementCanBeRecreated() {
        assertCreateFromValid(new IncrementImpl(mock(LocalVariableReference.class), mock(Expression.class), String.class, Affix.POSTFIX));
    }

    @Test
    public void allocationCanBeRecreated() {
        assertCreateFromValid(new InstanceAllocationImpl(String.class));
    }

    @Test
    public void gotoCanBeRecreated() {
        assertCreateFromValid(new GotoImpl(1234));
    }

    @Test
    public void compareCanBeRecreated() {
        assertCreateFromValid(new CompareImpl(mock(Expression.class), mock(Expression.class)));
    }

    private void assertCreateFromValid(Element element) {
        final Element copy = modelFactory.createFrom(element);

        expect(copy).toBe(equalTo(element));

        final MergedElementMetaData mergedElementMetaData = (MergedElementMetaData) copy.getMetaData();

        expect(mergedElementMetaData.getFirstCandidate()).toBe(element.getMetaData());
        expect(mergedElementMetaData.getSecondCandidate()).toBe(metaData);
    }

}