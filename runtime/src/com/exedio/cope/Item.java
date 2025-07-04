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

import static com.exedio.cope.SetValue.map;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.ItemField.DeletePolicy;
import com.exedio.cope.instrument.WrapType;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;

/**
 * This is the super class for all classes,
 * that want to store their data persistently with COPE.
 * <p>
 * Serialization of instances of {@code Item}
 * is guaranteed to be light-weight -
 * there are no non-static, non-transient object reference
 * fields in this class or its superclasses.
 *
 * @author Ralf Wiebicke
 */
@WrapType(
		wildcardClassCaster=ItemWildcardCast.class,
		type=TypesBound.class,
		typeDoc="The persistent type information for {0}.",
		activationConstructor=ActivationParameters.class,
		top=Item.class
)
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
	 * Returns a string unique for this item among all other items of the model.
	 * For any item {@code a} in its model {@code m}
	 * the following holds true:
	 * {@code a.equals(m.getItem(a.getCopeID()).}
	 * Never returns null.
	 * @see Model#getItem(String)
	 */
	public final String getCopeID()
	{
		return type.id + ID_SEPARATOR + pk;
	}

	/**
	 * Is equivalent to {@code bf.{@link StringBuilder#append(String) append}({@link #getCopeID()});}
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
	 * Returns true, if {@code o} represents the same item as this item.
	 * Is equivalent to
	 * <pre>(o != null) &amp;&amp; (o instanceof Item) &amp;&amp; getCopeID().equals(((Item)o).getCopeID())</pre>
	 * Does not activate this item, if it's not already active.
	 */
	@Override
	public final boolean equals(final Object o)
	{
		if(this==o)
			return true;
		if(!(o instanceof final Item i))
			return false;

		//noinspection NonFinalFieldReferenceInEquals
		return type==i.type && pk==i.pk;
	}

	/**
	 * Returns a hash code, that is consistent with {@link #equals(Object)}.
	 * Note, that this is not necessarily equivalent to {@code getCopeID().hashCode()}.
	 * Does not activate this item, if it's not already active.
	 */
	@Override
	public final int hashCode()
	{
		//noinspection NonFinalFieldReferencedInHashCode
		return type.hashCode() ^ Long.hashCode(pk);
	}

	/**
	 * Defines an order consistent to the query result order when using
	 * {@link Query#setOrderBy(Function, boolean) Query.setOrderBy}
	 * methods with any {@link ItemFunction}.
	 */
	@Override
	public int compareTo(final Item o)
	{
		if(this==o)
			return 0;

		@SuppressWarnings("CompareToUsesNonFinalVariable")
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
	 * For any two item objects {@code a}, {@code b} the following holds true:
	 * <p>
	 * If and only if {@code a.equals(b)} then {@code a.activeCopeItem() == b.activeCopeItem()}.
	 * <p>
	 * So it does for items, what {@link String#intern} does for strings.
	 * Does activate this item, if it's not already active.
	 * Is guaranteed to be very cheap, if this item object is already active, which means
	 * this method returns {@code this}.
	 * Never returns null.
	 */
	public final Item activeCopeItem()
	{
		return getEntity().getItem();
	}

	protected Item(final SetValue<?>... setValues)
	{
		this.type = TypesBound.forClass(getClass());
		final FieldValues fieldValues = type.prepareCreate(setValues);
		this.pk = type.nextPrimaryKey();
		doCreate(fieldValues);
	}

	void doCreate(final FieldValues fieldValues)
	{
		assert PK.isValid(pk) : pk;
		//System.out.println("create item "+type+" "+pk);

		final Entity entity = getEntity(false);
		entity.put(fieldValues);
		entity.write(fieldValues.toBlobs());

		type.getModel().changeHook.afterNew(this);
	}


	/**
	 * Is called after every item creation.
	 * Is called only, if {@link DefaultChangeHook} has been
	 * {@link ModelBuilder#changeHooks(ChangeHook.Factory[]) installed}.
	 * Override this method when needed.
	 * The default implementation does nothing.
	 * <p>
	 * If you want to affect field values <b>before</b> creating the item
	 * write a method:
	 * <p>
	 * {@code static SetValue[] beforeNewCopeItem(SetValue[])}
	 * <p>
	 * If you want to postpone your action until the current transaction commits,
	 * use either
	 * {@link Model#addPreCommitHookIfAbsent(Runnable) pre}- or
	 * {@link Model#addPostCommitHookIfAbsent(Runnable) post}-commit hooks.
	 *
	 * @see ChangeHook#afterNew(Item)
	 */
	protected void afterNewCopeItem()
	{
		// empty default implementation
	}

	/**
	 * Is called before every item modification.
	 * Is called only, if {@link DefaultChangeHook} has been
	 * {@link ModelBuilder#changeHooks(ChangeHook.Factory[]) installed}.
	 * Override this method when needed.
	 * The default implementation does nothing.
	 * <p>
	 * If you want to affect field values before <b>creating</b> the item
	 * write a method:
	 * <p>
	 * {@code static SetValue[] beforeNewCopeItem(SetValue[])}
	 * <p>
	 * If you want to postpone your action until the current transaction commits,
	 * use either
	 * {@link Model#addPreCommitHookIfAbsent(Runnable) pre}- or
	 * {@link Model#addPostCommitHookIfAbsent(Runnable) post}-commit hooks.
	 *
	 * @see Item#set(SetValue[])
	 * @see Item#set(FunctionField, Object)
	 * @param setValues is never null and never empty
	 * @return must not return null
	 * @see ChangeHook#beforeSet(Item, SetValue[])
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
		requireNonNull(ap,
				"activation constructor is for internal purposes only, " +
				"don't use it in your application!");
		this.type = ap.type;
		this.pk = ap.pk;
		//System.out.println("activate item:"+type+" "+pk);

		assert PK.isValid(pk) : pk;
	}

	public final <E> E get(final Function<E> function) throws UnsupportedGetException
	{
		return function.get(this);
	}

	public final <E> E get(final FunctionField<E> field)
	{
		return field.get(this);
	}

	/**
	 * @throws MandatoryViolationException
	 *         if {@code value} is null and {@code field}
	 *         is {@link Field#isMandatory() mandatory}.
	 * @throws FinalViolationException
	 *         if {@code field} is {@link Field#isFinal() final}.
	 * @throws ClassCastException
	 *         if {@code value} is not compatible to {@code field}.
	 */
	public final <E> void set(final FunctionField<E> field, final E value)
	{
		set(map(field, value));
	}

	/**
	 * @throws MandatoryViolationException
	 *         if {@code value} is null and {@code field}
	 *         is {@link Field#isMandatory() mandatory}.
	 * @throws FinalViolationException
	 *         if {@code field} is {@link Field#isFinal() final}.
	 * @throws ClassCastException
	 *         if {@code value} is not compatible to {@code field}.
	 */
	public final void set(SetValue<?>... setValues)
	{
		requireNonNull(setValues, "setValues");
		if(setValues.length==0)
			return;

		setValues = type.getModel().changeHook.beforeSet(this, setValues);
		requireNonNull(setValues, "setValues after ChangeHook#beforeSet");
		if(setValues.length==0)
			return;

		for(final SetValue<?> sv : setValues)
			if(sv.settable.isFinal())
				throw new FinalViolationException((Feature)sv.settable, this);

		final FieldValues fieldValues = new FieldValues(this, setValues);
		type.executeCopyConstraints(fieldValues);
		type.checkUniqueConstraints(fieldValues);
		type.checkCheckConstraints(fieldValues);
		type.checkCopyConstraints(fieldValues);
		type.checkSettables(setValues, fieldValues);

		final Entity entity = getEntity();
		entity.put(fieldValues);
		entity.write(fieldValues.toBlobs());
	}

	/**
	 * @throws IntegrityViolationException
	 * if this item cannot be deleted due a {@link ItemField reference} with
	 * {@link DeletePolicy#FORBID} pointing to this item.
	 */
	public final void deleteCopeItem()
	{
		checkDeleteCopeItem(new HashSet<>());
		deleteCopeItem(new HashSet<>());
	}

	private void checkDeleteCopeItem(final HashSet<Item> toDelete)
	{
		toDelete.add(this);

		for(final ItemField<?> field : type.getReferences())
		{
			switch(field.getDeletePolicy())
			{
				case FORBID ->
				{
					final int referrers = field.getType().newQuery(Cope.equalAndCast(field, this)).total();
					if(referrers!=0)
						throw new IntegrityViolationException(field, this, referrers);
				}
				case CASCADE ->
				{
					for(final Item item : field.getType().search(Cope.equalAndCast(field, this)))
					{
						//System.out.println("------------check:"+item.toString());
						if(!toDelete.contains(item))
							item.checkDeleteCopeItem(toDelete);
					}
				}
				case NULLIFY ->
				{
				}
			}
		}
	}

	/**
	 * Is called before every item deletion.
	 * Is called only, if {@link DefaultChangeHook} has been
	 * {@link ModelBuilder#changeHooks(ChangeHook.Factory[]) installed}.
	 * Override this method when needed.
	 * The default implementation does nothing.
	 * <p>
	 * If you want to postpone your action until the current transaction commits,
	 * use either
	 * {@link Model#addPreCommitHookIfAbsent(Runnable) pre}- or
	 * {@link Model#addPostCommitHookIfAbsent(Runnable) post}-commit hooks.
	 *
	 * @see ChangeHook#beforeDelete(Item)
	 */
	protected void beforeDeleteCopeItem()
	{
		// empty default implementation
	}

	private void deleteCopeItem(final HashSet<Item> toDelete)
	{
		type.getModel().changeHook.beforeDelete(this);
		toDelete.add(this);

		//final String tostring = toString();
		//System.out.println("------------delete:"+tostring);
		// TODO make sure, no item is deleted twice
		for(final ItemField<?> field : type.getReferences())
		{
			switch(field.getDeletePolicy())
			{
				case NULLIFY ->
				{
					for(final Item item : field.getType().search(Cope.equalAndCast(field, this)))
					{
						//System.out.println("------------nullify:"+item.toString());
						item.set(field, null);
					}
				}
				case CASCADE ->
				{
					for(final Item item : field.getType().search(Cope.equalAndCast(field, this)))
					{
						//System.out.println("------------check:"+item.toString());
						if(!toDelete.contains(item))
							item.deleteCopeItem(toDelete);
					}
				}
				case FORBID ->
				{
				}
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
			assert this==e.getItem();
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

	private Entity getEntityIfActive()
	{
		return type.getModel().currentTransaction().getEntityIfActive(type, pk);
	}

	/**
	 * @deprecated for unit tests only
	 */
	@Deprecated
	int getUpdateCountIfActive(final Type<?> type)
	{
		final Entity entity = getEntityIfActive();
		return
			entity==null
			? Integer.MIN_VALUE
			: entity.getUpdateCount(type);
	}

	/**
	 * @deprecated for unit tests only
	 */
	@Deprecated
	int getUpdateCountGlobal(final Type<?> type)
	{
		final WrittenState state =
			type.getModel().currentTransaction().getGlobalState(this);
		return
			state==null
			? Integer.MIN_VALUE
			: state.updateCount.getValue(type);
	}

	// serialization -------------

	@Serial
	private static final long serialVersionUID = 2l;

	/**
	 * <a href="https://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/output.html#5324">See Spec</a>
	 */
	@Serial
	protected final Object writeReplace()
	{
		return type.isBound() ? this : new Serialized(type, pk);
	}

	@Serial
	private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		type = TypesBound.forClass(getClass());
	}

	private record Serialized(Type<?> type, long pk) implements Serializable
	{
		@Serial
		private static final long serialVersionUID = 2l;

		/**
		 * <a href="https://java.sun.com/j2se/1.5.0/docs/guide/serialization/spec/input.html#5903">See Spec</a>
		 */
		@Serial
		private Object readResolve()
		{
			return type.activate(pk);
		}
	}
}
