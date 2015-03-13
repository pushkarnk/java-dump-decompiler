package jdd.grammar;

import jdd.instructions.UIFInstruction;

public class ArithmeticExpression extends Expression {
    
	
	int arithmeticOperator;
	Expression operand1;
	Expression operand2;
	
	public ArithmeticExpression ( int arithmeticOperator, Expression operand1, Expression operand2 )
	{
		this.arithmeticOperator = arithmeticOperator;
		this.operand1 = operand1;
		this.operand2 = operand2;
	}
	
	
	public String genJavaCode ( boolean haveBraces )
	{
		if ( haveBraces )
			return "(" + operand1.genJavaCode(true) + " " + getArithmeticOpStr( ) + " " + operand2.genJavaCode (true) + ")" ;
		else 
			return operand1.genJavaCode(true) + " " + getArithmeticOpStr( ) + " " + operand2.genJavaCode(true) ;
	}
	
	String getArithmeticOpStr ( )
	{
		switch ( arithmeticOperator )
		{
		    case UIFInstruction.ADD: return "+";
		    case UIFInstruction.SUB: return "-";
		    case UIFInstruction.MUL: return "*";
		    case UIFInstruction.DIV: return "/";
		    case UIFInstruction.MOD: return "%";
		}
		return "Operator Error";
	}
	
}

