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

package com.exedio.cope.instrument.testmodel;

import com.exedio.cope.StringField;
import com.exedio.cope.instrument.testlib.LibComposite;

/** test extending a composite that is imported from a library */
class LibCompositeUser extends LibComposite
{
	static final StringField subField = new StringField();

	/**
	 * Creates a new LibCompositeUser with all the fields initially needed.
	 * @param superField the initial value for field {@link #superField}.
	 * @param subField the initial value for field {@link #subField}.
	 * @throws com.exedio.cope.MandatoryViolationException if superField, subField is null.
	 * @throws com.exedio.cope.StringLengthViolationException if superField, subField violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	LibCompositeUser(
				@javax.annotation.Nonnull final java.lang.String superField,
				@javax.annotation.Nonnull final java.lang.String subField)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.instrument.testlib.LibComposite.superField.map(superField),
			LibCompositeUser.subField.map(subField),
		});
	}

	/**
	 * Creates a new LibCompositeUser and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected LibCompositeUser(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #subField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.lang.String getSubField()
	{
		return get(LibCompositeUser.subField);
	}

	/**
	 * Sets a new value for {@link #subField}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setSubField(@javax.annotation.Nonnull final java.lang.String subField)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		set(LibCompositeUser.subField,subField);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;
}
