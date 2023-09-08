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

import static com.exedio.cope.Vault.DEFAULT;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.util.Properties;
import com.exedio.cope.util.Sources;
import java.io.PrintStream;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Imports vault data from a database into an arbitrary vault service.
 * <p>
 * Usage:
 * <pre>
 * java -cp exedio-cope.jar:exedio-cope-util.jar:slf4j-api.jar:myjdbcdriver.jar com.exedio.cope.vault.VaultJdbcToService config.properties
 * </pre>
 * File {@code config.properties} may look like this:
 * <pre>
 * source.url=jdbc:mysql://myhost/myschema
 * source.username=myuser
 * source.password=mypassword
 * source.query=SELECT hash,data FROM Vault
 * target.service=com.exedio.cope.vault.VaultFileService
 * target.service.root=myrootdir
 * </pre>
 * The SQL query specified by {@code source.query} must return the hash in the first column of the result set
 * and the actual data in the second column.
 * Note, that the query here is not sorted deterministically,
 * you may want to append a {@code ORDER BY hash} for example.
 * For testing with small subsets you may want to append a {@code LIMIT 5}.
 */
public final class VaultJdbcToService
{
	public static void main(final String[] args)
	{
		try
		{
			mainInternal(System.out, args);
		}
		catch(final SQLException e)
		{
			System.err.println("ERROR");
			e.printStackTrace(System.err);
			//noinspection CallToSystemExit
			System.exit(1);
		}
	}

	static void mainInternal(
			final PrintStream out,
			final String... args)
			throws SQLException
	{
		if(args.length<1)
			throw new IllegalArgumentException(
					"config file must be specified as first and only parameter");
		main(out, args[0]);
	}

	@SuppressWarnings("ConfusingMainMethod")
	private static void main(
			final PrintStream out,
			final String config)
			throws SQLException
	{
		final Props props = new Props(Sources.load(Path.of(config)));
		props.ensureValidity();
		final VaultService service = props.target.newServices(DEFAULT).get(DEFAULT);

		try(Connection connection = props.newConnection();
			 Statement stmt = connection.createStatement();
			 ResultSet resultSet = stmt.executeQuery(props.query))
		{
			int row = 0;
			int skipped = 0, redundant = 0;
			while(resultSet.next())
			{
				final String hash = resultSet.getString(1);
				final byte[] value = resultSet.getBytes(2);
				try
				{
					if(!service.put(hash, value, PUT_INFO))
					{
						redundant++;
						out.println("Redundant put at row " + row + " for hash " + hash);
					}
				}
				catch(final NullPointerException e)
				{
					skipped++;
					out.println("Skipping null at row " + row + ": " + e.getMessage());
				}
				catch(final IllegalArgumentException e)
				{
					skipped++;
					out.println("Skipping illegal argument at row " + row + ": " + e.getMessage());
				}
				row++;
			}
			out.println("Finished after " + row + " rows, skipped " + skipped + ", redundant " + redundant);
		}
	}

	private static final class Props extends Properties
	{
		private final String url = value("source.url", (String)null);
		private final String username = value("source.username", (String)null);
		private final String password = valueHidden("source.password", null);

		Connection newConnection() throws SQLException
		{
			final Driver driver = requireNonNull(DriverManager.getDriver(url), "no driver found for " + url);
			final java.util.Properties info = new java.util.Properties();
			info.setProperty("user", username);
			info.setProperty("password", password);
			return driver.connect(url, info);
		}

		final String query = value("source.query", (String)null);

		final VaultProperties target = valnp("target", VaultProperties.factory());

		Props(final Source source)
		{
			super(source);
		}
	}

	private static final VaultPutInfo PUT_INFO = new VaultPutInfo()
	{
		@Override
		public String getOrigin()
		{
			return VaultJdbcToService.class.toString();
		}
		@Override
		public String toString()
		{
			return VaultJdbcToService.class.toString();
		}
	};

	private VaultJdbcToService()
	{
		// prevent instantiation
	}
}
