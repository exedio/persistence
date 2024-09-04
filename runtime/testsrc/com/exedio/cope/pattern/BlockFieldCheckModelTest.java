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
import static com.exedio.cope.pattern.BlockFieldCheckModelTest.ABlock.gamma;
import static com.exedio.cope.pattern.BlockFieldCheckModelTest.ABlock.greater;
import static com.exedio.cope.pattern.BlockFieldCheckModelTest.ABlock.isnull;
import static com.exedio.cope.pattern.BlockFieldCheckModelTest.ABlock.less;
import static com.exedio.cope.pattern.BlockFieldCheckModelTest.ABlock.not;
import static com.exedio.cope.pattern.BlockFieldCheckModelTest.AnItem.eins;
import static com.exedio.cope.pattern.BlockFieldCheckModelTest.AnItem.zwei;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.CheckConstraint;
import com.exedio.cope.Feature;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class BlockFieldCheckModelTest
{
	static final Model MODEL = new Model(AnItem.TYPE);

	static
	{
		MODEL.enableSerialization(BlockFieldCheckModelTest.class, "MODEL");
	}

	@Test void testIt()
	{
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				AnItem.TYPE.getThis(),
				AnItem.code,
				eins,
				eins.of(alpha), eins.of(beta), eins.of(gamma), eins.of(less), eins.of(greater),
				eins.of(compare), eins.of(composite), eins.of(isnull), eins.of(not),
				zwei,
				zwei.of(alpha), zwei.of(beta), zwei.of(gamma), zwei.of(less), zwei.of(greater),
				zwei.of(compare), zwei.of(composite), zwei.of(isnull), zwei.of(not),
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
				eins.of(alpha), eins.of(beta), eins.of(gamma), eins.of(less), eins.of(greater),
				eins.of(compare), eins.of(composite), eins.of(isnull), eins.of(not)),
			eins.getSourceFeatures());

		assertEquals(eins.of(alpha)+"<="+eins.of(beta), eins.of(less   ).getCondition().toString());
		assertEquals(eins.of(alpha)+">="+eins.of(beta), eins.of(greater).getCondition().toString());
		assertEquals(zwei.of(alpha)+"<="+zwei.of(beta), zwei.of(less   ).getCondition().toString());
		assertEquals(zwei.of(alpha)+">="+zwei.of(beta), zwei.of(greater).getCondition().toString());

		assertEquals(eins.of(alpha)+"<'200'", eins.of(compare).getCondition().toString());
		assertEquals("("+eins.of(alpha)+"<'300' and "+eins.of(beta)+"<'500')" , eins.of(composite).getCondition().toString());
		assertEquals(eins.of(alpha)+" is not null" , eins.of(isnull).getCondition().toString());
		assertEquals("!("+eins.of(gamma)+" regexp '(?s)\\AmyRegexp\\z')" , eins.of(not).getCondition().toString());

		assertEquals(ABlock.TYPE, eins.getValueType());
		assertEquals(ABlock.class, eins.getValueClass());

		assertSame(less, eins.getTemplate(eins.of(less)));
		assertEqualsUnmodifiable(list(
				alpha, beta, gamma, less, greater, compare, composite, isnull, not),
			eins.getTemplates());
		assertEqualsUnmodifiable(list(
				eins.of(alpha), eins.of(beta), eins.of(gamma), eins.of(less), eins.of(greater),
				eins.of(compare), eins.of(composite), eins.of(isnull), eins.of(not)),
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
		static final StringField gamma = new StringField().optional();
		static final CheckConstraint less    = new CheckConstraint(alpha.   lessOrEqual(beta));
		static final CheckConstraint greater = new CheckConstraint(alpha.greaterOrEqual(beta));

		static final CheckConstraint compare = new CheckConstraint(alpha.less(200));
		static final CheckConstraint composite = new CheckConstraint(alpha.less(300).and(beta.less(500)));
		static final CheckConstraint isnull = new CheckConstraint(alpha.isNotNull());
		static final CheckConstraint not = new CheckConstraint(gamma.regexpLike("myRegexp").not());


	/**
	 * Returns the value of {@link #alpha}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int getAlpha()
	{
		return field().of(ABlock.alpha).getMandatory(item());
	}

	/**
	 * Sets a new value for {@link #alpha}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setAlpha(final int alpha)
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
	{
		field().of(ABlock.beta).set(item(),beta);
	}

	/**
	 * Returns the value of {@link #gamma}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	java.lang.String getGamma()
	{
		return field().of(ABlock.gamma).get(item());
	}

	/**
	 * Sets a new value for {@link #gamma}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setGamma(@javax.annotation.Nullable final java.lang.String gamma)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		field().of(ABlock.gamma).set(item(),gamma);
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

		AnItem(
				final String code,
				final int einsAlpha, final int einsBeta,
				final int zweiAlpha, final int zweiBeta)
		{
			//noinspection UnnecessarilyQualifiedStaticUsage
			this(
				SetValue.map(AnItem.code, code),
				SetValue.map(AnItem.eins.of(alpha), einsAlpha),
				SetValue.map(AnItem.eins.of(beta ), einsBeta ),
				SetValue.map(AnItem.zwei.of(alpha), zweiAlpha),
				SetValue.map(AnItem.zwei.of(beta ), zweiBeta ));
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

