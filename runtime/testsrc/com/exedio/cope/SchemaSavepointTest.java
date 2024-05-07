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

import static com.exedio.cope.ConnectProperties.getDefaultPropertyFile;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.util.Properties;
import com.exedio.cope.util.Sources;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SchemaSavepointTest extends TestWithEnvironment
{
	public SchemaSavepointTest()
	{
		super(MODEL);
		copeRule.omitTransaction();
	}

	private ConnectProperties previousConnectProperties;

	@Test void test() throws SQLException
	{
		if(mysql)
		{
			assertFails(
					() -> getSchemaSavepoint(model),
					SQLSyntaxErrorException.class,
					"Access denied; you need (at least one of) the " +
					"SUPER, REPLICATION CLIENT" +
					" privilege(s) for this operation",
					this::dropMariaConnectionId);
			assertFails(
					model::getSchemaSavepointNew,
					SchemaSavepointNotAvailableException.class,
					"Access denied; you need the " +
					"REPLICATION CLIENT privilege for this operation");
			final ConnectProperties cprops = model.getConnectProperties();
			final Properties.Source props = cprops.getSourceObject();
			model.disconnect();
			final String USERNAME = "connection.username";
			model.connect(ConnectProperties.create(cascade(
					single(USERNAME, props.get(USERNAME) + "_replication_client"),
					props)));
			previousConnectProperties = cprops;
		}
		final String expected = new Props().schemaSavepoint;
		try
		{
			assertMatches(expected, "OK: " + getSchemaSavepoint(model));
		}
		catch(final SQLException e)
		{
			assertMatches(expected, "FAILS: " + e.getMessage());
		}
		try
		{
			assertMatches(expected, "OK: " + model.getSchemaSavepointNew());
		}
		catch(final SchemaSavepointNotAvailableException e)
		{
			assertMatches(expected, "FAILS: " + e.getMessage());
		}
	}

	@AfterEach void tearDown()
	{
		if(previousConnectProperties!=null)
		{
			model.rollbackIfNotCommitted();
			model.disconnect();
			model.connect(previousConnectProperties);
			previousConnectProperties = null;
		}
	}

	private void assertMatches(
			final String expected,
			final String actual)
	{
		if(!NOT_SUPPORTED.equals(expected) ||
			!NOT_SUPPORTED.equals(actual))
		{
			System.out.println(SchemaSavepointTest.class.getName());
			System.out.println("---" + expected + "---");
			System.out.println("---" + actual   + "---");
		}
		assertNotNull(expected);
		assertNotNull(actual);
		assertTrue(actual.matches(expected), () -> "---" + expected + "---" + actual + "---");
	}

	private final class Props extends Properties
	{
		final String schemaSavepoint = value("x-build.schemasavepoint", NOT_SUPPORTED);

		Props()
		{
			super(Sources.load(getDefaultPropertyFile()));
		}
	}

	private String NOT_SUPPORTED;

	@BeforeEach void setUp()
	{
		NOT_SUPPORTED = "FAILS: not supported by " + model.getConnectProperties().getDialect();
	}


	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class MyItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(MyItem.TYPE);

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	static final String getSchemaSavepoint(final Model model) throws SQLException
	{
		return model.getSchemaSavepoint();
	}
}
