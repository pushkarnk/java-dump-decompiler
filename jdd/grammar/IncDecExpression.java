package jdd.grammar;

public class IncDecExpression extends Expression {

    boolean isPost;
    boolean isIncrement;
    Expression lvar; //Only OperandExpression
    
    public IncDecExpression ( Expression localVariable, boolean isPost, boolean isInc )
    {
    	lvar = localVariable;
    	this.isPost = isPost;
    	this.isIncrement = isInc;
    }
    
	public String genJavaCode(boolean haveBraces) {
		if ( haveBraces )
			if ( isPost )
				return "( " + lvar.genJavaCode(false) + getOp ( ) + " )";
			else
				return "( " + getOp( ) + lvar.genJavaCode(false) + " )";
		else
			if ( isPost )
				return lvar.genJavaCode(false) + getOp ( );
			else
				return getOp( ) + lvar.genJavaCode(false);
	}

	private String getOp ( )
	{
		if ( isIncrement )
			return "++";
		else
			return "--";
	}
}
