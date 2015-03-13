package jdd.operands;

public class BranchDestination extends Operand {
	int branchDestinationPC;
	
	public BranchDestination ( int branch )
	{
		branchDestinationPC = branch;
	}
 
	public String toString ( )
	{
		return "B:" + branchDestinationPC;
	}
	
	public int getPc ( )
	{
		return branchDestinationPC;
	}
	
	public void setPc ( int pc)
	{
		branchDestinationPC = pc;
	}
}
