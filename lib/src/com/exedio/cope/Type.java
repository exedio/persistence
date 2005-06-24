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

package com.exedio.cope;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import bak.pcj.map.IntKeyOpenHashMap;

import com.exedio.cope.search.Condition;
import com.exedio.cope.util.ReactivationConstructorDummy;

public final class Type
	implements Selectable
{
	private static final HashMap typesByClass = new HashMap();

	final Class javaClass;
	private final String id;
	private final Type supertype;
	
	private final Attribute[] declaredAttributes;
	private final List declaredAttributeList;
	private final Attribute[] attributes;
	private final List attributeList;

	private final Feature[] declaredFeatures;
	private final List declaredFeatureList;
	private final Feature[] features;
	private final List featureList;
	private final HashMap featuresByName = new HashMap();

	final UniqueConstraint[] uniqueConstraints;
	private final List uniqueConstraintList;

	private final Pattern[] patterns;
	private final List patternList;
	
	private ArrayList subTypes = null;
	private ArrayList references = null;
	
	private Model model;

	private Table table;
	private PrimaryKeyIterator primaryKeyIterator;

	private final Constructor creationConstructor;
	private final Constructor reactivationConstructor;

	/**
	 * @throws RuntimeException if there is no type for the given java class.
	 */
	public static final Type findByJavaClass(final Class javaClass)
	{
		final Type result = (Type)typesByClass.get(javaClass);
		if(result==null)
			throw new RuntimeException("there is no type for "+javaClass);
		return result;
	}
	
	public Type(final Class javaClass)
	{
		this(javaClass, javaClass, new Class[]{}, true);
	}
	
	/**
	 * @deprecated BEWARE: use this constructor only, if you know what you are doing.
	 * @see #Type(Class)
	 */
	public Type(final Class javaClass, final Class componentJavaClass, final Class[] ignoreClasses)
	{
		this(javaClass, componentJavaClass, ignoreClasses, true);
	}

	private ArrayList attributesWhileConstruction;
	private ArrayList featuresWhileConstruction;
	private ArrayList uniqueConstraintsWhileConstruction;
	private ArrayList patternsWhileConstruction;

	public Type(final Class javaClass, final Class componentJavaClass, final Class[] ignoreClasses, boolean dontUse)
	{
		this.javaClass = javaClass;
		if(!Item.class.isAssignableFrom(javaClass))
			throw new IllegalArgumentException(javaClass.toString()+" is not a subclass of Item");
		typesByClass.put(javaClass, this);

		if(!Item.class.isAssignableFrom(componentJavaClass))
			throw new IllegalArgumentException(componentJavaClass.toString()+" is not a subclass of Item");

		{
			final String className = javaClass.getName();
			final int pos = className.lastIndexOf('.');
			this.id = className.substring(pos+1).intern();
		}

		// supertype
		final Class superClass;
		{
			Class superClassTemp;
			final HashSet ignoreClassesSet = new HashSet(Arrays.asList(ignoreClasses));
			ignoreClassesSet.add(componentJavaClass);
			for(superClassTemp = javaClass.getSuperclass();
					ignoreClassesSet.contains(superClassTemp);
					superClassTemp = superClassTemp.getSuperclass() )
			{
				// nothing to do here
			}
			superClass = superClassTemp;
		}
		
		if(superClass.equals(Item.class))
			supertype = null;
		else
		{
			supertype = findByJavaClass(superClass);
			supertype.registerSubType(this);
		}

		// declaredAttributes
		final Field[] fields = componentJavaClass.getDeclaredFields();
		this.attributesWhileConstruction = new ArrayList(fields.length);
		this.featuresWhileConstruction = new ArrayList(fields.length);
		this.uniqueConstraintsWhileConstruction = new ArrayList(fields.length);
		this.patternsWhileConstruction = new ArrayList(fields.length);
		final int expectedModifier = Modifier.STATIC | Modifier.FINAL;
		try
		{
			for(int i = 0; i<fields.length; i++)
			{
				final Field field = fields[i];
				if((field.getModifiers()&expectedModifier)==expectedModifier)
				{
					final Class fieldType = field.getType();
					if(Feature.class.isAssignableFrom(fieldType))
					{
						field.setAccessible(true);
						final Feature component = (Feature)field.get(null); // TODO rename component to feature
						if(component==null)
							throw new RuntimeException(field.getName());
						component.initialize(this, field.getName());
					}
				}
			}
		}
		catch(IllegalAccessException e)
		{
			throw new NestingRuntimeException(e);
		}
		this.declaredAttributes = (Attribute[])attributesWhileConstruction.toArray(new Attribute[attributesWhileConstruction.size()]);
		this.declaredAttributeList = Collections.unmodifiableList(Arrays.asList(this.declaredAttributes));
		this.declaredFeatures = (Feature[])featuresWhileConstruction.toArray(new Feature[featuresWhileConstruction.size()]);
		this.declaredFeatureList = Collections.unmodifiableList(Arrays.asList(this.declaredFeatures));
		this.uniqueConstraints = (UniqueConstraint[])uniqueConstraintsWhileConstruction.toArray(new UniqueConstraint[uniqueConstraintsWhileConstruction.size()]);
		this.uniqueConstraintList = Collections.unmodifiableList(Arrays.asList(this.uniqueConstraints));
		this.patterns = (Pattern[])patternsWhileConstruction.toArray(new Pattern[patternsWhileConstruction.size()]);
		this.patternList = Collections.unmodifiableList(Arrays.asList(this.patterns));

		// make sure, register methods fail from now on
		this.attributesWhileConstruction = null;
		this.featuresWhileConstruction = null;
		this.uniqueConstraintsWhileConstruction = null;
		this.patternsWhileConstruction = null;
		
		// attributes
		if(supertype==null)
		{
			attributes = this.declaredAttributes;
			features = this.declaredFeatures;
		}
		else
		{
			{
				final Attribute[] supertypeAttributes = supertype.attributes;
				attributes = new Attribute[supertypeAttributes.length+this.declaredAttributes.length];
				System.arraycopy(supertypeAttributes, 0, attributes, 0, supertypeAttributes.length);
				System.arraycopy(this.declaredAttributes, 0, attributes, supertypeAttributes.length, this.declaredAttributes.length);
			}
			{
				final Feature[] supertypeFeatures = supertype.features;
				features = new Attribute[supertypeFeatures.length+this.declaredFeatures.length];
				System.arraycopy(supertypeFeatures, 0, features, 0, supertypeFeatures.length);
				System.arraycopy(this.declaredFeatures, 0, features, supertypeFeatures.length, this.declaredFeatures.length);
			}
		}
		this.attributeList = Collections.unmodifiableList(Arrays.asList(attributes));
		this.featureList = Collections.unmodifiableList(Arrays.asList(features));

		// IMPLEMENTATION NOTE
		// Here we don't precompute the constructor parameters
		// because they are needed in the initialization phase
		// only.
		final Class attributeValueArrayClass;
		try
		{
			attributeValueArrayClass = Class.forName("[L"+AttributeValue.class.getName()+';');
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
			throw new NestingRuntimeException(e);
		}
		this.creationConstructor = getConstructor(new Class[]{attributeValueArrayClass}, "creation");
		this.reactivationConstructor = getConstructor(new Class[]{ReactivationConstructorDummy.class, int.class}, "reactivation");
	}
	
	private final Constructor getConstructor(final Class[] params, final String name)
	{
		try
		{
			final Constructor result = javaClass.getDeclaredConstructor(params);
			result.setAccessible(true);
			return result;
		}
		catch(NoSuchMethodException e)
		{
			throw new NestingRuntimeException(e, javaClass.getName() + " does not have a " + name + " constructor");
		}
	}

	final void registerInitialization(final Attribute attribute)
	{
		attributesWhileConstruction.add(attribute);
		featuresWhileConstruction.add(attribute);
		featuresByName.put(attribute.getName(), attribute);
	}

	final void registerInitialization(final ComputedFunction function)
	{
		featuresWhileConstruction.add(function);
		featuresByName.put(function.getName(), function);
	}

	final void registerInitialization(final UniqueConstraint uniqueConstraint)
	{
		uniqueConstraintsWhileConstruction.add(uniqueConstraint);
	}

	final void registerInitialization(final Pattern pattern)
	{
		patternsWhileConstruction.add(pattern);
	}
	
	final void registerSubType(final Type subType)
	{
		if(this.model!=null)
			throw new RuntimeException();

		if(subTypes==null)
			subTypes = new ArrayList();
		subTypes.add(subType);
	}
	
	ItemAttribute onlyReference = null;
	
	final void registerReference(final ItemAttribute reference)
	{
		if(this.model==null)
			throw new RuntimeException();

		if(references==null)
		{
			references = new ArrayList();
			onlyReference = reference;
		}
		else
			onlyReference = null;
			
		references.add(reference);
	}
	
	final void initialize(final Model model)
	{
		if(model==null)
			throw new RuntimeException();

		if(this.model!=null)
			throw new RuntimeException();
		if(this.table!=null)
			throw new RuntimeException();
		if(this.primaryKeyIterator!=null)
			throw new RuntimeException();
		
		this.model = model;
	}
	
	final void materialize(final Database database)
	{
		if(database==null)
			throw new RuntimeException();

		if(this.model==null)
			throw new RuntimeException();
		if(this.table!=null)
			throw new RuntimeException();
		if(this.primaryKeyIterator!=null)
			throw new RuntimeException();

		this.table = new Table(database, id);

		if(supertype!=null)
		{
			primaryKeyIterator = supertype.getPrimaryKeyIterator();
			new ItemColumn(table, supertype.getJavaClass());
		}
		else
		{
			primaryKeyIterator = new PrimaryKeyIterator(table);
			new IntegerColumn(table);
		}
		
		if(subTypes!=null)
		{
			final ArrayList typeIDs = new ArrayList();
			addRecursive(subTypes, typeIDs, 15);
			table.addTypeColumn(typeIDs);
		}

		for(int i = 0; i<declaredAttributes.length; i++)
			declaredAttributes[i].materialize(table);
		for(int i = 0; i<uniqueConstraints.length; i++)
			uniqueConstraints[i].materialize(database);
		this.table.setUniqueConstraints(this.uniqueConstraintList);
	}
	
	private static final void addRecursive(final List subTypes, final ArrayList typeIDs, int levelLimit)
	{
		if(levelLimit<=0)
			throw new RuntimeException(typeIDs.toString());
		levelLimit--;
		
		for(Iterator i = subTypes.iterator(); i.hasNext(); )
		{
			final Type type = (Type)i.next();
			typeIDs.add(type.getID());
			addRecursive(type.getSubTypes(), typeIDs, levelLimit);
		}
	}

	public final Class getJavaClass()
	{
		return javaClass;
	}
	
	public final String getID()
	{
		return id;
	}
	
	public final Model getModel()
	{
		if(model==null)
			throw new RuntimeException("model not set for type "+id+", probably you forgot to put this type into the model.");

		return model;
	}
	
	final Table getTable()
	{
		if(model==null)
			throw new RuntimeException();

		return table;
	}
	
	/**
	 * Returns the type representing the {@link Class#getSuperclass() superclass}
	 * of this type's {@link #getJavaClass() java class}.
	 * If this type has no super type
	 * (i.e. the superclass of this type's java class is {@link Item}),
	 * then null is returned.
	 */
	public final Type getSupertype()
	{
		return supertype;
	}
	
	/**
	 * @return a list of {@link Type}s.
	 */
	public final List getSubTypes()
	{
		return subTypes==null ? Collections.EMPTY_LIST : Collections.unmodifiableList(subTypes);
	}

	/**
	 * @return a list of {@link ItemAttribute}s.
	 */
	public final List getReferences()
	{
		return references==null ? Collections.EMPTY_LIST : Collections.unmodifiableList(references);
	}

	/**
	 * Returns the list of persistent attributes declared by the this type.
	 * This excludes inherited attributes.
	 * The elements in the list returned are ordered by their occurance in the source code.
	 * This method returns an empty list if the type declares no attributes.
	 * <p>
	 * If you want to get all persistent attributes of this type,
	 * including attributes inherited from super types,
	 * use {@link #getAttributes}.
	 * <p> 
	 * Naming of this method is inspired by Java Reflection API
	 * method {@link Class#getDeclaredFields() getDeclaredFields}.
	 */
	public final List getDeclaredAttributes()
	{
		return declaredAttributeList;
	}
	
	/**
	 * Returns the list of accessible persistent attributes of this type.
	 * This includes inherited attributes.
	 * The elements in the list returned are ordered by their type,
	 * with types higher in type hierarchy coming first,
	 * and within each type by their occurance in the source code.
	 * This method returns an empty list if the type has no accessible attributes.
	 * <p>
	 * If you want to get persistent attributes declared by this type only,
	 * excluding attributes inherited from super types,
	 * use {@link #getDeclaredAttributes}.
	 */
	public final List getAttributes()
	{
		return attributeList;
	}
	
	public final List getDeclaredFeatures()
	{
		return declaredFeatureList;
	}

	public final List getFeatures()
	{
		return featureList;
	}
	
	public final Feature getFeature(final String name)
	{
		return (Feature)featuresByName.get(name);
	}

	public final List getUniqueConstraints()
	{
		return uniqueConstraintList;
	}
	
	public final List getPatterns()
	{
		return patternList;
	}
	
	private static final AttributeValue[] EMPTY_ATTRIBUTE_VALUES = new AttributeValue[]{};
	
	public final Item newItem(final AttributeValue[] initialAttributeValues)
	{
		try
		{
			return 
				(Item)creationConstructor.newInstance(
					new Object[]{
						initialAttributeValues!=null
						? initialAttributeValues
						: EMPTY_ATTRIBUTE_VALUES
					}
				);
		}
		catch(InstantiationException e)
		{
			throw new NestingRuntimeException(e);
		}
		catch(IllegalAccessException e)
		{
			throw new NestingRuntimeException(e);
		}
		catch(InvocationTargetException e)
		{
			throw new NestingRuntimeException(e);
		}
	}

	/**
	 * Searches for items of this type, that match the given condition.
	 * <p>
	 * Returns an unmodifiable collection.
	 * Any attempts to modify the returned collection, whether direct or via its iterator,
	 * result in an <code>UnsupportedOperationException</code>.
	 * @param condition the condition the searched items must match.
	 */
	public final Collection search(final Condition condition)
	{
		return new Query(this, condition).search();
	}
	
	final Item searchUnique(final Condition condition)
	{
		final Iterator searchResult = search(condition).iterator();
		if(searchResult.hasNext())
		{
			final Item result = (Item)searchResult.next();
			if(searchResult.hasNext())
				throw new RuntimeException(condition.toString());
			else
				return result;
		}
		else
			return null;
	}
	
	private String toStringCache = null;
	
	public final String toString()
	{
		if(toStringCache!=null)
			return toStringCache;
		
		final StringBuffer buf = new StringBuffer();
		
		buf.append(javaClass.getName());
		for(int i = 0; i<uniqueConstraints.length; i++)
		{
			buf.append(' ');
			buf.append(uniqueConstraints[i].toString());
		}
		
		toStringCache = buf.toString();
		return toStringCache;
	}
	
	PrimaryKeyIterator getPrimaryKeyIterator()
	{
		if(primaryKeyIterator==null)
			throw new RuntimeException( "no primary key iterator in "+getID()+"; maybe you have to initialize the model first" );
		
		return primaryKeyIterator;
	}
	
	void onDropTable()
	{
		rows.clear();
		getPrimaryKeyIterator().flushPK();
	}

	// active items of this type ---------------------------------------------
	
	private final IntKeyOpenHashMap rows = new IntKeyOpenHashMap();
	
	/**
	 * Returns an item of this type and the given pk, if it's already active.
	 * Returns null, if either there is no such item with the given pk, or
	 * such an item is not active.
	 */
	Row getRow(final int pk)
	{
		return (Row)rows.get(pk);
	}
	
	void putRow(final Row row)
	{
		if(rows.put(row.pk, row)!=null)
			throw new RuntimeException();
	}
	
	void removeRow(final Row row)
	{
		if(rows.remove(row.pk)!=row)
			throw new RuntimeException();
	}
	
	static final ReactivationConstructorDummy REACTIVATION_DUMMY = new ReactivationConstructorDummy();

	private Item createItemObject(final int pk)
	{
		try
		{
			return 
				(Item)reactivationConstructor.newInstance(
					new Object[]{
						REACTIVATION_DUMMY,
						new Integer(pk)
					}
				);
		}
		catch(InstantiationException e)
		{
			throw new NestingRuntimeException(e, id);
		}
		catch(IllegalAccessException e)
		{
			throw new NestingRuntimeException(e, id);
		}
		catch(InvocationTargetException e)
		{
			throw new NestingRuntimeException(e, id);
		}
	}

	Item getItem(final int pk)
	{
		final Row row = getRow(pk);
		if(row!=null)
			return row.item;
		else
			return createItemObject(pk);
	}
	
	static final Comparator COMPARATOR = new Comparator()
	{
		public int compare(final Object o1, final Object o2)
		{
			final String t1 = ((Type)o1).id;
			final String t2 = ((Type)o2).id;
			return t1.compareTo(t2);
		}
	};

	static final int NOT_A_PK = Integer.MIN_VALUE;	

}
