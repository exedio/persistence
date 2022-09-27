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

import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.supportsCheckConstraints;
import static com.exedio.cope.UpdateCounterInvalidTest.MyItem.TYPE;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.tojunit.ConnectionRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.tojunit.SI;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public final class UpdateCounterInvalidTest extends TestWithEnvironment
{
	public UpdateCounterInvalidTest()
	{
		super(MODEL);
	}

	private final ConnectionRule connection = new ConnectionRule(model);

	@Test void test() throws SQLException
	{
		final MyItem item = new MyItem();
		assertEquals(true, item.existsCopeItem());
		commit();

		if(supportsCheckConstraints(model))
			// this model is used just by this test,
			// therefore we don't have to repair the schema
			// (recreate the check constraint)
			model.
					getSchema().
					getTable(getTableName(TYPE)).
					getConstraint("MyItem_catch_MN").
					drop();

		execute(
				"update " + SI.tab(TYPE) +
				" set " + SI.update(TYPE) + "=-1");
		startTransaction();
		assertFails(
				item::existsCopeItem,
				IllegalStateException.class,
				"update counter must be positive: " +
				SI.tab(TYPE) + "." + SI.update(TYPE) + "=-1 where " + SI.pk(TYPE) + "=0");
	}

	@com.exedio.cope.instrument.WrapperType(constructor=NONE, indent=2, comments=false) // TODO use import, but this is not accepted by javac
	static final class MyItem extends Item
	{
		static final StringField field = new StringField().optional();

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.lang.String getField()
		{
			return MyItem.field.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setField(@javax.annotation.Nullable final java.lang.String field)
				throws
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.field.set(this,field);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(TYPE);

	private void execute(final String sql) throws SQLException
	{
		assertEquals(1, connection.executeUpdate(sql));
	}
}
