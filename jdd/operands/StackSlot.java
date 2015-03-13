package jdd.operands;

import com.ibm.j9ddr.debugextensions.vm24.jdd.instructions.JVMInstruction;

public class StackSlot extends Operand {
	
	/*We need the producer/consumer links for the TIF reduction algorithm*/
	private JVMInstruction producer;
	private JVMInstruction consumer;
	
	public StackSlot ( )
	{
	}
	
	public String toString ( )
	{
		String jvmOp = "SS:";
		if ( producer != null )
			jvmOp += " Prod:" + producer.getPC();
		if (consumer != null)
			jvmOp += " Cons:" + consumer.getPC();
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
	
}
