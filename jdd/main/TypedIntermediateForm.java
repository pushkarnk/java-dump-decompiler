package jdd.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import jdd.dumpreader.Bytecodes;
import jdd.instructions.JVMInstruction;
import jdd.instructions.JVMInstructionBinary;
import jdd.instructions.JVMInstructionInvoke;
import jdd.instructions.JVMInstructionLookupSwitch;
import jdd.instructions.JVMInstructionMulArgsMulRes;
import jdd.instructions.JVMInstructionNoOp;
import jdd.instructions.JVMInstructionTableSwitch;
import jdd.instructions.JVMInstructionTernary;
import jdd.instructions.JVMInstructionUnary;
import jdd.instructions.JVMInstructionVarArgs;
import jdd.operands.BranchDestination;
import jdd.operands.ClassRef;
import jdd.operands.Constant;
import jdd.operands.FieldRef;
import jdd.operands.LocalVariable;
import jdd.operands.MethodRef;
import jdd.operands.Operand;
import jdd.operands.StackSlot;
import com.ibm.j9ddr.vm24.types.U32;

public class TypedIntermediateForm implements MethodDataCollector {
	
	/*Making this a Singleton. This will hold the entire bytecode graph and the method meta-data*/
	
	static final int NO_OPERATOR 		   = 0; //should have been NO_OPERAND, correction coming up in v2
	static final int VALUE       		   = 1;
	static final int CP_INDEX                  = 2;
	static final int LV_INDEX              	   = 3;
	static final int LV_INDEX_AND_CONSTANT     = 4;
	static final int BRANCH_OFFSET		   = 5;
	static final int INVOKE_INTERFACE          = 6;
	static final int ARRAY_TYPE                = 7;
	static final int MULTINEWARRAY	           = 8;
	static final int WIDE		           = 9;
	static final Operand MEMORY_FIELD = null; //side effect
	static final Operand NO_RESULT_OP = null;
	
	private TypedIntermediateForm ( )
	{
		methodData = new MethodMetaData ( );
		bytecodeGraph = new LinkedList<JVMInstruction> ( );
		debugInfo = new LinkedList<Object> ( );
	}
	
	public static TypedIntermediateForm getTIFObject ( )
	{
		return new TypedIntermediateForm ( );
	}
	
	private MethodMetaData                 methodData;
	private LinkedList<JVMInstruction>     bytecodeGraph;
	
	/* We should be prepared to generate this if the front end cannot provide it*/
	//TODO : Yet to design a type for debugInfo
	private LinkedList<Object>     debugInfo;

	public void setNameAndSignature(String name, String signature) {
	    methodData.setNameAndSignature(name, signature);
		
	}

	public JVMInstruction getJVMInstructionFromPC ( int pc )
	{
		Iterator<JVMInstruction> instrIter = this.getBytecodeIterator();
		while ( instrIter.hasNext() )
		{
			JVMInstruction instr = instrIter.next();
			if ( instr.getPC() == pc )
				return instr;
		}
		return null;
	}
	
	public JVMInstruction getNextJVMInstructionFromPC ( int pc )
	{
		Iterator<JVMInstruction> instrIter = this.getBytecodeIterator();
		while ( instrIter.hasNext() )
		{
			JVMInstruction instr = instrIter.next();
			if ( instr.getPC() == pc )
				if ( instrIter.hasNext() )
					return instrIter.next();
		}
		return null;
	}
	
	public JVMInstruction getPrevJVMInstructionFromPC ( int pc )
	{
		Iterator<JVMInstruction> instrIter = this.getBytecodeIterator();
		JVMInstruction prev = null;
		JVMInstruction instr = null;
		while ( instrIter.hasNext() )
		{
			prev  = instr;
			instr = instrIter.next();
			if ( instr.getPC() == pc )
				return prev;
		}
		return null;
	}
	
	public Object[] getBytecodeArray ( )
	{
		return bytecodeGraph.toArray();
	}
	
	public MethodMetaData getMethodMetaData ( )
	{
		return methodData;
	}
	
	public void setModifiers ( U32 modifiers )
	{
	    methodData.setModifiers ( modifiers );	
	}
	
	public void setClassName ( String className )
	{
	    methodData.setClassName(className);	
	}
	
	public void setMaxStack(int maxStack) 
	{
		methodData.setMaxStack(maxStack);
	}

	public void setArgumentsAndTempCount(int arguments, int temp) 
	{
	    methodData.setArgumentAndTempCount(arguments, temp);	
	}

	public void addACaughtExceptionType(int start, int end, int handler, String exceptionName) 
	{
		methodData.addACaughtException(start, end, handler, exceptionName);
	}

	public void addAThrownException(String exceptionName) 
	{
		methodData.addAThrownException(exceptionName);
	}

	public void addAJVMInstruction(int byteCode, int bcType, int bcLength,int pc, Object[] operands) 
	{
	    switch ( bcType )
	    {
	        /* sipush, bipush, newarray */
	        case VALUE: 
	        case ARRAY_TYPE:
	        	Constant constantOperand = new Constant ((Integer)operands[0]);
	        	if ( byteCode == Bytecodes.newarray )
	        		bytecodeGraph.add(new JVMInstructionBinary ( new StackSlot( ), constantOperand, new StackSlot( ), byteCode,bcLength,pc ));
	        	else
	        	    bytecodeGraph.add(new JVMInstructionUnary ( new StackSlot( ), constantOperand,byteCode,bcLength,pc ));
	        	break;
	        /*invokevirtual, invokespecial, invokestatic, invokeinterface */
	        /*getfield, putfield, getstatic, putstatic, instanceof, checkcast */
	        /*ldc, ldcw, ldc2lw, ldc2dw, anewarray, multianewarray, new*/
	        case CP_INDEX:
	        case INVOKE_INTERFACE:	
	        case MULTINEWARRAY:
	        	int cpIndex;
	        	String fieldName, methodName, className, signature;
	            switch ( byteCode )
	            {
	                case Bytecodes.invokevirtual:
	                case Bytecodes.invokespecial:
	                case Bytecodes.invokestatic: 
	                case Bytecodes.invokeinterface:
	                	/*The only operand that we currently have is the method data*/
	                	cpIndex = ((Integer)operands[0]).intValue();
	                	methodName = ((LinkedList<String>)operands[1]).toArray()[0].toString();
	                	className  = ((LinkedList<String>)operands[1]).toArray()[1].toString();
	                	signature  = ((LinkedList<String>)operands[1]).toArray()[2].toString();
	                	int numArgs = ((Integer)operands[2]).intValue();
	                	MethodRef invokeMethod = new MethodRef ( cpIndex, methodName, className, signature );
	                	StackSlot [] slot0 = new StackSlot[numArgs];
	               
	                	for ( int i = 0 ; i < slot0.length; i++ )
	                		slot0[i] = new StackSlot();
	                	if ( signature.charAt(signature.length()-1) == 'V') //void 
	                		bytecodeGraph.add( new JVMInstructionInvoke(NO_RESULT_OP,invokeMethod, slot0 , byteCode, bcLength, pc) );
	                	else 
	                		bytecodeGraph.add( new JVMInstructionInvoke(new StackSlot(),invokeMethod, slot0 , byteCode, bcLength, pc) );
	                	break;
	                
	                case Bytecodes.putfield:
	                case Bytecodes.getfield:
	                case Bytecodes.getstatic:
	                case Bytecodes.putstatic:
	                	/*We have only the field reference for now*/
	                	cpIndex = ((Integer)operands[0]).intValue();
	                	fieldName = ((LinkedList<String>)operands[1]).toArray()[0].toString();
	                	className = ((LinkedList<String>)operands[1]).toArray()[1].toString();
	                	signature = ((LinkedList<String>)operands[1]).toArray()[2].toString();
	                	FieldRef field = new FieldRef ( cpIndex, fieldName, className, signature );
	                	if ( byteCode == Bytecodes.putfield)
	                		bytecodeGraph.add( new JVMInstructionTernary ( MEMORY_FIELD,field, new StackSlot(),new StackSlot(),byteCode, bcLength, pc));
	                	else if ( byteCode == Bytecodes.getfield ) 
	                		bytecodeGraph.add( new JVMInstructionBinary ( new StackSlot(),field, new StackSlot(),byteCode, bcLength, pc));
	                	else if ( byteCode == Bytecodes.putstatic )
	                		bytecodeGraph.add( new JVMInstructionBinary ( MEMORY_FIELD,field, new StackSlot(),byteCode, bcLength, pc));
	                	else
	                		bytecodeGraph.add(new JVMInstructionUnary ( new StackSlot ( ),field, byteCode, bcLength, pc));
	                	break;
	                	
	                case Bytecodes.ldc:
	                case Bytecodes.ldcw:
	                case Bytecodes.ldc2lw:
	                case Bytecodes.ldc2dw:
	                	/* We have the constants */
	                	Constant constantObj;
	                	cpIndex = ((Integer)operands[0]).intValue();
	                	String type = ((LinkedList<String>)operands[1]).toArray()[0].toString();
	                	String value = ((LinkedList<String>)operands[1]).toArray()[1].toString();
	                	if ( type.equals("int") || type.equals("long"))
	                		constantObj = new Constant ( new Long (value)); //to support unsigned ints :(
	                	else if ( type.equals("String"))
	                		constantObj = new Constant ( value );
	                	else
	                		constantObj = new Constant (new Double (value));
	                	 bytecodeGraph.add( new JVMInstructionUnary(new StackSlot(),constantObj,byteCode, bcLength, pc));
	                	 break;
	                
	                case Bytecodes.anewarray:
	                case Bytecodes.Jinstanceof:
	                case Bytecodes.checkcast:
	                case Bytecodes.Jnew:	
	                	cpIndex = ((Integer)operands[0]).intValue();
	                	className = ((LinkedList<String>)operands[1]).toArray()[0].toString();
	                	ClassRef classRef = new ClassRef ( cpIndex, className );
	                	if ( byteCode == Bytecodes.Jnew )
	                		bytecodeGraph.add(new JVMInstructionUnary ( new StackSlot(), classRef, byteCode, bcLength, pc));
	                	else
	                		bytecodeGraph.add(new JVMInstructionBinary ( new StackSlot(), classRef, new StackSlot(), byteCode, bcLength, pc));
	                    break;
	                    
	                case Bytecodes.multianewarray:
	                	cpIndex = ((Integer)operands[0]).intValue();
	                	int dimensions = ((Integer)operands[1]).intValue();
	                	className = ((LinkedList<String>)operands[2]).toArray()[0].toString();
	                	StackSlot dimVal[] = new StackSlot[dimensions];
	                	for ( int i = 0 ; i <dimensions; i++ )
	                		dimVal[i] = new StackSlot ( );
	                	bytecodeGraph.add( new JVMInstructionVarArgs ( new StackSlot(), new ClassRef ( cpIndex, className), dimensions,dimVal, byteCode, bcLength, pc));
	                	break;
	                	
	            }
	            break;
	        
	        case LV_INDEX:
	        	switch ( byteCode )
	        	{
	        	    case Bytecodes.aload:
	        	    case Bytecodes.iload:
	        	    case Bytecodes.lload:
	        	    case Bytecodes.dload:
	        	    case Bytecodes.fload:
	        	    	Integer index = (Integer)operands[0];
	        	    	bytecodeGraph.add(new JVMInstructionUnary ( new StackSlot (), new LocalVariable ( index.intValue()), byteCode, bcLength, pc));
	        	    	break;
	        	    case Bytecodes.istore:
	        	    case Bytecodes.astore:
	        	    case Bytecodes.lstore:
	        	    case Bytecodes.fstore:
	        	    case Bytecodes.dstore:
	        	    	index = (Integer)operands[0];
	        	    	bytecodeGraph.add( new JVMInstructionUnary ( new LocalVariable(index.intValue()), new StackSlot ( ), byteCode, bcLength, pc));
	        	    	break;
	        	    	        	    	
	        	}
	            break;
	            
	         /*iinc*/   
	        case LV_INDEX_AND_CONSTANT:
	        	LocalVariable lv = new LocalVariable ( ((Integer)operands[0]).intValue());
	        	Constant     con = new Constant ( (Integer)operands[1]);
	        	bytecodeGraph.add(new JVMInstructionBinary ( lv, lv, con, byteCode, bcLength, pc));
	        	break;
	        
	        /*ifeq,ifge,ifgt,ifle,iflt,ifne*/
	        /*ificmpeq,ificmpne,ifcmpge,ificmpgt,ificmplt,ificmple*/
	        /*ifacmpeq,ifacmpne,goto,gotow*/
	        case BRANCH_OFFSET:
	        case WIDE:
	        	BranchDestination branchDest = new BranchDestination ( ((Integer)operands[0]).intValue());
	        	switch ( byteCode )
	        	{
	        	    case Bytecodes.Jgoto:
	        	    case Bytecodes.gotow:
	        	    	bytecodeGraph.add( new JVMInstructionUnary ( NO_RESULT_OP,branchDest, byteCode, bcLength, pc) );
	        	    	break;
	        		case Bytecodes.ifeq:
	        		case Bytecodes.ifge:
	        		case Bytecodes.ifgt:
	        		case Bytecodes.ifle:
	        		case Bytecodes.iflt:
	        		case Bytecodes.ifne:
	        		case Bytecodes.ifnull:
	        		case Bytecodes.ifnonnull:
	        			bytecodeGraph.add( new JVMInstructionBinary ( NO_RESULT_OP,branchDest, new StackSlot ( ), byteCode, bcLength, pc));
	        			break;
	        		case Bytecodes.ifacmpeq:
	        		case Bytecodes.ifacmpne:
	        		case Bytecodes.ificmpeq:
	        		case Bytecodes.ificmpge:
	        		case Bytecodes.ificmpgt:
	        		case Bytecodes.ificmple:
	        		case Bytecodes.ificmplt:
	        		case Bytecodes.ificmpne:
	        			bytecodeGraph.add( new JVMInstructionTernary ( NO_RESULT_OP,branchDest, new StackSlot ( ), new StackSlot ( ), byteCode, bcLength, pc));
	        			break;
	        		
	        	}
            	break;           
	        default:
	        	switch( byteCode )
	        	{
	        		case Bytecodes.aconstnull:
	        			bytecodeGraph.add( new JVMInstructionUnary ( new StackSlot(), new Constant(null),byteCode, bcLength, pc)); //null has significance here
	        			break;
	        		case Bytecodes.iconstm1:
	        			bytecodeGraph.add( new JVMInstructionUnary ( new StackSlot(), new Constant(new Integer(-1)) ,byteCode, bcLength, pc));
	        			break;
	        		case Bytecodes.iconst0:
	        			bytecodeGraph.add( new JVMInstructionUnary ( new StackSlot(), new Constant(new Integer(0)),byteCode, bcLength, pc));
	        			break;
	        		case Bytecodes.lconst0:
	        			bytecodeGraph.add( new JVMInstructionUnary ( new StackSlot(), new Constant(new Long(0)),byteCode, bcLength, pc));
	        			break;
	        		case Bytecodes.fconst0:
	        			bytecodeGraph.add( new JVMInstructionUnary ( new StackSlot(), new Constant(new Float(0)),byteCode, bcLength, pc));
	        			break;
	        		case Bytecodes.dconst0:
	        			bytecodeGraph.add( new JVMInstructionUnary ( new StackSlot(), new Constant(new Double(0)), byteCode, bcLength, pc));
	        			break;
	        		case Bytecodes.iconst1:
	        			bytecodeGraph.add( new JVMInstructionUnary ( new StackSlot(), new Constant(new Integer(1)),byteCode, bcLength, pc));
	        			break;
	        		case Bytecodes.fconst1:
	        			bytecodeGraph.add( new JVMInstructionUnary ( new StackSlot(), new Constant(new Float(1.0)),byteCode, bcLength, pc));
	        			break;
	        		case Bytecodes.lconst1:
	        			bytecodeGraph.add( new JVMInstructionUnary ( new StackSlot(), new Constant(new Long ( 1L)),byteCode, bcLength, pc));
	        			break;
	        		case Bytecodes.dconst1:
	        			bytecodeGraph.add( new JVMInstructionUnary ( new StackSlot(), new Constant(new Double (1.0d)),byteCode, bcLength, pc));
	        			break;
	        		case Bytecodes.iconst2:
	        			bytecodeGraph.add( new JVMInstructionUnary ( new StackSlot(), new Constant(new Integer(2)),byteCode, bcLength, pc));
	        			break;
	        		case Bytecodes.iconst3:
	        			bytecodeGraph.add( new JVMInstructionUnary ( new StackSlot(), new Constant(new Integer(3)),byteCode, bcLength, pc));
	        			break;
	        		case Bytecodes.iconst4:
	        			bytecodeGraph.add( new JVMInstructionUnary ( new StackSlot(), new Constant (new Integer(4)),byteCode, bcLength, pc));
	        			break;
	        		case Bytecodes.iconst5:
	        			bytecodeGraph.add( new JVMInstructionUnary ( new StackSlot(), new Constant(new Integer(5)),byteCode, bcLength, pc));
	        			break;
	        		case Bytecodes.aload0getfield:
	        		case Bytecodes.aload0:
	        		case Bytecodes.iload0:
	        		case Bytecodes.fload0:
	        		case Bytecodes.dload0:
	        		case Bytecodes.lload0:
	        			bytecodeGraph.add(new JVMInstructionUnary( new StackSlot(), new LocalVariable(0), byteCode, bcLength, pc));
	        			break;
	        		case Bytecodes.aload1:
	        		case Bytecodes.iload1:
	        		case Bytecodes.lload1:
	        		case Bytecodes.fload1:
	        		case Bytecodes.dload1:
	        			bytecodeGraph.add(new JVMInstructionUnary ( new StackSlot(), new LocalVariable(1), byteCode, bcLength, pc));
	        			break;
	        		case Bytecodes.aload2:
	        		case Bytecodes.iload2:
	        		case Bytecodes.lload2:
	        		case Bytecodes.fload2:
	        		case Bytecodes.dload2:
	        			bytecodeGraph.add(new JVMInstructionUnary( new StackSlot(), new LocalVariable(2), byteCode, bcLength, pc));
	        			break;
	        		case Bytecodes.aload3:
	        		case Bytecodes.iload3:
	        		case Bytecodes.lload3:
	        		case Bytecodes.fload3:
	        		case Bytecodes.dload3:
	        			bytecodeGraph.add(new JVMInstructionUnary( new StackSlot(), new LocalVariable(3), byteCode, bcLength, pc));
	        			break;
	        		case Bytecodes.aaload:
	        		case Bytecodes.iaload:
	        		case Bytecodes.faload:
	        		case Bytecodes.daload:
	        		case Bytecodes.saload:
	        		case Bytecodes.caload:
	        		case Bytecodes.laload:
	        		case Bytecodes.baload:
	        			bytecodeGraph.add( new JVMInstructionBinary( new StackSlot(), new StackSlot ( ), new StackSlot( ), byteCode, bcLength, pc));
	        			break; 
	        		case Bytecodes.istore0:
	        		case Bytecodes.dstore0:
	        		case Bytecodes.fstore0:
	        		case Bytecodes.lstore0:
	        		case Bytecodes.astore0:
	        			bytecodeGraph.add( new JVMInstructionUnary ( new LocalVariable(0), new StackSlot(), byteCode, bcLength, pc));
	        			break;
	        		case Bytecodes.istore1:
	        		case Bytecodes.dstore1:
	        		case Bytecodes.fstore1:
	        		case Bytecodes.lstore1:
	        		case Bytecodes.astore1:	
	        			bytecodeGraph.add( new JVMInstructionUnary ( new LocalVariable(1), new StackSlot(), byteCode, bcLength, pc));
	        			break;
	        		case Bytecodes.istore2:
	        		case Bytecodes.dstore2:
	        		case Bytecodes.fstore2:
	        		case Bytecodes.lstore2:
	        		case Bytecodes.astore2:	
	        			bytecodeGraph.add( new JVMInstructionUnary ( new LocalVariable(2), new StackSlot(), byteCode, bcLength, pc));
	        			break;
	        		case Bytecodes.istore3:
	        		case Bytecodes.dstore3:
	        		case Bytecodes.fstore3:
	        		case Bytecodes.lstore3:
	        		case Bytecodes.astore3:	
	        			bytecodeGraph.add( new JVMInstructionUnary ( new LocalVariable(3), new StackSlot(), byteCode, bcLength, pc));
	        			break;
	        		case Bytecodes.iastore:
	        		case Bytecodes.fastore:
	        		case Bytecodes.lastore:
	        		case Bytecodes.dastore:
	        		case Bytecodes.aastore:
	        		case Bytecodes.sastore:
	        		case Bytecodes.castore:
	        		case Bytecodes.bastore:
	        			bytecodeGraph.add( new JVMInstructionTernary ( MEMORY_FIELD, new StackSlot ( ), new StackSlot ( ), new StackSlot ( ), byteCode, bcLength, pc));
	        			break;
	        		case Bytecodes.pop:
	        			bytecodeGraph.add( new JVMInstructionUnary ( NO_RESULT_OP, new StackSlot ( ), byteCode, bcLength, pc));
	        			break;
	        		case Bytecodes.pop2:
	        			bytecodeGraph.add( new JVMInstructionBinary ( NO_RESULT_OP, new StackSlot ( ), new StackSlot ( ), byteCode, bcLength, pc));
	        			break;
	        		case Bytecodes.dup:
	        		case Bytecodes.dupx1:
	        		case Bytecodes.dupx2:
	        			StackSlot slot1[] = {new StackSlot()};
	        			StackSlot res1[] = {new StackSlot()};
	        			JVMInstructionMulArgsMulRes jvmInstr = new JVMInstructionMulArgsMulRes ( NO_RESULT_OP, slot1, byteCode, bcLength, pc);
	        			jvmInstr.setResults(res1);
	        			bytecodeGraph.add( jvmInstr );
	        			break;
	        		case Bytecodes.dup2:
	        		case Bytecodes.dup2x1:
	        		case Bytecodes.dup2x2:
	        		case Bytecodes.swap:
	        			StackSlot slot2[] = {new StackSlot(), new StackSlot()};
	        			StackSlot res2[] = {new StackSlot(), new StackSlot()};
	        			jvmInstr = new JVMInstructionMulArgsMulRes ( NO_RESULT_OP, slot2, byteCode, bcLength, pc);
	        			jvmInstr.setResults(res2);
	        			bytecodeGraph.add( jvmInstr );
	        			break;
	        		case Bytecodes.iadd:
	        		case Bytecodes.dadd:
	        		case Bytecodes.fadd:
	        		case Bytecodes.ladd:
	        		case Bytecodes.isub:
	        		case Bytecodes.dsub:
	        		case Bytecodes.fsub:
	        		case Bytecodes.lsub:
	        		case Bytecodes.imul:
	        		case Bytecodes.lmul:
	        		case Bytecodes.fmul:
	        		case Bytecodes.dmul:
	        		case Bytecodes.idiv:
	        		case Bytecodes.ldiv:
	        		case Bytecodes.fdiv:
	        		case Bytecodes.ddiv:
	        		case Bytecodes.irem:
	        		case Bytecodes.lrem:
	        		case Bytecodes.drem:
	        		case Bytecodes.frem:
	        		case Bytecodes.ishl:
	        		case Bytecodes.lshl:
	        		case Bytecodes.ishr:
	        		case Bytecodes.lshr:
	        		case Bytecodes.iushr:
	        		case Bytecodes.lushr:
	        		case Bytecodes.iand:
	        		case Bytecodes.land:
	        		case Bytecodes.ixor:
	        		case Bytecodes.lxor:
	        		case Bytecodes.ior:
	        		case Bytecodes.lor:
	        		case Bytecodes.lcmp:
	        		case Bytecodes.fcmpg:
	        		case Bytecodes.fcmpl:
	        		case Bytecodes.dcmpl:
	        		case Bytecodes.dcmpg:
	        			bytecodeGraph.add( new JVMInstructionBinary ( new StackSlot ( ), new StackSlot ( ), new StackSlot ( ), byteCode, bcLength, pc) );
	        			break;
	        		case Bytecodes.ineg:
	        		case Bytecodes.lneg:
	        		case Bytecodes.fneg:
	        		case Bytecodes.dneg:
	        		case Bytecodes.i2b:
	        		case Bytecodes.i2c:
	        		case Bytecodes.i2d:
	        		case Bytecodes.i2f:
	        		case Bytecodes.i2l:
	        		case Bytecodes.i2s:
	        		case Bytecodes.l2d:
	        		case Bytecodes.l2f:
	        		case Bytecodes.l2i:
	        		case Bytecodes.d2f:
	        		case Bytecodes.d2i:
	        		case Bytecodes.d2l:
	        		case Bytecodes.f2d:
	        		case Bytecodes.f2i:
	        		case Bytecodes.f2l:
	        		case Bytecodes.arraylength:
	        		    bytecodeGraph.add( new JVMInstructionUnary ( new StackSlot ( ), new StackSlot ( ), byteCode, bcLength, pc));
	        		    break;
	        		case Bytecodes.athrow:
	        		/* ATHROW is a special case, the created exception object is popped off the stack first.
	        		 * Then, after a suitable handler is found, it is reincarnated on the stack.
	        		 * Because this is a pseudo execution, we can afford to pop if off. Keeping it on the stack
	        		 * will cause some irregularities for future bytecode analysis. We can get it back on the stack if we are 
	        		 * to enter a catch block in the same method..
	        		 * Something like
	        		 * try {
	        		 *     throw new NullPointerException ( )
	        		 * } catch ( Exception e )
	        		 * {
	        		 *     e.printStackTrace ( );
	        		 * }
	        		 * When we are walking the bytecodes in the catch block, we will need the exception object on the stack.
	        		 * The exception object information can be derived from the preserved source operand of the athrow. 
	        		 * Hence pop.
	        		 */
	        		case Bytecodes.monitorenter:
	        		case Bytecodes.monitorexit:
	        			bytecodeGraph.add( new JVMInstructionUnary ( NO_RESULT_OP, new StackSlot ( ), byteCode, bcLength, pc));
	        			break;
	        		case Bytecodes.genericReturn:
	        		case Bytecodes.retFromNative0:
	        		case Bytecodes.retFromNative1:
	        		case Bytecodes.retFromNativeD:
	        		case Bytecodes.retFromNativeF:
	        		case Bytecodes.retFromNativeJ:
	        		case Bytecodes.return0:
	        		case Bytecodes.return1:
	        		case Bytecodes.return2:
	        		case Bytecodes.returnFromConstructor:
	        		case Bytecodes.returnToMicroJIT:
	        		case Bytecodes.invokeinterface2:
	        		case Bytecodes.syncReturn0:
	        		case Bytecodes.syncReturn1:
	        		case Bytecodes.syncReturn2:
	        			if ( methodReturnsVoid ( methodData ))
	        				bytecodeGraph.add(new JVMInstructionNoOp ( byteCode, bcLength, pc));
	        			else
	        				bytecodeGraph.add( new JVMInstructionUnary ( NO_RESULT_OP, new StackSlot ( ), byteCode, bcLength, pc));
	        			break;
	        		case Bytecodes.tableswitch:
	        			int low = ((Integer)operands[0]).intValue();
	        			int high = ((Integer)operands[1]).intValue();
	        			int defCase = ((Integer)operands[2]).intValue();
	        			ArrayList<Integer> jumpOffsets = (ArrayList<Integer>)operands[3];
	        			bytecodeGraph.add( new JVMInstructionTableSwitch ( Bytecodes.tableswitch,low, high, defCase, jumpOffsets, byteCode, bcLength, pc ));
	        			break;
	        		case Bytecodes.lookupswitch:
	        			int nPairs = ((Integer)operands[0]).intValue();
	        			int deft   = ((Integer)operands[1]).intValue();
	        		    HashMap<Integer,Integer> lookupMap = (HashMap<Integer,Integer>)operands[2];
	        		    bytecodeGraph.add ( new JVMInstructionLookupSwitch ( deft, nPairs, lookupMap, byteCode, bcLength, pc ));
	        		    break;
	        		default:
	        			System.out.println ( "Decompilation not supported.");
	        			return;
	        		
	        	}
	    }
	}
	
	public void dumpTIFGraph ( )
	{
		Iterator<JVMInstruction> iterator = bytecodeGraph.iterator();
		System.out.println ( "\n\n\n**Typed-Intermediate-Form Instructions**");
		while ( iterator.hasNext())
		    System.out.println ( iterator.next().toString() + "\n" );
		
	}
	
	
	/*Now, we go about the bytecode graph assigning producers and consumers to all stack slots and locals */
	/*Beware of branching! Use a depth first approach at branches */
	
	public void formOperandLinkages ( )
	{
		/* Form basic linkages*/
		doABytecodeWalkWithPseudoStack ( );
	}
	
	public Iterator<JVMInstruction> getBytecodeIterator ( )
	{
		return bytecodeGraph.iterator();
	}
	
	/* We need a fluctuating, pseudo, operand stack to establish all the operand linkages,
	 * and we will do an interpretation simulation, walking all possible code paths in a depth
	 * first search manner. This is a crucial step.
	 */
	
	private void registerArguments ( LocalVariable [] lvarArray, int numArgs )
	{
		for ( int index = 0 ; index < numArgs ; index++ )
		{
			lvarArray[index] = new LocalVariable ( index );
			lvarArray[index].setIsArg(true);
		}
	}
	
	
	
	private boolean isBranch ( int opCode )
	{
		switch ( opCode )
		{
			case Bytecodes.ifacmpeq: case Bytecodes.ifacmpne: case Bytecodes.ifeq:
			case Bytecodes.ifge:	case Bytecodes.ifgt: case Bytecodes.ificmpeq:
			case Bytecodes.ificmpge: case Bytecodes.ificmpgt: case Bytecodes.ificmple:
			case Bytecodes.ificmplt: case Bytecodes.ificmpne: case Bytecodes.ifle:
			case Bytecodes.iflt: case Bytecodes.ifne: case Bytecodes.ifnonnull:
			case Bytecodes.ifnull: 
				return true;
		}
		return false;
	}
	
	private int getIndexFromPC ( int pc )
	{
		Iterator<JVMInstruction> iterator = bytecodeGraph.iterator();
		int index = 0;
		while ( iterator.hasNext() )
		{
			if ( iterator.next().getPC() == pc )
				return index;
			index++;
		}
		return -1;
	}
	
	private void doABytecodeWalkWithPseudoStack ( )
	{
		/*Pseudo stack*/
		Vector<Operand> operandStack = new Vector<Operand> ( );
		LocalVariable [] lvarArray = new LocalVariable[methodData.getTempCount()+methodData.getArgumentCount()];
		
		/*Get the bytecode iterator*/
		Iterator<JVMInstruction> bytecodeIterator = bytecodeGraph.iterator();
		registerArguments ( lvarArray, methodData.getArgumentCount());
	
		JVMInstruction anchorIfExpr = null;
		int gotoDest = -1;
		while ( bytecodeIterator.hasNext())
		{
			JVMInstruction nextInstruction = bytecodeIterator.next ( );
			
			nextInstruction.linkResultOperands();
			nextInstruction.linkSourceOperands();
			
			if ( nextInstruction.getPC() == gotoDest )
			{
				//We have two possible values on the stack. Pop them and push a new value with the right linkage.
				System.out.println ( "At gotoDest size = " + operandStack.size());
				operandStack.remove(operandStack.size()-1);
				operandStack.remove(operandStack.size()-1);
				StackSlot newSlot = new StackSlot ( );
				System.out.println ( "Anchor" + anchorIfExpr.toString());
				newSlot.setProducer(anchorIfExpr);
				operandStack.add(newSlot);
				anchorIfExpr = null;
				gotoDest = -1;
			}
			
			if ( nextInstruction.getOpcode() == Bytecodes.genericReturn && operandStack.isEmpty()) //returns void
		         continue;
			
			if ( isBranch(nextInstruction.getOpcode()) && anchorIfExpr == null) //&& !operandStack.isEmpty()  && anchorIfExpr == null )
			{
				anchorIfExpr = nextInstruction;
				System.out.println ( "Anchor" + anchorIfExpr.toString());
			}
			
			if ( nextInstruction.getOpcode() == Bytecodes.Jgoto && !operandStack.isEmpty() )
			{
				gotoDest = ((BranchDestination)((JVMInstructionUnary)(nextInstruction)).getOperand()).getPc();
				System.out.println ( "gotoDest = " + gotoDest );
				nextInstruction.setExtra(new Integer(1));
			}
			
			Operand [] sourceOperands = nextInstruction.getSourceOperands();
			if ( sourceOperands != null )
			{
				for ( int index = sourceOperands.length - 1 ; index >= 0 ; index -- )
				{
					if ( sourceOperands[index] instanceof StackSlot )
					{
						 System.out.println ( index + ":" + nextInstruction.toString());
						 StackSlot backup = (StackSlot)operandStack.get(operandStack.size()-1);
						 if ( ((StackSlot)sourceOperands[index]).getProducer() == null )
							 ((StackSlot)sourceOperands[index]).setProducer(((StackSlot)operandStack.remove(operandStack.size()-1)).getProducer());
						 else
							 operandStack.remove(operandStack.size()-1);
						 
						 /* For some instructions, the stack top is not really consumed. Push back */
						 if ( nextInstruction instanceof JVMInstructionMulArgsMulRes )
							 operandStack.add( backup ); 
					}
					/* The first numArg locals are arguments, let the Producers for these be null*/
					else if ( sourceOperands[index] instanceof LocalVariable && (!lvarArray[((LocalVariable)sourceOperands[index]).getLVIndex()].isArgumentSlot()))
						 ((LocalVariable)sourceOperands[index]).setProducer( lvarArray[((LocalVariable)sourceOperands[index]).getLVIndex()].getProducer());
	
					/* Mark the source as argument, if needed */
					if ( sourceOperands[index] instanceof LocalVariable )
						if (((LocalVariable)sourceOperands[index]).getLVIndex() < methodData.getArgumentCount() )
							((LocalVariable)sourceOperands[index]).setIsArg(true);
				}
			}
			
			Operand resultOperand = nextInstruction.getResult();
			
			if ( (resultOperand != null) && (resultOperand instanceof StackSlot ))
				operandStack.add( resultOperand );
			else if ( ( resultOperand != null ) && (resultOperand instanceof LocalVariable ))
				lvarArray[((LocalVariable)resultOperand).getLVIndex()] = (LocalVariable) resultOperand;
			else if ( resultOperand == null && nextInstruction.getResults() != null )
				operandStack.add(nextInstruction.getResults()[0]); /*All results on the stack. Consider only dup for now*/	
		}
	}
	
		
	private int getPrevPC ( int pc )
	{
		int index = 0;
		while ( bytecodeGraph.get(index).getPC() != pc )
			index++;
		return bytecodeGraph.get(index-1).getPC();
	}
	
	private boolean methodReturnsVoid ( MethodMetaData methodData )
	{
		return methodData.getSignature().endsWith("V");
	}
	
	private boolean isStackOperation ( int bytecode )
	{
		switch ( bytecode )
		{
		    case Bytecodes.aload0:  case Bytecodes.aload1:   case Bytecodes.aload2:  case Bytecodes.aload3: 
		    case Bytecodes.aload:   case Bytecodes.iload0:   case Bytecodes.iload1:  case Bytecodes.iload2:
		    case Bytecodes.iload3:  case Bytecodes.iload:    case Bytecodes.fload0:  case Bytecodes.fload1:
		    case Bytecodes.fload2:  case Bytecodes.fload3:   case Bytecodes.fload:   case Bytecodes.dload0:
		    case Bytecodes.dload1:  case Bytecodes.dload2:   case Bytecodes.dload3:  case Bytecodes.dload:
		    case Bytecodes.lload0:  case Bytecodes.lload1:   case Bytecodes.lload2:  case Bytecodes.lload3:
		    case Bytecodes.iconst3: case Bytecodes.iconst4:  case Bytecodes.iconst5: case Bytecodes.iconstm1:
		    case Bytecodes.lconst0: case Bytecodes.lconst1:  case Bytecodes.dconst0: case Bytecodes.dconst1:
		    case Bytecodes.fconst0: case Bytecodes.fconst1:  case Bytecodes.fconst2: case Bytecodes.iconst1:
		    case Bytecodes.lload:   case Bytecodes.iconst2:  case Bytecodes.iconst0: case Bytecodes.aconstnull:
		    case Bytecodes.bipush:  case Bytecodes.sipush:   case Bytecodes.ldc2dw:  case Bytecodes.ldc2lw:
		    case Bytecodes.ldc:	    case Bytecodes.ldcw:	case Bytecodes.aload0getfield: 
		    	return true;
		    default: 
		    	return false;
		}
	}
	
	
	
}

