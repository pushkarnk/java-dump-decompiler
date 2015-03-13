package jdd.grammar;

public class ConditionExpression extends Expression {

	Expression conExp1;
	int booleanOperator;
	Expression conExp2;
	
	final static int AND = -1;
	final static int OR  = -2;
	final static int NOT = -3;
	
	boolean hasBraces = false;
	final public String [] boolOpStr = new String[] { "", "&&", "||", "!"};
	public final static int EQ = 0, LE = 1, GE = 2, LT = 3, GT = 4, NE = 5;
	
	public ConditionExpression ( Expression conExp1, int boolOp, Expression conExp2)
	{
		this.conExp1 = conExp1;
		this.conExp2 = conExp2;
		this.booleanOperator = boolOp;
	}
	
	public ConditionExpression ( Expression conExp )
	{
		conExp1 = conExp;
		booleanOperator = -1; // no op
		conExp2 = null;
	}
	
	public void setBraces (  )
	{
		hasBraces = true;
	}
	
	public String genJavaCode(boolean haveBraces) 
	{
		if ( conExp2 == null && hasBraces )
			return "(" + conExp1.genJavaCode(true) + ")";
		else if ( conExp2 == null )
			return conExp1.genJavaCode(true);
		if ( hasBraces )
			return "("+conExp1.genJavaCode(true) + " " +  boolOpStr[-booleanOperator] + " " + conExp2.genJavaCode(true)+")";
		else
			return conExp1.genJavaCode(true) + " " +  boolOpStr[-booleanOperator] + " " + conExp2.genJavaCode(true);
	}
	
	

}
