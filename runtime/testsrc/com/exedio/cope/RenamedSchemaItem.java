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

@CopeSchemaName("Zain") final class RenamedSchemaItem extends Item
{
	@CopeSchemaName("zitem")
	static final ItemField<RenamedSchemaTargetItem> item = ItemField.create(RenamedSchemaTargetItem.class).optional();

	@CopeSchemaName("zuniqueSingle")
	static final StringField uniqueSingle = new StringField().optional().unique();

	static final StringField uniqueDouble1 = new StringField();
	static final IntegerField uniqueDouble2 = new IntegerField();
	@CopeSchemaName("zuniqueDouble")
	static final UniqueConstraint uniqueDouble = new UniqueConstraint(uniqueDouble1, uniqueDouble2);

	@CopeSchemaName("zring")
	static final StringField string = new StringField().optional().lengthMax(4);

	@CopeSchemaName("zinteger")
	static final IntegerField integer = new IntegerField().optional().defaultToNext(1234);

	@CopeSchemaName("zequence")
	static final Sequence sequence = new Sequence(555);

	/**
	 * Creates a new RenamedSchemaItem with all the fields initially needed.
	 * @param uniqueDouble1 the initial value for field {@link #uniqueDouble1}.
	 * @param uniqueDouble2 the initial value for field {@link #uniqueDouble2}.
	 * @throws com.exedio.cope.MandatoryViolationException if uniqueDouble1 is null.
	 * @throws com.exedio.cope.StringLengthViolationException if uniqueDouble1 violates its length constraint.
	 * @throws com.exedio.cope.UniqueViolationException if uniqueDouble1, uniqueDouble2 is not unique.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	RenamedSchemaItem(
				@javax.annotation.Nonnull final java.lang.String uniqueDouble1,
				final int uniqueDouble2)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			RenamedSchemaItem.uniqueDouble1.map(uniqueDouble1),
			RenamedSchemaItem.uniqueDouble2.map(uniqueDouble2),
		});
	}

	/**
	 * Creates a new RenamedSchemaItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private RenamedSchemaItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #item}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final RenamedSchemaTargetItem getItem()
	{
		return RenamedSchemaItem.item.get(this);
	}

	/**
	 * Sets a new value for {@link #item}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setItem(@javax.annotation.Nullable final RenamedSchemaTargetItem item)
	{
		RenamedSchemaItem.item.set(this,item);
	}

	/**
	 * Returns the value of {@link #uniqueSingle}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final java.lang.String getUniqueSingle()
	{
		return RenamedSchemaItem.uniqueSingle.get(this);
	}

	/**
	 * Sets a new value for {@link #uniqueSingle}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setUniqueSingle(@javax.annotation.Nullable final java.lang.String uniqueSingle)
			throws
				com.exedio.cope.UniqueViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		RenamedSchemaItem.uniqueSingle.set(this,uniqueSingle);
	}

	/**
	 * Finds a renamedSchemaItem by it's {@link #uniqueSingle}.
	 * @param uniqueSingle shall be equal to field {@link #uniqueSingle}.
	 * @return null if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="for")
	@javax.annotation.Nullable
	static final RenamedSchemaItem forUniqueSingle(@javax.annotation.Nonnull final java.lang.String uniqueSingle)
	{
		return RenamedSchemaItem.uniqueSingle.searchUnique(RenamedSchemaItem.class,uniqueSingle);
	}

	/**
	 * Finds a renamedSchemaItem by its {@link #uniqueSingle}.
	 * @param uniqueSingle shall be equal to field {@link #uniqueSingle}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="forStrict")
	@javax.annotation.Nonnull
	static final RenamedSchemaItem forUniqueSingleStrict(@javax.annotation.Nonnull final java.lang.String uniqueSingle)
			throws
				java.lang.IllegalArgumentException
	{
		return RenamedSchemaItem.uniqueSingle.searchUniqueStrict(RenamedSchemaItem.class,uniqueSingle);
	}

	/**
	 * Returns the value of {@link #uniqueDouble1}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	final java.lang.String getUniqueDouble1()
	{
		return RenamedSchemaItem.uniqueDouble1.get(this);
	}

	/**
	 * Sets a new value for {@link #uniqueDouble1}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setUniqueDouble1(@javax.annotation.Nonnull final java.lang.String uniqueDouble1)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.UniqueViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		RenamedSchemaItem.uniqueDouble1.set(this,uniqueDouble1);
	}

	/**
	 * Returns the value of {@link #uniqueDouble2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final int getUniqueDouble2()
	{
		return RenamedSchemaItem.uniqueDouble2.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #uniqueDouble2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setUniqueDouble2(final int uniqueDouble2)
			throws
				com.exedio.cope.UniqueViolationException
	{
		RenamedSchemaItem.uniqueDouble2.set(this,uniqueDouble2);
	}

	/**
	 * Finds a renamedSchemaItem by it's unique fields.
	 * @param uniqueDouble1 shall be equal to field {@link #uniqueDouble1}.
	 * @param uniqueDouble2 shall be equal to field {@link #uniqueDouble2}.
	 * @return null if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="finder")
	@javax.annotation.Nullable
	static final RenamedSchemaItem forUniqueDouble(@javax.annotation.Nonnull final java.lang.String uniqueDouble1,final int uniqueDouble2)
	{
		return RenamedSchemaItem.uniqueDouble.search(RenamedSchemaItem.class,uniqueDouble1,uniqueDouble2);
	}

	/**
	 * Returns the value of {@link #string}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final java.lang.String getString()
	{
		return RenamedSchemaItem.string.get(this);
	}

	/**
	 * Sets a new value for {@link #string}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setString(@javax.annotation.Nullable final java.lang.String string)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		RenamedSchemaItem.string.set(this,string);
	}

	/**
	 * Returns the value of {@link #integer}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final java.lang.Integer getInteger()
	{
		return RenamedSchemaItem.integer.get(this);
	}

	/**
	 * Sets a new value for {@link #integer}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setInteger(@javax.annotation.Nullable final java.lang.Integer integer)
	{
		RenamedSchemaItem.integer.set(this,integer);
	}

	/**
	 * Generates a new sequence number.
	 * The result is not managed by a {@link com.exedio.cope.Transaction}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="next")
	static final int nextSequence()
	{
		return RenamedSchemaItem.sequence.next();
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for renamedSchemaItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<RenamedSchemaItem> TYPE = com.exedio.cope.TypesBound.newType(RenamedSchemaItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private RenamedSchemaItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
