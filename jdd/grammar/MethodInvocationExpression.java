package jdd.grammar;

import jdd.operands.MethodRef;

public class MethodInvocationExpression extends Expression {
	
	boolean isStaticInvocation;
	OperandExpression invokeMethod;
	ExpressionList expressionList;
	
	public MethodInvocationExpression ( OperandExpression invokeMethod, ExpressionList exprList, boolean isStatic )
	{
		//the MethodRef sits at the tail of this list inside an OperandExpression
		this.invokeMethod = invokeMethod;
		expressionList = exprList;
		isStaticInvocation = isStatic;
	}
	
	public OperandExpression getInvokeMethod ( )
	{
		return invokeMethod;
	}
	
    public String genJavaCode ( boolean haveBraces )
    {
    	if ( isStaticInvocation )
    		return ((MethodRef)(invokeMethod.getOperand())).getShortClassName() + "." + invokeMethod.genJavaCode(false) + "( " + getArgumentStrs( ) + " )";
    	String receiver  = ((Expression)(expressionList.getFirstElement())).genJavaCode(false);
    	if (((MethodRef)(invokeMethod.getOperand())).isSuperCall())
    		receiver = "super";
    	
    	if ( receiver == "this" )
    		return  invokeMethod.genJavaCode(false) + "(" + getArgumentStrs( ) + ")";
    	else
    		return receiver + "." + invokeMethod.genJavaCode(false) + "(" + getArgumentStrs( ) + ")";
    }
    
    String getArgumentStrs ( )
    {
    	if ( (!isStaticInvocation) && (expressionList.size() == 1))
    		return "";
    	else if ( isStaticInvocation && (expressionList.size() == 0))
    		return "";
    	else
    	{
    		if ( isStaticInvocation )
    			return expressionList.genJavaCode( 0 );
    		else
    			return expressionList.genJavaCode( 1 );
    	}
    }
    
    
    
    
}
