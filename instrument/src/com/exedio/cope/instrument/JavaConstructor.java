
package com.exedio.cope.instrument;

/**
 * Represents a constructor of a class parsed by the java parser.
 * @see Injector
 */
public final class JavaConstructor extends JavaBehaviour
{
	public JavaConstructor(
						final JavaClass parent,
						final int modifiers,
						final String name)
	throws InjectorParseException
	{
		super(parent, modifiers, null, name);
	}
	
	/**
	 * See Java Language Specification 8.6.3
	 * &quot;Constructor Modifiers&quot;
	 */
	public final int getAllowedModifiers()
	{
		return
		java.lang.reflect.Modifier.PUBLIC |
		java.lang.reflect.Modifier.PROTECTED |
		java.lang.reflect.Modifier.PRIVATE;
	}
	
}
