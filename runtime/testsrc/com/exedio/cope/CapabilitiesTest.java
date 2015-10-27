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

	public void testSupports()
	{
		final ConnectProperties props = model.getConnectProperties();

		boolean emptyStrings = true;
		boolean utf8mb4 = true;
		boolean random = false;
		boolean checkConstraints = true;
		boolean nativeDate = true;
		boolean notNull = true;
		boolean uniqueViolation = false;

		switch(dialect)
		{
			case hsqldb:
				break;
			case mysql:
				utf8mb4 = props.mysqlUtf8mb4;
				random = true;
				checkConstraints = false;
				nativeDate = false;
				uniqueViolation = true;
				break;
			case oracle:
				emptyStrings = false;
				notNull = false;
				break;
			case postgresql:
				break;
			default:
				fail(dialect.name());
		}

		assertEquals(emptyStrings && !props.isSupportDisabledForEmptyStrings(), model.supportsEmptyStrings());
		assertEquals(utf8mb4, model.supportsUTF8mb4());
		assertEquals(random, model.supportsRandom());

		// SchemaInfo
		assertEquals(checkConstraints, supportsCheckConstraints(model));
		assertEquals(nativeDate      && !props.isSupportDisabledForNativeDate(),      supportsNativeDate     (model));
		assertEquals(notNull,                                                         supportsNotNull        (model));
		assertEquals(uniqueViolation && !props.isSupportDisabledForUniqueViolation(), supportsUniqueViolation(model));
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
