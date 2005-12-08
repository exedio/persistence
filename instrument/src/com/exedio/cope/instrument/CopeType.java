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
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.exedio.cope.ReadOnlyViolationException;
import com.exedio.cope.util.ClassComparator;

final class CopeType
{
	// TODO rename to copeTypeByJavaClass
	private static final HashMap copeClassByJavaClass = new HashMap();
	
	static final CopeType getCopeClass(final JavaClass javaClass)
	{
		final CopeType result = (CopeType)copeClassByJavaClass.get(javaClass);
		//System.out.println("getCopeClass "+javaClass.getFullName()+" "+(result==null?"NULL":result.getName()));
		return result;
	}


	final JavaClass javaClass;
	final int accessModifier;
	final Option typeOption;
	final Option initialConstructorOption;
	final Option genericConstructorOption;

	private final ArrayList features = new ArrayList();
	private final TreeMap featureMap = new TreeMap();
	
	public CopeType(
			final JavaClass javaClass,
			final String typeOption,
			final String initialConstructorOption,
			final String genericConstructorOption)
		throws InjectorParseException
	{
		this.javaClass = javaClass;
		this.accessModifier = javaClass.getAccessModifier();
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
	
	public CopeType getSuperclass()
	{
		final List exts = javaClass.classExtends;
		switch(exts.size())
		{
			case 0:
				return null;
			case 1:
			{
				final String extname = (String)exts.iterator().next();
				try
				{
					return javaClass.file.repository.getCopeClass(extname);
				}
				catch(RuntimeException e)
				{
					if(!e.getMessage().startsWith("no cope type for ")) // TODO better exception
						throw new RuntimeException("bad exception", e);
					else
						return null;
				}
			}
			default:
				throw new RuntimeException(exts.toString());
		}
	}

	public void register(final CopeFeature feature)
	{
		features.add(feature);
		featureMap.put(feature.name, feature);
	}
	
	public CopeFeature getFeature(final String name)
	{
		return (CopeFeature)featureMap.get(name);
	}
	
	public List getFeatures()
	{
		return Collections.unmodifiableList(features);
	}
	
	public boolean hasInitialConstructor()
	{
		return initialConstructorOption.exists;
	}
	
	public int getInitialConstructorModifier()
	{
		int inheritedModifier = accessModifier;
		for(Iterator i = getInitialAttributes().iterator(); i.hasNext(); )
		{
			final CopeAttribute initialAttribute = (CopeAttribute)i.next();
			final int attributeAccessModifier = initialAttribute.accessModifier;
			if(inheritedModifier<attributeAccessModifier)
				inheritedModifier = attributeAccessModifier;
		}
		
		return initialConstructorOption.getModifier(JavaFeature.toReflectionModifier(inheritedModifier));
	}
	
	private ArrayList initialAttributes = null;
	private TreeSet constructorExceptions = null;
	
	private final void makeInitialAttributesAndConstructorExceptions()
	{
		initialAttributes = new ArrayList();
		constructorExceptions = new TreeSet(ClassComparator.getInstance());
		
		final CopeType superclass = getSuperclass();
		if(superclass!=null)
		{
			initialAttributes.addAll(superclass.getInitialAttributes());
			constructorExceptions.addAll(superclass.getConstructorExceptions());
		}
		
		for(Iterator i = getFeatures().iterator(); i.hasNext(); )
		{
			final CopeFeature feature = (CopeFeature)i.next();
			if(feature instanceof CopeAttribute)
			{
				final CopeAttribute copeAttribute = (CopeAttribute)feature;
				if(copeAttribute.isInitial())
				{
					initialAttributes.add(copeAttribute);
					constructorExceptions.addAll(copeAttribute.getSetterExceptions());
				}
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
