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

import static com.exedio.cope.CheckConstraintItem.TYPE;
import static com.exedio.cope.CheckConstraintItem.alpha;
import static com.exedio.cope.CheckConstraintItem.beta;
import static com.exedio.cope.CheckConstraintSuperItem.eins;
import static com.exedio.cope.CheckConstraintSuperItem.zwei;
import static com.exedio.cope.SchemaInfo.getTableName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.exedio.cope.tojunit.SI;
import com.exedio.dsmf.Node;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;
import org.junit.jupiter.api.Test;

public class CheckConstraintSchemaTest extends TestWithEnvironment
{
	public CheckConstraintSchemaTest()
	{
		super(CheckConstraintModelTest.MODEL);
		copeRule.omitTransaction();
	}

	@Test void testMeta()
	{
		final Schema schema = model.getVerifiedSchema();

		final Table table = schema.getTable(getTableName(TYPE));
		assertNotNull(table);
		assertEquals(null, table.getError());
		assertEquals(Node.Color.OK, table.getParticularColor());

		final Table superTable = schema.getTable(getTableName(CheckConstraintSuperItem.TYPE));
		assertNotNull(superTable);
		assertEquals(null, superTable.getError());
		assertEquals(Node.Color.OK, superTable.getParticularColor());

		assertCheckConstraint(table, "Main_alpha_MN"   , SI.col(alpha)+">=-2147483648");
		assertCheckConstraint(table, "Main_alpha_MX"   , SI.col(alpha)+"<=2147483647");
		assertCheckConstraint(table, "Main_alphaToBeta", SI.col(alpha)+"<"+SI.col(beta));

		assertCheckConstraint(superTable, "Super_einsToZwei", SI.col(eins)+">="+SI.col(zwei));
	}
}
