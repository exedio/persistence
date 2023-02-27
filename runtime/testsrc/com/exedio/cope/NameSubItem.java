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

@CopeName("Sub")
public final class NameSubItem extends NameLongItem
{
	static final IntegerField unique = new IntegerField().unique();
	static final IntegerField integer = new IntegerField();
	static final ItemField<NameSubItem> item = ItemField.create(NameSubItem.class);
	static final UniqueConstraint integers = UniqueConstraint.create(integer, item);

	@CopeSchemaName("uniqueY")
	static final IntegerField uniqueX = new IntegerField().unique();
	@CopeSchemaName("integerY")
	static final IntegerField integerX = new IntegerField();
	@CopeSchemaName("itemY")
	static final ItemField<NameSubItem> itemX = ItemField.create(NameSubItem.class);
	@CopeSchemaName("integersY")
	static final UniqueConstraint integersX = UniqueConstraint.create(integerX, itemX);

	/**
	 * Creates a new NameSubItem with all the fields initially needed.
	 * @param code the initial value for field {@link #code}.
	 * @param unique the initial value for field {@link #unique}.
	 * @param integer the initial value for field {@link #integer}.
	 * @param item the initial value for field {@link #item}.
	 * @param uniqueX the initial value for field {@link #uniqueX}.
	 * @param integerX the initial value for field {@link #integerX}.
	 * @param itemX the initial value for field {@link #itemX}.
	 * @throws com.exedio.cope.MandatoryViolationException if code, item, itemX is null.
	 * @throws com.exedio.cope.StringLengthViolationException if code violates its length constraint.
	 * @throws com.exedio.cope.UniqueViolationException if code, unique, integer, item, uniqueX, integerX, itemX is not unique.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	NameSubItem(
				@javax.annotation.Nonnull final java.lang.String code,
				final int unique,
				final int integer,
				@javax.annotation.Nonnull final NameSubItem item,
				final int uniqueX,
				final int integerX,
				@javax.annotation.Nonnull final NameSubItem itemX)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(com.exedio.cope.NameLongItem.code,code),
			com.exedio.cope.SetValue.map(NameSubItem.unique,unique),
			com.exedio.cope.SetValue.map(NameSubItem.integer,integer),
			com.exedio.cope.SetValue.map(NameSubItem.item,item),
			com.exedio.cope.SetValue.map(NameSubItem.uniqueX,uniqueX),
			com.exedio.cope.SetValue.map(NameSubItem.integerX,integerX),
			com.exedio.cope.SetValue.map(NameSubItem.itemX,itemX),
		});
	}

	/**
	 * Creates a new NameSubItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private NameSubItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #unique}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int getUnique()
	{
		return NameSubItem.unique.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #unique}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setUnique(final int unique)
			throws
				com.exedio.cope.UniqueViolationException
	{
		NameSubItem.unique.set(this,unique);
	}

	/**
	 * Finds a nameSubItem by its {@link #unique}.
	 * @param unique shall be equal to field {@link #unique}.
	 * @return null if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="for")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static NameSubItem forUnique(final int unique)
	{
		return NameSubItem.unique.searchUnique(NameSubItem.class,unique);
	}

	/**
	 * Finds a nameSubItem by its {@link #unique}.
	 * @param unique shall be equal to field {@link #unique}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="forStrict")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static NameSubItem forUniqueStrict(final int unique)
			throws
				java.lang.IllegalArgumentException
	{
		return NameSubItem.unique.searchUniqueStrict(NameSubItem.class,unique);
	}

	/**
	 * Returns the value of {@link #integer}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int getInteger()
	{
		return NameSubItem.integer.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #integer}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setInteger(final int integer)
			throws
				com.exedio.cope.UniqueViolationException
	{
		NameSubItem.integer.set(this,integer);
	}

	/**
	 * Returns the value of {@link #item}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	NameSubItem getItem()
	{
		return NameSubItem.item.get(this);
	}

	/**
	 * Sets a new value for {@link #item}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setItem(@javax.annotation.Nonnull final NameSubItem item)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.UniqueViolationException
	{
		NameSubItem.item.set(this,item);
	}

	/**
	 * Finds a nameSubItem by it's unique fields.
	 * @param integer shall be equal to field {@link #integer}.
	 * @param item shall be equal to field {@link #item}.
	 * @return null if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="finder")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static NameSubItem forIntegers(final int integer,@javax.annotation.Nonnull final NameSubItem item)
	{
		return NameSubItem.integers.search(NameSubItem.class,integer,item);
	}

	/**
	 * Finds a nameSubItem by its unique fields.
	 * @param integer shall be equal to field {@link #integer}.
	 * @param item shall be equal to field {@link #item}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="finderStrict")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static NameSubItem forIntegersStrict(final int integer,@javax.annotation.Nonnull final NameSubItem item)
			throws
				java.lang.IllegalArgumentException
	{
		return NameSubItem.integers.searchStrict(NameSubItem.class,integer,item);
	}

	/**
	 * Returns the value of {@link #uniqueX}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int getUniqueX()
	{
		return NameSubItem.uniqueX.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #uniqueX}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setUniqueX(final int uniqueX)
			throws
				com.exedio.cope.UniqueViolationException
	{
		NameSubItem.uniqueX.set(this,uniqueX);
	}

	/**
	 * Finds a nameSubItem by its {@link #uniqueX}.
	 * @param uniqueX shall be equal to field {@link #uniqueX}.
	 * @return null if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="for")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static NameSubItem forUniqueX(final int uniqueX)
	{
		return NameSubItem.uniqueX.searchUnique(NameSubItem.class,uniqueX);
	}

	/**
	 * Finds a nameSubItem by its {@link #uniqueX}.
	 * @param uniqueX shall be equal to field {@link #uniqueX}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="forStrict")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static NameSubItem forUniqueXStrict(final int uniqueX)
			throws
				java.lang.IllegalArgumentException
	{
		return NameSubItem.uniqueX.searchUniqueStrict(NameSubItem.class,uniqueX);
	}

	/**
	 * Returns the value of {@link #integerX}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int getIntegerX()
	{
		return NameSubItem.integerX.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #integerX}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setIntegerX(final int integerX)
			throws
				com.exedio.cope.UniqueViolationException
	{
		NameSubItem.integerX.set(this,integerX);
	}

	/**
	 * Returns the value of {@link #itemX}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	NameSubItem getItemX()
	{
		return NameSubItem.itemX.get(this);
	}

	/**
	 * Sets a new value for {@link #itemX}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setItemX(@javax.annotation.Nonnull final NameSubItem itemX)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.UniqueViolationException
	{
		NameSubItem.itemX.set(this,itemX);
	}

	/**
	 * Finds a nameSubItem by it's unique fields.
	 * @param integerX shall be equal to field {@link #integerX}.
	 * @param itemX shall be equal to field {@link #itemX}.
	 * @return null if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="finder")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static NameSubItem forIntegersX(final int integerX,@javax.annotation.Nonnull final NameSubItem itemX)
	{
		return NameSubItem.integersX.search(NameSubItem.class,integerX,itemX);
	}

	/**
	 * Finds a nameSubItem by its unique fields.
	 * @param integerX shall be equal to field {@link #integerX}.
	 * @param itemX shall be equal to field {@link #itemX}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="finderStrict")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static NameSubItem forIntegersXStrict(final int integerX,@javax.annotation.Nonnull final NameSubItem itemX)
			throws
				java.lang.IllegalArgumentException
	{
		return NameSubItem.integersX.searchStrict(NameSubItem.class,integerX,itemX);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for nameSubItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<NameSubItem> TYPE = com.exedio.cope.TypesBound.newType(NameSubItem.class,NameSubItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private NameSubItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
