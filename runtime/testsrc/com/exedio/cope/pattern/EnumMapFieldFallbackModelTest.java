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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.Test;

public class EnumMapFieldFallbackModelTest
{
	static final Model MODEL = new Model(AnItem.TYPE);

	@Test public void testIsMandatory()
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

	@Test public void testHasFallback()
	{
		assertEquals(false, noneMand.hasFallbacks());
		assertEquals(false, noneOpt .hasFallbacks());
		assertEquals(true,  fallMand.hasFallbacks());
		assertEquals(true,  fallOpt .hasFallbacks());
	}

	@Test public void testGetFallback()
	{
		assertEquals(null, noneMand.getFallback());
		assertEquals(null, noneOpt .getFallback());
		assertEquals(fall, fallMand.getFallback());
		assertEquals(fall, fallOpt .getFallback());
	}

	@Test public void testGetFunctionWithFallback()
	{
		assertEquals("coalesce(AnItem.fallMand-one,AnItem.fallMand-fall)", fallMand.getFunctionWithFallback(one ).toString());
		assertEquals("coalesce(AnItem.fallMand-two,AnItem.fallMand-fall)", fallMand.getFunctionWithFallback(two ).toString());
		assertEquals(                             "AnItem.fallMand-fall" , fallMand.getFunctionWithFallback(fall).toString());
	}

	@Test public void testGetFunctionWithFallbackWithoutFallback()
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

	@Test public void testGetFunctionWithFallbackNullKey()
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
	@Test public void testFallbackToNull()
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


	static enum AnEnum
	{
		one, two, @CopeEnumFallback fall;
	}

	static final class AnItem extends com.exedio.cope.Item // TODO use import, but this is not accepted by javac
	{
		static final EnumMapField<AnEnum, String> noneMand = EnumMapField.create(AnEnum.class, new StringField());
		static final EnumMapField<AnEnum, String> noneOpt  = EnumMapField.create(AnEnum.class, new StringField().optional());

		static final EnumMapField<AnEnum, String> fallMand = EnumMapField.create(AnEnum.class, new StringField()).fallback();
		static final EnumMapField<AnEnum, String> fallOpt  = EnumMapField.create(AnEnum.class, new StringField().optional()).fallback();

	/**

	 **
	 * Creates a new AnItem with all the fields initially needed.
	 * @param noneMand the initial value for field {@link #noneMand}.
	 * @param fallMand the initial value for field {@link #fallMand}.
	 * @throws com.exedio.cope.MandatoryViolationException if noneMand, fallMand is null.
	 * @throws com.exedio.cope.StringLengthViolationException if noneMand, fallMand violates its length constraint.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of fields.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
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
	 * Returns the value mapped to <tt>k</tt> by the field map {@link #noneMand}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nonnull()
	final String getNoneMand(@javax.annotation.Nonnull final AnEnum k)
	{
		return AnItem.noneMand.get(this,k);
	}/**

	 **
	 * Associates <tt>k</tt> to a new value in the field map {@link #noneMand}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void setNoneMand(@javax.annotation.Nonnull final AnEnum k,@javax.annotation.Nonnull final String noneMand)
	{
		AnItem.noneMand.set(this,k,noneMand);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getMap public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nonnull()
	final java.util.Map<AnEnum,String> getNoneMandMap()
	{
		return AnItem.noneMand.getMap(this);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.setMap public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void setNoneMandMap(@javax.annotation.Nonnull final java.util.Map<? extends AnEnum,? extends String> noneMand)
	{
		AnItem.noneMand.setMap(this,noneMand);
	}/**

	 **
	 * Returns the value mapped to <tt>k</tt> by the field map {@link #noneOpt}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	final String getNoneOpt(@javax.annotation.Nonnull final AnEnum k)
	{
		return AnItem.noneOpt.get(this,k);
	}/**

	 **
	 * Associates <tt>k</tt> to a new value in the field map {@link #noneOpt}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void setNoneOpt(@javax.annotation.Nonnull final AnEnum k,@javax.annotation.Nullable final String noneOpt)
	{
		AnItem.noneOpt.set(this,k,noneOpt);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getMap public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nonnull()
	final java.util.Map<AnEnum,String> getNoneOptMap()
	{
		return AnItem.noneOpt.getMap(this);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.setMap public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void setNoneOptMap(@javax.annotation.Nonnull final java.util.Map<? extends AnEnum,? extends String> noneOpt)
	{
		AnItem.noneOpt.setMap(this,noneOpt);
	}/**

	 **
	 * Returns the value mapped to <tt>k</tt> by the field map {@link #fallMand}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nonnull()
	final String getFallMand(@javax.annotation.Nonnull final AnEnum k)
	{
		return AnItem.fallMand.get(this,k);
	}/**

	 **
	 * Returns the value mapped to <tt>k</tt> by the field map {@link #fallMand}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getWithFallback public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nonnull()
	final String getFallMandWithFallback(@javax.annotation.Nonnull final AnEnum k)
	{
		return AnItem.fallMand.getWithFallback(this,k);
	}/**

	 **
	 * Associates <tt>k</tt> to a new value in the field map {@link #fallMand}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void setFallMand(@javax.annotation.Nonnull final AnEnum k,@javax.annotation.Nonnull final String fallMand)
	{
		AnItem.fallMand.set(this,k,fallMand);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getMap public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nonnull()
	final java.util.Map<AnEnum,String> getFallMandMap()
	{
		return AnItem.fallMand.getMap(this);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getMapWithFallback public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nonnull()
	final java.util.Map<AnEnum,String> getFallMandMapWithFallback()
	{
		return AnItem.fallMand.getMapWithFallback(this);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.setMap public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void setFallMandMap(@javax.annotation.Nonnull final java.util.Map<? extends AnEnum,? extends String> fallMand)
	{
		AnItem.fallMand.setMap(this,fallMand);
	}/**

	 **
	 * Returns the value mapped to <tt>k</tt> by the field map {@link #fallOpt}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	final String getFallOpt(@javax.annotation.Nonnull final AnEnum k)
	{
		return AnItem.fallOpt.get(this,k);
	}/**

	 **
	 * Returns the value mapped to <tt>k</tt> by the field map {@link #fallOpt}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getWithFallback public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	final String getFallOptWithFallback(@javax.annotation.Nonnull final AnEnum k)
	{
		return AnItem.fallOpt.getWithFallback(this,k);
	}/**

	 **
	 * Associates <tt>k</tt> to a new value in the field map {@link #fallOpt}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void setFallOpt(@javax.annotation.Nonnull final AnEnum k,@javax.annotation.Nullable final String fallOpt)
	{
		AnItem.fallOpt.set(this,k,fallOpt);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getMap public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nonnull()
	final java.util.Map<AnEnum,String> getFallOptMap()
	{
		return AnItem.fallOpt.getMap(this);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.getMapWithFallback public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nonnull()
	final java.util.Map<AnEnum,String> getFallOptMapWithFallback()
	{
		return AnItem.fallOpt.getMapWithFallback(this);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.setMap public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void setFallOptMap(@javax.annotation.Nonnull final java.util.Map<? extends AnEnum,? extends String> fallOpt)
	{
		AnItem.fallOpt.setMap(this,fallOpt);
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