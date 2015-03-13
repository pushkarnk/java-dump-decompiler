package jdd.operands;

public class Constant extends Operand {
    Object constantValue;
    String type;
    
   
    public Constant ( Object constant )
    {
    	constantValue = constant;
    	if ( constantValue instanceof Integer )
    		type = "int";
    	else if ( constantValue instanceof Double )
    		type = "double";
    	else if ( constantValue instanceof Float )
    		type = "float";
    	else if ( constantValue instanceof Long )
    		type = "long";
    	else if ( constantValue instanceof String )
    		type = "java/lang/String";
    	else
    		type = "java/lang/Object";  // ?
    }
    
    public String toString ( )
    {
    	String conStr;
    	if ( constantValue instanceof Integer )
    		conStr = "Integer:" + ((Integer)constantValue).toString();
    	else if ( constantValue instanceof Double )
    		conStr = "Double:" + ((Double)constantValue).toString();
    	else if ( constantValue instanceof Float )
    		conStr = "Float:" + ((Float)constantValue).toString();
    	else if ( constantValue instanceof Long )
    		conStr = "Long:" + ((Long)constantValue).toString();
    	else if ( constantValue instanceof String )
    		conStr = "String:" + ((String)constantValue).toString();
    	else
    		conStr = "Object: null";  // we only do an aconst"null"
    	return conStr;
    	
    }
    
    public String getConstant ( )
    {
    	String conStr;
    	if ( constantValue instanceof Integer )
    	{
    		if ( ((Integer) constantValue).intValue() > 1000 )
    			conStr = "0x" + Integer.toHexString(((Integer)constantValue).intValue());
    		else
    			conStr = ((Integer)constantValue).toString();
    	}
    	else if ( constantValue instanceof Double )
    		conStr = ((Double)constantValue).toString();
    	else if ( constantValue instanceof Float )
    		conStr = ((Float)constantValue).toString();
    	else if ( constantValue instanceof Boolean )
    		conStr = ((Boolean)constantValue).toString();
    	else if ( constantValue instanceof Long )
    	{
    		if ( ((Long) constantValue).intValue() > 1000 )
    			conStr = "0x" + Long.toHexString(((Long)constantValue).intValue());
    		else
    			conStr = ((Long)constantValue).toString();
    	}
    	else if ( constantValue instanceof String && (((String)constantValue).length() == 0) )
    		conStr = "\"\"";
    	else if ( constantValue instanceof String )
    		conStr = "\""+((String)constantValue).toString()+"\"";
    	else if ( constantValue == null )
    		conStr = "null";  
    	else
    		conStr = "null";
    	return conStr;
    	
    }
    
    
    public Object getConstantObj ( )
    {
    	return constantValue;
    }
  
    public String getType ( )
    {
    	return type;
    }
    
    public void setObjectAndType ( Object val, String type )
    {
    	constantValue = val;
    	this.type = type;
    }
    
}
