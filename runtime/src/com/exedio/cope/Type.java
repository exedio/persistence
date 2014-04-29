/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.Executor.integerResultSetHandler;
import static com.exedio.cope.FeatureSubSet.features;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.ItemField.DeletePolicy;
import com.exedio.cope.misc.Compare;
import com.exedio.cope.misc.SetValueUtil;
import com.exedio.cope.util.Cast;
import com.exedio.cope.util.CharSet;
import com.exedio.cope.util.Clock;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.InvalidObjectException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Type<T extends Item> implements SelectType<T>, Comparable<Type<?>>, Serializable
{
	private final Class<T> javaClass;
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final AnnotatedElement annotationSource;
	private final boolean bound;
	private static final CharSet ID_CHAR_SET = new CharSet('-', '-', '0', '9', 'A', 'Z', 'a', 'z');
	final String id;
	final String schemaId;
	private final Pattern pattern;
	final boolean isAbstract;
	final Type<? super T> supertype;
	final Type<? super T> toptype;
	private final HashSet<Type<?>> supertypes;

	final This<T> thisFunction = new This<>(this);
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final List<Feature> featuresDeclared;
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final List<Feature> features;
	private final HashMap<String, Feature> featuresByNameDeclared;
	private final HashMap<String, Feature> featuresByName;

	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final FeatureSubSet<Field<?>> fields;

	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final FeatureSubSet<UniqueConstraint> uniqueConstraints;

	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final FeatureSubSet<CheckConstraint> checkConstraints;

	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final FeatureSubSet<CopyConstraint> copyConstraints;

	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final Constructor<T> activationConstructor;
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final Method[] beforeNewItemMethods;
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final SequenceX primaryKeySequence;
	private final boolean uniqueConstraintsProblem;

	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private Mount<T> mountIfMounted = null;

	/**
	 * This id uniquely identifies a type within its model.
	 * However, this id is not stable across JVM restarts.
	 * So never put this id into any persistent storage,
	 * nor otherwise make this id accessible outside the library.
	 * <p>
	 * This id is negative for abstract types and positive
	 * (including zero) for non-abstract types.
	 */
	int cacheIdTransiently = Integer.MIN_VALUE;

	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	Table table;

	/**
	 * @see #asExtends(Class)
	 * @see Class#asSubclass(Class)
	 */
	public <X extends Item> Type<X> as(final Class<X> clazz)
	{
		if(javaClass!=clazz)
			throw new ClassCastException("expected " + clazz.getName() + ", but was " + javaClass.getName());

		@SuppressWarnings("unchecked") // OK: is checked on runtime
		final Type<X> result = (Type<X>)this;
		return result;
	}

	/**
	 * @see #as(Class)
	 * @see Class#asSubclass(Class)
	 */
	public <X extends Item> Type<? extends X> asExtends(final Class<X> clazz)
	{
		if(!clazz.isAssignableFrom(javaClass))
			throw new ClassCastException("expected ? extends " + clazz.getName() + ", but was " + javaClass.getName());

		@SuppressWarnings("unchecked") // OK: is checked on runtime
		final Type<X> result = (Type<X>)this;
		return result;
	}

	private ArrayList<Feature> featuresWhileConstruction;

	Type(
			final Class<T> javaClass,
			final AnnotatedElement annotationSource,
			final boolean bound,
			final String id,
			final Pattern pattern,
			final boolean isAbstract,
			final Type<? super T> supertype,
			final Features featuresParameter)
	{
		requireNonNull(javaClass, "javaClass"); // TODO test
		if(!Item.class.isAssignableFrom(javaClass))
			throw new IllegalArgumentException(javaClass + " is not a subclass of Item");
		if(javaClass.equals(Item.class))
			throw new IllegalArgumentException("Cannot make a type for " + javaClass + " itself, but only for subclasses.");

		if(annotationSource==null)
			throw new NullPointerException(javaClass.getName());
		if(!isAbstract && Modifier.isAbstract(javaClass.getModifiers()))
			throw new IllegalArgumentException("Cannot make a non-abstract type for abstract " + javaClass + '.'); // TODO test
		if(id==null)
			throw new NullPointerException("id for " + javaClass); // TODO test
		{
			final int i = ID_CHAR_SET.indexOfNotContains(id);
			if(i>=0)
				throw new IllegalArgumentException("id >" + id + "< of type contains illegal character >" + id.charAt(i) + "< at position " + i);
		}
		if(featuresParameter==null)
			throw new NullPointerException("featuresParameter for " + id); // TODO test

		this.javaClass = javaClass;
		this.annotationSource = annotationSource;
		this.bound = bound;
		this.id = id;
		final CopeSchemaName schemaNameAnnotation = getAnnotation(CopeSchemaName.class);
		this.schemaId = schemaNameAnnotation!=null ? schemaNameAnnotation.value() : id;
		this.pattern = pattern;
		this.isAbstract = isAbstract;
		this.supertype = supertype;

		if(supertype==null)
		{
			this.toptype = this;

			this.supertypes = null;
		}
		else
		{
			this.toptype = supertype.toptype;

			final HashSet<Type<?>> superSupertypes = supertype.supertypes;
			if(superSupertypes==null)
				this.supertypes = new HashSet<>();
			else
				this.supertypes = new HashSet<>(superSupertypes);

			this.supertypes.add(supertype);
		}

		// declared features
		this.featuresWhileConstruction = new ArrayList<>(featuresParameter.size() + 1);
		thisFunction.mount(this, This.NAME, null);
		featuresParameter.mount(this);
		featuresWhileConstruction.trimToSize();
		this.featuresDeclared = Collections.unmodifiableList(featuresWhileConstruction);
		// make sure, method registerMounted fails from now on
		this.featuresWhileConstruction = null;
		assert thisFunction==this.featuresDeclared.get(0) : this.featuresDeclared;

		// declared fields / unique constraints
		{
			final HashMap<String, Feature> declaredFeaturesByName = new HashMap<>();
			for(final Feature feature : featuresDeclared)
			{
				if(declaredFeaturesByName.put(feature.getName(), feature)!=null)
					throw new RuntimeException(feature.getName() + '/' + javaClass.getName()); // Features must prevent this
			}
			this.featuresByNameDeclared = declaredFeaturesByName;
		}

		// inherit features / fields / constraints
		if(supertype==null)
		{
			this.features          = this.featuresDeclared;
			this.featuresByName    = this.featuresByNameDeclared;
		}
		else
		{
			{
				final ArrayList<Feature> features = new ArrayList<>();
				features.add(thisFunction);
				final List<Feature> superFeatures = supertype.getFeatures();
				features.addAll(superFeatures.subList(1, superFeatures.size()));
				features.addAll(this.featuresDeclared.subList(1, this.featuresDeclared.size()));
				features.trimToSize();
				this.features = Collections.unmodifiableList(features);
			}
			this.featuresByName    = inherit(supertype.featuresByName,    this.featuresByNameDeclared);
		}
		assert thisFunction==this.features.get(0) : this.features;
		assert thisFunction==this.featuresByName.get(This.NAME) : this.featuresByName;

		{
			final Type<? super T> s = this.supertype;
			final List<Feature> df = this.featuresDeclared;
			this.fields            = features(s==null ? null : s.fields           , df, cast(Field.class));
			this.uniqueConstraints = features(s==null ? null : s.uniqueConstraints, df, UniqueConstraint.class);
			this. checkConstraints = features(s==null ? null : s. checkConstraints, df, CheckConstraint.class);
			this.  copyConstraints = features(s==null ? null : s.  copyConstraints, df, CopyConstraint.class);
		}

		this.activationConstructor = getActivationConstructor(javaClass);
		this.beforeNewItemMethods = getBeforeNewItemMethods(javaClass, supertype);

		this.primaryKeySequence =
			supertype!=null
			? supertype.primaryKeySequence
			: new SequenceX(thisFunction, PK.MIN_VALUE, PK.MIN_VALUE, PK.MAX_VALUE);

		this.uniqueConstraintsProblem = (supertype!=null) && (supertype.uniqueConstraintsProblem || !uniqueConstraints.all.isEmpty());
	}

	@SuppressWarnings({"unchecked", "rawtypes"}) // TODO remove
	private static Class<Field<?>> cast(final Class<Field> c)
	{
		return (Class)c;
	}

	private static final HashMap<String, Feature> inherit(final HashMap<String, Feature> inherited, final HashMap<String, Feature> declared)
	{
		final HashMap<String, Feature> result = new HashMap<>(inherited);
		result.putAll(declared);
		return result;
	}

	private static Method[] getBeforeNewItemMethods(final Class<?> javaClass, final Type<?> supertype)
	{
		final Method declared = getBeforeNewItemMethod(javaClass);
		final Method[] inherited = supertype!=null ? supertype.beforeNewItemMethods : null;
		if(declared==null)
			return inherited;
		else if(inherited==null)
			return new Method[]{declared};
		else
		{
			final Method[] result = new Method[inherited.length+1];
			result[0] = declared;
			System.arraycopy(inherited, 0, result, 1, inherited.length);
			return result;
		}
	}

	private static Method getBeforeNewItemMethod(final Class<?> javaClass)
	{
		final Method result;
		try
		{
			result = javaClass.getDeclaredMethod("beforeNewCopeItem", SetValue[].class);
		}
		catch(final NoSuchMethodException e)
		{
			return null;
		}

		if(!Modifier.isStatic(result.getModifiers()))
			throw new IllegalArgumentException(
					"method beforeNewCopeItem(SetValue[]) in class " + javaClass.getName() + " must be static");
		if(!SetValue[].class.equals(result.getReturnType()))
			throw new IllegalArgumentException(
					"method beforeNewCopeItem(SetValue[]) in class " + javaClass.getName() + " must return SetValue[], " +
							"but returns " + result.getReturnType().getName());

		result.setAccessible(true);
		return result;
	}

	SetValue<?>[] doBeforeNewItem(SetValue<?>[] setValues)
	{
		if(beforeNewItemMethods!=null)
		{
			try
			{
				for(final Method m : beforeNewItemMethods)
					setValues = (SetValue[])m.invoke(null, (Object)setValues);
			}
			catch(final InvocationTargetException e)
			{
				final Throwable cause = e.getCause();
				if(cause instanceof RuntimeException)
					throw (RuntimeException)cause;
				throw new RuntimeException(id, e);
			}
			catch(final IllegalAccessException e)
			{
				throw new RuntimeException(id, e);
			}
		}

		return setValues;
	}

	void registerMounted(final Feature feature)
	{
		featuresWhileConstruction.add(feature);
	}

	void mount(final Model model, final Types.MountParameters parameters)
	{
		if(model==null)
			throw new RuntimeException();
		assert this==parameters.type;

		if(this.mountIfMounted!=null)
			throw new RuntimeException(id);
		if(this.table!=null)
			throw new RuntimeException();
		if(this.cacheIdTransiently>=0)
			throw new RuntimeException();

		this.mountIfMounted = new Mount<>(model, id, parameters);
		this.cacheIdTransiently = parameters.cacheIdTransiently;
	}

	void assertNotMounted()
	{
		if(mountIfMounted!=null)
			throw new IllegalStateException("type " + id + " already mounted");
	}

	private Mount<T> mount()
	{
		final Mount<T> mount = this.mountIfMounted;
		if(mount==null)
			throw new IllegalStateException("type " + id + " (" + javaClass.getName() + ") does not belong to any model");
		return mount;
	}

	private static final class Mount<C extends Item>
	{
		final Model model;

		private final String id;

		/**
		 * This id uniquely identifies a type within its model.
		 * However, this id is not stable across JVM restarts.
		 * So never put this id into any persistent storage,
		 * nor otherwise make this id accessible outside the library.
		 * <p>
		 * This id is positive (including zero) for all types.
		 */
		private final int orderIdTransiently;

		final List<Type<? extends C>> subtypes;
		final List<Type<? extends C>> subtypesTransitively;
		final List<Type<? extends C>> typesOfInstances;

		final HashMap<String, Type<? extends C>> typesOfInstancesMap;
		final Type<? extends C> onlyPossibleTypeOfInstances;
		final String[] typesOfInstancesColumnValues;
		final Marshaller<C> marshaller;

		final List<ItemField<C>> declaredReferences;
		final List<ItemField<?>> references;

		Mount(final Model model, final String id, final Types.MountParameters parameters)
		{
			this.model = model;
			this.id = id;
			this.orderIdTransiently = parameters.orderIdTransiently;

			this.subtypes = castTypeInstanceList(parameters.getSubtypes());
			this.subtypesTransitively = castTypeInstanceList(parameters.getSubtypesTransitively());
			this.typesOfInstances = castTypeInstanceList(parameters.getTypesOfInstances());

			switch(typesOfInstances.size())
			{
				case 0:
					throw new RuntimeException("type " + parameters.type.id + " is abstract and has no non-abstract (even indirect) subtypes");
				case 1:
					this.typesOfInstancesMap = null;
					this.onlyPossibleTypeOfInstances = typesOfInstances.iterator().next();
					this.marshaller = new SimpleItemMarshaller<>(onlyPossibleTypeOfInstances);
					this.typesOfInstancesColumnValues = null;
					break;
				default:
					final HashMap<String, Type<?>> typesOfInstancesMap = new HashMap<>();
					this.typesOfInstancesColumnValues = new String[typesOfInstances.size()];
					int i = 0;
					for(final Type<?> t : typesOfInstances)
					{
						if(typesOfInstancesMap.put(t.schemaId, t)!=null)
							throw new RuntimeException(t.schemaId);
						typesOfInstancesColumnValues[i++] = t.schemaId;
					}
					this.typesOfInstancesMap = castTypeInstanceHasMap(typesOfInstancesMap);
					this.marshaller = new PolymorphicItemMarshaller<>(this.typesOfInstancesMap);
					this.onlyPossibleTypeOfInstances = null;
					break;
			}

			this.declaredReferences = castDeclaredReferences(parameters.getReferences());
			final Type<?> supertype = parameters.type.supertype;
			if(supertype!=null)
			{
				final List<ItemField<?>> inherited = supertype.getReferences();
				final List<ItemField<C>> declared = declaredReferences;
				if(declared.isEmpty())
					this.references = inherited;
				else if(inherited.isEmpty())
					this.references = castReferences(declared);
				else
				{
					final ArrayList<ItemField<?>> result = new ArrayList<>(inherited);
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

		@SuppressWarnings({"unchecked", "rawtypes", "static-method"})
		private List<Type<? extends C>> castTypeInstanceList(final List<Type<?>> l)
		{
			return (List)l;
		}

		@SuppressWarnings({"unchecked", "rawtypes", "static-method"})
		private HashMap<String, Type<? extends C>> castTypeInstanceHasMap(final HashMap m)
		{
			return m;
		}

		@SuppressWarnings({"unchecked", "rawtypes", "static-method"})
		private List<ItemField<C>> castDeclaredReferences(final List<ItemField<?>> l)
		{
			return (List)l;
		}

		@SuppressWarnings({"unchecked", "rawtypes", "static-method"})
		private List<ItemField<?>> castReferences(final List l)
		{
			return l;
		}

		@SuppressFBWarnings("EQ_COMPARETO_USE_OBJECT_EQUALS") // Class defines compareTo(...) and uses Object.equals()
		int compareTo(final Mount<?> o)
		{
			if(model!=o.model)
				throw new IllegalArgumentException(
						"types are not comparable, " +
						"because they do not belong to the same model: " +
						id + " (" + model + ") and " +
						o.id + " (" + o.model + ").");

			return Compare.compare(orderIdTransiently, o.orderIdTransiently);
		}
	}

	/**
	 * @see Class#isAnnotationPresent(Class)
	 */
	public boolean isAnnotationPresent(final Class<? extends Annotation> annotationClass)
	{
		return annotationSource.isAnnotationPresent(annotationClass);
	}

	/**
	 * @see Class#getAnnotation(Class)
	 */
	public <A extends Annotation> A getAnnotation(final Class<A> annotationClass)
	{
		return annotationSource.getAnnotation(annotationClass);
	}

	void connect(final Database database)
	{
		if(database==null)
			throw new RuntimeException();

		if(this.mountIfMounted==null)
			throw new RuntimeException();
		if(this.table!=null)
			throw new RuntimeException();

		this.table = new Table(
				database,
				schemaId,
				supertype,
				mount().typesOfInstancesColumnValues,
				!hasFinalTable());
		if(supertype==null)
		{
			primaryKeySequence.connect(database, table.primaryKey);
			database.addSequence(primaryKeySequence);
		}

		for(final Field<?> a : fields.declared)
			a.connect(table);
		for(final UniqueConstraint uc : uniqueConstraints.declared)
			uc.connect(table);
		this.table.setUniqueConstraints(this.uniqueConstraints.declared);
		this.table.setCheckConstraints (this.checkConstraints.declared);
		this.table.finish();
		for(final Feature f : featuresDeclared)
			if(f instanceof Sequence)
				((Sequence)f).connect(database);
	}

	private boolean hasFinalTable()
	{
		for(final Field<?> f : fields.all)
			if(!f.isFinal())
				return false;
		for(final Type<?> t : getSubtypes())
			if(!t.hasFinalTable())
				return false;
		return true;
	}

	void disconnect()
	{
		if(this.mountIfMounted==null)
			throw new RuntimeException();
		if(this.table==null)
			throw new RuntimeException();

		table = null;
		if(supertype==null)
			primaryKeySequence.disconnect();

		for(final Field<?> a : fields.declared)
			a.disconnect();
		for(final UniqueConstraint uc : uniqueConstraints.declared)
			uc.disconnect();
		for(final Feature f : featuresDeclared)
			if(f instanceof Sequence)
				((Sequence)f).disconnect();
	}

	public Class<T> getJavaClass()
	{
		return javaClass;
	}

	/**
	 * Returns, whether this type bound to it's java class.
	 * Only such types can be found by
	 * {@link TypesBound#forClass(Class)} and
	 * {@link TypesBound#forClassUnchecked(Class)}.
	 */
	public boolean isBound()
	{
		return bound;
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
		return mount().model;
	}

	/**
	 * Returns a list of types,
	 * that instances (items) of this type can have.
	 * These are all subtypes of this type,
	 * including indirect subtypes,
	 * and including this type itself,
	 * which are not abstract.
	 */
	public List<Type<? extends T>> getTypesOfInstances()
	{
		return mount().typesOfInstances;
	}

	Type<? extends T> getTypeOfInstance(final String id)
	{
		return mount().typesOfInstancesMap.get(id);
	}

	Type<? extends T> getOnlyPossibleTypeOfInstances()
	{
		return mount().onlyPossibleTypeOfInstances;
	}

	Marshaller<?> getMarshaller()
	{
		return mount().marshaller;
	}

	@SuppressFBWarnings("PZLA_PREFER_ZERO_LENGTH_ARRAYS")
	String[] getTypesOfInstancesColumnValues()
	{
		final String[] typesOfInstancesColumnValues = mount().typesOfInstancesColumnValues;

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
		if(table==null)
			throw new RuntimeException();

		return table;
	}

	public SequenceInfo getPrimaryKeyInfo()
	{
		return primaryKeySequence.getInfo();
	}

	/**
	 * @throws IllegalStateException is a transaction is bound to the current thread
	 */
	public int checkPrimaryKey()
	{
		return primaryKeySequence.check(getModel());
	}

	String getPrimaryKeySequenceSchemaName()
	{
		return primaryKeySequence.getSchemaName();
	}

	/**
	 * Returns the type representing the {@link Class#getSuperclass() superclass}
	 * of this type's {@link #getJavaClass() java class}.
	 * If this type has no super type
	 * (i.e. the superclass of this type's java class is {@link Item}),
	 * then null is returned.
	 */
	public Type<? super T> getSupertype()
	{
		return supertype;
	}

	/**
	 * @see #getSubtypesTransitively()
	 */
	public List<Type<? extends T>> getSubtypes()
	{
		return mount().subtypes;
	}

	/**
	 * @see #getSubtypes()
	 */
	public List<Type<? extends T>> getSubtypesTransitively()
	{
		return mount().subtypesTransitively;
	}

	public boolean isAssignableFrom(final Type<?> type)
	{
		if(this==type)
			return true;

		final HashSet<Type<?>> typeSupertypes = type.supertypes;
		if(typeSupertypes==null)
			return false;

		return typeSupertypes.contains(this);
	}

	/**
	 * @see Class#asSubclass(Class)
	 */
	public Type<? extends T> asSubtype(final Type<?> subtype)
	{
		if(subtype==null)
			return null;
		if(!isAssignableFrom(subtype))
			throw new ClassCastException("expected a " + toString() + ", but was a " + subtype);

		@SuppressWarnings({"unchecked", "rawtypes"}) // OK: checked at runtime
		final Type<T> result = (Type)subtype;
		return result;
	}

	void assertBelongs(final Field<?> f)
	{
		if(!f.getType().isAssignableFrom(this))
			throw new IllegalArgumentException("field " + f + " does not belong to type " + this.toString());
	}

	public boolean isAbstract()
	{
		return isAbstract;
	}

	public This<T> getThis()
	{
		return thisFunction;
	}

	/**
	 * Returns all {@link ItemField}s of the model this type belongs to,
	 * which {@link ItemField#getValueType value type} equals this type.
	 * @see #getReferences()
	 */
	public List<ItemField<T>> getDeclaredReferences()
	{
		return mount().declaredReferences;
	}

	/**
	 * Returns all {@link ItemField}s of the model this type belongs to,
	 * which {@link ItemField#getValueType value type} equals this type
	 * or any of it's super types.
	 * @see #getDeclaredReferences()
	 */
	public List<ItemField<?>> getReferences()
	{
		return mount().references;
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
	public List<Field<?>> getDeclaredFields()
	{
		return fields.declared;
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
	public List<Field<?>> getFields()
	{
		return fields.all;
	}

	public List<Feature> getDeclaredFeatures()
	{
		return featuresDeclared;
	}

	public List<Feature> getFeatures()
	{
		return features;
	}

	public Feature getDeclaredFeature(final String name)
	{
		return featuresByNameDeclared.get(name);
	}

	public Feature getFeature(final String name)
	{
		return featuresByName.get(name);
	}

	public List<UniqueConstraint> getDeclaredUniqueConstraints()
	{
		return uniqueConstraints.declared;
	}

	public List<UniqueConstraint> getUniqueConstraints()
	{
		return uniqueConstraints.all;
	}

	public List<CheckConstraint> getDeclaredCheckConstraints()
	{
		return checkConstraints.declared;
	}

	public List<CheckConstraint> getCheckConstraints()
	{
		return checkConstraints.all;
	}

	public List<CopyConstraint> getDeclaredCopyConstraints()
	{
		return copyConstraints.declared;
	}

	public List<CopyConstraint> getCopyConstraints()
	{
		return copyConstraints.all;
	}

	/**
	 * @see Pattern#getSourceTypes()
	 */
	public Pattern getPattern()
	{
		return pattern;
	}

	public ItemField<T> newItemField(final DeletePolicy policy)
	{
		return new ItemField<>(new Future<>(javaClass, this), policy);
	}

	private static final class Future<T extends Item> extends TypeFuture<T>
	{
		private final Type<T> type;

		Future(final Class<T> javaClass, final Type<T> type)
		{
			super(javaClass);
			this.type = type;
		}

		@Override
		Type<T> get()
		{
			return type;
		}

		@Override
		public String toString()
		{
			return type.id;
		}
	}

	public T newItem(final List<SetValue<?>> setValues)
		throws ConstraintViolationException
	{
		return newItem(SetValueUtil.toArray(setValues));
	}

	private static final SetValue<?>[] EMPTY_SET_VALUES = {};

	public T newItem(SetValue<?>... setValues)
		throws ConstraintViolationException
	{
		if(isAbstract)
			throw new IllegalArgumentException("cannot create item of abstract type " + id);

		if(setValues==null)
			setValues = EMPTY_SET_VALUES;

		final LinkedHashMap<Field<?>, Object> fieldValues = prepareCreate(setValues);
		final int pk = nextPrimaryKey();
		final T result = activate(pk);
		result.doCreate(fieldValues);
		return result;
	}

	@SuppressFBWarnings("WMI_WRONG_MAP_ITERATOR") // Inefficient use of keySet iterator instead of entrySet iterator
	LinkedHashMap<Field<?>, Object> executeCreate(SetValue<?>[] setValues)
	{
		setValues = doBeforeNewItem(setValues);
		final LinkedHashMap<Field<?>, Object> fieldValues = Item.executeSetValues(setValues, null);
		long now = Long.MIN_VALUE;
		boolean needsNow = true;
		for(final Field<?> field : fields.all)
		{
			if(field instanceof FunctionField<?> && !fieldValues.containsKey(field))
			{
				final FunctionField<?> ff = (FunctionField<?>)field;
				final DefaultSource<?> defaultSource = ff.defaultSource;
				if(defaultSource!=null)
				{
					if(needsNow)
					{
						now = Clock.currentTimeMillis();
						needsNow = false;
					}

					final Object defaultValue = defaultSource.generate(now);
					if(defaultValue==null)
						throw new RuntimeException(ff.getID());
					fieldValues.put(field, defaultValue);
				}
			}
		}
		for(final Field<?> field : fieldValues.keySet())
		{
			assertBelongs(field);
		}
		for(final Field<?> field : fields.all)
		{
			field.check(fieldValues.get(field), null);
		}

		return fieldValues;
	}

	LinkedHashMap<Field<?>, Object> prepareCreate(final SetValue<?>[] setValues)
	{
		final LinkedHashMap<Field<?>, Object> fieldValues = executeCreate(setValues);

		checkUniqueConstraints(null, fieldValues);

		for(final CopyConstraint cc : copyConstraints.all)
			cc.check(fieldValues);

		return fieldValues;
	}

	void checkUniqueConstraints(final Item item, final Map<? extends Field<?>, ?> fieldValues)
	{
		if(!uniqueConstraintsProblem && getModel().connect().executor.supportsUniqueViolation)
			return;

		for(final UniqueConstraint uc : uniqueConstraints.all)
			uc.check(item, fieldValues);
	}

	void checkCheckConstraints(final Item item, final Entity entity, final Item exceptionItem)
	{
		for(final CheckConstraint cc : checkConstraints.all)
			cc.check(item, entity, exceptionItem);
	}

	int nextPrimaryKey()
	{
		return primaryKeySequence.next();
	}

	public T cast(final Item item)
	{
		return Cast.verboseCast(javaClass, item);
	}

	/**
	 * Searches for all items of this type.
	 * <p>
	 * Returns an unmodifiable collection.
	 * Any attempts to modify the returned collection, whether direct or via its iterator,
	 * result in an <tt>UnsupportedOperationException</tt>.
	 */
	public List<T> search()
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
	public List<T> search(final Condition condition)
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
	public List<T> search(final Condition condition, final Function<?> orderBy, final boolean ascending)
	{
		final Query<T> query = newQuery(condition);
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
	public T searchSingleton(final Condition condition)
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
	public T searchSingletonStrict(final Condition condition)
	{
		return newQuery(condition).searchSingletonStrict();
	}

	public Query<T> newQuery()
	{
		return newQuery(null);
	}

	public Query<T> newQuery(final Condition condition)
	{
		return new Query<>(thisFunction, this, condition);
	}

	public Query<T> emptyQuery()
	{
		return new Query<>(thisFunction, this, Condition.FALSE);
	}

	@SuppressFBWarnings("EQ_COMPARETO_USE_OBJECT_EQUALS") // Class defines compareTo(...) and uses Object.equals()
	public int compareTo(final Type<?> o)
	{
		return mount().compareTo(o.mount());
	}

	@Override
	public String toString()
	{
		return id;
	}

	T getItemObject(final int pk)
	{
		final Entity entity = getModel().currentTransaction().getEntityIfActive(this, pk);
		if(entity!=null)
			return cast(entity.getItem());
		else
			return activate(pk);
	}

	T activate(final int pk)
	{
		final ActivationParameters ap = new ActivationParameters(this, pk);
		try
		{
			return activationConstructor.newInstance(ap);
		}
		catch(final InstantiationException e)
		{
			throw new RuntimeException(ap.toString() + '/' + javaClass.getName(), e);
		}
		catch(final IllegalAccessException e)
		{
			throw new RuntimeException(ap.toString() + '/' + javaClass.getName(), e);
		}
		catch(final InvocationTargetException e)
		{
			throw new RuntimeException(ap.toString() + '/' + javaClass.getName(), e);
		}
	}

	private static <C> Constructor<C> getActivationConstructor(final Class<C> javaClass)
	{
		final Constructor<C> result;
		try
		{
			result = javaClass.getDeclaredConstructor(ActivationParameters.class);
		}
		catch(final NoSuchMethodException e)
		{
			throw new IllegalArgumentException(
					javaClass.getName() + " does not have an activation constructor " +
					javaClass.getSimpleName() + '(' + ActivationParameters.class.getName() + ')', e);
		}

		result.setAccessible(true);
		return result;
	}

	void testActivation()
	{
		if(isAbstract)
			return;

		final T item = activate(PK.MAX_VALUE);
		if(item.type!=this)
			throw new IllegalArgumentException(id + '/' + javaClass.getName());
		if(item.pk!=PK.MAX_VALUE)
			throw new IllegalArgumentException(id + '/' + javaClass.getName());
	}

	boolean needsCheckTypeColumn()
	{
		return supertype!=null && supertype.getTable().typeColumn!=null;
	}

	int checkTypeColumn()
	{
		final Transaction tx = getModel().currentTransaction();
		final Executor executor = tx.connect.executor;
		final Table table = getTable();
		final Table superTable = supertype.getTable();

		final Statement bf = executor.newStatement(true);
		bf.append("SELECT COUNT(*) FROM ").
			append(table).append(',').append(superTable).
			append(" WHERE ").
			append(table.primaryKey).append('=').append(superTable.primaryKey).
			append(" AND ");

		if(table.typeColumn!=null)
			bf.append(table.typeColumn);
		else
			bf.appendParameter(getOnlyPossibleTypeOfInstances().schemaId);

		bf.append("<>").append(superTable.typeColumn);

		//System.out.println("CHECKT:"+bf.toString());

		return executor.query(tx.getConnection(), bf, null, false, integerResultSetHandler);
	}

	/**
	 * @param subType is allowed any type from {@link #getTypesOfInstances()}, but not itself.
	 */
	public int checkCompleteness(final Type<? extends T> subType)
	{
		requireNonNull(subType, "subType");
		if(equals(subType) || !getTypesOfInstances().contains(subType))
			throw new IllegalArgumentException("expected instantiable subtype of " + this + ", but was " + subType);

		final Transaction tx = getModel().currentTransaction();
		final Executor executor = tx.connect.executor;
		final Table table = getTable();
		final Table subTable = subType.getTable();

		final Statement bf = executor.newStatement(true);
		bf.append("SELECT COUNT(*) FROM ").append(table).
			append(" LEFT JOIN ").append(subTable).
			append(" ON ").append(table.primaryKey).append('=').append(subTable.primaryKey).
			append(" WHERE ").append(subTable.primaryKey).append(" IS NULL");
		if(table.typeColumn!=null)
			bf.append(" AND ").append(table.typeColumn).append('=').appendParameter(subType.schemaId);

		return executor.query(tx.getConnection(), bf, null, false, integerResultSetHandler);
	}

	public boolean needsCheckUpdateCounter()
	{
		return supertype!=null && getTable().updateCounter!=null;
	}

	public int checkUpdateCounter()
	{
		if(!needsCheckUpdateCounter())
			throw new RuntimeException("no check for update counter needed for " + this);

		final Transaction tx = getModel().currentTransaction();
		final Executor executor = tx.connect.executor;
		final Table table = getTable();
		final Table superTable = supertype.getTable();

		final Statement bf = executor.newStatement(true);
		bf.append("SELECT COUNT(*) FROM ").
			append(table).append(',').append(superTable).
			append(" WHERE ").
			append(table.primaryKey).append('=').append(superTable.primaryKey).
			append(" AND ").
			append(table.updateCounter).append("<>").append(superTable.updateCounter);

		//System.out.println("CHECKM:"+bf.toString());

		return executor.query(tx.getConnection(), bf, null, false, integerResultSetHandler);
	}

	public Random random(final int seed)
	{
		return new Random(this, seed);
	}

	// serialization -------------

	private static final long serialVersionUID = 1l;

	/**
	 * <a href="http://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/output.html#5324">See Spec</a>
	 */
	private Object writeReplace() throws ObjectStreamException
	{
		final Mount<?> mount = this.mountIfMounted;
		if(mount==null)
			throw new NotSerializableException(Type.class.getName());

		return new Serialized(mount.model, id);
	}

	/**
	 * Block malicious data streams.
	 * @see #writeReplace()
	 */
	@SuppressWarnings("static-method")
	private void readObject(@SuppressWarnings("unused") final ObjectInputStream ois) throws InvalidObjectException
	{
		throw new InvalidObjectException("required " + Serialized.class);
	}

	/**
	 * Block malicious data streams.
	 * @see #writeReplace()
	 */
	@SuppressWarnings("static-method")
	private Object readResolve() throws InvalidObjectException
	{
		throw new InvalidObjectException("required " + Serialized.class);
	}

	private static final class Serialized implements Serializable
	{
		private static final long serialVersionUID = 1l;

		private final Model model;
		private final String id;

		Serialized(final Model model, final String id)
		{
			this.model = model;
			this.id = id;
		}

		/**
		 * <a href="http://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/input.html#5903">See Spec</a>
		 */
		private Object readResolve() throws InvalidObjectException
		{
			final Type<?> result = model.getType(id);
			if(result==null)
				throw new InvalidObjectException("type does not exist: " + id);
			return result;
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
	public List<Field<?>> getDeclaredAttributes()
	{
		return getDeclaredFields();
	}

	/**
	 * @deprecated Renamed to {@link #getFields()}.
	 */
	@Deprecated
	public List<Field<?>> getAttributes()
	{
		return getFields();
	}

	/**
	 * @deprecated renamed to {@link #searchSingleton(Condition)}.
	 */
	@Deprecated
	public T searchUnique(final Condition condition)
	{
		return searchSingleton(condition);
	}

	/**
	 * @deprecated Use {@link #as(Class)} instead
	 */
	@Deprecated
	public <X extends Item> Type<X> castType(final Class<X> clazz)
	{
		return as(clazz);
	}

	/**
	 * @deprecated Use {@link #isBound()} instead
	 */
	@Deprecated
	public boolean hasUniqueJavaClass()
	{
		return isBound();
	}

	/**
	 * @deprecated Use {@link #getSubtypes()} instead
	 */
	@SuppressFBWarnings("NM_CONFUSING") // Confusing method names, the referenced methods have names that differ only by capitalization.
	@Deprecated
	public List<Type<? extends T>> getSubTypes()
	{
		return getSubtypes();
	}

	/**
	 * @deprecated Use {@link #getSubtypesTransitively()} instead
	 */
	@SuppressFBWarnings("NM_CONFUSING") // Confusing method names, the referenced methods have names that differ only by capitalization.
	@Deprecated
	public List<Type<? extends T>> getSubTypesTransitively()
	{
		return getSubtypesTransitively();
	}

	/**
	 * @deprecated Use {@link #isBound()} instead
	 */
	@Deprecated
	public boolean isJavaClassExclusive()
	{
		return isBound();
	}

	/**
	 * @deprecated Use {@link TypesBound#forClass(Class)} instead.
	 */
	@Deprecated
	public static final <X extends Item> Type<X> forClass(final Class<X> javaClass)
	{
		return TypesBound.forClass(javaClass);
	}

	/**
	 * @deprecated Use {@link TypesBound#forClassUnchecked(Class)} instead.
	 */
	@Deprecated
	public static final Type<?> forClassUnchecked(final Class<?> javaClass)
	{
		return TypesBound.forClassUnchecked(javaClass);
	}

	/**
	 * @deprecated Use {@link #needsCheckUpdateCounter()} instead
	 */
	@Deprecated
	public boolean needsCheckModificationCounter()
	{
		return needsCheckUpdateCounter();
	}

	/**
	 * @deprecated Use {@link #checkUpdateCounter()} instead
	 */
	@Deprecated
	public int checkModificationCounter()
	{
		return checkUpdateCounter();
	}
}
