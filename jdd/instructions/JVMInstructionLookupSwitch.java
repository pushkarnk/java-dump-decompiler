package jdd.instructions;

import java.util.HashMap;
import java.util.Iterator;

import jdd.dumpreader.ByteCodeTable;
import jdd.operands.Operand;

public class JVMInstructionLookupSwitch extends JVMInstruction {
	
	int defCase;
	int numPairs;
	HashMap<Integer,Integer> lookupMap;
	public JVMInstructionLookupSwitch ( int defaultByte, int nPairs, HashMap<Integer,Integer> lookupMap, int byteCode, int bcLength, int pc )
	{
		super ( null, byteCode, bcLength, pc);
		this.defCase = defaultByte;
		this.numPairs = nPairs;
		this.lookupMap = lookupMap;
	}
	
	public String toString ( )
	{
		String jvmInstrStr = "+" + pc + "\n" + ByteCodeTable.getBytecode(opCode);
		jvmInstrStr += "\n" + "nPairs = " + numPairs;
	    Iterator<Integer> keys = lookupMap.keySet().iterator();
	    while ( keys.hasNext())
	    {
	    	Integer key = keys.next();
	    	jvmInstrStr += "\n" + key.intValue() + "->" + (Integer)lookupMap.get(key);
	    }
	    jvmInstrStr += "\n" + "default" + "->" + defCase;
	    return jvmInstrStr;
		
	}

	@Override
	public void linkSourceOperands() {
		// no operands
	}
	
	public Operand[] getSourceOperands ( )
	{
		return null;
	}

}
