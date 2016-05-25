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

import static com.exedio.cope.CheckConstraintHierarchyItemBottom.TYPE;
import static com.exedio.cope.CheckConstraintHierarchyItemBottom.bottom1;
import static com.exedio.cope.CheckConstraintHierarchyItemBottom.bottom2;
import static com.exedio.cope.CheckConstraintHierarchyItemTop.top1;
import static com.exedio.cope.CheckConstraintHierarchyItemTop.top2;
import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.getTableName;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import com.exedio.dsmf.CheckConstraint;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;
import java.util.Iterator;
import org.junit.Test;

public class CheckConstraintHierarchySchemaTest extends TestWithEnvironment
{
	public CheckConstraintHierarchySchemaTest()
	{
		super(CheckConstraintHierarchyTest.MODEL);
		copeRule.omitTransaction();
	}

	@Test public void testSchema()
	{
		final Schema schema = model.getVerifiedSchema();

		final Table table = schema.getTable(getTableName(TYPE));
		assertNotNull(table);
		assertEquals(null, table.getError());
		assertEquals(Schema.Color.OK, table.getParticularColor());

		final Table superTable = schema.getTable(getTableName(CheckConstraintHierarchyItemTop.TYPE));
		assertNotNull(superTable);
		assertEquals(null, superTable.getError());
		assertEquals(Schema.Color.OK, superTable.getParticularColor());

		{
			final Iterator<Constraint> i = table.getConstraints().iterator();
			assertEquals("ItemBottom_this_MN", next(i).getName());
			assertEquals("ItemBottom_this_MX", next(i).getName());
			assertEquals("ItemBottom_catch_MN", next(i).getName());
			assertEquals("ItemBottom_catch_MX", next(i).getName());
			assertEquals("ItemBottom_bottom1_MN", next(i).getName());
			assertEquals("ItemBottom_bottom1_MX", next(i).getName());
			assertEquals("ItemBottom_bottom2_MN", next(i).getName());
			assertEquals("ItemBottom_bottom2_MX", next(i).getName());
			assertEquals("ItemBottom_cross2_MN", next(i).getName());
			assertEquals("ItemBottom_cross2_MX", next(i).getName());
			assertIt("ItemBottom_bottom", q(bottom1)+"<"+q(bottom2), next(i));
			assertFalse(hasNext(i));
		}
		{
			final Iterator<Constraint> i = superTable.getConstraints().iterator();
			assertEquals("ItemTop_this_MN", next(i).getName());
			assertEquals("ItemTop_this_MX", next(i).getName());
			assertEquals("ItemTop_class_EN", next(i).getName());
			assertEquals("ItemTop_catch_MN", next(i).getName());
			assertEquals("ItemTop_catch_MX", next(i).getName());
			assertEquals("ItemTop_top1_MN", next(i).getName());
			assertEquals("ItemTop_top1_MX", next(i).getName());
			assertEquals("ItemTop_top2_MN", next(i).getName());
			assertEquals("ItemTop_top2_MX", next(i).getName());
			assertEquals("ItemTop_up1_MN", next(i).getName());
			assertEquals("ItemTop_up1_MX", next(i).getName());
			assertEquals("ItemTop_up2_MN", next(i).getName());
			assertEquals("ItemTop_up2_MX", next(i).getName());
			assertEquals("ItemTop_cross1_MN", next(i).getName());
			assertEquals("ItemTop_cross1_MX", next(i).getName());
			assertIt("ItemTop_top", q(top1)+"<"+q(top2), next(i));
			assertFalse(hasNext(i));
		}
	}

	private final String q(final IntegerField f)
	{
		return SchemaInfo.quoteName(model, getColumnName(f));
	}

	private static final CheckConstraint next(final Iterator<Constraint> i)
	{
		Constraint result = null;
		do
		{
			result = i.next();
		}
		while(!(result instanceof CheckConstraint));

		return (CheckConstraint)result;
	}

	private static final boolean hasNext(final Iterator<Constraint> i)
	{
		do
		{
			if(!i.hasNext())
				return false;
			if(i.next() instanceof CheckConstraint)
				return true;
		}
		while(true);
	}

	private static void assertIt(final String name, final String requiredCondition, final CheckConstraint constraint)
	{
		assertEquals(name, constraint.getName());
		assertEquals(requiredCondition, constraint.getRequiredCondition());
	}
}
