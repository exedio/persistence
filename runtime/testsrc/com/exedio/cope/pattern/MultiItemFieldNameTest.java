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

import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.CopeName;
import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Model;
import com.exedio.cope.TestAnnotation;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.misc.Computed;
import org.junit.jupiter.api.Test;

public class MultiItemFieldNameTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(MyItem.TYPE, Alpha.TYPE, Beta.TYPE, Gamma.TYPE, Delta.TYPE);

	public MultiItemFieldNameTest()
	{
		super(MODEL);
	}

	@Test void test()
	{
		final ItemField<Alpha> alpha = MyItem.field.of(Alpha.class);
		final ItemField<Beta > beta  = MyItem.field.of(Beta .class);
		final ItemField<Gamma> gamma = MyItem.field.of(Gamma.class);
		final ItemField<Delta> delta = MyItem.field.of(Delta.class);
		assertEquals("field-Alpha"    , alpha.getName());
		assertEquals("field-BetaName" , beta .getName());
		assertEquals("field-Gamma"    , gamma.getName());
		assertEquals("field-DeltaName", delta.getName());
		assertEquals("MyItem.field-Alpha"    , alpha.getID());
		assertEquals("MyItem.field-BetaName" , beta .getID());
		assertEquals("MyItem.field-Gamma"    , gamma.getID());
		assertEquals("MyItem.field-DeltaName", delta.getID());
		assertEquals("field_Alpha"      , getColumnName(alpha));
		assertEquals("field_BetaName"   , getColumnName(beta ));
		assertEquals("field_GammaSchema", getColumnName(gamma));
		assertEquals("field_DeltaSchema", getColumnName(delta));

		assertTrue (alpha.isAnnotationPresent(Computed.class));
		assertTrue (beta .isAnnotationPresent(Computed.class));
		assertTrue (gamma.isAnnotationPresent(Computed.class));
		assertTrue (delta.isAnnotationPresent(Computed.class));
		assertFalse(alpha.isAnnotationPresent(TestAnnotation.class));
		assertFalse(beta .isAnnotationPresent(TestAnnotation.class));
		assertFalse(gamma.isAnnotationPresent(TestAnnotation.class));
		assertFalse(delta.isAnnotationPresent(TestAnnotation.class));
		assertEquals(null, alpha.getAnnotation(TestAnnotation.class));
		assertEquals(null, beta .getAnnotation(TestAnnotation.class));
		assertEquals(null, gamma.getAnnotation(TestAnnotation.class));
		assertEquals(null, delta.getAnnotation(TestAnnotation.class));
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class MyItem extends Item
	{
		@Computed
		@TestAnnotation("atField")
		@WrapperIgnore
		static final MultiItemField<MultiItemFieldValuex> field =
				MultiItemField.create(MultiItemFieldValuex.class).
				canBe(Alpha.class).canBe(Beta.class).canBe(Gamma.class).canBe(Delta.class);

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class Alpha extends Item implements MultiItemFieldValuex
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Alpha> TYPE = com.exedio.cope.TypesBound.newType(Alpha.class);

		@com.exedio.cope.instrument.Generated
		private Alpha(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@CopeName("BetaName")
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class Beta extends Item implements MultiItemFieldValuex
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Beta> TYPE = com.exedio.cope.TypesBound.newType(Beta.class);

		@com.exedio.cope.instrument.Generated
		private Beta(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@CopeSchemaName("GammaSchema")
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class Gamma extends Item implements MultiItemFieldValuex
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Gamma> TYPE = com.exedio.cope.TypesBound.newType(Gamma.class);

		@com.exedio.cope.instrument.Generated
		private Gamma(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@CopeName("DeltaName")
	@CopeSchemaName("DeltaSchema")
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class Delta extends Item implements MultiItemFieldValuex
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Delta> TYPE = com.exedio.cope.TypesBound.newType(Delta.class);

		@com.exedio.cope.instrument.Generated
		private Delta(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
