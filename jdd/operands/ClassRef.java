package jdd.operands;

public class ClassRef extends Operand {
	int cpIndex;
	String className;
	
	public ClassRef ( int cpIdx, String classname )
	{
		cpIndex = cpIdx;
		className = classname;
	}
	
	public String getClassName ( )
	{
		return className;
	}
	
	public String toString ( )
	{
		return "Class:"+className+"( #"+cpIndex+" )";
	}
}
