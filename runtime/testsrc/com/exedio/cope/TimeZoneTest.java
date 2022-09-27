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

import static java.time.LocalDateTime.of;
import static java.time.Month.DECEMBER;
import static java.time.Month.OCTOBER;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.TestSources;
import com.exedio.cope.util.Day;
import com.exedio.cope.util.Properties;
import com.exedio.cope.util.Sources;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class TimeZoneTest extends TestWithEnvironment
{
	public TimeZoneTest()
	{
		super(MODEL);
	}

	private static final Date dateA = date(of(1959, OCTOBER,  4,  0, 43, 39, 123_000_000)); // Luna 3
	private static final Date dateB = date(of(2018, DECEMBER, 7, 18, 23, 11, 123_000_000)); // Chang'e 4

	private static final Day dayA = new Day(1959, 10, 4);
	private static final Day dayB = new Day(2018, 12, 7);

	private AnItem itemA;
	private AnItem itemB;

	private void assertItems()
	{
		try(TransactionTry tx = MODEL.startTransactionTry(TimeZoneTest.class.getName()))
		{
			assertAll(
					() -> assertEquals(dateA, itemA.getDate()),
					() -> assertEquals(dateB, itemB.getDate()),
					() -> assertEquals(dayA, itemA.getDay()),
					() -> assertEquals(dayB, itemB.getDay()));
			tx.commit();
		}
	}

	@Test void test() throws SQLException
	{
		itemA = new AnItem(dateA, dayA);
		itemB = new AnItem(dateB, dayB);
		MODEL.commit();
		assertTimeZone("+00:00", GMT);
		assertItems();

		MODEL.clearCache();
		assertItems();

		reconnect("<default>", "<default>");
		assertTimeZone(SYSTEM, null);
		assertItems();

		reconnect("+05:00", "Europe/Moscow");
		assertTimeZone("+05:00", "Europe/Moscow");
		assertItems();

		reconnect("-08:30", "Canada/Eastern");
		assertTimeZone("-08:30", "Canada/Eastern");
		assertItems();

		reconnect("+00:00", GMT);
		assertTimeZone("+00:00", GMT);
		assertItems();
	}

	private void assertTimeZone(
			final String mysql,
			final String postgresql)
			throws SQLException
	{
		//noinspection EnumSwitchStatementWhichMissesCases
		switch(dialect)
		{
			case mysql:
				try(Connection c = SchemaInfo.newConnection(model);
					Statement s = c.createStatement();
					ResultSet rs = s.executeQuery("SELECT @@GLOBAL.time_zone, @@SESSION.time_zone"))
				{
					assertTrue(rs.next());
					assertAll(
							() -> assertEquals(SYSTEM, rs.getString(1), "global"),
							() -> assertEquals(mysql,  rs.getString(2), "session"));
					assertFalse(rs.next());
				}
				break;
			case postgresql:
				try(Connection c = SchemaInfo.newConnection(model);
					Statement s = c.createStatement();
					ResultSet rs = s.executeQuery("show TimeZone"))
				{
					assertTrue(rs.next());
					if(postgresql==null)
						assertNotNull(rs.getString(1));
					else
						assertEquals(postgresql, rs.getString(1));
					assertFalse(rs.next());
				}
				break;
			default:
				assumeTrue(false);
		}
	}

	private static final String SYSTEM = "SYSTEM";
	private static final String GMT = "GMT";

	private void reconnect(
			final String mysql,
			final String postgresql)
	{
		model.disconnect();
		final String value;
		//noinspection EnumSwitchStatementWhichMissesCases
		switch(dialect)
		{
			case mysql:      value = mysql;      break;
			case postgresql: value = postgresql; break;
			default:
				throw new AssertionFailedError();
		}
		model.connect(ConnectProperties.create(Sources.cascade(
				TestSources.single("dialect.connection.timeZone", value),
				initialConnectProperties)));
	}

	private Properties.Source initialConnectProperties;

	@BeforeEach void before()
	{
		initialConnectProperties = model.getConnectProperties().getSourceObject();
	}

	@WrapperType(indent=2, comments=false)
	private static final class AnItem extends Item
	{
		static final DateField date = new DateField().toFinal();
		static final DayField  day  = new DayField() .toFinal();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private AnItem(
					@javax.annotation.Nonnull final java.util.Date date,
					@javax.annotation.Nonnull final com.exedio.cope.util.Day day)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				AnItem.date.map(date),
				AnItem.day.map(day),
			});
		}

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.util.Date getDate()
		{
			return AnItem.date.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		com.exedio.cope.util.Day getDay()
		{
			return AnItem.day.get(this);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(AnItem.TYPE);

	private static Date date(final LocalDateTime ldt)
	{
		return Date.from(Instant.from(ldt.atZone(ZoneId.of("UTC"))));
	}
}
