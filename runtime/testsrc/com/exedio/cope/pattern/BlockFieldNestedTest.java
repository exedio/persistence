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

import static com.exedio.cope.pattern.BlockFieldNestedTest.AnItem.duo;
import static com.exedio.cope.pattern.BlockFieldNestedTest.AnItem.uno;
import static com.exedio.cope.pattern.BlockFieldNestedTest.Inner.alpha;
import static com.exedio.cope.pattern.BlockFieldNestedTest.Inner.beta;
import static com.exedio.cope.pattern.BlockFieldNestedTest.Outer.eins;
import static com.exedio.cope.pattern.BlockFieldNestedTest.Outer.zwei;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.exedio.cope.Feature;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import java.util.Arrays;
import org.junit.Test;

public class BlockFieldNestedTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(AnItem.TYPE);

	static
	{
		MODEL.enableSerialization(BlockFieldNestedTest.class, "MODEL");
	}

	public BlockFieldNestedTest()
	{
		super(MODEL);
	}

	@Test public void testIt()
	{
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				AnItem.TYPE.getThis(),
				uno, uno.of(eins), uno.of(eins).of(alpha), uno.of(eins).of(beta), uno.of(zwei), uno.of(zwei).of(alpha), uno.of(zwei).of(beta),
				duo, duo.of(eins), duo.of(eins).of(alpha), duo.of(eins).of(beta), duo.of(zwei), duo.of(zwei).of(alpha), duo.of(zwei).of(beta)
			}), AnItem.TYPE.getDeclaredFeatures());

		assertEquals(AnItem.TYPE, uno.getType());
		assertEquals(AnItem.TYPE, uno.of(eins).getType());
		assertEquals(AnItem.TYPE, uno.of(eins).of(alpha).getType());
		assertEquals("uno", uno.getName());
		assertEquals("uno-eins", uno.of(eins).getName());
		assertEquals("uno-eins-alpha", uno.of(eins).of(alpha).getName());
		assertEquals("AnItem.uno", uno.getID());
		assertEquals("AnItem.uno-eins", uno.of(eins).getID());
		assertEquals("AnItem.uno-eins-alpha", uno.of(eins).of(alpha).getID());
		assertEquals("AnItem.uno", uno.toString());
		assertEquals("AnItem.uno-eins", uno.of(eins).toString());
		assertEquals("AnItem.uno-eins-alpha", uno.of(eins).of(alpha).toString());
		assertEquals(Outer.class.getName() + "#eins", eins.toString());
		assertEquals(Inner.class.getName() + "#alpha", alpha.toString());
		assertEquals(null, uno.getPattern());
		assertEquals(uno, uno.of(eins).getPattern());
		assertEquals(uno.of(eins), uno.of(eins).of(alpha).getPattern());
		assertEqualsUnmodifiable(list(
				uno.of(eins), uno.of(zwei)),
				uno.getSourceFeatures());
		assertEqualsUnmodifiable(list(
				uno.of(eins).of(alpha), uno.of(eins).of(beta)),
				uno.of(eins).getSourceFeatures());

		assertEquals(Inner.TYPE, eins.getValueType());
		assertEquals(Inner.class, eins.getValueClass());

		assertSame(eins,  uno .getTemplate(uno .of(eins)));
		assertSame(alpha, eins.getTemplate(eins.of(alpha)));
		assertEqualsUnmodifiable(list(uno .of(eins) , uno .of(zwei)), uno .getComponents());
		assertEqualsUnmodifiable(list(eins.of(alpha), eins.of(beta)), eins.getComponents());
		assertEqualsUnmodifiable(list(eins , zwei), uno .getTemplates());
		assertEqualsUnmodifiable(list(alpha, beta), eins.getTemplates());

		// test persistence
		final AnItem item = new AnItem();
		final Outer uno = item.uno();
		final Inner unoEins = uno.eins();
		final Inner unoZwei = uno.zwei();
		final Outer duo = item.duo();
		final Inner duoEins = duo.eins();
		final Inner duoZwei = duo.zwei();

		assertEquals(null, unoEins.getAlpha());
		assertEquals(null, unoEins.getBeta());
		assertEquals(null, unoZwei.getAlpha());
		assertEquals(null, unoZwei.getBeta());
		assertEquals(null, duoEins.getAlpha());
		assertEquals(null, duoEins.getBeta());
		assertEquals(null, duoZwei.getAlpha());
		assertEquals(null, duoZwei.getBeta());

		unoEins.setAlpha(5);
		assertEquals(Integer.valueOf(5), unoEins.getAlpha());
		assertEquals(null, unoEins.getBeta());
		assertEquals(null, unoZwei.getAlpha());
		assertEquals(null, unoZwei.getBeta());
		assertEquals(null, duoEins.getAlpha());
		assertEquals(null, duoEins.getBeta());
		assertEquals(null, duoZwei.getAlpha());
		assertEquals(null, duoZwei.getBeta());

		duoZwei.setBeta(10);
		assertEquals(Integer.valueOf(5), unoEins.getAlpha());
		assertEquals(null, unoEins.getBeta());
		assertEquals(null, unoZwei.getAlpha());
		assertEquals(null, unoZwei.getBeta());
		assertEquals(null, duoEins.getAlpha());
		assertEquals(null, duoEins.getBeta());
		assertEquals(null, duoZwei.getAlpha());
		assertEquals(Integer.valueOf(10), duoZwei.getBeta());
	}

	static final class Inner extends Block
	{
		static final IntegerField alpha = new IntegerField().optional();
		static final IntegerField beta  = new IntegerField().optional();


	/**

	 **
	 * Returns the value of {@link #alpha}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final java.lang.Integer getAlpha()
	{
		return field().of(Inner.alpha).get(item());
	}/**

	 **
	 * Sets a new value for {@link #alpha}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void setAlpha(final java.lang.Integer alpha)
	{
		field().of(Inner.alpha).set(item(),alpha);
	}/**

	 **
	 * Returns the value of {@link #beta}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final java.lang.Integer getBeta()
	{
		return field().of(Inner.beta).get(item());
	}/**

	 **
	 * Sets a new value for {@link #beta}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void setBeta(final java.lang.Integer beta)
	{
		field().of(Inner.beta).set(item(),beta);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;/**

	 **
	 * The type information for inner.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	static final com.exedio.cope.pattern.BlockType<Inner> TYPE = com.exedio.cope.pattern.BlockType.newType(Inner.class);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.pattern.Block#Block(com.exedio.cope.pattern.BlockActivationParameters)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private Inner(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);
}}

	static final class Outer extends Block
	{
		static final BlockField<Inner> eins = BlockField.create(Inner.TYPE);
		static final BlockField<Inner> zwei = BlockField.create(Inner.TYPE);


	/**

	 **
	 * Returns the value of {@link #eins}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope. public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final Inner eins()
	{
		return field().of(Outer.eins).get(item());
	}/**

	 **
	 * Returns the value of {@link #zwei}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope. public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final Inner zwei()
	{
		return field().of(Outer.zwei).get(item());
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;/**

	 **
	 * The type information for outer.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	static final com.exedio.cope.pattern.BlockType<Outer> TYPE = com.exedio.cope.pattern.BlockType.newType(Outer.class);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.pattern.Block#Block(com.exedio.cope.pattern.BlockActivationParameters)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private Outer(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);
}}

	static final class AnItem extends com.exedio.cope.Item // TODO use import, but this is not accepted by javac
	{
		static final BlockField<Outer> uno = BlockField.create(Outer.TYPE);
		static final BlockField<Outer> duo = BlockField.create(Outer.TYPE);


	/**

	 **
	 * Creates a new AnItem with all the fields initially needed.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of fields.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	AnItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
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
	 * Returns the value of {@link #uno}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope. public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final Outer uno()
	{
		return AnItem.uno.get(this);
	}/**

	 **
	 * Returns the value of {@link #duo}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope. public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final Outer duo()
	{
		return AnItem.duo.get(this);
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

