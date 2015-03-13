package jdd.instructions;

import jdd.dumpreader.ByteCodeTable;
import jdd.operands.ClassRef;
import jdd.operands.Operand;
import jdd.operands.StackSlot;

/* I am introducing this only for multinewarray, 
 * which can have variable number of operands on the stack. 
 * Similar to the invoke instructions, but not an invoke!
 * */

public class JVMInstructionVarArgs extends JVMInstruction{
	int dimensions;
	ClassRef type;
	Operand [] dimensionsVals;
	public JVMInstructionVarArgs ( Operand result, ClassRef classRef, int dimensions, Operand[] vals, int byteCode, int bcLength, int pc )
	{
		super ( result, byteCode, bcLength, pc);
		this.dimensions = dimensions;
		this.type = classRef;
		dimensionsVals = vals;
	}
	
	public String toString ( )
	{
		String jvmInstr = "+" + pc + "\n" + ByteCodeTable.getBytecode(opCode);
		jvmInstr += "\nO:" + type.toString ( );
		jvmInstr += "\ndim:" + dimensions;
		for ( int i = 0; i < dimensionsVals.length; i++ )
			jvmInstr += "\nO:" + dimensionsVals[i].toString();
		jvmInstr += "\nR:" + resultOperand.toString();
		return jvmInstr;
	}

	@Override
	public void linkSourceOperands() 
	{
		//all operands are in stackslots
		for ( int i = 0 ; i < dimensionsVals.length; i++ )
			((StackSlot)dimensionsVals[i]).setConsumer(this);	
	}
	
	public Operand[] getSourceOperands ( )
	{
		return dimensionsVals;
	}
	
	public int getDimensions ( )
	{
		return dimensions;
	}
	
	public Operand getClassRef ( )
	{
		return type;
	}
	

}
