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

import static com.exedio.cope.DsmfTestHelper.dialect;
import static com.exedio.cope.DsmfTestHelper.getIntegerType;
import static com.exedio.cope.DsmfTestHelper.getStringType;
import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.WrapperType;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class SchemaTest extends TestWithEnvironment
{
	private Dialect dsmfDialect;
	String stringType;
	String intType;
	String intType2;
	boolean supportsCheckConstraint;
	private SimpleConnectionProvider provider;
	private final ArrayList<Connection> connections = new ArrayList<>();

	@BeforeEach final void setUpSchemaTest() throws SQLException
	{
		dsmfDialect = dialect(MODEL);
		stringType = getStringType(MODEL, 8);
		intType  = getIntegerType(MODEL, 0, Integer.MAX_VALUE);
		intType2 = getIntegerType(MODEL, 0, Long.MAX_VALUE);
		assertNotEquals(intType, intType2);

		supportsCheckConstraint = !dsmfDialect.disableCheckConstraint && dsmfDialect.supportsCheckConstraint();
		final int numberOfConnections =
				"HSQL Database Engine".equals(MODEL.getEnvironmentInfo().getDatabaseProductName()) ? 2 : 1;
		for(int i = 0; i<numberOfConnections; i++)
			connections.add(SchemaInfo.newConnection(MODEL));

		provider = new SimpleConnectionProvider(connections);
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
		return new Schema(dsmfDialect, provider);
	}

	protected final String p(final String name)
	{
		return dsmfDialect.quoteName(name);
	}


	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class SchemaTestItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<SchemaTestItem> TYPE = com.exedio.cope.TypesBound.newType(SchemaTestItem.class,SchemaTestItem::new);

		@com.exedio.cope.instrument.Generated
		private SchemaTestItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
	private static final Model MODEL = new Model(SchemaTestItem.TYPE);
	@SuppressWarnings("unused") // OK: called by junit via reflection
	SchemaTest()
	{
		super(MODEL);
		copeRule.omitTransaction();
	}
}
