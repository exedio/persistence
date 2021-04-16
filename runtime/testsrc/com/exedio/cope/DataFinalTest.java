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
import static com.exedio.cope.RuntimeAssert.assertData;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.tojunit.MyTemporaryFolder;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class DataFinalTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(DataFinalItem.TYPE);
	private static final DataField data = DataFinalItem.data;

	public DataFinalTest()
	{
		super(MODEL);
	}

	private final MyTemporaryFolder files = new MyTemporaryFolder();

	private DataFinalItem item;

	@BeforeEach final void setUp()
	{
		item = new DataFinalItem(toValue(bytes4));
	}

	@Test void testData() throws MandatoryViolationException, IOException
	{
		// test model
		assertEquals(true, data.isFinal());
		assertEquals(false, data.isMandatory());

		// test persistence
		assertData(bytes4, item.getDataArray());

		try
		{
			data.set(item, bytes6);
			fail();
		}
		catch(final FinalViolationException e)
		{
			assertSame(data, e.getFeature());
			assertSame(item, e.getItem());
		}
		assertData(bytes4, item.getDataArray());

		try
		{
			item.set(data.map(bytes6));
			fail();
		}
		catch(final FinalViolationException e)
		{
			assertSame(data, e.getFeature());
			assertSame(item, e.getItem());
		}
		assertData(bytes4, item.getDataArray());

		try
		{
			data.set(item, stream(bytes6));
			fail();
		}
		catch(final FinalViolationException e)
		{
			assertSame(data, e.getFeature());
			assertSame(item, e.getItem());
		}
		assertData(bytes4, item.getDataArray());

		try
		{
			data.set(item, files.newFile(bytes6));
			fail();
		}
		catch(final FinalViolationException e)
		{
			assertSame(data, e.getFeature());
			assertSame(item, e.getItem());
		}
		assertData(bytes4, item.getDataArray());
	}

	private static final byte[] bytes4  = {-86,122,-8,23};
	private static final byte[] bytes6  = {-97,35,-126,86,19,-8};
}
