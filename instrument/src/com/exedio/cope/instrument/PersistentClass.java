
package com.exedio.cope.instrument;

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

public final class PersistentClass
{
	private static final HashMap persistentClassByJavaClass = new HashMap();
	
	static final PersistentClass getPersistentClass(final JavaClass javaClass)
	{
		final PersistentClass result = (PersistentClass)persistentClassByJavaClass.get(javaClass);
		//System.out.println("getPersistentClass "+javaClass.getName()+" "+(result==null?"NULL":result.getName()));
		return result;
	}


	private final JavaClass javaClass;
	final int accessModifier;

	private final ArrayList persistentAttributes = new ArrayList();
	private final Map persistentAttributeMap = new TreeMap();
	private ArrayList uniqueConstraints = null;

	public PersistentClass(final JavaClass javaClass)
	{
		this.javaClass = javaClass;
		this.accessModifier = javaClass.accessModifier;
		persistentClassByJavaClass.put(javaClass, this);	
		//System.out.println("persistentClassByJavaClass "+javaClass.getName());
	}
	
	public String getName()
	{
		return javaClass.getName();
	}
	
	public boolean isAbstract()
	{
		return javaClass.isAbstract();
	}

	public boolean isInterface()
	{
		return javaClass.isInterface();
	}

	public void addPersistentAttribute(final PersistentAttribute persistentAttribute)
	{
		persistentAttributes.add(persistentAttribute);
		persistentAttributeMap.put(persistentAttribute.getName(), persistentAttribute);
	}
	
	/**
	 * @return unmodifiable list of {@link JavaAttribute}
	 */
	public List getPersistentAttributes()
	{
		return Collections.unmodifiableList(persistentAttributes);
	}
	
	public PersistentAttribute getPersistentAttribute(final String name)
	{
		return (PersistentAttribute)persistentAttributeMap.get(name);
	}
	
	public void makeUnique(final PersistentUniqueConstraint constraint)
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
	
	private ArrayList initialAttributes = null;
	private TreeSet constructorExceptions = null;
	
	private final void makeInitialAttributesAndConstructorExceptions()
	{
		initialAttributes = new ArrayList();
		constructorExceptions = new TreeSet(ClassComparator.getInstance());
		for(Iterator i = getPersistentAttributes().iterator(); i.hasNext(); )
		{
			final PersistentAttribute persistentAttribute = (PersistentAttribute)i.next();
			if(persistentAttribute.isInitial())
			{
				initialAttributes.add(persistentAttribute);
				constructorExceptions.addAll(persistentAttribute.getSetterExceptions());
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
	public final SortedSet getContructorExceptions()
	{
		if(constructorExceptions == null)
			makeInitialAttributesAndConstructorExceptions();
		return constructorExceptions;
	}

}
