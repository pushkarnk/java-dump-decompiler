package jdd.main;

import java.util.ArrayList;
import java.util.Iterator;

import com.ibm.j9ddr.debugextensions.vm24.jdd.dumpreader.Bytecodes;
import com.ibm.j9ddr.debugextensions.vm24.jdd.grammar.ArithmeticExpression;
import com.ibm.j9ddr.debugextensions.vm24.jdd.grammar.ArrayCreateExpression;
import com.ibm.j9ddr.debugextensions.vm24.jdd.grammar.ArrayLengthExpression;
import com.ibm.j9ddr.debugextensions.vm24.jdd.grammar.ArrayOperandExpression;
import com.ibm.j9ddr.debugextensions.vm24.jdd.grammar.AssignmentStatement;
import com.ibm.j9ddr.debugextensions.vm24.jdd.grammar.BitwiseExpression;
import com.ibm.j9ddr.debugextensions.vm24.jdd.grammar.BreakStatement;
import com.ibm.j9ddr.debugextensions.vm24.jdd.grammar.ComparisonExpression;
import com.ibm.j9ddr.debugextensions.vm24.jdd.grammar.ConditionExpression;
import com.ibm.j9ddr.debugextensions.vm24.jdd.grammar.ContinueStatement;
import com.ibm.j9ddr.debugextensions.vm24.jdd.grammar.Expression;
import com.ibm.j9ddr.debugextensions.vm24.jdd.grammar.ExpressionList;
import com.ibm.j9ddr.debugextensions.vm24.jdd.grammar.IfElseStatement;
import com.ibm.j9ddr.debugextensions.vm24.jdd.grammar.IncDecExpression;
import com.ibm.j9ddr.debugextensions.vm24.jdd.grammar.LocalVariableIncStatement;
import com.ibm.j9ddr.debugextensions.vm24.jdd.grammar.LoopStatement;
import com.ibm.j9ddr.debugextensions.vm24.jdd.grammar.MethodBody;
import com.ibm.j9ddr.debugextensions.vm24.jdd.grammar.MethodInvocationExpression;
import com.ibm.j9ddr.debugextensions.vm24.jdd.grammar.MethodInvocationStatement;
import com.ibm.j9ddr.debugextensions.vm24.jdd.grammar.ObjectCreateExpression;
import com.ibm.j9ddr.debugextensions.vm24.jdd.grammar.OperandExpression;
import com.ibm.j9ddr.debugextensions.vm24.jdd.grammar.ReadExpression;
import com.ibm.j9ddr.debugextensions.vm24.jdd.grammar.ReturnStatement;
import com.ibm.j9ddr.debugextensions.vm24.jdd.grammar.StatementList;
import com.ibm.j9ddr.debugextensions.vm24.jdd.grammar.TernaryOperatorExpression;
import com.ibm.j9ddr.debugextensions.vm24.jdd.grammar.TypecastExpression;
import com.ibm.j9ddr.debugextensions.vm24.jdd.grammar.UnaryExpression;
import com.ibm.j9ddr.debugextensions.vm24.jdd.grammar.WriteArrayStatement;
import com.ibm.j9ddr.debugextensions.vm24.jdd.grammar.WriteFieldStatement;
import com.ibm.j9ddr.debugextensions.vm24.jdd.instructions.UIFInstruction;
import com.ibm.j9ddr.debugextensions.vm24.jdd.operands.BranchDestination;
import com.ibm.j9ddr.debugextensions.vm24.jdd.operands.Constant;
import com.ibm.j9ddr.debugextensions.vm24.jdd.operands.LocalVariable;
import com.ibm.j9ddr.debugextensions.vm24.jdd.operands.Operand;

public class CodeGenerator {
	/* Class to support the final step of the de-compilation process - Java Code Generation
	 * An invocation of toString ( ) of this method outputs the product of de-compilation.
	 * TODO: Build this class incrementally
	 */
	UntypedIntermediateForm uifRep;
	MethodBody javaMethod;
	boolean decompilingLoop;
	int tempLoopStart;
	int tempLoopEnd;
	public CodeGenerator ( UntypedIntermediateForm uifObject )
	{
		uifRep = uifObject;
		javaMethod = new MethodBody ( uifRep );
		decompilingLoop = false;
	}
	
	public void generateJavaCode (  )
	{
		//Create the MethodBody in this method
		StatementList statements = javaMethod.getStatementList();
		markInfiniteLoopStarts ( );
		int index = generateJavaCode (statements, 0, uifRep.getUIFInstructionsInArray().length-1 );
		if ( index == uifRep.getUIFInstructionsInArray().length )
			System.out.println ( "Finished Decompilation" );
		
	}
	
	
	private void markInfiniteLoopStarts ( )
	{
		Iterator<UIFInstruction> uifIter = uifRep.getIterator();
		while (uifIter.hasNext())
		{
			UIFInstruction currInstr = uifIter.next();
			if ( currInstr.getInstructionType() == UIFInstruction.GOTO && ((BranchDestination)(currInstr.getSources()[0])).getPc() < currInstr.getPc() )
			{
				UIFInstruction closestIFHead = uifRep.getUIFInstructionFromPC(uifRep.closestIFHead(((BranchDestination)(currInstr.getSources()[0])).getPc()));
				int end = ((Integer)(closestIFHead.getSources()[6])).intValue();
				if ( end != currInstr.getPc())
				{
					uifRep.getUIFInstructionFromPC(((BranchDestination)(currInstr.getSources()[0])).getPc()).setStartInfiniteLoop();
					uifRep.getUIFInstructionFromPC(((BranchDestination)(currInstr.getSources()[0])).getPc()).setLoopEndPC(currInstr.getPc());
					System.out.println ( "Infinite loop starts at " + ((BranchDestination)(currInstr.getSources()[0])).getPc());
				}
			}
		}
	}
	
    private int generateJavaCode ( StatementList statements, int index, int end )
    {
    	Object[] uifList = uifRep.getUIFInstructionsInArray();
    	int startPC = -1, endPC = -1, gotoDestination = -1;
    	int startIdx = index;
    	while ( index <= end ) //uifList.length
    	{
    		
    		if ( ((UIFInstruction)uifList[index]).getStartInfiniteLoop() == true )
    		{
    			
    			((UIFInstruction)uifList[index]).resetStartInfiniteLoop();
				Object [] sources = ((UIFInstruction)uifList[index]).getSources();
				startPC = ((UIFInstruction)uifList[index]).getPc();
				endPC = uifRep.getUIFInstructionFromPC(startPC).getLoopEndPC();
				System.out.println ( "INFINITE LOOP from " + startPC + " to " + endPC );
				LoopStatement whileLoop = new LoopStatement ( LoopStatement.FOR_OR_WHILE );
				whileLoop.setExpression(null);
				decompilingLoop = true;
				tempLoopStart = startPC;
				tempLoopEnd = endPC;
				index = generateJavaCode ( whileLoop.getStatements(), uifRep.pc2aidx(startPC), uifRep.pc2aidx(uifRep.getPrevPc(endPC)));
				System.out.println ( index );
				decompilingLoop = false;
				statements.addStatement(whileLoop);
				continue;
    		}
    		
    		int instrType = ((UIFInstruction)uifList[index]).getInstructionType();
    		switch ( instrType )
    		{
    			case UIFInstruction.WRARRAY:
    				ExpressionList exprList = new ExpressionList ( );
    				Expression baseArray = collectDimensionExpressions (exprList,((UIFInstruction)uifList[index]));
    				Expression wrRHS = createExpression(((UIFInstruction)uifList[index]).getSources()[2]);
    				statements.addStatement(new WriteArrayStatement ( new ArrayOperandExpression(baseArray, exprList), wrRHS));
    				index++;
    				break;
    				
    			case UIFInstruction.WRFIELD:
    				Object [] sources = ((UIFInstruction)uifList[index]).getSources();
    				Expression receiver,wfRHSExpression; 
    			    if ( sources.length == 2 )
    			    {
    			    	receiver = createExpression(sources[0]);
    			    	wfRHSExpression = createExpression ( sources[1]);
    			    }
    			    else
    			    {
    			    	receiver = null;
    			    	wfRHSExpression = createExpression ( sources[0]);
    			    }
    				OperandExpression field = (OperandExpression) createExpression(((UIFInstruction)uifList[index]).getResult());
    				statements.addStatement(new WriteFieldStatement (receiver, field, wfRHSExpression));
    				index++;
    				break;
    				
    			case UIFInstruction.STORE:
    				//Create the RHS Expression first 
    				Expression rhsExpression = createExpression ( ((UIFInstruction)uifList[index]).getSources()[0]);
    				Operand lhsOperand = (Operand)((UIFInstruction)uifList[index]).getResult(); 
    				statements.addStatement(new AssignmentStatement (UIFInstruction.STORE, (OperandExpression)createExpression(lhsOperand), rhsExpression));
    			    index++;
    				break;
    				
    			case UIFInstruction.RETURN:
    				if ( ((UIFInstruction)uifList[index]).getSources() == null )
    				{
    					statements.addStatement(new ReturnStatement(null,0));
    					index++;
    					break; 
    				}
    				Object thisInstr = ((UIFInstruction)uifList[index]).getSources()[0];
    				Expression retExpression;
    				if ( thisInstr instanceof UIFInstruction && ((UIFInstruction) thisInstr).getInstructionType() == UIFInstruction.IF )
    					retExpression = createConditionExpression ( ((ArrayList<Integer>)(((UIFInstruction)thisInstr).getSources()[4])).toArray(), 0);
    				else
    					retExpression = createExpression ( ((UIFInstruction)uifList[index]).getSources()[0]);
    				int isBoolean = ((Integer)(((UIFInstruction)uifList[index]).getSources()[1])).intValue();
    				statements.addStatement(new ReturnStatement(retExpression, isBoolean));
    				index++;
    				System.out.println ( "Added RETURN");
    				break;
    				
    			case UIFInstruction.INVOKE:
    			case UIFInstruction.CONSTRUCT:
    				if ( isOrphan ((UIFInstruction)uifList[index]) )
    					statements.addStatement(new MethodInvocationStatement((MethodInvocationExpression)createExpression(uifList[index])));
    				index++;
    				break;
    				
    			case UIFInstruction.INC:
    				if ( isOrphan((UIFInstruction)uifList[index]) )
    				{
    					sources = ((UIFInstruction)uifList[index]).getSources();
    					statements.addStatement( new LocalVariableIncStatement ( (OperandExpression)createExpression(sources[0]), (OperandExpression)createExpression(sources[1])));
    				}
        			index++;
         			break;
					
    			case UIFInstruction.IF:
    			case UIFInstruction.TERNARY_OP:
    				if ( !isOrphan((UIFInstruction)uifList[index]))
    				{
    					/*The parent will be in the same block. Just jump to the parent.
    					 * If we come across cases where parents lie in a different code block, we'll
    					 * probably have much work to do in this area!
    					 */
    					index = uifRep.pc2aidx(getParent((UIFInstruction)uifList[index]).getPc());
    					continue;
    				}
    				/* We also check for loops here */
    				/* This check is for while/for loops */
    				sources = ((UIFInstruction)uifList[index]).getSources();
    				startPC = ((Integer)(sources[5])).intValue();
    				endPC = ((Integer)(sources[6])).intValue();
    				
    				if ( uifRep.getUIFInstructionTypeFromPC(endPC) == UIFInstruction.GOTO
    						&& ((BranchDestination)(uifRep.getUIFInstructionFromPC(endPC).getSources()[0])).getPc() < endPC    // are we jumping back ?
    							&& uifRep.closestIFHead(((BranchDestination)(uifRep.getUIFInstructionFromPC(endPC).getSources()[0])).getPc()) == ((UIFInstruction)(uifList[index])).getPc() )
    				{
    					LoopStatement whileLoop = new LoopStatement ( LoopStatement.FOR_OR_WHILE );
    					ArrayList<Integer> rawIfExpr = (ArrayList<Integer>)sources[4];
    					ConditionExpression condExp = createConditionExpression ( rawIfExpr.toArray(),0 );
    					whileLoop.setExpression(condExp);
    					decompilingLoop = true;
    					tempLoopStart = startPC;
    					tempLoopEnd = endPC;
    					index = generateJavaCode ( whileLoop.getStatements(), uifRep.pc2aidx(startPC), uifRep.pc2aidx(uifRep.getPrevPc(endPC)));
    					decompilingLoop = false;
    					statements.addStatement(whileLoop);
    					continue;
    				}
    				IfElseStatement ifElseStat = new IfElseStatement (  );
    				startPC = -1;
    				endPC = -1;
    				gotoDestination = -1;
    				do
    				{
    					if ( ((UIFInstruction)uifList[index]).getInstructionType() == UIFInstruction.IF )
    					{	
    						sources = ((UIFInstruction)uifList[index]).getSources();
    						System.out.println ( ((UIFInstruction)(uifList[index])).getPc() );
    						ArrayList<Integer> rawIfExpr = (ArrayList<Integer>)sources[4];
    						startPC = ((Integer)(sources[5])).intValue();
    						endPC   = ((Integer)(sources[6])).intValue();
    						//debug
    						System.out.println ( "Raw Expr = ");
    						for ( int i = 0 ; i < rawIfExpr.size(); i++ )
    							System.out.print ( " " + rawIfExpr.get(i).intValue());
    						System.out.println();
    						//debug
    						ConditionExpression condExp = createConditionExpression ( rawIfExpr.toArray(),0 );
    						ifElseStat.setNextConditionExpression(condExp);
    						System.out.println ( "Generating IF from startPC = " + startPC + "to endPC = " + endPC );
    						index = generateJavaCode ( ifElseStat.getCurrentStatementList(), uifRep.pc2aidx(startPC), uifRep.pc2aidx(endPC) );
    						System.out.println ( "Finished generating IF. Returned index = " + index );
    						UIFInstruction lastInstr = uifRep.fetchUIFInstruction(uifRep.getPrevPc(((UIFInstruction)(uifList[index])).getPc()));
    						if ( lastInstr.getInstructionType() != UIFInstruction.GOTO )
    							break; //Do not misinterpret a successive IF to be an ELSE-IF branch
    						else if ( ((Integer)(lastInstr.getSources()[1])).intValue() == 0 )
    							break; //This is a backward jump
    						else if ( gotoDestination == -1 )
    							gotoDestination = ((BranchDestination)(lastInstr.getSources()[0])).getPc();
    						else if (((BranchDestination)(lastInstr.getSources()[0])).getPc() != gotoDestination )
    							break; //All else-if branches merge at the same destination. This scenario is hence not practical.
    					
    					}
    					else 
    					{
    						
    						//We are into the else! We exit from here!
    						startPC = uifRep.getNextPc(endPC);	//prev end pc + 1
    						endPC   = uifRep.getPrevPc(gotoDestination); // goto destination - 1
    						System.out.println ( "In ELSE endPC = " + endPC );
    						endPC   = uifRep.searchOrphanGOTO ( startPC, endPC );
    						System.out.println ( "Generating ELSE from startPC = " + startPC + " endPC = " + endPC );
    						index = generateJavaCode ( ifElseStat.getElseStatementList(), uifRep.pc2aidx(startPC), uifRep.pc2aidx(endPC) );
    						System.out.println ( "Finished generating ELSE. Returned index = " + index );
    						//index++;
    						break;
    					}		
    				}while ( true );
    				statements.addStatement( ifElseStat );
    				System.out.println ( "index = " + index );
    				break;
    			case UIFInstruction.GOTO:
    				UIFInstruction gotoInstr = (UIFInstruction)(uifList[index]);
    				int gotoInstrPC = gotoInstr.getPc();
    				int gotoBranchPC = ((BranchDestination)(gotoInstr.getSources()[0])).getPc(); 
    				if ( decompilingLoop )
    				{
    					System.out.println ( "BREAK OR CONTINUE to "  + uifRep.closestIFHead(gotoBranchPC));
    					System.out.println ( "INSTR" + gotoInstr.toString() );
    					System.out.println ( "EndPC " + tempLoopEnd ); 
    					System.out.println ( "StartPC " + tempLoopStart ); 
    					if ( gotoBranchPC == uifRep.getNextPc(tempLoopEnd) ) 
    					{
    						statements.addStatement(new BreakStatement());
    						System.out.println ( "Added BREAK");
    					}
    					else if ( ((Integer)(uifRep.getUIFInstructionFromPC(uifRep.closestIFHead(gotoBranchPC)).getSources()[5])).intValue() <= gotoInstrPC 
    							&& ((Integer)(uifRep.getUIFInstructionFromPC(uifRep.closestIFHead(gotoBranchPC)).getSources()[6])).intValue() >= gotoInstrPC )
    						
    					{
    						statements.addStatement(new ContinueStatement ( ));
    						System.out.println ( "Added CONTINUE");
    					}
    				}
    				
    				index++;
    				break;
    			default:  
    				index++;		
    		}
    	}     
    	return index;
    }
   
    
    private boolean isForwardBranch ( int pc )
    {
    	if ( uifRep.getUIFInstructionFromPC(pc).getInstructionType() == UIFInstruction.GOTO )
    	{
    		if ( ((Integer)(uifRep.getUIFInstructionFromPC(pc).getSources()[1])).intValue() == 1 )
    			return true;
    		return false;
    	}
    	return false;
    }
    
    private boolean isOrphan ( UIFInstruction instr )
    {
    	Iterator<UIFInstruction> uifIter = uifRep.getIterator();
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
    					return false;
    			}
    		}
    	}
    	return true;
    }
     
    
    private UIFInstruction getParent ( UIFInstruction instr )
    {
    	Iterator<UIFInstruction> uifIter = uifRep.getIterator();
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
    private ConditionExpression createConditionExpression ( Object [] rawExpression, int startIndex )
    {
    	if ( ((Integer)(rawExpression[startIndex])).intValue() == -3 )
    	{
    		int numOpen = 1;
    		ArrayList<Integer> subExpression = new ArrayList<Integer> ( );
    		while ( numOpen > 0 )
    		{
    			startIndex++;
    			if ( ((Integer)(rawExpression[startIndex])).intValue() == -3 )
    			{
    				subExpression.add( ( (Integer)(rawExpression[startIndex])));
    				numOpen++;
    			}
    			else if ( ((Integer)(rawExpression[startIndex])).intValue() == -4 )
    			{
    				numOpen--;
    				if ( numOpen > 0 )
    					subExpression.add(((Integer)(rawExpression[startIndex])));
    			}
    			else
    				subExpression.add(((Integer)(rawExpression[startIndex])));
    		}
    		
    		System.out.println ( subExpression.size());
    		ConditionExpression conExp1 = createConditionExpression ( subExpression.toArray(), 0);
    		conExp1.setBraces();
    		if ( startIndex < rawExpression.length - 1)
    		{
    			int compOp = ((Integer)(rawExpression[++startIndex])).intValue();
    			System.out.println ( " " + compOp );
    			Expression conExp2 = createConditionExpression ( rawExpression, ++startIndex );
    			return new ConditionExpression ( conExp1, compOp, conExp2 );
    		}		
    		else
    			return new ConditionExpression ( conExp1 );
    	}
    	else
    	{
    		    int pc = ((Integer)(rawExpression[startIndex])).intValue();
    		    System.out.println ( "pc = " + pc );
    			Expression conExp1 = createExpression ( uifRep.getUIFInstructionFromPC(pc));
    			if ( startIndex < rawExpression.length - 1)
    			{
    				int compOp = ((Integer)(rawExpression[++startIndex])).intValue();
    				System.out.println ( " " + compOp );
    				Expression conExp2 = createConditionExpression ( rawExpression, ++startIndex );
    				return new ConditionExpression ( conExp1, compOp, conExp2 );
    			}
    			else
    				return new ConditionExpression ( conExp1 );
    	}
    		
    }
    
    private Expression createExpression ( Object source )
    {
    	if ( source instanceof Operand )
    		return new OperandExpression ((Operand)source );
    	
    	if ( source instanceof String )
    		return new OperandExpression ( new Constant(source));
    	
    	int instrType = ((UIFInstruction)source).getInstructionType();
    	((UIFInstruction)source).setVisited();
    	Object source1, source2;
    	switch ( instrType )
    	{
    		case UIFInstruction.ADD:
    		case UIFInstruction.SUB:
    		case UIFInstruction.MUL:
    		case UIFInstruction.DIV:
    		case UIFInstruction.MOD:
    			source1 = ((UIFInstruction)source).getSources()[0];
    			source2 = ((UIFInstruction)source).getSources()[1];
    		    return new ArithmeticExpression ( instrType, createExpression(source1), createExpression(source2));
    		    
    		case UIFInstruction.INC:
    			/* We shall come here only when increment is 1 or -1 */
    			source1 = ((UIFInstruction)source).getSources()[0];
    			source2 = ((UIFInstruction)source).getSources()[1];
    			int incValue = ((Integer)(((Constant)source2).getConstantObj())).intValue();
    			if ( incValue == 1 )
    				return new IncDecExpression ( createExpression(source1), false, true);
    			else if ( incValue == -1 )
    				return new IncDecExpression ( createExpression(source1), false, false);
    			
    		case UIFInstruction.NEG:
    			source1 = ((UIFInstruction)source).getSources()[0];
    			return new UnaryExpression ( instrType, createExpression ( source1));
    			
    		case UIFInstruction.AND:
    		case UIFInstruction.OR:
    		case UIFInstruction.XOR:
    		case UIFInstruction.SHIFTL:
    		case UIFInstruction.SHIFTR:
    		case UIFInstruction.SHIFTRU:
    			source1 = ((UIFInstruction)source).getSources()[0];
    			source2 = ((UIFInstruction)source).getSources()[1];
    		    return new BitwiseExpression ( instrType, createExpression(source1), createExpression(source2));
    		    
    		case UIFInstruction.INVOKE:
    			Object sources[] = ((UIFInstruction)source).getSources();
    			ExpressionList exprList = new ExpressionList ( );
    			for ( int i = 0 ; i < sources.length-1; i++ )
    			    exprList.addExpression(createExpression( sources[i]));	
    			boolean isStatic = (((UIFInstruction)source).getBytecode() == Bytecodes.invokestatic);
    		    return new MethodInvocationExpression ( (OperandExpression)(createExpression(sources[sources.length-1])), exprList, isStatic );
    		    
    		case UIFInstruction.OBJCREATE:
    			sources = ((UIFInstruction)source).getSources();
    			return new OperandExpression ((Operand)sources[0]);
    			
    		case UIFInstruction.CONSTRUCT: 
    			sources = ((UIFInstruction)source).getSources();
    			exprList = new ExpressionList ( );
    			if (!(sources[0] instanceof UIFInstruction))
    			{
    				for ( int i = 0 ; i < sources.length-1; i++ )
    					exprList.addExpression(createExpression( sources[i]));
    				return new MethodInvocationExpression ( (OperandExpression)(createExpression(sources[sources.length-1])), exprList, false );
    			}
    			else
    			{  
    				for ( int i = 1 ; i < sources.length-1; i++ )
    					exprList.addExpression(createExpression( sources[i]));
    				return new ObjectCreateExpression ((OperandExpression)(createExpression(sources[0])), exprList );
    			}
    			
    			
    		case UIFInstruction.ARRCREATE:		
    			sources = ((UIFInstruction)source).getSources();
    			exprList = new ExpressionList ( );
    			for ( int i = 1 ; i < sources.length ; i++ )
    			    exprList.addExpression(createExpression( sources[i]));
    			return new ArrayCreateExpression (((OperandExpression)createExpression(sources[0])), exprList );
    			
    		case UIFInstruction.RDARRAY:
    			ExpressionList dimExprList = new ExpressionList ( );
    			Expression baseExpression = collectDimensionExpressions ( dimExprList, (UIFInstruction)source);
    			return new ArrayOperandExpression ( baseExpression, dimExprList );
    			
    		case UIFInstruction.RDFIELD:
    			source1 = ((UIFInstruction)source).getSources()[0];
    			if (((UIFInstruction)source).getSources().length == 2 )
    			{
    				source2 = ((UIFInstruction)source).getSources()[1];
    				return new ReadExpression ( UIFInstruction.RDFIELD, (OperandExpression)createExpression(source1), (OperandExpression)createExpression(source2));
    			}	
    			else
    			    return new ReadExpression ( UIFInstruction.RDFIELD, null, (OperandExpression)createExpression(source1));
    			
    		case UIFInstruction.TYPECAST:
    			OperandExpression classRef = (OperandExpression)createExpression(((UIFInstruction)source).getSources()[0]);
    			Expression castExpression = createExpression(((UIFInstruction)source).getSources()[1]);
    			return new TypecastExpression ( classRef, castExpression );
    			
    		case UIFInstruction.ARRLENGTH:
				Object arrSource = ((UIFInstruction)source).getSources()[0];
				return new ArrayLengthExpression ( createExpression(arrSource));
				
    		case UIFInstruction.IF:
    			Object ifSrcs [] = ((UIFInstruction)source).getSources();
    			Object lhsExpr = ifSrcs[1];
    			int compOp = ((Integer)ifSrcs[0]).intValue();
    			
    			if ( lhsExpr instanceof UIFInstruction  && ((UIFInstruction) lhsExpr).getInstructionType() == UIFInstruction.COMPARE)
    			{
    				Object [] compareSources = ((UIFInstruction)lhsExpr).getSources();
    				lhsExpr = compareSources[0];
    				Object rhsExpr = compareSources[1];
    				return new ComparisonExpression ( createExpression(lhsExpr), compOp, createExpression(rhsExpr));
    			}
    			else
    				return new ComparisonExpression ( createExpression(lhsExpr), compOp, createExpression(ifSrcs[3]));
    		case UIFInstruction.INSTANCEOF:
    			Object ioSrcs []  = ((UIFInstruction)source).getSources();
    			compOp = 6;
    			lhsExpr = ioSrcs[1];
    			Object rhsExpr = ioSrcs[0];
    			return new ComparisonExpression ( createExpression(lhsExpr), compOp, createExpression(rhsExpr));
    			
    		case UIFInstruction.TERNARY_OP:
    			Object [] topSources = ((UIFInstruction)source).getSources();
    			ArrayList<Integer> rawIfExpr = (ArrayList<Integer>)topSources[4];
                ((UIFInstruction)source).setInstructionType(UIFInstruction.IF);
                System.out.println ( "E:" + source.toString());
    			Expression retExpr = new TernaryOperatorExpression (createConditionExpression(rawIfExpr.toArray(),0), createExpression(topSources[5]), createExpression(topSources[6]));
    			((UIFInstruction)source).setInstructionType(UIFInstruction.TERNARY_OP);
    			return retExpr;
    	}
    	return null;
    }
    

    private Expression collectDimensionExpressions ( ExpressionList exprList, UIFInstruction source )
    {
    	Object sources[] = source.getSources();
    	exprList.addExpression(createExpression(sources[1]));
    	if ( sources[0] instanceof UIFInstruction 
    			&& (((UIFInstruction)sources[0]).getInstructionType() == UIFInstruction.RDARRAY 
    					||((UIFInstruction)sources[0]).getInstructionType() == UIFInstruction.WRARRAY))
    		return collectDimensionExpressions ( exprList, (UIFInstruction)sources[0]);
    	else
    		return createExpression(sources[0]);		
    }
    
    public String toString ( )
    {
    	return "\n\n\n" + javaMethod.genJavaCode();
    }
}
