
package com.exedio.cope.instrument;

import java.io.PrintStream;
import java.lang.reflect.Modifier;

/**
 * Represents a java feature.
 * May be a class (even an inner class), an attribute or
 * a method.
 */
public abstract class JavaFeature
{
	public static final int ACCESS_PUBLIC = 0;
	public static final int ACCESS_PACKAGE = 1;
	public static final int ACCESS_PROTECTED = 2;
	public static final int ACCESS_PRIVATE = 3;
	

	/**
	 * The java file, which contains this feature.
	 * Must not be null.
	 */
	private final JavaFile file;
	
	/**
	 * The class, which contains this feature.
	 * Is null for top-level (not inner) classes.
	 */
	private final JavaClass parent;
	
	/**
	 * The modifiers of this feature.
	 * @see java.lang.reflect.Modifier
	 */
	private final int modifiers;
	
	final int accessModifier;
	
	/**
	 * The return type of the method.
	 * Is null, if it is a constructor, or a class.
	 */
	protected final String type;
	
	protected final String name;
	
	public JavaFeature(JavaFile file,
	JavaClass parent,
	int modifiers,
	String type,
	String name)
	throws InjectorParseException
	{
		this.file=file;
		this.parent=parent;
		this.modifiers=modifiers;
		this.accessModifier=toAccessModifier(modifiers);
		this.type=type;
		this.name=name;
		
		if(file==null)
			throw new RuntimeException();
		
		if(parent!=null && file!=parent.getFile()) // JavaFile objects are flyweight
			throw new RuntimeException();
		
		int over=modifiers&~getAllowedModifiers();
		if(over!=0)
			throw new InjectorParseException(
			"modifier(s) "+java.lang.reflect.Modifier.toString(over)+
			" not allowed for class feature "+name+
			" of type "+getClass().getName()+'.');
		
	}

	/**
	 * Returns the java file, which contains this feature.
	 * Is never null.
	 */
	public final JavaFile getFile()
	{
		return file;
	}
	
	/**
	 * Returns the package of the file containing this feature.
	 */
	public final String getPackageName()
	{
		return file.getPackageName();
	}
	
	/**
	 * Returns the class, which contains this feature.
	 * Is null for top-level (not inner) classes.
	 */
	public final JavaClass getParent()
	{
		return parent;
	}
	
	/**
	 * Returns the modifiers of this feature.
	 * @see java.lang.reflect.Modifier
	 */
	public final int getModifiers()
	{
		return modifiers;
	}
	
	/**
	 * Subclasses use this method to specify,
	 * which modifiers are allowed for the specific kind
	 * of feature.
	 */
	public abstract int getAllowedModifiers();
	
	public final boolean isStatic()
	{
		return (modifiers & Modifier.STATIC) > 0;
	}
	
	public final boolean isAbstract()
	{
		return (modifiers & Modifier.ABSTRACT) > 0;
	}
	
	public static final int toAccessModifier(final int modifier)
	{
		switch(modifier & (Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE))
		{
			case Modifier.PUBLIC:
				return ACCESS_PUBLIC;
			case 0:
				return ACCESS_PACKAGE;
			case Modifier.PROTECTED:
				return ACCESS_PROTECTED;
			case Modifier.PRIVATE:
				return ACCESS_PRIVATE;
			default:
				throw new RuntimeException(Integer.toString(modifier));
		}
	}
	
	public final String getAccessModifierString()
	{
		return toAccessModifierString(accessModifier);
	}

	public static final String toAccessModifierString(final int accessModifier)
	{
		switch(accessModifier)
		{
			case ACCESS_PUBLIC:
				return "public ";
			case ACCESS_PACKAGE:
				return "";
			case ACCESS_PROTECTED:
				return "protected ";
			case ACCESS_PRIVATE:
				return "private ";
			default:
				throw new RuntimeException(Integer.toString(accessModifier));
		}
	}

	/**
	 * The return type of the method.
	 * Is null, if it is a constructor, or a class.
	 */
	public final String getType()
	{
		return type;
	}
	
	public final String getName()
	{
		return name;
	}
	
	public String toString()
	{
		return getClass().getName()+'('+name+')';
	}
	
	public final void print(PrintStream o)
	{
		o.println("  "+JavaFile.extractClassName(getClass().getName())+
		" ("+Modifier.toString(modifiers)+
		") >"+type+
		"< >"+name+
		"< in >"+(parent==null?"none":parent.getName())+"<");
		printMore(o);
	}
	
	public void printMore(PrintStream o)
	{
	}
	
}


