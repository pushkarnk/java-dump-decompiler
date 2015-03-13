package jdd.grammar;

import jdd.operands.Constant;

public class ReturnStatement extends Statement {

	/*return an expression*/
	Expression returnExpression;
	boolean isBoolean = false;
	
	public ReturnStatement ( Expression expr, int isBoolean )
	{
		returnExpression = expr;
		if ( isBoolean == 1 )
			this.isBoolean = true;
	}
	
	public String genJavaCode ( int offset )
	{
		if ( isBoolean && returnExpression instanceof OperandExpression && ((OperandExpression)returnExpression).getOperand() instanceof Constant)
		{
			int constantVal = ((Integer)(((Constant)(((OperandExpression)returnExpression).getOperand())).getConstantObj())).intValue();
			if ( constantVal == 1)
				return indent(offset) + "return true" + ";\n";
			else
				return indent(offset) + "return false" + ";\n";
		}
		if ( returnExpression == null )
			return indent(offset) + "return;\n";
		return indent(offset) + "return " + returnExpression.genJavaCode(true) + ";\n";
	}
	
	private String indent ( int offset )
    {
    	StringBuffer s = new StringBuffer ( );
    	for ( int i = 0 ; i < offset * 4; i ++ )
    		s.append (" ");
    	return s.toString();
    }
}
