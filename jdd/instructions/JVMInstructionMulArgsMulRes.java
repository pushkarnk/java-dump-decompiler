package jdd.instructions;

import jdd.dumpreader.ByteCodeTable;
import jdd.operands.LocalVariable;
import jdd.operands.Operand;
import jdd.operands.StackSlot;


/* Dups need this. A small violation of polymorphism here.*/
/* TODO: This looks like a design flaw. Consider correction.*/

public class JVMInstructionMulArgsMulRes extends JVMInstruction {
	Operand [] operands;
	
	public JVMInstructionMulArgsMulRes ( Operand res, Operand[] in, int byteCode, int bcLength, int pc  )
	{
		super ( res, byteCode, bcLength, pc);
		operands = in;
	}
	
	public String toString ( )
	{
		String jvmInstr = "+" + pc + "\n" + ByteCodeTable.getBytecode(opCode);
		for ( int i = 0 ; i < operands.length; i++ )
			jvmInstr += "\nO:" + operands[i].toString();
		for ( int j= 0 ; j < results.length; j++ )
			jvmInstr += "\nR:" + results[j].toString();
		return jvmInstr;
	}

	@Override
	public void linkSourceOperands() {
		for ( int i  = 0; i < operands.length; i++ )
		{
			if ( operands[i] instanceof StackSlot )
				((StackSlot)operands[i]).setConsumer(this);
			else if ( operands[i] instanceof LocalVariable )
				((LocalVariable)operands[i]).setConsumer(this);
		}
	}
	
	public Operand[] getSourceOperands ( )
	{
		return operands;
	}

}
