
package com.exedio.cope.instrument;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.exedio.cope.lib.ReadOnlyViolationException;
import com.exedio.cope.lib.util.ClassComparator;

final class CopeClass
{
	private static final HashMap copeClassByJavaClass = new HashMap();
	
	static final CopeClass getCopeClass(final JavaClass javaClass)
	{
		final CopeClass result = (CopeClass)copeClassByJavaClass.get(javaClass);
		//System.out.println("getCopeClass "+javaClass.getName()+" "+(result==null?"NULL":result.getName()));
		return result;
	}


	final JavaClass javaClass;
	final int accessModifier;

	private final ArrayList copeAttributes = new ArrayList();
	private final Map copeAttributeMap = new TreeMap();
	private final Map copeUniqueConstraintMap = new TreeMap();
	private ArrayList uniqueConstraints = null;
	private ArrayList qualifiers = null;
	final int constructorOption;

	public CopeClass(
			final JavaClass javaClass,
			final String constructorOptionString)
		throws InjectorParseException
	{
		this.javaClass = javaClass;
		this.accessModifier = javaClass.accessModifier;
		copeClassByJavaClass.put(javaClass, this);	
		constructorOption = Option.getOption(constructorOptionString);
		//System.out.println("copeClassByJavaClass "+javaClass.getName());
		javaClass.file.repository.add(this);
	}
	
	public String getName()
	{
		return javaClass.name;
	}
	
	public boolean isAbstract()
	{
		return javaClass.isAbstract();
	}

	public boolean isInterface()
	{
		return javaClass.isInterface();
	}

	public void addCopeAttribute(final CopeAttribute copeAttribute)
	{
		copeAttributes.add(copeAttribute);
		copeAttributeMap.put(copeAttribute.getName(), copeAttribute);
	}
	
	/**
	 * @return unmodifiable list of {@link JavaAttribute}
	 */
	public List getCopeAttributes()
	{
		return Collections.unmodifiableList(copeAttributes);
	}
	
	public CopeAttribute getCopeAttribute(final String name)
	{
		return (CopeAttribute)copeAttributeMap.get(name);
	}
	
	public void addCopeUniqueConstraint(final CopeUniqueConstraint copeUniqueConstraint)
	{
		copeUniqueConstraintMap.put(copeUniqueConstraint.name, copeUniqueConstraint);
	}
	
	public CopeUniqueConstraint getCopeUniqueConstraint(final String name)
	{
		return (CopeUniqueConstraint)copeUniqueConstraintMap.get(name);
	}

	public boolean hasGeneratedConstructor()
	{
		return constructorOption != Option.NONE;
	}
	
	public int getGeneratedConstructorModifier()
	{
		switch(constructorOption)
		{
			case Option.NONE:
				throw new RuntimeException();
			case Option.AUTO:
			{
				int result = javaClass.accessModifier;
				for(Iterator i = getInitialAttributes().iterator(); i.hasNext(); )
				{
					final CopeAttribute initialAttribute = (CopeAttribute)i.next();
					final int attributeAccessModifier = initialAttribute.accessModifier;
					if(result<attributeAccessModifier)
						result = attributeAccessModifier;
				}
				return JavaFeature.toReflectionModifier(result);
			}
			case Option.PRIVATE:
				return Modifier.PRIVATE;
			case Option.PROTECTED:
				return Modifier.PROTECTED;
			case Option.PACKAGE:
				return 0;
			case Option.PUBLIC:
				return Modifier.PUBLIC;
			default:
				throw new RuntimeException(String.valueOf(constructorOption));
		}
	}
	
	public void makeUnique(final CopeUniqueConstraint constraint)
	{
		if(uniqueConstraints==null)
			uniqueConstraints=new ArrayList();
		
		uniqueConstraints.add(constraint);
	}
	
	/**
	 * @return unmodifiable list of {@link JavaAttribute}
	 */
	public List getUniqueConstraints()
	{
		return
			uniqueConstraints == null ? 
			Collections.EMPTY_LIST :
			Collections.unmodifiableList(uniqueConstraints);
	}
	
	public void addQualifier(final CopeQualifier qualifier)
	{
		if(qualifiers==null)
			qualifiers=new ArrayList();
		
		qualifiers.add(qualifier);
	}
	
	/**
	 * @return unmodifiable list of {@link JavaAttribute}
	 */
	public List getQualifiers()
	{
		return
			qualifiers == null ?
			Collections.EMPTY_LIST :
			Collections.unmodifiableList(qualifiers);
	}
	
	private ArrayList initialAttributes = null;
	private TreeSet constructorExceptions = null;
	
	private final void makeInitialAttributesAndConstructorExceptions()
	{
		initialAttributes = new ArrayList();
		constructorExceptions = new TreeSet(ClassComparator.getInstance());
		for(Iterator i = getCopeAttributes().iterator(); i.hasNext(); )
		{
			final CopeAttribute copeAttribute = (CopeAttribute)i.next();
			if(copeAttribute.isInitial())
			{
				initialAttributes.add(copeAttribute);
				constructorExceptions.addAll(copeAttribute.getSetterExceptions());
			}
		}
		constructorExceptions.remove(ReadOnlyViolationException.class);
	}

	/**
	 * Return all initial attributes of this class.
	 * Initial attributes are all attributes, which are read-only or not-null.
	 */
	public final List getInitialAttributes()
	{
		if(initialAttributes == null)
			makeInitialAttributesAndConstructorExceptions();
		return initialAttributes;
	}

	/**
	 * Returns all exceptions, the generated constructor of this class should throw.
	 * This is the unification of throws clauses of all the setters of the
	 * {@link #getInitialAttributes() initial attributes},
	 * but without the ReadOnlyViolationException,
	 * because read-only attributes can only be written in the constructor.
	 */
	public final SortedSet getConstructorExceptions()
	{
		if(constructorExceptions == null)
			makeInitialAttributesAndConstructorExceptions();
		return constructorExceptions;
	}

}
