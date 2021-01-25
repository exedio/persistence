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

import static com.exedio.cope.tojunit.Assert.list;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.SI;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.Test;

public class MatchTest extends TestWithEnvironment
{
	public MatchTest()
	{
		super(MODEL);
	}

	AnItem item;

	@Test void test() throws SQLException
	{
		final ConnectProperties propsBefore = MODEL.getConnectProperties();
		assertFalse(propsBefore.getFulltextIndex(), "fulltextIndex");

		item = new AnItem();
		item.setText("hallo bello cnallo");
		MODEL.commit();

		MODEL.startTransaction(MatchTest.class.getName());
		assertEquals(list(item), AnItem.TYPE.search(new MatchCondition(AnItem.text, "hallo")));
		assertEquals(list(item), AnItem.TYPE.search(new MatchCondition(AnItem.text, "bello")));
		assertEquals(list(item), AnItem.TYPE.search(new MatchCondition(AnItem.text, "cnallo")));
		assertEquals(list(), AnItem.TYPE.search(new MatchCondition(AnItem.text, "zack")));
		MODEL.commit();

		MODEL.disconnect();
		MODEL.connect(ConnectProperties.create(cascade(
				single("fulltextIndex", true),
				propsBefore.getSourceObject()
		)));
		assertTrue(MODEL.getConnectProperties().getFulltextIndex(), "fulltextIndex");

		//noinspection EnumSwitchStatementWhichMissesCases,SwitchStatementWithTooFewBranches OK: prepares more branches
		switch(dialect)
		{
			case mysql:
				if(MODEL.getEnvironmentInfo().isDatabaseVersionAtLeast(5, 6))
				{
					try(Connection c = SchemaInfo.newConnection(MODEL);
						 Statement s = c.createStatement())
					{
						s.execute(
								"CREATE FULLTEXT INDEX index_name " +
								"ON " + SI.tab(AnItem.TYPE) + " " +
								"(" + SI.col(AnItem.text) + ")");
					}
				}
				break;
		}

		MODEL.startTransaction(MatchTest.class.getName());
		assertEquals(list(item), AnItem.TYPE.search(new MatchCondition(AnItem.text, "hallo")));
		assertEquals(list(item), AnItem.TYPE.search(new MatchCondition(AnItem.text, "bello")));
		assertEquals(list(item), AnItem.TYPE.search(new MatchCondition(AnItem.text, "cnallo")));
		assertEquals(list(), AnItem.TYPE.search(new MatchCondition(AnItem.text, "zack")));
		MODEL.commit();
	}

	@WrapperType(indent=2, comments=false)
	private static final class AnItem extends Item
	{
		static final StringField text = new StringField().optional();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private AnItem()
		{
			this(new com.exedio.cope.SetValue<?>[]{
			});
		}

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nullable
		java.lang.String getText()
		{
			return AnItem.text.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setText(@javax.annotation.Nullable final java.lang.String text)
				throws
					com.exedio.cope.StringLengthViolationException
		{
			AnItem.text.set(this,text);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(AnItem.TYPE);
}
