package jdd.instructions;

import jdd.dumpreader.ByteCodeTable;
import jdd.operands.Operand;
import jdd.operands.StackSlot;

public class JVMInstructionInvoke extends JVMInstruction {
	Operand[]  argumentList;
	Operand    invokeMethod;
	
	public JVMInstructionInvoke ( Operand result, Operand invokeMethod, Operand[] argumentList, int opCode, int length, int pc )
	{
		super ( result, opCode, length, pc );
		this.invokeMethod = invokeMethod;
		this.argumentList = argumentList;
	}
	
	public String toString ( )
	{
		String invokeInstr = "+" + pc + "\n" + ByteCodeTable.getBytecode(opCode);
		invokeInstr += "\nO:"+invokeMethod.toString();
		
		for ( int i = 0 ; i < argumentList.length; i++ )
		{
			invokeInstr += "\nA:" + argumentList[i].toString(); 
		}
		if ( resultOperand != null )
			invokeInstr += "\nR:" + resultOperand.toString();
		return invokeInstr;
	}

	@Override
	public void linkSourceOperands() 
	{
	    /*All the arguments are in stack slots*/
		for ( int i = 0 ; i < argumentList.length; i ++ ) //there must be at least one argument - this
		{
			((StackSlot)argumentList[i]).setConsumer(this);
		}
	}
	
	public Operand[] getSourceOperands ( )
	{
		return argumentList;
	}
	
	public Operand getMethodReference ( )
	{
		return invokeMethod;
	}
}
