package jdd.main;


/*This class is an important part of the interface between the front-end and back-end.*/
/*The front-end dispatches data through this interface.The back-end implements this interface.*/
public interface MethodDataCollector
{
    void setNameAndSignature ( String name, String signature );
    void setMaxStack ( int maxStack );
    void setArgumentsAndTempCount ( int arguments, int temp );
    void setClassName ( String className );
    void setModifiers ( unsigned int modifiers ); 
    void addACaughtExceptionType ( int start, int end, int handler, String exceptionName );
    void addAThrownException ( String exceptionName );
    void addAJVMInstruction ( int byteCode, int bcType, int bcLength, int pc , Object[] operands );
}
