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
import com.exedio.cope.SetValue;
import com.exedio.cope.instrument.WrapperInitial;

public final class EnumSetFieldItem extends Item
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

	@WrapperInitial
	static final EnumSetField<Language> activeLanguage = EnumSetField.create(Language.class);

	public EnumSetFieldItem()
	{
		this(SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new EnumSetFieldItem with all the fields initially needed.
	 * @param activeLanguage the initial value for field {@link #activeLanguage}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	EnumSetFieldItem(
				@javax.annotation.Nonnull final java.util.Set<Language> activeLanguage)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(EnumSetFieldItem.activeLanguage,activeLanguage),
		});
	}

	/**
	 * Creates a new EnumSetFieldItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private EnumSetFieldItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="contains")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean containsActiveLanguage(@javax.annotation.Nonnull final Language element)
	{
		return EnumSetFieldItem.activeLanguage.contains(this,element);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="add")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void addActiveLanguage(@javax.annotation.Nonnull final Language element)
	{
		EnumSetFieldItem.activeLanguage.add(this,element);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="remove")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void removeActiveLanguage(@javax.annotation.Nonnull final Language element)
	{
		EnumSetFieldItem.activeLanguage.remove(this,element);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.EnumSet<Language> getActiveLanguage()
	{
		return EnumSetFieldItem.activeLanguage.get(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setActiveLanguage(@javax.annotation.Nonnull final java.util.Set<? extends Language> activeLanguage)
	{
		EnumSetFieldItem.activeLanguage.set(this,activeLanguage);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for enumSetFieldItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<EnumSetFieldItem> TYPE = com.exedio.cope.TypesBound.newType(EnumSetFieldItem.class,EnumSetFieldItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private EnumSetFieldItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
