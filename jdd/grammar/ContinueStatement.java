package jdd.grammar;

public class ContinueStatement extends Statement {

	@Override
	public String genJavaCode(int offset) {
		
		return indent(offset) + "continue;\n";
	}
	
	private String indent ( int offset )
    {
    	StringBuffer s = new StringBuffer ( );
    	for ( int i = 0 ; i < offset * 4; i ++ )
    		s.append (" ");
    	return s.toString();
    }

}
