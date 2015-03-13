package jdd.grammar;


public class TypecastExpression extends Expression {
	
	OperandExpression classType;
	Expression operand;
	
	public TypecastExpression ( OperandExpression classType, Expression operand )
	{
		this.classType = classType;
		this.operand = operand;
	}
	
	public String genJavaCode ( boolean hasBraces)
	{
		if ( operand instanceof OperandExpression )
			return "("+classType.genJavaCode(false)+")"+ operand.genJavaCode(false);
		return "("+classType.genJavaCode(false)+")"+ "(" + operand.genJavaCode(false)+")";
	}
	

}
