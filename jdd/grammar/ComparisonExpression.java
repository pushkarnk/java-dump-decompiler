package jdd.grammar;

import jdd.operands.Constant;
import jdd.operands.LocalVariable;
import jdd.operands.MethodRef;
import jdd.operands.Operand;

public class ComparisonExpression extends Expression {

	Expression lhsExpression;
	int comparisonOp;
	Expression rhsExpression;
	
	public ComparisonExpression ( Expression lhs, int compOp, Expression rhs )
	{
		lhsExpression = lhs;
		comparisonOp = compOp;
		rhsExpression = rhs;
		
		if ( lhsExpression instanceof MethodInvocationExpression  && rhsExpression instanceof OperandExpression )
		{
			OperandExpression opExpr = ((MethodInvocationExpression) lhsExpression).getInvokeMethod();
			MethodRef methRef = (MethodRef)(opExpr.getOperand());
			if ( methRef.returnsBoolean())
			{
				Operand rhsOp = ((OperandExpression)rhsExpression).getOperand();
				if ( rhsOp instanceof Constant )
				{
					Object consObject = ((Constant) rhsOp).getConstantObj();
					if ( consObject instanceof Integer )
					{
						if ( ((Integer) consObject).intValue() == 0 )
							consObject = new Boolean(false);
						else
							consObject = new Boolean(true);
					}
					((Constant) rhsOp).setObjectAndType(consObject, "Boolean");
				}
			}
		}
	}
	
	public final static int EQ = 0, LE = 1, GE = 2, LT = 3, GT = 4, NE = 5, IOF=6;
	String [] comparisonOperator = {"==", "<=", ">=", "<", ">", "!=","instanceof"};
	
	public String genJavaCode(boolean haveBraces) {
		StringBuffer javaCode = new StringBuffer ( );
		if ( haveBraces && isNotOperandExpr(lhsExpression) && isNotOperandExpr(rhsExpression))
			javaCode.append("(");
		if ( lhsExpression instanceof ComparisonExpression && ((ComparisonExpression) lhsExpression).comparisonOp == IOF)
		{
			int value = ((Integer)(((Constant)(((OperandExpression)rhsExpression).getOperand())).getConstantObj())).intValue();
			if ( (value == 0 && comparisonOp == NE) || (value == 1 && comparisonOp == EQ))
				javaCode.append( lhsExpression.genJavaCode(true));
			else if ( (value == 1 && comparisonOp == NE) || (value == 0 && comparisonOp == EQ))
				javaCode.append( "!"+lhsExpression.genJavaCode(true));
			if ( haveBraces && isNotOperandExpr(lhsExpression) && isNotOperandExpr(rhsExpression) )
				javaCode.append(")");
			return javaCode.toString();
		}
		javaCode.append( lhsExpression.genJavaCode(true));
		javaCode.append(" " + comparisonOperator[comparisonOp] + " ");
		javaCode.append( rhsExpression.genJavaCode(true));
		if ( haveBraces && isNotOperandExpr(lhsExpression) && isNotOperandExpr(rhsExpression) )
			javaCode.append(")");
		return javaCode.toString();
	}

	
	boolean isNotOperandExpr( Expression e )
	{
		return !(e instanceof OperandExpression);
	}
	
}
