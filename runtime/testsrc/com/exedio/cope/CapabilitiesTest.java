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

import static com.exedio.cope.SchemaInfo.supportsCheckConstraints;
import static com.exedio.cope.SchemaInfo.supportsNativeDate;
import static com.exedio.cope.SchemaInfo.supportsNotNull;
import static com.exedio.cope.SchemaInfo.supportsUniqueViolation;

import java.sql.SQLException;

public class CapabilitiesTest extends AbstractRuntimeTest
{
	public CapabilitiesTest()
	{
		super(SchemaTest.MODEL);
		skipTransactionManagement();
	}

	public void test()
	{
		final ConnectProperties props = model.getConnectProperties();

		boolean supportsEmptyStrings = true;
		boolean supportsRandom = false;
		boolean supportsCheckConstraints = true;
		boolean supportsNativeDate = true;
		boolean supportsNotNull = true;
		boolean supportsUniqueViolation = false;

		switch(dialect)
		{
			case hsqldb:
				supportsNotNull = false;
				break;
			case mysql:
				supportsRandom = true;
				supportsCheckConstraints = false;
				supportsNativeDate = false;
				supportsUniqueViolation = true;
				break;
			case oracle:
				supportsEmptyStrings = false;
				supportsNotNull = false;
				break;
			case postgresql:
				break;
			default:
				fail(dialect.name());
		}

		assertEquals(supportsEmptyStrings && !props.isSupportDisabledForEmptyStrings(), model.supportsEmptyStrings());
		assertEquals(supportsRandom, model.supportsRandom());

		// SchemaInfo
		assertEquals(supportsCheckConstraints, supportsCheckConstraints(model));
		assertEquals(supportsNativeDate      && !props.isSupportDisabledForNativeDate(),      supportsNativeDate     (model));
		assertEquals(supportsNotNull         && !props.isSupportDisabledForNotNull(),         supportsNotNull        (model));
		assertEquals(supportsUniqueViolation && !props.isSupportDisabledForUniqueViolation(), supportsUniqueViolation(model));
	}

	public void testSchemaSavepoint()
	{
		switch(dialect)
		{
			case mysql:
				try
				{
					final String result = model.getSchemaSavepoint();
					//System.out.println(result);
					assertNotNull(result);
				}
				catch(final SQLException e)
				{
					// ok
				}
				break;
			default:
				try
				{
					final String result = model.getSchemaSavepoint();
					fail(result);
				}
				catch(final SQLException e)
				{
					assertEquals("not supported", e.getMessage());
				}
				break;
		}
	}

	@Deprecated
	public void testDeprecated()
	{
		assertEquals(true, SchemaInfo.supportsSequences(model));
		assertEquals(true, model.nullsAreSortedLow());
	}
}
