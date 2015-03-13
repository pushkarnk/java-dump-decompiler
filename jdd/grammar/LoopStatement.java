package jdd.grammar;

public class LoopStatement extends Statement {

	public static final int FOR_OR_WHILE = 1;
	public static final int DO_WHILE = 2;
	int loopType;
	
	ConditionExpression checkExpression;
	StatementList loopStatements;
	
	public LoopStatement ( int type )
	{
		
		this.loopType = type;
		loopStatements = new StatementList ( );
	}
	
	public void setExpression ( ConditionExpression condExpr )
	{
		checkExpression = condExpr;
	}
	

	public StatementList getStatements ( )
	{
		return loopStatements;
	}
	
	public String genJavaCode(int offset) {
		if ( loopType == FOR_OR_WHILE )
			return genWhileLoop ( offset );
		else
			
			return genDoWhileLoop ( offset );
	}
	
	String genWhileLoop ( int offset )
	{
		StringBuffer whileLoop = new StringBuffer( ) ;
		whileLoop.append(indent(offset) + "while ( ");
		if ( checkExpression == null )
			whileLoop.append( "true )");
		else
			whileLoop.append( checkExpression.genJavaCode(true) + " )" );
		whileLoop.append("\n" + indent(offset) + "{");
		whileLoop.append( "\n" + loopStatements.genJavaCode(offset+1));
		whileLoop.append(indent(offset) + "}" + "\n" );
		return whileLoop.toString();
	}
	
	String genDoWhileLoop ( int offset )
	{
		StringBuffer doWhileLoop = new StringBuffer ( );
		doWhileLoop.append( indent(offset) + "do" );
		doWhileLoop.append( "\n" + indent(offset) + "{");
		doWhileLoop.append("\n" + loopStatements.genJavaCode(offset+1));
		doWhileLoop.append("\n" + indent(offset) + "}while" );
		doWhileLoop.append(checkExpression.genJavaCode(true)+";\n");
		return doWhileLoop.toString();
	}
	private String indent ( int offset )
    {
    	StringBuffer s = new StringBuffer ( );
    	for ( int i = 0 ; i < offset * 4; i ++ )
    		s.append (" ");
    	return s.toString();
    }

}
