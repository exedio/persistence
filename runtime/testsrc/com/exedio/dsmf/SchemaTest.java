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

package com.exedio.dsmf;

import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.util.Sources;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

@TestWithEnvironment.Tag
public abstract class SchemaTest
{
	private Dialect dialect;
	String stringType;
	String intType;
	String intType2;
	boolean supportsCheckConstraints;
	private boolean hsqldb = false;
	private boolean postgresql = false;
	private SimpleConnectionProvider provider;
	private final ArrayList<Connection> connections = new ArrayList<>();

	static final File getDefaultPropertyFile()
	{
		String result = System.getProperty("com.exedio.cope.properties");
		if(result==null)
			result = "cope.properties";

		return new File(result);
	}

	private static final class Properties extends com.exedio.cope.util.Properties
	{
		final String connectionUrl      = value      ("connection.url",      (String)null);
		final String connectionUsername = value      ("connection.username", (String)null);
		final String connectionPassword = valueHidden("connection.password", (String)null);
		final String mysqlRowFormat     = value      ("dialect.rowFormat", "NONE");
		final String connectionPostgresqlSearchPath = value("dialect.search_path", connectionUsername);

		Properties()
		{
			super(Sources.load(getDefaultPropertyFile()));
		}
	}

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	@BeforeEach final void setUpSchemaTest() throws SQLException, ReflectiveOperationException
	{
		final Properties config = new Properties();
		final String url = config.connectionUrl;
		final String username = config.connectionUsername;
		final String password = config.connectionPassword;
		final String mysqlRowFormat = config.mysqlRowFormat;
		final java.util.Properties info = new java.util.Properties();
		info.setProperty("user", username);
		info.setProperty("password", password);

		int numberOfConnections = 1;
		if(url.startsWith("jdbc:hsqldb:"))
		{
			// see HsqldbDialect#completeConnectionInfo
			info.setProperty("hsqldb.tx", "mvcc"); // fixes sparse dead locks when running tests, mostly on travis-ci
			dialect = newD("HsqldbDialect", true);
			numberOfConnections = 2;
			stringType = "VARCHAR(8)";
			intType = "INTEGER";
			intType2 = "BIGINT";
			hsqldb = true;
			postgresql = false;
		}
		else if(url.startsWith("jdbc:mysql:"))
		{
			// see MysqlDialect#completeConnectionInfo
			info.setProperty("allowMultiQueries", "true"); // needed for creating Sequence
			info.setProperty("useSSL", "false");
			dialect = newD("MysqlDialect",
					false, // TODO test true as well
					"CopeSequenceAutoIncrementColumnForTest",
					"NONE".equals(mysqlRowFormat) ? null : mysqlRowFormat);
			stringType = "varchar(8) CHARACTER SET utf8 COLLATE utf8_bin";
			intType = "int";
			intType2 = "bigint";
			hsqldb = false;
			postgresql = false;
		}
		else if(url.startsWith("jdbc:oracle:"))
		{
			dialect = newD("OracleDialect", username.toUpperCase(Locale.ENGLISH));
			stringType = "VARCHAR2(8 BYTE)";
			intType = "NUMBER(12)";
			intType2 = "NUMBER(15)";
			hsqldb = false;
			postgresql = false;
		}
		else if(url.startsWith("jdbc:postgresql:"))
		{
			dialect = newD("PostgresqlDialect", config.connectionPostgresqlSearchPath, false);
			stringType = "character varying(8)";
			intType  = "integer";
			intType2 = "bigint";
			hsqldb = false;
			postgresql = true;
		}
		else
			throw new RuntimeException(url);

		supportsCheckConstraints = dialect.supportsCheckConstraints();
		for(int i = 0; i<numberOfConnections; i++)
			connections.add(DriverManager.getConnection(url, info));

		if(postgresql)
		{
			for(final Connection connection : connections)
			{
				try(java.sql.Statement st = connection.createStatement())
				{
					// http://www.postgresql.org/docs/9.3/interactive/runtime-config-client.html#GUC-SEARCH-PATH
					st.execute("SET search_path TO " + config.connectionPostgresqlSearchPath);
				}
			}
		}

		provider = new SimpleConnectionProvider(connections);
	}

	private static Dialect newD(final String simpleName, final Object... initargs) throws ReflectiveOperationException
	{
		final Class<?>[] parameterTypes = new Class<?>[initargs.length];
		for(int i = 0; i<parameterTypes.length; i++)
		{
			Class<?> clazz = initargs[i]!=null ? initargs[i].getClass() : String.class;
			if(clazz==Boolean.class)
				clazz=boolean.class;
			parameterTypes[i] = clazz;
		}
		return (Dialect)
				Class.forName("com.exedio.dsmf." + simpleName).
				getConstructor(parameterTypes).
				newInstance(initargs);
	}

	@AfterEach final void tearDownSchemaTest() throws SQLException
	{
		for(final Connection connection : connections)
			connection.close();
	}

	private static final class SimpleConnectionProvider implements ConnectionProvider
	{
		final ArrayList<Connection> connections;

		SimpleConnectionProvider(final List<Connection> connections) throws SQLException
		{
			this.connections = new ArrayList<>(connections);
			for(final Connection c : connections)
				c.setAutoCommit(true);
		}

		@Override
		public Connection getConnection()
		{
			return connections.remove(connections.size()-1);
		}

		@Override
		public void putConnection(final Connection connection) throws SQLException
		{
			//noinspection PointlessBooleanExpression
			assert connection.getAutoCommit()==true;
			connections.add(connection);
		}

		@Override
		public boolean isSemicolonEnabled()
		{
			return false;
		}
	}

	protected final Schema newSchema()
	{
		return new Schema(dialect, provider);
	}

	protected final String p(final String name)
	{
		return dialect.quoteName(name);
	}

	protected final String hp(final String s)
	{
		if(hsqldb)
			return "(" + s + ")";
		else
			return s;
	}

	/**
	 * space after comma
	 */
	protected final String sac()
	{
		return postgresql ? " " : "";
	}
}
