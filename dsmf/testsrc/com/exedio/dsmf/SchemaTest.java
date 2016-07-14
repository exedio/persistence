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

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;

public abstract class SchemaTest
{
	private Dialect dialect;
	String stringType;
	String intType;
	String intType2;
	boolean supportsCheckConstraints;
	boolean hsqldb = false; // TODO remove
	boolean postgresql = false; // TODO remove
	private SimpleConnectionProvider provider;
	private final ArrayList<Connection> connections = new ArrayList<>();

	static final File getDefaultPropertyFile()
	{
		String result = System.getProperty("com.exedio.cope.properties");
		if(result==null)
			result = "cope.properties";

		return new File(result);
	}

	private final class Properties extends com.exedio.cope.util.Properties
	{
		final String connectionUrl      = value      ("connection.url",      (String)null);
		final String connectionUsername = value      ("connection.username", (String)null);
		final String connectionPassword = valueHidden("connection.password", (String)null);
		final String mysqlRowFormat     = value      ("schema.mysql.rowFormat", "NONE");

		@SuppressWarnings("deprecation")
		Properties()
		{
			super(getSource(getDefaultPropertyFile()), SYSTEM_PROPERTY_SOURCE);
		}
	}

	@Before public final void setUpSchemaTest() throws ClassNotFoundException, SQLException
	{
		final Properties config = new Properties();
		final String url = config.connectionUrl;
		final String username = config.connectionUsername;
		final String password = config.connectionPassword;
		final String mysqlRowFormat = config.mysqlRowFormat;

		int numberOfConnections = 1;
		if(url.startsWith("jdbc:hsqldb:"))
		{
			Class.forName("org.hsqldb.jdbcDriver");
			dialect = new HsqldbDialect();
			numberOfConnections = 2;
			stringType = "VARCHAR(8)";
			intType = "INTEGER";
			intType2 = "BIGINT";
			hsqldb = true;
			postgresql = false;
		}
		else if(url.startsWith("jdbc:mysql:"))
		{
			try
			{
				Class.forName("com.mysql.jdbc.Driver");
			}
			catch(final ClassNotFoundException e)
			{
				Class.forName("org.mariadb.jdbc.Driver");
			}
			dialect = new MysqlDialect(
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
			Class.forName("oracle.jdbc.driver.OracleDriver");
			dialect = new OracleDialect(username.toUpperCase());
			stringType = "VARCHAR2(8 BYTE)";
			intType = "NUMBER(12)";
			intType2 = "NUMBER(15)";
			hsqldb = false;
			postgresql = false;
		}
		else if(url.startsWith("jdbc:postgresql:"))
		{
			Class.forName("org.postgresql.Driver");
			dialect = new PostgresqlDialect(username);
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
			connections.add(DriverManager.getConnection(url, username, password));
		provider = new SimpleConnectionProvider(connections);
	}

	@After public final void tearDownSchemaTest() throws SQLException
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
			final Connection result = connections.remove(connections.size()-1);
			return result;
		}

		@Override
		public void putConnection(final Connection connection) throws SQLException
		{
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
}
