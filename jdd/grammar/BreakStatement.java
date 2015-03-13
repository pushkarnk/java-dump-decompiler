package jdd.grammar;

public class BreakStatement extends Statement {
	
	public String genJavaCode(int offset) {
		return indent(offset) + "break;\n";
	}
	
	private String indent ( int offset )
    {
    	StringBuffer s = new StringBuffer ( );
    	for ( int i = 0 ; i < offset * 4; i ++ )
    		s.append (" ");
    	return s.toString();
    }
}
