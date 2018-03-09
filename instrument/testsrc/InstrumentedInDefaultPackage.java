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

import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.StringField;

public final class InstrumentedInDefaultPackage extends Item
{
	static final StringField field = new StringField();

	static final ItemField<InstrumentedInDefaultPackage> item = ItemField.create(InstrumentedInDefaultPackage.class);


	/**
	 * Creates a new InstrumentedInDefaultPackage with all the fields initially needed.
	 * @param field the initial value for field {@link #field}.
	 * @param item the initial value for field {@link #item}.
	 * @throws com.exedio.cope.MandatoryViolationException if field, item is null.
	 * @throws com.exedio.cope.StringLengthViolationException if field violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	InstrumentedInDefaultPackage(
				@javax.annotation.Nonnull final java.lang.String field,
				@javax.annotation.Nonnull final InstrumentedInDefaultPackage item)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			InstrumentedInDefaultPackage.field.map(field),
			InstrumentedInDefaultPackage.item.map(item),
		});
	}

	/**
	 * Creates a new InstrumentedInDefaultPackage and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private InstrumentedInDefaultPackage(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #field}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	final java.lang.String getField()
	{
		return InstrumentedInDefaultPackage.field.get(this);
	}

	/**
	 * Sets a new value for {@link #field}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setField(@javax.annotation.Nonnull final java.lang.String field)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		InstrumentedInDefaultPackage.field.set(this,field);
	}

	/**
	 * Returns the value of {@link #item}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	final InstrumentedInDefaultPackage getItem()
	{
		return InstrumentedInDefaultPackage.item.get(this);
	}

	/**
	 * Sets a new value for {@link #item}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setItem(@javax.annotation.Nonnull final InstrumentedInDefaultPackage item)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		InstrumentedInDefaultPackage.item.set(this,item);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for instrumentedInDefaultPackage.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<InstrumentedInDefaultPackage> TYPE = com.exedio.cope.TypesBound.newType(InstrumentedInDefaultPackage.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private InstrumentedInDefaultPackage(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
