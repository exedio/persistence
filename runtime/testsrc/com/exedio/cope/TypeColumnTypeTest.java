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
import static com.exedio.dsmf.Dialect.NOT_NULL;
import static org.junit.Assert.assertEquals;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.tojunit.TestSources;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TypeColumnTypeTest
{
	@Test public void testIt()
	{
		assertEquals("StandardSub", getTypeColumnValue(StandardSub.TYPE));
		assertEquals("MinLengthSub", getTypeColumnValue(MinLengthSub.TYPE));
		assertEquals("MinLengthIneffectiveSub", getTypeColumnValue(MinLengthIneffectiveSub.TYPE));

		assertIt("VARCHAR(13)", StandardSuper.TYPE);
		assertIt("VARCHAR(13)", Ref.standard);
		assertIt("VARCHAR(20)", MinLengthSuper.TYPE);
		assertIt("VARCHAR(20)", Ref.minLength);
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


	@WrapperIgnore
	private static class StandardSuper extends Item
	{
		private static final long serialVersionUID = 1l;
		static final Type<StandardSuper> TYPE = TypesBound.newType(StandardSuper.class);
		StandardSuper(final ActivationParameters ap) { super(ap); }
	}

	@WrapperIgnore
	private static final class StandardSub extends StandardSuper
	{
		private static final long serialVersionUID = 1l;
		static final Type<StandardSub> TYPE = TypesBound.newType(StandardSub.class);
		private StandardSub(final ActivationParameters ap) { super(ap); }
	}

	@CopeTypeColumnMinLength(20)
	@WrapperIgnore
	private static class MinLengthSuper extends Item
	{
		private static final long serialVersionUID = 1l;
		static final Type<MinLengthSuper> TYPE = TypesBound.newType(MinLengthSuper.class);
		MinLengthSuper(final ActivationParameters ap) { super(ap); }
	}

	@WrapperIgnore
	private static final class MinLengthSub extends MinLengthSuper
	{
		private static final long serialVersionUID = 1l;
		static final Type<MinLengthSub> TYPE = TypesBound.newType(MinLengthSub.class);
		private MinLengthSub(final ActivationParameters ap) { super(ap); }
	}

	@CopeTypeColumnMinLength(20)
	@WrapperIgnore
	private static class MinLengthIneffectiveSuper extends Item
	{
		private static final long serialVersionUID = 1l;
		static final Type<MinLengthIneffectiveSuper> TYPE = TypesBound.newType(MinLengthIneffectiveSuper.class);
		MinLengthIneffectiveSuper(final ActivationParameters ap) { super(ap); }
	}

	@WrapperIgnore
	private static final class MinLengthIneffectiveSub extends MinLengthIneffectiveSuper
	{
		private static final long serialVersionUID = 1l;
		static final Type<MinLengthIneffectiveSub> TYPE = TypesBound.newType(MinLengthIneffectiveSub.class);
		private MinLengthIneffectiveSub(final ActivationParameters ap) { super(ap); }
	}

	@WrapperIgnore
	private static final class Ref extends Item
	{
		static final ItemField<StandardSuper> standard = ItemField.create(StandardSuper.class);
		static final ItemField<MinLengthSuper> minLength = ItemField.create(MinLengthSuper.class);
		static final ItemField<MinLengthIneffectiveSuper> minLengthIneffective = ItemField.create(MinLengthIneffectiveSuper.class);
		private static final long serialVersionUID = 1l;
		static final Type<Ref> TYPE = TypesBound.newType(Ref.class);
		private Ref(final ActivationParameters ap) { super(ap); }
	}

	private static final Model model = new Model(
			StandardSuper.TYPE, StandardSub.TYPE,
			MinLengthSuper.TYPE, MinLengthSub.TYPE,
			MinLengthIneffectiveSuper.TYPE, MinLengthIneffectiveSub.TYPE,
			Ref.TYPE);


	@SuppressWarnings("static-method")
	@Before public final void setUp()
	{
		model.connect(props);
	}

	@SuppressWarnings("static-method")
	@After public final void tearDown()
	{
		model.disconnect();
	}

	private static final ConnectProperties props = ConnectProperties.create(TestSources.minimal());
}
