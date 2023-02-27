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

import static com.exedio.cope.pattern.ImporterItem.TYPE;
import static com.exedio.cope.pattern.ImporterItem.description;
import static com.exedio.cope.pattern.ImporterItem.description2;
import static com.exedio.cope.pattern.ImporterItem.importByCode;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.SetValue;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.tojunit.ImporterRule;
import com.exedio.cope.tojunit.MainRule;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class ImporterTest extends TestWithEnvironment
{
	public ImporterTest()
	{
		super(ImporterModelTest.model);
	}

	private final ImporterRule importerRule = new ImporterRule(ImporterItem.byCode);

	@Test void testNonInitial()
	{
		doTest(false);
	}

	@Test void testInitial()
	{
		doTest(true);
	}

	private void doTest(final boolean hintInitial)
	{
		importerRule.set(hintInitial);

		assertEquals(list(), TYPE.search(null, TYPE.getThis(), true));

		final ImporterItem itemA =
			importByCode("codeA", SetValue.map(description, "descA"), SetValue.map(description2, "desc2A"));
		assertEquals(list(itemA), TYPE.search(null, TYPE.getThis(), true));
		assertEquals("codeA",  itemA.getCode());
		assertEquals("descA",  itemA.getDescription());
		assertEquals("desc2A", itemA.getDescription2());

		final ImporterItem itemB =
			importByCode("codeB", SetValue.map(description, "descB"), SetValue.map(description2, "desc2B"));
		assertEquals(list(itemA, itemB), TYPE.search(null, TYPE.getThis(), true));
		assertEquals("codeA",  itemA.getCode());
		assertEquals("descA",  itemA.getDescription());
		assertEquals("desc2A", itemA.getDescription2());
		assertEquals("codeB",  itemB.getCode());
		assertEquals("descB",  itemB.getDescription());
		assertEquals("desc2B", itemB.getDescription2());

		assertEquals(itemA,
			importByCode("codeA", SetValue.map(description, "descAx"), SetValue.map(description2, "desc2Ax")));
		assertEquals(list(itemA, itemB), TYPE.search(null, TYPE.getThis(), true));
		assertEquals("codeA", itemA.getCode());
		assertEquals("descAx", itemA.getDescription());
		assertEquals("desc2Ax",itemA.getDescription2());
		assertEquals("codeB",  itemB.getCode());
		assertEquals("descB",  itemB.getDescription());
		assertEquals("desc2B", itemB.getDescription2());

		final ArrayList<SetValue<?>> list = new ArrayList<>();
		list.add(SetValue.map(description, "descBl"));
		list.add(SetValue.map(description2, "desc2Bl"));
		assertEquals(itemB, importByCode("codeB", list));
		assertEquals(list(itemA, itemB), TYPE.search(null, TYPE.getThis(), true));
		assertEquals("codeA", itemA.getCode());
		assertEquals("descAx", itemA.getDescription());
		assertEquals("desc2Ax",itemA.getDescription2());
		assertEquals("codeB",  itemB.getCode());
		assertEquals("descBl",  itemB.getDescription());
		assertEquals("desc2Bl", itemB.getDescription2());
	}
}
