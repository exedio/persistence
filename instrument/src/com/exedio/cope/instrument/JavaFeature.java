
package com.exedio.cope.instrument;

import java.lang.reflect.Modifier;

/**
 * Represents a java feature.
 * May be a class (even an inner class), an attribute or
 * a method.
 */
abstract class JavaFeature
{
	public static final int ACCESS_PUBLIC = 0;
	public static final int ACCESS_PACKAGE = 1;
	public static final int ACCESS_PROTECTED = 2;
	public static final int ACCESS_PRIVATE = 3;
	

	/**
	 * The java file, which contains this feature.
	 * Is never null.
	 */
	final JavaFile file;
	
	/**
	 * The class, which contains this feature.
	 * Is null for top-level (not inner) classes.
	 */
	final JavaClass parent;
	
	/**
	 * The modifier of this feature.
	 * @see java.lang.reflect.Modifier
	 */
	final int modifier;
	
	final int accessModifier;
	
	/**
	 * The return type of the method.
	 * Is null, if it is a constructor, or a class.
	 */
	final String type;
	
	final String name;
	
	public JavaFeature(
							final JavaFile file,
							final JavaClass parent,
							final int modifier,
							final String type,
							final String name)
	throws InjectorParseException
	{
		this.file=file;
		this.parent=parent;
		this.modifier=modifier;
		this.accessModifier=toAccessModifier(modifier);
		this.type=type;
		this.name=name;
		
		if(file==null)
			throw new RuntimeException();
		
		if(parent!=null && file!=parent.file) // JavaFile objects are flyweight
			throw new RuntimeException();
		
		int over=modifier&~getAllowedModifiers();
		if(over!=0)
			throw new InjectorParseException(
			"modifier(s) "+java.lang.reflect.Modifier.toString(over)+
			" not allowed for class feature "+name+
			" of type "+getClass().getName()+'.');
		
	}

	/**
	 * Returns the package of the file containing this feature.
	 */
	public final String getPackageName()
	{
		return file.getPackageName();
	}
	
	/**
	 * Subclasses use this method to specify,
	 * which modifiers are allowed for the specific kind
	 * of feature.
	 */
	public abstract int getAllowedModifiers();
	
	public final boolean isStatic()
	{
		return (modifier & Modifier.STATIC) > 0;
	}
	
	public final boolean isAbstract()
	{
		return (modifier & Modifier.ABSTRACT) > 0;
	}
	
	public static final int toAccessModifier(final int reflectionModifier)
	{
		switch(reflectionModifier & (Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE))
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
				throw new RuntimeException(Integer.toString(reflectionModifier));
		}
	}
	
	public static final int toReflectionModifier(final int accessModifier)
	{
		switch(accessModifier)
		{
			case ACCESS_PUBLIC:
				return Modifier.PUBLIC;
			case ACCESS_PACKAGE:
				return 0;
			case ACCESS_PROTECTED:
				return Modifier.PROTECTED;
			case ACCESS_PRIVATE:
				return Modifier.PRIVATE;
			default:
				throw new RuntimeException(Integer.toString(accessModifier));
		}
	}
	
	public String toString()
	{
		return getClass().getName()+'('+name+')';
	}
	
}


