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

package com.exedio.cope.misc;

import static com.exedio.cope.instrument.Visibility.NONE;
import static java.time.LocalDateTime.of;
import static java.time.Month.DECEMBER;
import static java.time.Month.MAY;
import static java.time.Month.OCTOBER;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.CopeSchemaValue;
import com.exedio.cope.DateField;
import com.exedio.cope.EnumField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.tojunit.ConnectionRule;
import com.exedio.cope.tojunit.SI;
import com.exedio.dsmf.Node;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SchemaViewGeneratorTest extends TestWithEnvironment
{
	public SchemaViewGeneratorTest()
	{
		super(MODEL);
		copeRule.omitTransaction();
	}

	private final ConnectionRule connection = new ConnectionRule(model);

	@Test void testIt() throws SQLException
	{
		model.startTransaction(SchemaViewGeneratorTest.class.getName());
		new MyItem(511, MyEnum.beta,  711, MyEnum.alpha, date(of(1959, OCTOBER,  4,  0, 43, 39, 123_000_000))); // Luna 3
		new MyItem(522, MyEnum.delta, 722, MyEnum.delta, date(of(2018, DECEMBER, 7, 18, 23, 11, 123_000_000))); // Chang'e 4
		new MyItem(null, null, null, null, null);
		new MyItem(888, MyEnum.delta, 777, MyEnum.delta, date(of(2020, MAY, 8, 20, 23, 44, 120_000_000)));
		new MyItem(888, MyEnum.delta, 777, MyEnum.delta, date(of(2020, MAY, 8, 21, 23, 44, 100_000_000)));
		new MyItem(888, MyEnum.delta, 777, MyEnum.delta, date(of(2020, MAY, 8, 22, 23, 44, 0)));
		model.commit();

		final String SQL =
				"SELECT * FROM " + SI.view(MyItem.TYPE) + " " +
				"ORDER BY " + SI.pk(MyItem.TYPE);
		final SchemaView sv = new SchemaView(model);
		assertThrows(
				SQLException.class,
				() -> connection.execute(SQL));


		assumeTrue(mysql, "mysql"); // TODO other databases as well, amend SchemaViewTest
		sv.create();
		connection.execute("SET time_zone = '+00:00'"); // UTC needed for FROM_UNIXTIME
		final String nnd = SchemaInfo.supportsNativeDate(model) ? "" : "0";
		final String intType = mariaDriver&&atLeastMysql57() ? "INTEGER" : "INT";
		try(ResultSet rs = connection.executeQuery(SQL))
		{
			{
				final ResultSetMetaData md = rs.getMetaData();
				assertAll(
						() -> assertEquals("this",     md.getColumnName(1)),
						() -> assertEquals("supPlain", md.getColumnName(2)),
						() -> assertEquals("supEnum",  md.getColumnName(3)),
						() -> assertEquals("plain",    md.getColumnName(4)),
						() -> assertEquals("enum",     md.getColumnName(5)),
						() -> assertEquals("date",     md.getColumnName(6)),
						() -> assertEquals(intType,    md.getColumnTypeName(1), "this"),
						() -> assertEquals(intType,    md.getColumnTypeName(2), "supPlain"),
						() -> assertEquals("VARCHAR",  md.getColumnTypeName(3), "supEnum"),
						() -> assertEquals(intType,    md.getColumnTypeName(4), "plain"),
						() -> assertEquals("VARCHAR",  md.getColumnTypeName(5), "enum"),
						() -> assertEquals("DATETIME", md.getColumnTypeName(6), "date"),
						() -> assertEquals(6,          md.getColumnCount()));
			}

			assertResult(
					"0", "511", "beta", "711", "alpha",
					"1959-10-04 00:43:39.123"+nnd, rs);

			assertResult(
					"1", "522", "delta", "722", "delta",
					"2018-12-07 18:23:11.123"+nnd, rs);

			assertResult("2", null, null, null, null, null, rs);

			assertResult(
					"3", "888", "delta", "777", "delta",
					"2020-05-08 20:23:44.120"+nnd, rs);
			assertResult(
					"4", "888", "delta", "777", "delta",
					"2020-05-08 21:23:44.100"+nnd, rs);
			assertResult(
					"5", "888", "delta", "777", "delta",
					"2020-05-08 22:23:44" + (!mariaDriver ? "" : (".000"+nnd)), rs);

			assertFalse(rs.next());
		}
		assertSame(Node.Color.OK, MODEL.getVerifiedSchema().getCumulativeColor());


		sv.tearDown();
		assertThrows(
				SQLException.class,
				() -> connection.execute(SQL));


		sv.tearDown(); // tearDown is idempotent because of DROP VIEW IF EXISTS
		assertThrows(
				SQLException.class,
				() -> connection.execute(SQL));
	}

	private static void assertResult(
			final String thisS,
			final String supPlain,
			final String supEnum,
			final String plain,
			final String enumS,
			final String date,
			final ResultSet rs)
			throws SQLException
	{
		assertTrue(rs.next());
		assertAll(
				() -> assertEquals(thisS,    rs.getString(1), "this"),
				() -> assertEquals(supPlain, rs.getString(2), "supPlain"),
				() -> assertEquals(supEnum,  rs.getString(3), "supEnum"),
				() -> assertEquals(plain,    rs.getString(4), "plain"),
				() -> assertEquals(enumS,    rs.getString(5), "enum"),
				() -> assertEquals(date,     rs.getString(6), "date"));
	}


	@BeforeEach void setUp() throws SQLException
	{
		if(mysql)
			new SchemaView(model).tearDown();
	}

	@AfterEach void tearDown() throws SQLException
	{
		if(mysql)
			new SchemaView(model).tearDown();
	}


	@WrapperType(indent=2, comments=false)
	private static class MySuperItem extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		static final IntegerField supPlain = new IntegerField().toFinal().optional();

		@Wrapper(wrap="*", visibility=NONE)
		static final EnumField<MyEnum> supEnum = EnumField.create(MyEnum.class).toFinal().optional();

		// TODO column name duplicate to subtype

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private MySuperItem(
					@javax.annotation.Nullable final java.lang.Integer supPlain,
					@javax.annotation.Nullable final MyEnum supEnum)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(MySuperItem.supPlain,supPlain),
				com.exedio.cope.SetValue.map(MySuperItem.supEnum,supEnum),
			});
		}

		@com.exedio.cope.instrument.Generated
		protected MySuperItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MySuperItem> TYPE = com.exedio.cope.TypesBound.newType(MySuperItem.class,MySuperItem::new);

		@com.exedio.cope.instrument.Generated
		protected MySuperItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(indent=2, comments=false)
	private static final class MyItem extends MySuperItem
	{
		@Wrapper(wrap="*", visibility=NONE)
		static final IntegerField plain = new IntegerField().toFinal().optional();

		@Wrapper(wrap="*", visibility=NONE)
		@CopeSchemaName("enum")
		static final EnumField<MyEnum> myenum = EnumField.create(MyEnum.class).toFinal().optional();

		@Wrapper(wrap="*", visibility=NONE)
		static final DateField date = new DateField().toFinal().optional();


		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private MyItem(
					@javax.annotation.Nullable final java.lang.Integer supPlain,
					@javax.annotation.Nullable final com.exedio.cope.misc.SchemaViewGeneratorTest.MyEnum supEnum,
					@javax.annotation.Nullable final java.lang.Integer plain,
					@javax.annotation.Nullable final MyEnum myenum,
					@javax.annotation.Nullable final java.util.Date date)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				com.exedio.cope.SetValue.map(com.exedio.cope.misc.SchemaViewGeneratorTest.MySuperItem.supPlain,supPlain),
				com.exedio.cope.SetValue.map(com.exedio.cope.misc.SchemaViewGeneratorTest.MySuperItem.supEnum,supEnum),
				com.exedio.cope.SetValue.map(MyItem.plain,plain),
				com.exedio.cope.SetValue.map(MyItem.myenum,myenum),
				com.exedio.cope.SetValue.map(MyItem.date,date),
			});
		}

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	enum MyEnum
	{
		alpha, beta, @CopeSchemaValue(40) delta
	}

	static final Model MODEL = new Model(MySuperItem.TYPE, MyItem.TYPE);


	private static Date date(final LocalDateTime ldt)
	{
		return Date.from(Instant.from(ldt.atZone(ZoneId.of("UTC"))));
	}
}
