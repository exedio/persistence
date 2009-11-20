/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.exedio.cope.ItemField.DeletePolicy;
import com.exedio.cope.info.SequenceInfo;
import com.exedio.cope.util.CharSet;

public final class Type<C extends Item>
{
	private final Class<C> javaClass;
	private final boolean bound;
	private static final CharSet ID_CHAR_SET = new CharSet('.', '.', '0', '9', 'A', 'Z', 'a', 'z');
	final String id;
	final String schemaId;
	private final Pattern pattern;
	final boolean isAbstract;
	final Type<? super C> supertype;
	private final HashSet<Type<?>> supertypes;
	
	final This<C> thisFunction = new This<C>(this);
	private final List<Feature> declaredFeatures;
	private final List<Feature> features;
	private final HashMap<String, Feature> declaredFeaturesByName;
	private final HashMap<String, Feature> featuresByName;

	private final List<Field> declaredFields;
	private final List<Field> fields;
	
	private final List<UniqueConstraint> declaredUniqueConstraints;
	private final List<UniqueConstraint> uniqueConstraints;
	
	private final List<CopyConstraint> declaredCopyConstraints;
	private final List<CopyConstraint> copyConstraints;

	private final Constructor<C> activationConstructor;
	private final Method[] beforeNewItemMethods;
	private final Sequence primaryKeySequence;
	
	private Mount<C> mount = null;
	
	/**
	 * This id uniquely identifies a type within its model.
	 * However, this id is not stable across JVM restarts.
	 * So never put this id into any persistent storage,
	 * nor otherwise make this id accessible outside the library.
	 * <p>
	 * This id is negative for abstract types and positive
	 * (including zero) for non-abstract types.
	 */
	int idTransiently = Integer.MIN_VALUE;

	Table table;
	
	@SuppressWarnings("unchecked") // OK: unchecked cast is checked manually using runtime type information
	public <X extends Item> Type<X> as(final Class<X> clazz)
	{
		if(javaClass!=clazz)
			throw new ClassCastException("expected " + clazz.getName() + ", but was " + javaClass.getName());
		
		return (Type<X>)this;
	}
	
	private ArrayList<Feature> featuresWhileConstruction;
	
	Type(
			final Class<C> javaClass,
			final boolean bound,
			final String id,
			final Pattern pattern,
			final boolean isAbstract,
			final Type<? super C> supertype,
			final LinkedHashMap<String, Feature> featureMap)
	{
		if(javaClass==null)
			throw new NullPointerException("javaClass");
		if(!Item.class.isAssignableFrom(javaClass))
			throw new IllegalArgumentException(javaClass + " is not a subclass of Item");
		if(javaClass.equals(Item.class))
			throw new IllegalArgumentException("Cannot make a type for " + javaClass + " itself, but only for subclasses.");
		if(id==null)
			throw new NullPointerException("id for " + javaClass);
		{
			final int l = id.length();
			for(int i = 0; i<l; i++)
				if(!ID_CHAR_SET.contains(id.charAt(i)))
					throw new IllegalArgumentException("name >" + id + "< of feature in contains illegal character >" + id.charAt(i) + "< at position " + i);
		}
		if(featureMap==null)
			throw new NullPointerException("featureMap for " + id);
		
		this.javaClass = javaClass;
		this.bound = bound;
		this.id = id;
		final CopeSchemaName schemaNameAnnotation = getAnnotation(CopeSchemaName.class);
		this.schemaId = schemaNameAnnotation!=null ? schemaNameAnnotation.value() : id;
		this.pattern = pattern;
		this.isAbstract = isAbstract;
		this.supertype = supertype;
		
		if(supertype==null)
		{
			this.supertypes = null;
		}
		else
		{
			final HashSet<Type<?>> superSupertypes = supertype.supertypes;
			if(superSupertypes==null)
				this.supertypes = new HashSet<Type<?>>();
			else
				this.supertypes = new HashSet<Type<?>>(superSupertypes);
			
			this.supertypes.add(supertype);
		}

		// declared features
		this.featuresWhileConstruction = new ArrayList<Feature>(featureMap.size() + 1);
		thisFunction.mount(this, This.NAME);
		for(final Map.Entry<String, Feature> entry : featureMap.entrySet())
			entry.getValue().mount(this, entry.getKey());
		featuresWhileConstruction.trimToSize();
		this.declaredFeatures = Collections.unmodifiableList(featuresWhileConstruction);
		// make sure, method registerMounted fails from now on
		this.featuresWhileConstruction = null;
		assert thisFunction==this.declaredFeatures.get(0) : this.declaredFeatures;

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
			this.featuresByName    = inherit(supertype.featuresByName,    this.declaredFeaturesByName);
			this.fields            = inherit(supertype.fields,            this.declaredFields);
			this.uniqueConstraints = inherit(supertype.uniqueConstraints, this.declaredUniqueConstraints);
			this.copyConstraints   = inherit(supertype.copyConstraints,   this.declaredCopyConstraints);
		}
		assert thisFunction==this.features.get(0) : this.features;
		assert thisFunction==this.featuresByName.get(This.NAME) : this.featuresByName;

		this.activationConstructor = getActivationConstructor(javaClass);
		this.beforeNewItemMethods = getBeforeNewItemMethods(javaClass, supertype);

		this.primaryKeySequence =
			supertype!=null
			? supertype.primaryKeySequence
			: new Sequence(thisFunction, PK.MIN_VALUE, PK.MIN_VALUE, PK.MAX_VALUE);
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
	
	private static final HashMap<String, Feature> inherit(final HashMap<String, Feature> inherited, final HashMap<String, Feature> declared)
	{
		final HashMap<String, Feature> result = new HashMap<String, Feature>(inherited);
		result.putAll(declared);
		return result;
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
	
	private static Method[] getBeforeNewItemMethods(final Class javaClass, final Type supertype)
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
		catch(NoSuchMethodException e)
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
	
	SetValue[] doBeforeNewItem(SetValue[] setValues)
	{
		if(beforeNewItemMethods!=null)
		{
			try
			{
				for(final Method m : beforeNewItemMethods)
					setValues = (SetValue[])m.invoke(null, (Object)setValues);
			}
			catch(InvocationTargetException e)
			{
				throw new RuntimeException(id, e);
			}
			catch(IllegalAccessException e)
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

		if(this.mount!=null)
			throw new IllegalStateException("type already mounted");
		if(this.table!=null)
			throw new RuntimeException();
		if(this.idTransiently>=0)
			throw new RuntimeException();
		
		this.mount = new Mount<C>(model, parameters);
		this.idTransiently = parameters.idTransiently;
	}
	
	private Mount<C> mount()
	{
		if(mount==null)
			throw new IllegalStateException("model not set for type " + id + ", probably you forgot to put this type into the model.");

		return mount;
	}
	
	private static final class Mount<C extends Item>
	{
		final Model model;
		
		final List<Type<? extends C>> subtypes;
		final List<Type<? extends C>> subtypesTransitively;
		final List<Type<? extends C>> typesOfInstances;
		
		final HashMap<String, Type<? extends C>> typesOfInstancesMap;
		final Type<? extends C> onlyPossibleTypeOfInstances;
		final String[] typesOfInstancesColumnValues;
		
		final List<ItemField<C>> declaredReferences;
		final List<ItemField> references;
		
		Mount(final Model model, final Types.MountParameters parameters)
		{
			this.model = model;
			
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
					this.typesOfInstancesColumnValues = null;
					break;
				default:
					final HashMap<String, Type> typesOfInstancesMap = new HashMap<String, Type>();
					this.typesOfInstancesColumnValues = new String[typesOfInstances.size()];
					int i = 0;
					for(final Type t : typesOfInstances)
					{
						if(typesOfInstancesMap.put(t.id, t)!=null)
							throw new RuntimeException(t.id);
						typesOfInstancesColumnValues[i++] = t.id;
					}
					this.typesOfInstancesMap = castTypeInstanceHasMap(typesOfInstancesMap);
					this.onlyPossibleTypeOfInstances = null;
					break;
			}
			
			this.declaredReferences = castDeclaredReferences(parameters.getReferences());
			final Type<?> supertype = parameters.type.supertype;
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
		private List<Type<? extends C>> castTypeInstanceList(final List<Type> l)
		{
			return (List)l;
		}
		
		@SuppressWarnings("unchecked")
		private HashMap<String, Type<? extends C>> castTypeInstanceHasMap(final HashMap m)
		{
			return m;
		}
		
		@SuppressWarnings("unchecked")
		private List<ItemField<C>> castDeclaredReferences(final List<ItemField> l)
		{
			return (List)l;
		}
		
		@SuppressWarnings("unchecked")
		private List<ItemField> castReferences(final List l)
		{
			return l;
		}
	}
	
	/**
	 * @see Class#getAnnotation(Class)
	 */
	public <A extends Annotation> A getAnnotation(final Class<A> annotationClass)
	{
		return
			bound
			? javaClass.getAnnotation(annotationClass)
			: null;
	}
	
	void connect(final Database database)
	{
		if(database==null)
			throw new RuntimeException();

		if(this.mount==null)
			throw new RuntimeException();
		if(this.table!=null)
			throw new RuntimeException();
		
		this.table = new Table(database, schemaId, supertype, mount().typesOfInstancesColumnValues);
		if(supertype==null)
		{
			primaryKeySequence.connect(database, table.primaryKey);
			database.addSequence(primaryKeySequence);
		}

		for(final Field a : declaredFields)
			a.connect(table);
		for(final UniqueConstraint uc : declaredUniqueConstraints)
			uc.connect(table);
		this.table.setUniqueConstraints(this.declaredUniqueConstraints);
		this.table.finish();
	}
	
	void disconnect()
	{
		if(this.mount==null)
			throw new RuntimeException();
		if(this.table==null)
			throw new RuntimeException();

		table = null;
		if(supertype==null)
			primaryKeySequence.disconnect();
		
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
	 * Returns, whether this type bound to it's java class.
	 * Only such types can be found by
	 * {@link #forClass(Class)} and
	 * {@link #forClassUnchecked(Class)}.
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
	public List<Type<? extends C>> getTypesOfInstances()
	{
		return mount().typesOfInstances;
	}
	
	Type<? extends C> getTypeOfInstance(final String id)
	{
		return mount().typesOfInstancesMap.get(id);
	}
	
	Type<? extends C> getOnlyPossibleTypeOfInstances()
	{
		return mount().onlyPossibleTypeOfInstances;
	}
	
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
	
	public int checkPrimaryKey()
	{
		return primaryKeySequence.check(getModel().getCurrentTransaction().getConnection());
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
	 * @see #getSubtypesTransitively()
	 */
	public List<Type<? extends C>> getSubtypes()
	{
		return mount().subtypes;
	}
	
	/**
	 * @see #getSubtypes()
	 */
	public List<Type<? extends C>> getSubtypesTransitively()
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
		return mount().declaredReferences;
	}

	/**
	 * Returns all {@link ItemField}s of the model this type belongs to,
	 * which {@link ItemField#getValueType value type} equals this type
	 * or any of it's super types.
	 * @see #getDeclaredReferences()
	 */
	public List<ItemField> getReferences()
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
		return new ItemField<C>(new Future<C>(javaClass, this), policy);
	}
	
	private static final class Future<C extends Item> extends TypeFuture<C>
	{
		private final Type<C> type;
		
		Future(final Class<C> javaClass, final Type<C> type)
		{
			super(javaClass);
			this.type = type;
		}
		
		@Override
		Type<C> get()
		{
			return type;
		}
		
		@Override
		public String toString()
		{
			return type.id;
		}
	}

	private static final SetValue[] EMPTY_SET_VALUES = {};
	
	public C newItem(SetValue... setValues)
		throws ConstraintViolationException
	{
		if(isAbstract)
			throw new IllegalArgumentException("cannot create item of abstract type " + id);
		
		if(setValues==null)
			setValues = EMPTY_SET_VALUES;
		
		final Map<Field, Object> fieldValues = prepareCreate(setValues);
		final int pk = nextPrimaryKey();
		final C result = activate(pk);
		result.doCreate(fieldValues);
		return result;
	}
	
	Map<Field, Object> prepareCreate(SetValue[] setValues)
	{
		setValues = doBeforeNewItem(setValues);
		final Map<Field, Object> fieldValues = Item.executeSetValues(setValues, null);
		Date now = null;
		for(final Field field : fields)
		{
			if(field instanceof FunctionField && !fieldValues.containsKey(field))
			{
				final FunctionField ff = (FunctionField)field;
				Object defaultValue = ff.defaultConstant;
				if(defaultValue==null)
				{
					if(ff instanceof DateField && ((DateField)ff).defaultNow)
					{
						if(now==null)
							now = new Date();
						defaultValue = now;
					}
					else if(ff instanceof IntegerField)
					{
						final Sequence sequence = ((IntegerField)ff).defaultToNextSequence;
						if(sequence!=null)
							defaultValue = sequence.next(getModel().getCurrentTransaction().getConnection());
					}
				}
				if(defaultValue!=null)
					fieldValues.put(field, defaultValue);
			}
		}
		for(final Field field : fieldValues.keySet())
		{
			assertBelongs(field);
		}
		for(final Field field : fields)
		{
			field.check(fieldValues.get(field), null);
		}
		
		checkUniqueConstraints(null, fieldValues);
		
		for(final CopyConstraint cc : copyConstraints)
			cc.check(fieldValues);

		return fieldValues;
	}
	
	void checkUniqueConstraints(final Item item, final Map<? extends Field, ?> fieldValues)
	{
		for(final UniqueConstraint uc : uniqueConstraints)
			uc.check(item, fieldValues);
	}
	
	int nextPrimaryKey()
	{
		// TODO use a separate transaction to avoid dead locks
		return primaryKeySequence.next(getModel().getCurrentTransaction().getConnection());
	}
	
	/**
	 * for test only
	 */
	void flushPrimaryKey()
	{
		primaryKeySequence.flush();
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
	
	C getItemObject(final int pk)
	{
		final Entity entity = getModel().getCurrentTransaction().getEntityIfActive(this, pk);
		if(entity!=null)
			return cast(entity.getItem());
		else
			return activate(pk);
	}
	
	C activate(final int pk)
	{
		final ActivationParameters ap = new ActivationParameters(this, pk);
		try
		{
			return activationConstructor.newInstance(ap);
		}
		catch(InstantiationException e)
		{
			throw new RuntimeException(id + '/' + javaClass.getName(), e);
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(id + '/' + javaClass.getName(), e);
		}
		catch(InvocationTargetException e)
		{
			throw new RuntimeException(id + '/' + javaClass.getName(), e);
		}
	}
	
	private static <C> Constructor<C> getActivationConstructor(final Class<C> javaClass)
	{
		final Constructor<C> result;
		try
		{
			result = javaClass.getDeclaredConstructor(ActivationParameters.class);
		}
		catch(NoSuchMethodException e)
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
		
		final C item = activate(PK.MAX_VALUE);
		if(item.type!=this)
			throw new IllegalArgumentException(id + '/' + javaClass.getName());
		if(item.pk!=PK.MAX_VALUE)
			throw new IllegalArgumentException(id + '/' + javaClass.getName());
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
	 * @deprecated renamed to {@link #searchSingleton(Condition)}.
	 */
	@Deprecated
	public C searchUnique(final Condition condition)
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
	@Deprecated
	public List<Type<? extends C>> getSubTypes()
	{
		return getSubtypes();
	}
	
	/**
	 * @deprecated Use {@link #getSubtypesTransitively()} instead
	 */
	@Deprecated
	public List<Type<? extends C>> getSubTypesTransitively()
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
}
