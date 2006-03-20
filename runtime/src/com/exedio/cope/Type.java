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

package com.exedio.cope;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.exedio.cope.util.ReactivationConstructorDummy;

public final class Type
	implements Selectable
{
	private static final HashMap<Class<? extends Item>, Type> typesByClass = new HashMap<Class<? extends Item>, Type>();

	final Class<? extends Item> javaClass;
	final String id;
	private final Type supertype;
	
	private final List<Feature> declaredFeatures;
	private final List<Feature> features;
	private final HashMap<String, Feature> declaredFeaturesByName;
	private final HashMap<String, Feature> featuresByName;

	private final List<Attribute> declaredAttributes;
	private final List<Attribute> attributes;
	final List<UniqueConstraint> declaredUniqueConstraints;
	private final List<UniqueConstraint> uniqueConstraints;

	private ArrayList<Type> subTypes = null;
	private ArrayList<ItemAttribute> references = null;
	
	private Model model;
	private ArrayList<Type> typesOfInstances;
	private Type onlyPossibleTypeOfInstances;
	private String[] typesOfInstancesColumnValues;
	
	private Table table;
	private PkSource pkSource;

	private final Constructor creationConstructor;
	private final Constructor reactivationConstructor;
	
	/**
	 * This number uniquely identifies a type within its model.
	 * However, this number is not stable across JVM restarts.
	 * So never put this number into any persistent storage,
	 * nor otherwise make this number accessible outside the library.
	 */
	int transientNumber = -1;

	/**
	 * @throws RuntimeException if there is no type for the given java class.
	 */
	public static final Type findByJavaClass(final Class<? extends Item> javaClass)
	{
		final Type result = typesByClass.get(javaClass);
		if(result==null)
			throw new RuntimeException("there is no type for "+javaClass);
		return result;
	}
	
	private ArrayList<Feature> featuresWhileConstruction;
	
	private static final String classToId(final Class javaClass)
	{
		final String className = javaClass.getName();
		final int pos = className.lastIndexOf('.');
		return className.substring(pos+1).intern();
	}

	public Type(final Class<? extends Item> javaClass)
	{
		this(javaClass, classToId(javaClass));
	}
	
	public Type(final Class<? extends Item> javaClass, final String id)
	{
		this.javaClass = javaClass;
		this.id = id;
		if(javaClass.equals(Item.class))
			throw new IllegalArgumentException("Cannot make a type for " + javaClass + " itself, but only for subclasses.");
		typesByClass.put(javaClass, this);

		// supertype
		final Class superClass = javaClass.getSuperclass();
		
		if(superClass.equals(Item.class))
			supertype = null;
		else
		{
			supertype = findByJavaClass(castSuperType(superClass));
			supertype.registerSubType(this);
		}

		// declared features
		final Field[] fields = javaClass.getDeclaredFields();
		this.featuresWhileConstruction = new ArrayList<Feature>(fields.length);
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
						final Feature feature = (Feature)field.get(null); 
						if(feature==null)
							throw new RuntimeException(field.getName());
						feature.initialize(this, field.getName());
					}
				}
			}
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
		featuresWhileConstruction.trimToSize();
		this.declaredFeatures = Collections.unmodifiableList(featuresWhileConstruction);
		// make sure, method registerInitialization fails from now on
		this.featuresWhileConstruction = null;

		// declared attributes / unique constraints
		{
			final ArrayList<Attribute> declaredAttributes = new ArrayList<Attribute>(declaredFeatures.size());
			final ArrayList<UniqueConstraint> declaredUniqueConstraints = new ArrayList<UniqueConstraint>(declaredFeatures.size());
			final HashMap<String, Feature> declaredFeaturesByName = new HashMap<String, Feature>();
			for(final Feature feature : declaredFeatures)
			{
				if(feature instanceof Attribute)
					declaredAttributes.add((Attribute)feature);
				if(feature instanceof UniqueConstraint)
					declaredUniqueConstraints.add((UniqueConstraint)feature);
				if(declaredFeaturesByName.put(feature.getName(), feature)!=null)
					throw new RuntimeException("duplicate feature "+feature.getName()+" for type "+javaClass.getName());
			}
			declaredAttributes.trimToSize();
			declaredUniqueConstraints.trimToSize();
			this.declaredAttributes = Collections.unmodifiableList(declaredAttributes);
			this.declaredUniqueConstraints = Collections.unmodifiableList(declaredUniqueConstraints);
			this.declaredFeaturesByName = declaredFeaturesByName;
		}

		// inherit features / attributes
		if(supertype==null)
		{
			this.features = this.declaredFeatures;
			this.featuresByName = this.declaredFeaturesByName;
			this.attributes = this.declaredAttributes;
			this.uniqueConstraints = this.declaredUniqueConstraints;
		}
		else
		{
			this.features = inherit(supertype.getFeatures(), this.declaredFeatures);
			{
				final HashMap<String, Feature> inherited = supertype.featuresByName;
				final HashMap<String, Feature> own = this.declaredFeaturesByName;
				if(own.isEmpty())
					this.featuresByName = inherited;
				else
				{
					final HashMap<String, Feature> result = new HashMap<String, Feature>(inherited);
					for(final Feature f : own.values())
					{
						if(result.put(f.getName(), f)!=null)
							throw new RuntimeException("cannot override inherited feature "+f.getName()+" in type "+id);
					}
					this.featuresByName = result;
				}
			}
			this.attributes = inherit(supertype.getAttributes(), this.declaredAttributes);
			this.uniqueConstraints = inherit(supertype.getUniqueConstraints(), this.declaredUniqueConstraints);
		}

		// IMPLEMENTATION NOTE
		// Here we don't precompute the constructor parameters
		// because they are needed in the initialization phase
		// only.
		this.creationConstructor = getConstructor(new Class[]{(new AttributeValue[0]).getClass()}, "creation");
		this.reactivationConstructor = getConstructor(new Class[]{ReactivationConstructorDummy.class, int.class}, "reactivation");
	}
	
	@SuppressWarnings("unchecked")
	private static final Class<Item> castSuperType(final Class o)
	{
		return (Class<Item>)o;
	}

	private static final <T extends Feature> List<T> inherit(final List<T> inherited, final List<T> own)
	{
		assert inherited!=null;
		
		if(own.isEmpty())
			return inherited;
		else if(inherited.isEmpty())
			return own;
		else
		{
			final ArrayList<T> result = new ArrayList<T>(inherited);
			result.addAll(own);
			result.trimToSize();
			return Collections.<T>unmodifiableList(result);
		}
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
			throw new RuntimeException(javaClass.getName() + " does not have a " + name + " constructor", e);
		}
	}
	
	final void registerInitialization(final Feature feature)
	{
		featuresWhileConstruction.add(feature);
	}

	final void registerSubType(final Type subType)
	{
		assert subType!=null : id;
		if(this.model!=null)
			throw new RuntimeException(id+'-'+subType.id);

		if(subTypes==null)
			subTypes = new ArrayList<Type>();
		subTypes.add(subType);
	}
	
	final void registerReference(final ItemAttribute reference)
	{
		if(this.model==null)
			throw new RuntimeException();

		if(references==null)
			references = new ArrayList<ItemAttribute>();
			
		references.add(reference);
	}
	
	final void initialize(final Model model, final int transientNumber)
	{
		if(model==null)
			throw new RuntimeException();
		assert (transientNumber<0) == isAbstract();

		if(this.model!=null)
			throw new RuntimeException();
		if(this.typesOfInstances!=null)
			throw new RuntimeException();
		if(this.onlyPossibleTypeOfInstances!=null)
			throw new RuntimeException();
		if(this.typesOfInstancesColumnValues!=null)
			throw new RuntimeException();
		if(this.table!=null)
			throw new RuntimeException();
		if(this.pkSource!=null)
			throw new RuntimeException();
		if(this.transientNumber>=0)
			throw new RuntimeException();
		
		this.model = model;
		this.transientNumber = transientNumber;
		
		typesOfInstances = new ArrayList<Type>();
		collectTypesOfInstances(typesOfInstances, 15);
		switch(typesOfInstances.size())
		{
			case 0:
				throw new RuntimeException("type "+id+" is abstract and has no non-abstract (even indirect) subtypes");
			case 1:
				onlyPossibleTypeOfInstances = (Type)typesOfInstances.iterator().next();
				break;
			default:
				typesOfInstancesColumnValues = new String[typesOfInstances.size()];
				int i = 0;
				for(Iterator iter = typesOfInstances.iterator(); iter.hasNext(); i++)
					typesOfInstancesColumnValues[i] = ((Type)iter.next()).id;
				break;
		}
	}
	
	private final void collectTypesOfInstances(final ArrayList<Type> result, int levelLimit)
	{
		if(levelLimit<=0)
			throw new RuntimeException(result.toString());
		levelLimit--;
		
		if(!isAbstract())
			result.add(this);
		
		for(final Type t : getSubTypes())
			t.collectTypesOfInstances(result, levelLimit);
	}
	
	final void materialize(final Database database)
	{
		if(database==null)
			throw new RuntimeException();

		if(this.model==null)
			throw new RuntimeException();
		if(this.table!=null)
			throw new RuntimeException();
		if(this.pkSource!=null)
			throw new RuntimeException();

		this.table = new Table(database, id, supertype, typesOfInstancesColumnValues);

		if(supertype!=null)
			pkSource = supertype.getPkSource();
		else
			pkSource = database.makePkSource(table);
		
		for(Iterator i = declaredAttributes.iterator(); i.hasNext(); )
			((Attribute)i.next()).materialize(table);
		for(Iterator i = declaredUniqueConstraints.iterator(); i.hasNext(); )
			((UniqueConstraint)i.next()).materialize(database);
		this.table.setUniqueConstraints(this.declaredUniqueConstraints);
		this.table.finish();
	}
	
	public final Class<? extends Item> getJavaClass()
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
	
	/**
	 * Returns a list of types,
	 * that instances (items) of this type can have.
	 * These are all subtypes of this type,
	 * including indirect subtypes,
	 * and including this type itself,
	 * which are not abstract.
	 */
	final List getTypesOfInstances()
	{
		if(typesOfInstances==null)
			throw new RuntimeException();

		return Collections.unmodifiableList(typesOfInstances);
	}
	
	final Type getOnlyPossibleTypeOfInstances()
	{
		if(typesOfInstances==null)
			throw new RuntimeException();

		return onlyPossibleTypeOfInstances;
	}
	
	final String[] getTypesOfInstancesColumnValues()
	{
		if(typesOfInstances==null)
			throw new RuntimeException();
		
		if(typesOfInstancesColumnValues==null)
			return null;
		else
		{
			final String[] result = new String[typesOfInstancesColumnValues.length];
			System.arraycopy(typesOfInstancesColumnValues, 0, result, 0, result.length);
			return result;
		}
	}
	
	final Table getTable()
	{
		if(model==null)
			throw new RuntimeException();

		return table;
	}
	
	/**
	 * Returns the name of database table for this type - use with care!
	 * <p>
	 * This information is needed only, if you want to access
	 * the database without cope.
	 * In this case you should really know, what you are doing.
	 * Please note, that this string may vary,
	 * if a cope model is configured for different databases.
	 * 
	 * @see Attribute#getColumnName()
	 */
	public final String getTableName()
	{
		return table.id;
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
	public final List<Type> getSubTypes()
	{
		return subTypes==null ? Collections.<Type>emptyList() : Collections.unmodifiableList(subTypes);
	}
	
	public boolean isAssignableFrom(final Type type)
	{
		return javaClass.isAssignableFrom(type.javaClass);
	}
	
	public boolean isAbstract()
	{
		return ( javaClass.getModifiers() & Modifier.ABSTRACT ) > 0;
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
		return declaredAttributes;
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
	public final List<Attribute> getAttributes()
	{
		return attributes;
	}
	
	public final List<Feature> getDeclaredFeatures()
	{
		return declaredFeatures;
	}

	public final List<Feature> getFeatures()
	{
		return features;
	}
	
	public final Feature getDeclaredFeature(final String name)
	{
		return (Feature)declaredFeaturesByName.get(name);
	}

	public final Feature getFeature(final String name)
	{
		return (Feature)featuresByName.get(name);
	}

	public final List getDeclaredUniqueConstraints()
	{
		return declaredUniqueConstraints;
	}
	
	public final List<UniqueConstraint> getUniqueConstraints()
	{
		return uniqueConstraints;
	}
	
	private static final AttributeValue[] EMPTY_ATTRIBUTE_VALUES = new AttributeValue[]{};
	
	public final Item newItem(final AttributeValue[] initialAttributeValues)
		throws ConstraintViolationException
	{
		final Item result;
		try
		{
			result =
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
			throw new RuntimeException(e);
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
		catch(InvocationTargetException e)
		{
			final Throwable t = e.getCause();
			if(t instanceof ConstraintViolationException)
				throw (ConstraintViolationException)t;
			else
				throw new RuntimeException(e);
		}
		
		return result;
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
	
	public final Collection search(final Condition condition, final Function orderBy, final boolean ascending)
	{
		final Query query = new Query(this, condition);
		query.setOrderBy(orderBy, ascending);
		return query.search();
	}
	
	/**
	 * Searches equivalently to {@link #search(Condition)},
	 * but assumes that the condition forces the search result to have at most one element.
	 * <p>
	 * Returns null, if the search result is {@link Collection#isEmpty() empty},
	 * returns the only element of the search result, if the result {@link Collection#size() size} is exactly one.
	 * @throws RuntimeException if the search result size is greater than one.
	 */
	public final Item searchUnique(final Condition condition)
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
	
	public final String toString()
	{
		return id;
	}
	
	PkSource getPkSource()
	{
		if(pkSource==null)
			throw new RuntimeException( "no primary key source in "+id+"; maybe you have to initialize the model first" );
		
		return pkSource;
	}
	
	void onDropTable()
	{
		getPkSource().flushPK();
	}

	
	static final ReactivationConstructorDummy REACTIVATION_DUMMY = new ReactivationConstructorDummy();

	Item getItemObject(final int pk)
	{
		final Entity entity = getModel().getCurrentTransaction().getEntityIfActive(this, pk);
		if(entity!=null)
			return entity.getItem();

		try
		{
			return
				(Item)reactivationConstructor.newInstance(
					new Object[]{
						REACTIVATION_DUMMY,
						Integer.valueOf(pk)
					}
				);
		}
		catch(InstantiationException e)
		{
			throw new RuntimeException(id, e);
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(id, e);
		}
		catch(InvocationTargetException e)
		{
			throw new RuntimeException(id, e);
		}
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
