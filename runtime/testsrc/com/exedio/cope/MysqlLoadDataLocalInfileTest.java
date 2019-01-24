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

import static com.exedio.cope.CacheIsolationItem.TYPE;
import static com.exedio.cope.CacheIsolationTest.MODEL;
import static java.nio.file.Files.setPosixFilePermissions;
import static java.nio.file.attribute.PosixFilePermission.GROUP_READ;
import static java.nio.file.attribute.PosixFilePermission.GROUP_WRITE;
import static java.nio.file.attribute.PosixFilePermission.OTHERS_READ;
import static java.nio.file.attribute.PosixFilePermission.OTHERS_WRITE;
import static java.nio.file.attribute.PosixFilePermission.OWNER_READ;
import static java.nio.file.attribute.PosixFilePermission.OWNER_WRITE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.exedio.cope.tojunit.ConnectionRule;
import com.exedio.cope.tojunit.MyTemporaryFolder;
import com.exedio.cope.tojunit.SI;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLSyntaxErrorException;
import java.util.EnumSet;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
public class MysqlLoadDataLocalInfileTest extends TestWithEnvironment
{
	public MysqlLoadDataLocalInfileTest()
	{
		super(MODEL);
		copeRule.omitTransaction();
	}

	private final MyTemporaryFolder files = new MyTemporaryFolder();
	private final ConnectionRule connection = new ConnectionRule(model);

	@Test void test() throws IOException
	{
		final Path file = files.newPath(new byte[]{'A','B'});
		setPosixFilePermissions(file, EnumSet.of(
				OWNER_READ,  GROUP_READ,  OTHERS_READ,
				OWNER_WRITE, GROUP_WRITE, OTHERS_WRITE));
		assumeTrue(mysql, "mysql");
		final Class<? extends SQLException> expected =
				mariaDriver
				? SQLFeatureNotSupportedException.class
				: SQLSyntaxErrorException.class;
		final SQLException actual = assertThrows(expected,
				() -> connection.executeUpdate(
						"LOAD DATA LOCAL INFILE '" + file.toAbsolutePath() + "' " +
						"INTO TABLE " + SI.tab(TYPE))
		);
		assertEquals(
				mariaDriver
				? "Usage of LOCAL INFILE is disabled. To use it enable it via the connection property allowLocalInfile=true"
				: "The used command is not allowed with this MySQL version",
				dropMariaConnectionId(actual.getMessage()));
	}
}
