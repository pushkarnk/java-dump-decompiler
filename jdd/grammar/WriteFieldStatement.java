package jdd.grammar;

import jdd.operands.FieldRef;

public class WriteFieldStatement extends Statement {
	
	Expression receiver;
	OperandExpression field;
	int assignmentType;
	Expression rhsExpression;
	
	public WriteFieldStatement ( Expression receiver, OperandExpression field, Expression rhsExpression )
	{
		this.receiver = receiver;
		this.field = field;
	    assignmentType = AssignmentTypeWF.EQ;
	    this.rhsExpression = rhsExpression;
	}
	
	public String genJavaCode ( int offset )
	{
		if ( receiver == null )
			return indent(offset) + ((FieldRef)(field.getOperand())).getClassName() + "." + field.genJavaCode(false) + " " + AssignmentTypeWF.assignmentOpString(assignmentType) + " " + rhsExpression.genJavaCode(false) + ";\n";
		else
		{
			if ( receiver.genJavaCode(false) == "this")
				return indent(offset) + field.genJavaCode(false) + " " + AssignmentTypeWF.assignmentOpString(assignmentType) + " " + rhsExpression.genJavaCode(false) + ";\n";
			else
				return indent(offset) + receiver.genJavaCode(false) + "." + field.genJavaCode(false) + " " + AssignmentTypeWF.assignmentOpString(assignmentType) + " " + rhsExpression.genJavaCode(false) + ";\n";
		}
	}
	
	private String indent ( int offset )
    {
    	StringBuffer s = new StringBuffer ( );
    	for ( int i = 0 ; i < offset * 4; i ++ )
    		s.append (" ");
    	return s.toString();
    }
	
	

}

class AssignmentTypeWF
{
	public static final int EQ      = 1;  //=
	public static final int PLUSEQ  = 2;  //+= 
	public static final int MINEQ   = 3;  //-= 
	public static final int MULEQ   = 4;  //*= 
	public static final int DIVEQ   = 5;  ///= 
	public static final int MODEQ   = 6;  //%= 
	public static final int ANDEQ   = 7;  //&= 
	public static final int XOREQ   = 8;  //^= 
	public static final int OREQ    = 9;  //|= 
	public static final int SLEQ    = 10; //<<= 
	public static final int SREQ    = 11; //>>= 
	public static final int SRUEQ   = 12; //>>>=
	
	public static String assignmentOpString ( int type )
	{
		switch ( type )
		{
			case EQ : 
				return "=";
			case PLUSEQ : 
				return "+=";
			case MINEQ:
				return "-=";
			case MULEQ:
				return "*=";
			case DIVEQ:
				return "/=";
			case MODEQ:
				return "%=";
			case ANDEQ:
				return "&=";
			case OREQ:
				return "|=";
			case SLEQ:
				return "<<=";
			case SREQ:
				return ">>=";
			case SRUEQ:
				return ">>>=";
			default:
				return "Error";
		}
	}
		
}
