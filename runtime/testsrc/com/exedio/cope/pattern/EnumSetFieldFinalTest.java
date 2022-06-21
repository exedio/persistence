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

import static com.exedio.cope.pattern.EnumSetFieldFinalTest.AnEnum.DE;
import static com.exedio.cope.pattern.EnumSetFieldFinalTest.AnEnum.EN;
import static com.exedio.cope.pattern.EnumSetFieldFinalTest.AnEnum.PL;
import static com.exedio.cope.pattern.EnumSetFieldFinalTest.AnItem.TYPE;
import static com.exedio.cope.pattern.EnumSetFieldFinalTest.AnItem.field;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.BooleanField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.SetValue;
import com.exedio.cope.instrument.WrapperInitial;
import org.junit.jupiter.api.Test;

public class EnumSetFieldFinalTest
{
	private static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(EnumSetFieldFinalTest.class, "MODEL");
	}

	@Test void testIt()
	{
		assertEquals(TYPE, field.getType());
		assertEquals("field", field.getName());

		assertEquals(AnEnum.class, field.getElementClass());
		assertEquals(true, field.isFinal());
		assertEquals(true, field.getField(DE).isFinal());
		assertEquals(true, field.getField(EN).isFinal());
		assertEquals(true, field.getField(PL).isFinal());

		assertEquals(BooleanField.class, field.getField(DE).getClass());
		assertEquals("field-DE", field.getField(DE).getName());
		assertSame(TYPE, field.getField(DE).getType());
		assertEquals(field, field.getField(DE).getPattern());
		assertEqualsUnmodifiable(list(field.getField(DE), field.getField(EN), field.getField(PL)), field.getSourceFeatures());

		assertEqualsUnmodifiable(
				list(
						TYPE.getThis(),
						field,
						field.getField(DE), field.getField(EN), field.getField(PL)),
				TYPE.getFeatures());
		assertEqualsUnmodifiable(
				list(
						field.getField(DE), field.getField(EN), field.getField(PL)),
				TYPE.getFields());
	}

	enum AnEnum
	{
		DE, EN, PL
	}

	static final class AnItem extends Item
	{
		@WrapperInitial
		static final EnumSetField<AnEnum> field = EnumSetField.create(AnEnum.class).toFinal();

		AnItem()
		{
			this(SetValue.EMPTY_ARRAY);
		}

	/**
	 * Creates a new AnItem with all the fields initially needed.
	 * @param field the initial value for field {@link #field}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	AnItem(
				@javax.annotation.Nonnull final java.util.EnumSet<AnEnum> field)
	{
		this(new com.exedio.cope.SetValue<?>[]{
			AnItem.field.map(field),
		});
	}

	/**
	 * Creates a new AnItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="contains")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	boolean containsField(@javax.annotation.Nonnull final AnEnum element)
	{
		return AnItem.field.contains(this,element);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	java.util.EnumSet<AnEnum> getField()
	{
		return AnItem.field.get(this);
	}

	@com.exedio.cope.instrument.Generated
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
