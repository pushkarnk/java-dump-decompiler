package jdd.grammar;

import jdd.instructions.UIFInstruction;

public class BitwiseExpression extends Expression {
	
    
	int bitwiseOperator;
	Expression operand1;
	Expression operand2;
	
	public BitwiseExpression ( int bitwiseOperator, Expression op1, Expression op2 )
	{
		this.bitwiseOperator = bitwiseOperator;
		operand1 = op1;
		operand2 = op2;
		
	}
	
	public String genJavaCode( boolean haveBraces )
	{
		if ( haveBraces )
			return "(" + operand1.genJavaCode(true) + " " + getBitwiseOperatorStr ( ) + " " + operand2.genJavaCode(true) + ")";
		else
			return operand1.genJavaCode(true) + " " + getBitwiseOperatorStr ( ) + " " + operand2.genJavaCode(true);
	}
	
	public String getBitwiseOperatorStr ( )
	{
		switch ( bitwiseOperator )
		{
		    case UIFInstruction.AND: 
		    	return "&";
		    case UIFInstruction.OR : 
		    	return "|";
		    case UIFInstruction.XOR: 
		    	return "^";
		    case UIFInstruction.SHIFTL:
		    	return "<<";
		    case UIFInstruction.SHIFTR:
		    	return ">>";
		    case UIFInstruction.SHIFTRU:
		    	return ">>>";
		    default:
		    	return "Error";
		}
	}
	

}

