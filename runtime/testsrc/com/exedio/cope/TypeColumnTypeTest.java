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

package com.exedio.cope;

import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.getTypeColumnName;
import static com.exedio.cope.SchemaInfo.getTypeColumnValue;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.dsmf.Dialect.NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.TestSources;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TypeColumnTypeTest
{
	@Test void testIt()
	{
		assertEquals("StandardSub", getTypeColumnValue(StandardSub.TYPE));
		assertEquals("MinLengthSub", getTypeColumnValue(MinLengthSub.TYPE));
		assertEquals("MinLengthIneffectiveSub", getTypeColumnValue(MinLengthIneffectiveSub.TYPE));

		assertIt("VARCHAR(13)", StandardSuper.TYPE);
		assertIt("VARCHAR(13)", Ref.standard);
		assertIt("VARCHAR(20)", MinLengthSuper.TYPE);
		assertIt("VARCHAR(20)", Ref.minLength);
		assertIt("VARCHAR(15)", MinLengthSub.TYPE);
		assertIt("VARCHAR(15)", Ref.minLengthSub);
		assertIt("VARCHAR(25)", MinLengthIneffectiveSuper.TYPE);
		assertIt("VARCHAR(25)", Ref.minLengthIneffective);
	}

	private static void assertIt(final String expected, final Type<?> type)
	{
		assertEquals(
				expected + NOT_NULL,
				model.getSchema().
					getTable(getTableName(type)).
					getColumn(getTypeColumnName(type)).
					getType());
	}

	private static void assertIt(final String expected, final ItemField<?> field)
	{
		assertEquals(
				expected + NOT_NULL,
				model.getSchema().
					getTable(getTableName(field.getType())).
					getColumn(getTypeColumnName(field)).
					getType());
	}


	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class StandardSuper extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<StandardSuper> TYPE = com.exedio.cope.TypesBound.newType(StandardSuper.class);

		@com.exedio.cope.instrument.Generated
		protected StandardSuper(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class StandardSub extends StandardSuper
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<StandardSub> TYPE = com.exedio.cope.TypesBound.newType(StandardSub.class);

		@com.exedio.cope.instrument.Generated
		private StandardSub(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@CopeTypeColumnMinLength(20)
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class MinLengthSuper extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MinLengthSuper> TYPE = com.exedio.cope.TypesBound.newType(MinLengthSuper.class);

		@com.exedio.cope.instrument.Generated
		protected MinLengthSuper(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class MinLengthSub extends MinLengthSuper
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MinLengthSub> TYPE = com.exedio.cope.TypesBound.newType(MinLengthSub.class);

		@com.exedio.cope.instrument.Generated
		protected MinLengthSub(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class MinLengthSubber extends MinLengthSub
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MinLengthSubber> TYPE = com.exedio.cope.TypesBound.newType(MinLengthSubber.class);

		@com.exedio.cope.instrument.Generated
		private MinLengthSubber(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@CopeTypeColumnMinLength(20)
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class MinLengthIneffectiveSuper extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MinLengthIneffectiveSuper> TYPE = com.exedio.cope.TypesBound.newType(MinLengthIneffectiveSuper.class);

		@com.exedio.cope.instrument.Generated
		protected MinLengthIneffectiveSuper(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class MinLengthIneffectiveSub extends MinLengthIneffectiveSuper
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MinLengthIneffectiveSub> TYPE = com.exedio.cope.TypesBound.newType(MinLengthIneffectiveSub.class);

		@com.exedio.cope.instrument.Generated
		private MinLengthIneffectiveSub(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class Ref extends Item
	{
		@WrapperIgnore static final ItemField<StandardSuper> standard = ItemField.create(StandardSuper.class);
		@WrapperIgnore static final ItemField<MinLengthSuper> minLength = ItemField.create(MinLengthSuper.class);
		@WrapperIgnore static final ItemField<MinLengthSub> minLengthSub = ItemField.create(MinLengthSub.class);
		@WrapperIgnore static final ItemField<MinLengthIneffectiveSuper> minLengthIneffective = ItemField.create(MinLengthIneffectiveSuper.class);

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Ref> TYPE = com.exedio.cope.TypesBound.newType(Ref.class);

		@com.exedio.cope.instrument.Generated
		private Ref(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model model = new Model(
			StandardSuper.TYPE, StandardSub.TYPE,
			MinLengthSuper.TYPE, MinLengthSub.TYPE, MinLengthSubber.TYPE,
			MinLengthIneffectiveSuper.TYPE, MinLengthIneffectiveSub.TYPE,
			Ref.TYPE);


	@BeforeEach final void setUp()
	{
		model.connect(props);
	}

	@AfterEach final void tearDown()
	{
		model.disconnect();
	}

	private static final ConnectProperties props = ConnectProperties.create(TestSources.minimal());
}
