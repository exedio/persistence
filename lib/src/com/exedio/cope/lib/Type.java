
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
import java.util.List;

import bak.pcj.map.IntKeyOpenHashMap;

import com.exedio.cope.lib.search.Condition;
import com.exedio.cope.lib.util.ReactivationConstructorDummy;

public final class Type
{
	private static final ArrayList typesModifyable = new ArrayList();
	private static final List types = Collections.unmodifiableList(typesModifyable);
	private static final HashMap typesByName = new HashMap();
	private static final HashMap typesByID = new HashMap();
	
	private final Class javaClass;
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
	
	final String id;
	final String protectedID;
	private final List columns;
	final Column primaryKey;
	private final List allColumns;

	private final Constructor reactivationConstructor;
	private static final Class[] reactivationConstructorParams =
		new Class[]{ReactivationConstructorDummy.class, int.class};

	public static final List getTypes()
	{
		return types;
	}
	
	public static final Type findByJavaClass(final Class javaClass)
	{
		return (Type)typesByName.get(javaClass);
	}
	
	public static final Type findByID(final String id)
	{
		return (Type)typesByID.get(id);
	}
	
	public Type(final Class javaClass)
	{
		this.javaClass = javaClass;

		typesModifyable.add(this);
		typesByName.put(javaClass, this);
		this.id = Database.theInstance.trimName(this);
		typesByID.put(this.id, this);
		this.protectedID = Database.theInstance.protectName(this.id);

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
				System.arraycopy(supertypeFeatures, 0, attributes, 0, supertypeFeatures.length);
				System.arraycopy(this.declaredFeatures, 0, features, supertypeFeatures.length, this.declaredFeatures.length);
			}
		}
		this.attributeList = Collections.unmodifiableList(Arrays.asList(attributes));
		this.featureList = Collections.unmodifiableList(Arrays.asList(features));

		{
			final ArrayList columns = new ArrayList();
			for(int i = 0; i<this.declaredAttributes.length; i++)
				columns.addAll(this.declaredAttributes[i].getColumns());
			this.columns = Collections.unmodifiableList(columns);
		}
		this.primaryKey = new IntegerColumn(this);
		{
			final ArrayList allColumns = new ArrayList(columns.size()+1);
			allColumns.add(primaryKey);
			allColumns.addAll(columns);
			this.allColumns = Collections.unmodifiableList(allColumns);
		}
		

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
	
	public final Class getJavaClass()
	{
		return javaClass;
	}
	
	public final String getID()
	{
		return id;
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
	 * Returns &quot;payload&quot; columns of this type only,
	 * excluding primary key column.
	 * @see #getAllColumns()
	 */
	List getColumns()
	{
		return columns;
	}
	
	/**
	 * Returns all columns of this type,
	 * including primary key column.
	 * @see #getColumns()
	 */
	List getAllColumns()
	{
		return allColumns;
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
	
	public final Collection search(final Condition condition)
	{
		return Search.search(this, condition);
	}
	
	void onDropTable()
	{
		rows.clear();
		flushPK();
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

	// pk generation ---------------------------------------------

	static final int NOT_A_PK = Integer.MIN_VALUE;	
	private int nextPkLo = NOT_A_PK;
	private int nextPkHi = NOT_A_PK;
	private boolean nextIsLo;
	
	void flushPK()
	{
		nextPkLo = NOT_A_PK;
		nextPkHi = NOT_A_PK;
	}

	int nextPK()
	{
		if(nextPkLo==NOT_A_PK)
		{
			final int[] nextPks = Database.theInstance.getNextPK(this);
			if(nextPks.length!=2)
				throw new RuntimeException(String.valueOf(nextPks.length));
			nextPkLo = nextPks[0];
			nextPkHi = nextPks[1];
			if(nextPkLo>=nextPkHi)
				throw new RuntimeException(String.valueOf(nextPkLo)+">="+String.valueOf(nextPkHi));
			nextIsLo = (-nextPkLo)<=nextPkHi;
			//System.out.println(this.trimmedName+": getNextPK:"+nextPkLo+"/"+nextPkHi+"  nextIs"+(nextIsLo?"Lo":"Hi"));
		}
		
		//System.out.println(this.trimmedName+": nextPK:"+nextPkLo+"/"+nextPkHi+"  nextIs"+(nextIsLo?"Lo":"Hi"));
		final int result = nextIsLo ? nextPkLo-- : nextPkHi++;
		nextIsLo = !nextIsLo;

		if(nextPkLo>=nextPkHi) // TODO some handle pk overflow
			throw new RuntimeException(String.valueOf(nextPkHi)+String.valueOf(nextPkLo));
		return result;
	}

}
