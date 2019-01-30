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

import com.exedio.cope.Item;
import com.exedio.cope.instrument.testfeature.NullabilityFeature;

public final class NullabilityItem extends Item
{
	static final NullabilityFeature optional = new NullabilityFeature(true);
	static final NullabilityFeature mandatory = new NullabilityFeature(false);

	/**
	 * Creates a new NullabilityItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public NullabilityItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new NullabilityItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private NullabilityItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="allCanReturnNull")
	@javax.annotation.Nullable
	static java.lang.Object allOptionalCanReturnNull()
	{
		return NullabilityItem.optional.allCanReturnNull();
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="allCannotReturnNull")
	@javax.annotation.Nonnull
	static java.lang.Object allOptionalCannotReturnNull()
	{
		return NullabilityItem.optional.allCannotReturnNull();
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="onlyOptionalsCanReturnNull")
	@javax.annotation.Nullable
	static java.lang.Object onlyOptionalOptionalsCanReturnNull()
	{
		return NullabilityItem.optional.onlyOptionalsCanReturnNull();
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="allCanTakeNull")
	static void allOptionalCanTakeNull(@javax.annotation.Nullable final java.lang.Object optional)
	{
		NullabilityItem.optional.allCanTakeNull(optional);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="allCannotTakeNull")
	static void allOptionalCannotTakeNull(@javax.annotation.Nonnull final java.lang.Object optional)
	{
		NullabilityItem.optional.allCannotTakeNull(optional);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="onlyOptionalsCanTakeNull")
	void onlyOptionalOptionalsCanTakeNull(@javax.annotation.Nullable final java.lang.Object optional)
	{
		NullabilityItem.optional.onlyOptionalsCanTakeNull(this,optional);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="allCanReturnNull")
	@javax.annotation.Nullable
	static java.lang.Object allMandatoryCanReturnNull()
	{
		return NullabilityItem.mandatory.allCanReturnNull();
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="allCannotReturnNull")
	@javax.annotation.Nonnull
	static java.lang.Object allMandatoryCannotReturnNull()
	{
		return NullabilityItem.mandatory.allCannotReturnNull();
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="onlyOptionalsCanReturnNull")
	@javax.annotation.Nonnull
	static java.lang.Object onlyMandatoryOptionalsCanReturnNull()
	{
		return NullabilityItem.mandatory.onlyOptionalsCanReturnNull();
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="allCanTakeNull")
	static void allMandatoryCanTakeNull(@javax.annotation.Nullable final java.lang.Object mandatory)
	{
		NullabilityItem.mandatory.allCanTakeNull(mandatory);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="allCannotTakeNull")
	static void allMandatoryCannotTakeNull(@javax.annotation.Nonnull final java.lang.Object mandatory)
	{
		NullabilityItem.mandatory.allCannotTakeNull(mandatory);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="onlyOptionalsCanTakeNull")
	void onlyMandatoryOptionalsCanTakeNull(@javax.annotation.Nonnull final java.lang.Object mandatory)
	{
		NullabilityItem.mandatory.onlyOptionalsCanTakeNull(this,mandatory);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for nullabilityItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<NullabilityItem> TYPE = com.exedio.cope.TypesBound.newType(NullabilityItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private NullabilityItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
