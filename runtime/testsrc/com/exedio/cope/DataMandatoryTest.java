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
import static com.exedio.cope.DataMandatoryItem.TYPE;
import static com.exedio.cope.DataMandatoryItem.data;
import static com.exedio.cope.RuntimeAssert.assertData;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.tojunit.MyTemporaryFolder;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class DataMandatoryTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(TYPE);

	public DataMandatoryTest()
	{
		super(MODEL);
	}

	private final MyTemporaryFolder files = new MyTemporaryFolder();

	private DataMandatoryItem item;

	@BeforeEach final void setUp()
	{
		item = new DataMandatoryItem(toValue(bytes4));
	}

	@Test void testData() throws MandatoryViolationException, IOException
	{
		// test model
		assertEquals(false, data.isFinal());
		assertEquals(true, data.isMandatory());

		// test persistence
		assertData(bytes4, item.getDataArray());

		item.setData(bytes6);
		assertData(bytes6, item.getDataArray());

		try
		{
			item.setData((byte[])null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertSame(data, e.getFeature());
			assertSame(item, e.getItem());
		}
		assertData(bytes6, item.getDataArray());

		item.setData(stream(bytes4));
		assertData(bytes4, item.getDataArray());

		try
		{
			item.setData((InputStream)null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertSame(data, e.getFeature());
			assertSame(item, e.getItem());
		}
		assertData(bytes4, item.getDataArray());

		item.setData(files.newPath(bytes5));
		assertData(bytes5, item.getDataArray());

		try
		{
			item.setData((Path)null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertSame(data, e.getFeature());
			assertSame(item, e.getItem());
		}
		assertData(bytes5, item.getDataArray());

		item.setData(files.newFile(bytes6));
		assertData(bytes6, item.getDataArray());

		try
		{
			item.setData((File)null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertSame(data, e.getFeature());
			assertSame(item, e.getItem());
		}
		assertData(bytes6, item.getDataArray());

		try
		{
			new DataMandatoryItem((DataField.Value)null);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertSame(data, e.getFeature());
			assertSame(null, e.getItem());
		}
		assertEquals(list(item), TYPE.search());

		try
		{
			new DataMandatoryItem(new SetValue<?>[0]);
			fail();
		}
		catch(final MandatoryViolationException e)
		{
			assertSame(data, e.getFeature());
			assertSame(null, e.getItem());
		}
		assertEquals(list(item), TYPE.search());
	}

	private static final byte[] bytes4  = {-86,122,-8,23};
	private static final byte[] bytes5  = {-55,56,-3,15};
	private static final byte[] bytes6  = {-97,35,-126,86,19,-8};
}
