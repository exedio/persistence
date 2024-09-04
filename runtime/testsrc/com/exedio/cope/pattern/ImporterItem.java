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

import com.exedio.cope.Item;
import com.exedio.cope.StringField;

final class ImporterItem extends Item
{
	static final StringField code = new StringField().toFinal().unique();
	static final Importer<String> byCode = Importer.create(code);
	static final StringField description = new StringField();
	static final StringField description2 = new StringField();

	/**
	 * Creates a new ImporterItem with all the fields initially needed.
	 * @param code the initial value for field {@link #code}.
	 * @param description the initial value for field {@link #description}.
	 * @param description2 the initial value for field {@link #description2}.
	 * @throws com.exedio.cope.MandatoryViolationException if code, description, description2 is null.
	 * @throws com.exedio.cope.StringLengthViolationException if code, description, description2 violates its length constraint.
	 * @throws com.exedio.cope.UniqueViolationException if code is not unique.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	ImporterItem(
				@javax.annotation.Nonnull final java.lang.String code,
				@javax.annotation.Nonnull final java.lang.String description,
				@javax.annotation.Nonnull final java.lang.String description2)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(ImporterItem.code,code),
			com.exedio.cope.SetValue.map(ImporterItem.description,description),
			com.exedio.cope.SetValue.map(ImporterItem.description2,description2),
		});
	}

	/**
	 * Creates a new ImporterItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private ImporterItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #code}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getCode()
	{
		return ImporterItem.code.get(this);
	}

	/**
	 * Finds a importerItem by its {@link #code}.
	 * @param code shall be equal to field {@link #code}.
	 * @return null if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="for")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static ImporterItem forCode(@javax.annotation.Nonnull final java.lang.String code)
	{
		return ImporterItem.code.searchUnique(ImporterItem.class,code);
	}

	/**
	 * Finds a importerItem by its {@link #code}.
	 * @param code shall be equal to field {@link #code}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="forStrict")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static ImporterItem forCodeStrict(@javax.annotation.Nonnull final java.lang.String code)
			throws
				java.lang.IllegalArgumentException
	{
		return ImporterItem.code.searchUniqueStrict(ImporterItem.class,code);
	}

	/**
	 * Import {@link #byCode}.
	 * @return the imported item
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="import")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static ImporterItem importByCode(@javax.annotation.Nonnull final String keyValue,@javax.annotation.Nonnull final com.exedio.cope.SetValue<?>... setValues)
	{
		return ImporterItem.byCode.doImport(ImporterItem.class,keyValue,setValues);
	}

	/**
	 * Import {@link #byCode}.
	 * @return the imported item
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="import")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static ImporterItem importByCode(@javax.annotation.Nonnull final String keyValue,@javax.annotation.Nonnull final java.util.List<com.exedio.cope.SetValue<?>> setValues)
	{
		return ImporterItem.byCode.doImport(ImporterItem.class,keyValue,setValues);
	}

	/**
	 * Returns the value of {@link #description}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getDescription()
	{
		return ImporterItem.description.get(this);
	}

	/**
	 * Sets a new value for {@link #description}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setDescription(@javax.annotation.Nonnull final java.lang.String description)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		ImporterItem.description.set(this,description);
	}

	/**
	 * Returns the value of {@link #description2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getDescription2()
	{
		return ImporterItem.description2.get(this);
	}

	/**
	 * Sets a new value for {@link #description2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setDescription2(@javax.annotation.Nonnull final java.lang.String description2)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		ImporterItem.description2.set(this,description2);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for importerItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<ImporterItem> TYPE = com.exedio.cope.TypesBound.newType(ImporterItem.class,ImporterItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private ImporterItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
