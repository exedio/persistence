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

import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.pattern.BlockFieldCheckModelTest.ABlock.alpha;
import static com.exedio.cope.pattern.BlockFieldCheckModelTest.ABlock.beta;
import static com.exedio.cope.pattern.BlockFieldCheckModelTest.ABlock.compare;
import static com.exedio.cope.pattern.BlockFieldCheckModelTest.ABlock.composite;
import static com.exedio.cope.pattern.BlockFieldCheckModelTest.ABlock.greater;
import static com.exedio.cope.pattern.BlockFieldCheckModelTest.ABlock.less;
import static com.exedio.cope.pattern.BlockFieldCheckModelTest.AnItem.eins;
import static com.exedio.cope.pattern.BlockFieldCheckModelTest.AnItem.zwei;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.exedio.cope.CheckConstraint;
import com.exedio.cope.Feature;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import java.util.Arrays;
import org.junit.Test;

public class BlockFieldCheckModelTest
{
	static final Model MODEL = new Model(AnItem.TYPE);

	static
	{
		MODEL.enableSerialization(BlockFieldCheckModelTest.class, "MODEL");
	}

	@Test public void testIt()
	{
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				AnItem.TYPE.getThis(),
				AnItem.code,
				eins,
				eins.of(alpha), eins.of(beta), eins.of(less), eins.of(greater),
				eins.of(compare), eins.of(composite),
				zwei,
				zwei.of(alpha), zwei.of(beta), zwei.of(less), zwei.of(greater),
				zwei.of(compare), zwei.of(composite),
			}), AnItem.TYPE.getDeclaredFeatures());


		assertEquals(AnItem.TYPE, eins.of(less).getType());
		assertEquals(AnItem.TYPE, eins.getType());
		assertEquals("eins-less", eins.of(less).getName());
		assertEquals("eins", eins.getName());
		assertEquals("com.exedio.cope.pattern.BlockFieldCheckModelTest$ABlock#less", less.toString());
		assertEquals("AnItem.eins-less", eins.of(less).toString());
		assertEquals("AnItem.eins", eins.toString());
		assertEquals(eins, eins.of(less).getPattern());
		assertEqualsUnmodifiable(list(
				eins.of(alpha), eins.of(beta), eins.of(less), eins.of(greater),
				eins.of(compare), eins.of(composite)),
			eins.getSourceFeatures());

		assertEquals(eins.of(alpha)+"<="+eins.of(beta), eins.of(less   ).getCondition().toString());
		assertEquals(eins.of(alpha)+">="+eins.of(beta), eins.of(greater).getCondition().toString());
		assertEquals(zwei.of(alpha)+"<="+zwei.of(beta), zwei.of(less   ).getCondition().toString());
		assertEquals(zwei.of(alpha)+">="+zwei.of(beta), zwei.of(greater).getCondition().toString());

		assertEquals(eins.of(alpha)+"<'200'", eins.of(compare).getCondition().toString());
		assertEquals("("+eins.of(alpha)+"<'300' AND "+eins.of(beta)+"<'500')" , eins.of(composite).getCondition().toString());

		assertEquals(ABlock.TYPE, eins.getValueType());
		assertEquals(ABlock.class, eins.getValueClass());

		assertSame(less, eins.getTemplate(eins.of(less)));
		assertEqualsUnmodifiable(list(
				alpha, beta, less, greater, compare, composite),
			eins.getTemplates());
		assertEqualsUnmodifiable(list(
				eins.of(alpha), eins.of(beta), eins.of(less), eins.of(greater),
				eins.of(compare), eins.of(composite)),
			eins.getComponents());

		assertSerializedSame(alpha, 334);
		assertSerializedSame(less, 333);
		assertSerializedSame(eins.of(alpha), 390);
		assertSerializedSame(eins.of(less), 389);
		assertSerializedSame(zwei.of(alpha), 390);
		assertSerializedSame(zwei.of(less), 389);
		assertSerializedSame(eins, 384);
		assertSerializedSame(zwei, 384);
	}

	static final class ABlock extends Block
	{
		static final IntegerField alpha = new IntegerField();
		static final IntegerField beta = new IntegerField();
		static final CheckConstraint less    = new CheckConstraint(alpha.   lessOrEqual(beta));
		static final CheckConstraint greater = new CheckConstraint(alpha.greaterOrEqual(beta));

		static final CheckConstraint compare = new CheckConstraint(alpha.less(200));
		static final CheckConstraint composite = new CheckConstraint(alpha.less(300).and(beta.less(500)));


	/**

	 **
	 * Returns the value of {@link #alpha}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final int getAlpha()
	{
		return field().of(ABlock.alpha).getMandatory(item());
	}/**

	 **
	 * Sets a new value for {@link #alpha}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void setAlpha(final int alpha)
	{
		field().of(ABlock.alpha).set(item(),alpha);
	}/**

	 **
	 * Returns the value of {@link #beta}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final int getBeta()
	{
		return field().of(ABlock.beta).getMandatory(item());
	}/**

	 **
	 * Sets a new value for {@link #beta}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final void setBeta(final int beta)
	{
		field().of(ABlock.beta).set(item(),beta);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;/**

	 **
	 * The type information for aBlock.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	static final com.exedio.cope.pattern.BlockType<ABlock> TYPE = com.exedio.cope.pattern.BlockType.newType(ABlock.class);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.pattern.Block#Block(com.exedio.cope.pattern.BlockActivationParameters)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private ABlock(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);
}}

	static final class AnItem extends com.exedio.cope.Item // TODO use import, but this is not accepted by javac
	{
		static final StringField code = new StringField().toFinal();

		static final BlockField<ABlock> eins = BlockField.create(ABlock.TYPE);
		static final BlockField<ABlock> zwei = BlockField.create(ABlock.TYPE);

		AnItem(
				final String code,
				final int einsAlpha, final int einsBeta,
				final int zweiAlpha, final int zweiBeta)
		{
			this(
				AnItem.code.map(code),
				AnItem.eins.of(alpha).map(einsAlpha),
				AnItem.eins.of(beta ).map(einsBeta ),
				AnItem.zwei.of(alpha).map(zweiAlpha),
				AnItem.zwei.of(beta ).map(zweiBeta ));
		}


	/**

	 **
	 * Creates a new AnItem with all the fields initially needed.
	 * @param code the initial value for field {@link #code}.
	 * @throws com.exedio.cope.MandatoryViolationException if code is null.
	 * @throws com.exedio.cope.StringLengthViolationException if code violates its length constraint.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of fields.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	AnItem(
				final java.lang.String code)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			AnItem.code.map(code),
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
	 * Returns the value of {@link #code}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final java.lang.String getCode()
	{
		return AnItem.code.get(this);
	}/**

	 **
	 * Returns the value of {@link #eins}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope. public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final ABlock eins()
	{
		return AnItem.eins.get(this);
	}/**

	 **
	 * Returns the value of {@link #zwei}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope. public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final ABlock zwei()
	{
		return AnItem.zwei.get(this);
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

