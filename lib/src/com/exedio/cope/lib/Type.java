
package com.exedio.cope.lib;

import com.exedio.cope.lib.Database;
import com.exedio.cope.lib.util.ReactivationConstructorDummy;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public final class Type
{
	private static final ArrayList typesModifyable = new ArrayList();
	private static final List types = Collections.unmodifiableList(typesModifyable);
	private static final HashMap typesByName = new HashMap();
	
	private final Class javaClass;
	
	private final Attribute[] attributes;
	private final List attributeList;

	private final UniqueConstraint[] uniqueConstraints;
	private final List uniqueConstraintList;
	
	final String trimmedName;
	final String protectedName;
	private final List columns;
	final Column primaryKey;

	private final Constructor reactivationConstructor;
	private static final Class[] reactivationConstructorParams =
		new Class[]{ReactivationConstructorDummy.class, int.class};

	public static final List getTypes()
	{
		return types;
	}
	
	public static final Type getType(String className)
	{
		return (Type)typesByName.get(className);
	}
	
	public Type(final Class javaClass, final Attribute[] attributes, final UniqueConstraint[] uniqueConstraints)
	{
		this.javaClass = javaClass;

		if(attributes!=null)
		{
			this.attributes = attributes;
			this.attributeList = Collections.unmodifiableList(Arrays.asList(attributes));
			for(int i = 0; i<attributes.length; i++)
				attributes[i].setType(this);
		}
		else
		{
			this.attributes = new Attribute[]{};
			this.attributeList = Collections.EMPTY_LIST;
		}

		if(uniqueConstraints!=null)
		{
			this.uniqueConstraints = uniqueConstraints;
			this.uniqueConstraintList = Collections.unmodifiableList(Arrays.asList(uniqueConstraints));
		}
		else
		{
			this.uniqueConstraints = new UniqueConstraint[]{};
			this.uniqueConstraintList = Collections.EMPTY_LIST;
		}
		
		typesModifyable.add(this);
		typesByName.put(javaClass.getName(), this);
		this.trimmedName = Database.theInstance.trimName(this);
		this.protectedName = Database.theInstance.protectName(this.trimmedName);

		final ArrayList columns = new ArrayList();
		for(int i = 0; i<this.attributes.length; i++)
			columns.addAll(this.attributes[i].getColumns());
		this.columns = Collections.unmodifiableList(columns);
		this.primaryKey = new IntegerColumn(this, "PK", true, ItemAttribute.SYNTETIC_PRIMARY_KEY_PRECISION, null, null);

		try
		{
			reactivationConstructor = javaClass.getDeclaredConstructor(reactivationConstructorParams);
			reactivationConstructor.setAccessible(true);
		}
		catch(NoSuchMethodException e)
		{
			throw new SystemException(e);
		}
	}
	
	public final Class getJavaClass()
	{
		return javaClass;
	}
	
	public final List getAttributes()
	{
		return attributeList;
	}
	
	public final List getUniqueConstraints()
	{
		return uniqueConstraintList;
	}
	
	List getColumns()
	{
		return columns;
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
	
	void onDropTable()
	{
		rows.clear();
		flushPK();
	}

	// active items of this type ---------------------------------------------
	
	/**
	 * TODO: use something more efficient for integer keys.
	 */
	private HashMap rows = new HashMap();
	
	/**
	 * Returns an item of this type and the given pk, if it's already active.
	 * Returns null, if either there is no such item with the given pk, or
	 * such an item is not active.
	 */
	Row getRow(final int pk)
	{
		return (Row)rows.get(new Integer(pk));
	}
	
	void putRow(final Row row)
	{
		if(rows.put(new Integer(row.pk), row)!=null)
			throw new RuntimeException();
	}
	
	void removeRow(final Row row)
	{
		if(rows.remove(new Integer(row.pk))!=row)
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
