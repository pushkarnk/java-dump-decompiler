package jdd.operands;


public class FieldRef extends Operand {
	int cpIndex;
	String className;
	String fieldName;
	String signature;
	
	public FieldRef ( int cpIdx, String classname, String fieldName, String signature )
	{
		cpIndex = cpIdx;
		className = classname;
		this.fieldName = fieldName;
		this.signature = resolve(signature);
	}
	
	public String toString ( )
	{
		String fStr = "Field:" + className + "." + fieldName + " " + signature + " ( #" + cpIndex + " )";
		return fStr;
	}
	
	public String getFieldName ( )
	{
		return fieldName;
	}
	
	public String getClassName ( )
	{
		String [] className = this.className.split("/");
		return className[className.length-1];
	}
	
	public String getFullClassName ( )
	{
		return className;
	}
	
	public String getSignature ( )
	{
		return signature;
	}
	
	private String resolve ( String signature )
	{
		if ( signature.equals("I"))
			return "int";
		else if ( signature.equals("J"))
			return "long";
		else if ( signature.equals("Z"))
			return "boolean";
		else if ( signature.equals("C"))
			return "char";
		else if ( signature.equals("D"))
			return "double";
		else if ( signature.equals("F"))
			return "float";
		else if ( signature.equals("B"))
			return "byte";
		else 
			return signature;
	}
}
