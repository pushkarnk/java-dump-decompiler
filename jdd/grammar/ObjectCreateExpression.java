package jdd.grammar;


public class ObjectCreateExpression extends Expression {
	OperandExpression classRef;
	ExpressionList expressionList;
	boolean isArray;
	
	public ObjectCreateExpression (  OperandExpression classRef, ExpressionList exprList )
	{
		this.classRef = classRef;
		expressionList = exprList;
	}
	
	public String genJavaCode ( boolean hasBraces )
	{
		return "new " + classRef.genJavaCode(false) + " ( " + expressionList.genJavaCode(0) + " ) ";
	}
}
