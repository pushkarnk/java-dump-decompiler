package jdd.grammar;

public class UnaryExpression extends Expression {
	
	
	Expression operand;
	int typeOperator;
	
	public UnaryExpression ( int typeOp, Expression expr )
	{
		typeOperator = typeOp;
		operand = expr;
	}
	
	public String genJavaCode ( boolean hasBraces )
	{
		if ( operand instanceof OperandExpression )
		{
			if ( hasBraces )
				return "( -" + operand.genJavaCode(false) + " )";
			else 
				return "-" + operand.genJavaCode(false);
		}
		if ( hasBraces)
			return "( -" +  operand.genJavaCode(true) + ")";
		else
			return "-" +  operand.genJavaCode(true) + ")";
		
	}

}
