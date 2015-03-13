package jdd.grammar;

import java.util.LinkedList;

/*Sincerely following the grammar.
 * This class is just a list of Expressions.
 * Can be put to work for invocations/multi-dimensional array operations.
 */
public class ExpressionList {

	LinkedList<Expression> expressionList;
	
	public ExpressionList ( )
	{
		expressionList = new LinkedList<Expression> () ;
	}
	
	public void addExpression ( Expression expr )
	{
		expressionList.add(expr);
	}
	
	public int size ( )
	{
		return expressionList.size();
	}
	
	public Object[] toArray ( )
	{
		return expressionList.toArray();
	}
	
	public Object getFirstElement ( )
	{
		return expressionList.getFirst();
	}
	
	public String genJavaCode ( int start  )
	{
		StringBuffer argList = new StringBuffer ( );
		Object [] expressionArray = toArray();
		if ( expressionArray.length == 0 )
			return "";
		argList.append( ((Expression)expressionArray[start++]).genJavaCode(false));
		for ( int i = start ; i < expressionArray.length ; i++ )
		{
			argList.append( ", ");
			argList.append( ((Expression)expressionArray[i]).genJavaCode(false));
		}
		return argList.toString();
	}
}
