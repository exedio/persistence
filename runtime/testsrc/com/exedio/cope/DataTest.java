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
import static com.exedio.cope.SchemaInfo.checkVaultTrail;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.tojunit.MyTemporaryFolder;
import com.exedio.cope.tojunit.SI;
import com.exedio.cope.util.Hex;
import com.exedio.cope.vault.VaultProperties;
import com.exedio.cope.vaulttest.VaultServiceTest.AssertionErrorOutputStream;
import com.exedio.cope.vaulttest.VaultServiceTest.NonCloseableOrFlushableOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

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

	private void assertIt(final byte[] expectedData) throws MandatoryViolationException, IOException, NoSuchAlgorithmException
	{
		assertIt(expectedData, item);
	}

	private void assertIt(final byte[] expectedData, final DataItem item)
			throws MandatoryViolationException, IOException, NoSuchAlgorithmException
	{
		final byte[] alreadyExists = {1,2,3,4,5,6,7,8,9,10};

		final boolean vault =
				model.getConnectProperties().vault!=null &&
				model.getConnectProperties().vault.isAppliedToAllFields();
		if(expectedData!=null)
		{
			assertTrue(!item.isDataNull());
			assertEquals(expectedData.length, item.getDataLength());
			assertData(expectedData, item.getDataArray());

			{
				final NonCloseableOrFlushableOutputStream temp = new NonCloseableOrFlushableOutputStream();
				item.getData(temp);
				assertData(expectedData, temp.toByteArray());
			}
			{
				final Path temp = files.newPathNotExists();
				item.getData(temp);
				assertTrue(Files.exists(temp));
				assertEqualContent(expectedData, temp.toFile());
			}
			{
				final File temp = files.newFileNotExists();
				item.getData(temp);
				assertTrue(temp.exists());
				assertEqualContent(expectedData, temp);
			}
			{
				final Path temp = files.newPath(alreadyExists);
				item.getData(temp);
				assertTrue(Files.exists(temp));
				assertEqualContent(expectedData, temp.toFile());
			}
			{
				final File temp = files.newFile(alreadyExists);
				item.getData(temp);
				assertTrue(temp.exists());
				assertEqualContent(expectedData, temp);
			}

			if(vault)
				assertEquals(
						Hex.encodeLower(MessageDigest.getInstance("SHA-512").digest(expectedData)),
						data.getVaultHash(item));
			else
				assertFails(
						() -> data.getVaultHash(item),
						IllegalArgumentException.class,
						"vault disabled for DataItem.data");
		}
		else
		{
			assertTrue(item.isDataNull());
			assertEquals(-1, item.getDataLength());
			assertEquals(null, item.getDataArray());

			{
				final AssertionErrorOutputStream temp = new AssertionErrorOutputStream();
				item.getData(temp);
				assertEquals(0, temp.toByteArray().length);
			}
			{
				final Path temp = files.newPathNotExists();
				item.getData(temp);
				assertFalse(Files.exists(temp));
			}
			{
				final File temp = files.newFileNotExists();
				item.getData(temp);
				assertFalse(temp.exists());
			}
			{
				final Path temp = files.newPath(alreadyExists);
				item.getData(temp);
				assertTrue(Files.exists(temp)); // TODO maybe file should be deleted when field is null?
				assertEqualContent(alreadyExists, temp.toFile());
			}
			{
				final File temp = files.newFile(alreadyExists);
				item.getData(temp);
				assertTrue(temp.exists()); // TODO maybe file should be deleted when field is null?
				assertEqualContent(alreadyExists, temp);
			}

			if(vault)
				assertEquals(null, data.getVaultHash(item));
			else
				assertFails(
						() -> data.getVaultHash(item),
						IllegalArgumentException.class,
						"vault disabled for DataItem.data");
		}
	}

	@Test void testData() throws MandatoryViolationException, IOException, NoSuchAlgorithmException
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

		assertFailsLength(
				() -> item.setData10(bytes11),
				item, data10, 11, true,
				"length violation on " + item + ", 11 bytes is too long for " + data10);
		assertData(bytes10, item.getData10Array());

		assertFailsLength(
				() -> item.setData10(stream(bytes11)),
				item, data10, 11, false,
				"length violation on " + item + ", 11 bytes or more is too long for " + data10);
		assertData(bytes10, item.getData10Array());

		assertFailsLength(
				() -> item.setData10(files.newPath(bytes11)),
				item, data10, 11, true,
				"length violation on " + item + ", 11 bytes is too long for " + data10);
		assertData(bytes10, item.getData10Array());

		assertFailsLength(
				() -> item.setData10(files.newFile(bytes11)),
				item, data10, 11, true,
				"length violation on " + item + ", 11 bytes is too long for " + data10);
		assertData(bytes10, item.getData10Array());

		final DataField.Value value4 = DataField.toValue(bytes4);
		item.setData(value4);
		assertData(bytes4, item.getDataArray());

		assertFails(
				() -> item.setData(value4),
				IllegalStateException.class,
				"Value already exhausted: DataField.Value:aa7af817. " +
				"Each DataField.Value can be used for at most one setter action.");

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

		assertFailsLength(
				() -> item.set(data10.map(bytes11)),
				item, data10, 11, true,
				"length violation on " + item + ", 11 bytes is too long for " + data10);
		assertData(bytes10, item.getData10Array());

		item.set(
				data.mapNull(),
				data10.mapNull()
		);
		assertNull(item.getDataArray());
		assertNull(item.getData10Array());
	}

	@Test void testCreate()
	{
		assertEquals(Arrays.asList(item), DataItem.TYPE.search());

		assertFailsLength(
				() -> DataItem.TYPE.newItem(data10.map(bytes11)),
				null, data10, 11, true,
				"length violation, 11 bytes is too long for " + data10);
		assertEquals(Arrays.asList(item), DataItem.TYPE.search());

		assertFailsLength(
				() -> DataItem.TYPE.newItem(data10.map(stream(bytes11))),
				null, data10, 11, false,
				"length violation, 11 bytes or more is too long for " + data10);
		assertEquals(Arrays.asList(item), DataItem.TYPE.search());

		assertFailsLength(
				() -> DataItem.TYPE.newItem(data10.map(files.newPath(bytes11))),
				null, data10, 11, true,
				"length violation, 11 bytes is too long for " + data10);
		assertEquals(Arrays.asList(item), DataItem.TYPE.search());

		assertFailsLength(
				() -> DataItem.TYPE.newItem(data10.map(files.newFile(bytes11))),
				null, data10, 11, true,
				"length violation, 11 bytes is too long for " + data10);
		assertEquals(Arrays.asList(item), DataItem.TYPE.search());
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

	@SuppressWarnings({"unchecked","rawtypes"}) // OK: test bad API usage
	@Test void testUnchecked()
	{
		data.set(item, bytes8);
		assertData(bytes8, item.getDataArray());

		assertFails(
				() -> item.set(SetValue.map((Field)data, "zack")),
				ClassCastException.class,
				"expected a " + DataField.Value.class.getName() + ", " +
				"but was a java.lang.String for " + data + '.');
		assertData(bytes8, item.getDataArray());

		assertFails(
				() -> DataItem.TYPE.newItem(SetValue.map((Field)data, Integer.valueOf(1))),
				ClassCastException.class,
				"expected a " + DataField.Value.class.getName() + ", " +
				"but was a java.lang.Integer for " + data + '.');
		assertData(bytes8, item.getDataArray());
	}

	@Test void testSchema()
	{
		assertSchema();
	}

	@Test void testCheckVaultTrail()
	{
		assertFails(
				() -> checkVaultTrail(null),
				NullPointerException.class,
				"field");
		final VaultProperties vp = model.getConnectProperties().getVaultProperties();
		if(vp==null)
		{
			assertFails(data::checkVaultTrail, IllegalStateException.class, "vault is disabled");
			assertFails(() -> checkVaultTrail(data), IllegalStateException.class, "vault is disabled");
		}
		else if(!vp.isTrailEnabled())
		{
			assertFails(data::checkVaultTrail, IllegalStateException.class, "trail is disabled");
			assertFails(() -> checkVaultTrail(data), IllegalStateException.class, "trail is disabled");
		}
		else
		{
			assertEquals(0, data.checkVaultTrail());
			final String trailTab  = SchemaInfo.quoteName(model, "VaultTrail_default");
			final String trailHash = SchemaInfo.quoteName(model, "hash");
			assertEquals(
					"SELECT COUNT(*) FROM " + SI.tab(DataItem.TYPE) + " " +
					"LEFT JOIN " + trailTab + " " +
					"ON " + SI.colq(data) + "=" + trailTab + "." + trailHash + " " +
					"WHERE " + SI.colq(data) + " IS NOT NULL " +
					"AND " + trailTab + "." + trailHash + " IS NULL",
					checkVaultTrail(data));
		}
	}

	private static final byte[] bytes0  = {};
	private static final byte[] bytes4  = {-86,122,-8,23};
	private static final byte[] bytes6  = {-97,35,-126,86,19,-8};
	private static final byte[] bytes8  = {-54,104,-63,23,19,-45,71,-23};
	private static final byte[] bytes10 = {-97,19,-8,35,-126,-86,122,86,19,-8};
	private static final byte[] bytes11 = {22,-97,19,-8,35,-126,-86,122,86,19,-8};

	private static void assertFailsLength(
			final Executable executable,
			final Item item,
			final DataField field,
			final int length,
			final boolean lengthExact,
			final String message)
	{
		final DataLengthViolationException e =
				assertFails(executable, DataLengthViolationException.class, message);
		assertAll(
				() -> assertEquals(item, e.getItem(), "item"),
				() -> assertEquals(field, e.getFeature(), "field"),
				() -> assertEquals(length, e.getLength(), "length"),
				() -> assertEquals(lengthExact, e.isLengthExact(), "lengthExact"),
				() -> assertEquals(null, e.getCause()));
	}

	static ZipFile openZip() throws IOException, URISyntaxException
	{
		return new ZipFile(DataTest.class.getResource("DataTest.zip").toURI().getPath());
	}
}
