package jdd.instructions;

import jdd.dumpreader.ByteCodeTable;
import jdd.operands.LocalVariable;
import jdd.operands.Operand;
import jdd.operands.StackSlot;

/* JVM Instructions with one explicit operand*/
public class JVMInstructionUnary extends JVMInstruction implements Cloneable{
		private Operand op;
		
		public JVMInstructionUnary ( Operand result, Operand op, int opCode, int length, int pc )
		{
			super( result, opCode,length, pc );
			setOperand ( op );
		}
		
		public void setOperand ( Operand op )
		{
			this.op = op;
		}
		
		public Operand getOperand ( )
		{
			return op;
		}
		
		public String toString ( )
		{
			String jvmInstr = "+" + pc + "\n" + ByteCodeTable.getBytecode(opCode);
			jvmInstr += "\nO:" + op.toString();
			if ( resultOperand != null )
			    jvmInstr += "\nR:" + resultOperand.toString();
			if ( extra != null )
				jvmInstr += "\nExtra:" + extra.toString();
			return jvmInstr;
		}

		@Override
		public void linkSourceOperands() {
			if ( op instanceof StackSlot )
				((StackSlot)op).setConsumer(this);
			else if ( op instanceof LocalVariable )
				((LocalVariable)op).setConsumer(this);
			
		}
		
		public Operand[] getSourceOperands ( )
		{
			return new Operand[] {op};
		}
		
		public Object clone ( )
		{
			return new JVMInstructionUnary ( resultOperand, op, opCode, length, pc );
		}
}
