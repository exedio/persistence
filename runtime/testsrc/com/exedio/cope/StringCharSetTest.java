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

import static com.exedio.cope.StringCharSetItem.TYPE;
import static com.exedio.cope.StringCharSetItem.alpha;
import static com.exedio.cope.StringCharSetItem.any;
import static java.util.Arrays.asList;

import com.exedio.cope.util.CharSet;
import java.util.HashSet;
import org.junit.Test;

public class StringCharSetTest extends AbstractRuntimeModelTest
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

	@Test public void testCheckOk()
	{
		alpha.check("abcabc");
	}

	@Test public void testCheckFail()
	{
		try
		{
			alpha.check("abc1abc");
			fail();
		}
		catch(final StringCharSetViolationException e)
		{
			assertEquals(alpha, e.getFeature());
			assertEquals(null, e.getItem());
			assertEquals(
					"character set violation, " +
					"'abc1abc' for " + alpha + ", " +
					"contains forbidden character '1' on position 3.",
					e.getMessage());
		}
	}

	@Test public void testCheckUnsupportedConstraints()
	{
		commit();
		model.checkUnsupportedConstraints();
		startTransaction();
	}

	@Test public void testCondition()
	{
		assertEquals(
				"StringCharSetItem.any conformsTo [A-Z]",
				new CharSetCondition(any, new CharSet('A', 'Z')).toString());

		any("nullV", null);
		final StringCharSetItem abc   = any("abc", "abcd");
		final StringCharSetItem space = any("space", "ab cd");
		final StringCharSetItem bsp   = any("bsp", "ab\bcd"); // backspace
		final StringCharSetItem tab   = any("tab", "ab\tcd");
		final StringCharSetItem nl    = any("nl", "ab\ncd");
		final StringCharSetItem cr    = any("cr", "ab\rcd");
		final StringCharSetItem uuml  = any("uuml", "ab\u00fccd");

		final CharSet printable7bit  = new CharSet(' ', '~');
		final CharSet printable16bit = new CharSet(' ', '\uffff');
		final CharSet whiteSpace7bit  = new CharSet('\t', '\n', '\r', '\r', ' ', '~');
		final CharSet whiteSpace16bit = new CharSet('\t', '\n', '\r', '\r', ' ', '\uffff');
		final CharSet control7bit  = new CharSet((char)0, '~');
		final CharSet control16bit = new CharSet((char)0, '\uffff');

		assertIt(printable7bit,   true,  abc, space);
		assertIt(printable16bit,  false, abc, space, uuml);
		assertIt(whiteSpace7bit,  true,  abc, space,            tab, nl, cr);
		assertIt(whiteSpace16bit, false, abc, space, uuml,      tab, nl, cr);
		assertIt(control7bit,     true,  abc, space,       bsp, tab, nl, cr);
		assertIt(control16bit,    false, abc, space, uuml, bsp, tab, nl, cr);
	}

	StringCharSetItem any(final String code, final String any)
	{
		return new StringCharSetItem(code, any);
	}

	private void assertIt(final CharSet cs, final boolean isSubsetOfAscii, final StringCharSetItem... result)
	{
		assertEquals("isSubsetOfAscii", isSubsetOfAscii, cs.isSubsetOfAscii());
		final CharSetCondition c = new CharSetCondition(any, cs);
		final HashSet<StringCharSetItem> resultSet = new HashSet<>(asList(result));
		for(final StringCharSetItem i : TYPE.search(null, TYPE.getThis(), true))
			assertEquals(i.getCode(), resultSet.contains(i), c.get(i));

		if(mysql)
		{
			if(isSubsetOfAscii)
			{
				assertEquals(asList(result), TYPE.search(c, TYPE.getThis(), true));
			}
			else
			{
				try
				{
					TYPE.search(c);
					fail();
				}
				catch(final IllegalStateException e)
				{
					assertEquals("not supported: CharSetCondition on MySQL with non-ASCII CharSet: " + cs, e.getMessage());
				}
			}
		}
		else
		{
			assertNotYetImplemented(c);
		}
	}

	@Test public void testConditionApos()
	{
		final CharSet cs = StringCharSetItem.apos.getCharSet();
		assertEquals("^[',A-Z]*$", cs.getRegularExpression());

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

	@Test public void testNot()
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
		try
		{
			TYPE.search(condition);
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals("CharSetCondition not yet implemented", e.getMessage());
			assertEquals(RuntimeException.class, e.getClass());
		}
	}

	@Test public void testSchema()
	{
		assertSchema();
	}
}
