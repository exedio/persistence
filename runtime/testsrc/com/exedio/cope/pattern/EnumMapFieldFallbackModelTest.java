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

import static com.exedio.cope.pattern.EnumMapFieldFallbackModelTest.AnEnum.fall;
import static com.exedio.cope.pattern.EnumMapFieldFallbackModelTest.AnEnum.one;
import static com.exedio.cope.pattern.EnumMapFieldFallbackModelTest.AnEnum.two;
import static com.exedio.cope.pattern.EnumMapFieldFallbackModelTest.AnItem.fallMand;
import static com.exedio.cope.pattern.EnumMapFieldFallbackModelTest.AnItem.fallOpt;
import static com.exedio.cope.pattern.EnumMapFieldFallbackModelTest.AnItem.noneMand;
import static com.exedio.cope.pattern.EnumMapFieldFallbackModelTest.AnItem.noneOpt;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

public class EnumMapFieldFallbackModelTest
{
	static final Model MODEL = new Model(AnItem.TYPE);

	@Test void testIsMandatory()
	{
		assertEquals(true,  noneMand.getField(one ).isMandatory());
		assertEquals(true,  noneMand.getField(two ).isMandatory());
		assertEquals(true,  noneMand.getField(fall).isMandatory());
		assertEquals(false, noneOpt .getField(one ).isMandatory());
		assertEquals(false, noneOpt .getField(two ).isMandatory());
		assertEquals(false, noneOpt .getField(fall).isMandatory());

		assertEquals(false, fallMand.getField(one ).isMandatory());
		assertEquals(false, fallMand.getField(two ).isMandatory());
		assertEquals(true,  fallMand.getField(fall).isMandatory());
		assertEquals(false, fallOpt .getField(one ).isMandatory());
		assertEquals(false, fallOpt .getField(two ).isMandatory());
		assertEquals(false, fallOpt .getField(fall).isMandatory());
	}

	@Test void testHasFallback()
	{
		assertEquals(false, noneMand.hasFallbacks());
		assertEquals(false, noneOpt .hasFallbacks());
		assertEquals(true,  fallMand.hasFallbacks());
		assertEquals(true,  fallOpt .hasFallbacks());
	}

	@Test void testGetFallback()
	{
		assertEquals(null, noneMand.getFallback());
		assertEquals(null, noneOpt .getFallback());
		assertEquals(fall, fallMand.getFallback());
		assertEquals(fall, fallOpt .getFallback());
	}

	@Test void testGetFunctionWithFallback()
	{
		assertEquals("coalesce(AnItem.fallMand-one,AnItem.fallMand-fall)", fallMand.getFunctionWithFallback(one ).toString());
		assertEquals("coalesce(AnItem.fallMand-two,AnItem.fallMand-fall)", fallMand.getFunctionWithFallback(two ).toString());
		assertEquals(                             "AnItem.fallMand-fall" , fallMand.getFunctionWithFallback(fall).toString());
	}

	@Test void testGetFunctionWithFallbackWithoutFallback()
	{
		try
		{
			noneMand.getFunctionWithFallback(one);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("field AnItem.noneMand has no fallbacks", e.getMessage());
		}
	}

	@Test void testGetFunctionWithFallbackNullKey()
	{
		try
		{
			fallMand.getFunctionWithFallback(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("key", e.getMessage());
		}
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_INFERRED")
	@Test void testFallbackToNull()
	{
		final EnumMapField<?,?> f = EnumMapField.create(AnEnum.class, new StringField());
		try
		{
			f.fallbackTo(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("key", e.getMessage());
		}
	}


	enum AnEnum
	{
		one, two, @CopeEnumFallback fall
	}

	static final class AnItem extends com.exedio.cope.Item // TODO use import, but this is not accepted by javac
	{
		static final EnumMapField<AnEnum, String> noneMand = EnumMapField.create(AnEnum.class, new StringField());
		static final EnumMapField<AnEnum, String> noneOpt  = EnumMapField.create(AnEnum.class, new StringField().optional());

		static final EnumMapField<AnEnum, String> fallMand = EnumMapField.create(AnEnum.class, new StringField()).fallback();
		static final EnumMapField<AnEnum, String> fallOpt  = EnumMapField.create(AnEnum.class, new StringField().optional()).fallback();

	/**
	 * Creates a new AnItem with all the fields initially needed.
	 * @param noneMand the initial value for field {@link #noneMand}.
	 * @param fallMand the initial value for field {@link #fallMand}.
	 * @throws com.exedio.cope.MandatoryViolationException if noneMand, fallMand is null.
	 * @throws com.exedio.cope.StringLengthViolationException if noneMand, fallMand violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	AnItem(
				@javax.annotation.Nonnull final java.util.EnumMap<AnEnum,String> noneMand,
				@javax.annotation.Nonnull final java.util.EnumMap<AnEnum,String> fallMand)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			AnItem.noneMand.map(noneMand),
			AnItem.fallMand.map(fallMand),
		});
	}

	/**
	 * Creates a new AnItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value mapped to {@code k} by the field map {@link #noneMand}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	String getNoneMand(@javax.annotation.Nonnull final AnEnum k)
	{
		return AnItem.noneMand.get(this,k);
	}

	/**
	 * Associates {@code k} to a new value in the field map {@link #noneMand}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setNoneMand(@javax.annotation.Nonnull final AnEnum k,@javax.annotation.Nonnull final String noneMand)
	{
		AnItem.noneMand.set(this,k,noneMand);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getMap")
	@javax.annotation.Nonnull
	java.util.Map<AnEnum,String> getNoneMandMap()
	{
		return AnItem.noneMand.getMap(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setMap")
	void setNoneMandMap(@javax.annotation.Nonnull final java.util.Map<? extends AnEnum,? extends String> noneMand)
	{
		AnItem.noneMand.setMap(this,noneMand);
	}

	/**
	 * Returns the value mapped to {@code k} by the field map {@link #noneOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	String getNoneOpt(@javax.annotation.Nonnull final AnEnum k)
	{
		return AnItem.noneOpt.get(this,k);
	}

	/**
	 * Associates {@code k} to a new value in the field map {@link #noneOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setNoneOpt(@javax.annotation.Nonnull final AnEnum k,@javax.annotation.Nullable final String noneOpt)
	{
		AnItem.noneOpt.set(this,k,noneOpt);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getMap")
	@javax.annotation.Nonnull
	java.util.Map<AnEnum,String> getNoneOptMap()
	{
		return AnItem.noneOpt.getMap(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setMap")
	void setNoneOptMap(@javax.annotation.Nonnull final java.util.Map<? extends AnEnum,? extends String> noneOpt)
	{
		AnItem.noneOpt.setMap(this,noneOpt);
	}

	/**
	 * Returns the value mapped to {@code k} by the field map {@link #fallMand}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	String getFallMand(@javax.annotation.Nonnull final AnEnum k)
	{
		return AnItem.fallMand.get(this,k);
	}

	/**
	 * Returns the value mapped to {@code k} by the field map {@link #fallMand}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getWithFallback")
	@javax.annotation.Nonnull
	String getFallMandWithFallback(@javax.annotation.Nonnull final AnEnum k)
	{
		return AnItem.fallMand.getWithFallback(this,k);
	}

	/**
	 * Associates {@code k} to a new value in the field map {@link #fallMand}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFallMand(@javax.annotation.Nonnull final AnEnum k,@javax.annotation.Nullable final String fallMand)
	{
		AnItem.fallMand.set(this,k,fallMand);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getMap")
	@javax.annotation.Nonnull
	java.util.Map<AnEnum,String> getFallMandMap()
	{
		return AnItem.fallMand.getMap(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getMapWithFallback")
	@javax.annotation.Nonnull
	java.util.Map<AnEnum,String> getFallMandMapWithFallback()
	{
		return AnItem.fallMand.getMapWithFallback(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setMap")
	void setFallMandMap(@javax.annotation.Nonnull final java.util.Map<? extends AnEnum,? extends String> fallMand)
	{
		AnItem.fallMand.setMap(this,fallMand);
	}

	/**
	 * Returns the value mapped to {@code k} by the field map {@link #fallOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	String getFallOpt(@javax.annotation.Nonnull final AnEnum k)
	{
		return AnItem.fallOpt.get(this,k);
	}

	/**
	 * Returns the value mapped to {@code k} by the field map {@link #fallOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getWithFallback")
	@javax.annotation.Nullable
	String getFallOptWithFallback(@javax.annotation.Nonnull final AnEnum k)
	{
		return AnItem.fallOpt.getWithFallback(this,k);
	}

	/**
	 * Associates {@code k} to a new value in the field map {@link #fallOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setFallOpt(@javax.annotation.Nonnull final AnEnum k,@javax.annotation.Nullable final String fallOpt)
	{
		AnItem.fallOpt.set(this,k,fallOpt);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getMap")
	@javax.annotation.Nonnull
	java.util.Map<AnEnum,String> getFallOptMap()
	{
		return AnItem.fallOpt.getMap(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getMapWithFallback")
	@javax.annotation.Nonnull
	java.util.Map<AnEnum,String> getFallOptMapWithFallback()
	{
		return AnItem.fallOpt.getMapWithFallback(this);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setMap")
	void setFallOptMap(@javax.annotation.Nonnull final java.util.Map<? extends AnEnum,? extends String> fallOpt)
	{
		AnItem.fallOpt.setMap(this,fallOpt);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for anItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
}
