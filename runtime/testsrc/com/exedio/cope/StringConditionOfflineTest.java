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
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.tojunit.EqualsAssert.assertNotEqualsAndHash;
import static java.lang.System.lineSeparator;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.regex.PatternSyntaxException;
import org.junit.jupiter.api.Test;

public class StringConditionOfflineTest
{
	@Test void testFieldsCovered()
	{
		final StringField f = new StringField();
		final StringField f2 = new StringField();
		assertFieldsCovered(asList(f), f.like("a"));
		assertFieldsCovered(asList(f2), f2.likeIgnoreCase("a"));
	}

	@Test void testConditions()
	{
		final StringField f = new StringField();
		final StringField f2 = new StringField();
		assertEqualsAndHash(f.equal("hallo"), f.equal("hallo"));
		assertEqualsAndHash(f.equal(f2), f.equal(f2));
		assertNotEqualsAndHash(
				f.equal("hallo"),
				f.equal("bello"),
				f.equal((String)null),
				f.like("hallo"),
				f.regexpLike("regexp"),
				f.equal(f2),
				f.equal(f));
		assertSame(f, f.like("hallo").getFunction());
		assertSame("hallo", f.like("hallo").getValue());
	}

	@Test void testConditionsConvenience()
	{
		final StringField f = new StringField();
		assertEquals(f.like( "lowerUPPER%"), f.startsWith("lowerUPPER"));
		assertEquals(f.like("%lowerUPPER" ), f.  endsWith("lowerUPPER"));
		assertEquals(f.like("%lowerUPPER%"), f.  contains("lowerUPPER"));
		final CaseView l = f.toLowerCase();
		assertEquals(l.equal( "lowerupper" ), f.     equalIgnoreCase("lowerUPPER" ));
		assertEquals(l.like ( "lowerupper%"), f.      likeIgnoreCase("lowerUPPER%"));
		assertEquals(l.like ( "lowerupper%"), f.startsWithIgnoreCase("lowerUPPER" ));
		assertEquals(l.like ("%lowerupper" ), f.  endsWithIgnoreCase("lowerUPPER" ));
		assertEquals(l.like ("%lowerupper%"), f.  containsIgnoreCase("lowerUPPER" ));

		final StringField f2 = new StringField();
		final CaseView l2 = f2.toLowerCase();
		assertEquals(l.equal(l2), f.equalIgnoreCase(f2));
	}

	@Test void testConditionsConvenienceEmpty() // TODO should be isNotNull
	{
		final StringField f = new StringField();
		assertEquals(f.like("%" ), f.startsWith(""));
		assertEquals(f.like("%" ), f.  endsWith(""));
		assertEquals(f.like("%%"), f.  contains(""));
		final CaseView l = f.toLowerCase();
		assertEquals(l.equal(""  ), f.     equalIgnoreCase("" ));
		assertEquals(l.like ("%" ), f.      likeIgnoreCase("%"));
		assertEquals(l.like ("%" ), f.startsWithIgnoreCase("" ));
		assertEquals(l.like ("%" ), f.  endsWithIgnoreCase("" ));
		assertEquals(l.like ("%%"), f.  containsIgnoreCase("" ));
	}

	@Test void testConditionsConvenienceNull()
	{
		final StringField f = new StringField();
		assertFails(() -> f.startsWith(null), NullPointerException.class, "Cannot invoke \"String.replaceAll(String, String)\" because \"value\" is null");
		assertFails(() -> f.  endsWith(null), NullPointerException.class, "Cannot invoke \"String.replaceAll(String, String)\" because \"value\" is null");
		assertFails(() -> f.  contains(null), NullPointerException.class, "Cannot invoke \"String.replaceAll(String, String)\" because \"value\" is null");
		assertFails(() -> f.     equalIgnoreCase((String)null), NullPointerException.class, "Cannot invoke \"String.toLowerCase(java.util.Locale)\" because \"value\" is null");
		assertFails(() -> f.     equalIgnoreCase((Function<String>)null), NullPointerException.class, "sources[0]");
		assertFails(() -> f.      likeIgnoreCase(null), NullPointerException.class, "Cannot invoke \"String.toLowerCase(java.util.Locale)\" because \"value\" is null");
		assertFails(() -> f.startsWithIgnoreCase(null), NullPointerException.class, "Cannot invoke \"String.toLowerCase(java.util.Locale)\" because \"value\" is null");
		assertFails(() -> f.  endsWithIgnoreCase(null), NullPointerException.class, "Cannot invoke \"String.toLowerCase(java.util.Locale)\" because \"value\" is null");
		assertFails(() -> f.  containsIgnoreCase(null), NullPointerException.class, "Cannot invoke \"String.toLowerCase(java.util.Locale)\" because \"value\" is null");
	}

	@Test void testRegexp()
	{
		final StringField f = new StringField();
		final StringField f2 = new StringField();
		assertEquals(f + " regexp '(?s)\\A[C-F]*\\z'", f.regexpLike("[C-F]*").toString());
		assertEqualsAndHash(
				f.regexpLike("regexp"),
				f.regexpLike("regexp"));
		assertNotEqualsAndHash(
				f.regexpLike("regexp"),
				f.regexpLike("regexp2"),
				f2.regexpLike("regexp"));
		assertFails(
				() -> f.regexpLike(null),
				NullPointerException.class,
				"regexp");
		assertFails(
				() -> f.regexpLike(""),
				IllegalArgumentException.class,
				"regexp must not be empty");
		assertFails(
				() -> f.regexpLike("[A-"),
				PatternSyntaxException.class,
				"Illegal/unsupported escape sequence near index 10" + lineSeparator() + "(?s)\\A[A-\\z" + lineSeparator() + "          ^"
		);
	}
}
