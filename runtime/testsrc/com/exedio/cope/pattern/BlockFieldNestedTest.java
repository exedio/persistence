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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.Feature;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

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

	@Test void testIt()
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
	 * Returns the value of {@link #alpha}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Integer getAlpha()
	{
		return field().of(Inner.alpha).get(item());
	}

	/**
	 * Sets a new value for {@link #alpha}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setAlpha(@javax.annotation.Nullable final java.lang.Integer alpha)
	{
		field().of(Inner.alpha).set(item(),alpha);
	}

	/**
	 * Returns the value of {@link #beta}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.Integer getBeta()
	{
		return field().of(Inner.beta).get(item());
	}

	/**
	 * Sets a new value for {@link #beta}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setBeta(@javax.annotation.Nullable final java.lang.Integer beta)
	{
		field().of(Inner.beta).set(item(),beta);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The type information for inner.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.pattern.BlockType<Inner> TYPE = com.exedio.cope.pattern.BlockType.newType(Inner.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.pattern.Block#Block(com.exedio.cope.pattern.BlockActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private Inner(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
}

	static final class Outer extends Block
	{
		static final BlockField<Inner> eins = BlockField.create(Inner.TYPE);
		static final BlockField<Inner> zwei = BlockField.create(Inner.TYPE);


	/**
	 * Returns the value of {@link #eins}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	Inner eins()
	{
		return field().of(Outer.eins).get(item());
	}

	/**
	 * Returns the value of {@link #zwei}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	Inner zwei()
	{
		return field().of(Outer.zwei).get(item());
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The type information for outer.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.pattern.BlockType<Outer> TYPE = com.exedio.cope.pattern.BlockType.newType(Outer.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.pattern.Block#Block(com.exedio.cope.pattern.BlockActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private Outer(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
}

	static final class AnItem extends com.exedio.cope.Item // TODO use import, but this is not accepted by javac
	{
		static final BlockField<Outer> uno = BlockField.create(Outer.TYPE);
		static final BlockField<Outer> duo = BlockField.create(Outer.TYPE);


	/**
	 * Creates a new AnItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	AnItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new AnItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #uno}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	Outer uno()
	{
		return AnItem.uno.get(this);
	}

	/**
	 * Returns the value of {@link #duo}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	Outer duo()
	{
		return AnItem.duo.get(this);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for anItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
}

