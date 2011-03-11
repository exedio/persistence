/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.DumperItem.TYPE;
import static com.exedio.cope.DumperSubItem.subString;
import static com.exedio.cope.DumperItem.string;
import static com.exedio.cope.DumperItem.unique;

import java.io.IOException;

public class DumperTest extends AbstractRuntimeTest
{
	private static final Model MODEL = new Model(TYPE, DumperSubItem.TYPE);

	public DumperTest()
	{
		super(MODEL);
		skipTransactionManagement();
	}

	private Dumper dumper = null;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		DumperItem.beforeNewCopeItemCount = 0;
		dumper = new Dumper();
	}

	public void testOk() throws IOException
	{
		assertFalse(model.hasCurrentTransaction());

		final StringBuilder out = new StringBuilder();
		dumper.newItem(out, TYPE,
				string.map("string0"),
				unique.map("unique0"));
		assertEquals(
				"insert into " + tab(TYPE) +
				"(" + pk(TYPE) + "," + cls(TYPE) + "," + col(string) + "," + col(unique) + ")values" +
				"(0,'DumperItem','string0','unique0');",
				out.toString());
		assertEquals(1, DumperItem.beforeNewCopeItemCount);
	}

	public void testSub() throws IOException
	{
		assertFalse(model.hasCurrentTransaction());

		final StringBuilder out = new StringBuilder();
		dumper.newItem(out, DumperSubItem.TYPE,
				string.map("string0"),
				unique.map("unique0"),
				subString.map("subString0"));
		assertEquals(
				"insert into " + tab(TYPE) +
				"(" + pk(TYPE) + "," + cls(TYPE) + "," + col(string) + "," + col(unique) + ")values" +
				"(0,'DumperSubItem','string0','unique0');" +
				"insert into " + tab(DumperSubItem.TYPE) +
				"(" + pk(DumperSubItem.TYPE) + "," + col(subString) + ")values" +
				"(0,'subString0');",
				out.toString());
		assertEquals(1, DumperItem.beforeNewCopeItemCount);
	}

	public void testMandatory() throws IOException
	{
		try
		{
			dumper.newItem(null, TYPE,
					string.map(null),
					unique.map("unique"));
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(string, e.getFeature());
			assertEquals(null, e.getItem());
		}
		assertEquals(1, DumperItem.beforeNewCopeItemCount);
	}

	public void testLength() throws IOException
	{
		try
		{
			dumper.newItem(null, TYPE,
					string.map("12345678901"),
					unique.map("unique"));
			fail();
		}
		catch(final StringLengthViolationException e)
		{
			assertEquals(string, e.getFeature());
			assertEquals(null, e.getItem());
		}
		assertEquals(1, DumperItem.beforeNewCopeItemCount);
	}


	private String tab(final Type type)
	{
		return SchemaInfo.quoteName(model, SchemaInfo.getTableName(type));
	}

	private String pk(final Type type)
	{
		return SchemaInfo.quoteName(model, SchemaInfo.getPrimaryKeyColumnName(type));
	}

	private String cls(final Type type)
	{
		return SchemaInfo.quoteName(model, SchemaInfo.getTypeColumnName(type));
	}

	private String col(final Field field)
	{
		return SchemaInfo.quoteName(model, SchemaInfo.getColumnName(field));
	}
}
