/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.util.CharSet;
import java.util.Arrays;
import java.util.HashSet;

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

	public void testCheckOk()
	{
		alpha.check("abcabc");
	}

	public void testCheckFail()
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

	public void testCondition()
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
		final HashSet<StringCharSetItem> resultSet = new HashSet<>(Arrays.asList(result));
		for(final StringCharSetItem i : TYPE.search(null, TYPE.getThis(), true))
			assertEquals(i.getCode(), resultSet.contains(i), c.get(i));

		if(mysql)
		{
			if(isSubsetOfAscii)
			{
				assertEquals(Arrays.asList(result), TYPE.search(c, TYPE.getThis(), true));
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
			try
			{
				TYPE.search(c);
				fail();
			}
			catch(final RuntimeException e)
			{
				assertEquals("CharSetCondition not yet implemented", e.getMessage());
			}
		}
	}

	public void testSchema()
	{
		assertSchema();
	}
}
