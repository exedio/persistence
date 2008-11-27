/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import java.lang.annotation.Annotation;
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

import com.exedio.cope.CompareFunctionCondition.Operator;
import com.exedio.cope.Field.Option;
import com.exedio.cope.ItemField.DeletePolicy;
import com.exedio.cope.search.ExtremumAggregate;
import com.exedio.cope.util.CharSet;
import com.exedio.cope.util.ReactivationConstructorDummy;

public final class Type<C extends Item>
{
	private static final HashMap<Class<? extends Item>, Type<? extends Item>> typesByClass = new HashMap<Class<? extends Item>, Type<? extends Item>>();

	private final Class<C> javaClass;
	private final boolean uniqueJavaClass;
	private static final CharSet ID_CHAR_SET = new CharSet('.', '.', '0', '9', 'A', 'Z', 'a', 'z');
	final String id;
	private final Pattern pattern;
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
	
	private final List<CopyConstraint> declaredCopyConstraints;
	final List<CopyConstraint> copyConstraints;

	private final Constructor<C> creationConstructor;
	private final Constructor<C> reactivationConstructor;
	private final PkSource pkSource;

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

	Table table;
	
	/**
	 * @throws IllegalArgumentException if there is no type for the given java class.
	 * @see #hasUniqueJavaClass()
	 */
	public static final <X extends Item> Type<X> forClass(final Class<X> javaClass)
	{
		return forClassUnchecked(javaClass).castType(javaClass);
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
	public static final Type<?> forClassUnchecked(final Class<?> javaClass)
	{
		final Type<? extends Item> result = typesByClass.get(javaClass);
		if(result==null)
			throw new IllegalArgumentException("there is no type for " + javaClass);
		return result;
	}
	
	private ArrayList<Feature> featuresWhileConstruction;
	
	Type(final Class<C> javaClass)
	{
		this(javaClass, true, getID(javaClass), null, getFeatureMap(javaClass));
	}
	
	private static final String getID(final Class<?> javaClass)
	{
		final CopeID annotation = javaClass.getAnnotation(CopeID.class);
		return
			annotation!=null
			? annotation.value()
			: javaClass.getSimpleName();
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
					if(Feature.class.isAssignableFrom(field.getType()))
					{
						field.setAccessible(true);
						final Feature feature = (Feature)field.get(null);
						if(feature==null)
							throw new RuntimeException(javaClass.getName() + '-' + field.getName());
						result.put(field.getName(), feature);
						feature.setAnnotationField(field);
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
	
	Type(
			final Class<C> javaClass,
			final boolean uniqueJavaClass,
			final String id,
			final Pattern pattern,
			final LinkedHashMap<String, Feature> featureMap)
	{
		{
			final int l = id.length();
			for(int i = 0; i<l; i++)
				if(!ID_CHAR_SET.contains(id.charAt(i)))
					throw new IllegalArgumentException("name >" + id + "< of feature in contains illegal character >"+ id.charAt(i) + "< at position " + i);
		}
		
		this.javaClass = javaClass;
		this.uniqueJavaClass = uniqueJavaClass;
		this.id = id;
		this.pattern = pattern;
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
			supertype = forClass(castSuperType(superClass));
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
			final ArrayList<CopyConstraint> declaredCopyConstraints = new ArrayList<CopyConstraint>(declaredFeatures.size());
			final HashMap<String, Feature> declaredFeaturesByName = new HashMap<String, Feature>();
			for(final Feature feature : declaredFeatures)
			{
				if(feature instanceof Field)
					declaredFields.add((Field)feature);
				else if(feature instanceof UniqueConstraint)
					declaredUniqueConstraints.add((UniqueConstraint)feature);
				else if(feature instanceof CopyConstraint)
					declaredCopyConstraints.add((CopyConstraint)feature);
				
				if(declaredFeaturesByName.put(feature.getName(), feature)!=null)
					throw new RuntimeException("duplicate feature "+feature.getName()+" for type "+javaClass.getName());
			}
			this.declaredFields            = finish(declaredFields);
			this.declaredUniqueConstraints = finish(declaredUniqueConstraints);
			this.declaredCopyConstraints   = finish(declaredCopyConstraints);
			this.declaredFeaturesByName = declaredFeaturesByName;
		}

		// inherit features / fields / constraints
		if(supertype==null)
		{
			this.features          = this.declaredFeatures;
			this.featuresByName    = this.declaredFeaturesByName;
			this.fields            = this.declaredFields;
			this.uniqueConstraints = this.declaredUniqueConstraints;
			this.copyConstraints   = this.declaredCopyConstraints;
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
			this.fields            = inherit(supertype.fields,            this.declaredFields);
			this.uniqueConstraints = inherit(supertype.uniqueConstraints, this.declaredUniqueConstraints);
			this.copyConstraints   = inherit(supertype.copyConstraints,   this.declaredCopyConstraints);
		}

		// IMPLEMENTATION NOTE
		// Here we don't precompute the constructor parameters
		// because they are needed in the initialization phase
		// only.
		this.creationConstructor = getConstructor("creation", SetValue[].class);
		this.reactivationConstructor = getConstructor("reactivation", ReactivationConstructorDummy.class, int.class);

		this.pkSource =
			supertype!=null
			? supertype.getPkSource()
			: new PkSource(this);
		
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
	
	private static final <F extends Feature> List<F> finish(final ArrayList<F> list)
	{
		switch(list.size())
		{
		case 0:
			return Collections.<F>emptyList();
		case 1:
			return Collections.singletonList(list.get(0));
		default:
			list.trimToSize();
			return Collections.<F>unmodifiableList(list);
		}
	}
	
	private Constructor<C> getConstructor(final String name, Class... parameterTypes)
	{
		if(!uniqueJavaClass)
		{
			final int l = parameterTypes.length;
			final Class[] c = new Class[l + 1];
			System.arraycopy(parameterTypes, 0, c, 0, l);
			c[l] = Type.class;
			parameterTypes = c;
		}
		
		try
		{
			final Constructor<C> result = javaClass.getDeclaredConstructor(parameterTypes);
			result.setAccessible(true);
			return result;
		}
		catch(NoSuchMethodException e)
		{
			throw new IllegalArgumentException(
					javaClass.getName() + " does not have a " +
					name + " constructor " +
					javaClass.getSimpleName() + '(' + toString(parameterTypes) + ')', e);
		}
	}
	
	void registerInitialization(final Feature feature)
	{
		featuresWhileConstruction.add(feature);
	}
	
	private static final String toString(final Class... parameterTypes)
	{
		if(parameterTypes.length==1)
			return parameterTypes[0].getCanonicalName();
		else
		{
			final StringBuilder bf = new StringBuilder(parameterTypes[0].getCanonicalName());
			for(int i = 1; i<parameterTypes.length; i++)
			{
				bf.append(',').
					append(parameterTypes[i].getCanonicalName());
			}
			return bf.toString();
		}
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
	
	/**
	 * @see Class#getAnnotation(Class)
	 */
	public <T extends Annotation> T getAnnotation(final Class<T> annotationClass)
	{
		return
			(uniqueJavaClass && javaClass!=null)
			? javaClass.getAnnotation(annotationClass)
			: null;
	}
	
	void connect(final Database database)
	{
		if(database==null)
			throw new RuntimeException();

		if(this.model==null)
			throw new RuntimeException();
		if(this.table!=null)
			throw new RuntimeException();
		
		final CopeSchemaName annotation = getAnnotation(CopeSchemaName.class);
		final String tableID = annotation!=null ? annotation.value(): id;
		this.table = new Table(database, tableID, supertype, typesOfInstancesColumnValues);

		for(final Field a : declaredFields)
			a.connect(table);
		for(final UniqueConstraint uc : declaredUniqueConstraints)
			uc.connect(table);
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
		pkSource.flush();
		
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
	 * {@link #forClass(Class)} and
	 * {@link #forClassUnchecked(Class)}.
	 */
	public boolean hasUniqueJavaClass()
	{
		return uniqueJavaClass;
	}
	
	/**
	 * @see Model#getType(String)
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
		return getPkSource().getInfo();
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
	
	public List<CopyConstraint> getDeclaredCopyConstraints()
	{
		return declaredCopyConstraints;
	}
	
	public List<CopyConstraint> getCopyConstraints()
	{
		return copyConstraints;
	}
	
	/**
	 * @see Pattern#getSourceTypes()
	 */
	public Pattern getPattern()
	{
		return pattern;
	}
	
	public ItemField<C> newItemField(final DeletePolicy policy)
	{
		return new ItemField<C>(this, policy);
	}
	
	private static final SetValue[] EMPTY_SET_VALUES = {};
	
	public C newItem(SetValue... setValues)
		throws ConstraintViolationException
	{
		if(setValues==null)
			setValues = EMPTY_SET_VALUES;
		try
		{
			return
				creationConstructor.newInstance(
					uniqueJavaClass
					? new Object[]{ setValues }
					: new Object[]{ setValues, this }
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
	 * returns the only element of the search result,
	 * if the result {@link Collection#size() size} is exactly one.
	 * @throws IllegalArgumentException if the search result size is greater than one.
	 * @see Query#searchSingleton()
	 * @see #searchSingletonStrict(Condition)
	 */
	public C searchSingleton(final Condition condition)
	{
		return newQuery(condition).searchSingleton();
	}
	
	/**
	 * Searches equivalently to {@link #search(Condition)},
	 * but assumes that the condition forces the search result to have exactly one element.
	 * <p>
	 * Returns the only element of the search result,
	 * if the result {@link Collection#size() size} is exactly one.
	 * @throws IllegalArgumentException if the search result size is not exactly one.
	 * @see Query#searchSingletonStrict()
	 * @see #searchSingleton(Condition)
	 */
	public C searchSingletonStrict(final Condition condition)
	{
		return newQuery(condition).searchSingletonStrict();
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
		getPkSource().flush();
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
		try
		{
			return
				reactivationConstructor.newInstance(
					uniqueJavaClass
					? new Object[]{ REACTIVATION_DUMMY, pk }
					: new Object[]{ REACTIVATION_DUMMY, pk, this }
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

	public static final class This<E extends Item> extends Feature implements Function<E>, ItemFunction<E>
	{
		private static final String NAME = "this";
		
		final Type<E> type;
		
		This(final Type<E> type)
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
		
		@Deprecated // OK: for internal use within COPE only
		public void check(final TC tc, final Join join)
		{
			tc.check(this, join);
		}
		
		@Deprecated // OK: for internal use within COPE only
		public void append(final Statement bf, final Join join)
		{
			bf.appendPK(type, join);
		}
		
		@Deprecated // OK: for internal use within COPE only
		public void appendSelect(final Statement bf, final Join join, final Holder<Column> columnHolder, final Holder<Type> typeHolder)
		{
			final Type selectType = getType();
			bf.appendPK(selectType, join);

			final IntegerColumn column = selectType.getTable().primaryKey;
			assert column.primaryKey;
			columnHolder.value = column;
			
			final StringColumn typeColumn = column.getTypeColumn();
			if(typeColumn!=null)
			{
				bf.append(',').
					append(typeColumn);
			}
			else
				typeHolder.value = selectType.getOnlyPossibleTypeOfInstances();
		}
		
		@Deprecated // OK: for internal use within COPE only
		public void appendType(final Statement bf, final Join join)
		{
			bf.append(Statement.assertTypeColumn(type.getTable().typeColumn, type), join);
		}
		
		@Deprecated // OK: for internal use within COPE only
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
			return new CompareCondition<E>(Operator.Less, this, value);
		}
		
		public CompareCondition<E> lessOrEqual(final E value)
		{
			return new CompareCondition<E>(Operator.LessEqual, this, value);
		}
		
		public CompareCondition<E> greater(final E value)
		{
			return new CompareCondition<E>(Operator.Greater, this, value);
		}
		
		public CompareCondition<E> greaterOrEqual(final E value)
		{
			return new CompareCondition<E>(Operator.GreaterEqual, this, value);
		}
		
		public Condition between(final E lowerBound, final E upperBound)
		{
			return greaterOrEqual(lowerBound).and(lessOrEqual(upperBound));
		}
		
		public CompareFunctionCondition<E> equal(final Function<E> right)
		{
			return new CompareFunctionCondition<E>(Operator.Equal, this, right);
		}

		public CompareFunctionCondition<E> notEqual(final Function<E> right)
		{
			return new CompareFunctionCondition<E>(Operator.NotEqual, this, right);
		}

		public final CompareFunctionCondition<E> less(final Function<E> right)
		{
			return new CompareFunctionCondition<E>(Operator.Less, this, right);
		}
		
		public final CompareFunctionCondition<E> lessOrEqual(final Function<E> right)
		{
			return new CompareFunctionCondition<E>(Operator.LessEqual, this, right);
		}
		
		public final CompareFunctionCondition<E> greater(final Function<E> right)
		{
			return new CompareFunctionCondition<E>(Operator.Greater, this, right);
		}
		
		public final CompareFunctionCondition<E> greaterOrEqual(final Function<E> right)
		{
			return new CompareFunctionCondition<E>(Operator.GreaterEqual, this, right);
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

		// ------------------- deprecated stuff -------------------
		
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
	
	// ------------------- deprecated stuff -------------------
	
	/**
	 * @deprecated Use {@link #forClass(Class)} instead
	 */
	@Deprecated
	public static final <X extends Item> Type<X> findByJavaClass(final Class<X> javaClass)
	{
		return forClass(javaClass);
	}

	/**
	 * @deprecated Use {@link #forClassUnchecked(Class)} instead
	 */
	@Deprecated
	public static final Type<?> findByJavaClassUnchecked(final Class<?> javaClass)
	{
		return forClassUnchecked(javaClass);
	}

	/**
	 * @deprecated Use {@link SchemaInfo#getTableName(Type)} instead
	 */
	@Deprecated
	public String getTableName()
	{
		return SchemaInfo.getTableName(this);
	}
	
	/**
	 * @deprecated Use {@link SchemaInfo#getPrimaryKeyColumnName(Type)} instead
	 */
	@Deprecated
	public String getPrimaryKeyColumnName()
	{
		return SchemaInfo.getPrimaryKeyColumnName(this);
	}
	
	/**
	 * @deprecated Use {@link SchemaInfo#getTypeColumnName(Type)} instead
	 */
	@Deprecated
	public String getTypeColumnName()
	{
		return SchemaInfo.getTypeColumnName(this);
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
	 * @deprecated Renamed to {@link #getFields()}.
	 */
	@Deprecated
	public List<Field> getAttributes()
	{
		return getFields();
	}
	
	/**
	 * @deprecated use {@link Field#toFinal()}, {@link FunctionField#unique()} and {@link Field#optional()} instead.
	 */
	@Deprecated
	public ItemField<C> newItemField(final Option option, final DeletePolicy policy)
	{
		return new ItemField<C>(option, this, policy);
	}
	
	/**
	 * @deprecated renamed to {@link #searchSingleton(Condition)}.
	 */
	@Deprecated
	public C searchUnique(final Condition condition)
	{
		return searchSingleton(condition);
	}
	
}
