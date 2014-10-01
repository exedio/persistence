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

package com.exedio.cope;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.getTypeColumnName;
import static com.exedio.cope.SchemaInfo.getTypeColumnValue;

import com.exedio.cope.junit.CopeAssert;
import java.io.File;

public final class TypeColumnTypeTest extends CopeAssert
{
	@SuppressWarnings("static-method")
	public void testIt()
	{
		assertEquals("StandardSub", getTypeColumnValue(StandardSub.TYPE));
		assertEquals("MinLengthSub", getTypeColumnValue(MinLengthSub.TYPE));
		assertEquals("MinLengthIneffectiveSub", getTypeColumnValue(MinLengthIneffectiveSub.TYPE));

		assertIt("VARCHAR(13)", StandardSuper.TYPE);
		assertIt("VARCHAR(13)", Ref.standard);
		assertIt("VARCHAR(14)", MinLengthSuper.TYPE);
		assertIt("VARCHAR(14)", Ref.minLength);
		assertIt("VARCHAR(25)", MinLengthIneffectiveSuper.TYPE);
		assertIt("VARCHAR(25)", Ref.minLengthIneffective);
	}

	private static void assertIt(final String expected, final Type<?> type)
	{
		assertEquals(
				expected,
				model.getSchema().
					getTable(getTableName(type)).
					getColumn(getTypeColumnName(type)).
					getType());
	}

	private static void assertIt(final String expected, final ItemField<?> field)
	{
		assertEquals(
				expected,
				model.getSchema().
					getTable(getTableName(field.getType())).
					getColumn(getTypeColumnName(field)).
					getType());
	}


	private static class StandardSuper extends Item
	{
		private static final long serialVersionUID = 1l;
		static final Type<StandardSuper> TYPE = TypesBound.newType(StandardSuper.class);
		StandardSuper(final ActivationParameters ap) { super(ap); }
	}

	private static final class StandardSub extends StandardSuper
	{
		private static final long serialVersionUID = 1l;
		static final Type<StandardSub> TYPE = TypesBound.newType(StandardSub.class);
		private StandardSub(final ActivationParameters ap) { super(ap); }
	}

	private static class MinLengthSuper extends Item
	{
		private static final long serialVersionUID = 1l;
		static final Type<MinLengthSuper> TYPE = TypesBound.newType(MinLengthSuper.class);
		MinLengthSuper(final ActivationParameters ap) { super(ap); }
	}

	private static final class MinLengthSub extends MinLengthSuper
	{
		private static final long serialVersionUID = 1l;
		static final Type<MinLengthSub> TYPE = TypesBound.newType(MinLengthSub.class);
		private MinLengthSub(final ActivationParameters ap) { super(ap); }
	}

	private static class MinLengthIneffectiveSuper extends Item
	{
		private static final long serialVersionUID = 1l;
		static final Type<MinLengthIneffectiveSuper> TYPE = TypesBound.newType(MinLengthIneffectiveSuper.class);
		MinLengthIneffectiveSuper(final ActivationParameters ap) { super(ap); }
	}

	private static final class MinLengthIneffectiveSub extends MinLengthIneffectiveSuper
	{
		private static final long serialVersionUID = 1l;
		static final Type<MinLengthIneffectiveSub> TYPE = TypesBound.newType(MinLengthIneffectiveSub.class);
		private MinLengthIneffectiveSub(final ActivationParameters ap) { super(ap); }
	}

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


	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		model.connect(props);
	}

	@Override
	protected void tearDown() throws Exception
	{
		model.disconnect();
		super.tearDown();
	}

	private static final ConnectProperties props = new ConnectProperties(new File("runtime/utiltest.properties"));
}
