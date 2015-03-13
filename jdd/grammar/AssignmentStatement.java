package jdd.grammar;


/* This represents an assignment in Java
 * Eg. int x = str.length ( );
 *     array[0] = 1;
 *     obj.a = obj.b + foo ( );
 */
public class AssignmentStatement extends Statement {

	int storeType; //WRFIELD, WRARRAY, STORE
    OperandExpression lhsOperand; 
    int assignmentType;
    Expression rhsExpression;
    
    public AssignmentStatement ( int storeType, OperandExpression lhsOperand, Expression rhsExpression )
    {
    	this.storeType = storeType;
    	this.lhsOperand = lhsOperand;
    	this.rhsExpression = rhsExpression;
    	assignmentType = AssignmentType.EQ;
    }
    
    public String genJavaCode ( int offset )
    {
    	return indent(offset) + lhsOperand.genJavaCode(false) + " " + AssignmentType.assignmentOpString  ( assignmentType ) + " " + rhsExpression.genJavaCode( false ) +";\n";
    }
    
    private String indent ( int offset )
    {
    	StringBuffer s = new StringBuffer ( );
    	for ( int i = 0 ; i < offset * 4; i ++ )
    		s.append (" ");
    	return s.toString();
    }
    
}

class AssignmentType
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
