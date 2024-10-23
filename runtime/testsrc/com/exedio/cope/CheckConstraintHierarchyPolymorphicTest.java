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

import static com.exedio.cope.CheckConstraintTest.assertFailsCheck;
import static com.exedio.cope.SchemaInfo.getPrimaryKeyColumnValueL;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.supportsCheckConstraint;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.ConnectionRule;
import com.exedio.cope.tojunit.SI;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Node;
import com.exedio.dsmf.Schema;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

public class CheckConstraintHierarchyPolymorphicTest extends TestWithEnvironment
{
	public CheckConstraintHierarchyPolymorphicTest()
	{
		super(MODEL);
	}

	/**
	 * @see #testTopViolate()
	 */
	@Test void testTop()
	{
		final Top item = new Top("value");
		assertEquals("value", item.getField());

		item.setField(null);
		assertEquals(null, item.getField());
	}

	/**
	 * @see #testMiddleViolate()
	 */
	@Test void testMiddle()
	{
		final Middle item = new Middle("value");
		assertEquals("value", item.getField());

		assertFailsCheck(
				() -> item.setField(null),
				item,
				Middle.constraint);
		assertEquals("value", item.getField());
	}

	/**
	 * @see #testBottomViolate()
	 */
	@Test void testBottom()
	{
		final Bottom item = new Bottom("value");
		assertEquals("value", item.getField());

		assertFailsCheck(
				() -> item.setField(null),
				item,
				Middle.constraint);
		assertEquals("value", item.getField());
	}

	@Test void testSchema()
	{
		model.commit();

		assertEquals(true, Middle.constraint.isSupportedBySchemaIfSupportedByDialect());

		final Schema schema = model.getVerifiedSchema();

		final com.exedio.dsmf.Table topTable = schema.getTable(getTableName(Top.TYPE));
		assertNotNull(topTable);
		assertEquals(null, topTable.getError());
		assertEquals(Node.Color.OK, topTable.getParticularColor());

		final Constraint constraint = topTable.getConstraint(Middle.CONSTRAINT_NAME);
		assertNotNull(constraint);
		assertEquals(
				"("+SI.type(Top.TYPE)+" NOT IN ('Middle','Bottom')) OR ("+SI.col(Top.field)+" IS NOT NULL)",
				constraint.getRequiredCondition());
		assertEquals(supportsCheckConstraint(model)?null:"unsupported", constraint.getError());
		assertEquals(Node.Color.OK, constraint.getParticularColor());
	}


	/**
	 * @see #testTop()
	 */
	@Test void testTopViolate() throws SQLException
	{
		final Top item = new Top("value");
		assertEquals(0, Middle.constraint.check());
		commit();

		final String sql = sql(item);
		assertEquals(1, connection.executeUpdate(sql));
		startTransaction();
		assertEquals(0, Middle.constraint.check());
	}

	/**
	 * @see #testMiddle()
	 */
	@Test void testMiddleViolate() throws SQLException
	{
		final Middle item = new Middle("value");
		assertEquals(0, Middle.constraint.check());
		commit();

		final String sql = sql(item);
		if(supportsCheckConstraint(model))
		{
			assertFailsSql(
					() -> connection.execute(sql),
					checkViolation());
		}
		else
		{
			assertEquals(1, connection.executeUpdate(sql));
		}
		startTransaction();
		assertEquals(supportsCheckConstraint(model)?0:1, Middle.constraint.check());
	}

	/**
	 * @see #testBottom()
	 */
	@Test void testBottomViolate() throws SQLException
	{
		final Bottom item = new Bottom("value");
		assertEquals(0, Middle.constraint.check());
		commit();

		final String sql = sql(item);
		if(supportsCheckConstraint(model))
		{
			assertFailsSql(
					() -> connection.execute(sql),
					checkViolation());
		}
		else
		{
			assertEquals(1, connection.executeUpdate(sql));
		}
		startTransaction();
		assertEquals(supportsCheckConstraint(model)?0:1, Middle.constraint.check());
	}

	private static String sql(final Top item)
	{
		return
				"UPDATE " + SI.tab(Top.TYPE) + " " +
				"SET " + SI.col(Top.field) + "=NULL " +
				"WHERE " + SI.pk(Top.TYPE) + "=" + getPrimaryKeyColumnValueL(item);
	}

	private void assertFailsSql(
			final Executable executable,
			final String message)
	{
		assertEquals(message, dropMariaConnectionId(assertThrows(SQLException.class, executable).getMessage()));
	}

	private String checkViolation()
	{
		return checkViolationMessage(getTableName(Top.TYPE), Middle.CONSTRAINT_NAME);
	}

	private final ConnectionRule connection = new ConnectionRule(model);


	@WrapperType(indent=2, comments=false)
	private static class Top extends Item
	{
		@WrapperInitial
		static final StringField field = new StringField().optional();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private Top(
					@javax.annotation.Nullable final java.lang.String field)
				throws
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(Top.field,field),
			});
		}

		@com.exedio.cope.instrument.Generated
		protected Top(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		final java.lang.String getField()
		{
			return Top.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		final void setField(@javax.annotation.Nullable final java.lang.String field)
				throws
					com.exedio.cope.StringLengthViolationException
		{
			Top.field.set(this,field);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Top> TYPE = com.exedio.cope.TypesBound.newType(Top.class,Top::new);

		@com.exedio.cope.instrument.Generated
		protected Top(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static class Middle extends Top
	{
		static final CheckConstraint constraint = new CheckConstraint(field.isNotNull());

		static final String CONSTRAINT_NAME = "Middle_constraint"; // NOTE: Divergent name prefix, see notes in CheckConstraint#makeSchema

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private Middle(
					@javax.annotation.Nullable final java.lang.String field)
				throws
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(com.exedio.cope.CheckConstraintHierarchyPolymorphicTest.Top.field,field),
			});
		}

		@com.exedio.cope.instrument.Generated
		protected Middle(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Middle> TYPE = com.exedio.cope.TypesBound.newType(Middle.class,Middle::new);

		@com.exedio.cope.instrument.Generated
		protected Middle(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static final class Bottom extends Middle
	{
		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private Bottom(
					@javax.annotation.Nullable final java.lang.String field)
				throws
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(com.exedio.cope.CheckConstraintHierarchyPolymorphicTest.Top.field,field),
			});
		}

		@com.exedio.cope.instrument.Generated
		private Bottom(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Bottom> TYPE = com.exedio.cope.TypesBound.newType(Bottom.class,Bottom::new);

		@com.exedio.cope.instrument.Generated
		private Bottom(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(Top.TYPE, Middle.TYPE, Bottom.TYPE);

	static
	{
		MODEL.enableSerialization(CheckConstraintHierarchyPolymorphicTest.class, "MODEL");
	}
}
