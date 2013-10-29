/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.AbstractRuntimeTest.assertSerializedSame;
import static com.exedio.cope.pattern.BatzenFieldUniqueModelTest.ABatzen.alpha;
import static com.exedio.cope.pattern.BatzenFieldUniqueModelTest.ABatzen.alphaPrice;
import static com.exedio.cope.pattern.BatzenFieldUniqueModelTest.ABatzen.beta;
import static com.exedio.cope.pattern.BatzenFieldUniqueModelTest.ABatzen.betaPrice;
import static com.exedio.cope.pattern.BatzenFieldUniqueModelTest.ABatzen.constraint;
import static com.exedio.cope.pattern.BatzenFieldUniqueModelTest.ABatzen.constraintPrice;
import static com.exedio.cope.pattern.BatzenFieldUniqueModelTest.AnItem.eins;
import static com.exedio.cope.pattern.BatzenFieldUniqueModelTest.AnItem.zwei;

import com.exedio.cope.Feature;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.junit.CopeAssert;
import java.util.Arrays;

public class BatzenFieldUniqueModelTest extends CopeAssert
{
	static final Model MODEL = new Model(AnItem.TYPE);

	static
	{
		MODEL.enableSerialization(BatzenFieldUniqueModelTest.class, "MODEL");
	}

	public void testIt()
	{
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				AnItem.TYPE.getThis(),
				AnItem.code,
				eins,
				eins.of(alpha), eins.of(beta), eins.of(constraint),
				eins.of(alphaPrice), eins.of(alphaPrice).getInt(), eins.of(betaPrice), eins.of(betaPrice).getInt(), eins.of(constraintPrice),
				zwei,
				zwei.of(alpha), zwei.of(beta), zwei.of(constraint),
				zwei.of(alphaPrice), zwei.of(alphaPrice).getInt(), zwei.of(betaPrice), zwei.of(betaPrice).getInt(), zwei.of(constraintPrice),
			}), AnItem.TYPE.getDeclaredFeatures());


		assertEquals(AnItem.TYPE, eins.of(constraint).getType());
		assertEquals(AnItem.TYPE, eins.getType());
		assertEquals("eins-constraint", eins.of(constraint).getName());
		assertEquals("eins", eins.getName());
		assertEquals("com.exedio.cope.pattern.BatzenFieldUniqueModelTest$ABatzen#constraint", constraint.toString());
		assertEquals("AnItem.eins-constraint", eins.of(constraint).toString());
		assertEquals("AnItem.eins", eins.toString());
		assertEquals(eins, eins.of(constraint).getPattern());
		assertEqualsUnmodifiable(list(
				eins.of(alpha), eins.of(beta), eins.of(constraint),
				eins.of(alphaPrice), eins.of(betaPrice), eins.of(constraintPrice)),
			eins.getSourceFeatures());

		assertEquals(list(eins.of(alpha), eins.of(beta)), eins.of(constraint).getFields());
		assertEquals(list(zwei.of(alpha), zwei.of(beta)), zwei.of(constraint).getFields());
		assertEquals(list(eins.of(alphaPrice).getInt(), eins.of(betaPrice).getInt()), eins.of(constraintPrice).getFields());
		assertEquals(list(zwei.of(alphaPrice).getInt(), zwei.of(betaPrice).getInt()), zwei.of(constraintPrice).getFields());
		assertEquals(list(alpha, beta), constraint.getFields());
		assertEquals(list(alphaPrice.getInt(), betaPrice.getInt()), constraintPrice.getFields());

		assertEquals(ABatzen.TYPE, eins.getValueType());
		assertEquals(ABatzen.class, eins.getValueClass());

		assertSame(constraint, eins.getTemplate(eins.of(constraint)));
		assertEqualsUnmodifiable(list(
				alpha, beta, constraint,
				alphaPrice, betaPrice, constraintPrice),
			eins.getTemplates());
		assertEqualsUnmodifiable(list(
				eins.of(alpha), eins.of(beta), eins.of(constraint),
				eins.of(alphaPrice), eins.of(betaPrice), eins.of(constraintPrice)),
			eins.getComponents());

		assertSerializedSame(alpha, 339);
		assertSerializedSame(constraint, 344);
		assertSerializedSame(eins.of(alpha), 392);
		assertSerializedSame(eins.of(constraint), 397);
		assertSerializedSame(zwei.of(alpha), 392);
		assertSerializedSame(zwei.of(constraint), 397);
		assertSerializedSame(eins, 386);
		assertSerializedSame(zwei, 386);
	}

	static final class ABatzen extends Batzen
	{
		static final StringField alpha = new StringField();
		static final IntegerField beta = new IntegerField();
		static final UniqueConstraint constraint = new UniqueConstraint(alpha, beta);

		static final PriceField alphaPrice = new PriceField();
		static final PriceField betaPrice  = new PriceField();
		static final UniqueConstraint constraintPrice = new UniqueConstraint(alphaPrice.getInt(), betaPrice.getInt());


	/**

	 **
	 * Returns the value of {@link #alpha}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final java.lang.String getAlpha()
	{
		return field().of(ABatzen.alpha).get(item());
	}/**

	 **
	 * Sets a new value for {@link #alpha}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final void setAlpha(final java.lang.String alpha)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.UniqueViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		field().of(ABatzen.alpha).set(item(),alpha);
	}/**

	 **
	 * Returns the value of {@link #beta}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final int getBeta()
	{
		return field().of(ABatzen.beta).getMandatory(item());
	}/**

	 **
	 * Sets a new value for {@link #beta}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final void setBeta(final int beta)
			throws
				com.exedio.cope.UniqueViolationException
	{
		field().of(ABatzen.beta).set(item(),beta);
	}/**

	 **
	 * Returns the value of {@link #alphaPrice}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final com.exedio.cope.pattern.Price getAlphaPrice()
	{
		return field().of(ABatzen.alphaPrice).get(item());
	}/**

	 **
	 * Sets a new value for {@link #alphaPrice}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final void setAlphaPrice(final com.exedio.cope.pattern.Price alphaPrice)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.UniqueViolationException
	{
		field().of(ABatzen.alphaPrice).set(item(),alphaPrice);
	}/**

	 **
	 * Returns the value of {@link #betaPrice}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final com.exedio.cope.pattern.Price getBetaPrice()
	{
		return field().of(ABatzen.betaPrice).get(item());
	}/**

	 **
	 * Sets a new value for {@link #betaPrice}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final void setBetaPrice(final com.exedio.cope.pattern.Price betaPrice)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.UniqueViolationException
	{
		field().of(ABatzen.betaPrice).set(item(),betaPrice);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	private static final long serialVersionUID = 1l;/**

	 **
	 * The type information for aBatzen.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	static final com.exedio.cope.pattern.BatzenType<ABatzen> TYPE = com.exedio.cope.pattern.BatzenType.newType(ABatzen.class);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.pattern.Batzen#Batzen(com.exedio.cope.pattern.BatzenActivationParameters)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@SuppressWarnings("unused") private ABatzen(final com.exedio.cope.pattern.BatzenActivationParameters ap){super(ap);
}}

	static final class AnItem extends com.exedio.cope.Item
	{
		static final StringField code = new StringField().toFinal();

		static final BatzenField<ABatzen> eins = BatzenField.create(ABatzen.TYPE);
		static final BatzenField<ABatzen> zwei = BatzenField.create(ABatzen.TYPE);

		AnItem(final String code, final int n)
		{
			this(
				AnItem.code.map(code),
				AnItem.eins.of(alpha).map(code + '-' + n + 'A'),
				AnItem.eins.of(beta).map(n),
				AnItem.zwei.of(alpha).map(code + '-' + n + 'B'),
				AnItem.zwei.of(beta).map(n + 10),
				AnItem.eins.of(alphaPrice).map(Price.storeOf(150+n)),
				AnItem.eins.of( betaPrice).map(Price.storeOf(160+n)),
				AnItem.zwei.of(alphaPrice).map(Price.storeOf(250+n)),
				AnItem.zwei.of( betaPrice).map(Price.storeOf(260+n)));
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
	private AnItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}/**

	 **
	 * Returns the value of {@link #code}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final java.lang.String getCode()
	{
		return AnItem.code.get(this);
	}/**

	 **
	 * Returns the value of {@link #eins}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope. public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final ABatzen eins()
	{
		return AnItem.eins.get(this);
	}/**

	 **
	 * Returns the value of {@link #zwei}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope. public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	final ABatzen zwei()
	{
		return AnItem.zwei.get(this);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	private static final long serialVersionUID = 1l;/**

	 **
	 * The persistent type information for anItem.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@SuppressWarnings("unused") private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);
}}
}

