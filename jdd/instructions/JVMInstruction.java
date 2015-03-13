package jdd.instructions;

import jdd.operands.LocalVariable;
import jdd.operands.Operand;
import jdd.operands.StackSlot;

abstract public class JVMInstruction implements Cloneable{
    protected int opCode;
    protected int length;
    protected int pc;
    Operand resultOperand;
    Operand[] results;
    JVMInstruction sourceInstruction; //used while creating source operand linkages
    Object extra;
    
    public JVMInstruction ( Operand result, int opCode, int length, int pc)
    {
    	results = null;
    	resultOperand = result;
    	this.opCode = opCode;
    	this.length = length;
    	this.pc = pc;
    	sourceInstruction = null;
    	extra = null;
    }
    
    public void setExtra ( Object extra )
    {
    	this.extra = extra;
    }
    
    public Object getExtra ( )
    {
    	return extra;
    }
    
    public void setSourceInstruction ( JVMInstruction source )
    {
    	sourceInstruction = source;
    }
 
    public JVMInstruction getSourceInstruction ( JVMInstruction source )
    {
    	return sourceInstruction;
    }
    
    public void setResults ( Operand [] results )
    {
    	if ( this instanceof JVMInstructionMulArgsMulRes )
    	{
    		this.results = results;
    	}
    }
    
    public int getPC ( )
    {
    	return pc;
    }
    
    public void setPC ( int pc )
    {
    	this.pc = pc;
    }
    
    public int getOpcode ( )
    {
    	return opCode;
    }
    public Operand getResult ( )
    {
    	return resultOperand;
    }
    
    public void linkResultOperands ( )
    {
    	if ( resultOperand != null )
    	{
            if ( resultOperand instanceof StackSlot )
    		    ((StackSlot)resultOperand).setProducer(this);
            else if ( resultOperand instanceof LocalVariable )
            	((LocalVariable)resultOperand).setProducer(this);
        }
    	
    	else if ( resultOperand == null && results != null && results.length > 0 )
    	{
    		for ( int i = 0 ; i < results.length; i++ )
    		{
    			if ( results[i] instanceof StackSlot )
    				((StackSlot)results[i]).setProducer(this);
    			else if ( results[i] instanceof LocalVariable )
    				((LocalVariable)results[i]).setProducer(this);
    		}
    	}
    }
    
    
    public void linkSourceOperands ( )
    {
    	
    }
    
    
    public Operand[] getResults ( )
    {
    	return results;
    }
    
    public  abstract Operand[] getSourceOperands ( );
	
}
