package jdd.operands;

public class MethodRef extends Operand {
	int cpIndex;
	
	String className;
	String methodName;
    String signature;
    boolean isSuperCall = false;
    
    public MethodRef ( int cpIdx, String classname, String methodname, String sign )
    {
    	cpIndex = cpIdx;
    	className = classname;
    	methodName = methodname;
    	signature = sign;
    }
    
    public String toString ( )
    {
    	return "Method:" + className + "." + methodName + signature + "( #" + cpIndex + " )";
    }
    
    public void setName ( String name )
    {
    	methodName = name;
    }
    
    public void setClassName ( String name )
    {
    	className = name;
    }
    
    public String getName ( )
    {
    	return methodName;
    }
    
    public String getSignature ( )
    {
    	return signature;
    }
    
    public String getShortClassName ( )
    {
    	String [] strs = className.split("/");
    	return strs[strs.length-1];
    }
    
    public void setSuperCall ( )
    {
    	isSuperCall = true;
    }
    
    public boolean isSuperCall ( )
    {
    	return isSuperCall;
    }
    
    public boolean returnsBoolean ( )
    {
    	return signature.endsWith(")Z");
    }
}

