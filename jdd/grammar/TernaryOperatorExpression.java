package jdd.grammar;

public class TernaryOperatorExpression extends Expression {

	ConditionExpression condExpression;
	Expression trueExpression;
	Expression falseExpression;
	
	public String genJavaCode(boolean haveBraces) 
	{
		
		boolean trueEx = trueExpression instanceof OperandExpression;
		boolean falsEx = falseExpression instanceof OperandExpression;
		if ( haveBraces )
			return "(" + condExpression.genJavaCode(true) + " ? " + trueExpression.genJavaCode(trueEx == true?false:true) + " : " + falseExpression.genJavaCode(falsEx == true?false:true) + ")";
		else 
			return condExpression.genJavaCode(true) + " ? " + trueExpression.genJavaCode(trueEx == true?false:true) + " : " + falseExpression.genJavaCode(falsEx == true?false:true);
	}
	
	public TernaryOperatorExpression ( ConditionExpression condExp, Expression trueExpr, Expression falseExpr )
	{
		condExpression = condExp;
		trueExpression = trueExpr;
		falseExpression = falseExpr;
	}

}
