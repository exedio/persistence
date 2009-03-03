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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.exedio.cope.ItemField.DeletePolicy;
import com.exedio.cope.util.ReactivationConstructorDummy;

/**
 * This is the super class for all classes,
 * that want to store their data persistently with COPE.
 * <p>
 * Serialization of instances of <tt>Item</tt>
 * is guaranteed to be light-weight -
 * there are no non-static, non-transient object reference
 * fields in this class or its superclasses.
 *
 * @author Ralf Wiebicke
 */
public abstract class Item implements Serializable
{
	static final char ID_SEPARATOR = '.';
	
	transient Type<? extends Item> type;

	/**
	 * The primary key of the item,
	 * that is unique within the {@link #type} of this item.
	 */
	final int pk;
	
	/**
	 * Returns a string unique for this item in all other items of the model.
	 * For any item <tt>a</tt> in its model <tt>m</tt>
	 * the following holds true:
	 * <tt>a.equals(m.getItem(a.getCopeID()).</tt>
	 * Does not activate this item, if it's not already active.
	 * Never returns null.
	 * @see Model#getItem(String)
	 */
	public final String getCopeID()
	{
		return type.id + ID_SEPARATOR + pk;
	}
	
	/**
	 * Returns the type of this item.
	 * Never returns null.
	 */
	public final Type<? extends Item> getCopeType()
	{
		assert type!=null;
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
		if(this==o)
			return true;
		if(o==null || !(o instanceof Item))
			return false;

		final Item i = (Item)o;
		return type==i.type && pk==i.pk;
	}

	/**
	 * Returns a hash code, that is consistent with {@link #equals(Object)}.
	 * Note, that this is not neccessarily equivalent to <tt>getCopeID().hashCode()</tt>.
	 * Does not activate this item, if it's not already active.
	 */
	@Override
	public final int hashCode()
	{
		return type.hashCode() ^ pk;
	}
	
	/**
	 * Returns {@link #getCopeID()} as a default implementation
	 * for all persistent classes.
	 */
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
	 *         if <tt>value</tt> is null and <tt>field</tt>
	 *         is {@link Field#isMandatory() mandatory}.
	 * @throws ClassCastException
	 *         if <tt>value</tt> is not compatible to <tt>field</tt>.
	 */
	protected Item(final SetValue... setValues)
	{
		this(setValues, null);
	}

	private static final Map<Field, Object> prepareCreate(final SetValue[] setValues, final Type<? extends Item> type)
	{
		final Map<Field, Object> fieldValues = executeSetValues(setValues, null);
		Date now = null;
		for(final Field field : type.getFields())
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
							defaultValue = sequence.next(type.getModel().getCurrentTransaction().getConnection());
					}
				}
				if(defaultValue!=null)
					fieldValues.put(field, defaultValue);
			}
		}
		for(final Field field : fieldValues.keySet())
		{
			type.assertBelongs(field);
		}
		for(final Field field : type.getFields())
		{
			field.check(fieldValues.get(field), null);
		}
		
		checkUniqueConstraints(type, null, fieldValues);
		
		for(final CopyConstraint cc : type.copyConstraints)
			cc.check(fieldValues);

		return fieldValues;
	}
	
	public Item(final SetValue[] setValues, final Type<? extends Item> typeWithoutJavaClass)
	{
		this.type = typeWithoutJavaClass==null ? Type.forClass(getClass()) : typeWithoutJavaClass;
		final Map<Field, Object> fieldValues = prepareCreate(setValues, type);
		this.pk = type.primaryKeySequence.next(type.getModel().getCurrentTransaction().getConnection());
		assert PK.isValid(pk) : pk;
		//System.out.println("create item "+type+" "+pk);
		
		final Entity entity = getEntity(false);
		entity.put(fieldValues);
		entity.write(toBlobs(fieldValues, null));
		
		postCreate();
	}
	
	
	/**
	 * Is called after every item creation.
	 * Override this method when needed.
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
		this(reactivationDummy, pk, null);
	}

	protected Item(final ReactivationConstructorDummy reactivationDummy, final int pk, final Type<? extends Item> typeWithoutJavaClass)
	{
		if(reactivationDummy!=Type.REACTIVATION_DUMMY)
			throw new RuntimeException("reactivation constructor is for internal purposes only, don't use it in your application!");
		this.type = typeWithoutJavaClass==null ? Type.forClass(getClass()) : typeWithoutJavaClass;
		this.pk = pk;
		//System.out.println("reactivate item:"+type+" "+pk);

		assert PK.isValid(pk) : pk;
	}
	
	private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		type = Type.forClass(getClass()); // TODO does not work for types without unique java class
	}
	
	public final <E> E get(final Function<E> function)
	{
		return function.get(this);
	}

	/**
	 * @throws MandatoryViolationException
	 *         if <tt>value</tt> is null and <tt>field</tt>
	 *         is {@link Field#isMandatory() mandatory}.
	 * @throws FinalViolationException
	 *         if <tt>field</tt> is {@link Field#isFinal() final}.
	 * @throws ClassCastException
	 *         if <tt>value</tt> is not compatible to <tt>field</tt>.
	 */
	public final <E> void set(final FunctionField<E> field, final E value)
		throws
			UniqueViolationException,
			MandatoryViolationException,
			StringLengthViolationException,
			FinalViolationException,
			ClassCastException
	{
		type.assertBelongs(field);
		
		if(field.isfinal)
			throw new FinalViolationException(field, this);

		field.check(value, this);

		checkUniqueConstraints(type, this, Collections.singletonMap(field, value));
		
		final Entity entity = getEntity();
		entity.put(field, value);
		entity.write(Collections.<BlobColumn, byte[]>emptyMap());
	}

	/**
	 * @throws MandatoryViolationException
	 *         if <tt>value</tt> is null and <tt>field</tt>
	 *         is {@link Field#isMandatory() mandatory}.
	 * @throws FinalViolationException
	 *         if <tt>field</tt> is {@link Field#isFinal() final}.
	 * @throws ClassCastException
	 *         if <tt>value</tt> is not compatible to <tt>field</tt>.
	 */
	public final void set(final SetValue... setValues)
		throws
			UniqueViolationException,
			MandatoryViolationException,
			StringLengthViolationException,
			FinalViolationException,
			ClassCastException
	{
		final Map<Field, Object> fieldValues = executeSetValues(setValues, this);
		for(final Field field : fieldValues.keySet())
		{
			type.assertBelongs(field);
			
			if(field.isfinal)
				throw new FinalViolationException(field, this);

			field.check(fieldValues.get(field), this);
		}
		checkUniqueConstraints(type, this, fieldValues);

		final Entity entity = getEntity();
		entity.put(fieldValues);
		entity.write(toBlobs(fieldValues, this));
	}
	
	private static final void checkUniqueConstraints(final Type<?> type, final Item item, final Map<? extends Field, ?> fieldValues)
	{
		for(final UniqueConstraint uc : type.uniqueConstraints)
			uc.check(item, fieldValues);
	}

	public final void deleteCopeItem() throws IntegrityViolationException
	{
		checkDeleteCopeItem(new HashSet<Item>());
		deleteCopeItem(new HashSet<Item>());
	}

	private final void checkDeleteCopeItem(final HashSet<Item> toDelete)
			throws IntegrityViolationException
	{
		toDelete.add(this);
		
		for(final ItemField<?> field : type.getReferences())
		{
			switch(field.getDeletePolicy())
			{
				case FORBID:
				{
					final Collection s = field.getType().search(Cope.equalAndCast(field, this));
					if(!s.isEmpty())
						throw new IntegrityViolationException(field, this);
					break;
				}
				case CASCADE:
				{
					for(final Item item : field.getType().search(Cope.equalAndCast(field, this)))
					{
						//System.out.println("------------check:"+item.toString());
						if(!toDelete.contains(item))
							item.checkDeleteCopeItem(toDelete);
					}
					break;
				}
				case NULLIFY:
					// avoid warnings
					break;
			}
		}
	}
		
	private final void deleteCopeItem(final HashSet<Item> toDelete)
	{
		toDelete.add(this);
		
		//final String tostring = toString();
		//System.out.println("------------delete:"+tostring);
		// TODO make sure, no item is deleted twice
		for(final ItemField<?> field : type.getReferences())
		{
			switch(field.getDeletePolicy())
			{
				case NULLIFY:
				{
					for(final Item item : field.getType().search(Cope.equalAndCast(field, this)))
					{
						//System.out.println("------------nullify:"+item.toString());
						item.set(field, null);
					}
					break;
				}
				case CASCADE:
				{
					for(final Item item : field.getType().search(Cope.equalAndCast(field, this)))
					{
						//System.out.println("------------check:"+item.toString());
						if(!toDelete.contains(item))
							item.deleteCopeItem(toDelete);
					}
					break;
				}
				case FORBID:
					// avoid warnings
					break;
			}
		}
		Entity entity = getEntity();
		entity.delete();
		entity.write(null);
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
	
	public static final ItemField.DeletePolicy FORBID = ItemField.DeletePolicy.FORBID;
	public static final ItemField.DeletePolicy NULLIFY = ItemField.DeletePolicy.NULLIFY;
	public static final ItemField.DeletePolicy CASCADE = ItemField.DeletePolicy.CASCADE;
	
	protected static final <C extends Item> Type<C> newType(final Class<C> javaClass)
	{
		return new Type<C>(javaClass);
	}
	
	public static final <E extends Enum<E>> EnumField<E> newEnumField(final Class<E> valueClass)
	{
		return new EnumField<E>(valueClass);
	}
	
	public static final <E extends Item> ItemField<E> newItemField(final Class<E> valueClass)
	{
		return new ItemField<E>(valueClass);
	}
	
	public static final <E extends Item> ItemField<E> newItemField(final Class<E> valueClass, final DeletePolicy policy)
	{
		return new ItemField<E>(valueClass, policy);
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
	
	private static final Map<Field, Object> executeSetValues(final SetValue<?>[] sources, final Item exceptionItem)
	{
		final HashMap<Field, Object> result = new HashMap<Field, Object>();
		for(final SetValue<?> source : sources)
		{
			if(source.settable instanceof Field)
			{
				putField(result, source);
			}
			else
			{
				for(final SetValue part : execute(source, exceptionItem))
					putField(result, part);
			}
		}
		return result;
	}
	
	private static final void putField(final HashMap<Field, Object> result, final SetValue<?> setValue)
	{
		if(result.put((Field)setValue.settable, setValue.value)!=null)
			throw new RuntimeException("duplicate field " + setValue.settable.toString());
	}
	
	private static final <X> SetValue[] execute(final SetValue<X> sv, final Item exceptionItem)
	{
		return sv.settable.execute(sv.value, exceptionItem);
	}
	
	private static final HashMap<BlobColumn, byte[]> toBlobs(final Map<Field, Object> fieldValues, final Item exceptionItem)
	{
		final HashMap<BlobColumn, byte[]> result = new HashMap<BlobColumn, byte[]>();
		
		for(final Field field : fieldValues.keySet())
		{
			if(!(field instanceof DataField))
				continue;
			
			final DataField.Value value = (DataField.Value)fieldValues.get(field);
			final DataField df = (DataField)field;
			result.put((BlobColumn)df.getColumn(), value!=null ? value.asArray(df, exceptionItem) : null);
		}
		return result;
	}
	
	// ------------------- deprecated stuff -------------------
	
	/**
	 * @deprecated Renamed to {@link #newEnumField(Class)}.
	 */
	@Deprecated
	public static final <E extends Enum<E>> EnumField<E> newEnumAttribute(final Class<E> valueClass)
	{
		return newEnumField(valueClass);
	}
	
	/**
	 * @deprecated Renamed to {@link #newItemField(Class)}.
	 */
	@Deprecated
	public static final <E extends Item> ItemField<E> newItemAttribute(final Class<E> valueClass)
	{
		return newItemField(valueClass);
	}
	
	/**
	 * @deprecated Renamed to {@link #newItemField(Class, com.exedio.cope.ItemField.DeletePolicy)}.
	 */
	@Deprecated
	public static final <E extends Item> ItemField<E> newItemAttribute(final Class<E> valueClass, final DeletePolicy policy)
	{
		return newItemField(valueClass, policy);
	}
}
