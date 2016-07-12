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
import static org.junit.Assert.assertEquals;

import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.TestWithEnvironment;
import java.util.EnumMap;
import org.junit.Test;

public class EnumMapFieldFallbackTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(AnItem.TYPE);

	public EnumMapFieldFallbackTest()
	{
		super(MODEL);
	}

	@Test public void testWithoutFallback()
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

		assertEquals(map, item.getTextMap());
		assertEquals(map, item.getTextMapWithFallback());

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

	@Test public void testWithFallback()
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

		assertEquals(map, item.getTextMap());
		map.put(missing, "vFall");
		assertEquals(map, item.getTextMapWithFallback());

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

	@Test public void testEmpty()
	{
		final EnumMap<AnEnum,String> map = new EnumMap<>(AnEnum.class);
		final AnItem item = new AnItem(map);

		assertEquals(null, item.getText(present));
		assertEquals(null, item.getText(missing));
		assertEquals(null, item.getText(fallback));

		assertEquals(null, item.getTextWithFallback(present));
		assertEquals(null, item.getTextWithFallback(missing));
		assertEquals(null, item.getTextWithFallback(fallback));

		assertEquals(map, item.getTextMap());
		assertEquals(map, item.getTextMapWithFallback());

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
		assertEquals("direct",     directItem, AnItem.TYPE.searchSingleton(text.getField               (key).equal(value)));
		assertEquals("fallback", fallbackItem, AnItem.TYPE.searchSingleton(text.getFunctionWithFallback(key).equal(value)));
	}


	static enum AnEnum
	{
		present, missing, @CopeEnumFallback fallback;
	}

	static final class AnItem extends com.exedio.cope.Item // TODO use import, but this is not accepted by javac
	{
		/** @cope.initial */
		static final EnumMapField<AnEnum, String> text =
				EnumMapField.create(AnEnum.class, new StringField().optional()).fallback();

	/**

	 **
	 * Creates a new AnItem with all the fields initially needed.
	 * @param text the initial value for field {@link #text}.
	 * @throws com.exedio.cope.StringLengthViolationException if text violates its length constraint.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of fields.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	AnItem(
				@javax.annotation.Nonnull final java.util.EnumMap<AnEnum,String> text)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			AnItem.text.map(text),
		});
	}/**

	 **
	 * Creates a new AnItem and sets the given fields initially.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.generic.constructor public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private AnItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}/**

	 **
	 * Returns the value mapped to <tt>k</tt> by the field map {@link #text}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	final String getText(@javax.annotation.Nonnull final AnEnum k)
	{
		return AnItem.text.get(this,k);
	}/**

	 **
	 * Returns the value mapped to <tt>k</tt> by the field map {@link #text}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getWithFallback public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	final String getTextWithFallback(@javax.annotation.Nonnull final AnEnum k)
	{
		return AnItem.text.getWithFallback(this,k);
	}/**

	 **
	 * Associates <tt>k</tt> to a new value in the field map {@link #text}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void setText(@javax.annotation.Nonnull final AnEnum k,@javax.annotation.Nullable final String text)
	{
		AnItem.text.set(this,k,text);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getMap public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nonnull()
	final java.util.Map<AnEnum,String> getTextMap()
	{
		return AnItem.text.getMap(this);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getMapWithFallback public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nonnull()
	final java.util.Map<AnEnum,String> getTextMapWithFallback()
	{
		return AnItem.text.getMapWithFallback(this);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.setMap public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void setTextMap(@javax.annotation.Nonnull final java.util.Map<? extends AnEnum,? extends String> text)
	{
		AnItem.text.setMap(this,text);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;/**

	 **
	 * The persistent type information for anItem.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);
}}

}
