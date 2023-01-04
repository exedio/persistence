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

package com.exedio.cope.vault;

import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.TimeZoneStrict.getTimeZone;
import static com.exedio.cope.vault.VaultFileToTrail.mainInternal;
import static com.exedio.cope.vault.VaultFileToTrailTest.SERVICE_PARAMETERS;
import static com.exedio.cope.vault.VaultFileToTrailTest.put;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.exedio.cope.SchemaInfo;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.testmodel.Main;
import com.exedio.cope.vault.VaultFileToTrail.HumanReadableException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

public class VaultFileToTrailInsertTest extends TestWithEnvironment
{
	VaultFileToTrailInsertTest()
	{
		super(Main.model);
		copeRule.omitTransaction();
	}

	protected final TemporaryFolder files = new TemporaryFolder();

	@Test void test() throws IOException, HumanReadableException, SQLException
	{
		final Path root = files.newFolder().toPath();
		try(final VaultFileService s = new VaultFileService(SERVICE_PARAMETERS, new VaultFileService.Props(
				single("root", root))))
		{
			Files.createDirectory(s.tempDir);
			assertEquals("70903e79b7575e3f4e7ffa15c2608ac7", put(new byte[]{1,2,3,4,5,6,7,8,9,10}, s));
			assertEquals("8596c1af55b14b7b320112944fcb8536", put(new byte[]{1,2,3,4,5,6,7,8,9}, s));
			assertEquals("a8445619abd08f3ba0ebfcb31183f7f9", put(new byte[]{18}, s));
			for(final String path : new String[]{
					"709/03e79b7575e3f4e7ffa15c2608ac7",
					"859/6c1af55b14b7b320112944fcb8536",
					"a84/45619abd08f3ba0ebfcb31183f7f9",
			})
				Files.setLastModifiedTime(
						root.resolve(path),
						FileTime.from(LocalDateTime.of(2012, 3, 16, 8, 10, 22).toInstant(UTC)));

			final ByteArrayOutputStream outB = new ByteArrayOutputStream();
			final PrintStream out = new PrintStream(outB, false, US_ASCII);
			mainInternal(out,
					new PrintStream(new ByteArrayOutputStream(), false, US_ASCII),
					root.toString(), "default", "9");

			assumeTrue(mysql, "no mysql"); // TODO implement on postgresql using ON CONFLICT
			assumeTrue(model.getConnectProperties().getVaultProperties()!=null, "vault disabled");
			try(Connection con = SchemaInfo.newConnection(model);
				 Statement stmt = con.createStatement())
			{
				try(ResultSet rs = stmt.executeQuery(SELECT_TRAIL))
				{
					assertMetaData(rs);
					assertFalse(rs.next());
				}

				stmt.execute(outB.toString(US_ASCII));

				try(ResultSet rs = stmt.executeQuery(SELECT_TRAIL))
				{
					assertMetaData(rs);
					assertIt("70903e79b7575e3f4e7ffa15c2608ac7", 10, new byte[]{1,2,3,4,5,6,7,8,9}, rs);
					assertIt("8596c1af55b14b7b320112944fcb8536",  9, new byte[]{1,2,3,4,5,6,7,8,9}, rs);
					assertIt("a8445619abd08f3ba0ebfcb31183f7f9",  1, new byte[]{18}, rs);
					assertFalse(rs.next());
				}

				// test idempotence
				stmt.execute(outB.toString(US_ASCII));

				try(ResultSet rs = stmt.executeQuery(SELECT_TRAIL))
				{
					assertMetaData(rs);
					assertIt("70903e79b7575e3f4e7ffa15c2608ac7", 10, new byte[]{1,2,3,4,5,6,7,8,9}, rs);
					assertIt("8596c1af55b14b7b320112944fcb8536",  9, new byte[]{1,2,3,4,5,6,7,8,9}, rs);
					assertIt("a8445619abd08f3ba0ebfcb31183f7f9",  1, new byte[]{18}, rs);
					assertFalse(rs.next());
				}
			}
		}
	}

	private static final String SELECT_TRAIL = "SELECT * FROM `VaultTrail_default` ORDER BY `hash`";

	private static void assertMetaData(final ResultSet resultSet) throws SQLException
	{
		final ResultSetMetaData metaData = resultSet.getMetaData();
		assertEquals(
				List.of("hash", "length", "start20", "markPut", "date", "field", "origin"),
				IntStream.range(1, metaData.getColumnCount()+1).mapToObj(i -> {
					try
					{
						return metaData.getColumnName(i);
					}
					catch(final SQLException e)
					{
						throw new RuntimeException(e);
					}
				}).collect(Collectors.toList()));
	}

	private static void assertIt(
			final String hash,
			final int length,
			final byte[] start20,
			final ResultSet rs) throws SQLException
	{
		final GregorianCalendar cal = new GregorianCalendar(getTimeZone("GMT"), Locale.ENGLISH);
		assertTrue(rs.next());
		assertAll(
				() -> assertEquals(hash, rs.getString(1)),
				() -> assertEquals(length, rs.getInt(2)),
				() -> assertArrayEquals(start20, rs.getBytes(3)),
				() -> assertEquals(null, rs.getString(4)), // markPut
				() -> assertEquals("2012-03-16 08:10:22.0", rs.getTimestamp(5, cal).toString()),
				() -> assertEquals(null, rs.getString(6)), // field
				() -> assertEquals("VaultFileToTrail", rs.getString(7))); // origin
	}
}
