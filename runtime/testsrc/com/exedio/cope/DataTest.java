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

import static com.exedio.cope.AbstractRuntimeTest.assertEqualContent;
import static com.exedio.cope.DataItem.data;
import static com.exedio.cope.DataItem.data10;
import static com.exedio.cope.RuntimeAssert.assertData;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.tojunit.MyTemporaryFolder;
import com.exedio.cope.vaulttest.VaultServiceTest.AssertionErrorOutputStream;
import com.exedio.cope.vaulttest.VaultServiceTest.NonCloseableOrFlushableOutputStream;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class DataTest extends TestWithEnvironment
{
	public DataTest()
	{
		super(DataModelTest.MODEL);
	}

	private final MyTemporaryFolder files = new MyTemporaryFolder();

	private DataItem item;
	private byte[] dataBig;

	@BeforeEach final void setUp()
	{
		final int data8Length = bytes8.length;
		// must be substantially larger than
		// dataAttribute.bufferSize* values in cope.properties
		final int dataBigLength = (50*1024) + 77;
		dataBig = new byte[dataBigLength];
		for(int i = 0; i<dataBigLength; i++)
			dataBig[i] = bytes8[i % data8Length];

		item = new DataItem();
	}

	@AfterEach final void tearDown()
	{
		// release memory
		dataBig = null;
	}

	private void assertIt(final byte[] expectedData) throws MandatoryViolationException, IOException
	{
		assertIt(expectedData, item);
	}

	private void assertIt(final byte[] expectedData, final DataItem item)
		throws MandatoryViolationException, IOException
	{
		assertIt(expectedData, item, oracle);
	}

	private void assertIt(final byte[] expectedData, final DataItem item, final boolean oracle)
		throws MandatoryViolationException, IOException
	{
		if(expectedData!=null && !(oracle && expectedData.length==0))
		{
			assertTrue(!item.isDataNull());
			assertEquals(expectedData.length, item.getDataLength());
			assertData(expectedData, item.getDataArray());

			{
				final NonCloseableOrFlushableOutputStream tempStream = new NonCloseableOrFlushableOutputStream();
				item.getData(tempStream);
				assertData(expectedData, tempStream.toByteArray());
			}
			{
				final File tempFile = files.newFileNotExists();
				item.getData(tempFile);
				assertTrue(tempFile.exists());
				assertEqualContent(expectedData, tempFile);
			}
		}
		else
		{
			assertTrue(item.isDataNull());
			assertEquals(-1, item.getDataLength());
			assertEquals(null, item.getDataArray());

			{
				final AssertionErrorOutputStream tempStream = new AssertionErrorOutputStream();
				item.getData(tempStream);
				assertEquals(0, tempStream.toByteArray().length);
			}
			{
				final File tempFile = files.newFileNotExists();
				item.getData(tempFile);
				assertFalse(tempFile.exists());
			}
		}
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_INFERRED")
	@Test void testData() throws MandatoryViolationException, IOException
	{
		assertIt(null);

		// set byte[]
		item.setData(bytes4);
		assertIt(bytes4);

		item.setData(bytes6);
		assertIt(bytes6);

		item.setData(bytes0);
		assertIt(bytes0);

		item.setData(dataBig);
		assertIt(dataBig);

		item.setData((byte[])null);
		assertIt(null);


		// set InputStream
		item.setData(stream(bytes4));
		assertStreamClosed();
		assertIt(bytes4);

		item.setData(stream(bytes6));
		assertStreamClosed();
		assertIt(bytes6);

		item.setData(stream(bytes0));
		assertStreamClosed();
		assertIt(bytes0);

		item.setData(stream(dataBig));
		assertStreamClosed();
		assertIt(dataBig);

		item.setData((InputStream)null);
		assertIt(null);


		// set Path
		item.setData(files.newPath(bytes8));
		assertIt(bytes8);

		item.setData(files.newPath(bytes0));
		assertIt(bytes0);

		item.setData(files.newPath(dataBig));
		assertIt(dataBig);

		item.setData((Path)null);
		assertIt(null);


		// set File
		item.setData(files.newFile(bytes8));
		assertIt(bytes8);

		item.setData(files.newFile(bytes0));
		assertIt(bytes0);

		item.setData(files.newFile(dataBig));
		assertIt(dataBig);

		item.setData((File)null);
		assertIt(null);


		final DataSubItem subItem = new DataSubItem();

		subItem.setData(stream(bytes4));
		assertStreamClosed();
		assertIt(bytes4, subItem);
		assertEquals(bytes4.length, subItem.getDataLength());

		// test maximum length
		item.setData10(bytes0);
		item.setData10(bytes4);
		item.setData10(bytes6);
		item.setData10(bytes8);
		item.setData10(bytes10);
		assertData(bytes10, item.getData10Array());

		try
		{
			item.setData10(bytes11);
			fail();
		}
		catch(final DataLengthViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(data10, e.getFeature());
			assertEquals(data10, e.getFeature());
			assertEquals(11, e.getLength());
			assertEquals(true, e.isLengthExact());
			assertEquals("length violation on " + item + ", 11 bytes is too long for " + data10, e.getMessage());
		}
		assertData(bytes10, item.getData10Array());
		try
		{
			item.setData10(stream(bytes11));
			fail();
		}
		catch(final DataLengthViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(data10, e.getFeature());
			assertEquals(data10, e.getFeature());
			assertEquals(11, e.getLength());
			assertEquals(false, e.isLengthExact());
			assertEquals("length violation on " + item + ", 11 bytes or more is too long for " + data10, e.getMessage(), e.getMessage());
		}
		assertData(bytes10, item.getData10Array());
		try
		{
			item.setData10(files.newPath(bytes11));
			fail();
		}
		catch(final DataLengthViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(data10, e.getFeature());
			assertEquals(data10, e.getFeature());
			assertEquals(11, e.getLength());
			assertEquals(true, e.isLengthExact());
			assertEquals("length violation on " + item + ", 11 bytes is too long for " + data10, e.getMessage());
		}
		assertData(bytes10, item.getData10Array());
		try
		{
			item.setData10(files.newFile(bytes11));
			fail();
		}
		catch(final DataLengthViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(data10, e.getFeature());
			assertEquals(data10, e.getFeature());
			assertEquals(11, e.getLength());
			assertEquals(true, e.isLengthExact());
			assertEquals("length violation on " + item + ", 11 bytes is too long for " + data10, e.getMessage());
		}
		assertData(bytes10, item.getData10Array());

		final DataField.Value value4 = DataField.toValue(bytes4);
		item.setData(value4);
		assertData(bytes4, item.getDataArray());
		try
		{
			item.setData(value4);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("Value already exhausted: DataField.Value:aa7af817. Each DataField.Value can be used for at most one setter action.", e.getMessage());
		}

		// implements Settable
		assertNull(item.getName());
		item.set(
				data.map(bytes8),
				data10.map(bytes10),
				DataItem.name.map("eins")
		);
		assertData(bytes8, item.getDataArray());
		assertData(bytes10, item.getData10Array());
		assertEquals("eins", item.getName());

		item.set(
				data.map(bytes11),
				data10.map(bytes10)
		);
		assertData(bytes11, item.getDataArray());
		assertData(bytes10, item.getData10Array());
		assertEquals("eins", item.getName());

		{
			final DataItem item2 = new DataItem(bytes4, bytes10);
			assertData(bytes4, item2.getDataArray());
			assertData(bytes10, item2.getData10Array());
		}
		{
			final DataItem item3 = DataItem.TYPE.newItem(
					data.map(bytes6),
					data10.map(bytes10)
			);
			assertData(bytes6, item3.getDataArray());
			assertData(bytes10, item3.getData10Array());
		}

		assertData(bytes10, item.getData10Array());
		try
		{
			item.set(data10.map(bytes11));
			fail();
		}
		catch(final DataLengthViolationException e)
		{
			assertEquals(item, e.getItem());
			assertEquals(data10, e.getFeature());
			assertEquals(data10, e.getFeature());
			assertEquals(11, e.getLength());
			assertEquals(true, e.isLengthExact());
			assertEquals("length violation on " + item + ", 11 bytes is too long for " + data10, e.getMessage());
		}
		assertData(bytes10, item.getData10Array());

		try
		{
			DataItem.TYPE.newItem(data10.map(bytes11));
			fail();
		}
		catch(final DataLengthViolationException e)
		{
			assertEquals(null, e.getItem());
			assertEquals(data10, e.getFeature());
			assertEquals(data10, e.getFeature());
			assertEquals(11, e.getLength());
			assertEquals(true, e.isLengthExact());
			assertEquals("length violation, 11 bytes is too long for " + data10, e.getMessage());
		}
		item.set(
				data.mapNull(),
				data10.mapNull()
		);
		assertNull(item.getDataArray());
		assertNull(item.getData10Array());
	}

	@Test void testZipSet() throws IOException, URISyntaxException
	{
		assertEquals(null, item.getDataArray());

		try(ZipFile file = openZip())
		{
			final ZipEntry entry = file.getEntry("bytes4.dat");
			final DataField.Value value = DataField.toValue(file, entry);
			item.setData(value);
			assertData(bytes4, item.getDataArray());
		}
	}

	@Test void testZipCreate() throws IOException, URISyntaxException
	{
		try(ZipFile file = openZip())
		{
			final ZipEntry entry = file.getEntry("bytes4.dat");
			final DataField.Value value = DataField.toValue(file, entry);
			final DataItem item2 = new DataItem(data.map(value));
			assertData(bytes4, item2.getDataArray());
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: test bad API usage
	@Test void testUnchecked()
	{
		data.set(item, bytes8);
		assertData(bytes8, item.getDataArray());
		try
		{
			item.set(
					SetValue.map((Field)data, "zack")
			);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected a " + DataField.Value.class.getName() + ", but was a java.lang.String for " + data + '.', e.getMessage());
		}
		assertData(bytes8, item.getDataArray());

		try
		{
			DataItem.TYPE.newItem(
					SetValue.map((Field)data, Integer.valueOf(1))
			);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected a " + DataField.Value.class.getName() + ", but was a java.lang.Integer for " + data + '.', e.getMessage());
		}
		assertData(bytes8, item.getDataArray());
	}

	@Test void testSchema()
	{
		assertSchema();
	}

	private static final byte[] bytes0  = {};
	private static final byte[] bytes4  = {-86,122,-8,23};
	private static final byte[] bytes6  = {-97,35,-126,86,19,-8};
	private static final byte[] bytes8  = {-54,104,-63,23,19,-45,71,-23};
	private static final byte[] bytes10 = {-97,19,-8,35,-126,-86,122,86,19,-8};
	private static final byte[] bytes11 = {22,-97,19,-8,35,-126,-86,122,86,19,-8};

	static ZipFile openZip() throws IOException, URISyntaxException
	{
		return new ZipFile(DataTest.class.getResource("DataTest.zip").toURI().getPath());
	}
}
