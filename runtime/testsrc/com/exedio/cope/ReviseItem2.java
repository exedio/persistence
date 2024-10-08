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

@CopeName("ReviseItem")
final class ReviseItem2 extends Item
{
	static final StringField field5 = new StringField();
	static final StringField field6 = new StringField();
	static final StringField field6b = new StringField();
	static final StringField field7 = new StringField();

	/**
	 * Creates a new ReviseItem2 with all the fields initially needed.
	 * @param field5 the initial value for field {@link #field5}.
	 * @param field6 the initial value for field {@link #field6}.
	 * @param field6b the initial value for field {@link #field6b}.
	 * @param field7 the initial value for field {@link #field7}.
	 * @throws com.exedio.cope.MandatoryViolationException if field5, field6, field6b, field7 is null.
	 * @throws com.exedio.cope.StringLengthViolationException if field5, field6, field6b, field7 violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	ReviseItem2(
				@javax.annotation.Nonnull final java.lang.String field5,
				@javax.annotation.Nonnull final java.lang.String field6,
				@javax.annotation.Nonnull final java.lang.String field6b,
				@javax.annotation.Nonnull final java.lang.String field7)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(ReviseItem2.field5,field5),
			com.exedio.cope.SetValue.map(ReviseItem2.field6,field6),
			com.exedio.cope.SetValue.map(ReviseItem2.field6b,field6b),
			com.exedio.cope.SetValue.map(ReviseItem2.field7,field7),
		});
	}

	/**
	 * Creates a new ReviseItem2 and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private ReviseItem2(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #field5}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getField5()
	{
		return ReviseItem2.field5.get(this);
	}

	/**
	 * Sets a new value for {@link #field5}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setField5(@javax.annotation.Nonnull final java.lang.String field5)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		ReviseItem2.field5.set(this,field5);
	}

	/**
	 * Returns the value of {@link #field6}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getField6()
	{
		return ReviseItem2.field6.get(this);
	}

	/**
	 * Sets a new value for {@link #field6}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setField6(@javax.annotation.Nonnull final java.lang.String field6)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		ReviseItem2.field6.set(this,field6);
	}

	/**
	 * Returns the value of {@link #field6b}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getField6b()
	{
		return ReviseItem2.field6b.get(this);
	}

	/**
	 * Sets a new value for {@link #field6b}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setField6b(@javax.annotation.Nonnull final java.lang.String field6b)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		ReviseItem2.field6b.set(this,field6b);
	}

	/**
	 * Returns the value of {@link #field7}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getField7()
	{
		return ReviseItem2.field7.get(this);
	}

	/**
	 * Sets a new value for {@link #field7}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setField7(@javax.annotation.Nonnull final java.lang.String field7)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		ReviseItem2.field7.set(this,field7);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for reviseItem2.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<ReviseItem2> TYPE = com.exedio.cope.TypesBound.newType(ReviseItem2.class,ReviseItem2::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private ReviseItem2(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
