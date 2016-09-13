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

import static java.util.Objects.requireNonNull;

import com.exedio.cope.ItemField.DeletePolicy;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

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
public abstract class Item implements Serializable, Comparable<Item>
{
	static final char ID_SEPARATOR = '-';

	transient Type<?> type;

	/**
	 * The primary key of the item,
	 * that is unique within the {@link #type} of this item.
	 */
	final long pk;

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
	 * Is equivalent to <tt>bf.{@link StringBuilder#append(String) append}({@link #getCopeID()});</tt>
	 */
	public final void appendCopeID(final StringBuilder bf)
	{
		bf.append(type.id).
			append(ID_SEPARATOR).
			append(pk);
	}

	/**
	 * Returns the type of this item.
	 * Never returns null.
	 */
	public final Type<?> getCopeType()
	{
		assert type!=null;
		return type;
	}

	/**
	 * Returns true, if <tt>o</tt> represents the same item as this item.
	 * Is equivalent to
	 * <pre>(o != null) &amp;&amp; (o instanceof Item) &amp;&amp; getCopeID().equals(((Item)o).getCopeID())</pre>
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
	 * Note, that this is not necessarily equivalent to <tt>getCopeID().hashCode()</tt>.
	 * Does not activate this item, if it's not already active.
	 */
	@Override
	public final int hashCode()
	{
		return type.hashCode() ^ Long.hashCode(pk);
	}

	/**
	 * Defines an order consistent to the query result order when using
	 * {@link Query#setOrderBy(Selectable, boolean) Query.setOrderBy}
	 * methods with any {@link ItemFunction}.
	 */
	@Override
	public int compareTo(final Item o)
	{
		if(this==o)
			return 0;

		final int typeResult = type.toptype.compareTo(o.type.toptype);
		if(typeResult!=0)
			return typeResult;

		return Long.compare(pk, o.pk);
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

	protected Item(final SetValue<?>... setValues)
	{
		this.type = TypesBound.forClass(getClass());
		final LinkedHashMap<Field<?>, Object> fieldValues = type.prepareCreate(setValues);
		this.pk = type.nextPrimaryKey();
		doCreate(fieldValues);
	}

	void doCreate(final LinkedHashMap<Field<?>, Object> fieldValues)
	{
		assert PK.isValid(pk) : pk;
		//System.out.println("create item "+type+" "+pk);

		final Entity entity = getEntity(false);
		entity.put(fieldValues);
		type.checkCheckConstraints(this, entity, null);
		entity.write(toBlobs(fieldValues, null));

		afterNewCopeItem();
	}


	/**
	 * Is called after every item creation.
	 * Override this method when needed.
	 * The default implementation does nothing.
	 * <p>
	 * If you want to affect field values <b>before</b> creating the item
	 * write a method:
	 * <p>
	 * {@code static SetValue[] beforeNewCopeItem(SetValue[])}
	 */
	protected void afterNewCopeItem()
	{
		// empty default implementation
	}

	/**
	 * Is called before every item modification.
	 * Override this method when needed.
	 * The default implementation does nothing.
	 * <p>
	 * If you want to affect field values before <b>creating</b> the item
	 * write a method:
	 * <p>
	 * {@code static SetValue[] beforeNewCopeItem(SetValue[])}
	 * @see Item#set(SetValue[])
	 * @see Item#set(FunctionField, Object)
	 * @param setValues is never null and never empty
	 * @return must not return null
	 */
	protected SetValue<?>[] beforeSetCopeItem(final SetValue<?>[] setValues)
	{
		return setValues;
	}

	/**
	 * Activation constructor.
	 * Is used for internal purposes only.
	 * Does not actually create a new item, but a passive item object for
	 * an already existing item.
	 */
	protected Item(final ActivationParameters ap)
	{
		if(ap==null)
			throw new RuntimeException(
					"activation constructor is for internal purposes only, " +
					"don't use it in your application!");

		this.type = ap.type;
		this.pk = ap.pk;
		//System.out.println("activate item:"+type+" "+pk);

		assert PK.isValid(pk) : pk;
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
	{
		set(field.map(value));
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
	public final void set(SetValue<?>... setValues)
	{
		requireNonNull(setValues, "setValues");
		if(setValues.length==0)
			return;

		setValues = beforeSetCopeItem(setValues);
		requireNonNull(setValues, "setValues after beforeSetCopeItem");
		if(setValues.length==0)
			return;

		for(final SetValue<?> sv : setValues)
			if(sv.settable.isFinal())
			{
				@SuppressWarnings("deprecation")
				final FinalViolationException ex =
					new FinalViolationException((Feature)sv.settable, sv.settable, this);
				throw ex;
			}

		final LinkedHashMap<Field<?>, Object> fieldValues = executeSetValues(setValues, this);
		for(final Map.Entry<Field<?>, Object> e : fieldValues.entrySet())
		{
			final Field<?> field = e.getKey();
			type.assertBelongs(field);

			FinalViolationException.check(field, this);
			field.check(e.getValue(), this);
		}
		type.checkUniqueConstraints(this, fieldValues);
		checkSettables(this, setValues, fieldValues);

		final Entity entity = getEntity();
		entity.put(fieldValues);
		type.checkCheckConstraints(this, entity, this);
		entity.write(toBlobs(fieldValues, this));
	}

	static void checkSettables(
			final Item item,
			final SetValue<?>[] setValues,
			final Map<? extends Field<?>, ?> fieldValues)
	{
		for(final SetValue<?> sv : setValues)
			if(sv.settable instanceof CheckingSettable<?>)
			{
				// TODO test unmodifiableMap
				check(item, sv, Collections.unmodifiableMap(fieldValues));
			}
	}

	private static <E> void check(
			final Item item,
			final SetValue<E> sv,
			final Map<? extends Field<?>, ?> fieldValues)
	{
		((CheckingSettable<E>)sv.settable).check(sv.value, item, fieldValues);
	}

	/**
	 * @throws IntegrityViolationException
	 * if this item cannot be deleted due a {@link ItemField reference} with
	 * {@link DeletePolicy#FORBID} pointing to this item.
	 */
	public final void deleteCopeItem()
	{
		checkDeleteCopeItem(new HashSet<Item>());
		deleteCopeItem(new HashSet<Item>());
	}

	private final void checkDeleteCopeItem(final HashSet<Item> toDelete)
	{
		toDelete.add(this);

		for(final ItemField<?> field : type.getReferences())
		{
			switch(field.getDeletePolicy())
			{
				case FORBID:
				{
					final int referrers = field.getType().newQuery(Cope.equalAndCast(field, this)).total();
					if(referrers!=0)
						throw new IntegrityViolationException(field, this, referrers);
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

	/**
	 * Is called before every item deletion.
	 * Override this method when needed.
	 * The default implementation does nothing.
	 * You may want to use {@link Model#addCommitHook} to postpone your action
	 * until the deletion is committed by the current transaction.
	 */
	protected void beforeDeleteCopeItem()
	{
		// empty default implementation
	}

	private final void deleteCopeItem(final HashSet<Item> toDelete)
	{
		beforeDeleteCopeItem();
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
		final Entity entity = getEntity();
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
		catch ( final NoSuchItemException e )
		{
			return false;
		}
	}

	// activation/deactivation -----------------------------------------------------

	final Entity getEntity()
	{
		return getEntity(true);
	}

	final Entity getEntity(final boolean present)
	{
		return type.getModel().currentTransaction().getEntity(this, present);
	}

	private final Entity getEntityIfActive()
	{
		return type.getModel().currentTransaction().getEntityIfActive(type, pk);
	}

	/**
	 * @deprecated for unit tests only
	 */
	@Deprecated
	int getUpdateCountIfActive()
	{
		final Entity entity = getEntityIfActive();
		return
			entity==null
			? Integer.MIN_VALUE
			: entity.getUpdateCount();
	}

	/**
	 * @deprecated for unit tests only
	 */
	@Deprecated
	int getUpdateCountGlobal()
	{
		final WrittenState state =
			type.getModel().currentTransaction().getGlobalState(this);
		return
			state==null
			? Integer.MIN_VALUE
			: state.updateCount;
	}

	static final LinkedHashMap<Field<?>, Object> executeSetValues(final SetValue<?>[] sources, final Item exceptionItem)
	{
		final LinkedHashMap<Field<?>, Object> result = new LinkedHashMap<>();
		for(final SetValue<?> source : sources)
		{
			if(source.settable instanceof Field<?>)
			{
				putField(result, source);
			}
			else
			{
				for(final SetValue<?> part : execute(source, exceptionItem))
					putField(result, part);
			}
		}
		return result;
	}

	private static final void putField(final LinkedHashMap<Field<?>, Object> result, final SetValue<?> setValue)
	{
		if(result.put((Field<?>)setValue.settable, setValue.value)!=null)
			throw new IllegalArgumentException("SetValues contain duplicate settable " + setValue.settable);
	}

	private static final <X> SetValue<?>[] execute(final SetValue<X> sv, final Item exceptionItem)
	{
		return sv.settable.execute(sv.value, exceptionItem);
	}

	@SuppressFBWarnings("WMI_WRONG_MAP_ITERATOR") // Inefficient use of keySet iterator instead of entrySet iterator
	static final HashMap<BlobColumn, byte[]> toBlobs(final LinkedHashMap<Field<?>, Object> fieldValues, final Item exceptionItem)
	{
		final HashMap<BlobColumn, byte[]> result = new HashMap<>();

		for(final Field<?> field : fieldValues.keySet())
		{
			if(!(field instanceof DataField))
				continue;

			final DataField.Value value = (DataField.Value)fieldValues.get(field);
			final DataField df = (DataField)field;
			result.put((BlobColumn)df.getColumn(), value!=null ? value.asArray(df, exceptionItem) : null);
		}
		return result;
	}

	// serialization -------------

	private static final long serialVersionUID = 2l;

	/**
	 * <a href="http://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/output.html#5324">See Spec</a>
	 */
	protected final Object writeReplace()
	{
		return type.isBound() ? this : new Serialized(type, pk);
	}

	private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		type = TypesBound.forClass(getClass());
	}

	private static final class Serialized implements Serializable
	{
		private static final long serialVersionUID = 2l;

		private final Type<?> type;
		private final long pk;

		Serialized(final Type<?> type, final long pk)
		{
			this.type = type;
			this.pk = pk;
		}

		/**
		 * <a href="http://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/input.html#5903">See Spec</a>
		 */
		private Object readResolve()
		{
			return type.activate(pk);
		}
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Is default delete policy anyway.
	 */
	@Deprecated
	public static final ItemField.DeletePolicy FORBID = ItemField.DeletePolicy.FORBID;

	/**
	 * @deprecated Use {@link ItemField#nullify()} instead
	 */
	@Deprecated
	public static final ItemField.DeletePolicy NULLIFY = ItemField.DeletePolicy.NULLIFY;

	/**
	 * @deprecated Use {@link ItemField#cascade()} instead
	 */
	@Deprecated
	public static final ItemField.DeletePolicy CASCADE = ItemField.DeletePolicy.CASCADE;

	/**
	 * @deprecated Use {@link EnumField#create(Class)} instead
	 */
	@Deprecated
	public static final <E extends Enum<E>> EnumField<E> newEnumField(final Class<E> valueClass)
	{
		return EnumField.create(valueClass);
	}

	/**
	 * @deprecated Use {@link ItemField#create(Class)} instead
	 */
	@Deprecated
	public static final <E extends Item> ItemField<E> newItemField(final Class<E> valueClass)
	{
		return ItemField.create(valueClass);
	}

	/**
	 * @deprecated Use {@link ItemField#create(Class, DeletePolicy)} instead
	 */
	@Deprecated
	public static final <E extends Item> ItemField<E> newItemField(final Class<E> valueClass, final DeletePolicy policy)
	{
		return ItemField.create(valueClass, policy);
	}

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

	/**
	 * @deprecated Use {@link TypesBound#newType(Class)} instead.
	 */
	@Deprecated
	protected static final <C extends Item> Type<C> newType(final Class<C> javaClass)
	{
		return TypesBound.newType(javaClass);
	}
}
