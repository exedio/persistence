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

import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.util.Hex.encodeLower;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class DataLengthViolationOutputStreamTest
{
	@Test void testByte() throws IOException
	{
		final DataField field = new DataField().lengthMax(3);
		final ByteArrayOutputStream back = new ByteArrayOutputStream();
		//noinspection IOResourceOpenedButNotSafelyClosed
		final DataLengthViolationOutputStream s = new DataLengthViolationOutputStream(field, back, null);
		s.write(1);
		assertEquals("01", encodeLower(back.toByteArray()));
		s.write(2);
		assertEquals("0102", encodeLower(back.toByteArray()));
		s.write(3);
		assertEquals("010203", encodeLower(back.toByteArray()));
		assertFails(
				() -> s.write(4),
				DataLengthViolationException.class,
				"length violation, 4 bytes or more is too long for " + field);
		assertEquals("010203", encodeLower(back.toByteArray()));
		assertFails(
				() -> s.write(5),
				IllegalStateException.class,
				"exhausted");
		assertEquals("010203", encodeLower(back.toByteArray()));
	}

	@Test void testBytes() throws IOException
	{
		final DataField field = new DataField().lengthMax(9);
		final ByteArrayOutputStream back = new ByteArrayOutputStream();
		//noinspection IOResourceOpenedButNotSafelyClosed
		final DataLengthViolationOutputStream s = new DataLengthViolationOutputStream(field, back, null);
		s.write(new byte[]{1,2,3});
		assertEquals("010203", encodeLower(back.toByteArray()));
		s.write(new byte[]{4,5,6});
		assertEquals("010203040506", encodeLower(back.toByteArray()));
		s.write(new byte[]{7,8,9});
		assertEquals("010203040506070809", encodeLower(back.toByteArray()));
		assertFails(
				() -> s.write(new byte[]{10}),
				DataLengthViolationException.class,
				"length violation, 10 bytes or more is too long for " + field);
		assertEquals("010203040506070809", encodeLower(back.toByteArray()));
		assertFails(
				() -> s.write(new byte[]{11}),
				IllegalStateException.class,
				"exhausted");
		assertEquals("010203040506070809", encodeLower(back.toByteArray()));
	}

	@Test void testBytesAtOnce()
	{
		final DataField field = new DataField().lengthMax(3);
		final ByteArrayOutputStream back = new ByteArrayOutputStream();
		//noinspection IOResourceOpenedButNotSafelyClosed
		final DataLengthViolationOutputStream s = new DataLengthViolationOutputStream(field, back, null);
		assertFails(
				() -> s.write(new byte[]{1,2,3,4}),
				DataLengthViolationException.class,
				"length violation, 4 bytes or more is too long for " + field);
		assertEquals("", encodeLower(back.toByteArray()));
		assertFails(
				() -> s.write(new byte[]{11}),
				IllegalStateException.class,
				"exhausted");
		assertEquals("", encodeLower(back.toByteArray()));
	}

	@Test void testBytesRange() throws IOException
	{
		final DataField field = new DataField().lengthMax(9);
		final ByteArrayOutputStream back = new ByteArrayOutputStream();
		//noinspection IOResourceOpenedButNotSafelyClosed
		final DataLengthViolationOutputStream s = new DataLengthViolationOutputStream(field, back, null);
		s.write(new byte[]{99,99,1,2,3,99}, 2, 3);
		assertEquals("010203", encodeLower(back.toByteArray()));
		s.write(new byte[]{99,99,4,5,6,99}, 2, 3);
		assertEquals("010203040506", encodeLower(back.toByteArray()));
		s.write(new byte[]{99,99,7,8,9,99}, 2, 3);
		assertEquals("010203040506070809", encodeLower(back.toByteArray()));
		assertFails(
				() -> s.write(new byte[]{99,99,10,99}, 2, 1),
				DataLengthViolationException.class,
				"length violation, 10 bytes or more is too long for " + field);
		assertEquals("010203040506070809", encodeLower(back.toByteArray()));
		assertFails(
				() -> s.write(new byte[]{99,99,11,99}, 2, 1),
				IllegalStateException.class,
				"exhausted");
		assertEquals("010203040506070809", encodeLower(back.toByteArray()));
	}

	@Test void testBytesRangeAtOnce()
	{
		final DataField field = new DataField().lengthMax(3);
		final ByteArrayOutputStream back = new ByteArrayOutputStream();
		//noinspection IOResourceOpenedButNotSafelyClosed
		final DataLengthViolationOutputStream s = new DataLengthViolationOutputStream(field, back, null);
		assertFails(
				() -> s.write(new byte[]{99,99,1,2,3,4,99}, 2, 4),
				DataLengthViolationException.class,
				"length violation, 4 bytes or more is too long for " + field);
		assertEquals("", encodeLower(back.toByteArray()));
		assertFails(
				() -> s.write(new byte[]{99,99,1,99}, 2, 1),
				IllegalStateException.class,
				"exhausted");
		assertEquals("", encodeLower(back.toByteArray()));
	}
}
