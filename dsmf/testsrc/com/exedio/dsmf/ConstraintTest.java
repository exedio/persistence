/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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


public class ConstraintTest extends SchemaTest
{
	private static final Class CHECK = CheckConstraint.class;
	private static final Class PK = PrimaryKeyConstraint.class;
	private static final Class FK = ForeignKeyConstraint.class;
	private static final Class UNIQUE = UniqueConstraint.class;
	
	protected Schema getSchema()
	{
		final Schema result = newSchema();
		{
			final Table table = new Table(result, "AttributeItem");

			if(supportsCheckConstraints)
			{
				new Column(table, "someNotNullString", stringType);
				new CheckConstraint(table, "AttrItem_somNotNullStr_Ck", p("someNotNullString")+" IS NOT NULL");
				
				new Column(table, "someNotNullBoolean", stringType);
				new CheckConstraint(table, "AttrItem_somNotNullBoo_Ck", "("+p("someNotNullBoolean")+" IS NOT NULL) AND ("+p("someNotNullBoolean")+" IN (0,1))");
			}
			
			new Column(table, "this", stringType);
			new PrimaryKeyConstraint(table, "AttributeItem_Pk", "this");
			
			new Column(table, "someItem", stringType);
			{
				final Table targetTable = new Table(result, "EmptyItem");
				new Column(targetTable, "thus", stringType);
				new PrimaryKeyConstraint(targetTable, "EmptyItem_Pk", "thus");
			}
			new ForeignKeyConstraint(table, "AttributeItem_someItem_Fk", "someItem", "EmptyItem", "thus");
		}
		{
			final Table table = new Table(result, "ItemWithSingleUnique");
			new Column(table, "uniqueString", stringType);
			new UniqueConstraint(table, "ItemWithSingUni_unStr_Unq", "("+p("uniqueString")+")");
		}
		{
			final Table table = new Table(result, "ItemWithDoubleUnique");
			new Column(table, "string", stringType);
			new Column(table, "integer", intType);
			new UniqueConstraint(table, "ItemWithDoubUni_doUni_Unq", "("+p("string")+","+p("integer")+")");
		}
		return result;
	}
	
	public void testConstraints()
	{
		final Schema schema = getVerifiedSchema();

		final Table attributeItem = schema.getTable("AttributeItem");
		assertNotNull(attributeItem);
		assertEquals(null, attributeItem.getError());
		assertEquals(Schema.COLOR_OK, attributeItem.getParticularColor());
		
		assertCheckConstraint(attributeItem, "AttrItem_somNotNullStr_Ck", p("someNotNullString")+" IS NOT NULL");
		assertCheckConstraint(attributeItem, "AttrItem_somNotNullBoo_Ck", "("+p("someNotNullBoolean")+" IS NOT NULL) AND ("+p("someNotNullBoolean")+" IN (0,1))");
		assertPkConstraint(attributeItem, "AttributeItem_Pk", null, "this");
		assertFkConstraint(attributeItem, "AttributeItem_someItem_Fk", "someItem", "EmptyItem", "thus");

		final Table uniqueItem = schema.getTable("ItemWithSingleUnique");
		assertNotNull(uniqueItem);
		assertEquals(null, uniqueItem.getError());
		assertEquals(Schema.COLOR_OK, uniqueItem.getParticularColor());
		
		assertUniqueConstraint(uniqueItem, "ItemWithSingUni_unStr_Unq", "("+p("uniqueString")+")");
		
		final Table doubleUniqueItem = schema.getTable("ItemWithDoubleUnique");
		assertNotNull(doubleUniqueItem);
		assertEquals(null, doubleUniqueItem.getError());
		assertEquals(Schema.COLOR_OK, doubleUniqueItem.getParticularColor());
		
		assertUniqueConstraint(doubleUniqueItem, "ItemWithDoubUni_doUni_Unq", "("+p("string")+","+p("integer")+")");
	}

	private void assertCheckConstraint(final Table table, final String constraintName, final String requiredCondition)
	{
		final CheckConstraint constraint =
			(CheckConstraint)assertConstraint(table, CHECK, constraintName, requiredCondition);
	}
	
	private void assertPkConstraint(final Table table, final String constraintName, final String requiredCondition, final String primaryKeyColumn)
	{
		final PrimaryKeyConstraint constraint =
			(PrimaryKeyConstraint)assertConstraint(table, PK, constraintName, requiredCondition);

		assertEquals(primaryKeyColumn, constraint.getPrimaryKeyColumn());
	}
	
	private void assertFkConstraint(final Table table, final String constraintName, final String foreignKeyColumn, final String targetTable, final String targetColumn)
	{
		final ForeignKeyConstraint constraint =
			(ForeignKeyConstraint)assertConstraint(table, FK, constraintName, null);

		assertEquals(foreignKeyColumn, constraint.getForeignKeyColumn());
		assertEquals(targetTable, constraint.getTargetTable());
		assertEquals(targetColumn, constraint.getTargetColumn());
	}
	
	private void assertUniqueConstraint(final Table table, final String constraintName, final String clause)
	{
		final UniqueConstraint constraint =
			(UniqueConstraint)assertConstraint(table, UNIQUE, constraintName, clause);

		assertEquals(clause, constraint.getClause());
	}
	
	private Constraint assertConstraint(final Table table, final Class constraintType, final String constraintName, final String requiredCondition)
	{
		final Constraint constraint = table.getConstraint(constraintName);
		if(supportsCheckConstraints || constraintType!=CHECK)
		{
			assertNotNull("no such constraint "+constraintName+", but has "+table.getConstraints(), constraint);
			assertEquals(constraintName, constraintType, constraint.getClass());
			assertEquals(constraintName, requiredCondition, constraint.getRequiredCondition());
			assertEquals(constraintName, null, constraint.getError());
			assertEquals(constraintName, Schema.COLOR_OK, constraint.getParticularColor());
		}
		else
			assertEquals(constraintName, null, constraint);

		return constraint;
	}

}
