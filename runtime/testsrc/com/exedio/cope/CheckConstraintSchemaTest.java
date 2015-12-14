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
import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.getTableName;

import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;

public class CheckConstraintSchemaTest extends AbstractRuntimeModelTest
{
	public CheckConstraintSchemaTest()
	{
		super(CheckConstraintModelTest.MODEL);
	}

	@Override
	protected boolean doesManageTransactions()
	{
		return false;
	}

	@Test public void testMeta()
	{
		final Schema schema = model.getVerifiedSchema();

		final Table table = schema.getTable(getTableName(TYPE));
		assertNotNull(table);
		assertEquals(null, table.getError());
		assertEquals(Schema.Color.OK, table.getParticularColor());

		final Table superTable = schema.getTable(getTableName(CheckConstraintSuperItem.TYPE));
		assertNotNull(superTable);
		assertEquals(null, superTable.getError());
		assertEquals(Schema.Color.OK, superTable.getParticularColor());

		assertCheckConstraint(table, "CheckConstraItem_alpha_Ck", "(("+q(alpha)+" IS NOT NULL) AND (("+q(alpha)+">=-2147483648) AND ("+q(alpha)+"<=2147483647))) OR ("+q(alpha)+" IS NULL)");
		assertCheckConstraint(table, "CheckConsItem_alpLessBeta", q(alpha)+"<"+q(beta));

		assertCheckConstraint(superTable, "CheConSupIte_eiGreOrEquZw", q(eins)+">="+q(zwei));
	}

	private final String q(final IntegerField f)
	{
		return SchemaInfo.quoteName(model, getColumnName(f));
	}
}
