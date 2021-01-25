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

import static com.exedio.cope.DataField.toValue;
import static com.exedio.cope.DumperItem.TYPE;
import static com.exedio.cope.DumperItem.data;
import static com.exedio.cope.DumperItem.string;
import static com.exedio.cope.DumperItem.unique;
import static com.exedio.cope.DumperSubItem.subString;
import static com.exedio.cope.util.Hex.decodeLower;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.exedio.cope.tojunit.SI;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DumperTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(TYPE, DumperSubItem.TYPE);

	public DumperTest()
	{
		super(MODEL);
		copeRule.omitTransaction();
	}

	private Dumper dumper = null;
	private String dataL = null;

	@SuppressWarnings({"AssignmentToStaticFieldFromInstanceMethod", "deprecation"})
	@BeforeEach final void setUp()
	{
		DumperItem.beforeNewCopeItemCount = 0;
		dumper = new Dumper();
		switch(dialect)
		{
			case hsqldb:     dataL =      "X'aabbcc'"; break;
			case mysql:      dataL =      "x'aabbcc'"; break;
			case postgresql: dataL = "E'\\\\xaabbcc'"; break;
			default:
				fail(dialect.toString());
		}
	}

	@Test void testOk() throws IOException
	{
		assertFalse(model.hasCurrentTransaction());
		assumeNoVault();

		final StringBuilder out = new StringBuilder();
		final DumperItem item = dumper.newItem(out, TYPE,
				string.map("string0"),
				unique.map("unique0"),
				data.map(toValue(decodeLower("aabbcc"))));
		assertEquals("DumperItem-0", item.getCopeID());
		assertEquals(
				"insert into " + tab(TYPE) +
				"(" + pk(TYPE) + "," + cls(TYPE) + "," + upd(TYPE) + "," + col(string) + "," + col(unique) + "," + col(data) + ")values" +
				"(0,'DumperItem',0,'string0','unique0'," + dataL + ");",
				out.toString());
		assertEquals(1, DumperItem.beforeNewCopeItemCount);
	}

	@Test void testSub() throws IOException
	{
		assertFalse(model.hasCurrentTransaction());
		assumeNoVault();

		final StringBuilder out = new StringBuilder();
		final DumperItem item = dumper.newItem(out, DumperSubItem.TYPE,
				string.map("string0"),
				unique.map("unique0"),
				data.map(toValue(decodeLower("aabbcc"))),
				subString.map("subString0"));
		assertEquals("DumperSubItem-0", item.getCopeID());
		assertEquals(
				"insert into " + tab(TYPE) +
				"(" + pk(TYPE) + "," + cls(TYPE) + "," + upd(TYPE) + "," + col(string) + "," + col(unique) + "," + col(data) + ")values" +
				"(0,'DumperSubItem',0,'string0','unique0'," + dataL + ");" +
				"insert into " + tab(DumperSubItem.TYPE) +
				"(" + pk(DumperSubItem.TYPE) + "," + upd(DumperSubItem.TYPE) + "," + col(subString) + ")values" +
				"(0,0,'subString0');",
				out.toString());
		assertEquals(1, DumperItem.beforeNewCopeItemCount);
	}

	@Test void testMandatory() throws IOException
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

	@Test void testMandatoryData() throws IOException
	{
		try
		{
			dumper.newItem(null, TYPE,
					string.map("string"),
					unique.map("unique"));
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertEquals(data, e.getFeature());
			assertEquals(null, e.getItem());
		}
		assertEquals(1, DumperItem.beforeNewCopeItemCount);
	}

	@Test void testLength() throws IOException
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
			assertSame(string, e.getFeature());
			assertEquals(null, e.getItem());
		}
		assertEquals(1, DumperItem.beforeNewCopeItemCount);
	}

	@SuppressWarnings("HardcodedLineSeparator") // OK unix newline in sql
	@Test void testPrepare() throws IOException
	{
		final StringBuilder out = new StringBuilder();
		dumper.prepare(out, model);
		if(mysql)
			assertEquals(
					"SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT;\n" +
					"SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS;\n" +
					"SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION;\n" +
					"SET NAMES utf8;\n" +
					"SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_ALL_TABLES,NO_ZERO_DATE,NO_ZERO_IN_DATE,NO_ENGINE_SUBSTITUTION,NO_BACKSLASH_ESCAPES,ONLY_FULL_GROUP_BY';\n" +
					"SET @OLD_TIME_ZONE=@@TIME_ZONE;\n"+
					"SET TIME_ZONE='+00:00';\n",
				out.toString());
		else
			assertEquals("", out.toString());

		out.setLength(0);
		dumper.unprepare(out, model);
		if(mysql)
			assertEquals(
					"SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT;\n" +
					"SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS;\n" +
					"SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION;\n" +
					"SET SQL_MODE=@OLD_SQL_MODE;\n" +
					"SET TIME_ZONE=@OLD_TIME_ZONE;\n",
				out.toString());
		else
			assertEquals("", out.toString());
	}


	private void assumeNoVault() throws IOException
	{
		if(data.getVaultInfo()==null)
			return;

		final StringBuilder out = new StringBuilder();
		try
		{
			dumper.newItem(out, TYPE,
					string.map("string0"),
					unique.map("unique0"),
					data.map(toValue(decodeLower("aabbcc"))));
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals(
					"Dumper does not support DataField Vault: DumperItem.data",
					e.getMessage());
			assumeTrue(false, "no vault");
		}
	}

	private static String tab(final Type<?> type)
	{
		return SI.tab(type);
	}

	private static String pk(final Type<?> type)
	{
		return SI.pk(type);
	}

	private static String cls(final Type<?> type)
	{
		return SI.type(type);
	}

	private static String upd(final Type<?> type)
	{
		return SI.update(type);
	}

	private static String col(final Field<?> field)
	{
		return SI.col(field);
	}
}
