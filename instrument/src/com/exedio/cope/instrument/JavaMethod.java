
package com.exedio.cope.instrument;

/**
 * Represents a method of a class parsed by the java parser.
 * @see Injector
 */
final class JavaMethod extends JavaBehaviour
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
	
}
