package jdd.instructions;

import java.util.ArrayList;

import jdd.dumpreader.ByteCodeTable;
import jdd.operands.Operand;


public class JVMInstructionTableSwitch extends JVMInstruction {
    int high, low, defaultOffset;
    ArrayList<Integer> jmpOffsets;

	
	public JVMInstructionTableSwitch  ( int type, int low, int high, int defCase, ArrayList<Integer> jmpOffsets, int byteCode, int bcLength, int pc )
	{
		super ( null,byteCode ,bcLength, pc);
		this.low = low;
		this.high = high;
		this.defaultOffset = defCase;
		this.jmpOffsets = jmpOffsets;	
	}
	
	public String toString ( )
	{
		String jvmInstrStr = "+"  + pc + "\n" + ByteCodeTable.getBytecode(opCode);
		jvmInstrStr += "\nlow=" + low;
		jvmInstrStr += "\nhigh" + high;
		for ( int i = 0; i < high-low+1; i++ )
			jvmInstrStr += "\n" + (i+low) + "->" + jmpOffsets.toArray()[i];
		jvmInstrStr += "\ndefault ->" + defaultOffset;
		return jvmInstrStr;
	}

	@Override
	public void linkSourceOperands()
	{
		// no source operands	
	}
	
	public Operand[] getSourceOperands ( )
	{
		return null;
	}
}