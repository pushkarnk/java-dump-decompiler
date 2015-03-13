package jdd.instructions;

import jdd.dumpreader.ByteCodeTable;
import jdd.operands.LocalVariable;
import jdd.operands.Operand;
import jdd.operands.StackSlot;

public class JVMInstructionBinary extends JVMInstruction{
    private Operand op1;
    private Operand op2;
    
    public JVMInstructionBinary ( Operand result, Operand op1, Operand op2, int opCode, int length, int pc )
    {
    	super ( result, opCode, length, pc );
    	setOperand1 ( op1 );
    	setOperand2 ( op2 );
    }
    
    public void setOperand1 ( Operand op )
    {
    	op1 = op;
    }
    
    public void setOperand2 ( Operand op )
    {
    	op2 = op;
    }
    
    public Operand getOperand1 ( )
    {
    	return op1;
    }
    
    public Operand getOperand2 ( )
    {
    	return op2;
    }
    
    public String toString ( )
    {
       String jvmInstr = "+" + pc + "\n" + ByteCodeTable.getBytecode(opCode);
       jvmInstr += "\nO:"+op1.toString();
       jvmInstr += "\nO:"+op2.toString();
       if ( resultOperand != null )
           jvmInstr += "\nR:" + resultOperand.toString();	
       return jvmInstr;
    }

	@Override
	public void linkSourceOperands() 
	{	
		if ( op1 instanceof StackSlot )
			((StackSlot)op1).setConsumer(this);
		else if ( op1 instanceof LocalVariable )
			((LocalVariable)op1).setConsumer(this);
		
		if ( op2 instanceof StackSlot )
			((StackSlot)op2).setConsumer(this);
		else if ( op2 instanceof LocalVariable )
			((LocalVariable)op2).setConsumer(this);
	}
	
	public Operand[] getSourceOperands ( )
	{
		return new Operand[] {op1,op2};
	}
}
