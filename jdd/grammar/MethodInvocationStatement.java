package jdd.grammar;

public class MethodInvocationStatement extends Statement {
	
	MethodInvocationExpression methodInvocation;
	
	public MethodInvocationStatement ( MethodInvocationExpression method )
	{
		methodInvocation = method;
	}
	
	public String genJavaCode ( int offset )
	{
		return indent(offset) + methodInvocation.genJavaCode(false) + ";\n" ;
	}
   
	private String indent ( int offset )
    {
    	StringBuffer s = new StringBuffer ( );
    	for ( int i = 0 ; i < offset * 4; i ++ )
    		s.append (" ");
    	return s.toString();
    }
}
