
package injection;

/**
 * Thrown by the java parser, if the input stream is not valid
 * java language. Should never be thrown on java code, which passes
 * javac sucessfully (otherwise it's a bug.)
 * @see Injector
 */
public class InjectorParseException extends Exception
{
	protected InjectorParseException(String message)
	{
		super(message);
	}
}
