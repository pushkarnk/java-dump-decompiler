package jdd.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import com.ibm.j9ddr.debugextensions.vm24.jdd.dumpreader.Bytecodes;
import com.ibm.j9ddr.debugextensions.vm24.jdd.grammar.RandomNameGenerator;
import com.ibm.j9ddr.debugextensions.vm24.jdd.instructions.JVMInstruction;
import com.ibm.j9ddr.debugextensions.vm24.jdd.instructions.JVMInstructionBinary;
import com.ibm.j9ddr.debugextensions.vm24.jdd.instructions.JVMInstructionInvoke;
import com.ibm.j9ddr.debugextensions.vm24.jdd.instructions.JVMInstructionMulArgsMulRes;
import com.ibm.j9ddr.debugextensions.vm24.jdd.instructions.JVMInstructionTernary;
import com.ibm.j9ddr.debugextensions.vm24.jdd.instructions.JVMInstructionUnary;
import com.ibm.j9ddr.debugextensions.vm24.jdd.instructions.JVMInstructionVarArgs;
import com.ibm.j9ddr.debugextensions.vm24.jdd.instructions.UIFInstruction;
import com.ibm.j9ddr.debugextensions.vm24.jdd.operands.BranchDestination;
import com.ibm.j9ddr.debugextensions.vm24.jdd.operands.ClassRef;
import com.ibm.j9ddr.debugextensions.vm24.jdd.operands.Constant;
import com.ibm.j9ddr.debugextensions.vm24.jdd.operands.FieldRef;
import com.ibm.j9ddr.debugextensions.vm24.jdd.operands.LocalVariable;
import com.ibm.j9ddr.debugextensions.vm24.jdd.operands.MethodRef;
import com.ibm.j9ddr.debugextensions.vm24.jdd.operands.Operand;
import com.ibm.j9ddr.debugextensions.vm24.jdd.operands.StackSlot;

public class UntypedIntermediateForm {

	
	private LinkedList<UIFInstruction> uifGraph;
	private MethodSignature methodSignature;
	private MethodMetaData methodData;
	private LvarNameAndType [] lvarArray;
	
	public UntypedIntermediateForm ( )
	{
		uifGraph = new LinkedList<UIFInstruction> ( );
	}
	
	public MethodSignature getMethodSignature ( )
	{
		return methodSignature;
	}
	
	public Object[] getUIFInstructionsInArray( )
	{
	    return uifGraph.toArray();	
	}
	
	public Iterator<UIFInstruction> getIterator ( )
	{
		return uifGraph.iterator();
	}
	
	public UIFInstruction fetchUIFInstruction ( int pc )
	{
		Object [] uifArray = uifGraph.toArray();
		for ( int index = 0 ; index <= pc; index ++)
			if ( ((UIFInstruction)uifArray[index]).getPc() == pc )
				return (UIFInstruction)uifArray[index];
		return null; //not really possible
	}
	
	public void createUIFGraph ( TypedIntermediateForm TIFObject )
	{
		Iterator<JVMInstruction> bytecodeIterator = TIFObject.getBytecodeIterator();
		methodData = TIFObject.getMethodMetaData();
		methodSignature = new MethodSignature ( methodData );
		lvarArray = new LvarNameAndType[methodData.getTempCount()+methodData.getArgumentCount()];

		while ( bytecodeIterator.hasNext())
		{
			/* Iterate over all the JVMInstructions and convert them into Untyped Instructions
			 * Untyped Instructions point to sources which may be either Operand or another 
			 * Untyped Instruction. 
			 */
			Object [] source;
			Operand result;
			JVMInstruction nextInstruction = bytecodeIterator.next();
			switch( nextInstruction.getOpcode() )
			{
			    /* STORE INTO LOCAL VARIABLE */
			    case Bytecodes.istore0:
			    case Bytecodes.astore0:
			    case Bytecodes.lstore0:
			    case Bytecodes.fstore0:
			    case Bytecodes.dstore0:
			    case Bytecodes.istore1:
			    case Bytecodes.istore2:
			    case Bytecodes.istore3:
			    case Bytecodes.astore1:
			    case Bytecodes.astore2:
			    case Bytecodes.astore3:
			    case Bytecodes.lstore1:
			    case Bytecodes.lstore2:
			    case Bytecodes.lstore3:
			    case Bytecodes.fstore1:
			    case Bytecodes.fstore2:
			    case Bytecodes.fstore3:
			    case Bytecodes.dstore1:
			    case Bytecodes.dstore2:
			    case Bytecodes.dstore3:
			    case Bytecodes.astore:
			    case Bytecodes.istore:
			    case Bytecodes.fstore:
			    case Bytecodes.dstore:
			    case Bytecodes.lstore:
			    	source = new Object [1];
			    	source[0] = ((StackSlot)((JVMInstructionUnary)nextInstruction).getOperand()).getProducer();
			    	source[0] = reachActualSource ( source[0]);
			    	result = ((JVMInstructionUnary)nextInstruction).getResult();
			    	uifGraph.add ( new UIFInstruction ( source, result, UIFInstruction.STORE, nextInstruction.getOpcode(), nextInstruction.getPC()));
			    	break;
			    /*ARITHMETIC & BITWISE*/
			    case Bytecodes.iadd:
			    case Bytecodes.dadd:
			    case Bytecodes.fadd:
			    case Bytecodes.ladd:
			    case Bytecodes.isub:
			    case Bytecodes.lsub:
			    case Bytecodes.dsub:
			    case Bytecodes.fsub:
			    case Bytecodes.imul:  
			    case Bytecodes.fmul:
			    case Bytecodes.dmul:
			    case Bytecodes.lmul:
			    case Bytecodes.irem:
			    case Bytecodes.lrem:
			    case Bytecodes.frem:
			    case Bytecodes.drem:
			    case Bytecodes.idiv:
			    case Bytecodes.ldiv:
			    case Bytecodes.ddiv:
			    case Bytecodes.fdiv:
			    case Bytecodes.ixor:
			    case Bytecodes.lxor:
			    case Bytecodes.lor:
			    case Bytecodes.ior:
			    case Bytecodes.iand:
			    case Bytecodes.land:
			    case Bytecodes.ishr:
			    case Bytecodes.ishl:
			    case Bytecodes.lshl:
			    case Bytecodes.lshr:
			    case Bytecodes.iushr:
			    case Bytecodes.lushr:
			    	source = new Object[2];
			    	source[0] = ((StackSlot)((JVMInstructionBinary)nextInstruction).getOperand1()).getProducer();
			    	source[0] = reachActualSource ( source[0]);
			    	source[1] = ((StackSlot)((JVMInstructionBinary)nextInstruction).getOperand2()).getProducer();
			    	source[1] = reachActualSource ( source[1]);
			    	result = ((JVMInstructionBinary)nextInstruction).getResult();
			    	uifGraph.add ( new UIFInstruction ( source, result, getUIFInstructionType(nextInstruction.getOpcode()), nextInstruction.getOpcode(),nextInstruction.getPC()));
			    	break;
			    case Bytecodes.ineg:
			    case Bytecodes.lneg:
			    case Bytecodes.fneg:
			    case Bytecodes.dneg:
			    	source = new Object [1];
			    	source[0] = ((StackSlot)((JVMInstructionUnary)nextInstruction).getOperand()).getProducer();
			    	source[0] = reachActualSource(source[0]);
			    	result = ((JVMInstructionUnary)nextInstruction).getResult();
			    	uifGraph.add ( new UIFInstruction ( source, result, UIFInstruction.NEG, nextInstruction.getOpcode(),nextInstruction.getPC()));
			    	break;
			    case Bytecodes.iinc: /* no stack top in picture */
			    	source = new Object[2];
			    	source[0] = ((JVMInstructionBinary)nextInstruction).getOperand1();
			    	source[1] = ((JVMInstructionBinary)nextInstruction).getOperand2();
			    	result = ((JVMInstructionBinary)nextInstruction).getResult();
			    	uifGraph.add ( new UIFInstruction ( source, result, getUIFInstructionType(nextInstruction.getOpcode()), nextInstruction.getOpcode(), nextInstruction.getPC()));
			        break;
			    /*RETURN*/
			    case Bytecodes.return0:
			    case Bytecodes.return1:
			    case Bytecodes.return2:
			    case Bytecodes.returnFromConstructor:
			    case Bytecodes.genericReturn:
			    case Bytecodes.syncReturn0:
			    case Bytecodes.syncReturn1:
			    case Bytecodes.syncReturn2:
			    	if ( methodReturnsVoid ( methodData ))
			    	{
			    		uifGraph.add ( new UIFInstruction ( null, null, UIFInstruction.RETURN,nextInstruction.getOpcode(), nextInstruction.getPC()));
			    		break;
			    	}
			    	source = new Object [2];
			    	source[1] = new Integer ( methodReturnsBoolean ( methodData ) );
			    	source[0] = ((StackSlot)((JVMInstructionUnary)nextInstruction).getOperand()).getProducer();
			    	source[0] = reachActualSource ( source[0]);
			    	result = ((JVMInstructionUnary)nextInstruction).getResult();
			    	uifGraph.add ( new UIFInstruction ( source, result, UIFInstruction.RETURN,nextInstruction.getOpcode(), nextInstruction.getPC()));
			    	break;	
			    /*INVOCATIONS*/
			    case Bytecodes.invokeinterface:
			    case Bytecodes.invokespecial:
			    case Bytecodes.invokestatic:
			    case Bytecodes.invokevirtual:
			    	MethodRef mref = (MethodRef)((JVMInstructionInvoke)nextInstruction).getMethodReference();
			    	int numArgs = ((JVMInstructionInvoke)nextInstruction).getSourceOperands().length;
			    	source = new Object[numArgs+1];
			    	for ( int i = 0 ; i < numArgs; i++ )
			    	{
			    		source[i] = ((StackSlot)(((JVMInstructionInvoke)nextInstruction).getSourceOperands()[i])).getProducer();
			    		source[i] = reachActualSource ( source[i]);
			    	}
			    	source[numArgs] = mref;
			    	result = ((JVMInstructionInvoke)nextInstruction).getResult();
			    	if( mref.getName().equals("<init>"))
			    	{
			    		if ( !((source[0] instanceof UIFInstruction) && (((UIFInstruction)source[0])).getInstructionType() == UIFInstruction.OBJCREATE ))
			    		{
			    			//probably the "super" call!
			    			((MethodRef)source[numArgs]).setName("super");
			    		}
			    	 	uifGraph.add( new UIFInstruction (source, result, UIFInstruction.CONSTRUCT, nextInstruction.getOpcode(), nextInstruction.getPC()));
			    	}
			    	else
			    	{
			    		if ( isSuperCall( source[0], mref))
			    			mref.setSuperCall();
			    		uifGraph.add( new UIFInstruction (source, result, UIFInstruction.INVOKE, nextInstruction.getOpcode(), nextInstruction.getPC()));
			    	}
			    	break;
			   /*OBJECT & CLASS OPERATIONS*/
			   case Bytecodes.putfield:
				   source = new Object[2];
				   source[0] = ((StackSlot)((JVMInstructionTernary)nextInstruction).getOperand2()).getProducer();
			       source[0] = reachActualSource ( source[0] );
				   source[1] = ((StackSlot)((JVMInstructionTernary)nextInstruction).getOperand3()).getProducer();
			       source[1] = reachActualSource ( source[1] );
			       result = ((JVMInstructionTernary)nextInstruction).getOperand1();
			       uifGraph.add( new UIFInstruction ( source, result, UIFInstruction.WRFIELD, nextInstruction.getOpcode(), nextInstruction.getPC()));
			       break;
			   case Bytecodes.putstatic:
				   source = new Object[1];
				   source[0] = ((StackSlot)((JVMInstructionBinary)nextInstruction).getOperand2()).getProducer();
			       source[0] = reachActualSource(source[0]);
			       result = ((JVMInstructionBinary)nextInstruction).getOperand1();
			       uifGraph.add( new UIFInstruction ( source, result, UIFInstruction.WRFIELD, nextInstruction.getOpcode(), nextInstruction.getPC()));
			       break;
			   case Bytecodes.getfield:
				   source = new Object[2];
				   source[0] = ((StackSlot)((JVMInstructionBinary)nextInstruction).getOperand2()).getProducer();
			       source[0] = reachActualSource ( source[0]);
			       source[1] = ((JVMInstructionBinary)nextInstruction).getOperand1();
			       result = ((JVMInstructionBinary)nextInstruction).getResult();
			       uifGraph.add( new UIFInstruction ( source, result, UIFInstruction.RDFIELD, nextInstruction.getOpcode(), nextInstruction.getPC()));
				   break;
			   case Bytecodes.getstatic:
				   source = new Object[1];
				   source[0] = ((JVMInstructionUnary)nextInstruction).getOperand(); 
			       result = ((JVMInstructionUnary)nextInstruction).getResult();
			       uifGraph.add( new UIFInstruction ( source, result, UIFInstruction.RDFIELD, nextInstruction.getOpcode(), nextInstruction.getPC()));
			       break;
			   case Bytecodes.Jnew:
				   source = new Object[1];
				   source[0] = ((JVMInstructionUnary)nextInstruction).getOperand();
				   result = ((JVMInstructionUnary)nextInstruction).getResult();
				   uifGraph.add( new UIFInstruction ( source, result, UIFInstruction.OBJCREATE, nextInstruction.getOpcode(), nextInstruction.getPC()));
				   break;
			   case Bytecodes.newarray:
			   case Bytecodes.anewarray:
				   source = new Object[2];
				   source[0] = ((JVMInstructionBinary)nextInstruction).getOperand1(); //A constant integer OR a ClassRef
				   source[1] = ((StackSlot)((JVMInstructionBinary)nextInstruction).getOperand2()).getProducer();
				   source[1] = reachActualSource ( source[1] );
				   result = ((JVMInstructionBinary)nextInstruction).getResult();
				   uifGraph.add( new UIFInstruction ( source, result, UIFInstruction.ARRCREATE, nextInstruction.getOpcode(), nextInstruction.getPC()));
				   break;
			   case Bytecodes.multianewarray:
				   int dimensions = ((JVMInstructionVarArgs)nextInstruction).getDimensions();
				   source = new Object[dimensions+1];
				   source[0] = ((JVMInstructionVarArgs)nextInstruction).getClassRef();
				   StackSlot [] dimensionVals = (StackSlot[])((JVMInstructionVarArgs)nextInstruction).getSourceOperands();
				   for ( int i = 0 ; i < dimensions; i++ )
				   {
				       source[i+1] = dimensionVals[i].getProducer();
				       source[i+1] = reachActualSource(source[i+1]);
				   }
				   result = ((JVMInstructionVarArgs)nextInstruction).getResult();
				   uifGraph.add( new UIFInstruction ( source, result, UIFInstruction.ARRCREATE, nextInstruction.getOpcode(), nextInstruction.getPC()));
				   break;
			   case Bytecodes.aaload:
			   case Bytecodes.baload:
			   case Bytecodes.caload:
			   case Bytecodes.daload:
			   case Bytecodes.faload:
			   case Bytecodes.iaload:
			   case Bytecodes.laload:
			   case Bytecodes.saload:
				   source = new Object[2];
				   source[0] = ((StackSlot)((JVMInstructionBinary)nextInstruction).getOperand1()).getProducer();
				   source[0] = reachActualSource ( source[0] );
				   source[1] = ((StackSlot)((JVMInstructionBinary)nextInstruction).getOperand2()).getProducer();
				   source[1] = reachActualSource ( source[1] );
				   result = ((JVMInstructionBinary)nextInstruction).getResult();
				   uifGraph.add( new UIFInstruction ( source, result, UIFInstruction.RDARRAY, nextInstruction.getOpcode(), nextInstruction.getPC()));
				   break;
			   case Bytecodes.sastore:
			   case Bytecodes.bastore:
			   case Bytecodes.lastore:
			   case Bytecodes.aastore:
			   case Bytecodes.dastore:
			   case Bytecodes.fastore:
			   case Bytecodes.castore:
			   case Bytecodes.iastore:
				   source = new Object[3];
			       source[0] = ((StackSlot)((JVMInstructionTernary)nextInstruction).getOperand1()).getProducer();
			       source[0] = reachActualSource ( source[0] );
			       source[1] = ((StackSlot)((JVMInstructionTernary)nextInstruction).getOperand2()).getProducer();
			       source[1] = reachActualSource ( source[1] );
			       source[2] = ((StackSlot)((JVMInstructionTernary)nextInstruction).getOperand3()).getProducer();
			       source[2] = reachActualSource ( source[2]);
			       result = null; //sources 1 and 2 themselves indicate the result operand. Adjust!
			       uifGraph.add( new UIFInstruction ( source, result, UIFInstruction.WRARRAY, nextInstruction.getOpcode(), nextInstruction.getPC()));
                   break;
			   case Bytecodes.arraylength:
				   source = new Object[1];
				   source[0] = ((StackSlot)((JVMInstructionUnary)nextInstruction).getOperand()).getProducer();
				   source[0] = reachActualSource ( source[0]);
				   result = ((JVMInstructionUnary)nextInstruction).getResult();
				   uifGraph.add( new UIFInstruction ( source, result, UIFInstruction.ARRLENGTH, nextInstruction.getOpcode(), nextInstruction.getPC()));
				   break;
			   /*TYPECASTS*/
			   case Bytecodes.d2f:
			   case Bytecodes.i2f:
			   case Bytecodes.l2f:
			   case Bytecodes.d2i:
			   case Bytecodes.f2i:	   
			   case Bytecodes.l2i:
			   case Bytecodes.i2d:
			   case Bytecodes.f2d:
			   case Bytecodes.l2d:  
			   case Bytecodes.d2l:
			   case Bytecodes.f2l:
			   case Bytecodes.i2l:
			   case Bytecodes.i2b:
			   case Bytecodes.i2c:
			   case Bytecodes.i2s:
				   source = new Object[2];
				   source[0] = ((StackSlot)((JVMInstructionUnary)nextInstruction).getOperand()).getProducer();
				   source[0] = reachActualSource ( source[0]);
				   source[1] = typeCastTo ( nextInstruction.getOpcode());
				   result = ((JVMInstructionUnary)nextInstruction).getResult();
				   uifGraph.add( new UIFInstruction ( source, result, UIFInstruction.TYPECAST, nextInstruction.getOpcode(), nextInstruction.getPC()));
				   break;
			   case Bytecodes.checkcast:
				   source = new Object[2];
				   source[0] = ((JVMInstructionBinary)nextInstruction).getOperand1(); //classref
				   source[1] = ((StackSlot)(((JVMInstructionBinary)nextInstruction).getOperand2())).getProducer();
				   source[1] = reachActualSource ( source[1]);
				   result = ((JVMInstructionBinary)nextInstruction).getResult();	   									
				   uifGraph.add( new UIFInstruction ( source, result, UIFInstruction.TYPECAST, nextInstruction.getOpcode(), nextInstruction.getPC()));
				   break;
			   case Bytecodes.Jinstanceof:
				   source = new Object[2];
				   source[0] = ((JVMInstructionBinary)nextInstruction).getOperand1(); //classref
				   source[1] = ((StackSlot)(((JVMInstructionBinary)nextInstruction).getOperand2())).getProducer();
				   source[1] = reachActualSource ( source[1]);
				   result = ((JVMInstructionBinary)nextInstruction).getResult();
				   uifGraph.add( new UIFInstruction ( source, result, UIFInstruction.INSTANCEOF, nextInstruction.getOpcode(), nextInstruction.getPC()));
				   break;
			   
			   case Bytecodes.ifeq:
			   case Bytecodes.ifge:
			   case Bytecodes.ifgt:
			   case Bytecodes.ifle:
			   case Bytecodes.iflt:
			   case Bytecodes.ifne:
			   case Bytecodes.ifnull:
			   case Bytecodes.ifnonnull:
				   source = new Object[7];
				   source[6] = null;
				   source[5] = null;
				   source[4] = null;
				   if ( nextInstruction.getOpcode() == Bytecodes.ifnull || nextInstruction.getOpcode() == Bytecodes.ifnonnull) 
					   source[3] = new Constant(null);
				   else
					   source[3] = new Constant(0);
				   source[0] = new Integer ( getIfType (nextInstruction.getOpcode())); 
				   source[2] = ((JVMInstructionBinary)nextInstruction).getOperand1(); //branch destination
				   source[1] = ((StackSlot)(((JVMInstructionBinary)nextInstruction).getOperand2())).getProducer(); //operand under comparison
				   source[1] = reachActualSource (source[1]);
				   uifGraph.add( new UIFInstruction ( source, null, UIFInstruction.IF, nextInstruction.getOpcode(), nextInstruction.getPC()));
				   break;
				   
			   case Bytecodes.dcmpg:
			   case Bytecodes.dcmpl:
			   case Bytecodes.fcmpg:
			   case Bytecodes.fcmpl:
			   case Bytecodes.lcmp:
				   	source = new Object[2];
			    	source[0] = ((StackSlot)((JVMInstructionBinary)nextInstruction).getOperand1()).getProducer();
			    	source[0] = reachActualSource ( source[0]);
			    	source[1] = ((StackSlot)((JVMInstructionBinary)nextInstruction).getOperand2()).getProducer();
			    	source[1] = reachActualSource ( source[1]);
			    	result = ((JVMInstructionBinary)nextInstruction).getResult();
			    	uifGraph.add ( new UIFInstruction ( source, result, UIFInstruction.COMPARE, nextInstruction.getOpcode(),nextInstruction.getPC()));
				    break;
				    
			   case Bytecodes.ifacmpeq:
			   case Bytecodes.ifacmpne:
			   case Bytecodes.ificmpeq:
			   case Bytecodes.ificmpge:
			   case Bytecodes.ificmpgt:
			   case Bytecodes.ificmple:
			   case Bytecodes.ificmplt:
			   case Bytecodes.ificmpne:
				   source = new Object[7];
				   source[6] = null;
				   source[5] = null;
				   source[4] = null;
				   source[3] = ((StackSlot)(((JVMInstructionTernary)nextInstruction).getOperand3())).getProducer(); //operand under comparison
				   source[3] = reachActualSource ( source[3]);
				   source[0] = new Integer ( getIfType (nextInstruction.getOpcode())); 
				   source[2] = ((JVMInstructionTernary)nextInstruction).getOperand1(); //branch destination
				   source[1] = ((StackSlot)(((JVMInstructionTernary)nextInstruction).getOperand2())).getProducer(); //operand under comparison
				   source[1] = reachActualSource ( source[1]);
				   uifGraph.add( new UIFInstruction ( source, null, UIFInstruction.IF, nextInstruction.getOpcode(), nextInstruction.getPC()));
				   break; 
			   
			   case Bytecodes.Jgoto:
				   source = new Object[3];
				   source[0] = ((JVMInstructionUnary)nextInstruction).getOperand();
				   if ( ((BranchDestination)(((JVMInstructionUnary)nextInstruction).getOperand())).getPc() > nextInstruction.getPC() )
					   source[1] = new Integer ( 1 ); //forward jump
				   else
					   source[1] = new Integer ( 0 ); //backward jump
				   source[2] = nextInstruction.getExtra();
				   uifGraph.add( new UIFInstruction ( source, null, UIFInstruction.GOTO, nextInstruction.getOpcode(), nextInstruction.getPC()));
				   break;
			}
		}
		/* Links to JVMInstruction objects must be translated to UIFInstruction links*/
		convertJVMInstrLinktoUIFInstrLink ( );
		/* This is critical to if-else-if-else statement construction */
		combineSuccessiveIFStatements ( );
		/* Recognize ternary operator expressions */
		detectTernaryOperatorExpressions ( TIFObject );
		/* Identify pre-increment/pre-decrement operators in conditions*/
		detectPreIncPreDecOperatorsInComparisons ( );
		/* Naming and typing the local variables */
		evaluateLocalVariableTypes ( );
		
	}

	
	private void detectPreIncPreDecOperatorsInComparisons ( )
	{
		Object [] uifInstrs = uifGraph.toArray();
		int index = 0;
	    UIFInstruction replacedInstruction = null;
		while ( index < uifInstrs.length )
		{
			UIFInstruction currInstr = (UIFInstruction)(uifInstrs[index]);
			Object [] currSources = currInstr.getSources();
		    
			for ( int srcIdx = 0 ; currSources != null && srcIdx < currSources.length; srcIdx++ )
			{	
				replacedInstruction = null;
			
			    if ( currSources[srcIdx] instanceof LocalVariable )
				{
					int lvarIndex = ((LocalVariable)(currSources[srcIdx])).getLVIndex();
					int backTrackIndex = index - 1;
			
					while ( backTrackIndex >= 0 )
					{
						UIFInstruction prevInstruction = (UIFInstruction)(uifInstrs[backTrackIndex]);
						if ( prevInstruction.getInstructionType() == UIFInstruction.INC)
						{
							int prevLvarIndex = ((LocalVariable)(prevInstruction.getSources()[0])).getLVIndex();
							int incFactor = ((Integer)(((Constant)(prevInstruction.getSources()[1])).getConstantObj())).intValue();
							if ( prevLvarIndex == lvarIndex && (incFactor == 1 || incFactor == -1 ))
							{
								currInstr.getSources()[srcIdx] = prevInstruction;
								replacedInstruction = prevInstruction;
								break;
							}
						}
						/* Don't cross block boundaries */
						/* TODO : Consider try-catch blocks and switch case blocks in future */
						else if ( prevInstruction.getInstructionType() == UIFInstruction.IF 
								|| prevInstruction.getInstructionType( ) == UIFInstruction.GOTO 
								 ||  prevInstruction.getInstructionType() == UIFInstruction.RETURN )
							break; 
						else //check if the local is being used anywhere after the inc, if yes, stop!
						{
							Object [] pSources = prevInstruction.getSources();
							for ( int i = 0 ; i < pSources.length; i++ )
							{
								if ( pSources[i] instanceof LocalVariable 
										&& ((LocalVariable)pSources[i]).getLVIndex() == lvarIndex )
									break;
								if ( replacedInstruction != null && pSources[i] instanceof UIFInstruction 
										&& ((UIFInstruction)(pSources[i])).getPc() == replacedInstruction.getPc())
									break;
							}
						}
						backTrackIndex --;
					}
				}
			}
			index++;
		}
	}
	
	
	private void detectTernaryOperatorExpressions ( TypedIntermediateForm TIFObject )
	{
		/* We search for IF statements which are not orphans.
		 * We then look into the TIF Graph and deduce the expressions resulting on truth and falsity.
		 * Thereafter, we tag these UIF Instructions as TERNARY_OP.
		 */ 
		Object [] uifInstrs = uifGraph.toArray();
		int index = 0;
		while ( index < uifInstrs.length )
		{
			UIFInstruction currInstr = (UIFInstruction)(uifInstrs[index]);
			if ( currInstr.getInstructionType() == UIFInstruction.IF )
				if ( isOrphan(currInstr) == false && currInstr.getSources()[4] != null )
					createTernaryOperatorInstruction ( TIFObject, index );
			index++;
		}
	}
	
	private void createTernaryOperatorInstruction ( TypedIntermediateForm TIFObject, int index )
	{
		/*	Approach
		 *	---------
		 *		This method receives IF-heads. For a given IF-head, we make an assumption that it harbors
		 *	a TERNARY_OP. We then evaluate the truth and falsity sections. They may either hold a sequence
		 *	of UIFs with one of them being the parent, or they may hold an Operand which is either a LocalVariable
		 *	or a Constant.
		 *   	First begin with the limits of the truth-section which are found at offsets 5 & 6 in the sources
		 *   of an IF-head. If the limits match, then it is likely that they correspond to a GOTO or a RETURN.
		 *   In that case truth is an Operand, which is loaded just at the previous TIFInstruction. Fetch this guy
		 *   from the TIFInstruction. If not, truth will point to a valid UIFInstruction. Now, to get the falsity
		 *   section jump to one UIFStatement after the end of the truth section. If this UIFInstruction is a GOTO
		 *   or a RETURN, falsity is again an Operand found one TIFInstruction before the current. Else, it is a valid
		 *   UIFInstruction. This case is trivial. In the case where the truth-section limits are not the same, we extend 
		 *   the same logic.
		 */
		UIFInstruction ifHead = uifGraph.get( index );
		
		Integer truthStart = (Integer)(ifHead.getSources()[5]);
		Integer truthEnd   = (Integer)(ifHead.getSources()[6]);
		Object truthSection, falseSection;
	
		int falsity = getNextPc ( truthEnd );
		
		/*True*/
		if ( getUIFInstructionFromPC ( truthStart ).getInstructionType() == UIFInstruction.GOTO 
				|| getUIFInstructionFromPC ( truthStart ).getInstructionType() == UIFInstruction.RETURN )
		{
			Operand op = ((JVMInstructionUnary)(TIFObject.getPrevJVMInstructionFromPC(truthStart.intValue()))).getOperand();
			truthSection = ifHead.getSources()[5] = op;
		}
		else
			truthSection = ifHead.getSources()[5] = getUIFInstructionFromPC ( truthStart );
		
		/*False*/
		if ( (getUIFInstructionFromPC(falsity) == getParent(ifHead)) || getUIFInstructionFromPC ( falsity ).getInstructionType() == UIFInstruction.GOTO 
				|| getUIFInstructionFromPC ( falsity ).getInstructionType() == UIFInstruction.RETURN )
		{
			Operand op = ((JVMInstructionUnary)(TIFObject.getNextJVMInstructionFromPC(getPrevPc(falsity)))).getOperand();
			falseSection = ifHead.getSources()[6] = op;
		}
		else
			falseSection = ifHead.getSources()[6] = getUIFInstructionFromPC ( falsity );
		
		if ( truthSection instanceof Constant && falseSection instanceof Constant )
			if ( ((Constant)truthSection).getConstantObj() instanceof Integer && ((Constant)falseSection).getConstantObj() instanceof Integer ) 
				if ( ((Integer)(((Constant)truthSection).getConstantObj())).intValue() == 1 && ((Integer)(((Constant)falseSection).getConstantObj())).intValue() == 0 )
					if ( methodReturnsBoolean(methodData) == 1 )
				{
					ifHead.getSources()[5] = truthStart;
					ifHead.getSources()[6] = truthEnd;
					return;
				}
		ifHead.setInstructionType(UIFInstruction.TERNARY_OP);
		
	}

	
		
	

	
	private void combineSuccessiveIFStatements ( )
	{
		/* We do this:
		 * 1. Make a table of truth and falsity branch values for each uif-IF
		 * 
		 * 2. Ensure that:
		 *    Constraint 1: The overall truth-branch value is always in the truth column
		 *    Constraint 2: The overall false-branch value is always in the falsity column
		 *    Constraint 3: All occurrences of an intermediate branch target fall in the same column
		 *    
		 *    More about constraint 3
		 *    ------------------------
		 *    Eg. #		T		F
		 *        1		9		5
		 *        5		13		9
		 *    This means:
		 *    (1 || 5) --false--> 9
		 *    and
		 *    1 --true--> 9
		 *    which is possible if we have the expression ( 1 || 5 || 9 )
		 *    But both of the above can't hold true at the same time. We hence reverse the 1st expression.
		 *    
		 *    #		T		F
		 *    1		5		9
		 *    5		13		9
		 *    means:
		 *    1 --false--> 9
		 *    1 && 5 --false--> 9
		 *    Both of these hold good together.
		 *    
		 *    While applying Constraint 3, start from the bottom of the list. With this, the ORs will remain ORs and
		 *    reverse AND's will get reverse back. This theory is under test.
		 *    
		 * 3. Once the truth-falsity tables are ready, attempt to move from uif-IF #1 to uif-IF #N
		 * 
		 *    On each uif-IF:
		 *     - If we are reaching this IF falling on a false branch, OR it in
		 *     - If we are reaching this IF falling on a true branch, AND it in
		 *     - If there are alternate paths coming to this IF, introduce a  ')' before it.
		 *       Introduce the '(' just before the root of this alternate path
		 */
		
		/*Start*/
		Object [] uifGraphArray = uifGraph.toArray();
		int low = -1, high = -1; //d101
		int index = 0, currPc, truePC, falsePC;
		int pc1, pc2;
		UIFInstruction prevHead = null; //see use
		ArrayList<UIFInstruction> successiveIFs = null;
		while ( index < uifGraphArray.length )
		{
			successiveIFs = new ArrayList<UIFInstruction>( );
			UIFInstruction currInstr = ((UIFInstruction)(uifGraphArray[index]));
			
			if (currInstr.getInstructionType() == UIFInstruction.IF )
			{
				/* Gather all adjacent IFs */
				currPc = currInstr.getPc();
				successiveIFs.add(currInstr);
				pc1 = ((BranchDestination)(currInstr.getSources()[2])).getPc();
				pc2 = getNextPc(currInstr.getPc());
				low = pc1 < pc2 ? pc1 : pc2;
				high = pc2 > pc1 ? pc2 : pc1;
				
				do {
					currPc = getNextPc(currPc);
					UIFInstruction nextInstr = getUIFInstructionFromPC ( currPc );
					index++;
					/*Defect d101: Sometimes an adjacent IF may lie in the true-block or false block of a given IF*/	
					if ( notOtherStatement (nextInstr) && nextInstr.getInstructionType() == UIFInstruction.IF)
					{
						/*d101 begin*/
						pc1 = ((BranchDestination)(nextInstr.getSources()[2])).getPc();
						pc2 = getNextPc(nextInstr.getPc());
						if ( inRange( pc1, pc2, low, high ))
						{
							//index--;
							break;
						}
						successiveIFs.add(nextInstr);
						low = pc1 < pc2 ? pc1 : pc2;
						high = pc2 > pc1 ? pc2 : pc1;
					} 
					else if (reachableFromNextIf(currPc))
					{
						System.out.println ( "Yes");
						continue;
					}
					else
						break;
				}while (true);
			}
			else
			{
				index++;
				continue;
			}
		    System.out.println ( "if - size = " + successiveIFs.size());
			truePC = getNextPc(((UIFInstruction)(successiveIFs.get(successiveIFs.size()-1))).getPc());
			falsePC = -1;
		
			
			/*Construct truth/falsity tables */
			int size = successiveIFs.size();
			int [] ifPCs = new int [size];
			int [] truePCs = new int [size];
			int [] falsePCs = new int [size];
			int [] compOps = new int [size];
			
			Iterator<UIFInstruction> ifIter = successiveIFs.iterator();
			int idx = 0;
			while ( ifIter.hasNext())
			{
				UIFInstruction ifInstr = (UIFInstruction)ifIter.next();
				ifPCs[idx] = ifInstr.getPc();
				truePCs[idx] = ((BranchDestination)(ifInstr.getSources()[2])).getPc();
				if ( truePCs[idx] > truePC )
					falsePC = truePCs[idx];
				falsePCs[idx] = getNextPc ( ifPCs[idx] );
				if ( falsePCs[idx] > truePC )
					falsePC = falsePCs[idx];
				compOps[idx] = ((Integer)(ifInstr.getSources()[0])).intValue();
				idx++;
			}
			//debug start
			for ( int i = 0 ; i < idx; i++ )
				System.out.println ( ifPCs[i]+"\t" + truePCs[i] + "\t" + falsePCs[i] + "\t" + compOps[i]);
			System.out.println( "true = " + truePC );
			System.out.println( "false = " + falsePC );
			//debug end
			
			
			/* Apply Constraint 1 */
			for ( int i = 0 ; i < size; i++ )
			{
				if ( falsePCs[i] == truePC )
				{
					int temp = truePCs[i];
					truePCs[i] = falsePCs[i];
					falsePCs[i] = temp;
					compOps[i] = Math.abs( compOps[i]-5);
				}
			}
			
			/* Apply Constraint 2 */
			if ( falsePC != -1 )
			{	
				for ( int i = 0 ; i < size; i++ )
				{
					if ( truePCs[i] == falsePC )
					{
						int temp = truePCs[i];
						truePCs[i] = falsePCs[i];
						falsePCs[i] = temp;
						compOps[i] = Math.abs( compOps[i]-5);
					}
				}
			}
			
			/* Apply Constraint 3 to Truth table*/
			for ( int i = 0 ; i < size ; i ++ )
			{
				int tPC = truePCs[i];
				for ( int j = i + 1 ; j < size ; j++ )
				{
					if ( falsePCs[j] == tPC )
					{
						if ( truePCs[i] == truePC )
							continue;
						truePCs[i] = falsePCs[i];
						falsePCs[i] = tPC;
						compOps[i] = Math.abs( compOps[i]-5);
					}
				}
			}
			
			/* Apply Constraint 3 to False table */
			for ( int i = 0 ; i < size ; i ++ )
			{
				int fPC = falsePCs[i];
				for ( int j = i + 1 ; j < size ; j++ )
				{
					if ( truePCs[j] == fPC )
					{
						if ( falsePCs[i] == falsePC )
							continue;
						falsePCs[i] = truePCs[i];
						truePCs[i] = fPC;
						compOps[i] = Math.abs( compOps[i]-5);
					}
				}
			}
			
			System.out.println ( "After applying constraints");
			for ( int i = 0 ; i < idx; i++ )
				System.out.println ( ifPCs[i]+"\t" + truePCs[i] + "\t" + falsePCs[i] + "\t" + compOps[i]);
			System.out.println( "true = " + truePC );
			System.out.println( "false = " + falsePC );
			
			/* Construct the raw expression */
			
			/* These will have to be exported to the CodeGenerator */
			final int AND  = -1;
			final int OR   = -2;
			final int OB   = -3;
			final int CB   = -4;
			
			
			int pc = ifPCs[0];
			int pcidx = 1;
			ArrayList<Integer> rawExpression = new ArrayList<Integer> ();
			rawExpression.add(new Integer(pc));
			while ( pcidx < size )
			{
				if ( ifPCs[pcidx] == truePCs[pcidx-1] || linkExists(ifPCs[pcidx], truePCs[pcidx-1]) )
				{
					if ( alternatePathExists (  pcidx, ifPCs, truePCs, falsePCs ))
					{
						rawExpression.add( new Integer(CB));
						int root = getRootOfAlternatePath ( pcidx, ifPCs, truePCs, falsePCs );
						int rootIdx = rawExpression.indexOf(new Integer(root));
						rawExpression.add(rootIdx, new Integer ( OB));
						
					}
					rawExpression.add(new Integer(AND));
					rawExpression.add(new Integer(ifPCs[pcidx]));
				}
				
				else if ( ifPCs[pcidx] == falsePCs[pcidx-1] || linkExists(ifPCs[pcidx], falsePCs[pcidx-1]))
				{
					if ( alternatePathExists (  pcidx, ifPCs, truePCs, falsePCs ))
					{
						rawExpression.add( new Integer(CB));
						int root = getRootOfAlternatePath ( pcidx, ifPCs, truePCs, falsePCs );
						int rootIdx = rawExpression.indexOf(new Integer(root));
						rawExpression.add(rootIdx , new Integer ( OB));
						
					}
					rawExpression.add(new Integer(OR));
					rawExpression.add(new Integer(ifPCs[pcidx]));
				}
				
				pcidx++;
					
			}
			
			/* Update the UIFInstructions with the reversed comparison operators*/
			for ( int  i  = 0 ; i < size; i++ )
			{
				int compOp = ((Integer)(successiveIFs.get(i).getSources()[0])).intValue();
				if ( compOp != compOps[i] )
					successiveIFs.get(i).getSources()[0] = new Integer ( compOps[i] );
				
			}
			
			
			/* If any UIF-Instruction ahead is pointing back to one of the IFs from an operand
			 * reconnect it to the first IF-UIFInstruction.
			 */
			for ( int  i  = 1 ; i < size; i++ )
			{
				int currPC = successiveIFs.get(i).getPc();
				UIFInstruction currIfInstr = getUIFInstructionFromPC(currPC);
				int currIdx = this.pc2aidx(pc);
				for ( int j = currIdx+1; j < uifGraph.size(); j++)
				{
					UIFInstruction currentInstr = uifGraph.get(j);
					Object [] currentSources = currentInstr.getSources();
					for ( int k = 0 ; k < currentSources.length ; k ++)
						if (currentSources[k] == currIfInstr )
							currentSources[k] = successiveIFs.get(0);	
				}
			}
			
			/*Can we merge this with the previous IF-head ? */
			if ( prevHead != null )
			{
					if ( ((Integer)(prevHead.getSources()[5])).intValue() == successiveIFs.get(0).getPc()
							&& ((Integer)(prevHead.getSources()[6])).intValue() == getPrevPc(falsePC) )
					{
						ArrayList<Integer> prevRawExpression = (ArrayList<Integer>)prevHead.getSources()[4];
						prevRawExpression.add(new Integer(-1));
						prevRawExpression.add(new Integer(-3));
						prevRawExpression.addAll(rawExpression);
						prevRawExpression.add(new Integer(-4));
						prevHead.getSources()[4] = prevRawExpression;
						/* Shift the start pc of the prev to that of the current */
						prevHead.getSources()[5] = truePC;
						//debug
						System.out.println ( "Merged with former HEAD.");
						Object [] rawExpr = prevRawExpression.toArray();
						System.out.println ( "Raw Expression" );
						for ( int i = 0 ; i < rawExpr.length ; i ++ )
							System.out.print ( ((Integer)(rawExpr[i])).intValue()  + " ");
						System.out.println ( );
						//debug
						continue;
					}
			
			}
			/*Cache the raw expression in the first IF-UIFInstruction */
			successiveIFs.get(0).getSources()[4] = rawExpression;
			successiveIFs.get(0).getSources()[5] = new Integer ( truePC );
			successiveIFs.get(0).getSources()[6] = new Integer ( getPrevPc(falsePC) );
			prevHead = successiveIFs.get(0);
			successiveIFs = null;
			
			//debug starts
			Object [] rawExpr = rawExpression.toArray();
			System.out.println ( "Raw Expression" );
			for ( int i = 0 ; i < rawExpr.length ; i ++ )
				System.out.print ( ((Integer)(rawExpr[i])).intValue()  + " ");
			System.out.println ( );
			//debug ends
		}
	}
	
	private boolean alternatePathExists ( int pcidx, int [] ifPCs, int [] truePCs, int [] falsePCs )
	{
		int idx = pcidx - 2;
		
		while ( idx >= 0 )
		{
			if ( truePCs[idx] == ifPCs[pcidx])
				return true;
			if ( falsePCs[idx] == ifPCs[pcidx])
				return true;
			idx --;
		}
		return false;
	}
	
	boolean inRange ( int pc1, int pc2, int low, int high )
	{
		System.out.println ( "inRange : " + pc1 + " " + pc2 + " " + low + " " + high );
		int hpc = pc1 > pc2 ? pc1 : pc2;
		int lpc = pc1 < pc2 ? pc1 : pc2;
		if ( lpc > low && hpc < high )
			return true;
		return false;
	}
	
	private int getRootOfAlternatePath ( int pcidx, int [] ifPCs, int [] truePCs, int [] falsePCs )
	{
		int idx = pcidx - 2;
		int searchPC = ifPCs[pcidx];
		
		while ( idx >= 0 )
		{
		    if ( truePCs[idx] == searchPC || falsePCs[idx] == searchPC )
				searchPC = ifPCs[idx];	
			idx--;
		}
		return searchPC;
	}
	

	public int getNextPc ( int pc )
	{
		Iterator<UIFInstruction> uifIter = uifGraph.iterator();
		while ( uifIter.hasNext())
		{
			UIFInstruction uifInstr = uifIter.next();
			if ( uifInstr.getPc() > pc )
				return uifInstr.getPc();
		}
		return -1;
	}
	
	public int getFirstPc (  )
	{
		return uifGraph.getFirst().getPc();
	}
	
	public int getPrevPc ( int pc )
	{
		Iterator<UIFInstruction> uifIter = uifGraph.iterator();
		UIFInstruction prev = null, uifInstr = null;
		while ( uifIter.hasNext() )
		{
			prev = uifInstr; 
			uifInstr = uifIter.next();
			if ( uifInstr.getPc() ==  pc )
				return prev.getPc();
		}
		return -1;
	}
	
	public int getUIFInstructionTypeFromPC ( int pc )
	{
		Iterator<UIFInstruction> uifIter = uifGraph.iterator();
		while ( uifIter.hasNext())
		{
			UIFInstruction uifInstr = uifIter.next();
			if ( uifInstr.getPc() == pc )
				return uifInstr.getInstructionType();
		}
		return -1;
	}
	
	public int pc2aidx( int pc )
	{
		Object [] uifIter = uifGraph.toArray();
		int index = 0;
		while ( index < uifIter.length )
		{
			if ( ((UIFInstruction)(uifIter[index])).getPc() == pc )
				return index;
			index++;
		}
		return -1;
	}
	
	public UIFInstruction getUIFInstructionFromPC ( int pc )
	{
		Iterator<UIFInstruction> uifIter = uifGraph.iterator();
		while ( uifIter.hasNext())
		{
			UIFInstruction uifInstr = uifIter.next();
			if ( uifInstr.getPc() == pc )
				return uifInstr;
		}
		return null;
	}
	
	private boolean reachableFromNextIf ( int currPc )
	{
		System.out.println ( "Reachable from next if " + currPc );
		UIFInstruction nextInstr;
		int lastInstrPc = ((UIFInstruction)uifGraph.toArray()[uifGraph.toArray().length-1]).getPc();
		int thisPc = currPc;
		while (currPc < lastInstrPc )
	    {
			 currPc = getNextPc(currPc);
			 nextInstr = getUIFInstructionFromPC ( currPc );
			 if ( nextInstr.getInstructionType() == UIFInstruction.IF )
				 break;
	    }
		if ( currPc == lastInstrPc )
			return false;
		else
			return linkExists (currPc, thisPc);
	}
	
	
	private boolean linkExists ( int fromPC, int toPC )
	{
		System.out.println ( "linkExists ? " + fromPC + " to " + toPC );
		UIFInstruction fromInstr = this.getUIFInstructionFromPC(fromPC);
		UIFInstruction toInstr   = this.getUIFInstructionFromPC(toPC);
		if ( isOperand(fromInstr, toInstr))
			return true;
		else
		{
			Object [] sources = fromInstr.getSources();
			for ( int i = 0 ; i < sources.length ; i ++)
				if ( sources[i] instanceof UIFInstruction && linkExists(((UIFInstruction)(sources[i])).getPc(),toInstr.getPc()))
					return true;
			return false;
		}
	}
	
	private boolean isOperand ( UIFInstruction fromInstr, UIFInstruction toInstr )
	{
		Object [] sources = fromInstr.getSources();
		for ( int i = 0 ; i < sources.length ; i ++)
			if ( sources[i] instanceof UIFInstruction && ((UIFInstruction)(sources[i])).getPc() == toInstr.getPc() )
				return true;
		return false;
	}
	
	
	
	private boolean isOrphan ( UIFInstruction invokeInstr )
    {
    	Iterator<UIFInstruction> uifIter = uifGraph.iterator();
    	while ( uifIter.hasNext())
    	{
    		UIFInstruction uifInstr = uifIter.next();
    		Object [] sources = uifInstr.getSources();
    		if ( sources == null )
    			continue;
    		for ( int i = 0 ; i < sources.length ; i++ )
    		{
    			if ( sources[i] instanceof UIFInstruction )
    			{
    				if ( sources[i] == invokeInstr )
    					return false;
    			}
    		}
    	}
    	return true;
    }
	
    private UIFInstruction getParent ( UIFInstruction instr )
    {
    	Iterator<UIFInstruction> uifIter = uifGraph.iterator();
    	while ( uifIter.hasNext())
    	{
    		UIFInstruction uifInstr = uifIter.next();
    		Object [] sources = uifInstr.getSources();
    		if ( sources == null )
    			continue;
    		for ( int i = 0 ; i < sources.length ; i++ )
    		{
    			if ( sources[i] instanceof UIFInstruction )
    			{
    				if ( sources[i] == instr )
    					return uifInstr;
    			}
    		}
    	}
    	return null;
    }
    
	private boolean notOtherStatement ( UIFInstruction uifInstr )
	{
		switch ( uifInstr.getInstructionType() )
		{
			case UIFInstruction.WRARRAY:
			case UIFInstruction.WRFIELD:
			case UIFInstruction.STORE:
			case UIFInstruction.RETURN:
			case UIFInstruction.INC: 
				return false;
			case UIFInstruction.INVOKE:
			case UIFInstruction.CONSTRUCT:
				return (!isOrphan ( uifInstr ));
			default : return true;
		}
	}
	
	private void convertJVMInstrLinktoUIFInstrLink ( )
	{
	    Iterator<UIFInstruction> uifGraphIterator = uifGraph.iterator();
	    int dbcount=0;
	    while ( uifGraphIterator.hasNext())
	    {
	    	UIFInstruction currentInstr = ((UIFInstruction)(uifGraphIterator.next()));
	    	Object [] sources = currentInstr.getSources();
	    	if ( sources == null )
	    		continue; //for the dummy instruction
	    	for ( int i = 0 ; i < sources.length ; i ++ )
	    		if ( sources[i] instanceof JVMInstruction )
	    		{
	    			System.out.println ("aioob->" + ((JVMInstruction)sources[i]).getPC());
	    			sources[i] = fetchUIFInstruction ( ((JVMInstruction)sources[i]).getPC());
	    			if ( ((UIFInstruction)sources[i]).getInstructionType() == UIFInstruction.OBJCREATE && currentInstr.getInstructionType()!= UIFInstruction.CONSTRUCT )
	    	            sources[i] = ((UIFInstruction)sources[i]).getResult();
	    		}    
	    	if ((currentInstr.getInstructionType() == UIFInstruction.CONSTRUCT) && (sources[0] instanceof UIFInstruction))
	    	    ((UIFInstruction)sources[0]).setResult(currentInstr);
	    	/*Adjust the branch destinations which still point to removed TIs*/
	    	if ( currentInstr.getInstructionType() == UIFInstruction.IF  )
	    	{
	    		adjustDestination ( currentInstr.getSources()[2] );
	    	}
	    	if ( currentInstr.getInstructionType() == UIFInstruction.GOTO )
	    	{
	    		adjustDestination ( currentInstr.getSources()[0] );
	    	}
	    	dbcount++;
	    }
	}
	
	private boolean isSuperCall ( Object receiver, MethodRef mref )
	{
		/* If a method A(S)R invokes another method A(S)R on the this object, it is a super.A call!*/
		if ( receiver instanceof LocalVariable)
			if ( ((LocalVariable)receiver).getLVIndex() == 0 ) //this
				if ( mref.getName().equals(methodData.getName()))
					if ( mref.getSignature().equals(methodData.getSignature()))
						return true;
		return false;
	}
	
	private void adjustDestination ( Object branchDestination )
	{
		int dest = ((BranchDestination)branchDestination).getPc();
		Object []  uifArray = uifGraph.toArray();
		int index = 0, pc = 0;
		do
		{
			pc = ((UIFInstruction)uifArray[index++]).getPc();
		}while ( index < uifArray.length && pc < dest);
		
		((BranchDestination)branchDestination).setPc(pc);
	}
	
	
	public int searchOrphanGOTO ( int startPC, int endPC )
	{
		int travPC = startPC;
		boolean singleIF = false;
		while ( travPC <= endPC )
		{
			if ( this.getUIFInstructionTypeFromPC(travPC) == UIFInstruction.IF )
				singleIF = true;
			else if ( this.getUIFInstructionTypeFromPC(travPC) == UIFInstruction.GOTO )
			{
				if ( singleIF )
					singleIF = false;
				else
					break;
			}
			travPC = getNextPc ( travPC );
		}
		if ( travPC > endPC )
			travPC = getPrevPc ( travPC );
		return travPC;
	}
	
	private void evaluateLocalVariableTypes ( )
	{
		String [] argumentTypes = methodSignature.getFullQualifiedArgumentArray();
		String [] argumentNames = methodSignature.getArgumentNames();
		String [] shortTypes 	= methodSignature.getShortArgumentTypes();

		for ( int i = 0; i < argumentTypes.length; i++ )
		{
			lvarArray[i] = new LvarNameAndType ( argumentNames[i], argumentTypes[i], shortTypes[i]);
			if ( argumentTypes[i] == "long" || argumentTypes[i] == "double")
	    		i++;
		}
		
	    Iterator<UIFInstruction> uifGraphIterator = uifGraph.iterator();
	  
	    while ( uifGraphIterator.hasNext())
	    {
	    	UIFInstruction uifInstr = uifGraphIterator.next();
	    	Object [] sources = ((UIFInstruction)(uifInstr)).getSources();
	    	copyNameFromLvarArray ( sources );
	    	switch ( uifInstr.getInstructionType())
	    	{
	    		case UIFInstruction.ARRCREATE:
	    			int numDimensions = uifInstr.getSources().length - 1;
	    			if ( sources[0] instanceof ClassRef )
	    			{
	    				String className = ((ClassRef)sources[0]).getClassName();
	    				for ( int i = 0 ; i < numDimensions; i++ )
	    					className = "[" + className; 
	    				uifInstr.setDataType(arrayClassName(className));
	    			}
	    			else if ( sources[0] instanceof Constant )
	    			{
	    				String arrayTypeS;
	    				switch ( ((Integer)(((Constant)(sources[0])).getConstantObj())).intValue())
	    				{
	    					case 4 : arrayTypeS = "boolean"; break;
	    					case 5 : arrayTypeS = "char";    break;
	    					case 6 : arrayTypeS = "float";   break;
	    					case 7 : arrayTypeS = "double";  break;
	    					case 8 : arrayTypeS = "byte";    break;
	    					case 9 : arrayTypeS = "short";   break;
	    					case 10: arrayTypeS = "int";     break;
	    					case 11: arrayTypeS = "long";    break;
	    					default: arrayTypeS = "error";
	    				}
	    				for ( int i = 0 ; i < numDimensions; i++ )
	    					arrayTypeS = "[" + arrayTypeS;
	    				uifInstr.setDataType(arrayTypeS);
	    			}
	    			break;
	    			
	    		case UIFInstruction.OBJCREATE:
	    			uifInstr.setDataType(((ClassRef)sources[0]).getClassName());
	    			break;
	    			
	    		case UIFInstruction.TERNARY_OP:
	    			Object tSource = uifInstr.getSources()[5];
	    			if ( tSource instanceof Constant && ((Constant) tSource).getConstantObj() == null )
	    				tSource = uifInstr.getSources()[6];
	    			
	    			if ( tSource instanceof Integer )
	    			{
	    				if ((getUIFInstructionFromPC(((Integer) tSource).intValue())).getDataType() != null)
	    					uifInstr.setDataType((getUIFInstructionFromPC(((Integer) tSource).intValue())).getDataType());
	    				else
		    			{
	    					getUIFInstructionFromPC(((Integer) tSource).intValue()).addTypeUpdate(uifInstr.getPc());
		    				System.out.println ( "Caching for update on INSTR " + tSource.toString());
		    			}
	    			}
	    			else if ( tSource instanceof UIFInstruction )
	    			{
	    				if ( ((UIFInstruction) tSource).getDataType() != null )
	    					uifInstr.setDataType(((UIFInstruction)tSource).getDataType ( ));
	    				else
	    				{
	    					((UIFInstruction)tSource).addTypeUpdate(uifInstr.getPc ( ));
	    					System.out.println ( "Caching for update on " + tSource.toString());
	    				}
	    			}
	    			else if ( tSource instanceof LocalVariable )
	    				uifInstr.setDataType(lvarArray[((LocalVariable)tSource).getLVIndex()].getFullClassName());
	    			else if ( tSource instanceof Constant )
	    				uifInstr.setDataType( ((Constant) tSource).getType());
	    			
	    			System.out.println ( tSource.toString());
	    			System.out.println ( "Type TERNARY_OP = " + uifInstr.getDataType());
	    			break;
	    			
	    		case UIFInstruction.RDARRAY:
	    			Object source1 = uifInstr.getSources()[0];
	    			if ( source1 instanceof UIFInstruction)
	    				uifInstr.setDataType(((UIFInstruction)(source1)).getDataType());
	    			else if ( source1 instanceof LocalVariable )
	    				uifInstr.setDataType(lvarArray[((LocalVariable)source1).getLVIndex()].getFullClassName());
	    			break;
	    			
	    		case UIFInstruction.ARRLENGTH:
	    			uifInstr.setDataType("int");
	    			break;
	    			
	    		case UIFInstruction.RDFIELD:
	    			switch ( uifInstr.getBytecode())
	    			{
	    				case Bytecodes.getfield :
	    					uifInstr.setDataType(((FieldRef)(uifInstr.getSources()[1])).getSignature());
	    					break;
	    				case Bytecodes.getstatic:
	    					uifInstr.setDataType(((FieldRef)(uifInstr.getSources()[0])).getSignature());
	    					break;
	    			}
	    		    break;
	    		    
	    		case UIFInstruction.ADD:
	    		case UIFInstruction.SUB:
	    		case UIFInstruction.MUL:
	    		case UIFInstruction.DIV:
	    		case UIFInstruction.MOD:
	    		case UIFInstruction.AND:
	    		case UIFInstruction.OR:
	    		case UIFInstruction.XOR:
	    		case UIFInstruction.SHIFTL:
	    		case UIFInstruction.SHIFTR:
	    		case UIFInstruction.SHIFTRU:
	    		    switch ( uifInstr.getBytecode())
	    		    {
	    		    	case Bytecodes.dadd: case Bytecodes.dsub: case Bytecodes.dmul:
	    		    	case Bytecodes.ddiv: case Bytecodes.drem:
	    		    		uifInstr.setDataType("double");
	    		    		break;
	    		    	case Bytecodes.ladd: case Bytecodes.lsub: case Bytecodes.lmul:
	    		        case Bytecodes.ldiv: case Bytecodes.lrem: case Bytecodes.land:
	    		    	case Bytecodes.lor:	 case Bytecodes.lxor: case Bytecodes.lshl:
	    		    	case Bytecodes.lshr: case Bytecodes.lushr:
	    		    		uifInstr.setDataType("long");
	    		    		break;
	    		    	case Bytecodes.iadd: case Bytecodes.isub: case Bytecodes.imul:
	    		    	case Bytecodes.idiv: case Bytecodes.irem: case Bytecodes.iand:
	    		    	case Bytecodes.ior:  case Bytecodes.ixor: case Bytecodes.ishl:
	    		    	case Bytecodes.ishr: case Bytecodes.iushr:
	    		    		uifInstr.setDataType("int");
	    		    		break;
	    		    	case Bytecodes.fadd: case Bytecodes.frem: case Bytecodes.fsub:
	    		    	case Bytecodes.fdiv: case Bytecodes.fmul:
	    		    		uifInstr.setDataType("float");
	    		    		break;
	    		    }
	    		    break;
	    		case UIFInstruction.INC:
	    			uifInstr.setDataType("int");
	    			break;
	    		case UIFInstruction.TYPECAST:
	    			switch ( uifInstr.getBytecode())
	    			{
	    				case Bytecodes.checkcast:
	    					uifInstr.setDataType( ((ClassRef)(uifInstr.getSources()[0])).getClassName());
	    					break;
	    				default:
	    					uifInstr.setDataType(typeCastTo ( uifInstr.getBytecode()));
	    					break;
	    		    }
	    			break;
	    		
	    		case UIFInstruction.INVOKE:
	    			Object [] scs = uifInstr.getSources();
	    			String signature = ((MethodRef)scs[scs.length-1]).getSignature();
	    			uifInstr.setDataType(getReturnType(signature));
	    			break;
	    			
	    		case UIFInstruction.CONSTRUCT:
	    			Object conSrc = ((UIFInstruction)uifInstr).getSources()[0];
	    			if ( conSrc instanceof UIFInstruction )
	    				uifInstr.setDataType( ((UIFInstruction)conSrc).getDataType());
	    			else if ( conSrc instanceof LocalVariable )
	    				uifInstr.setDataType( lvarArray[((LocalVariable)conSrc).getLVIndex()].getFullClassName());
	    		    break;
	    		    
	    		case UIFInstruction.STORE:
 	    			Object result = uifInstr.getResult();
	    			int resultIndex = ((LocalVariable)result).getLVIndex();
	    			Object source = uifInstr.getSources()[0];
	    			
	    			if (lvarArray[resultIndex] != null )
	    			{
	    				
	    				int srcIndex;
		    			String srcType;
	    				if ( source instanceof LocalVariable )
	    				{
	    					srcIndex = ((LocalVariable)source).getLVIndex();
	    					srcType = lvarArray[srcIndex].getFullClassName();
	    				}
	    				else if ( source instanceof Constant )
	    					srcType = ((Constant)source).getType();
	    				else	
	    					srcType = ((UIFInstruction)source).getDataType();
	    				
	    				((LocalVariable)result).setNameAndType(lvarArray[resultIndex].getFullClassName(), lvarArray[resultIndex].getShortName(), lvarArray[resultIndex].getName());
	    				
	    				if ( !srcType.equals(lvarArray[resultIndex].getFullClassName())) //the local is being reused
	    				{
	    					if ( source instanceof UIFInstruction && ((UIFInstruction)source).getInstructionType() == UIFInstruction.RDARRAY )
		    				{
		    					int accessDims = getNumberOfDimensions ( (UIFInstruction)source );
		    					int typeDims   = getNumberOfDimsFromString ( srcType );
		    					srcType = adjustDims ( srcType, typeDims - accessDims);
		    				}
		    				uifInstr.setDataType( srcType );
	    					lvarArray[resultIndex] = new LvarNameAndType ( srcType );
	    					((LocalVariable)result).setInitializer(true);	
	    				}
	    				((LocalVariable)result).setNameAndType(lvarArray[resultIndex].getFullClassName(), lvarArray[resultIndex].getShortName(), lvarArray[resultIndex].getName());
	    				break;
	    			}
	    			
	    			if ( source instanceof LocalVariable )
	    			{
	    				int srcIndex = ((LocalVariable)source).getLVIndex();
	    				String srcType = lvarArray[srcIndex].getFullClassName();
	    				lvarArray[resultIndex] = new LvarNameAndType ( srcType );
	    			}
	    			else if ( source instanceof Constant )
	    			{
	    				String srcType = ((Constant)source).getType();
	    				lvarArray[resultIndex] = new LvarNameAndType ( srcType );
	    			}
	    			else
	    			{  
	    				String srcType = ((UIFInstruction)source).getDataType();
	    				if (((UIFInstruction)source).getInstructionType() == UIFInstruction.RDARRAY )
	    				{
	    					int accessDims = getNumberOfDimensions ( (UIFInstruction)source );
	    					int typeDims   = getNumberOfDimsFromString ( srcType );
	    					srcType = adjustDims ( srcType, typeDims - accessDims);
	    				}
	    				if (((UIFInstruction)source).getInstructionType() == UIFInstruction.ARRCREATE )
	    				{
	    					int accessDims = ((UIFInstruction)source).getSources().length-1;
	    					srcType = adjustDims ( srcType, accessDims);
	    				}
	    				uifInstr.setDataType( srcType );
	    				lvarArray[resultIndex] = new LvarNameAndType ( srcType );
	    			} 
	    			((LocalVariable)result).setNameAndType(lvarArray[resultIndex].getFullClassName(), lvarArray[resultIndex].getShortName(), lvarArray[resultIndex].getName());
	    			((LocalVariable)result).setInitializer(true);
	    			break;
	    		
	    	}
	    	int [] updatePCs = uifInstr.getTypeUpdatePC();
	    	String type = uifInstr.getDataType();
	    	for ( int i = 0 ; i < uifInstr.getNumTypeUpdates() ; i++ )
	    	{
	    		UIFInstruction cachedInstr = getUIFInstructionFromPC(updatePCs[i]);
	    		cachedInstr.setDataType(type);
	    	}
	    }
	}
	
	private int getNumberOfDimensions ( UIFInstruction source )
	{
			int numDim = 1;
			while ( (source.getSources()[0] instanceof UIFInstruction) && (((UIFInstruction)(source.getSources()[0])).getInstructionType() == UIFInstruction.RDARRAY ))
			{
				source = (UIFInstruction)(source.getSources()[0]);
			    numDim++;
			}
			return numDim;
	}
	
	private int getNumberOfDimsFromString ( String source )
	{
		int index = 0, numDim = 0;
		while ( index < source.length() )
		{
			if ( source.charAt(index++) == '[')
				numDim++;
		}
		return numDim;
	}
	
	private String adjustDims ( String src, int dims )
	{
		StringBuffer sBuff = new StringBuffer ( );
		int i = 0;
		while ( i < src.length())
		{
			if ( src.charAt(i) == '[' || src.charAt(i) == ' ' ) 
			{
				i++;
				continue;
			}
			else
			{
				sBuff.append( src.charAt(i) );
				i++;
			}
		}
		if ( dims > 0 )
			sBuff.append(" ");
		for ( int j = 0 ; j < dims ; j++ )
			sBuff.append ( "[]");
		return sBuff.toString();
	}
	
	private void copyNameFromLvarArray ( Object [] sources )
	{
		if ( sources == null )
			return;
		for ( int i = 0 ; i < sources.length; i++ )
		{
			if ( sources[i] instanceof LocalVariable )
			{
				int lvIndex = ((LocalVariable)sources[i]).getLVIndex();
				((LocalVariable)sources[i]).setNameAndType(lvarArray[lvIndex].getFullClassName(), lvarArray[lvIndex].getShortName(), lvarArray[lvIndex].getName());
			}
		}
	}
	
	private String arrayClassName ( String className )
	{
		StringBuffer newName = new StringBuffer ( );
		char [] oldName = className.toCharArray();
		int index = 0;
		int dims = 0;
		while ( index < oldName.length)
		{
			if ( oldName[index] != '[' && oldName[index] != ';')
				newName.append(oldName[index]);
			if ( oldName[index] == '[')
				dims++;
			index++;
		}
	 
		StringBuffer dimsArr = new StringBuffer ( );
	
		for ( int j = 0 ; j < dims ; j++ )
			dimsArr.append ("[]");
		String typeString = newName.toString();
		String dimsStr = dimsArr.toString();
			 if ( typeString.equals("I")) typeString = "int";
		else if ( typeString.equals("J")) typeString = "long";
		else if ( typeString.equals("Z")) typeString = "boolean";
		else if ( typeString.equals("C")) typeString = "char";
		else if ( typeString.equals("S")) typeString = "short";
		else if ( typeString.equals("F")) typeString = "float";
		else if ( typeString.equals("B")) typeString = "byte";
		else if ( typeString.equals("D")) typeString = "double";
	
	    return typeString.concat(" "+dimsStr);
	}
	
	private String getReturnType ( String signature )
	{
		char [] sign = signature.toCharArray();
		StringBuffer sBuff = new StringBuffer ( );
		int index = 0;
		while ( sign[index] != ')')
			index++;
		index++;
		while ( sign[index] == '[')
			sBuff.append( sign[index++]);
		if ( sign[index] == 'L')
		{
			index++;
			while ( sign[index] != ';')
				sBuff.append( sign[index++]);
		}
		else
			sBuff.append ( sign[index]);
		if ( sBuff.toString().contains("[") )
		{
			return arrayClassName (sBuff.toString());
		}
		return sBuff.toString();
	}
	
	
	public void dumpUIFGraph ( )
	{
		UIFInstruction nextInstr = null;
	    int length = uifGraph.size();
	    int index = 0;
	    System.out.println ( "\n\n\n***Untyped-Intermediate-Form Instructions***");
	    while ( index < length )
	    {
	    	nextInstr = (UIFInstruction)uifGraph.get(index);
	    	System.out.println ( "\n"+ nextInstr.toString() );
	    	index++;
	    }
	}
	
	private String typeCastTo ( int bytecode )
	{
	    	switch ( bytecode )
	    	{
	    	   case Bytecodes.d2f:
			   case Bytecodes.i2f:
			   case Bytecodes.l2f:
				   return "float";
			   case Bytecodes.d2i:
			   case Bytecodes.f2i:	   
			   case Bytecodes.l2i:
				   return "int";
			   case Bytecodes.i2d:
			   case Bytecodes.f2d:
			   case Bytecodes.l2d:
				   return "double";
			   case Bytecodes.d2l:
			   case Bytecodes.f2l:
			   case Bytecodes.i2l:
				   return "long";
			   case Bytecodes.i2b:
				   return "byte";
			   case Bytecodes.i2c:
				   return "char";
			   case Bytecodes.i2s:
				   return "short";
			   default : 
				   return "error";
	    	}
	}
	
	private boolean isSingleArgDup ( int bytecode )
	{
		return ( bytecode == Bytecodes.dup );
	}
	
	private boolean methodReturnsVoid ( MethodMetaData methodData )
	{
		return methodData.getSignature().endsWith("V");
	}
	
	private int methodReturnsBoolean ( MethodMetaData methodData )
	{
		if ( methodData.getSignature().endsWith("Z") )
			return 1;
		else 
			return 0;
	}
	
	public int closestIFHead ( int pc )
	{
		int travPC = pc;
		System.out.println ( "Start from PC = " + travPC );
		while ( getUIFInstructionFromPC(travPC).getInstructionType() != UIFInstruction.IF 
				|| getUIFInstructionFromPC(travPC).getSources()[4] == null )
		{
			travPC = getNextPc ( travPC );
			System.out.println ( "travPC = " + travPC );
		}
		return travPC;
	}
	
	private Object reachActualSource ( Object source )
	{
		/* I am supporting only one level of dup
		 * Eg:
		 * 	aload
		 * 	dup
		 *  ...
		 * There may be multiple dups in a sequence - something that I am not considering.
		 */
    	if ( isStackOperation(((JVMInstruction)source).getOpcode()))
    		source = ((JVMInstructionUnary)source).getOperand();
    	else if ( isSingleArgDup (((JVMInstruction)source).getOpcode()))
		{
			source = ((StackSlot)(((JVMInstructionMulArgsMulRes)source).getSourceOperands()[0])).getProducer();
			if ( isStackOperation(((JVMInstruction)source).getOpcode()))
    		{
				Object src = ((JVMInstructionUnary)source ).getOperand();
				if ( src instanceof JVMInstruction )
					source = ((StackSlot)(((JVMInstructionUnary)source ).getOperand())).getProducer();
				else if ( src instanceof LocalVariable )
					source = (LocalVariable)src;
					
    		}
		}
    	return source;
	}
	
	private int getUIFInstructionType ( int bytecode )
	{
		switch ( bytecode )
		{
			case Bytecodes.iadd:
			case Bytecodes.ladd:
			case Bytecodes.fadd:
			case Bytecodes.dadd:
				return UIFInstruction.ADD;
			case Bytecodes.imul:
			case Bytecodes.lmul:
			case Bytecodes.fmul:
			case Bytecodes.dmul:
				return UIFInstruction.MUL;
			case Bytecodes.idiv:
			case Bytecodes.ldiv:
			case Bytecodes.fdiv:
			case Bytecodes.ddiv:
				return UIFInstruction.DIV;
			case Bytecodes.ineg:
			case Bytecodes.lneg:
			case Bytecodes.dneg:
			case Bytecodes.fneg:
				return UIFInstruction.NEG;
			case Bytecodes.irem:
			case Bytecodes.lrem:
			case Bytecodes.drem:
			case Bytecodes.frem:
				return UIFInstruction.MOD;
			case Bytecodes.isub:
			case Bytecodes.lsub:
			case Bytecodes.fsub:
			case Bytecodes.dsub:
				return UIFInstruction.SUB;
			case Bytecodes.iinc:
				return UIFInstruction.INC;
			case Bytecodes.ishl:
			case Bytecodes.lshl:
				return UIFInstruction.SHIFTL;
			case Bytecodes.iand:
			case Bytecodes.land:
				return UIFInstruction.AND;
			case Bytecodes.ior:
			case Bytecodes.lor:
				return UIFInstruction.OR;
			case Bytecodes.ixor:
			case Bytecodes.lxor:
				return UIFInstruction.XOR;
			case Bytecodes.lshr:
			case Bytecodes.ishr:
				return UIFInstruction.SHIFTR;
			case Bytecodes.lushr:
			case Bytecodes.iushr:
				 return UIFInstruction.SHIFTRU;
			case Bytecodes.invokeinterface:
			case Bytecodes.invokespecial:
			case Bytecodes.invokestatic:
			case Bytecodes.invokevirtual:
				return UIFInstruction.INVOKE;
			default: return -1;
		}
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
	
	private int getIfType ( int opcode )
	{
		switch ( opcode )
		{
			case Bytecodes.ifeq : 
			case Bytecodes.ificmpeq:
			case Bytecodes.ifacmpeq:
			case Bytecodes.ifnull:
				return UIFInstruction.EQ;
			case Bytecodes.ifne :
			case Bytecodes.ificmpne:
			case Bytecodes.ifacmpne:
			case Bytecodes.ifnonnull:
				return UIFInstruction.NE;
			case Bytecodes.ifge :
			case Bytecodes.ificmpge:
				return UIFInstruction.GE;
			case Bytecodes.ifle :
			case Bytecodes.ificmple:
				return UIFInstruction.LE;
			case Bytecodes.ifgt :
			case Bytecodes.ificmpgt:
				return UIFInstruction.GT;
			case Bytecodes.iflt :
			case Bytecodes.ificmplt:
				return UIFInstruction.LT;
		}
		return -1; //not possible
	}

}

class LvarNameAndType 
{
	String name;
	String fullClassName;
	String shortName;
	
	public LvarNameAndType ( String type )
	{
		System.out.println ( " In LvarNameAndType with type = " + type);
		this.fullClassName = type;
		if ( fullClassName.contains("/"))
		{
			String [] pckStr = fullClassName.split("/");
			shortName = pckStr[pckStr.length-1];
		}
		else
		{
				shortName = fullClassName;
			         if ( shortName.equals("I")) shortName = "int";
				else if ( shortName.equals("J")) shortName = "long";
				else if ( shortName.equals("Z")) shortName = "boolean";
				else if ( shortName.equals("C")) shortName = "char";
				else if ( shortName.equals("S")) shortName = "short";
				else if ( shortName.equals("F")) shortName = "float";
				else if ( shortName.equals("B")) shortName = "byte";
				else if ( shortName.equals("D")) shortName = "double";
		}
		System.out.println ( "shortName = " + shortName );
		name = RandomNameGenerator.generateName(shortName);
	}
	
	public LvarNameAndType ( String name, String fullType, String type )
	{
		this.name = name;
		this.fullClassName = fullType;
		this.shortName = type;
	}
	
	public String getFullClassName ( )
	{
		return fullClassName;
	}
	
	public String getShortName ( )
	{
		return shortName;
	}
	
	public String getName ( )
	{
		return name;
	}
	
}

