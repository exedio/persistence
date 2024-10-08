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
import static com.exedio.cope.pattern.BlockFieldUniqueMultiModelTest.ABlock.alpha;
import static com.exedio.cope.pattern.BlockFieldUniqueMultiModelTest.ABlock.alphaPrice;
import static com.exedio.cope.pattern.BlockFieldUniqueMultiModelTest.ABlock.beta;
import static com.exedio.cope.pattern.BlockFieldUniqueMultiModelTest.ABlock.betaPrice;
import static com.exedio.cope.pattern.BlockFieldUniqueMultiModelTest.ABlock.constraint;
import static com.exedio.cope.pattern.BlockFieldUniqueMultiModelTest.ABlock.constraintPrice;
import static com.exedio.cope.pattern.BlockFieldUniqueMultiModelTest.AnItem.eins;
import static com.exedio.cope.pattern.BlockFieldUniqueMultiModelTest.AnItem.zwei;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.Feature;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.UniqueConstraint;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class BlockFieldUniqueMultiModelTest
{
	static final Model MODEL = new Model(AnItem.TYPE);

	static
	{
		MODEL.enableSerialization(BlockFieldUniqueMultiModelTest.class, "MODEL");
	}

	@Test void testIt()
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
		assertEquals("com.exedio.cope.pattern.BlockFieldUniqueMultiModelTest$ABlock#constraint", constraint.toString());
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

		assertEquals(ABlock.TYPE, eins.getValueType());
		assertEquals(ABlock.class, eins.getValueClass());

		assertSame(constraint, eins.getTemplate(eins.of(constraint)));
		assertEqualsUnmodifiable(list(
				alpha, beta, constraint,
				alphaPrice, betaPrice, constraintPrice),
			eins.getTemplates());
		assertEqualsUnmodifiable(list(
				eins.of(alpha), eins.of(beta), eins.of(constraint),
				eins.of(alphaPrice), eins.of(betaPrice), eins.of(constraintPrice)),
			eins.getComponents());

		assertSerializedSame(alpha, 340);
		assertSerializedSame(constraint, 345);
		assertSerializedSame(eins.of(alpha), 396);
		assertSerializedSame(eins.of(constraint), 401);
		assertSerializedSame(zwei.of(alpha), 396);
		assertSerializedSame(zwei.of(constraint), 401);
		assertSerializedSame(eins, 390);
		assertSerializedSame(zwei, 390);
	}

	static final class ABlock extends Block
	{
		static final StringField alpha = new StringField();
		static final IntegerField beta = new IntegerField();
		static final UniqueConstraint constraint = UniqueConstraint.create(alpha, beta);

		static final PriceField alphaPrice = new PriceField();
		static final PriceField betaPrice  = new PriceField();
		static final UniqueConstraint constraintPrice = UniqueConstraint.create(alphaPrice.getInt(), betaPrice.getInt());


	/**
	 * Returns the value of {@link #alpha}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getAlpha()
	{
		return field().of(ABlock.alpha).get(item());
	}

	/**
	 * Sets a new value for {@link #alpha}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setAlpha(@javax.annotation.Nonnull final java.lang.String alpha)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.UniqueViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		field().of(ABlock.alpha).set(item(),alpha);
	}

	/**
	 * Returns the value of {@link #beta}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int getBeta()
	{
		return field().of(ABlock.beta).getMandatory(item());
	}

	/**
	 * Sets a new value for {@link #beta}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setBeta(final int beta)
			throws
				com.exedio.cope.UniqueViolationException
	{
		field().of(ABlock.beta).set(item(),beta);
	}

	/**
	 * Returns the value of {@link #alphaPrice}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.Price getAlphaPrice()
	{
		return field().of(ABlock.alphaPrice).get(item());
	}

	/**
	 * Sets a new value for {@link #alphaPrice}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setAlphaPrice(@javax.annotation.Nonnull final com.exedio.cope.pattern.Price alphaPrice)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.UniqueViolationException
	{
		field().of(ABlock.alphaPrice).set(item(),alphaPrice);
	}

	/**
	 * Returns the value of {@link #betaPrice}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	com.exedio.cope.pattern.Price getBetaPrice()
	{
		return field().of(ABlock.betaPrice).get(item());
	}

	/**
	 * Sets a new value for {@link #betaPrice}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setBetaPrice(@javax.annotation.Nonnull final com.exedio.cope.pattern.Price betaPrice)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.UniqueViolationException
	{
		field().of(ABlock.betaPrice).set(item(),betaPrice);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The type information for aBlock.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.pattern.BlockType<ABlock> TYPE = com.exedio.cope.pattern.BlockType.newType(ABlock.class,ABlock::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.pattern.Block#Block(com.exedio.cope.pattern.BlockActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private ABlock(final com.exedio.cope.pattern.BlockActivationParameters ap){super(ap);}
}

	static final class AnItem extends Item
	{
		static final StringField code = new StringField().toFinal();

		static final BlockField<ABlock> eins = BlockField.create(ABlock.TYPE);
		static final BlockField<ABlock> zwei = BlockField.create(ABlock.TYPE);

		AnItem(final String code, final int n)
		{
			//noinspection UnnecessarilyQualifiedStaticUsage
			this(
				SetValue.map(AnItem.code, code),
				SetValue.map(AnItem.eins.of(alpha), code + '-' + n + 'A'),
				SetValue.map(AnItem.eins.of(beta), n),
				SetValue.map(AnItem.zwei.of(alpha), code + '-' + n + 'B'),
				SetValue.map(AnItem.zwei.of(beta), n + 10),
				SetValue.map(AnItem.eins.of(alphaPrice), Price.storeOf(150+n)),
				SetValue.map(AnItem.eins.of( betaPrice), Price.storeOf(160+n)),
				SetValue.map(AnItem.zwei.of(alphaPrice), Price.storeOf(250+n)),
				SetValue.map(AnItem.zwei.of( betaPrice), Price.storeOf(260+n)));
		}


	/**
	 * Creates a new AnItem with all the fields initially needed.
	 * @param code the initial value for field {@link #code}.
	 * @throws com.exedio.cope.MandatoryViolationException if code is null.
	 * @throws com.exedio.cope.StringLengthViolationException if code violates its length constraint.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	AnItem(
				@javax.annotation.Nonnull final java.lang.String code)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(AnItem.code,code),
		});
	}

	/**
	 * Creates a new AnItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #code}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.lang.String getCode()
	{
		return AnItem.code.get(this);
	}

	/**
	 * Returns the value of {@link #eins}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	ABlock eins()
	{
		return AnItem.eins.get(this);
	}

	/**
	 * Returns the value of {@link #zwei}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	ABlock zwei()
	{
		return AnItem.zwei.get(this);
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

