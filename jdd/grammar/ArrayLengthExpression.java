package jdd.grammar;

public class ArrayLengthExpression extends Expression {
	
	Expression operand;
	
	public ArrayLengthExpression ( Expression e )
	{
		operand = e;
	}
	
	public String genJavaCode ( boolean hasBraces )
	{
		return operand.genJavaCode ( false ) + "." + "length";
	}

}
