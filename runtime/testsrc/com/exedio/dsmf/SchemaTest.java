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

import static com.exedio.cope.ConnectProperties.getDefaultPropertyFile;

import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.tojunit.ModelConnector;
import com.exedio.cope.util.Sources;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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

	private static final class Properties extends com.exedio.cope.util.Properties
	{
		final String connectionUrl      = value      ("connection.url",      (String)null);
		final String connectionUsername = value      ("connection.username", (String)null);
		final String connectionPassword = valueHidden("connection.password", null);
		final String mysqlRowFormat     = value      ("dialect.rowFormat", "NONE");
		final boolean mysql80           = value      ("x-build.mysql80", false);
		final String connectionPostgresqlSchema = value("dialect.connection.schema", connectionUsername);

		Properties()
		{
			super(Sources.load(getDefaultPropertyFile()));
		}
	}

	@BeforeEach final void setUpSchemaTest() throws SQLException, ReflectiveOperationException
	{
		ModelConnector.reset();
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
			dialect = newD("Hsqldb", true);
			numberOfConnections = 2;
			stringType = "VARCHAR(8)";
			intType = "INTEGER";
			intType2 = "BIGINT";
			hsqldb = true;
			postgresql = false;
		}
		else if(url.startsWith("jdbc:mysql:")||url.startsWith("jdbc:mariadb:"))
		{
			// see MysqlDialect#completeConnectionInfo
			info.setProperty("allowMultiQueries", "true"); // needed for creating Sequence
			info.setProperty("sslMode", "DISABLED");
			info.setProperty("serverTimezone", "UTC");
			info.setProperty("allowLoadLocalInfile", "false"); // MySQL driver
			info.setProperty("allowLocalInfile", "false"); // MariaDB driver
			dialect = newD("Mysql",
					config.mysql80,
					"NONE".equals(mysqlRowFormat) ? null : mysqlRowFormat);
			stringType = "varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin";
			intType = "int";
			intType2 = "bigint";
			hsqldb = false;
			postgresql = false;
		}
		else if(url.startsWith("jdbc:postgresql:"))
		{
			dialect = newD("Postgresql", config.connectionPostgresqlSchema);
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
					// https://www.postgresql.org/docs/9.6/runtime-config-client.html#GUC-SEARCH-PATH
					st.execute("SET SCHEMA '" + config.connectionPostgresqlSchema + "'");
				}
			}
		}

		provider = new SimpleConnectionProvider(connections);
	}

	private static Dialect newD(final String name, final Object... initargs) throws ReflectiveOperationException
	{
		final Class<?>[] parameterTypes = new Class<?>[initargs.length];
		for(int i = 0; i<parameterTypes.length; i++)
		{
			Class<?> clazz = initargs[i]!=null ? initargs[i].getClass() : String.class;
			if(clazz==Boolean.class)
				clazz=boolean.class;
			parameterTypes[i] = clazz;
		}
		final Constructor<? extends Dialect> c =
				Class.forName("com.exedio.cope." + name + "SchemaDialect").
				asSubclass(Dialect.class).
				getDeclaredConstructor(parameterTypes);
		c.setAccessible(true);
		return c.newInstance(initargs);
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
