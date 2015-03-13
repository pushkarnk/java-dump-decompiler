package jdd.grammar;

import jdd.main.MethodSignature;
import jdd.main.UntypedIntermediateForm;

public class MethodBody {
	MethodSignature fullDummyMethodSignature;
	StatementList   statements;
	UntypedIntermediateForm uifRep;
	
	public MethodBody ( UntypedIntermediateForm uifRep )
	{
		this.uifRep = uifRep;
		fullDummyMethodSignature = uifRep.getMethodSignature();
		statements = new StatementList (  );
		
	}
	
	public String genJavaCode ( )
	{
		String javaMethod = fullDummyMethodSignature.toString ( );
		javaMethod += "\n{\n";
		javaMethod += statements.genJavaCode ( 1 );
		javaMethod += "}";
		return javaMethod;
	}
	
	public StatementList getStatementList ( )
	{
		return statements;
	}
	
	public void addStatement ( Statement s )
	{
		statements.addStatement (s);
	}

}
