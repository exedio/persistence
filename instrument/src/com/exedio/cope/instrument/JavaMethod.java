
package injection;

import java.util.*;
import java.io.PrintStream;

/**
 * Represents a method of a class parsed by the java parser.
 * @see Injector
 */
public final class JavaMethod extends JavaBehaviour
{
	
	public JavaMethod(JavaClass parent, int modifiers, String type, String name)
	throws InjectorParseException
	{
		// parent must not be null
		super(parent, modifiers, type, name);
		
		if(type==null)
			throw new RuntimeException();
	}
	
	/**
	 * A cache for getSignature.
	 * @see #getSignature
	 */
	private String signature;
	
	/**
	 * Returns the signature of this method.
	 */
	private String getSignature()
	{
		if(signature!=null)
			return signature;
		StringBuffer buf=new StringBuffer();
		buf.append(getName());
		buf.append('(');
		for(Iterator i=parameters.iterator(); i.hasNext(); )
		{
			buf.append((String)i.next());
			i.next();
			if(i.hasNext()) buf.append(',');
		}
		buf.append(')');
		return buf.toString();
	}
	
	/**
	 * See Java Specification 8.4.3 &quot;Method Modifiers&quot;
	 */
	public final int getAllowedModifiers()
	{
		return
		java.lang.reflect.Modifier.PUBLIC |
		java.lang.reflect.Modifier.PROTECTED |
		java.lang.reflect.Modifier.PRIVATE |
		java.lang.reflect.Modifier.FINAL |
		java.lang.reflect.Modifier.STATIC |
		java.lang.reflect.Modifier.ABSTRACT |
		java.lang.reflect.Modifier.NATIVE |
		java.lang.reflect.Modifier.SYNCHRONIZED;
	}
	
	public final void printMore(PrintStream o)
	{
		super.printMore(o);
		System.out.println("    signatr: >"+getSignature()+"<");
	}
	
}
