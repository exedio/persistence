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

import static com.exedio.cope.pattern.EnumMapFieldFallbackTest.AnEnum.fallback;
import static com.exedio.cope.pattern.EnumMapFieldFallbackTest.AnEnum.missing;
import static com.exedio.cope.pattern.EnumMapFieldFallbackTest.AnEnum.present;
import static com.exedio.cope.pattern.EnumMapFieldFallbackTest.AnItem.text;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.WrapperInitial;
import java.util.EnumMap;
import org.junit.jupiter.api.Test;

public class EnumMapFieldFallbackTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(AnItem.TYPE);

	public EnumMapFieldFallbackTest()
	{
		super(MODEL);
	}

	@Test void testWithoutFallback()
	{
		final EnumMap<AnEnum,String> map = new EnumMap<>(AnEnum.class);
		map.put(present, "vPres");
		final AnItem item = new AnItem(map);

		assertEquals("vPres", item.getText(present));
		assertEquals(null,    item.getText(missing));
		assertEquals(null,    item.getText(fallback));

		assertEquals("vPres", item.getTextWithFallback(present));
		assertEquals(null,    item.getTextWithFallback(missing));
		assertEquals(null,    item.getTextWithFallback(fallback));

		assertEqualsUnmodifiable(map, item.getTextMap());
		assertEqualsUnmodifiable(map, item.getTextMapWithFallback());

		assertSearch(item, item, present,  "vPres");
		assertSearch(null, null, missing,  "vPres");
		assertSearch(null, null, fallback, "vPres");
		assertSearch(null, null, present,  null);
		assertSearch(item, item, missing,  null);
		assertSearch(item, item, fallback, null);
		assertSearch(null, null, present,  "any");
		assertSearch(null, null, missing,  "any");
		assertSearch(null, null, fallback, "any");
	}

	@Test void testWithFallback()
	{
		final EnumMap<AnEnum,String> map = new EnumMap<>(AnEnum.class);
		map.put(present, "vPres");
		map.put(fallback, "vFall");
		final AnItem item = new AnItem(map);

		assertEquals("vPres", item.getText(present));
		assertEquals(null,    item.getText(missing));
		assertEquals("vFall", item.getText(fallback));

		assertEquals("vPres", item.getTextWithFallback(present));
		assertEquals("vFall", item.getTextWithFallback(missing));
		assertEquals("vFall", item.getTextWithFallback(fallback));

		assertEqualsUnmodifiable(map, item.getTextMap());
		map.put(missing, "vFall");
		assertEqualsUnmodifiable(map, item.getTextMapWithFallback());

		assertSearch(item, item, present,  "vPres");
		assertSearch(null, null, missing,  "vPres");
		assertSearch(null, null, fallback, "vPres");
		assertSearch(null, null, present,  "vFall");
		assertSearch(null, item, missing,  "vFall");
		assertSearch(item, item, fallback, "vFall");
		assertSearch(null, null, present,  null);
		assertSearch(item, null, missing,  null);
		assertSearch(null, null, fallback, null);
		assertSearch(null, null, present,  "any");
		assertSearch(null, null, missing,  "any");
		assertSearch(null, null, fallback, "any");
	}

	@Test void testEmpty()
	{
		final EnumMap<AnEnum,String> map = new EnumMap<>(AnEnum.class);
		final AnItem item = new AnItem(map);

		assertEquals(null, item.getText(present));
		assertEquals(null, item.getText(missing));
		assertEquals(null, item.getText(fallback));

		assertEquals(null, item.getTextWithFallback(present));
		assertEquals(null, item.getTextWithFallback(missing));
		assertEquals(null, item.getTextWithFallback(fallback));

		assertEqualsUnmodifiable(map, item.getTextMap());
		assertEqualsUnmodifiable(map, item.getTextMapWithFallback());

		assertSearch(item, item, present,  null);
		assertSearch(item, item, missing,  null);
		assertSearch(item, item, fallback, null);
		assertSearch(null, null, present,  "any");
		assertSearch(null, null, missing,  "any");
		assertSearch(null, null, fallback, "any");
	}


	private static void assertSearch(
			final AnItem directItem, final AnItem fallbackItem,
			final AnEnum key, final String value)
	{
		assertEquals(  directItem, AnItem.TYPE.searchSingleton(text.getField               (key).is(value)), "direct");
		assertEquals(fallbackItem, AnItem.TYPE.searchSingleton(text.getFunctionWithFallback(key).is(value)), "fallback");
	}


	enum AnEnum
	{
		present, missing, @CopeEnumFallback fallback
	}

	static final class AnItem extends Item
	{
		@WrapperInitial
		static final EnumMapField<AnEnum, String> text =
				EnumMapField.create(AnEnum.class, new StringField().optional()).fallback();

	/**
	 * Creates a new AnItem with all the fields initially needed.
	 * @param text the initial value for field {@link #text}.
	 * @throws com.exedio.cope.StringLengthViolationException if text violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	AnItem(
				@javax.annotation.Nonnull final java.util.Map<AnEnum,String> text)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(AnItem.text,text),
		});
	}

	/**
	 * Creates a new AnItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value mapped to {@code k} by the field map {@link #text}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	String getText(@javax.annotation.Nonnull final AnEnum k)
	{
		return AnItem.text.get(this,k);
	}

	/**
	 * Returns the value mapped to {@code k} by the field map {@link #text}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getWithFallback")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	String getTextWithFallback(@javax.annotation.Nonnull final AnEnum k)
	{
		return AnItem.text.getWithFallback(this,k);
	}

	/**
	 * Associates {@code k} to a new value in the field map {@link #text}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setText(@javax.annotation.Nonnull final AnEnum k,@javax.annotation.Nullable final String text)
	{
		AnItem.text.set(this,k,text);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getMap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Map<AnEnum,String> getTextMap()
	{
		return AnItem.text.getMap(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getMapWithFallback")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.Map<AnEnum,String> getTextMapWithFallback()
	{
		return AnItem.text.getMapWithFallback(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="setMap")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setTextMap(@javax.annotation.Nonnull final java.util.Map<? extends AnEnum,? extends String> text)
	{
		AnItem.text.setMap(this,text);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for anItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

}
