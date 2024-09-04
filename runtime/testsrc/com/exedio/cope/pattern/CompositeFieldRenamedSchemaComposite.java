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

package com.exedio.cope.pattern;

import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.StringField;

final class CompositeFieldRenamedSchemaComposite extends Composite
{
	static final StringField virgnTemp = new StringField();
	@CopeSchemaName("namedTemp")
	static final StringField wrongTemp = new StringField();

	/**
	 * Creates a new CompositeFieldRenamedSchemaComposite with all the fields initially needed.
	 * @param virgnTemp the initial value for field {@link #virgnTemp}.
	 * @param wrongTemp the initial value for field {@link #wrongTemp}.
	 * @throws com.exedio.cope.MandatoryViolationException if virgnTemp, wrongTemp is null.
	 * @throws com.exedio.cope.StringLengthViolationException if virgnTemp, wrongTemp violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	CompositeFieldRenamedSchemaComposite(
				@javax.annotation.Nonnull final java.lang.String virgnTemp,
				@javax.annotation.Nonnull final java.lang.String wrongTemp)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(CompositeFieldRenamedSchemaComposite.virgnTemp,virgnTemp),
			com.exedio.cope.SetValue.map(CompositeFieldRenamedSchemaComposite.wrongTemp,wrongTemp),
		});
	}

	/**
	 * Creates a new CompositeFieldRenamedSchemaComposite and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private CompositeFieldRenamedSchemaComposite(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #virgnTemp}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getVirgnTemp()
	{
		return get(CompositeFieldRenamedSchemaComposite.virgnTemp);
	}

	/**
	 * Sets a new value for {@link #virgnTemp}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setVirgnTemp(@javax.annotation.Nonnull final java.lang.String virgnTemp)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		set(CompositeFieldRenamedSchemaComposite.virgnTemp,virgnTemp);
	}

	/**
	 * Returns the value of {@link #wrongTemp}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getWrongTemp()
	{
		return get(CompositeFieldRenamedSchemaComposite.wrongTemp);
	}

	/**
	 * Sets a new value for {@link #wrongTemp}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setWrongTemp(@javax.annotation.Nonnull final java.lang.String wrongTemp)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		set(CompositeFieldRenamedSchemaComposite.wrongTemp,wrongTemp);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;
}
