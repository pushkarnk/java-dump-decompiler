package jdd.grammar;

public class ArrayCreateExpression extends Expression {
	
	OperandExpression classRef;
	ExpressionList dimensionExpressions;
	
	public ArrayCreateExpression ( OperandExpression cRef, ExpressionList exprList )
	{
		classRef = cRef;
		dimensionExpressions = exprList;
	}
	
	public String genJavaCode ( boolean hasBraces )
	{
		StringBuffer arrayElement = new StringBuffer ( );
		Object[] expressionsArray = dimensionExpressions.toArray();
		String clsStrs []  = classRef.genJavaCode(false).split("/");
		String className = clsStrs[clsStrs.length-1];
		arrayElement.append( "new " + arrayClassName(className));
		boolean braces = true;
		for ( int i = 0 ; i < dimensionExpressions.size() ;  i++ )
		{
			braces = true;
			Expression dimExpr = (Expression)(expressionsArray[i]);
			if ( dimExpr instanceof OperandExpression || dimExpr instanceof MethodInvocationExpression )
				braces = false;
			arrayElement.append( "[" + dimExpr.genJavaCode(braces) + "]");
		}
		return arrayElement.toString();
	}
	
	private String arrayClassName ( String className )
	{
		if ( className.length() == 1 )
		{
			switch ( className.charAt(0) )
			{
				case '4' : return "boolean";
				case '5' : return "char";    
				case '6' : return "float";
				case '7' : return "double";
				case '8' : return "byte";
				case '9' : return "short";
			}
		}
		if ( className.equals("10"))
		   return "int";
		if ( className.equals("11"))
			   return "long";
		
		StringBuffer newName = new StringBuffer ( );
		char [] oldName = className.toCharArray();
		int index = 0;
		while ( index < oldName.length)
		{
			if ( oldName[index] != '[' && oldName[index] != ';')
				newName.append(oldName[index]);
			index++;
		}
		String typeString = newName.toString();
			 if ( typeString.equals("I")) return "int";
		else if ( typeString.equals("J")) return "long";
		else if ( typeString.equals("Z")) return "boolean";
		else if ( typeString.equals("C")) return "char";
		else if ( typeString.equals("S")) return "short";
		else if ( typeString.equals("F")) return "float";
		else if ( typeString.equals("B")) return "byte";
		else if ( typeString.equals("D")) return "double";
		else 							  return typeString;
			 
			 
	}
}
