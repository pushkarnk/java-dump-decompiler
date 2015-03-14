package jdd.dumpreader;




/*This interface will have implementations specific to the dump reading APIs*/

public interface DumpReader {
	
	//This method needs to populate the MethodDataCollector object
	public abstract int readDump(long j9methodAddr, Object context, MethodDataCollector decompilerData ) throws Exception;
            
}

