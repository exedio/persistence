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

import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.ConnectionRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.tojunit.SI;
import com.exedio.dsmf.Node.Color;
import com.exedio.dsmf.Schema;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Tests schema verification with views present in schema.
 * Needed for "nice views" currently implemented in copeconsole.
 */
@MainRule.Tag
public class SchemaViewTest extends TestWithEnvironment
{
	private final ConnectionRule connection = new ConnectionRule(model);

	public SchemaViewTest()
	{
		super(MODEL);
		copeRule.omitTransaction();
	}

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	@Test void testIt() throws SQLException
	{
		final Schema schema1 = MODEL.getVerifiedSchema();
		assertSame(Color.OK, schema1.getCumulativeColor());

		switch(dialect)
		{
			case mysql:
				connection.executeUpdate(
						"CREATE VIEW " + NAME + " AS " +
						"(SELECT " + SI.col(MyItem.field) +
						" FROM "   + SI.tab(MyItem.TYPE)  + ")");
				break;
			case hsqldb:
			case oracle:
			case postgresql:
				// TODO nothing so far
				break;
			default:
				throw new RuntimeException("" + dialect);
		}

		final Schema schema2 = MODEL.getVerifiedSchema();
		assertSame(Color.OK, schema2.getCumulativeColor());
	}

	@AfterEach void after() throws SQLException
	{
		switch(dialect)
		{
			case mysql:
				connection.executeUpdate(
						"DROP VIEW IF EXISTS " + NAME);
				break;
			case hsqldb:
			case oracle:
			case postgresql:
				// nothing so far
				break;
			default:
				throw new RuntimeException("" + dialect);
		}
	}

	private static final String NAME = "SchemaViewTest";

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static final class MyItem extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		static final IntegerField field = new IntegerField();

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@SuppressWarnings("unused") private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final Model MODEL = new Model(MyItem.TYPE);
}
