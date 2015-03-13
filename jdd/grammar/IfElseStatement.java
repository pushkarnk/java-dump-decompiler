package jdd.grammar;

import java.util.ArrayList;

import jdd.instructions.UIFInstruction;

public class IfElseStatement extends Statement {
	
	ArrayList<ConditionExpression> conditionExpressionList;
    ArrayList<StatementList>       statementsList;
    StatementList                  elseStatements;
    int conditionalNumber;
    
    public IfElseStatement ( )
    {
    	conditionExpressionList = new ArrayList<ConditionExpression> ( );
        statementsList = new ArrayList<StatementList> ( );
        conditionalNumber = 0;
    }
	
    public StatementList getCurrentStatementList ( )
    {
    	return statementsList.get(conditionalNumber-1);
    }
    
    public StatementList getElseStatementList ( )
    {
    	if ( elseStatements == null) 
    		elseStatements = new StatementList ( );
    	return elseStatements;
    }
    
    public void setNextConditionExpression ( ConditionExpression conExp )
    {
    	conditionExpressionList.add(conExp);
    	statementsList.add(new StatementList ( ));
    	conditionalNumber++;
    }
    
	public String genJavaCode( int offset ) {
		StringBuffer ifStat = new StringBuffer ( );
		int index = 0;
		while ( index < conditionalNumber )
		{
			ifStat.append(indent(offset));
			if ( index == 0 )
				ifStat.append("if ( ").append(conditionExpressionList.get(index).genJavaCode(true)).append (" )\n");
			else
				ifStat.append("else if ( ").append(conditionExpressionList.get(index).genJavaCode(true)).append ( " )\n");
			if ( statementsList.get(index).size() > 1 )
			{
				ifStat.append(indent(offset));
				ifStat.append("{\n");
			}	
			ifStat.append(statementsList.get(index).genJavaCode(offset+1));
			if ( statementsList.get(index).size() > 1 )
			{	
				ifStat.append(indent(offset));
				ifStat.append("}\n");
			}
			index++;
	    }
		if ( elseStatements == null )
			return ifStat.toString();
		ifStat.append(indent(offset));
		ifStat.append("else \n");
		if ( elseStatements.size() > 1 )
		{
			ifStat.append(indent(offset));
			ifStat.append("{\n");
		}
		ifStat.append(elseStatements.genJavaCode(offset+1));
		if ( elseStatements.size() > 1 )
		{
			ifStat.append(indent(offset));
			ifStat.append("}\n");
		}
		return ifStat.toString();
	}
	
	private String indent ( int offset )
    {
    	StringBuffer s = new StringBuffer ( );
    	for ( int i = 0 ; i < offset * 4; i ++ )
    		s.append (" ");
    	return s.toString();
    }

}
