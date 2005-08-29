/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

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

import com.exedio.cope.ReadOnlyViolationException;
import com.exedio.cope.util.ClassComparator;

final class CopeClass
{
	private static final HashMap copeClassByJavaClass = new HashMap();
	
	static final CopeClass getCopeClass(final JavaClass javaClass)
	{
		final CopeClass result = (CopeClass)copeClassByJavaClass.get(javaClass);
		//System.out.println("getCopeClass "+javaClass.getFullName()+" "+(result==null?"NULL":result.getName()));
		return result;
	}


	final JavaClass javaClass;
	final int accessModifier;

	private final ArrayList copeAttributes = new ArrayList();
	private final Map copeAttributeMap = new TreeMap();
	private final Map copeUniqueConstraintMap = new TreeMap();
	private ArrayList uniqueConstraints = null;
	private ArrayList qualifiers = null;
	private ArrayList vectors = null;
	private ArrayList entities = null;
	final Option typeOption;
	final Option initialConstructorOption;
	final Option genericConstructorOption;

	public CopeClass(
			final JavaClass javaClass,
			final String typeOption,
			final String initialConstructorOption,
			final String genericConstructorOption)
		throws InjectorParseException
	{
		this.javaClass = javaClass;
		this.accessModifier = javaClass.accessModifier;
		copeClassByJavaClass.put(javaClass, this);	
		this.typeOption = new Option(typeOption, false);
		this.initialConstructorOption = new Option(initialConstructorOption, false);
		this.genericConstructorOption = new Option(genericConstructorOption, false);
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

	public void add(final CopeAttribute copeAttribute)
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
	
	public void add(final CopeUniqueConstraint copeUniqueConstraint)
	{
		copeUniqueConstraintMap.put(copeUniqueConstraint.name, copeUniqueConstraint);
	}
	
	public CopeUniqueConstraint getCopeUniqueConstraint(final String name)
	{
		return (CopeUniqueConstraint)copeUniqueConstraintMap.get(name);
	}

	public boolean hasInitialConstructor()
	{
		return initialConstructorOption.exists;
	}
	
	public int getInitialConstructorModifier()
	{
		int inheritedModifier = javaClass.accessModifier;
		for(Iterator i = getInitialAttributes().iterator(); i.hasNext(); )
		{
			final CopeAttribute initialAttribute = (CopeAttribute)i.next();
			final int attributeAccessModifier = initialAttribute.accessModifier;
			if(inheritedModifier<attributeAccessModifier)
				inheritedModifier = attributeAccessModifier;
		}
		
		return initialConstructorOption.getModifier(JavaFeature.toReflectionModifier(inheritedModifier));
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
	
	public void add(final CopeQualifier qualifier)
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
	
	public void add(final CopeVector vector)
	{
		if(vectors==null)
			vectors=new ArrayList();

		vectors.add(vector);
	}
	
	/**
	 * @return unmodifiable list of {@link CopeVector}s.
	 */
	public List getVectors()
	{
		return
			vectors == null ?
			Collections.EMPTY_LIST :
			Collections.unmodifiableList(vectors);
	}
	
	public void add(final CopeMedia entity)
	{
		if(entities==null)
			entities=new ArrayList();

		entities.add(entity);
	}
	
	/**
	 * @return unmodifiable list of {@link Media}s.
	 */
	public List getHttpEntities()
	{
		return
			entities == null ?
			Collections.EMPTY_LIST :
			Collections.unmodifiableList(entities);
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
	 * Initial attributes are all attributes, which are read-only or mandatory.
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
