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

@CopeSchemaName("NameLongItem")
@CopeName("NameLongNameLongNameLongNameLongNameLongNameLongItem")
class NameLongItem extends Item
{

	static final StringField code = new StringField().unique();

	static final StringField codeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName =
		new StringField().optional().unique();

	static final ItemField<NameLongItem> pointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName =
		ItemField.create(NameLongItem.class).nullify();

	/**
	 * Creates a new NameLongItem with all the fields initially needed.
	 * @param code the initial value for field {@link #code}.
	 * @throws com.exedio.cope.MandatoryViolationException if code is null.
	 * @throws com.exedio.cope.StringLengthViolationException if code violates its length constraint.
	 * @throws com.exedio.cope.UniqueViolationException if code is not unique.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	NameLongItem(
				@javax.annotation.Nonnull final java.lang.String code)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			NameLongItem.code.map(code),
		});
	}

	/**
	 * Creates a new NameLongItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected NameLongItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #code}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.lang.String getCode()
	{
		return NameLongItem.code.get(this);
	}

	/**
	 * Sets a new value for {@link #code}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setCode(@javax.annotation.Nonnull final java.lang.String code)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.UniqueViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		NameLongItem.code.set(this,code);
	}

	/**
	 * Finds a nameLongItem by its {@link #code}.
	 * @param code shall be equal to field {@link #code}.
	 * @return null if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="for")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static final NameLongItem forCode(@javax.annotation.Nonnull final java.lang.String code)
	{
		return NameLongItem.code.searchUnique(NameLongItem.class,code);
	}

	/**
	 * Finds a nameLongItem by its {@link #code}.
	 * @param code shall be equal to field {@link #code}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="forStrict")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static final NameLongItem forCodeStrict(@javax.annotation.Nonnull final java.lang.String code)
			throws
				java.lang.IllegalArgumentException
	{
		return NameLongItem.code.searchUniqueStrict(NameLongItem.class,code);
	}

	/**
	 * Returns the value of {@link #codeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final java.lang.String getCodeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName()
	{
		return NameLongItem.codeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName.get(this);
	}

	/**
	 * Sets a new value for {@link #codeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setCodeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName(@javax.annotation.Nullable final java.lang.String codeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName)
			throws
				com.exedio.cope.UniqueViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		NameLongItem.codeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName.set(this,codeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName);
	}

	/**
	 * Finds a nameLongItem by its {@link #codeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName}.
	 * @param codeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName shall be equal to field {@link #codeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName}.
	 * @return null if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="for")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static final NameLongItem forCodeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName(@javax.annotation.Nonnull final java.lang.String codeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName)
	{
		return NameLongItem.codeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName.searchUnique(NameLongItem.class,codeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName);
	}

	/**
	 * Finds a nameLongItem by its {@link #codeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName}.
	 * @param codeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName shall be equal to field {@link #codeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="forStrict")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static final NameLongItem forCodeLoooooooooooooooooooooooooooooooooooooooooooooooooooongNameStrict(@javax.annotation.Nonnull final java.lang.String codeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName)
			throws
				java.lang.IllegalArgumentException
	{
		return NameLongItem.codeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName.searchUniqueStrict(NameLongItem.class,codeLoooooooooooooooooooooooooooooooooooooooooooooooooooongName);
	}

	/**
	 * Returns the value of {@link #pointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	final NameLongItem getPointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName()
	{
		return NameLongItem.pointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName.get(this);
	}

	/**
	 * Sets a new value for {@link #pointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setPointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName(@javax.annotation.Nullable final NameLongItem pointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName)
	{
		NameLongItem.pointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName.set(this,pointerLoooooooooooooooooooooooooooooooooooooooooooooooooooongName);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for nameLongItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<NameLongItem> TYPE = com.exedio.cope.TypesBound.newType(NameLongItem.class,NameLongItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected NameLongItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
