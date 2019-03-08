/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.CastUtils.toIntCapped;
import static com.exedio.cope.Executor.integerResultSetHandler;
import static com.exedio.cope.Executor.longResultSetHandler;
import static com.exedio.cope.FeatureSubSet.features;
import static com.exedio.cope.Intern.intern;
import static com.exedio.cope.misc.Check.requireNonNegative;
import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSortedSet;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.ItemField.DeletePolicy;
import com.exedio.cope.misc.LocalizationKeys;
import com.exedio.cope.misc.SetValueUtil;
import com.exedio.cope.util.Cast;
import com.exedio.cope.util.CharSet;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.InvalidObjectException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

@SuppressWarnings("ComparableImplementedButEqualsNotOverridden") // OK: compareTo just changes order, but not equality
public final class Type<T extends Item> implements SelectType<T>, Comparable<Type<?>>, AbstractType<T>
{
	private final Class<T> javaClass;
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final AnnotatedElement annotationSource;
	private final boolean bound;
	private static final CharSet ID_CHAR_SET = new CharSet('-', '-', '0', '9', 'A', 'Z', 'a', 'z');
	final String id;
	final String schemaId;
	final int typeColumnMinLength;
	private final Pattern pattern;
	final boolean isAbstract;
	final Type<? super T> supertype;
	final Type<? super T> toptype;
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final IdentityHashMap<Type<?>,Void> supertypes;

	@SuppressWarnings("ThisEscapedInObjectConstruction")
	final This<T> thisFunction = new This<>(this);
	private final List<? extends Feature> featuresDeclared;
	private final List<? extends Feature> features;
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
	private final Map<FunctionField<?>,List<CopyConstraint>> copyConstraintsByCopyField;

	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final Constructor<T> activationConstructor;
	final long createLimit;
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final SequenceX primaryKeySequence;
	private final boolean uniqueConstraintsProblem;

	final boolean external;
	final int expectedMaxRowSize;

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
	 * @see #asSuper(Class)
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

	/**
	 * @see #as(Class)
	 */
	public <X extends Item> Type<? super X> asSuper(final Class<X> clazz)
	{
		if(!javaClass.isAssignableFrom(clazz))
			throw new ClassCastException("expected ? super " + clazz.getName() + ", but was " + javaClass.getName());

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
		this.schemaId = intern(schemaNameAnnotation!=null ? schemaNameAnnotation.value() : id);
		this.pattern = pattern;
		this.isAbstract = isAbstract;
		this.supertype = supertype;
		this.typeColumnMinLength = getTypeColumnMinLength();

		if(supertype==null)
		{
			this.toptype = this;

			this.supertypes = null;
		}
		else
		{
			this.toptype = supertype.toptype;

			final IdentityHashMap<Type<?>,Void> superSupertypes = supertype.supertypes;
			if(superSupertypes==null)
				this.supertypes = new IdentityHashMap<>(1);
			else
				this.supertypes = new IdentityHashMap<>(superSupertypes);

			this.supertypes.put(supertype, null);
		}

		// declared features
		this.featuresWhileConstruction = new ArrayList<>(featuresParameter.size() + 1);
		//noinspection ThisEscapedInObjectConstruction
		thisFunction.mount(this, This.NAME, null);
		//noinspection ThisEscapedInObjectConstruction
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
				if(declaredFeaturesByName.putIfAbsent(feature.getName(), feature)!=null)
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
				final List<? extends Feature> superFeatures = supertype.getFeatures();
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
			final List<? extends Feature> df = this.featuresDeclared;
			this.fields            = features(s==null ? null : s.fields           , df, cast(Field.class));
			this.uniqueConstraints = features(s==null ? null : s.uniqueConstraints, df, UniqueConstraint.class);
			this. checkConstraints = features(s==null ? null : s. checkConstraints, df, CheckConstraint.class);
			this.  copyConstraints = features(s==null ? null : s.  copyConstraints, df, CopyConstraint.class);
		}
		{
			final LinkedHashMap<FunctionField<?>,List<CopyConstraint>> byCopy = new LinkedHashMap<>();
			for(final CopyConstraint cc : copyConstraints.all)
				if(!cc.isChoice())
					byCopy.computeIfAbsent(cc.getCopyField(), k -> new ArrayList<>()).add(cc);

			this.copyConstraintsByCopyField = byCopy.isEmpty() ? emptyMap() : unmodifiableMap(byCopy);
		}
		checkForDuplicateUniqueConstraint(id, uniqueConstraints.all);

		this.activationConstructor = getActivationConstructor(javaClass);

		if(supertype!=null)
		{
			createLimit = supertype.createLimit;
			if(getAnnotation(CopeCreateLimit.class)!=null)
				throw new IllegalArgumentException(
						"@" + CopeCreateLimit.class.getSimpleName() + " is allowed on top-level types only, " +
						"but " + id + " has super type " + supertype.id);
		}
		else
		{
			final CopeCreateLimit ann = getAnnotation(CopeCreateLimit.class);
			createLimit =
				ann!=null
				? requireNonNegative(ann.value(), "@" + CopeCreateLimit.class.getSimpleName() + " of " + id) // use Supplier in JDK 1.8
				: Integer.MAX_VALUE;
		}

		this.primaryKeySequence =
			supertype!=null
			? supertype.primaryKeySequence
			: new SequenceX(
					thisFunction,
					com.exedio.dsmf.Sequence.Type.fromMaxValueLenient(createLimit),
					PK.MIN_VALUE,
					PK.MIN_VALUE,
					createLimit);

		this.uniqueConstraintsProblem = (supertype!=null) && (supertype.uniqueConstraintsProblem || !uniqueConstraints.all.isEmpty());

		this.external = isAnnotationPresent(CopeExternal.class);
		if(supertype!=null && this.external!=supertype.external)
			throw new IllegalArgumentException(
					"@"+CopeExternal.class.getSimpleName() +
					" must be set consistently at type and supertype");

		this.expectedMaxRowSize =
				fields.all.size() +
				(1/*this*/ + 1/*class*/ + 1/*catch*/) * ((supertypes!=null?supertypes.size():0) + 1/*myself*/);
	}

	@SuppressWarnings({"unchecked", "rawtypes"}) // TODO remove
	private static Class<Field<?>> cast(final Class<Field> c)
	{
		return (Class)c;
	}

	private int getTypeColumnMinLength()
	{
		final CopeTypeColumnMinLength annotation = getAnnotation(CopeTypeColumnMinLength.class);
		if(annotation==null)
			return 0;

		final int value = annotation.value();
		if(value<=0)
			throw new IllegalArgumentException(
					"illegal @" + CopeTypeColumnMinLength.class.getSimpleName() +
					" for type " + id +
					", must be greater zero, but was " + value);
		return value;
	}

	private static HashMap<String, Feature> inherit(final HashMap<String, Feature> inherited, final HashMap<String, Feature> declared)
	{
		final HashMap<String, Feature> result = new HashMap<>(inherited);
		result.putAll(declared);
		return result;
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

		if(mountIfMounted!=null)
			throw new RuntimeException(id);
		if(table!=null)
			throw new RuntimeException();
		if(cacheIdTransiently>=0)
			throw new RuntimeException();

		mountIfMounted = new Mount<>(model, id, parameters);
		cacheIdTransiently = parameters.cacheIdTransiently;
	}

	void assertNotMounted()
	{
		if(mountIfMounted!=null)
			throw new IllegalStateException("type " + id + " already mounted");
	}

	private Mount<T> mount()
	{
		final Mount<T> result = mountIfMounted;
		if(result==null)
			throw new IllegalStateException("type " + id + " (" + javaClass.getName() + ") does not belong to any model");
		return result;
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
		final SortedSet<String> typesOfInstancesColumnValues;
		final Marshaller<C> marshaller;

		final List<ItemField<C>> declaredReferences;
		final List<ItemField<? super C>> references;

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
					final TreeSet<String> typesOfInstancesColumnValues = new TreeSet<>();
					for(final Type<?> t : typesOfInstances)
					{
						if(typesOfInstancesMap.putIfAbsent(t.schemaId, t)!=null)
							throw new RuntimeException(t.schemaId);
						if(!typesOfInstancesColumnValues.add(t.schemaId))
							throw new RuntimeException(t.schemaId);
					}
					this.typesOfInstancesMap = castTypeInstanceHasMap(typesOfInstancesMap);
					this.typesOfInstancesColumnValues = unmodifiableSortedSet(typesOfInstancesColumnValues);
					this.marshaller = new PolymorphicItemMarshaller<>(this.typesOfInstancesMap);
					this.onlyPossibleTypeOfInstances = null;
					break;
			}

			this.declaredReferences = castDeclaredReferences(parameters.getReferences());
			final Type<?> supertype = parameters.type.supertype;
			if(supertype!=null)
			{
				@SuppressWarnings("unchecked")
				final List<ItemField<?>> inherited = (List<ItemField<?>>)supertype.getReferences();
				final List<ItemField<C>> declared = declaredReferences;
				if(declared.isEmpty())
					this.references = castReferences(inherited);
				else if(inherited.isEmpty())
					this.references = castReferences(declared);
				else
				{
					final ArrayList<ItemField<?>> result = new ArrayList<>(inherited);
					result.addAll(declared);
					result.trimToSize();
					this.references = Collections.unmodifiableList(castReferences(result));
				}
			}
			else
			{
				this.references = castReferences(declaredReferences);
			}
		}

		@SuppressWarnings({"unchecked", "static-method"})
		private List<Type<? extends C>> castTypeInstanceList(final List<Type<?>> l)
		{
			return (List)l;
		}

		@SuppressWarnings({"unchecked", "rawtypes", "static-method"})
		private HashMap<String, Type<? extends C>> castTypeInstanceHasMap(final HashMap m)
		{
			return m;
		}

		@SuppressWarnings({"unchecked", "static-method"})
		private List<ItemField<C>> castDeclaredReferences(final List<ItemField<?>> l)
		{
			return (List)l;
		}

		@SuppressWarnings({"unchecked", "rawtypes", "static-method"})
		private List<ItemField<? super C>> castReferences(final List l)
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

			return Integer.compare(orderIdTransiently, o.orderIdTransiently);
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

		if(mountIfMounted==null)
			throw new RuntimeException();
		if(table!=null)
			throw new RuntimeException();

		this.table = new Table(
				database,
				schemaId,
				supertype,
				typeColumnMinLength,
				mount().typesOfInstancesColumnValues,
				hasUpdateableTable(),
				createLimit);
		if(supertype==null)
		{
			primaryKeySequence.connectPrimaryKey(database, table.primaryKey);
			database.addSequence(primaryKeySequence);
		}

		for(final Field<?> a : fields.declared)
			a.connect(table);
		for(final UniqueConstraint uc : uniqueConstraints.declared)
			uc.connect(table);
		table.setUniqueConstraints(uniqueConstraints.declared);
		table.setCheckConstraints (checkConstraints.declared);
		table.finish();
		for(final Feature f : featuresDeclared)
			if(f instanceof Sequence)
				((Sequence)f).connect(database);
	}

	private boolean hasUpdateableTable()
	{
		for(final Field<?> f : fields.all)
			if(!f.isFinal())
				return true;
		for(final Type<?> t : getSubtypes())
			if(t.hasUpdateableTable())
				return true;
		return false;
	}

	void disconnect()
	{
		if(mountIfMounted==null)
			throw new RuntimeException();
		if(table==null)
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

	@Override
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
	 * Returns the id of this type.
	 * Type ids are unique within a {@link Model model}.
	 * Use {@link Model#getType(String)} for lookup.
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
	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // typesOfInstances is unmodifiable
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

	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // typesOfInstancesColumnValues is unmodifiable
	SortedSet<String> getTypesOfInstancesColumnValues()
	{
		return mount().typesOfInstancesColumnValues;
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
	 * @deprecated Use {@link #checkSequenceBehindPrimaryKey()}.{@link SequenceBehindInfo#isBehindBy() isBehindBy}() instead
	 */
	@Deprecated
	public int checkPrimaryKey()
	{
		return toIntCapped(checkSequenceBehindPrimaryKey().isBehindBy());
	}

	/**
	 * @throws IllegalStateException is a transaction is bound to the current thread
	 */
	public SequenceBehindInfo checkSequenceBehindPrimaryKey()
	{
		return primaryKeySequence.check(getModel(), table.primaryKey);
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
	@Override
	public Type<? super T> getSupertype()
	{
		return supertype;
	}

	/**
	 * Returns all types whose {@link #getSupertype() super type}
	 * is this type.
	 * @see #getSubtypesTransitively()
	 */
	@Override
	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
	public List<? extends Type<? extends T>> getSubtypes()
	{
		return mount().subtypes;
	}

	/**
	 * Returns a list of all {@link #getSubtypes() subtypes}
	 * and all subtypes of the subtypes etc.
	 * The result includes this type as well, as any type is its
	 * own zeroth-order subtype.
	 * @see #getSubtypes()
	 */
	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
	public List<? extends Type<? extends T>> getSubtypesTransitively()
	{
		return mount().subtypesTransitively;
	}

	public boolean isAssignableFrom(final Type<?> type)
	{
		if(this==type)
			return true;

		final IdentityHashMap<Type<?>,Void> typeSupertypes = type.supertypes;
		if(typeSupertypes==null)
			return false;

		return typeSupertypes.containsKey(this);
	}

	boolean overlaps(final Type<?> other)
	{
		return
				this .isAssignableFrom(other) ||
				other.isAssignableFrom(this);
	}

	/**
	 * @see Class#cast(Object)
	 */
	public Type<? extends T> castTypeExtends(final Type<?> subtype)
	{
		if(subtype==null)
			return null;
		if(!isAssignableFrom(subtype))
			throw new ClassCastException("expected a " + this + ", but was a " + subtype);

		@SuppressWarnings("unchecked") // OK: checked at runtime
		final Type<T> result = (Type)subtype;
		return result;
	}

	void assertBelongs(final Field<?> f)
	{
		if(!f.getType().isAssignableFrom(this))
			throw new IllegalArgumentException("field " + f + " does not belong to type " + this);
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
	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // declaredReferences is unmodifiable
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
	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // references is unmodifiable
	public List<ItemField<? super T>> getReferences()
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
	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // declared is unmodifiable
	public List<? extends Field<?>> getDeclaredFields()
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
	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // all is unmodifiable
	public List<? extends Field<?>> getFields()
	{
		return fields.all;
	}

	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // featuresDeclared is unmodifiable
	@Override
	public List<? extends Feature> getDeclaredFeatures()
	{
		return featuresDeclared;
	}

	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // features is unmodifiable
	@Override
	public List<? extends Feature> getFeatures()
	{
		return features;
	}

	@Override
	public Feature getDeclaredFeature(final String name)
	{
		return featuresByNameDeclared.get(name);
	}

	@Override
	public Feature getFeature(final String name)
	{
		return featuresByName.get(name);
	}

	private List<String> localizationKeysIfInitialized = null;

	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // result of LocalizationKeys#get is unmodifiable
	@Override
	public List<String> getLocalizationKeys()
	{
		if(localizationKeysIfInitialized!=null)
			return localizationKeysIfInitialized;

		localizationKeysIfInitialized = LocalizationKeys.get(javaClass);
		return localizationKeysIfInitialized;
	}

	@SuppressWarnings({"TypeParameterExtendsFinalClass", "AssignmentOrReturnOfFieldWithMutableType"}) // OK: effectively makes collection somewhat compiler-unmodifiable; declared is unmodifiable
	public List<? extends UniqueConstraint> getDeclaredUniqueConstraints()
	{
		return uniqueConstraints.declared;
	}

	@SuppressWarnings({"TypeParameterExtendsFinalClass", "AssignmentOrReturnOfFieldWithMutableType"}) // OK: effectively makes collection somewhat compiler-unmodifiable; all is unmodifiable
	public List<? extends UniqueConstraint> getUniqueConstraints()
	{
		return uniqueConstraints.all;
	}

	@SuppressWarnings({"TypeParameterExtendsFinalClass", "AssignmentOrReturnOfFieldWithMutableType"}) // OK: effectively makes collection somewhat compiler-unmodifiable; declared is unmodifiable
	public List<? extends CheckConstraint> getDeclaredCheckConstraints()
	{
		return checkConstraints.declared;
	}

	@SuppressWarnings({"TypeParameterExtendsFinalClass", "AssignmentOrReturnOfFieldWithMutableType"}) // OK: effectively makes collection somewhat compiler-unmodifiable; all is unmodifiable
	public List<? extends CheckConstraint> getCheckConstraints()
	{
		return checkConstraints.all;
	}

	@SuppressWarnings({"TypeParameterExtendsFinalClass", "AssignmentOrReturnOfFieldWithMutableType"}) // OK: effectively makes collection somewhat compiler-unmodifiable; declared is unmodifiable
	public List<? extends CopyConstraint> getDeclaredCopyConstraints()
	{
		return copyConstraints.declared;
	}

	@SuppressWarnings({"TypeParameterExtendsFinalClass", "AssignmentOrReturnOfFieldWithMutableType"}) // OK: effectively makes collection somewhat compiler-unmodifiable; all is unmodifiable
	public List<? extends CopyConstraint> getCopyConstraints()
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
		return new ItemField<>(javaClass, future(this), policy);
	}

	private static <T extends Item> TypeFuture<T> future(final Type<T> type)
	{
		// NOTE: static helper method enforces static inner class
		return new TypeFuture<T>()
		{
			@Override
			public Type<T> get()
			{
				return type;
			}
			@Override
			public String toString()
			{
				return type.id;
			}
		};
	}

	/**
	 * @see CopeCreateLimit
	 */
	public long getCreateLimit()
	{
		return createLimit;
	}

	public T newItem(final List<SetValue<?>> setValues)
	{
		return newItem(SetValueUtil.toArray(setValues));
	}

	private static final SetValue<?>[] EMPTY_SET_VALUES = {};

	public T newItem(SetValue<?>... setValues)
	{
		if(isAbstract)
			throw new IllegalArgumentException("cannot create item of abstract type " + id);

		if(setValues==null)
			setValues = EMPTY_SET_VALUES;

		final FieldValues fieldValues = prepareCreate(setValues);
		final long pk = nextPrimaryKey();
		final T result = activate(pk);
		result.doCreate(fieldValues);
		return result;
	}

	FieldValues prepareCreate(final SetValue<?>[] setValues)
	{
		final FieldValues fieldValues = executeCreate(setValues);

		checkUniqueConstraints(fieldValues);
		checkCheckConstraints(fieldValues);
		checkCopyConstraints(fieldValues);
		checkSettables(setValues, fieldValues);

		return fieldValues;
	}

	FieldValues executeCreate(SetValue<?>[] setValues)
	{
		setValues = getModel().changeHook.beforeNew(this, setValues);
		final FieldValues fieldValues = new FieldValues(this, setValues);
		executeCopyConstraints(fieldValues);

		DefaultSupplier.Context ctx = null;
		for(final Field<?> field : fields.all)
		{
			if(field instanceof FunctionField<?> && !fieldValues.isDirty(field))
			{
				final FunctionField<?> ff = (FunctionField<?>)field;
				final DefaultSupplier<?> defaultS = ff.defaultS;
				if(defaultS!=null)
				{
					if(ctx==null)
						ctx = new DefaultSupplier.Context();

					final Object defaultValue = defaultS.generate(ctx);
					if(defaultValue==null)
						throw new RuntimeException(ff.getID());
					fieldValues.setDirty(field, defaultValue);
				}
			}
		}
		fieldValues.checkNonDirtyMandatoryOnCreate();

		return fieldValues;
	}

	void executeCopyConstraints(final FieldValues fieldValues)
	{
		for(final Map.Entry<FunctionField<?>,List<CopyConstraint>> e : copyConstraintsByCopyField.entrySet())
		{
			final FunctionField<?> copy = e.getKey();
			if(fieldValues.isDirty(copy))
				continue;
			// do not touch final fields when changing item
			if(fieldValues.getBackingItem()!=null && copy.isFinal())
				continue;

			Object value = null;
			CopyConstraint copyConstraintForValue = null;
			Item targetItemForValue = null;
			for(final CopyConstraint cc : e.getValue())
			{
				final Item targetItem = fieldValues.get(cc.getTarget());
				if(targetItem==null)
					continue;

				final Object template = cc.getTemplate().get(targetItem);
				if(copyConstraintForValue==null)
				{
					value = template;
					copyConstraintForValue = cc;
					targetItemForValue = targetItem;
				}
				else
				{
					if(!Objects.equals(value, template))
					{
						throw new CopyViolationException(
							fieldValues,
							targetItemForValue, targetItem,
							copyConstraintForValue, cc,
							value, template
						);
					}
				}
			}
			if(copyConstraintForValue!=null)
				fieldValues.setDirty(copy, value);
		}
	}

	void checkUniqueConstraints(final FieldValues fieldValues)
	{
		if(!uniqueConstraintsProblem && getModel().connect().supportsUniqueViolation)
			return;

		for(final UniqueConstraint uc : uniqueConstraints.all)
			uc.check(fieldValues);
	}

	void checkCheckConstraints(final FieldValues item)
	{
		for(final CheckConstraint cc : checkConstraints.all)
			cc.check(item);
	}

	void checkCopyConstraints(final FieldValues fieldValues)
	{
		for(final CopyConstraint cc : copyConstraints.all)
			cc.check(fieldValues);
	}

	void checkSettables(
			final SetValue<?>[] setValues,
			final FieldValues fieldValues)
	{
		for(final SetValue<?> sv : setValues)
			if(sv.settable instanceof CheckingSettable<?>)
				check(sv, fieldValues);
	}

	private static <E> void check(
			final SetValue<E> sv,
			final FieldValues fieldValues)
	{
		((CheckingSettable<E>)sv.settable).check(sv.value, fieldValues);
	}

	long nextPrimaryKey()
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
	 * result in an {@code UnsupportedOperationException}.
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
	 * result in an {@code UnsupportedOperationException}.
	 *
	 * @param condition the condition the searched items must match.
	 */
	public List<T> search(final Condition condition)
	{
		return newQuery(condition).search();
	}

	/**
	 * Searches for items of this type, that match the given condition.
	 * The result is sorted by the given function {@code orderBy}.
	 * <p>
	 * Returns an unmodifiable collection.
	 * Any attempts to modify the returned collection, whether direct or via its iterator,
	 * result in an {@code UnsupportedOperationException}.
	 *
	 * @param condition the condition the searched items must match.
	 * @param ascending whether the result is sorted ascendingly ({@code true</tt>) or descendingly (<tt>false}).
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

	@Override
	@SuppressFBWarnings("EQ_COMPARETO_USE_OBJECT_EQUALS") // Class defines compareTo(...) and uses Object.equals()
	public int compareTo(final Type<?> o)
	{
		if(this==o)
			return 0;

		return mount().compareTo(o.mount());
	}

	@Override
	public String toString()
	{
		return id;
	}

	T getItemObject(final long pk)
	{
		final Entity entity = getModel().currentTransaction().getEntityIfActive(this, pk);
		if(entity!=null)
			return cast(entity.getItem());
		else
			return activate(pk);
	}

	T activate(final long pk)
	{
		final ActivationParameters ap = new ActivationParameters(this, pk);
		try
		{
			return activationConstructor.newInstance(ap);
		}
		catch(final ReflectiveOperationException e)
		{
			throw new RuntimeException(ap.toString() + '/' + javaClass.getName(), e);
		}
	}

	private static void checkForDuplicateUniqueConstraint(
			final String id,
			@SuppressWarnings("TypeParameterExtendsFinalClass") // OK: effectively makes collection somewhat compiler-unmodifiable
			final List<? extends UniqueConstraint> constraints)
	{
		if(constraints.size()<=1)
			return;

		for(final UniqueConstraint a : constraints)
		{
			for(final UniqueConstraint b : constraints)
			{
				if(a==b)
					continue; // do not compare twice

				final List<FunctionField<?>> af = a.getFields();
				final List<FunctionField<?>> bf = b.getFields();
				if(new HashSet<>(af).equals(
					new HashSet<>(bf)))
				{
					throw new IllegalArgumentException(
							"duplicate unique constraints at type " + id + ": " + a + " and " + b +
							" with fields " + af + (af.equals(bf) ? "" : (" and " + bf)) + '.');
				}
			}
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

		final T item = activate(createLimit);
		if(item.type!=this)
			throw new IllegalArgumentException(id + '/' + javaClass.getName());
		if(item.pk!=createLimit)
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
	public long checkCompletenessL(final Type<? extends T> subType)
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

		return executor.query(tx.getConnection(), bf, null, false, longResultSetHandler);
	}

	public boolean needsCheckUpdateCounter()
	{
		return supertype!=null && getTable().updateCounter!=null;
	}

	public long checkUpdateCounterL()
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

		return executor.query(tx.getConnection(), bf, null, false, longResultSetHandler);
	}

	public Random random(final int seed)
	{
		return new Random(this, seed);
	}

	// serialization -------------

	private static final long serialVersionUID = 1l;

	/**
	 * <a href="https://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/output.html#5324">See Spec</a>
	 */
	private Object writeReplace() throws ObjectStreamException
	{
		final Mount<?> mount = mountIfMounted;
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
		 * <a href="https://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/input.html#5903">See Spec</a>
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
	 * @deprecated Use {@link #checkUpdateCounterL()} instead
	 */
	@Deprecated
	public int checkUpdateCounter()
	{
		return toIntCapped(checkUpdateCounterL());
	}

	/**
	 * @deprecated Use {@link #checkCompletenessL(Type)} instead
	 */
	@Deprecated
	public int checkCompleteness(final Type<? extends T> subType)
	{
		return toIntCapped(checkCompletenessL(subType));
	}

	/**
	 * @deprecated Use {@link #castTypeExtends(Type)} instead
	 */
	@Deprecated
	public Type<? extends T> asSubtype(final Type<?> subtype)
	{
		return castTypeExtends(subtype);
	}

	/**
	 * @deprecated Use {@link #forClass(Class)} instead
	 */
	@Deprecated
	public static <X extends Item> Type<X> findByJavaClass(final Class<X> javaClass)
	{
		return forClass(javaClass);
	}

	/**
	 * @deprecated Use {@link #forClassUnchecked(Class)} instead
	 */
	@Deprecated
	public static Type<?> findByJavaClassUnchecked(final Class<? extends Item> javaClass)
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
	public List<? extends Field<?>> getDeclaredAttributes()
	{
		return getDeclaredFields();
	}

	/**
	 * @deprecated Renamed to {@link #getFields()}.
	 */
	@Deprecated
	public List<? extends Field<?>> getAttributes()
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
	public List<? extends Type<? extends T>> getSubTypes()
	{
		return getSubtypes();
	}

	/**
	 * @deprecated Use {@link #getSubtypesTransitively()} instead
	 */
	@SuppressFBWarnings("NM_CONFUSING") // Confusing method names, the referenced methods have names that differ only by capitalization.
	@Deprecated
	public List<? extends Type<? extends T>> getSubTypesTransitively()
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
	public static <X extends Item> Type<X> forClass(final Class<X> javaClass)
	{
		return TypesBound.forClass(javaClass);
	}

	/**
	 * @deprecated Use {@link TypesBound#forClassUnchecked(Class)} instead.
	 */
	@Deprecated
	public static Type<?> forClassUnchecked(final Class<? extends Item> javaClass)
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
