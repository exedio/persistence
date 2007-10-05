/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.exedio.cope.Field.Option;
import com.exedio.cope.ItemField.DeletePolicy;
import com.exedio.cope.search.ExtremumAggregate;
import com.exedio.cope.util.ReactivationConstructorDummy;

public final class Type<C extends Item>
{
	private static final HashMap<Class<? extends Item>, Type<? extends Item>> typesByClass = new HashMap<Class<? extends Item>, Type<? extends Item>>();

	private final Class<C> javaClass;
	private final boolean uniqueJavaClass;
	final String id;
	final boolean isAbstract;
	final Type<? super C> supertype;
	
	final This<C> thisFunction = new This<C>(this);
	private final List<Feature> declaredFeatures;
	private final List<Feature> features;
	private final HashMap<String, Feature> declaredFeaturesByName;
	private final HashMap<String, Feature> featuresByName;

	private final List<Field> declaredFields;
	private final List<Field> fields;
	private final List<UniqueConstraint> declaredUniqueConstraints;
	final List<UniqueConstraint> uniqueConstraints;

	private final Constructor<C> creationConstructor;
	private final Constructor<C> reactivationConstructor;

	private ArrayList<Type<? extends C>> subTypes = null;

	private ArrayList<ItemField<C>> referencesWhileInitialization = new ArrayList<ItemField<C>>();
	private List<ItemField<C>> declaredReferences = null;
	private List<ItemField> references = null;
	
	private Model model;
	private ArrayList<Type<? extends C>> subTypesTransitively;
	private ArrayList<Type<? extends C>> typesOfInstances;
	private HashMap<String, Type<? extends C>> typesOfInstancesMap;
	private Type<? extends C> onlyPossibleTypeOfInstances;
	private String[] typesOfInstancesColumnValues;
	
	/**
	 * This id uniquely identifies a type within its model.
	 * However, this id is not stable across JVM restarts.
	 * So never put this id into any persistent storage,
	 * nor otherwise make this id accessible outside the library.
	 * <p>
	 * This id is negative for abstract types and positive
	 * (including zero) for non-abstract types.
	 */
	int idTransiently = -1;

	private Table table;
	private PkSource pkSource;
	
	/**
	 * @throws IllegalArgumentException if there is no type for the given java class.
	 * @see #hasUniqueJavaClass()
	 */
	public static final <X extends Item> Type<X> findByJavaClass(final Class<X> javaClass)
	{
		return findByJavaClassUnchecked(javaClass).castType(javaClass);
	}
	
	@SuppressWarnings("unchecked") // OK: unchecked cast is checked manually using runtime type information
	public <X extends Item> Type<X> castType(final Class<X> clazz)
	{
		if(javaClass!=clazz)
			throw new ClassCastException("expected " + clazz.getName() + ", but was " + javaClass.getName());
		
		return (Type<X>)this;
	}
	
	/**
	 * @throws IllegalArgumentException if there is no type for the given java class.
	 * @see #hasUniqueJavaClass()
	 */
	public static final Type<?> findByJavaClassUnchecked(final Class<?> javaClass)
	{
		final Type<? extends Item> result = typesByClass.get(javaClass);
		if(result==null)
			throw new IllegalArgumentException("there is no type for " + javaClass);
		return result;
	}
	
	private ArrayList<Feature> featuresWhileConstruction;
	
	Type(final Class<C> javaClass)
	{
		this(javaClass, javaClass.getSimpleName());
	}
	
	Type(final Class<C> javaClass, final String id)
	{
		this(javaClass, id, getFeatureMap(javaClass));
	}
	
	private static final LinkedHashMap<String, Feature> getFeatureMap(final Class<?> javaClass)
	{
		final LinkedHashMap<String, Feature> result = new LinkedHashMap<String, Feature>();
		final java.lang.reflect.Field[] fields = javaClass.getDeclaredFields();
		final int expectedModifier = Modifier.STATIC | Modifier.FINAL;
		try
		{
			for(final java.lang.reflect.Field field : fields)
			{
				if((field.getModifiers()&expectedModifier)==expectedModifier)
				{
					final Class fieldType = field.getType();
					if(Feature.class.isAssignableFrom(fieldType))
					{
						field.setAccessible(true);
						final Feature feature = (Feature)field.get(null);
						if(feature==null)
							throw new RuntimeException(field.getName());
						result.put(field.getName(), feature);
					}
				}
			}
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
		return result;
	}
	
	Type(final Class<C> javaClass, final String id, final LinkedHashMap<String, Feature> featureMap)
	{
		this.javaClass = javaClass;
		this.uniqueJavaClass = (javaClass!=ItemWithoutJavaClass.class);
		this.id = id;
		this.isAbstract = ( javaClass.getModifiers() & Modifier.ABSTRACT ) > 0;
		
		if(!Item.class.isAssignableFrom(javaClass))
			throw new IllegalArgumentException(javaClass + " is not a subclass of Item");
		if(javaClass.equals(Item.class))
			throw new IllegalArgumentException("Cannot make a type for " + javaClass + " itself, but only for subclasses.");

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
		this.featuresWhileConstruction = new ArrayList<Feature>(featureMap.size() + 1);
		thisFunction.initialize(this, This.NAME);
		for(final Map.Entry<String, Feature> entry : featureMap.entrySet())
			entry.getValue().initialize(this, entry.getKey());
		featuresWhileConstruction.trimToSize();
		this.declaredFeatures = Collections.unmodifiableList(featuresWhileConstruction);
		// make sure, method registerInitialization fails from now on
		this.featuresWhileConstruction = null;

		// declared fields / unique constraints
		{
			final ArrayList<Field> declaredFields = new ArrayList<Field>(declaredFeatures.size());
			final ArrayList<UniqueConstraint> declaredUniqueConstraints = new ArrayList<UniqueConstraint>(declaredFeatures.size());
			final HashMap<String, Feature> declaredFeaturesByName = new HashMap<String, Feature>();
			for(final Feature feature : declaredFeatures)
			{
				if(feature instanceof Field)
					declaredFields.add((Field)feature);
				if(feature instanceof UniqueConstraint)
					declaredUniqueConstraints.add((UniqueConstraint)feature);
				if(declaredFeaturesByName.put(feature.getName(), feature)!=null)
					throw new RuntimeException("duplicate feature "+feature.getName()+" for type "+javaClass.getName());
			}
			declaredFields.trimToSize();
			declaredUniqueConstraints.trimToSize();
			this.declaredFields = Collections.unmodifiableList(declaredFields);
			this.declaredUniqueConstraints = Collections.unmodifiableList(declaredUniqueConstraints);
			this.declaredFeaturesByName = declaredFeaturesByName;
		}

		// inherit features / fields
		if(supertype==null)
		{
			this.features = this.declaredFeatures;
			this.featuresByName = this.declaredFeaturesByName;
			this.fields = this.declaredFields;
			this.uniqueConstraints = this.declaredUniqueConstraints;
		}
		else
		{
			{
				final ArrayList<Feature> features = new ArrayList<Feature>();
				features.add(thisFunction);
				final List<Feature> superFeatures = supertype.getFeatures();
				features.addAll(superFeatures.subList(1, superFeatures.size()));
				features.addAll(this.declaredFeatures.subList(1, this.declaredFeatures.size()));
				features.trimToSize();
				this.features = Collections.unmodifiableList(features);
			}
			{
				final HashMap<String, Feature> inherited = supertype.featuresByName;
				final HashMap<String, Feature> declared = this.declaredFeaturesByName;
				final HashMap<String, Feature> result = new HashMap<String, Feature>(inherited);
				for(final Feature f : declared.values())
					result.put(f.getName(), f);
				this.featuresByName = result;
			}
			this.fields = inherit(supertype.getFields(), this.declaredFields);
			this.uniqueConstraints = inherit(supertype.getUniqueConstraints(), this.declaredUniqueConstraints);
		}

		// IMPLEMENTATION NOTE
		// Here we don't precompute the constructor parameters
		// because they are needed in the initialization phase
		// only.
		this.creationConstructor = getConstructor(new Class[]{SetValue[].class}, "creation");
		this.reactivationConstructor = getConstructor(new Class[]{ReactivationConstructorDummy.class, int.class}, "reactivation");

		// register type at the end of the constructor, so the
		// type is not registered, if the constructor throws
		// an exception
		if(uniqueJavaClass)
			typesByClass.put(javaClass, this);
	}
	
	@SuppressWarnings("unchecked") // OK: Class.getSuperclass() does not support generics
	private static final Class<Item> castSuperType(final Class o)
	{
		return o;
	}

	private static final <F extends Feature> List<F> inherit(final List<F> inherited, final List<F> declared)
	{
		assert inherited!=null;
		
		if(declared.isEmpty())
			return inherited;
		else if(inherited.isEmpty())
			return declared;
		else
		{
			final ArrayList<F> result = new ArrayList<F>(inherited);
			result.addAll(declared);
			result.trimToSize();
			return Collections.<F>unmodifiableList(result);
		}
	}
	
	private Constructor<C> getConstructor(final Class[] params, final String name)
	{
		if(!uniqueJavaClass)
			return null;
		
		try
		{
			final Constructor<C> result = javaClass.getDeclaredConstructor(params);
			result.setAccessible(true);
			return result;
		}
		catch(NoSuchMethodException e)
		{
			throw new IllegalArgumentException(javaClass.getName() + " does not have a " + name + " constructor", e);
		}
	}
	
	void registerInitialization(final Feature feature)
	{
		featuresWhileConstruction.add(feature);
	}

	void registerSubType(final Type subType)
	{
		assert subType!=null : id;
		if(this.model!=null)
			throw new RuntimeException(id+'-'+subType.id);

		if(subTypes==null)
			subTypes = new ArrayList<Type<? extends C>>();
		subTypes.add(castTypeInstance(subType));
	}
	
	@SuppressWarnings("unchecked")
	private Type<? extends C> castTypeInstance(final Type t)
	{
		return t;
	}
	
	void registerReference(final ItemField<C> reference)
	{
		referencesWhileInitialization.add(reference);
	}
	
	void initialize(final Model model, final int idTransiently)
	{
		if(model==null)
			throw new RuntimeException();
		assert (idTransiently<0) == isAbstract;

		if(this.model!=null)
			throw new RuntimeException();
		if(this.subTypesTransitively!=null)
			throw new RuntimeException();
		if(this.typesOfInstances!=null)
			throw new RuntimeException();
		if(this.typesOfInstancesMap!=null)
			throw new RuntimeException();
		if(this.onlyPossibleTypeOfInstances!=null)
			throw new RuntimeException();
		if(this.typesOfInstancesColumnValues!=null)
			throw new RuntimeException();
		if(this.table!=null)
			throw new RuntimeException();
		if(this.pkSource!=null)
			throw new RuntimeException();
		if(this.idTransiently>=0)
			throw new RuntimeException();
		
		this.model = model;
		this.idTransiently = idTransiently;
		
		final ArrayList<Type> subTypesTransitively = new ArrayList<Type>();
		final ArrayList<Type> typesOfInstances = new ArrayList<Type>();
		collectSubTypes(subTypesTransitively, typesOfInstances, 15);
		switch(typesOfInstances.size())
		{
			case 0:
				throw new RuntimeException("type " + id + " is abstract and has no non-abstract (even indirect) subtypes");
			case 1:
				onlyPossibleTypeOfInstances = castTypeInstance(typesOfInstances.iterator().next());
				break;
			default:
				final HashMap<String, Type> typesOfInstancesMap = new HashMap<String, Type>();
				typesOfInstancesColumnValues = new String[typesOfInstances.size()];
				int i = 0;
				for(final Type t : typesOfInstances)
				{
					if(typesOfInstancesMap.put(t.id, t)!=null)
						throw new RuntimeException(t.id);
					typesOfInstancesColumnValues[i++] = t.id;
				}
				this.typesOfInstancesMap = castTypeInstanceHasMap(typesOfInstancesMap);
				break;
		}
		this.subTypesTransitively = castTypeInstanceArrayList(subTypesTransitively);
		this.typesOfInstances = castTypeInstanceArrayList(typesOfInstances);

		for(final Field a : declaredFields)
			if(a instanceof ItemField)
				((ItemField)a).postInitialize();
	}
	
	@SuppressWarnings("unchecked")
	private ArrayList<Type<? extends C>> castTypeInstanceArrayList(final ArrayList l)
	{
		return l;
	}
	
	@SuppressWarnings("unchecked")
	private HashMap<String, Type<? extends C>> castTypeInstanceHasMap(final HashMap m)
	{
		return m;
	}
	
	private void collectSubTypes(final ArrayList<Type> all, final ArrayList<Type> concrete, int levelLimit)
	{
		if(levelLimit<=0)
			throw new RuntimeException(all.toString());
		levelLimit--;
		
		all.add(this);
		if(!isAbstract)
			concrete.add(this);
		
		for(final Type<? extends C> t : getSubTypes())
			t.collectSubTypes(all, concrete, levelLimit);
	}
	
	void postInitialize()
	{
		assert referencesWhileInitialization!=null;
		assert declaredReferences==null;
		assert references==null;
		
		referencesWhileInitialization.trimToSize();
		this.declaredReferences = Collections.unmodifiableList(referencesWhileInitialization);
		this.referencesWhileInitialization = null;
		if(supertype!=null)
		{
			final List<ItemField> inherited = supertype.getReferences();
			final List<ItemField<C>> declared = declaredReferences;
			if(declared.isEmpty())
				this.references = inherited;
			else if(inherited.isEmpty())
				this.references = castReferences(declared);
			else
			{
				final ArrayList<ItemField> result = new ArrayList<ItemField>(inherited);
				result.addAll(declared);
				result.trimToSize();
				this.references = Collections.unmodifiableList(result);
			}
		}
		else
		{
			this.references = castReferences(declaredReferences);
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<ItemField> castReferences(final List l)
	{
		return l;
	}
	
	void connect(final Database database)
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

		pkSource =
			supertype!=null
			? supertype.getPkSource()
			: new PkSource(table);
		
		for(final Field a : declaredFields)
			a.connect(table);
		for(final UniqueConstraint uc : declaredUniqueConstraints)
			uc.connect(database);
		this.table.setUniqueConstraints(this.declaredUniqueConstraints);
		this.table.finish();
	}
	
	void disconnect()
	{
		if(this.model==null)
			throw new RuntimeException();
		if(this.table==null)
			throw new RuntimeException();
		if(this.pkSource==null)
			throw new RuntimeException();

		table = null;
		pkSource = null;
		
		for(final Field a : declaredFields)
			a.disconnect();
		for(final UniqueConstraint uc : declaredUniqueConstraints)
			uc.disconnect();
	}
	
	public Class<C> getJavaClass()
	{
		return javaClass;
	}
	
	/**
	 * Returns, whether this type has a java class
	 * uniquely for this type.
	 * Only such types can be found by
	 * {@link #findByJavaClass(Class)} and
	 * {@link #findByJavaClassUnchecked(Class)}.
	 */
	public boolean hasUniqueJavaClass()
	{
		return uniqueJavaClass;
	}
	
	/**
	 * @see Model#findTypeByID(String)
	 */
	public String getID()
	{
		return id;
	}
	
	public Model getModel()
	{
		if(model==null)
			throw new RuntimeException("model not set for type " + id + ", probably you forgot to put this type into the model.");

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
	public List<Type<? extends C>> getTypesOfInstances()
	{
		if(typesOfInstances==null)
			throw new RuntimeException();

		return Collections.unmodifiableList(typesOfInstances);
	}
	
	Type<? extends C> getTypeOfInstance(final String id)
	{
		return typesOfInstancesMap.get(id);
	}
	
	Type<? extends C> getOnlyPossibleTypeOfInstances()
	{
		if(typesOfInstances==null)
			throw new RuntimeException();

		return onlyPossibleTypeOfInstances;
	}
	
	String[] getTypesOfInstancesColumnValues()
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
	
	Table getTable()
	{
		if(model==null)
			throw new RuntimeException();

		return table;
	}
	
	public Integer getPrimaryKeyInfo()
	{
		return getPkSource().getPrimaryKeyInfo();
	}
	
	/**
	 * Returns the name of database table for this type
	 * - <b>use with care!</b>
	 * <p>
	 * This information is needed only, if you want to access
	 * the database without cope.
	 * In this case you should really know, what you are doing.
	 * Any INSERT/UPDATE/DELETE on the database bypassing cope
	 * may lead to inconsistent caches.
	 * Please note, that this string may vary,
	 * if a cope model is configured for different databases.
	 *
	 * @see #getPrimaryKeyColumnName()
	 * @see #getTypeColumnName()
	 * @see Field#getColumnName()
	 * @see ItemField#getTypeColumnName()
	 */
	public String getTableName()
	{
		return table.id;
	}
	
	/**
	 * Returns the name of primary key column in the database for this type
	 * - <b>use with care!</b>
	 * <p>
	 * This information is needed only, if you want to access
	 * the database without cope.
	 * In this case you should really know, what you are doing.
	 * Any INSERT/UPDATE/DELETE on the database bypassing cope
	 * may lead to inconsistent caches.
	 * Please note, that this string may vary,
	 * if a cope model is configured for different databases.
	 *
	 * @see #getTableName()
	 * @see #getTypeColumnName()
	 * @see Field#getColumnName()
	 */
	public String getPrimaryKeyColumnName()
	{
		return table.primaryKey.id;
	}
	
	/**
	 * Returns the name of type column in the database for this type
	 * - <b>use with care!</b>
	 * <p>
	 * This information is needed only, if you want to access
	 * the database without cope.
	 * In this case you should really know, what you are doing.
	 * Any INSERT/UPDATE/DELETE on the database bypassing cope
	 * may lead to inconsistent caches.
	 * Please note, that this string may vary,
	 * if a cope model is configured for different databases.
	 *
	 * @throws IllegalArgumentException
	 *         if there is no type column for this type,
	 *         because <code>{@link Type#getTypesOfInstances()}</code>
	 *         contains one type only.
	 * @see #getTableName()
	 * @see #getPrimaryKeyColumnName()
	 * @see Field#getColumnName()
	 * @see ItemField#getTypeColumnName()
	 */
	public String getTypeColumnName()
	{
		if(table.typeColumn==null)
			throw new IllegalArgumentException("no type column for " + this);

		return table.typeColumn.id;
	}
	
	/**
	 * Returns the type representing the {@link Class#getSuperclass() superclass}
	 * of this type's {@link #getJavaClass() java class}.
	 * If this type has no super type
	 * (i.e. the superclass of this type's java class is {@link Item}),
	 * then null is returned.
	 */
	public Type<? super C> getSupertype()
	{
		return supertype;
	}
	
	/**
	 * @see #getSubTypesTransitively()
	 */
	public List<Type<? extends C>> getSubTypes()
	{
		return subTypes==null ? Collections.<Type<? extends C>>emptyList() : Collections.unmodifiableList(subTypes);
	}
	
	/**
	 * @see #getSubTypes()
	 */
	public List<Type<? extends C>> getSubTypesTransitively()
	{
		if(subTypesTransitively==null)
			throw new RuntimeException();

		return Collections.unmodifiableList(subTypesTransitively);
	}
	
	public boolean isAssignableFrom(final Type type)
	{
		return
			(uniqueJavaClass&&type.uniqueJavaClass)
			? javaClass.isAssignableFrom(type.javaClass)
			: (this==type);
	}
	
	void assertBelongs(final Field f)
	{
		if(!f.getType().isAssignableFrom(this))
			throw new IllegalArgumentException("field " + f + " does not belong to type " + this.toString());
	}
	
	public boolean isAbstract()
	{
		return isAbstract;
	}
	
	public This<C> getThis()
	{
		return thisFunction;
	}

	/**
	 * Returns all {@link ItemField}s of the model this type belongs to,
	 * which {@link ItemField#getValueType value type} equals this type.
	 * @see #getReferences()
	 */
	public List<ItemField<C>> getDeclaredReferences()
	{
		assert declaredReferences!=null;
		return declaredReferences;
	}

	/**
	 * Returns all {@link ItemField}s of the model this type belongs to,
	 * which {@link ItemField#getValueType value type} equals this type
	 * or any of it's super types.
	 * @see #getDeclaredReferences()
	 */
	public List<ItemField> getReferences()
	{
		assert references!=null;
		return references;
	}

	/**
	 * Returns the list of persistent fields declared by the this type.
	 * This excludes inherited fields.
	 * The elements in the list returned are ordered by their occurance in the source code.
	 * This method returns an empty list if the type declares no fields.
	 * <p>
	 * If you want to get all persistent fields of this type,
	 * including fields inherited from super types,
	 * use {@link #getFields()}.
	 * <p>
	 * Naming of this method is inspired by Java Reflection API
	 * method {@link Class#getDeclaredFields() getDeclaredFields}.
	 */
	public List<Field> getDeclaredFields()
	{
		return declaredFields;
	}
	
	/**
	 * @deprecated Renamed to {@link #getDeclaredFields()}.
	 */
	@Deprecated
	public List<Field> getDeclaredAttributes()
	{
		return declaredFields;
	}
	
	/**
	 * Returns the list of accessible persistent fields of this type.
	 * This includes inherited fields.
	 * The elements in the list returned are ordered by their type,
	 * with types higher in type hierarchy coming first,
	 * and within each type by their occurance in the source code.
	 * This method returns an empty list if the type has no accessible fields.
	 * <p>
	 * If you want to get persistent fields declared by this type only,
	 * excluding fields inherited from super types,
	 * use {@link #getDeclaredFields()}.
	 */
	public List<Field> getFields()
	{
		return fields;
	}
	
	/**
	 * @deprecated Renamed to {@link #getFields()}.
	 */
	@Deprecated
	public List<Field> getAttributes()
	{
		return getFields();
	}
	
	public List<Feature> getDeclaredFeatures()
	{
		return declaredFeatures;
	}

	public List<Feature> getFeatures()
	{
		return features;
	}
	
	public Feature getDeclaredFeature(final String name)
	{
		return declaredFeaturesByName.get(name);
	}

	public Feature getFeature(final String name)
	{
		return featuresByName.get(name);
	}

	public List<UniqueConstraint> getDeclaredUniqueConstraints()
	{
		return declaredUniqueConstraints;
	}
	
	public List<UniqueConstraint> getUniqueConstraints()
	{
		return uniqueConstraints;
	}
	
	public ItemField<C> newItemField(final DeletePolicy policy)
	{
		return new ItemField<C>(this, policy);
	}
	
	/**
	 * @deprecated use {@link Field#toFinal()}, {@link FunctionField#unique()} and {@link Field#optional()} instead.
	 */
	@Deprecated
	public ItemField<C> newItemField(final Option option, final DeletePolicy policy)
	{
		return new ItemField<C>(option, this, policy);
	}
	
	private static final SetValue[] EMPTY_SET_VALUES = {};
	
	public C newItem(final SetValue... setValues)
		throws ConstraintViolationException
	{
		if(!uniqueJavaClass)
			return cast(new ItemWithoutJavaClass(setValues, this));

		try
		{
			return
				creationConstructor.newInstance(
					new Object[]{
						setValues!=null
						? setValues
						: EMPTY_SET_VALUES
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
			if(t instanceof RuntimeException)
				throw (RuntimeException)t;
			else
				throw new RuntimeException(e);
		}
	}

	public C cast(final Item item)
	{
		return Cope.verboseCast(javaClass, item);
	}

	/**
	 * Searches for all items of this type.
	 * <p>
	 * Returns an unmodifiable collection.
	 * Any attempts to modify the returned collection, whether direct or via its iterator,
	 * result in an <tt>UnsupportedOperationException</tt>.
	 */
	public List<C> search()
	{
		return search(null);
	}
	
	/**
	 * Searches for items of this type, that match the given condition.
	 * <p>
	 * Returns an unmodifiable collection.
	 * Any attempts to modify the returned collection, whether direct or via its iterator,
	 * result in an <tt>UnsupportedOperationException</tt>.
	 *
	 * @param condition the condition the searched items must match.
	 */
	public List<C> search(final Condition condition)
	{
		return newQuery(condition).search();
	}
	
	/**
	 * Searches for items of this type, that match the given condition.
	 * The result is sorted by the given function <tt>orderBy</tt>.
	 * <p>
	 * Returns an unmodifiable collection.
	 * Any attempts to modify the returned collection, whether direct or via its iterator,
	 * result in an <tt>UnsupportedOperationException</tt>.
	 *
	 * @param condition the condition the searched items must match.
	 * @param ascending whether the result is sorted ascendingly (<tt>true</tt>) or descendingly (<tt>false</tt>).
	 */
	public List<C> search(final Condition condition, final Function orderBy, final boolean ascending)
	{
		final Query<C> query = newQuery(condition);
		query.setOrderBy(orderBy, ascending);
		return query.search();
	}
	
	/**
	 * Searches equivalently to {@link #search(Condition)},
	 * but assumes that the condition forces the search result to have at most one element.
	 * <p>
	 * Returns null, if the search result is {@link Collection#isEmpty() empty},
	 * returns the only element of the search result, if the result {@link Collection#size() size} is exactly one.
	 * @throws IllegalArgumentException if the search result size is greater than one.
	 * @see Query#searchSingleton()
	 */
	public C searchSingleton(final Condition condition)
	{
		return newQuery(condition).searchSingleton();
	}
	
	/**
	 * @deprecated renamed to {@link #searchSingleton(Condition)}.
	 */
	@Deprecated
	public C searchUnique(final Condition condition)
	{
		return searchSingleton(condition);
	}
	
	public Query<C> newQuery()
	{
		return newQuery(null);
	}
	
	public Query<C> newQuery(final Condition condition)
	{
		return new Query<C>(thisFunction, this, condition);
	}
	
	@Override
	public String toString()
	{
		return id;
	}
	
	PkSource getPkSource()
	{
		if(pkSource==null)
			throw new RuntimeException("no primary key source in " + id + "; maybe you have to initialize the model first");
		
		return pkSource;
	}
	
	void onDropTable()
	{
		getPkSource().flushPK();
	}

	
	static final ReactivationConstructorDummy REACTIVATION_DUMMY = new ReactivationConstructorDummy();

	C getItemObject(final int pk)
	{
		final Entity entity = getModel().getCurrentTransaction().getEntityIfActive(this, pk);
		if(entity!=null)
			return cast(entity.getItem());
		else
			return createItemObject(pk);
	}
	
	C createItemObject(final int pk)
	{
		if(!uniqueJavaClass)
			return cast(new ItemWithoutJavaClass(pk, this));

		try
		{
			return reactivationConstructor.newInstance(REACTIVATION_DUMMY, pk);
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

	static final int MIN_PK = 0;
	static final int MAX_PK = Integer.MAX_VALUE;
	static final int NOT_A_PK = Integer.MIN_VALUE;

	public static final class This<E extends Item> extends Feature implements Function<E>, ItemFunction<E>
	{
		private static final String NAME = "this";
		
		final Type<E> type;
		
		private This(final Type<E> type)
		{
			assert type!=null;
			this.type = type;
		}
		
		@Override
		void initialize(final Type<? extends Item> type, final String name)
		{
			super.initialize(type, name);
			assert this.type == type;
			assert NAME.equals(name);
		}
		
		public E get(final Item item)
		{
			return type.cast(item);
		}
		
		public Class<E> getValueClass()
		{
			return type.getJavaClass();
		}
		
		/**
		 * @deprecated For internal use within COPE only.
		 */
		@Deprecated
		public void check(final TC tc, final Join join)
		{
			tc.check(this, join);
		}
		
		/**
		 * @deprecated For internal use within COPE only.
		 */
		@Deprecated
		public void append(final Statement bf, final Join join)
		{
			bf.appendPK(type, join);
		}
		
		/**
		 * @deprecated For internal use within COPE only.
		 */
		@Deprecated
		public void appendType(final Statement bf, final Join join)
		{
			bf.append(Statement.assertTypeColumn(type.getTable().typeColumn, type), join);
		}
		
		/**
		 * @deprecated For internal use within COPE only.
		 */
		@Deprecated
		public final int getTypeForDefiningColumn()
		{
			return IntegerColumn.JDBC_TYPE_INT;
		}
		
		/**
		 * @deprecated For internal use within COPE only.
		 */
		@Deprecated
		public void appendParameter(final Statement bf, final E value)
		{
			bf.appendParameter(value.pk);
		}

		public Type<E> getValueType()
		{
			return type;
		}
		
		public boolean needsCheckTypeColumn()
		{
			return type.supertype!=null && type.supertype.getTable().typeColumn!=null;
		}

		public int checkTypeColumn()
		{
			if(!needsCheckTypeColumn())
				throw new RuntimeException("no check for type column needed for " + this);
			
			return type.table.database.checkTypeColumn(type.getModel().getCurrentTransaction().getConnection(), type);
		}
		
		// convenience methods for conditions and views ---------------------------------

		/**
		 * Note: a primary key can become null in queries using outer joins.
		 */
		public IsNullCondition<E> isNull()
		{
			return new IsNullCondition<E>(this, false);
		}
		
		/**
		 * Note: a primary key can become null in queries using outer joins.
		 */
		public IsNullCondition<E> isNotNull()
		{
			return new IsNullCondition<E>(this, true);
		}
		
		public Condition equal(final E value)
		{
			return Cope.equal(this, value);
		}
		
		public Condition equal(final Join join, final E value)
		{
			return this.bind(join).equal(value);
		}
		
		public final Condition in(final E... values)
		{
			return CompositeCondition.in(this, values);
		}
		
		public Condition in(final Collection<E> values)
		{
			return CompositeCondition.in(this, values);
		}
		
		public Condition notEqual(final E value)
		{
			return Cope.notEqual(this, value);
		}
		
		public CompareCondition<E> less(final E value)
		{
			return new CompareCondition<E>(CompareCondition.Operator.Less, this, value);
		}
		
		public CompareCondition<E> lessOrEqual(final E value)
		{
			return new CompareCondition<E>(CompareCondition.Operator.LessEqual, this, value);
		}
		
		public CompareCondition<E> greater(final E value)
		{
			return new CompareCondition<E>(CompareCondition.Operator.Greater, this, value);
		}
		
		public CompareCondition<E> greaterOrEqual(final E value)
		{
			return new CompareCondition<E>(CompareCondition.Operator.GreaterEqual, this, value);
		}
		
		public Condition between(final E lowerBound, final E upperBound)
		{
			return greaterOrEqual(lowerBound).and(lessOrEqual(upperBound));
		}
		
		public CompareFunctionCondition<E> equal(final Function<E> right)
		{
			return new CompareFunctionCondition<E>(CompareCondition.Operator.Equal, this, right);
		}

		public CompareFunctionCondition<E> notEqual(final Function<E> right)
		{
			return new CompareFunctionCondition<E>(CompareCondition.Operator.NotEqual, this, right);
		}

		public final CompareFunctionCondition<E> less(final Function<E> right)
		{
			return new CompareFunctionCondition<E>(CompareCondition.Operator.Less, this, right);
		}
		
		public final CompareFunctionCondition<E> lessOrEqual(final Function<E> right)
		{
			return new CompareFunctionCondition<E>(CompareCondition.Operator.LessEqual, this, right);
		}
		
		public final CompareFunctionCondition<E> greater(final Function<E> right)
		{
			return new CompareFunctionCondition<E>(CompareCondition.Operator.Greater, this, right);
		}
		
		public final CompareFunctionCondition<E> greaterOrEqual(final Function<E> right)
		{
			return new CompareFunctionCondition<E>(CompareCondition.Operator.GreaterEqual, this, right);
		}

		public ExtremumAggregate<E> min()
		{
			return new ExtremumAggregate<E>(this, true);
		}
		
		public ExtremumAggregate<E> max()
		{
			return new ExtremumAggregate<E>(this, false);
		}

		public final BindItemFunction<E> bind(final Join join)
		{
			return new BindItemFunction<E>(this, join);
		}
		
		public CompareFunctionCondition equalTarget()
		{
			return equal(getValueType().thisFunction);
		}
		
		public CompareFunctionCondition equalTarget(final Join targetJoin)
		{
			return equal(getValueType().thisFunction.bind(targetJoin));
		}
		
		public InstanceOfCondition<E> instanceOf(final Type<? extends E> type1)
		{
			return new InstanceOfCondition<E>(this, false, type1);
		}

		public InstanceOfCondition<E> instanceOf(final Type<? extends E> type1, final Type<? extends E> type2)
		{
			return new InstanceOfCondition<E>(this, false, type1, type2);
		}

		public InstanceOfCondition<E> instanceOf(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3)
		{
			return new InstanceOfCondition<E>(this, false, type1, type2, type3);
		}

		public InstanceOfCondition<E> instanceOf(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3, final Type<E> type4)
		{
			return new InstanceOfCondition<E>(this, false, type1, type2, type3, type4);
		}

		public InstanceOfCondition<E> instanceOf(final Type[] types)
		{
			return new InstanceOfCondition<E>(this, false, types);
		}
		
		public InstanceOfCondition<E> notInstanceOf(final Type<? extends E> type1)
		{
			return new InstanceOfCondition<E>(this, true, type1);
		}

		public InstanceOfCondition<E> notInstanceOf(final Type<? extends E> type1, final Type<? extends E> type2)
		{
			return new InstanceOfCondition<E>(this, true, type1, type2);
		}

		public InstanceOfCondition<E> notInstanceOf(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3)
		{
			return new InstanceOfCondition<E>(this, true, type1, type2, type3);
		}

		public InstanceOfCondition<E> notInstanceOf(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3, final Type<E> type4)
		{
			return new InstanceOfCondition<E>(this, true, type1, type2, type3, type4);
		}

		public InstanceOfCondition<E> notInstanceOf(final Type[] types)
		{
			return new InstanceOfCondition<E>(this, true, types);
		}

		@Deprecated
		public InstanceOfCondition<E> typeIn(final Type<? extends E> type1)
		{
			return instanceOf(type1);
		}

		@Deprecated
		public InstanceOfCondition<E> typeIn(final Type<? extends E> type1, final Type<? extends E> type2)
		{
			return instanceOf(type1, type2);
		}

		@Deprecated
		public InstanceOfCondition<E> typeIn(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3)
		{
			return instanceOf(type1, type2, type3);
		}

		@Deprecated
		public InstanceOfCondition<E> typeIn(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3, final Type<E> type4)
		{
			return instanceOf(type1, type2, type3, type4);
		}

		@Deprecated
		public InstanceOfCondition<E> typeIn(final Type[] types)
		{
			return instanceOf(types);
		}
		
		@Deprecated
		public InstanceOfCondition<E> typeNotIn(final Type<? extends E> type1)
		{
			return notInstanceOf(type1);
		}

		@Deprecated
		public InstanceOfCondition<E> typeNotIn(final Type<? extends E> type1, final Type<? extends E> type2)
		{
			return notInstanceOf(type1, type2);
		}

		@Deprecated
		public InstanceOfCondition<E> typeNotIn(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3)
		{
			return notInstanceOf(type1, type2, type3);
		}

		@Deprecated
		public InstanceOfCondition<E> typeNotIn(final Type<? extends E> type1, final Type<? extends E> type2, final Type<? extends E> type3, final Type<E> type4)
		{
			return notInstanceOf(type1, type2, type3, type4);
		}

		@Deprecated
		public InstanceOfCondition<E> typeNotIn(final Type[] types)
		{
			return notInstanceOf(types);
		}
	}
}
