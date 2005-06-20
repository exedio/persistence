/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

public abstract class SchemaTest extends TestCase
{
	private Driver driver;
	String stringType;
	String intType;
	boolean supportsCheckConstraints = true;
	private SimpleConnectionProvider provider;
	private Connection connection1;
	private Connection connection2;
	
	public void setUp() throws Exception
	{
		super.setUp();
		
		final Properties config = new Properties();
		config.load(new FileInputStream(System.getProperty("com.exedio.cope.properties")));
		final String database = config.getProperty("database");
		final String url = config.getProperty("database.url");
		final String user = config.getProperty("database.user");
		final String password = config.getProperty("database.password");
		
		if("hsqldb".equals(database))
		{
			Class.forName("org.hsqldb.jdbcDriver");
			driver = new HsqldbDriver();
			stringType = "varchar(8)";
			intType = "integer";
		}
		else if("mysql".equals(database))
		{
			Class.forName("com.mysql.jdbc.Driver");
			driver = new MysqlDriver("this");
			stringType = "varchar(8) binary";
			intType = "integer";
			supportsCheckConstraints = false;
		}
		else if("oracle".equals(database))
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
			driver = new OracleDriver(user.toUpperCase());
			stringType = "VARCHAR2(8)";
			intType = "NUMBER(12)";
		}
		else
			throw new RuntimeException(database);
		
		connection1 = DriverManager.getConnection(url, user, password);
		connection2 = DriverManager.getConnection(url, user, password);
		provider = new SimpleConnectionProvider(Arrays.asList(new Connection[]{connection1, connection2}));
	}
	
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
		final ArrayList connections;
		
		SimpleConnectionProvider(final List connections)
		{
			this.connections = new ArrayList(connections);
		}

		public Connection getConnection() throws SQLException
		{
			final Connection result =  (Connection)connections.get(connections.size()-1);
			connections.remove(connections.size()-1);
			return result;
		}

		public void putConnection(final Connection connection) throws SQLException
		{
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
