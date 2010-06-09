/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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
import static com.exedio.cope.CheckConstraintItem.alphaLessBeta;
import static com.exedio.cope.CheckConstraintItem.beta;
import static com.exedio.cope.CheckConstraintItem.delta;
import static com.exedio.cope.CheckConstraintItem.gamma;
import static com.exedio.cope.CheckConstraintSuperItem.drei;
import static com.exedio.cope.CheckConstraintSuperItem.eins;
import static com.exedio.cope.CheckConstraintSuperItem.einsGreaterOrEqualZwei;
import static com.exedio.cope.CheckConstraintSuperItem.zwei;
import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.getTableName;

import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;

public class CheckConstraintTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(CheckConstraintItem.TYPE, CheckConstraintSuperItem.TYPE);
	
	static
	{
		MODEL.enableSerialization(CheckConstraintTest.class, "MODEL");
	}
	
	public CheckConstraintTest()
	{
		super(MODEL);
	}

	public void testItemWithSingleUnique()
	{
		// test model
		assertEqualsUnmodifiable(
			list(
				TYPE.getThis(),
				alpha, beta, gamma, delta,
				alphaLessBeta),
			TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(
			list(
				TYPE.getThis(),
				eins, zwei, drei,
				einsGreaterOrEqualZwei,
				alpha, beta, gamma, delta,
				alphaLessBeta),
			TYPE.getFeatures());
		assertEqualsUnmodifiable(
			list(
				alphaLessBeta),
			TYPE.getDeclaredCheckConstraints());
		assertEqualsUnmodifiable(
			list(
				einsGreaterOrEqualZwei,
				alphaLessBeta),
			TYPE.getCheckConstraints());
		assertEqualsUnmodifiable(
			list(
				CheckConstraintSuperItem.TYPE.getThis(),
				eins, zwei, drei,
				einsGreaterOrEqualZwei),
			CheckConstraintSuperItem.TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(
			list(
				CheckConstraintSuperItem.TYPE.getThis(),
				eins, zwei, drei,
				einsGreaterOrEqualZwei),
			CheckConstraintSuperItem.TYPE.getFeatures());
		assertEqualsUnmodifiable(
			list(
				einsGreaterOrEqualZwei),
			CheckConstraintSuperItem.TYPE.getDeclaredCheckConstraints());
		assertEqualsUnmodifiable(
			list(
				einsGreaterOrEqualZwei),
			CheckConstraintSuperItem.TYPE.getCheckConstraints());
		
		try
		{
			new CheckConstraint(null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("condition", e.getMessage());
		}
		try
		{
			new CheckConstraint(Condition.TRUE);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("literal condition makes no sense, but was Condition.TRUE", e.getMessage());
		}
		try
		{
			new CheckConstraint(Condition.FALSE);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("literal condition makes no sense, but was Condition.FALSE", e.getMessage());
		}
		
		assertSerializedSame(alphaLessBeta, 393);
		
		// test schema
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
	}
	
	private final String q(final IntegerField f)
	{
		return SchemaInfo.quoteName(model, getColumnName(f));
	}
}
