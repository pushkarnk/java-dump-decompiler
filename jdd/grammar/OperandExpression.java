package jdd.grammar;

import jdd.operands.ClassRef;
import jdd.operands.Constant;
import jdd.operands.FieldRef;
import jdd.operands.LocalVariable;
import jdd.operands.MethodRef;
import jdd.operands.Operand;

/*A wrapper around Operand*/

public class OperandExpression extends Expression {
	
	Operand operand;

	public OperandExpression ( Operand opr )
	{
		operand = opr;
	}
	
	public Operand getOperand ( )
	{
		return operand;
	}
	
	public String genJavaCode( boolean hasBraces )
	{
		if ( operand instanceof ClassRef )
		{
			String [] packageStr = ((ClassRef)operand).getClassName().split("/");
			return appendBrackets(packageStr[0], packageStr[packageStr.length-1].split(";")[0]);
		}
		else if ( operand instanceof Constant )
		{
			return ((Constant) operand).getConstant();
		}
		else if ( operand instanceof FieldRef )
		{
			return ((FieldRef) operand).getFieldName();
		}
		else if ( operand instanceof LocalVariable)
		{
			return ((LocalVariable) operand).getName();
		}
		else if ( operand instanceof MethodRef )
		{
			return ((MethodRef) operand).getName();
		}
		else
			return "Error";
	}
	
	private String appendBrackets ( String strStart, String cName )
	{
		int index = 0;
		StringBuffer cNameBuff = new StringBuffer(cName);
		while ( strStart.charAt(index) == '[')
		{
			index++;
			cNameBuff.append("[]");
		}
		return cNameBuff.toString();
	}
}
