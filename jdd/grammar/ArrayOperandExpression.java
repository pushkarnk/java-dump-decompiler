package jdd.grammar;

public class ArrayOperandExpression extends Expression {
	
	Expression arrayBase;
	ExpressionList dimExpressions;
	
	public ArrayOperandExpression ( Expression arrayBase, ExpressionList dimExpressions )
	{
		this.arrayBase = arrayBase;
		this.dimExpressions = dimExpressions;
	}
	
	public String genJavaCode ( boolean hasBraces )
	{
		StringBuffer arrayElement = new StringBuffer ( );
		Object[] expressionsArray = dimExpressions.toArray();
		arrayElement.append( arrayBase.genJavaCode(false));
		boolean braces = true;
		for ( int i = dimExpressions.size() -1  ; i >= 0 ; i-- )
		{
			braces = true;
			Expression dimExpr = (Expression)(expressionsArray[i]);
			if ( dimExpr instanceof OperandExpression || dimExpr instanceof MethodInvocationExpression )
				braces = false;
			arrayElement.append( "[" + dimExpr.genJavaCode(braces) + "]");
		}
		return arrayElement.toString();
	}
}
