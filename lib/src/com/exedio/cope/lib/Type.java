
package com.exedio.cope.lib;

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
import java.util.Iterator;
import java.util.List;

import bak.pcj.map.IntKeyOpenHashMap;

import com.exedio.cope.lib.search.AndCondition;
import com.exedio.cope.lib.search.Condition;
import com.exedio.cope.lib.search.EqualCondition;
import com.exedio.cope.lib.util.ReactivationConstructorDummy;

public final class Type
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

	private final UniqueConstraint[] uniqueConstraints;
	private final List uniqueConstraintList;
	
	private Model model;

	private Table table;
	private PrimaryKeyIterator primaryKeyIterator;

	private final Constructor reactivationConstructor;
	private static final Class[] reactivationConstructorParams =
		new Class[]{ReactivationConstructorDummy.class, int.class};

	public static final Type findByJavaClass(final Class javaClass)
	{
		return (Type)typesByClass.get(javaClass);
	}
	
	public Type(final Class javaClass)
	{
		this.javaClass = javaClass;
		if(!Item.class.isAssignableFrom(javaClass))
			throw new IllegalArgumentException(javaClass.toString()+" is not a subclass of Item");

		typesByClass.put(javaClass, this);

		{
			final String className = javaClass.getName();
			final int pos = className.lastIndexOf('.');
			this.id = className.substring(pos+1);
		}

		// supertype
		final Class superClass = javaClass.getSuperclass();
		if(superClass.equals(Item.class))
			supertype = null;
		else
		{
			supertype = findByJavaClass(superClass);
			if(supertype==null)
				throw new NullPointerException(superClass.getName());
		}

		// declaredAttributes
		final Field[] fields = javaClass.getDeclaredFields();
		final ArrayList attributesTemp = new ArrayList(fields.length);
		final ArrayList featuresTemp = new ArrayList(fields.length);
		final ArrayList uniqueConstraintsTemp = new ArrayList(fields.length);
		final int expectedModifier = Modifier.STATIC | Modifier.FINAL;
		try
		{
			for(int i = 0; i<fields.length; i++)
			{
				final Field field = fields[i];
				if((field.getModifiers()&expectedModifier)==expectedModifier)
				{
					if(Attribute.class.isAssignableFrom(field.getType()))
					{
						field.setAccessible(true);
						final Attribute attribute = (Attribute)field.get(null);
						if(attribute==null)
							throw new InitializerRuntimeException(field.getName());
						attribute.initialize(this, field.getName());
						attributesTemp.add(attribute);
						featuresTemp.add(attribute);
						featuresByName.put(attribute.getName(), attribute);
						final UniqueConstraint uniqueConstraint = attribute.getSingleUniqueConstaint();
						if(uniqueConstraint!=null)
						{
							uniqueConstraint.initialize(this, field.getName());
							uniqueConstraintsTemp.add(uniqueConstraint);
						}
					}
					else if(ComputedFunction.class.isAssignableFrom(field.getType()))
					{
						field.setAccessible(true);
						final ComputedFunction function = (ComputedFunction)field.get(null);
						if(function==null)
							throw new InitializerRuntimeException(field.getName());
						function.initialize(this, field.getName());
						featuresTemp.add(function);
						featuresByName.put(function.getName(), function);
					}
					else if(UniqueConstraint.class.isAssignableFrom(field.getType()))
					{
						field.setAccessible(true);
						final UniqueConstraint uniqueConstraint = (UniqueConstraint)field.get(null);
						if(uniqueConstraint==null)
							throw new InitializerRuntimeException(field.getName());
						uniqueConstraint.initialize(this, field.getName());
						uniqueConstraintsTemp.add(uniqueConstraint);
					}
					else if(MediaAttributeVariant.class.isAssignableFrom(field.getType()))
					{
						field.setAccessible(true);
						final MediaAttributeVariant variant = (MediaAttributeVariant)field.get(null);
						if(variant==null)
							throw new InitializerRuntimeException(field.getName());
						variant.initialize(this, field.getName());
					}
				}
			}
		}
		catch(IllegalAccessException e)
		{
			throw new InitializerRuntimeException(e);
		}
		this.declaredAttributes = (Attribute[])attributesTemp.toArray(new Attribute[attributesTemp.size()]);
		this.declaredAttributeList = Collections.unmodifiableList(Arrays.asList(this.declaredAttributes));
		this.declaredFeatures = (Feature[])featuresTemp.toArray(new Feature[featuresTemp.size()]);
		this.declaredFeatureList = Collections.unmodifiableList(Arrays.asList(this.declaredFeatures));
		this.uniqueConstraints = (UniqueConstraint[])uniqueConstraintsTemp.toArray(new UniqueConstraint[uniqueConstraintsTemp.size()]);
		this.uniqueConstraintList = Collections.unmodifiableList(Arrays.asList(this.uniqueConstraints));
		
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


		try
		{
			reactivationConstructor = javaClass.getDeclaredConstructor(reactivationConstructorParams);
			reactivationConstructor.setAccessible(true);
		}
		catch(NoSuchMethodException e)
		{
			throw new InitializerRuntimeException(e);
		}
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

		for(int i = 0; i<declaredAttributes.length; i++)
			declaredAttributes[i].materialize(table);
		for(int i = 0; i<uniqueConstraints.length; i++)
			uniqueConstraints[i].materialize(database);
		this.table.setUniqueConstraints(this.uniqueConstraintList);
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
			throw new RuntimeException();

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
		return Search.search(new Query(this, condition));
	}
	
	public final Item searchUnique(final ObjectAttribute attribute, final Object value)
	{
		// TODO: search nativly for unique constraints
		return searchUnique(new EqualCondition(attribute, value));
	}
		
	public final Item searchUnique(final UniqueConstraint constraint, final Object[] values)
	{
		// TODO: search nativly for unique constraints
		final List attributes = constraint.getUniqueAttributes();
		if(attributes.size()!=values.length)
			throw new RuntimeException();

		final Iterator attributeIterator = attributes.iterator();
		final Condition[] conditions = new Condition[attributes.size()];
		for(int j = 0; attributeIterator.hasNext(); j++)
			conditions[j] = new EqualCondition((ObjectAttribute)attributeIterator.next(), values[j]);

		return searchUnique(new AndCondition(conditions));
	}
		
	private final Item searchUnique(final Condition condition)
	{
		final Iterator searchResult = search(condition).iterator();
		if(searchResult.hasNext())
		{
			final Item result = (Item)searchResult.next();
			if(searchResult.hasNext())
				throw new SystemException(null);
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
			throw new RuntimeException();
		
		return primaryKeyIterator;
	}
	
	void onDropTable()
	{
		rows.clear();
		primaryKeyIterator.flushPK();
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
			throw new SystemException(e);
		}
		catch(IllegalAccessException e)
		{
			throw new SystemException(e);
		}
		catch(InvocationTargetException e)
		{
			throw new SystemException(e);
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
		public int compare(Object o1, Object o2)
		{
			final Type t1 = (Type)o1;
			final Type t2 = (Type)o2;
			return t1.id.compareTo(t2.id);
		}	
	};

	static final int NOT_A_PK = Integer.MIN_VALUE;	

}
