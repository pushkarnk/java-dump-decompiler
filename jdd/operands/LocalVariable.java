package jdd.operands;

import com.ibm.j9ddr.debugextensions.vm24.jdd.instructions.JVMInstruction;

public class LocalVariable extends Operand {
	int NUM_REUSE = 32;
    int lvIndex;
    boolean isArg = false;            
    String [] name;
    String [] fullClassName;
    String [] shortClassName;
    int count;
    boolean isInitializer = false;
    /*We need the producer/consumer links for the TIF reduction algorithm*/
    private JVMInstruction producer;
    private JVMInstruction consumer;
    
    public LocalVariable ( int lvIndex )
    {
    	this.lvIndex = lvIndex;
    	name = new String[NUM_REUSE];
    	fullClassName = new String[NUM_REUSE];
    	shortClassName = new String[NUM_REUSE];
    	count = -1;
    }
    
    public int getLVIndex ( )
    {
    	return lvIndex;
    }
    
    public void setNameAndType ( String fullClassName, String shortClassName, String name )
    {
    	count++;
    	this.fullClassName[count] = fullClassName;
    	this.shortClassName[count] = shortClassName;
    	this.name[count] = name;
    }
    
    public void setInitializer ( boolean value )
    {
    	isInitializer = true;
    }
    
    public boolean isInitializer ( )
    {
    	return isInitializer;
    }
    
    public String toString ( )
    {
    	if ( isArg )
    		return "arg" + lvIndex;
    	String jvmOp = "LV#" + lvIndex;
    	if ( producer != null)
    		jvmOp += " Prod:" + producer.getPC();
    	if ( consumer != null )
    		jvmOp +=  " Cons:" + consumer.getPC();
    	return jvmOp;
    }
    
    public void setProducer ( JVMInstruction jvmInstr )
    {
    	producer = jvmInstr;
    }
    
    public void setConsumer ( JVMInstruction jvmInstr )
    {
    	consumer = jvmInstr;
    }
    
    public JVMInstruction getConsumer ( )
    {
    	return consumer;
    }
    
    public JVMInstruction getProducer ( )
    {
    	return producer;
    }
        
    public boolean isArgumentSlot ( )
    {
    	return isArg;
    }
    
    public void setIsArg ( boolean isArg )
    {
    	this.isArg = isArg;
    }
    
    public String getName ( )
    {
    	if ( isInitializer )
    		return appendBrackets(shortClassName[count].split(";")[0]) + " " + name[count];
    	return name[count];
    }
    
    private String appendBrackets ( String shortName )
    {
    	StringBuffer nmBuff = new StringBuffer ( shortName );
    	for ( int k = 0; k < nmBuff.length(); k++ )
    		if ( nmBuff.charAt(k) == '[')
    			nmBuff.deleteCharAt(k);
    	
    	int idx=0;
    	while ( fullClassName[count].split("/")[0].charAt(idx) == '[')
    	{
    		idx++;
    		nmBuff.append("[]");
    	}
    	return nmBuff.toString();
    }
}
