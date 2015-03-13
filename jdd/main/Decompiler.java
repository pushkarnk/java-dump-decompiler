package jdd.main;

import java.io.PrintStream;

import com.ibm.j9ddr.debugextensions.vm24.jdd.dumpreader.DumpReader;
import com.ibm.j9ddr.tools.ddrinteractive.Context;
import com.ibm.j9ddr.tools.ddrinteractive.DDRInteractiveCommandException;


public final class Decompiler {
	
	private static Decompiler singletonInstance = null;


	
	public static Decompiler getDecompiler ( )		//ensuring that there is only one Decompiler instance 
	{
		if ( singletonInstance == null )
		{
			singletonInstance = new Decompiler ( );
		}
		return singletonInstance;
	}

	public void decompile  ( long methodDataAddr, Context context, PrintStream out ) throws DDRInteractiveCommandException
	{
		//Create an empty Typed Intermediate Form (TIF)object
		//Send it across to the DumpReader, which will populate it all the required info and send it back
		//Apply the "Data Object Duplicates' Elimination" algorithm on the TIF. A compact TIF results
		//Translate the TIF to a Untyped Information Form (UIF) object
		//Translate the UIF to Java

		/*Read contents of the dump into a TIF form */
		TypedIntermediateForm TIFObject = TypedIntermediateForm.getTIFObject();
		DumpReader dumpReader = DumpReader.getDumpReader();
		dumpReader.readDump(methodDataAddr, context, TIFObject);
		
		TIFObject.formOperandLinkages();
		TIFObject.dumpTIFGraph();
		
		/*Convert the TIF representation to a UIF representation */
		UntypedIntermediateForm UIFObject = new UntypedIntermediateForm ( );
		UIFObject.createUIFGraph(TIFObject);
		UIFObject.dumpUIFGraph();
	   
		/*Generate Java code using the UIF representation*/
		CodeGenerator codeGen = new CodeGenerator ( UIFObject );
		codeGen.generateJavaCode ( );
		System.out.println ( codeGen.toString());
	}
}
