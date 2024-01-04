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

import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.TestSources;
import com.exedio.cope.util.Sources;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.StatementListener;
import com.exedio.dsmf.misc.DefaultStatementListener;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class SemicolonTest
{
	@Test void testEnabled()
	{
		MODEL.connect(ConnectProperties.create(
				TestSources.minimal()
		));
		final Schema schema = MODEL.getSchema();

		schema.create(listener);
		assertStatements(CREATE_SEQ_1 + ";" + CREATE_SEQ_2 + ";" + CREATE_TAB_1 + ";" + CREATE_TAB_2, CREATE_TO_2);

		schema.drop(listener);
		assertStatements(DROP_TO_2, DROP_SEQ_1 + ";" + DROP_SEQ_2 + ";" + DROP_TAB_2 + ";" + DROP_TAB_1);
	}

	@Test void testDisabled()
	{
		MODEL.connect(ConnectProperties.create(Sources.cascade(
				TestSources.minimal(),
				TestSources.single("disableSupport.semicolon", true)
		)));
		final Schema schema = MODEL.getSchema();

		schema.create(listener);
		assertStatements(CREATE_SEQ_1, CREATE_SEQ_2, CREATE_TAB_1, CREATE_TAB_2, CREATE_TO_2);

		schema.drop(listener);
		assertStatements(DROP_TO_2, DROP_TAB_2, DROP_TAB_1, DROP_SEQ_2, DROP_SEQ_1);
	}


	private final ArrayList<String> statements = new ArrayList<>();
	private final StatementListener listener = new DefaultStatementListener()
	{
		@Override public void afterExecute(final String statement, final int rows)
		{
			statements.add(statement);
		}
	};
	private void assertStatements(final String... expected)
	{
		assertEquals(List.of(expected), statements);
		statements.clear();
	}

	private static final String CREATE_TAB_1 = "CREATE TABLE \"MyItem1\"(\"this\" INTEGER not null,\"to2\" INTEGER not null,CONSTRAINT \"MyItem1_PK\" PRIMARY KEY(\"this\"),CONSTRAINT \"MyItem1_this_MN\" CHECK(\"this\">=0),CONSTRAINT \"MyItem1_this_MX\" CHECK(\"this\"<=2147483647),CONSTRAINT \"MyItem1_to2_MN\" CHECK(\"to2\">=0),CONSTRAINT \"MyItem1_to2_MX\" CHECK(\"to2\"<=2147483647))";
	private static final String CREATE_TAB_2 = "CREATE TABLE \"MyItem2\"(\"this\" INTEGER not null,\"to1\" INTEGER not null,CONSTRAINT \"MyItem2_PK\" PRIMARY KEY(\"this\"),CONSTRAINT \"MyItem2_this_MN\" CHECK(\"this\">=0),CONSTRAINT \"MyItem2_this_MX\" CHECK(\"this\"<=2147483647),CONSTRAINT \"MyItem2_to1_MN\" CHECK(\"to1\">=0),CONSTRAINT \"MyItem2_to1_MX\" CHECK(\"to1\"<=2147483647),CONSTRAINT \"MyItem2_to1_Fk\" FOREIGN KEY (\"to1\") REFERENCES \"MyItem1\"(\"this\"))";
	private static final String CREATE_TO_2 = "ALTER TABLE \"MyItem1\" ADD CONSTRAINT \"MyItem1_to2_Fk\" FOREIGN KEY (\"to2\") REFERENCES \"MyItem2\"(\"this\")";
	private static final String DROP_TO_2   = "ALTER TABLE \"MyItem1\" DROP CONSTRAINT \"MyItem1_to2_Fk\"";
	private static final String CREATE_SEQ_1 = "CREATE SEQUENCE \"MyItem1_seq1\" AS INTEGER START WITH 55 INCREMENT BY 1";
	private static final String CREATE_SEQ_2 = "CREATE SEQUENCE \"MyItem2_seq2\" AS INTEGER START WITH 77 INCREMENT BY 1";
	private static final String DROP_TAB_1 = "DROP TABLE \"MyItem1\"";
	private static final String DROP_TAB_2 = "DROP TABLE \"MyItem2\"";
	private static final String DROP_SEQ_1 = "DROP SEQUENCE \"MyItem1_seq1\"";
	private static final String DROP_SEQ_2 = "DROP SEQUENCE \"MyItem2_seq2\"";


	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class MyItem1 extends Item
	{
		@WrapperIgnore
		@SuppressWarnings("unused") // OK: feature collected by reflection
		static final ItemField<MyItem2> to2 = ItemField.create(MyItem2.class).toFinal(); // toFinal avoids column catch

		@WrapperIgnore
		@SuppressWarnings("unused") // OK: feature collected by reflection
		static final Sequence seq1 = new Sequence(55, 66);

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem1> TYPE = com.exedio.cope.TypesBound.newType(MyItem1.class,MyItem1::new);

		@com.exedio.cope.instrument.Generated
		private MyItem1(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class MyItem2 extends Item
	{
		@WrapperIgnore
		@SuppressWarnings("unused") // OK: feature collected by reflection
		static final ItemField<MyItem1> to1 = ItemField.create(MyItem1.class).toFinal(); // toFinal avoids column catch

		@WrapperIgnore
		@SuppressWarnings("unused") // OK: feature collected by reflection
		static final Sequence seq2 = new Sequence(77, 88);

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem2> TYPE = com.exedio.cope.TypesBound.newType(MyItem2.class,MyItem2::new);

		@com.exedio.cope.instrument.Generated
		private MyItem2(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(MyItem1.TYPE, MyItem2.TYPE);

	@AfterEach void after()
	{
		statements.clear();
		if(MODEL.isConnected())
		{
			MODEL.tearDownSchema();
			MODEL.disconnect();
		}
	}
}
