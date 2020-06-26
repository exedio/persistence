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

import static com.exedio.cope.RuntimeTester.assertFieldsCovered;
import static com.exedio.cope.SchemaInfo.newConnection;
import static com.exedio.cope.StringCharSetItem.TYPE;
import static com.exedio.cope.StringCharSetItem.alpha;
import static com.exedio.cope.StringCharSetItem.any;
import static com.exedio.cope.StringCharSetItem.asciiplus;
import static com.exedio.cope.StringCharSetItem.email;
import static com.exedio.cope.StringCharSetItem.nonascii;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.exedio.cope.tojunit.SI;
import com.exedio.cope.util.CharSet;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Table;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

public class StringCharSetTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(StringCharSetTest.class, "MODEL");
	}

	public StringCharSetTest()
	{
		super(MODEL);
	}

	@Test void testCheckOk()
	{
		alpha.check("abcabc");
	}

	@Test void testCheckFail()
	{
		final StringCharSetViolationException e = assertFails(
				() -> alpha.check("abc1abc"),
				StringCharSetViolationException.class,
				"character set violation, " +
				"'abc1abc' for " + alpha + ", " +
				"contains forbidden character '1' (U+0031) on position 3.");
		assertEquals(alpha, e.getFeature());
		assertEquals(null, e.getItem());
	}

	@Test void testCheckUnsupportedConstraints()
	{
		commit();
		model.checkUnsupportedConstraints();
		startTransaction();
	}

	@SuppressWarnings("HardcodedLineSeparator")
	@Test void testCondition()
	{
		assertEquals(
				"StringCharSetItem.any conformsTo [A-Z]",
				new CharSetCondition(any, new CharSet('A', 'Z')).toString());
		assertFieldsCovered(asList(any), new CharSetCondition(any, new CharSet('A', 'Z')));

		any("nullV", null);
		final StringCharSetItem abc   = any("abc", "abcd");
		final StringCharSetItem space = any("space", "ab cd");
		final StringCharSetItem bsp   = any("bsp", "ab\bcd"); // backspace
		final StringCharSetItem tab   = any("tab", "ab\tcd");
		final StringCharSetItem nl    = any("nl", "ab\ncd");
		final StringCharSetItem cr    = any("cr", "ab\rcd");
		final StringCharSetItem uuml  = any("uuml", "ab\u00fccd");
		final StringCharSetItem del   = any("del", "\u007f");
		final StringCharSetItem quote = any("quote", "'");
		final StringCharSetItem brkts = any("brackets", "][");

		final CharSet printable7bit  = new CharSet(' ', '~');
		final CharSet printable16bit = new CharSet(' ', '\uffff');
		final CharSet whiteSpace7bit  = new CharSet('\t', '\n', '\r', '\r', ' ', '~');
		final CharSet whiteSpace16bit = new CharSet('\t', '\n', '\r', '\r', ' ', '\uffff');
		final CharSet control7bit  = new CharSet((char)0, '~');
		final CharSet control16bit = new CharSet((char)0, '\uffff');
		final CharSet onlyDel = new CharSet('\u007f', '\u007f');
		final CharSet onlyQuote = new CharSet('\'', '\'');
		final CharSet brackets = new CharSet('[', ']');

		assertIt(printable7bit,   true,  abc, space,                              quote, brkts);
		assertIt(printable16bit,  false, abc, space,                   uuml, del, quote, brkts);
		assertIt(whiteSpace7bit,  true,  abc, space,      tab, nl, cr,            quote, brkts);
		assertIt(whiteSpace16bit, false, abc, space,      tab, nl, cr, uuml, del, quote, brkts);
		assertIt(control7bit,     true,  abc, space, bsp, tab, nl, cr,            quote, brkts);
		assertIt(control16bit,    false, abc, space, bsp, tab, nl, cr, uuml, del, quote, brkts);
		assertIt(onlyDel,         true,                                      del);
		assertIt(onlyQuote,       true,                                           quote);
		assertIt(brackets,        true,                                                  brkts);
	}

	private static String charSetConstraintName(final StringField field)
	{
		return TYPE.getID()+"_"+field.getName()+"_CS";
	}

	@Test void testNonSubAsciiConstraints()
	{
		final Table table = MODEL.getSchema().getTable(SchemaInfo.getTableName(TYPE));
		if (mysql)
		{
			assertNotNull(table.getConstraint(charSetConstraintName(nonascii)));
			if(MODEL.getEnvironmentInfo().isDatabaseVersionAtLeast(8, 0))
				assertNotNull(table.getConstraint(charSetConstraintName(asciiplus)));
			else
				assertEquals(null, table.getConstraint(charSetConstraintName(asciiplus)));
		}
		else
		{
			assertEquals(null, table.getConstraint(charSetConstraintName(nonascii)));
			assertEquals(null, table.getConstraint(charSetConstraintName(asciiplus)));
		}
	}

	@Test void testCheckLEmail() throws SQLException
	{
		any("check", null);
		checkEmail( "azAZ09!#$%&'*+-/=?^_`{|}~.", " (),:;<>\"[\\]" );
	}

	private void checkEmail( final String validChars, final String invalidChars ) throws SQLException
	{
		MODEL.commit();
		final Constraint emailCsConstraint = MODEL.getSchema().getTable(SchemaInfo.getTableName(TYPE)).getConstraint(charSetConstraintName(email));
		if (!mysql)
		{
			assertEquals(null, emailCsConstraint);
			return;
		}
		assertNotNull(emailCsConstraint, charSetConstraintName(email));
		assertEquals(0, emailCsConstraint.checkL());
		setEmailBySql(validChars);
		assertEquals(0, emailCsConstraint.checkL());
		final String mask = validChars.substring(0, Math.min(validChars.length(), invalidChars.length())-1);
		for (int i = 0; i < invalidChars.length(); i++)
		{
			final char invalidChar = invalidChars.charAt(i);
			final int insertIndex = i%(mask.length()+1);
			setEmailBySql(mask.substring(0, insertIndex) + invalidChar + mask.substring(insertIndex));
			assertEquals(1, emailCsConstraint.checkL(), "invalid char not detected: "+invalidChar);
		}
	}

	@SuppressFBWarnings("SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING")
	private static void setEmailBySql(final String value) throws SQLException
	{
		final String update = "UPDATE " + SI.tab(TYPE) + " SET " + SI.col(email) + " = ?";
		try (
				Connection connection = newConnection(MODEL);
				PreparedStatement statement = connection.prepareStatement(update))
		{
			statement.setString(1, value);
			assertEquals(1, statement.executeUpdate());
		}
	}

	StringCharSetItem any(final String code, final String any)
	{
		return new StringCharSetItem(code, any);
	}

	private void assertIt(final CharSet cs, final boolean isSubsetOfAscii, final StringCharSetItem... result)
	{
		assertEquals(isSubsetOfAscii, cs.isSubsetOfAscii(), "isSubsetOfAscii");
		assertEquals(asList(result), Arrays.stream(result).sorted().collect(toList()));
		final CharSetCondition c = new CharSetCondition(any, cs);
		final HashSet<StringCharSetItem> resultSet = new HashSet<>(asList(result));
		for(final StringCharSetItem i : TYPE.search(null, TYPE.getThis(), true))
			assertEquals(resultSet.contains(i), c.get(i), i.getCode());

		if(mysql)
		{
			if(isSubsetOfAscii || MODEL.getEnvironmentInfo().isDatabaseVersionAtLeast(8, 0))
			{
				assertEquals(asList(result), TYPE.search(c, TYPE.getThis(), true));
			}
			else
			{
				assertFails(
						() -> TYPE.search(c),
						IllegalStateException.class,
						"not supported: CharSetCondition on MySQL " +
						"with non-ASCII CharSet: " + cs);
			}
		}
		else
		{
			assertNotYetImplemented(c);
		}
	}

	@Test void testConditionApos()
	{
		final CharSet cs = StringCharSetItem.apos.getCharSet();
		assertEquals("^['A-Z]*$", cs.getRegularExpression());

		final StringCharSetItem yes   = any("yes", "AB'CD");
		final StringCharSetItem no    = any("no" , "aB'CD");

		final CharSetCondition c = new CharSetCondition(any, cs);
		assertEquals(true,  c.get(yes));
		assertEquals(false, c.get(no));

		if(mysql)
		{
			assertEquals(asList(yes), TYPE.search(c, TYPE.getThis(), true));
		}
		else
		{
			assertNotYetImplemented(c);
		}
	}

	@Test void testNot()
	{
		final CharSetCondition condition = new CharSetCondition(any, new CharSet('a', 'd'));
		final Condition conditionNot = condition.not();
		final StringCharSetItem itemTrue  = any("true",  "abcd");
		final StringCharSetItem itemFalse = any("false", "abcX");
		final StringCharSetItem itemNull  = any("null",  null);

		assertEquals(true,  condition.get(itemTrue));
		assertEquals(false, condition.get(itemFalse));
		assertEquals(false, condition.get(itemNull));

		assertEquals(false, conditionNot.get(itemTrue));
		assertEquals(true,  conditionNot.get(itemFalse));
		assertEquals(false, conditionNot.get(itemNull));

		if(mysql)
		{
			assertEquals(asList(itemTrue ), TYPE.search(condition,    TYPE.getThis(), true));
			assertEquals(asList(itemFalse), TYPE.search(conditionNot, TYPE.getThis(), true));
		}
		else
		{
			assertNotYetImplemented(condition);
			assertNotYetImplemented(conditionNot);
		}
	}

	private static void assertNotYetImplemented(final Condition condition)
	{
		assertFails(
				() -> TYPE.search(condition),
				RuntimeException.class,
				"CharSetCondition not yet implemented");
	}

	@Test void testSchema()
	{
		assertSchema();
	}
}
