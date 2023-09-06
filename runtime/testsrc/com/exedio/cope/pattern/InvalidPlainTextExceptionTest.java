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

package com.exedio.cope.pattern;

import static com.exedio.cope.RuntimeAssert.assumeNotGithub;
import static com.exedio.cope.tojunit.Assert.reserialize;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.pattern.Hash.InvalidPlainTextException;
import org.junit.jupiter.api.Test;

public class InvalidPlainTextExceptionTest
{
	@Test void testNormal()
	{
		final InvalidPlainTextException e =
				new InvalidPlainTextException("message", "plaintext", null, null);
		assertEquals("message", e.getMessageWithoutFeature());
		assertEquals("message for null", e.getMessage());
		assertEquals("plaintext", e.getPlainText());
		assertEquals(null, e.getItem());
		assertEquals(null, e.getFeature());

		assumeNotGithub(); // expectedSize 3746 on github
		final InvalidPlainTextException es = reserialize(e, 3744);
		assertEquals("message", es.getMessageWithoutFeature());
		assertEquals("message for null", es.getMessage());
		assertEquals(null, es.getPlainText());
		assertEquals(null, es.getItem());
		assertEquals(null, es.getFeature());
	}
	@Test void testMessageNull()
	{
		final InvalidPlainTextException e =
				new InvalidPlainTextException(null, "plaintext", null, null);
		assertEquals(null, e.getMessageWithoutFeature());
		assertEquals("null for null", e.getMessage());
		assertEquals("plaintext", e.getPlainText());
		assertEquals(null, e.getItem());
		assertEquals(null, e.getFeature());

		assumeNotGithub(); // expectedSize 3742 on github
		final InvalidPlainTextException es = reserialize(e, 3740);
		assertEquals(null, es.getMessageWithoutFeature());
		assertEquals("null for null", es.getMessage());
		assertEquals(null, es.getPlainText());
		assertEquals(null, es.getItem());
		assertEquals(null, es.getFeature());
	}
	@Test void testPlainTextNull()
	{
		final InvalidPlainTextException e =
				new InvalidPlainTextException("message", null, null, null);
		assertEquals("message", e.getMessageWithoutFeature());
		assertEquals("message for null", e.getMessage());
		assertEquals(null, e.getPlainText());
		assertEquals(null, e.getItem());
		assertEquals(null, e.getFeature());

		assumeNotGithub(); // expectedSize 3753 on github
		final InvalidPlainTextException es = reserialize(e, 3751);
		assertEquals("message", es.getMessageWithoutFeature());
		assertEquals("message for null", es.getMessage());
		assertEquals(null, es.getPlainText());
		assertEquals(null, es.getItem());
		assertEquals(null, es.getFeature());
	}
	@Test void testPlainTextEmpty()
	{
		final InvalidPlainTextException e =
				new InvalidPlainTextException("message", "", null, null);
		assertEquals("message", e.getMessageWithoutFeature());
		assertEquals("message for null", e.getMessage());
		assertEquals("", e.getPlainText());
		assertEquals(null, e.getItem());
		assertEquals(null, e.getFeature());
	}
	@Test void testPlainTextInMessage()
	{
		final InvalidPlainTextException e =
				new InvalidPlainTextException("message with 'plaintext'", "plaintext", null, null);
		assertEquals("message with '<plainText wiped>'", e.getMessageWithoutFeature());
		assertEquals("message with '<plainText wiped>' for null", e.getMessage());
		assertEquals("plaintext", e.getPlainText());
		assertEquals(null, e.getItem());
		assertEquals(null, e.getFeature());
	}
	@Test void testPlainTextInMessageShort()
	{
		final InvalidPlainTextException e =
				new InvalidPlainTextException("message with '1234'", "1234", null, null);
		assertEquals("message with '<plainText wiped>'", e.getMessageWithoutFeature());
		assertEquals("message with '<plainText wiped>' for null", e.getMessage());
		assertEquals("1234", e.getPlainText());
		assertEquals(null, e.getItem());
		assertEquals(null, e.getFeature());
	}
	@Test void testPlainTextInMessageTooShort()
	{
		final InvalidPlainTextException e =
				new InvalidPlainTextException("message with '123'", "123", null, null);
		assertEquals("message with '123'", e.getMessageWithoutFeature());
		assertEquals("message with '123' for null", e.getMessage());
		assertEquals("123", e.getPlainText());
		assertEquals(null, e.getItem());
		assertEquals(null, e.getFeature());
	}
}
