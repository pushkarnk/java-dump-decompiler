package jdd.dumpreader;

/*
 *
 *
 * This code has been stripped off to maintain confidentiality.
 *
 *
 *
 *
 */



/*This class is specific to the Direct Dump Reader API*/
/*It has call-backs to the back-end which are based on the MethodDataCollector interface*/
/*Data extraction may be done by other means. However, the call-backs should be suitably placed*/

import java.io.PrintStream;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;



public class DumpReader {
    
	private PrintStream outHandle = null;
	private boolean bigEndian = false;
	private Context context;
	private static DumpReader dumpReader = null;
	
	private DumpReader ( )
	{
		
	}
	public static DumpReader getDumpReader ( )
	{
		if ( dumpReader == null )
			dumpReader = new DumpReader ( );
		return dumpReader;
	}

	public int readDump(long j9methodAddr, Context context, MethodDataCollector decompilerData )
			throws Exception {
                
            //this method needs to populate the MethodDataCollector object
		
        }		
	
}

