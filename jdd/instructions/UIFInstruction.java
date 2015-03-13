package jdd.instructions;

import jdd.operands.BranchDestination;
import jdd.operands.ClassRef;
import jdd.operands.Constant;
import jdd.operands.FieldRef;
import jdd.operands.LocalVariable;
import jdd.operands.MethodRef;

public class UIFInstruction {
	
	/*Beginning of vanilla*/
	public final static int LOAD            = 0;
	public final static int STORE 			= 1;
	public final static int ARRCREATE 		= 2;
	public final static int RDARRAY 		= 3;
	public final static int WRARRAY			= 4;
	public final static int ARRLENGTH		= 5;
	public final static int OBJCREATE		= 6;
	public final static int WRFIELD			= 7;
	public final static int RDFIELD			= 8;
	public final static int RETURN			= 9;
	public final static int ADD				= 10;
	public final static int SUB				= 11;
	public final static int DIV				= 12;
	public final static int MUL				= 13;
	public final static int NEG				= 14;
	public final static int MOD				= 15;
	public final static int INC				= 16;
	public final static int SHIFTL			= 17;
	public final static int SHIFTR			= 18;
	public final static int AND				= 19;
	public final static int OR				= 20;
	public final static int XOR				= 21;
	public final static int TYPECAST		= 22;
	public final static int INVOKE			= 23;
	public final static int SHIFTRU			= 24;
	public final static int INSTANCEOF      = 25;
	public final static int CONSTRUCT		= 26;
	public final static int IF              = 27;
	public final static int COMPARE         = 28;
	public final static int GOTO			= 29;
	public final static int TERNARY_OP	    = 30;
	
	public final static int EQ = 0, LE = 1, GE = 2, LT = 3, GT = 4, NE = 5;
	                     /*
						 ICMPEQ, ICMPNE, ICMPLT, ICMPLE, ICMPGT, ICMPGE, 
						 NONNULL, NULL, 
						 DCMPG, DCMPL,
						 FCMPG, FCMPL,
						 LCMP, INSTANCEOF */
	
	Object [] sources; //should be either a LocalVariable or another UIFInstruction
	Object    result;
	int instructionType;
	int pc;
	int bytecode;
	String dataType;
	boolean visitedInAnExpression;
	int [] typeUpdatesCache;
	int numTypeUpdates;
	boolean startInfiniteLoop = false;
	int infiniteLoopEndPC = -1;
	public UIFInstruction ( Object [] sources, Object result, int instructionType, int bytecode, int pc )
	{
		this.sources = sources;
		this.result = result;
		this.instructionType = instructionType;
		this.pc = pc;
		this.bytecode = bytecode;
		typeUpdatesCache = new int [32];
		numTypeUpdates = 0;
	}
	
	public void setStartInfiniteLoop ( )
	{
		startInfiniteLoop = true;
	}
	
	public void resetStartInfiniteLoop ( )
	{
		startInfiniteLoop = false;
	}
	public void setLoopEndPC ( int pc )
	{
		infiniteLoopEndPC = pc;
	}
	
	public int getLoopEndPC (  )
	{
		return infiniteLoopEndPC;
	}
	public boolean getStartInfiniteLoop ( )
	{
		return startInfiniteLoop;
	}
	public void addTypeUpdate ( int pc )
	{
		typeUpdatesCache[numTypeUpdates] = pc;
		numTypeUpdates ++;
	}
	
	public int[] getTypeUpdatePC ( )
	{
		return typeUpdatesCache;
	}
	
	public int getNumTypeUpdates ( )
	{
		return numTypeUpdates;
	}
	
	public void setVisited ( )
	{
		visitedInAnExpression = true;
	}
	
	public boolean isVisited ( )
	{
		return visitedInAnExpression;
	}
	
	public void setDataType ( String dt )
	{
		//if ( isArray(dt))
			//dataType = chopADimension(dt);
		if ( dt == "I")
			dt = "int";
		else if ( dt == "J")
			dt = "long";
		else if ( dt == "Z")
			dt = "boolean";
		else if ( dt == "C")
			dt = "char";
		else if ( dt == "F")
		    dt = "float";
		else if ( dt == "S")
			dt = "small";
		else if ( dt == "D")
			dt = "double";
		else if ( dt == "B")
			dt = "byte"; 
		dataType = dt;
	}
	
	public String getDataType ( )
	{
		return dataType;
	}
	
	public String toString( )
	{
		String returnString = pc + ":" + getStringFromType ( );
		returnString += "\nSources:";
		if ( sources == null )
			return returnString;
		for ( int i = 0 ; i < sources.length; i++ )
		{
			if ( sources[i] instanceof UIFInstruction )
				returnString += ("        INSTR:" + ((UIFInstruction)sources[i]).getPc());
			else if ( sources[i] instanceof LocalVariable )
				returnString += ("        LV:" + ((LocalVariable)sources[i]).getLVIndex());
			else if ( sources[i] instanceof Constant )
				returnString += ("        Con:"+ sources[i].toString());
			else if ( sources[i] instanceof FieldRef )
				returnString += ("        Fld:"+ sources[i].toString());
			else if ( sources[i] instanceof MethodRef )
				returnString += ("        Mth:"+ sources[i].toString());
			else if ( sources[i] instanceof ClassRef )
				returnString += ("        Cls:"+ sources[i].toString());
			else if ( sources[i] instanceof String )
				returnString += ("        Str:" + sources[i]);
			else if ( sources[i] instanceof BranchDestination )
				returnString += ("        JMP:" + sources[i].toString());
			else if ( sources[i] instanceof Integer)
				returnString += ("        " + sources[i].toString());
			else
				returnString += "        unknown";
		}
		if ( result instanceof LocalVariable )
			returnString += ("\nResult:         LV:" + ((LocalVariable)result).getLVIndex());
		else if ( result instanceof FieldRef )
			returnString += ("\nResult:         FD:" + ((FieldRef)result).toString());
		returnString += "\ntype = " + this.getDataType();
		return returnString;
	}
	
	private String getStringFromType ( )
	{
		switch ( instructionType )
		{
		    case STORE : 
		    	return "STORE";
		    case ARRCREATE:
		    	return "ARRCREATE";
		    case RDARRAY:
		    	return "RDARRAY";
		    case WRARRAY:
		    	return "WRARRAY";
		    case ARRLENGTH:
		    	return "ARRLENGTH";
		    case OBJCREATE:
		    	return "OBJCREATE";
		    case RDFIELD:
		    	return "RDFIELD";
		    case WRFIELD:
		    	return "WRFIELD";
		    case RETURN:
		    	return "RETURN";
		    case ADD:
		    	return "ADD";
		    case SUB:
		    	return "SUB";
		    case DIV:
		    	return "DIV";
		    case MUL:
		    	return "MUL";
		    case MOD:
		    	return "MOD";
		    case INC:
		    	return "INC";
		    case SHIFTL:
		    	return "SHIFTL";
		    case SHIFTR:
		    	return "SHIFTR";
		    case AND:
		    	return "AND";
		    case OR:
		    	return "OR";
		    case XOR:
		    	return "XOR";
		    case TYPECAST:
		    	return "TYPECAST";
		    case INVOKE:
		    	return "INVOKE";
		    case NEG:
		    	return "NEG";
		    case SHIFTRU:
		    	return "SHIFTRU";
		    case CONSTRUCT:
		    	return "CONSTRUCT";
		    case INSTANCEOF:
		    	return "INSTANCEOF";
		    case IF:
		    	return "IF";
		    case COMPARE:
		    	return "COMPARE";
		    case GOTO:
		    	return "GOTO";
		    case TERNARY_OP:
		    	return "TERNARY_OP";
		    default:
		    	return "UNKNOWN";
		}
	}
	
	public int getInstructionType (  )
	{
		return instructionType;
	}
	
	public void setInstructionType ( int type )
	{
		instructionType = type;
	}
	
	public Object[] getSources ( )
	{
		return sources;
	}
	
	public Object getResult ( )
	{
		return result;
	}
	
	public void setResult ( Object result )
	{
		this.result = result;
	}
	
	public int getPc ( )
	{
		return pc;
	}
	
	public int getBytecode ( )
	{
		return bytecode;
	}
	
	private boolean isArray ( String dt )
	{
		if ( dt.contains("[]"))
			return true;
		return false;
	}
	
	private String chopADimension ( String dt )
	{
		StringBuffer rdt = new StringBuffer ( dt );
		rdt.deleteCharAt(rdt.length()-1);
		rdt.deleteCharAt(rdt.length()-1);
		return rdt.toString();
	}
	
}
