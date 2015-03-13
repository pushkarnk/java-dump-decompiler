package jdd.grammar;

public class RandomNameGenerator {
	
static int iterator = 97;
	
	public static String generateName ( String className )
	{
		boolean isArray = false;
		StringBuffer sBuff = new StringBuffer ( );
		if ( className.contains("["))
		{
			className = chopBrackets ( className );
			isArray = true;
		}
		System.out.println ( className );
		if ( isPrimitiveType ( className ))
			return generateNameFromIterator ( className.substring(0,3), isArray );
		sBuff.append( (char)iterator );
		iterator++;
		if ( iterator == 123 )
			iterator = 65;
		if ( iterator == 91 )
			iterator = 97;
		if ( isArray )
			sBuff.append( "Arr");
		sBuff.append( "Obj");
		System.out.println ( "className = " + className );
		sBuff.append( className.subSequence(0, 1) );
		return sBuff.toString();
	}
	
	static boolean isPrimitiveType ( String className )
	{
		if ( className.equals("int") || className.equals("short") || className.equals("boolean")
				|| className.equals( "float") || className.equals("double") || className.equals("char")
				    || className.equals("long") || className.equals("short") || className.equals("byte") )
		return true;
		return false;
	}
	
	static String generateNameFromIterator ( String className , boolean isArray )
	{
		/*Can generate only 52 names for each type */
		StringBuffer nBuff = new StringBuffer ( );
		nBuff.append( className.charAt(0));
		StringBuffer buf = new StringBuffer(nBuff.toString());
		if ( isJavaKeyword(buf.append((char)iterator).toString()))
			iterator++;
		nBuff.append( (char)iterator );
		if ( isArray )
			nBuff.append ( "Arr");
		iterator++;
		if ( iterator == 123 )
			iterator = 65;
		if ( iterator == 91 )
			iterator = 97; 
		return nBuff.toString();
	}
	
	static boolean isJavaKeyword ( String s )
	{
		if  ( s.equals("if") || s.equals ("do") || s.equals("for") || s.equals("while") || s.equals("break") || s.equals("continue") )
			return true;
		return false;
	}
	
	static String chopBrackets ( String className )
	{
		StringBuffer newName = new StringBuffer ( );
		char [] oldName = className.toCharArray();
		int index = 0;
		while ( index < oldName.length )
			if ( oldName[index] != '[' && oldName[index] != ' ' ) 
				newName.append ( oldName[index++]);
			else
				index++;
		return newName.toString();
	}

}
