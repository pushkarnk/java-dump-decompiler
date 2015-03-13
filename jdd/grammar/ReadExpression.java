package jdd.grammar;

import jdd.instructions.UIFInstruction;
import jdd.operands.FieldRef;

public class ReadExpression extends Expression {
	
	int typeRead; //RDFIELD, RDARRAY
	int bytecode;
	OperandExpression receiverObject; //LocalVariable, ArrayElement
	OperandExpression operandField; //FieldRef
	
	public ReadExpression ( int typeRead, OperandExpression receiver, OperandExpression field)
	{
		operandField = field;
		receiverObject = receiver;
		this.typeRead = typeRead;
	}
	
	public String genJavaCode ( boolean hasBraces )
	{
		if ( typeRead == UIFInstruction.RDFIELD )
		{
			if ( receiverObject == null ) //static
				return ((FieldRef)operandField.getOperand()).getClassName() + "." + operandField.genJavaCode(false);
			else
			{
			    /*local variable, field, array element*/
				if ( receiverObject.genJavaCode(false) == "this")
					return operandField.genJavaCode(false);
				return receiverObject.genJavaCode(false) + "." +  operandField.genJavaCode(false);
			}
		}
		return "Error";
	}
}
