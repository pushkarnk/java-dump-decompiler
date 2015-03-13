package jdd.main;


import com.ibm.j9ddr.debugextensions.vm24.jdd.grammar.RandomNameGenerator;
import com.ibm.j9ddr.vm24.types.U32;

public class MethodSignature {
	
	String returnType;
	String fullQualifiedReturnType;
	String methodName;
	String className;
	String [] argumentTypes;
	String [] fullQualifiedArgTypes;
	String [] argumentNames;
	String modifierString;
	boolean isStatic;
	
	public MethodSignature ( MethodMetaData methodData )
	{
		isStatic = false;
		setModifierString ( methodData.getModifiers());
		methodName = methodData.getName();
		className  = methodData.getClassName();
		fullQualifiedArgTypes = new String [methodData.getArgumentCount()];
		argumentTypes = new String [methodData.getArgumentCount()];
		argumentNames = new String [methodData.getArgumentCount()];
		setArgumentTypeArray ( methodData.getSignature());
		setArgumentNames ( );
		setReturnType ( methodData.getSignature());
		
	}
	
	public String [] getFullQualifiedArgumentArray ( )
	{
		return fullQualifiedArgTypes;
	}
	
	public String [] getShortArgumentTypes ( )
	{
		return argumentTypes;
	}
	
	public String [] getArgumentNames ( )
	{
		return argumentNames;
	}
	
	public String toString ( )
	{
		StringBuffer fullSign = new StringBuffer ( );
		fullSign.append(modifierString);
		if ( !methodName.equals("<init>"))
			fullSign.append(returnType + " ");
		if (methodName.equals("<init>"))
		{
			String [] clStr = className.split("/");
			String shortName = clStr[clStr.length-1];
			fullSign.append( shortName );
		}
		else
			fullSign.append( methodName);
		fullSign.append( "( ");
		int start;
		if ( isStatic )
			start = 0;
		else
			start =	1 ;
		if ( start >= argumentTypes.length )
		{
			fullSign.append( " )");
			return fullSign.toString();
		}
		fullSign.append( argumentTypes[start]);
		fullSign.append (" " + argumentNames[start++]);
		if ( argumentTypes[start-1] == "long" || argumentTypes[start-1] == "double" )
			start++;
		for ( int i = start; i < argumentTypes.length ; i++ )
		{
			/*Since the DebugInfo is not available for now, let us generate random names*/
			fullSign.append(", ");
			fullSign.append(argumentTypes[i]);
			fullSign.append (" " + argumentNames[i]);
			if ( argumentTypes[i] == "long" || argumentTypes[i] == "double" )
				i++;
		}
		fullSign.append( " )");
		return fullSign.toString();
			
	}

	
	void setModifierString ( U32 modifier )
	{
		   int ACC_PUBLIC    = 0x1;
		   int ACC_PRIVATE   = 0x2;
		   int ACC_STATIC    = 0x8;
		   int ACC_FINAL     = 0x10;
		   int ACC_SYNCHRONIZED = 0x20;
		   StringBuffer modifierString = new StringBuffer ( );
	      
	       if ( modifier.bitAnd(ACC_PUBLIC).intValue() != 0 )
	    	   modifierString.append( "public ");
	       else if ( modifier.bitAnd(ACC_PRIVATE).intValue() != 0 )
	    	   modifierString.append( "private ");
	       else
	    	   modifierString.append("protected ");
	       
	       if( modifier.bitAnd(ACC_STATIC).intValue() != 0 )
	       {
	    	   modifierString.append("static ");
	    	   isStatic = true;
	       }
	       if (modifier.bitAnd(ACC_FINAL).intValue()!= 0 )
	    	   modifierString.append("final ");
	       if (modifier.bitAnd(ACC_SYNCHRONIZED).intValue() != 0 )
	    	   modifierString.append("synchronized ");
	    
	       this.modifierString = modifierString.toString(); 
	}
	void setArgumentTypeArray ( String signature )
	{
		int index = 0;
		int count = 0;
		if ( !isStatic )
		{
		   fullQualifiedArgTypes[count] = className;
		   String [] splitNames = className.split("/");
		   String shortName = splitNames[splitNames.length-1];
		   argumentTypes[count] = shortName;
		   argumentNames[count++] = "this";
		}
		while ( signature.charAt(index) != ')')
		{
			switch ( signature.charAt(index))
			{
			    case 'B' :
			    case 'D' :
			    case 'C' :
			    case 'F' :
			    case 'I' :
			    case 'J' :
			    case 'S' :
			    case 'Z' : 
			    	       String typeString = getTypeString (signature.charAt(index));
			    	       fullQualifiedArgTypes[count] = typeString;
			    	       argumentTypes[count] = typeString;
			    	       /* Longs and doubles occupy two LV slots */
			    	       if ( argumentTypes[count] == "long" || argumentTypes[count] == "double")
			    	    	   count ++;
			    	       count++;
			               index++;
			               break;
			    case '[' : index = addArrayType ( signature, index , count, false );
			               count++;
			               break;
			    case 'L' : 
			    	       index = addClassType ( signature, index, count, false );
			               count++;
			               break;
			    case '(': index++;
			                
			}
		}
	}
	
	private void setArgumentNames ( )
	{
		int start;
		if ( isStatic )
			start = 0;
		else
			start = 1;
		System.out.println (argumentNames.length);
	    for ( int i = start; i < argumentNames.length; i++ )
	    {
	    	argumentNames[i] = RandomNameGenerator.generateName(argumentTypes[i]);
	    	if ( argumentTypes[i] == "long" || argumentTypes[i] == "double")
	    		i++;
	    }		
	    
	}
	
	private int addArrayType ( String signature, int index, int count, boolean forReturnType )
	{
		StringBuffer dims = new StringBuffer ( );
		StringBuffer name = new StringBuffer ( ); 
		while ( signature.charAt(index) == '[' )
		{
			dims.append(signature.charAt(index));
			dims.append(']');
			index++; //multi-dimensional array
			
		}
		if ( signature.charAt(index) == 'L')
		{
			index++;
			while ( signature.charAt(index) != ';') //its an object array
			{
				name.append(signature.charAt(index));
				index++;
			}	
		}
		else
			name = new StringBuffer(getTypeString(signature.charAt(index)));
	
		String dimsString = new String ( dims );
		String className = new String (name);
		String shortName;
		if ( className.contains("/"))
		{
			String [] splitNames = className.split("/");
			shortName = splitNames[splitNames.length-1];
		}
		else
		{
			shortName = className;
		}
		if ( forReturnType )
		{
			returnType = shortName.concat(dimsString);
			fullQualifiedReturnType = className.concat(dimsString);
			return 0;
		}
		fullQualifiedArgTypes[count] = className.concat(dimsString);
		argumentTypes[count] = shortName.concat(dimsString);
		return index+1;
	}
	
	private int addClassType ( String signature, int index, int count, boolean forReturnType )
	{
		index++;
		StringBuffer fullName = new StringBuffer ( );
		while ( signature.charAt(index) != ';')
		{	
			fullName.append(signature.charAt(index));
			index++;
		}
		String className = fullName.toString();
		String [] splitNames = className.split("/");
		String shortName = splitNames[splitNames.length-1];
		if ( forReturnType )
		{
			returnType = shortName;
			fullQualifiedReturnType = className;
			return 0;
		}
		fullQualifiedArgTypes[count] = className;
		argumentTypes[count] = shortName;
		return index+1;
	}
	
	private void setReturnType ( String signature )
	{
		int index = 0;
		while ( signature.charAt(index) != ')' )
			index++;
		index++;
		switch ( signature.charAt(index))
		{
		    case 'B' :
		    case 'D' :
		    case 'C' :
		    case 'F' :
		    case 'I' :
		    case 'J' :
		    case 'S' :
		    case 'Z' : 
		    case 'V' :
		    		   returnType = getTypeString (signature.charAt(index));
		               break;
		    case '[' : addArrayType ( signature, index , 0, true );
		               break;
		    case 'L' : addClassType ( signature, index, 0, true );
		               break;
		}
	}
	
	private String getTypeString ( char c )
	{
	    switch ( c )
	    {
	    	case 'B' : return "byte";
	    	case 'D' : return "double";
	    	case 'C' : return "char";
	    	case 'F' : return "float";
	    	case 'I' : return "int";
	    	case 'J' : return "long";
	    	case 'S' : return "short";
	    	case 'Z' : return "boolean";
	    	case 'V' : return "void";
	    }
	    return "error";
	    	
	}
	
	/*private boolean isPrimitiveType ( String className )
	{
		if ( className.equals("int") || className.equals("short") || className.equals("boolean")
				|| className.equals( "float") || className.equals("double") || className.equals("char")
				    || className.equals("long") || className.equals("short") || className.equals("byte") )
		return true;
		return false;
	}*/
}


