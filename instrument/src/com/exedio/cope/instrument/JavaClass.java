
package com.exedio.cope.instrument;

import java.lang.reflect.Modifier;

/**
 * Represents a class parsed by the java parser.
 * Is an inner class, if parent is not null.
 * @see #getParent()
 * @see Injector
 */
public class JavaClass extends JavaFeature
{

	/**
	 * @param parent may be null for non-inner classes
	 */
	public JavaClass(JavaFile file, JavaClass parent, int modifiers, String name)
	throws InjectorParseException
	{
		super(file, parent, modifiers, null, name);
	}
	
	/**
	 * Constructs the fully qualified name of this class,
	 * including package path.
	 */
	public String getFullName()
	{
		StringBuffer buf=new StringBuffer();
		String packagename=getPackageName();
		if(packagename!=null)
		{
			buf.append(packagename);
			buf.append('.');
		}
		int pos=buf.length();
		for(JavaClass i=this; i!=null; i=i.getParent())
		{
			if(i!=this)
				buf.insert(pos, '$');
			buf.insert(pos, i.getName());
		}
		return buf.toString();
	}
	
	/**
	 * Constructs the fully qualified name of this class,
	 * including package path.
	 * The same as {@link #getFullName()}, but without
	 * dots and dollars, so that this string can be used
	 * as part of a java identifier.
	 */
	public String getFullNameEscaped()
	{
		StringBuffer buf=new StringBuffer();
		String packagename=getPackageName();
		if(packagename!=null)
		{
			buf.append('_');
			for(int i=0; i<packagename.length(); i++)
			{
				char c=packagename.charAt(i);
				if(c=='.')
					buf.append('_');
				else
					buf.append(c);
			}
		}
		int pos=buf.length();
		for(JavaClass i=this; i!=null; i=i.getParent())
		{
			buf.insert(pos, i.getName());
			buf.insert(pos, '_');
		}
		return buf.toString();
	}
	
	public final boolean isInterface()
	{
		return (getModifiers() & Modifier.INTERFACE) > 0;
	}
	
	public final int getAllowedModifiers()
	{
		return
		Modifier.INTERFACE |
		Modifier.PUBLIC |
		Modifier.PROTECTED |
		Modifier.PRIVATE |
		Modifier.FINAL |
		Modifier.STATIC |
		Modifier.ABSTRACT;
	}
	
	public final void printMore(java.io.PrintStream o)
	{
		o.println("    package: >"+getPackageName()+"<");
		o.println("    fullnam: >"+getFullName()+"<");
	}
	
}
