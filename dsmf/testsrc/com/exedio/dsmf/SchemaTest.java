/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public abstract class SchemaTest extends TestCase
{
	private Driver driver;
	String stringType;
	String intType;
	String intType2;
	boolean supportsCheckConstraints;
	boolean postgresql = false; // TODO remove
	private SimpleConnectionProvider provider;
	Connection connection1; // visible for BatchTest
	private Connection connection2;
	
	static final File getDefaultPropertyFile()
	{
		String result = System.getProperty("com.exedio.cope.properties");
		if(result==null)
			result = "cope.properties";

		return new File(result);
	}

	private final class Properties extends com.exedio.cope.util.Properties
	{
		final StringField databaseUrl =  new StringField("database.url");
		final StringField databaseUser =  new StringField("database.user");
		final StringField databasePassword =  new StringField("database.password", true);

		Properties()
		{
			this(getDefaultPropertyFile(), getSystemPropertySource());
		}
		
		Properties(final File file, final Context context)
		{
			super(loadProperties(file), file.getAbsolutePath(), context);
		}
	}
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		
		final Properties config = new Properties();
		final String url = config.databaseUrl.getStringValue();
		final String user = config.databaseUser.getStringValue();
		final String password = config.databasePassword.getStringValue();
		
		if(url.startsWith("jdbc:hsqldb:"))
		{
			Class.forName("org.hsqldb.jdbcDriver");
			driver = new HsqldbDriver();
			stringType = "varchar(8)";
			intType = "integer";
			intType2 = null;
			postgresql = false;
		}
		else if(url.startsWith("jdbc:mysql:"))
		{
			Class.forName("com.mysql.jdbc.Driver");
			driver = new MysqlDriver("this");
			stringType = "varchar(8) character set utf8 binary";
			intType = "integer";
			intType2 = "bigint";
			postgresql = false;
		}
		else if(url.startsWith("jdbc:oracle:"))
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			driver = new OracleDriver(user.toUpperCase());
			stringType = "VARCHAR2(8 BYTE)";
			intType = "NUMBER(12)";
			intType2 = "NUMBER(15)";
			postgresql = false;
		}
		else if(url.startsWith("jdbc:postgresql:"))
		{
			Class.forName("org.postgresql.Driver");
			driver = new PostgresqlDriver();
			stringType = "VARCHAR(8)";
			intType = "INTEGER";
			intType2 = null;
			postgresql = true;
		}
		else
			throw new RuntimeException(url);
		
		supportsCheckConstraints = driver.supportsCheckConstraints();
		connection1 = DriverManager.getConnection(url, user, password);
		connection2 = DriverManager.getConnection(url, user, password);
		provider = new SimpleConnectionProvider(Arrays.asList(new Connection[]{connection1, connection2}));
	}
	
	@Override
	public void tearDown() throws Exception
	{
		if(connection1!=null)
			connection1.close();
		if(connection2!=null)
			connection2.close();

		super.tearDown();
	}
	
	private static final class SimpleConnectionProvider implements ConnectionProvider
	{
		final ArrayList<Connection> connections;
		
		SimpleConnectionProvider(final List<Connection> connections) throws SQLException
		{
			this.connections = new ArrayList<Connection>(connections);
			for(Connection c : connections)
				c.setAutoCommit(true);
		}

		public Connection getConnection()
		{
			final Connection result = connections.remove(connections.size()-1);
			return result;
		}

		public void putConnection(final Connection connection) throws SQLException
		{
			assert connection.getAutoCommit()==true;
			connections.add(connection);
		}
	}
	
	protected final Schema newSchema()
	{
		return new Schema(driver, provider);
	}
	
	protected final String p(final String name)
	{
		return driver.protectName(name);
	}
	
}
