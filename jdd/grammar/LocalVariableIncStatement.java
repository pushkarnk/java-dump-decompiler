package jdd.grammar;

import jdd.operands.Constant;

public class LocalVariableIncStatement extends Statement {
	
	OperandExpression localVariable; //only a local var
	OperandExpression constant; //only a constant
	
	
	public LocalVariableIncStatement ( OperandExpression lvar, OperandExpression constant )
	{
		localVariable = lvar;
		this.constant = constant;
	}
	
	public String genJavaCode( int offset ) 
	{
	    //only for integers
		int constantInt = ((Integer)(((Constant)(constant.getOperand())).getConstantObj())).intValue();
		if (  constantInt < 0 )
			return indent(offset) + localVariable.genJavaCode(false) + " -= " + (-1) * constantInt + ";\n";
		else 
			return indent(offset) + localVariable.genJavaCode(false) + " += " + constantInt + ";\n";
	}
	
	private String indent ( int offset )
    {
    	StringBuffer s = new StringBuffer ( );
    	for ( int i = 0 ; i < offset * 4; i ++ )
    		s.append (" ");
    	return s.toString();
    }

}
