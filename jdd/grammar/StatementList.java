package jdd.grammar;

import java.util.Iterator;
import java.util.LinkedList;

/* A list of Statements :-) 
 * Honestly complying with the grammar.
 */

public class StatementList {
	
	LinkedList<Statement> statementList;
	
	public StatementList ( )
	{
		statementList = new LinkedList<Statement> (  );
	}
	
	public void addStatement ( Statement s )
	{
		statementList.add(s);
	}
	
	public int size ( )
	{
		return statementList.size();
	}
	
	public String genJavaCode ( int offset )
	{
		Iterator<Statement> statIter = statementList.iterator();
		StringBuffer statBuff = new StringBuffer ( );
		System.out.println ( "statements = " + statementList.size());
		while ( statIter.hasNext())
			statBuff.append(statIter.next().genJavaCode( offset ) );
		return statBuff.toString();
	}

}
