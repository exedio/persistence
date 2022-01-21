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

import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;

public final class EnumMapFieldItem extends Item
{
	enum Language
	{
		DE, EN, PL,
		SUBCLASS
		{
			@SuppressWarnings("unused")
			void zack()
			{
				// empty
			}
		}
	}

	static final EnumMapField<Language, String> name = EnumMapField.create(Language.class, new StringField().optional());

	static final EnumMapField<Language, Integer> nameLength = EnumMapField.create(Language.class, new IntegerField().optional());

	static final EnumMapField<Language, String> defaults =
		EnumMapField.create(Language.class, new StringField().optional()).
			defaultTo(Language.DE, "defaultDExxx").
			defaultTo(Language.DE, "defaultDE").
			defaultTo(Language.EN, null);

	/**
	 * Creates a new EnumMapFieldItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public EnumMapFieldItem()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new EnumMapFieldItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private EnumMapFieldItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value mapped to {@code k} by the field map {@link #name}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	String getName(@javax.annotation.Nonnull final Language k)
	{
		return EnumMapFieldItem.name.get(this,k);
	}

	/**
	 * Associates {@code k} to a new value in the field map {@link #name}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setName(@javax.annotation.Nonnull final Language k,@javax.annotation.Nullable final String name)
	{
		EnumMapFieldItem.name.set(this,k,name);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getMap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Map<Language,String> getNameMap()
	{
		return EnumMapFieldItem.name.getMap(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setMap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setNameMap(@javax.annotation.Nonnull final java.util.Map<? extends Language,? extends String> name)
	{
		EnumMapFieldItem.name.setMap(this,name);
	}

	/**
	 * Returns the value mapped to {@code k} by the field map {@link #nameLength}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	Integer getNameLength(@javax.annotation.Nonnull final Language k)
	{
		return EnumMapFieldItem.nameLength.get(this,k);
	}

	/**
	 * Associates {@code k} to a new value in the field map {@link #nameLength}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setNameLength(@javax.annotation.Nonnull final Language k,@javax.annotation.Nullable final Integer nameLength)
	{
		EnumMapFieldItem.nameLength.set(this,k,nameLength);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getMap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Map<Language,Integer> getNameLengthMap()
	{
		return EnumMapFieldItem.nameLength.getMap(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setMap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setNameLengthMap(@javax.annotation.Nonnull final java.util.Map<? extends Language,? extends Integer> nameLength)
	{
		EnumMapFieldItem.nameLength.setMap(this,nameLength);
	}

	/**
	 * Returns the value mapped to {@code k} by the field map {@link #defaults}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	String getDefaults(@javax.annotation.Nonnull final Language k)
	{
		return EnumMapFieldItem.defaults.get(this,k);
	}

	/**
	 * Associates {@code k} to a new value in the field map {@link #defaults}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setDefaults(@javax.annotation.Nonnull final Language k,@javax.annotation.Nullable final String defaults)
	{
		EnumMapFieldItem.defaults.set(this,k,defaults);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getMap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Map<Language,String> getDefaultsMap()
	{
		return EnumMapFieldItem.defaults.getMap(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setMap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setDefaultsMap(@javax.annotation.Nonnull final java.util.Map<? extends Language,? extends String> defaults)
	{
		EnumMapFieldItem.defaults.setMap(this,defaults);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for enumMapFieldItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<EnumMapFieldItem> TYPE = com.exedio.cope.TypesBound.newType(EnumMapFieldItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private EnumMapFieldItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
