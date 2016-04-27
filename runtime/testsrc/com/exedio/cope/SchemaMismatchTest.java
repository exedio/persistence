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

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.exedio.dsmf.Node;
import com.exedio.dsmf.Node.Color;
import org.junit.After;
import org.junit.Before;

public abstract class SchemaMismatchTest extends TestWithEnvironment
{
	private final Model modelB;

	public SchemaMismatchTest(final Model modelA, final Model modelB)
	{
		super(modelA);
		copeRule.omitTransaction();
		this.modelB = requireNonNull(modelB, "modelB");
	}

	@Before public final void setUpSchemaMismatchTest()
	{
		modelB.connect(model.getConnectProperties());
	}

	@After public final void tearDownSchemaMismatchTest()
	{
		modelB.disconnect();
	}

	protected static final String name(final Type<?> element)
	{
		return SchemaInfo.getTableName(element);
	}

	protected static final String name(final This<?> element)
	{
		return SchemaInfo.getPrimaryKeyColumnName(element.getType());
	}

	protected static final String name(final Field<?> element)
	{
		return SchemaInfo.getColumnName(element);
	}

	protected static final String nameSeq(final This<?> element)
	{
		return SchemaInfo.getPrimaryKeySequenceName(element.getType());
	}

	protected static final void assertIt(
			final String expectedError,
			final Color expectedParticularColor,
			final Color expectedCumulativeColor,
			final Node actual)
	{
		assertNotNull("notNull", actual);
		assertEquals("error", expectedError, actual.getError());
		assertEquals("particularColor", expectedParticularColor, actual.getParticularColor());
		assertEquals("cumulativeColor", expectedCumulativeColor, actual.getCumulativeColor());
	}
}
