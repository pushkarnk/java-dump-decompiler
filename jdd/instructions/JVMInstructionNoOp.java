package jdd.instructions;

import jdd.dumpreader.ByteCodeTable;
import jdd.operands.Operand;

public class JVMInstructionNoOp extends JVMInstruction {

	public JVMInstructionNoOp ( int byteCode, int bcLength, int pc )
	{
		super ( null, byteCode, bcLength, pc );
	}
	
	public String toString ( )
	{
		return "+" + pc + "\n" + ByteCodeTable.getBytecode(opCode);
	}
	
	@Override
	public void linkSourceOperands() {
		// no source operands
	}
	
	public Operand[] getSourceOperands ( )
	{
		return null;
	}
}
