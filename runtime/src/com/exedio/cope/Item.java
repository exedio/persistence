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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.exedio.cope.pattern.CustomAttribute;
import com.exedio.cope.pattern.CustomAttributeException;
import com.exedio.cope.util.ReactivationConstructorDummy;

/**
 * This is the super class for all classes,
 * that want to store their data persistently with COPE.
 * 
 * @author Ralf Wiebicke
 */
public abstract class Item extends Cope
{
	final Type type;

	/**
	 * The primary key of the item,
	 * that is unique within the {@link #type} of this item.
	 */
	final int pk;
	
	/**
	 * Returns a string unique for this item in all other items of the model.
	 * For any item <code>a</code> in its model <code>m</code>
	 * the following holds true:
	 * <code>a.equals(m.findByID(a.getCopeID()).</code>
	 * Does not activate this item, if it's not already active.
	 * Never returns null.
	 * @see Model#findByID(String)
	 */
	public final String getCopeID()
	{
		return type.id + '.' + type.getPkSource().pk2id(pk);
	}
	
	/**
	 * Returns the type of this item.
	 * Never returns null.
	 */
	public final Type getCopeType()
	{
		return type;
	}

	/**
	 * Returns true, if <code>o</code> represents the same item as this item.
	 * Is equivalent to
	 * <pre>(o != null) && (o instanceof Item) && getCopeID().equals(((Item)o).getCopeID())</pre>
	 * Does not activate this item, if it's not already active.
	 */
	public final boolean equals(final Object o)
	{
		return (o!=null) && (getClass()==o.getClass()) && (pk==((Item)o).pk);
	}

	/**
	 * Returns a hash code, that is consistent with {@link #equals(Object)}.
	 * Note, that this is not neccessarily equivalent to <code>getCopeID().hashCode()</code>.
	 * Does not activate this item, if it's not already active.
	 */
	public final int hashCode()
	{
		return getClass().hashCode() ^ pk;
	}
	
	public String toString()
	{
		return getCopeID();
	}

	/**
	 * Returns, whether this item is active.
	 */	
	public final boolean isActiveCopeItem()
	{
		final Entity entity = getEntityIfActive();
		return (entity!=null) && (entity.getItem() == this);
	}

	/**
	 * Returns the active item object representing the same item as this item object.
	 * For any two item objects <code>a</code>, <code>b</code> the following holds true:
	 * <p>
	 * If and only if <code>a.equals(b)</code> then <code>a.activeCopeItem() == b.activeCopeItem()</code>.
	 * <p>
	 * So it does for items, what {@link String#intern} does for strings.
	 * Does activate this item, if it's not already active.
	 * Is guaranteed to be very cheap, if this item object is already active, which means
	 * this method returns <code>this</code>.
	 * Never returns null.
	 */
	public final Item activeCopeItem()
	{
		return getEntity().getItem();
	}

	/**
	 * @throws MandatoryViolationException
	 *         if <code>value</code> is null and <code>attribute</code>
	 *         is {@link Attribute#isMandatory() mandatory}.
	 * @throws ClassCastException
	 *         if <code>value</code> is not compatible to <code>attribute</code>.
	 */
	protected Item(AttributeValue[] initialAttributeValues)
		throws
			UniqueViolationException,
			MandatoryViolationException,
			LengthViolationException,
			ClassCastException
	{
		this.type = Type.findByJavaClass(getClass());
		this.pk = type.getPkSource().nextPK(type.getModel().getCurrentTransaction().getConnection());
		if(pk==Type.NOT_A_PK)
			throw new RuntimeException();
		//System.out.println("create item "+type+" "+pk);
		
		initialAttributeValues = executeCustomAttributes(initialAttributeValues, null);
		for(int i = 0; i<initialAttributeValues.length; i++)
		{
			final AttributeValue av = initialAttributeValues[i];
			((FunctionAttribute)av.attribute).checkValue(av.value, null);
		}

		final Entity entity = getEntity(false);
		entity.put( initialAttributeValues );
		entity.write();
	}
	
	/**
	 * Reactivation constructor.
	 * Is used for internal purposes only.
	 * Does not actually create a new item, but a passive item object for
	 * an already existing item.
	 */
	protected Item(
		final ReactivationConstructorDummy reactivationDummy,
		final int pk)
	{
		this.type = Type.findByJavaClass(getClass());
		this.pk = pk;
		//System.out.println("reactivate item:"+type+" "+pk);

		if(reactivationDummy!=Type.REACTIVATION_DUMMY)
			throw new RuntimeException("reactivation constructor is for internal purposes only, don't use it in your application!");
		if(pk==Type.NOT_A_PK)
			throw new RuntimeException();
	}

	public final Object get(final Function function)
	{
		return function.getObject(this);
	}

	/**
	 * @throws MandatoryViolationException
	 *         if <code>value</code> is null and <code>attribute</code>
	 *         is {@link Attribute#isMandatory() mandatory}.
	 * @throws FinalViolationException
	 *         if <code>attribute</code> is {@link Attribute#isFinal() final}.
	 * @throws ClassCastException
	 *         if <code>value</code> is not compatible to <code>attribute</code>.
	 */
	public final void set(final FunctionAttribute attribute, final Object value)
		throws
			UniqueViolationException,
			MandatoryViolationException,
			LengthViolationException,
			FinalViolationException,
			ClassCastException
	{
		if(!attribute.getType().isAssignableFrom(type))
			throw new RuntimeException("attribute "+attribute+" does not belong to type "+type.toString());
		
		if(attribute.isfinal)
			throw new FinalViolationException(attribute, this);

		attribute.checkValue(value, this);

		final Entity entity = getEntity();
		entity.put(attribute, value);
		entity.write();
	}

	/**
	 * @throws MandatoryViolationException
	 *         if <code>value</code> is null and <code>attribute</code>
	 *         is {@link Attribute#isMandatory() mandatory}.
	 * @throws FinalViolationException
	 *         if <code>attribute</code> is {@link Attribute#isFinal() final}.
	 * @throws ClassCastException
	 *         if <code>value</code> is not compatible to <code>attribute</code>.
	 */
	public final void set(AttributeValue[] attributeValues)
		throws
			UniqueViolationException,
			MandatoryViolationException,
			LengthViolationException,
			FinalViolationException,
			CustomAttributeException,
			ClassCastException
	{
		attributeValues = executeCustomAttributes(attributeValues, this);
		for(int i = 0; i<attributeValues.length; i++)
		{
			final AttributeValue attributeValue = attributeValues[i];
			final FunctionAttribute attribute = (FunctionAttribute)attributeValue.attribute;

			if(!attribute.getType().isAssignableFrom(type))
				throw new RuntimeException("attribute "+attribute+" does not belong to type "+type.toString());
			
			if(attribute.isfinal)
				throw new FinalViolationException(attribute, this);
	
			attribute.checkValue(attributeValue.value, this);
		}

		final Entity entity = getEntity();		
		entity.put(attributeValues);
		entity.write();
	}

	public final void deleteCopeItem()
			throws IntegrityViolationException
	{
		checkDeleteCopeItem(new HashSet());
		deleteCopeItem(new HashSet());
	}

	private final void checkDeleteCopeItem(final HashSet toDelete)
			throws IntegrityViolationException
	{
		toDelete.add(this);
		
		for(Iterator i = type.getReferences().iterator(); i.hasNext(); )
		{
			final ItemAttribute attribute = (ItemAttribute)i.next();
			if(attribute.getDeletePolicy().forbid)
			{
				final Collection s = attribute.getType().search(attribute.equal(this));
				if(!s.isEmpty())
					throw new IntegrityViolationException(attribute, this);
			}
			if(attribute.getDeletePolicy().cascade)
			{
				for(Iterator j = attribute.getType().search(attribute.equal(this)).iterator(); j.hasNext(); )
				{
					final Item item = (Item)j.next();
					//System.out.println("------------check:"+item.toString());
					if(!toDelete.contains(item))
						item.checkDeleteCopeItem(toDelete);
				}
			}
		}
	}
		
	private final void deleteCopeItem(final HashSet toDelete)
	{
		toDelete.add(this);
		
		//final String tostring = toString();
		//System.out.println("------------delete:"+tostring);
		try
		{
			// TODO make sure, no item is deleted twice
			for(Iterator i = type.getReferences().iterator(); i.hasNext(); )
			{
				final ItemAttribute attribute = (ItemAttribute)i.next();
				if(attribute.getDeletePolicy().nullify)
				{
					final Query q = new Query(attribute.getType(), attribute.equal(this));
					for(Iterator j = q.search().iterator(); j.hasNext(); )
					{
						final Item item = (Item)j.next();
						//System.out.println("------------nullify:"+item.toString());
						item.set(attribute, null);
					}
				}
				if(attribute.getDeletePolicy().cascade)
				{
					final Query q = new Query(attribute.getType(), attribute.equal(this));
					for(Iterator j = q.search().iterator(); j.hasNext(); )
					{
						final Item item = (Item)j.next();
						//System.out.println("------------check:"+item.toString());
						if(!toDelete.contains(item))
							item.deleteCopeItem(toDelete);
					}
				}
			}
			Entity entity = getEntity();
			entity.delete();
			entity.write();
		}
		catch(UniqueViolationException e)
		{
			// cannot happen, since null does not violate uniqueness
			throw new RuntimeException(e);
		}
		catch(MandatoryViolationException e)
		{
			// cannot happen, since nullify ItemAttributes cannot be mandatory
			throw new RuntimeException(e);
		}
		catch(LengthViolationException e)
		{
			// cannot happen, since there are no StringAttributes written
			throw new RuntimeException(e);
		}
		catch(FinalViolationException e)
		{
			// cannot happen, since nullify ItemAttributes cannot be final
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Returns, whether the item does exist.
	 * There are two possibilities, why an item could not exist:
	 * <ol>
	 * <li>the item has been deleted by {@link #deleteCopeItem()}.
	 * <li>the item has been created in a transaction,
	 *     that was subsequently rolled back by {@link Model#rollback()}.
	 * </ol>
	 */
	public final boolean existsCopeItem()
	{
		try
		{
			return getEntity().exists();
		}
		catch ( NoSuchItemException e )
		{
			return false;
		}
	}

	public static final Attribute.Option MANDATORY = new Attribute.Option(false, false, true);
	public static final Attribute.Option OPTIONAL = new Attribute.Option(false, false, false);

	public static final Attribute.Option UNIQUE = new Attribute.Option(false, true, true);
	public static final Attribute.Option UNIQUE_OPTIONAL = new Attribute.Option(false, true, false);

	public static final Attribute.Option FINAL = new Attribute.Option(true, false, true);
	public static final Attribute.Option FINAL_OPTIONAL = new Attribute.Option(true, false, false);

	public static final Attribute.Option FINAL_UNIQUE = new Attribute.Option(true, true, true);
	public static final Attribute.Option FINAL_UNIQUE_OPTIONAL = new Attribute.Option(true, true, false);

	/**
	 * @deprecated Has been renamed to {@link #FINAL}.
	 */
	public static final Attribute.Option READ_ONLY = FINAL;
	
	/**
	 * @deprecated Has been renamed to {@link #FINAL_OPTIONAL}.
	 */
	public static final Attribute.Option READ_ONLY_OPTIONAL = FINAL_OPTIONAL;
	
	/**
	 * @deprecated Has been renamed to {@link #FINAL_UNIQUE}.
	 */
	public static final Attribute.Option READ_ONLY_UNIQUE = FINAL_UNIQUE;

	/**
	 * @deprecated Has been renamed to {@link #FINAL_UNIQUE_OPTIONAL}.
	 */
	public static final Attribute.Option READ_ONLY_UNIQUE_OPTIONAL = FINAL_UNIQUE_OPTIONAL;
	
	public static final ItemAttribute.DeletePolicy FORBID = new ItemAttribute.DeletePolicy(0);
	public static final ItemAttribute.DeletePolicy NULLIFY = new ItemAttribute.DeletePolicy(1);
	public static final ItemAttribute.DeletePolicy CASCADE = new ItemAttribute.DeletePolicy(2);
	
	// activation/deactivation -----------------------------------------------------
	
	private final Entity getEntity()
	{
		return getEntity(true);
	}

	private final Entity getEntity(final boolean present)
	{
		return type.getModel().getCurrentTransaction().getEntity(this, present);
	}

	private final Entity getEntityIfActive()
	{
		return type.getModel().getCurrentTransaction().getEntityIfActive(type, pk);
	}
	
	private static final AttributeValue[] executeCustomAttributes(final AttributeValue[] source, final Item exceptionItem)
		throws CustomAttributeException
	{
		final HashMap<Settable, AttributeValue> result = new HashMap<Settable, AttributeValue>();
		boolean customAttributeOccured = false;
		for(int i = 0; i<source.length; i++)
		{
			final AttributeValue av = source[i];
			final Settable settable = av.attribute;
			if(settable instanceof FunctionAttribute)
			{
				if(result.put(settable, av)!=null)
					throw new RuntimeException("duplicate settable "+settable.toString());
			}
			else
			{
				customAttributeOccured = true;
				final CustomAttribute ca = (CustomAttribute)av.attribute;
				final AttributeValue[] caav = ca.execute(av.value, exceptionItem);
				for(int j = 0; j<caav.length; j++)
				{
					if(result.put(caav[j].attribute, caav[j])!=null)
						throw new RuntimeException("duplicate settable "+caav[j].attribute.toString());
				}
			}
		}
		if(!customAttributeOccured)
			return source;
		
		final AttributeValue[] resultArray = new AttributeValue[result.size()];
		int n = 0;
		for(Iterator i = result.values().iterator(); i.hasNext(); n++)
		{
			resultArray[n] = (AttributeValue)i.next();
			assert resultArray[n].attribute instanceof FunctionAttribute;
		}
		return resultArray;
	}

}
