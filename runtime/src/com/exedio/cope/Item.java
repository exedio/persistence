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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.exedio.cope.Attribute.Option;
import com.exedio.cope.ItemAttribute.DeletePolicy;
import com.exedio.cope.util.ReactivationConstructorDummy;

/**
 * This is the super class for all classes,
 * that want to store their data persistently with COPE.
 * <p>
 * To enable serialization for subclasses of <tt>Item</tt>,
 * see {@link #Item()}.
 *
 * @author Ralf Wiebicke
 */
public abstract class Item extends Cope
{
	final transient Type<? extends Item> type = Type.findByJavaClass(getClass());

	/**
	 * The primary key of the item,
	 * that is unique within the {@link #type} of this item.
	 */
	final int pk;
	
	/**
	 * Returns a string unique for this item in all other items of the model.
	 * For any item <tt>a</tt> in its model <tt>m</tt>
	 * the following holds true:
	 * <tt>a.equals(m.findByID(a.getCopeID()).</tt>
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
	public final Type<? extends Item> getCopeType()
	{
		return type;
	}

	/**
	 * Returns true, if <tt>o</tt> represents the same item as this item.
	 * Is equivalent to
	 * <pre>(o != null) && (o instanceof Item) && getCopeID().equals(((Item)o).getCopeID())</pre>
	 * Does not activate this item, if it's not already active.
	 */
	@Override
	public final boolean equals(final Object o)
	{
		return (o!=null) && (getClass()==o.getClass()) && (pk==((Item)o).pk);
	}

	/**
	 * Returns a hash code, that is consistent with {@link #equals(Object)}.
	 * Note, that this is not neccessarily equivalent to <tt>getCopeID().hashCode()</tt>.
	 * Does not activate this item, if it's not already active.
	 */
	@Override
	public final int hashCode()
	{
		return getClass().hashCode() ^ pk;
	}
	
	@Override
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
	 * For any two item objects <tt>a</tt>, <tt>b</tt> the following holds true:
	 * <p>
	 * If and only if <tt>a.equals(b)</tt> then <tt>a.activeCopeItem() == b.activeCopeItem()</tt>.
	 * <p>
	 * So it does for items, what {@link String#intern} does for strings.
	 * Does activate this item, if it's not already active.
	 * Is guaranteed to be very cheap, if this item object is already active, which means
	 * this method returns <tt>this</tt>.
	 * Never returns null.
	 */
	public final Item activeCopeItem()
	{
		return getEntity().getItem();
	}

	/**
	 * @throws MandatoryViolationException
	 *         if <tt>value</tt> is null and <tt>attribute</tt>
	 *         is {@link Attribute#isMandatory() mandatory}.
	 * @throws ClassCastException
	 *         if <tt>value</tt> is not compatible to <tt>attribute</tt>.
	 */
	protected Item(final SetValue[] setValues)
		throws
			UniqueViolationException,
			MandatoryViolationException,
			LengthViolationException,
			ClassCastException
	{
		this.pk = type.getPkSource().nextPK(type.getModel().getCurrentTransaction().getConnection());
		if(pk==Type.NOT_A_PK)
			throw new RuntimeException();
		//System.out.println("create item "+type+" "+pk);
		
		final Map<Attribute, Object> attributeValues = executeSetValues(setValues, null);
		Date now = null;
		for(final Attribute attribute : type.getAttributes())
		{
			if(attribute instanceof FunctionAttribute && !attributeValues.containsKey(attribute))
			{
				final FunctionAttribute fa = (FunctionAttribute)attribute;
				Object defaultValue = fa.defaultConstant;
				if(defaultValue==null && fa instanceof DateAttribute && ((DateAttribute)fa).defaultNow)
				{
					if(now==null)
						now = new Date();
					defaultValue = now;
				}
				if(defaultValue!=null)
					attributeValues.put(attribute, defaultValue);
			}
		}
		for(final Attribute attribute : attributeValues.keySet())
		{
			if(!attribute.getType().isAssignableFrom(type))
				throw new RuntimeException("attribute " + attribute + " does not belong to type " + type.toString());
		}
		for(final Attribute attribute : type.getAttributes())
		{
			attribute.checkValue(attributeValues.get(attribute), null);
		}

		final Entity entity = getEntity(false);
		entity.put(attributeValues);
		entity.write(toBlobs(attributeValues));
		
		postCreate();
	}
	
	
	/**
	 * Is called after every item creation. Override this method when needed.
	 * The default implementation does nothing.
	 */
	protected void postCreate()
	{
		// empty default implementation
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
		this.pk = pk;
		//System.out.println("reactivate item:"+type+" "+pk);

		if(reactivationDummy!=Type.REACTIVATION_DUMMY)
			throw new RuntimeException("reactivation constructor is for internal purposes only, don't use it in your application!");
		if(pk==Type.NOT_A_PK)
			throw new RuntimeException();
	}
	
	/**
	 * Empty constructor for deserialization.
	 * <p>
	 * To enable serialization for subclasses of <tt>Item</tt>,
	 * let these classes implement {@link java.io.Serializable}
	 * and make sure, there is a no-arg constructor
	 * calling this deserialization constructor.
	 * <p>
	 * Serialization of instances of <tt>Item</tt>
	 * is guaranteed to be light-weigth -
	 * there are no non-static, non-transient object reference
	 * fields in this class or its supertypes.
	 */
	protected Item()
	{
		pk = suppressWarning(this.pk);
	}
	
	private static final int suppressWarning(final int pk)
	{
		return pk;
	}

	public final <E> E get(final Function<E> function)
	{
		return function.get(this);
	}

	/**
	 * @throws MandatoryViolationException
	 *         if <tt>value</tt> is null and <tt>attribute</tt>
	 *         is {@link Attribute#isMandatory() mandatory}.
	 * @throws FinalViolationException
	 *         if <tt>attribute</tt> is {@link Attribute#isFinal() final}.
	 * @throws ClassCastException
	 *         if <tt>value</tt> is not compatible to <tt>attribute</tt>.
	 */
	public final <E> void set(final FunctionAttribute<E> attribute, final E value)
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
		entity.write(Collections.<BlobColumn, byte[]>emptyMap());
	}

	/**
	 * @throws MandatoryViolationException
	 *         if <tt>value</tt> is null and <tt>attribute</tt>
	 *         is {@link Attribute#isMandatory() mandatory}.
	 * @throws FinalViolationException
	 *         if <tt>attribute</tt> is {@link Attribute#isFinal() final}.
	 * @throws ClassCastException
	 *         if <tt>value</tt> is not compatible to <tt>attribute</tt>.
	 */
	public final void set(final SetValue[] setValues)
		throws
			UniqueViolationException,
			MandatoryViolationException,
			LengthViolationException,
			FinalViolationException,
			ClassCastException
	{
		final Map<Attribute, Object> attributeValues = executeSetValues(setValues, this);
		for(final Attribute attribute : attributeValues.keySet())
		{
			if(!attribute.getType().isAssignableFrom(type))
				throw new RuntimeException("attribute "+attribute+" does not belong to type "+type.toString());
			
			if(attribute.isfinal)
				throw new FinalViolationException(attribute, this);

			attribute.checkValue(attributeValues.get(attribute), this);
		}

		final Entity entity = getEntity();
		entity.put(attributeValues);
		entity.write(toBlobs(attributeValues));
	}

	public final void deleteCopeItem()
			throws IntegrityViolationException
	{
		checkDeleteCopeItem(new HashSet<Item>());
		deleteCopeItem(new HashSet<Item>());
	}

	private final void checkDeleteCopeItem(final HashSet<Item> toDelete)
			throws IntegrityViolationException
	{
		toDelete.add(this);
		
		for(final ItemAttribute<Item> attribute : castReferences(type.getReferences()))
		{
			switch(attribute.getDeletePolicy())
			{
				case FORBID:
				{
					final Collection s = attribute.getType().search(attribute.equal(this));
					if(!s.isEmpty())
						throw new IntegrityViolationException(attribute, this);
					break;
				}
				case CASCADE:
				{
					for(final Item item : attribute.getType().search(attribute.equal(this)))
					{
						//System.out.println("------------check:"+item.toString());
						if(!toDelete.contains(item))
							item.checkDeleteCopeItem(toDelete);
					}
					break;
				}
			}
		}
	}
		
	private final void deleteCopeItem(final HashSet<Item> toDelete)
	{
		toDelete.add(this);
		
		//final String tostring = toString();
		//System.out.println("------------delete:"+tostring);
		try
		{
			// TODO make sure, no item is deleted twice
			for(final ItemAttribute<Item> attribute : castReferences(type.getReferences()))
			{
				switch(attribute.getDeletePolicy())
				{
					case NULLIFY:
					{
						final Query<? extends Item> q = attribute.getType().newQuery(attribute.equal(this));
						for(final Item item : q.search())
						{
							//System.out.println("------------nullify:"+item.toString());
							item.set(attribute, null);
						}
						break;
					}
					case CASCADE:
					{
						final Query q = attribute.getType().newQuery(attribute.equal(this));
						for(Iterator j = q.search().iterator(); j.hasNext(); )
						{
							final Item item = (Item)j.next();
							//System.out.println("------------check:"+item.toString());
							if(!toDelete.contains(item))
								item.deleteCopeItem(toDelete);
						}
						break;
					}
				}
			}
			Entity entity = getEntity();
			entity.delete();
			entity.write(null);
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
	
	@SuppressWarnings("unchecked")
	private final List<ItemAttribute<Item>> castReferences(final List l)
	{
		return (List<ItemAttribute<Item>>)l;
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

	// convenience for subclasses --------------------------------------------------
	
	public static final Attribute.Option MANDATORY = Attribute.Option.MANDATORY;
	public static final Attribute.Option OPTIONAL = Attribute.Option.OPTIONAL;
	public static final Attribute.Option UNIQUE = Attribute.Option.UNIQUE;
	public static final Attribute.Option UNIQUE_OPTIONAL = Attribute.Option.UNIQUE_OPTIONAL;
	public static final Attribute.Option FINAL = Attribute.Option.FINAL;
	public static final Attribute.Option FINAL_OPTIONAL = Attribute.Option.FINAL_OPTIONAL;
	public static final Attribute.Option FINAL_UNIQUE = Attribute.Option.FINAL_UNIQUE;
	public static final Attribute.Option FINAL_UNIQUE_OPTIONAL = Attribute.Option.FINAL_UNIQUE_OPTIONAL;

	/**
	 * @deprecated Has been renamed to {@link #FINAL}.
	 */
	@Deprecated
	public static final Attribute.Option READ_ONLY = FINAL;
	
	/**
	 * @deprecated Has been renamed to {@link #FINAL_OPTIONAL}.
	 */
	@Deprecated
	public static final Attribute.Option READ_ONLY_OPTIONAL = FINAL_OPTIONAL;
	
	/**
	 * @deprecated Has been renamed to {@link #FINAL_UNIQUE}.
	 */
	@Deprecated
	public static final Attribute.Option READ_ONLY_UNIQUE = FINAL_UNIQUE;

	/**
	 * @deprecated Has been renamed to {@link #FINAL_UNIQUE_OPTIONAL}.
	 */
	@Deprecated
	public static final Attribute.Option READ_ONLY_UNIQUE_OPTIONAL = FINAL_UNIQUE_OPTIONAL;
	
	public static final ItemAttribute.DeletePolicy FORBID = ItemAttribute.DeletePolicy.FORBID;
	public static final ItemAttribute.DeletePolicy NULLIFY = ItemAttribute.DeletePolicy.NULLIFY;
	public static final ItemAttribute.DeletePolicy CASCADE = ItemAttribute.DeletePolicy.CASCADE;
	
	protected static final <C extends Item> Type<C> newType(final Class<C> javaClass)
	{
		return new Type<C>(javaClass);
	}
	
	protected static final <C extends Item> Type<C> newType(final Class<C> javaClass, final String id)
	{
		return new Type<C>(javaClass, id);
	}
	
	public static final <E extends Enum<E>> EnumAttribute<E> newEnumAttribute(final Option option, final Class<E> valueClass)
	{
		return new EnumAttribute<E>(option, valueClass);
	}
	
	public static final <E extends Item> ItemAttribute<E> newItemAttribute(final Option option, final Class<E> valueClass)
	{
		return new ItemAttribute<E>(option, valueClass);
	}
	
	public static final <E extends Item> ItemAttribute<E> newItemAttribute(final Option option, final Class<E> valueClass, final DeletePolicy policy)
	{
		return new ItemAttribute<E>(option, valueClass, policy);
	}
	
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
	
	private static final Map<Attribute, Object> executeSetValues(final SetValue<?>[] sources, final Item exceptionItem)
	{
		final HashMap<Attribute, Object> result = new HashMap<Attribute, Object>();
		for(final SetValue<?> av : sources)
		{
			if(av.settable instanceof Attribute)
			{
				putAttribute(result, av);
			}
			else
			{
				for(final SetValue part : execute(av, exceptionItem))
					putAttribute(result, part);
			}
		}
		return result;
	}
	
	private static final void putAttribute(final HashMap<Attribute, Object> result, final SetValue<?> setValue)
	{
		if(result.put((Attribute)setValue.settable, setValue.value)!=null)
			throw new RuntimeException("duplicate function attribute " + setValue.settable.toString());
	}
	
	private static final <X extends Object> SetValue[] execute(final SetValue<X> sv, final Item exceptionItem)
	{
		return sv.settable.execute(sv.value, exceptionItem);
	}
	
	private final HashMap<BlobColumn, byte[]> toBlobs(final Map<Attribute, Object> attributeValues)
	{
		final HashMap<BlobColumn, byte[]> result = new HashMap<BlobColumn, byte[]>();
		
		for(final Attribute attribute : attributeValues.keySet())
		{
			if(!(attribute instanceof DataAttribute))
				continue;
			
			final DataAttribute da = (DataAttribute)attribute;
			da.impl.fillBlob((byte[])attributeValues.get(attribute), result, this);
		}
		return result;
	}

}
