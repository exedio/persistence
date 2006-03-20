/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.Item;
import com.exedio.cope.FinalViolationException;
import com.exedio.cope.util.ClassComparator;

final class CopeType
{
	private static final HashMap copeTypeByJavaClass = new HashMap();
	
	static final CopeType getCopeType(final JavaClass javaClass)
	{
		final CopeType result = (CopeType)copeTypeByJavaClass.get(javaClass);
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
		copeTypeByJavaClass.put(javaClass, this);	
		this.typeOption = new Option(typeOption, false);
		this.initialConstructorOption = new Option(initialConstructorOption, false);
		this.genericConstructorOption = new Option(genericConstructorOption, false);
		//System.out.println("copeTypeByJavaClass "+javaClass.getName());
		javaClass.nameSpace.importStatic(Item.class);
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
	
	private CopeType supertype;
	private ArrayList subtypes = new ArrayList();
	
	void endBuildStage()
	{
		assert javaClass.file.repository.isBuildStage();
		
		final List exts = javaClass.classExtends;
		switch(exts.size())
		{
			case 0:
				supertype = null;
				break;
			case 1:
			{
				final String extname = (String)exts.iterator().next();
				try
				{
					supertype = javaClass.file.repository.getCopeType(extname);
					supertype.addSubtype(this);
				}
				catch(RuntimeException e)
				{
					if(!e.getMessage().startsWith("no cope type for ")) // TODO better exception
						throw new RuntimeException("bad exception", e);
					else
						supertype = null;
				}
				break;
			}
			default:
				throw new RuntimeException(exts.toString());
		}
	}

	void addSubtype(final CopeType subtype)
	{
		assert javaClass.file.repository.isBuildStage();
		
		subtypes.add(subtype);
	}
	
	public CopeType getSuperclass()
	{
		assert !javaClass.file.repository.isBuildStage();
		
		return supertype;
	}
	
	public List getSubtypes()
	{
		assert !javaClass.file.repository.isBuildStage();
		
		return subtypes;
	}
	
	boolean allowSubTypes()
	{
		assert !javaClass.file.repository.isBuildStage();

		return isAbstract() || !getSubtypes().isEmpty();
	}

	public void register(final CopeFeature feature)
	{
		assert javaClass.file.repository.isBuildStage();
		features.add(feature);
		final Object collision = featureMap.put(feature.name, feature);
		assert collision==null : feature.name;
	}
	
	public CopeFeature getFeature(final String name)
	{
		assert !javaClass.file.repository.isBuildStage();
		return (CopeFeature)featureMap.get(name);
	}
	
	public List getFeatures()
	{
		assert !javaClass.file.repository.isBuildStage();
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
	private TreeSet<Class> constructorExceptions = null;
	
	private final void makeInitialAttributesAndConstructorExceptions()
	{
		initialAttributes = new ArrayList();
		constructorExceptions = new TreeSet<Class>(ClassComparator.getInstance());
		
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
				final CopeAttribute attribute = (CopeAttribute)feature;
				if(attribute.isInitial())
				{
					initialAttributes.add(attribute);
					constructorExceptions.addAll(attribute.getSetterExceptions());
				}
			}
		}
		constructorExceptions.remove(FinalViolationException.class);
	}

	/**
	 * Return all initial attributes of this class.
	 * Initial attributes are all attributes, which are final or mandatory.
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
	 * but without the FinalViolationException,
	 * because final attributes can only be written in the constructor.
	 */
	public final SortedSet<Class> getConstructorExceptions()
	{
		if(constructorExceptions == null)
			makeInitialAttributesAndConstructorExceptions();
		return constructorExceptions;
	}

}
