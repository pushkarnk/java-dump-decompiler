package jdd.main;

import java.util.LinkedList;


public class MethodMetaData {
	private String name;
	private String className;
	private String signature;
	private int    maxStack;
	private int argumentCount;
	private int tempCount;
	private LinkedList<CaughtExceptionData> caughtExceptions;
	private LinkedList<String> thrownExceptions;
	int modifiers;
	
	public MethodMetaData () 
	{
		caughtExceptions = new LinkedList<CaughtExceptionData> ( );
		thrownExceptions = new LinkedList<String> ( );
	}
	
	
	public int getModifiers ( )
	{
		return modifiers;
	}
	
	public void setModifiers ( U32 modifiers )
	{
		this.modifiers = modifiers;
	}
	
	public String getClassName ( )
	{
		return className;
	}
	
	public void setClassName ( String className )
	{
	    this.className = className;
	}
	
	public String getName ( )
	{
		return name;
	}
	
	public String getSignature ( )
	{
		return signature;
	}
	
	public void setNameAndSignature ( String methodName, String methodSign )
	{
		name = methodName;
		signature = methodSign;
	}
	
	public int getMaxStack ( )
	{
		return maxStack;
	}
	
	public void setMaxStack( int maxStack )
	{
		this.maxStack = maxStack;
	}
	
	public int getArgumentCount ( )
	{
		return argumentCount;
	}
	
	public int getTempCount ( )
	{
		return tempCount;
	}
	
	public void setArgumentAndTempCount ( int argc, int tempc )
	{
		argumentCount = argc;
		tempCount = tempc;
	}
	
	public void addAThrownException ( String exceptionName )
	{
		thrownExceptions.add(exceptionName);
	}
	
	public void addACaughtException ( int start, int end, int handler, String name )
	{
		caughtExceptions.add(new CaughtExceptionData ( start, end, handler, name));
	}
	
	public String[] getAllThrownExceptions ( )
	{
		return (String[])thrownExceptions.toArray();
	}
	
	public CaughtExceptionData[] getAllCaughtExceptions ( )
	{
		return ( CaughtExceptionData[])caughtExceptions.toArray();
	}
}

class CaughtExceptionData
{
	int startPC;
	int endPC;
	int handlerPC;
	String exceptionName;
	
	public CaughtExceptionData ( int start, int end, int handler, String name )
	{
		startPC = start;
		endPC = end;
		handlerPC = handler;
		exceptionName = name;
	}
} 

