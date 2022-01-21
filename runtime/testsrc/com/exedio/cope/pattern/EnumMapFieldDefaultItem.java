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
// EnumMapFieldItem.Language has to be used with full name below, otherwise instrumentation fails on Windows:
// import com.exedio.cope.pattern.EnumMapFieldItem.Language;

public final class EnumMapFieldDefaultItem extends Item
{
	static final EnumMapField<EnumMapFieldItem.Language, String> text =
			EnumMapField.create(EnumMapFieldItem.Language.class, new StringField()).
				defaultTo(EnumMapFieldItem.Language.DE, "defaultDE").
				defaultTo(EnumMapFieldItem.Language.EN, "defaultEN").
				defaultTo(EnumMapFieldItem.Language.PL, "defaultPL").
				defaultTo(EnumMapFieldItem.Language.SUBCLASS, "defaultSUBCLASS");

	/**
	 * Creates a new EnumMapFieldDefaultItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	public EnumMapFieldDefaultItem()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new EnumMapFieldDefaultItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private EnumMapFieldDefaultItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value mapped to {@code k} by the field map {@link #text}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	String getText(@javax.annotation.Nonnull final EnumMapFieldItem.Language k)
	{
		return EnumMapFieldDefaultItem.text.get(this,k);
	}

	/**
	 * Associates {@code k} to a new value in the field map {@link #text}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setText(@javax.annotation.Nonnull final EnumMapFieldItem.Language k,@javax.annotation.Nonnull final String text)
	{
		EnumMapFieldDefaultItem.text.set(this,k,text);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getMap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Map<EnumMapFieldItem.Language,String> getTextMap()
	{
		return EnumMapFieldDefaultItem.text.getMap(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setMap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setTextMap(@javax.annotation.Nonnull final java.util.Map<? extends EnumMapFieldItem.Language,? extends String> text)
	{
		EnumMapFieldDefaultItem.text.setMap(this,text);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for enumMapFieldDefaultItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<EnumMapFieldDefaultItem> TYPE = com.exedio.cope.TypesBound.newType(EnumMapFieldDefaultItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private EnumMapFieldDefaultItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
