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

import static java.util.Objects.requireNonNull;

import com.exedio.cope.Vault;
import com.exedio.cope.util.Hex;
import com.exedio.cope.util.MessageDigestFactory;
import com.exedio.cope.util.Properties;
import com.exedio.cope.util.Sources;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

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
 * target.default.service=com.exedio.cope.vault.VaultFileService
 * target.default.service.root=myrootdir
 * </pre>
 * The SQL query specified by {@code source.query} must return the hash in the first column of the result set
 * and the actual data in the second column.
 * Note, that the query here is not sorted deterministically,
 * you may want to append a {@code ORDER BY hash} for example.
 * For testing with small subsets you may want to append a {@code LIMIT 5}.
 * <p>
 * Before the import, all {@link com.exedio.cope.util.Properties.Probe probes} of the service are run.
 * You may suppress individual probes in {@code config.properties}:
 * <pre>
 * targetProbesSuppressed=root.Exists root.Free
 * </pre>
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
		out.println("Fetch size set to " + props.fetchSize);
		props.ensureValidity();
		props.probeService(out);

		final int queriesSize = props.queries.size();
		final ArrayList<Stats> statsSummary = new ArrayList<>(queriesSize);
		final MessageDigestFactory queryHash = props.queryHash();
		try(VaultService service = props.target.newServices(BUCKET).get(BUCKET);
			 Connection connection = props.newConnection();
			 Statement stmt = connection.createStatement())
		{
			stmt.setFetchSize(props.fetchSize);
			int queriesCount = 1;
			for(final String query : props.queries)
			{
				final Stats stats = new Stats(out, queriesCount++, query, queriesSize);
				statsSummary.add(stats);
				try(ResultSet resultSet = stmt.executeQuery(query))
				{
					while(resultSet.next())
					{
						final String hash;
						final byte[] value;
						if(queryHash==null)
						{
							hash = resultSet.getString(1);
							value = resultSet.getBytes(2);
						}
						else
						{
							value = resultSet.getBytes(1);
							if(value==null)
							{
								stats.onSkipNullRow();
								stats.onRow();
								continue;
							}
							hash = Hex.encodeLower(queryHash.digest(value));
						}
						try
						{
							if(!service.put(hash, value))
								stats.onRedundant(hash);
						}
						catch(final NullPointerException e)
						{
							stats.onSkip(e);
						}
						catch(final IllegalArgumentException e)
						{
							stats.onSkip(e);
						}
						stats.onRow();
					}
					stats.printFinished();
				}
			}
		}
		Stats.printSummary(out, statsSummary);
	}

	private static final class Stats
	{
		private final PrintStream out;
		private final int queriesCount;
		private final int queriesSize;

		private Stats(
				final PrintStream out,
				final int queriesCount,
				final String query,
				final int queriesSize)
		{
			this.out = out;
			this.queriesCount = queriesCount;
			this.queriesSize = queriesSize;
			out.println("Query " + queriesCount + '/' + queriesSize + " importing: " + query);
		}

		private int row = 0;
		private int skipped = 0;
		private int redundant = 0;

		void onRedundant(final String hash)
		{
			redundant++;
			out.println("Redundant put at row " + row + " for hash " + hash);
		}

		void onSkip(final NullPointerException exception)
		{
			skipped++;
			out.println("Skipping null at row " + row + ": " + exception.getMessage());
		}

		void onSkipNullRow()
		{
			skipped++;
			out.println("Skipping null at row " + row);
		}

		void onSkip(final IllegalArgumentException exception)
		{
			skipped++;
			out.println("Skipping illegal argument at row " + row + ": " + exception.getMessage());
		}

		void onRow()
		{
			row++;
		}

		void printFinished()
		{
			out.println(
					"Finished query " + queriesCount + '/' + queriesSize + " after " + row + " rows, " +
					"skipped " + skipped + ", redundant " + redundant);
		}

		static void printSummary(final PrintStream out, final List<Stats> allStats)
		{
			final int size = allStats.size();
			if(size==1)
				return;

			int row = 0, skipped = 0, redundant = 0;
			for(final Stats stats : allStats)
			{
				row += stats.row;
				skipped += stats.skipped;
				redundant += stats.redundant;
			}
			out.println(
					"Finished " + size + " queries after " + row + " rows, " +
					"skipped " + skipped + ", redundant " + redundant);
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

		final List<String> queries = toList(value("source.query", (String)null));

		// TODO read separate sql file with all queries
		private static List<String> toList(final String s)
		{
			final ArrayList<String> result = new ArrayList<>();
			try(BufferedReader r = new BufferedReader(new StringReader(s)))
			{
				for(String line = r.readLine(); line!=null; line = r.readLine())
					if(!line.isEmpty())
						result.add(line);
			}
			catch(final IOException e)
			{
				throw new RuntimeException(e);
			}
			return List.copyOf(result);
		}

		final boolean queryHash = value("source.queryHash", true);

		MessageDigestFactory queryHash()
		{
			return queryHash
					? null
					: target.bucket(BUCKET).getAlgorithmFactory();
		}

		/**
		 * Stream result sets row-by-row with minimum heap space requirement.
		 * MySQL requires non-standard setting of {@link Integer#MIN_VALUE}, setting to 1 is not sufficient.
		 * See <a href="https://dev.mysql.com/doc/connector-j/8.1/en/connector-j-reference-implementation-notes.html">Docs</a>
		 */
		final int fetchSize = value("source.fetchSize",
				url.startsWith("jdbc:mysql:") ? Integer.MIN_VALUE : 1,
				Integer.MIN_VALUE);

		final VaultProperties target = valnp("target", VaultProperties.factory());

		final Set<String> targetProbesSuppressed = Set.copyOf(valuesSpaceSeparated("targetProbesSuppressed"));

		void probeService(final PrintStream out)
		{
			final String NAME_PREFIX = BUCKET + ".service.";

			for(final Callable<?> probe : target.getProbes())
			{
				final String fullName = probe.toString();
				if(!fullName.startsWith(NAME_PREFIX))
					continue;
				final String name = fullName.substring(NAME_PREFIX.length());

				if(targetProbesSuppressed.contains(name))
				{
					out.println("Probing " + name + " suppressed");
					continue;
				}

				out.println("Probing " + name + " ...");
				try
				{
					final Object result = probe.call();
					out.println("  success" + ((result!=null) ? ": " + result : ""));
				}
				catch(final Properties.ProbeAbortedException e)
				{
					final Object result = e.getMessage();
					out.println("  aborted" + ((result!=null) ? ": " + result : ""));
				}
				catch(final Exception e)
				{
					throw e instanceof RuntimeException
							? (RuntimeException)e
							: new RuntimeException(e);
				}
			}
		}

		Props(final Source source)
		{
			super(source);
		}
	}

	private static final String BUCKET = Vault.DEFAULT;

	private VaultJdbcToService()
	{
		// prevent instantiation
	}
}
