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

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class StringCopyTest
{
	@Test void testOptional()
	{
		final StringField orig = new StringField().optional();
		assertEquals(false, orig.isFinal());
		assertEquals(false, orig.isMandatory());
		assertEquals(1, orig.getMinimumLength());
		assertEquals(StringField.DEFAULT_MAXIMUM_LENGTH, orig.getMaximumLength());

		final StringField copy = orig.copy();
		assertEquals(false, copy.isFinal());
		assertEquals(false, copy.isMandatory());
		assertEquals(1, copy.getMinimumLength());
		assertEquals(StringField.DEFAULT_MAXIMUM_LENGTH, copy.getMaximumLength());
	}

	@Deprecated // OK test deprecated api
	@Test void testEmpty()
	{
		final StringField orig = new StringField().optional().lengthMin(0);
		assertEquals(false, orig.isFinal());
		assertEquals(false, orig.isMandatory());
		assertEquals(0, orig.getMinimumLength());
		assertEquals(StringField.DEFAULT_MAXIMUM_LENGTH, orig.getMaximumLength());

		final StringField copy = orig.copy();
		assertEquals(false, copy.isFinal());
		assertEquals(false, copy.isMandatory());
		assertEquals(0, copy.getMinimumLength());
		assertEquals(StringField.DEFAULT_MAXIMUM_LENGTH, copy.getMaximumLength());
	}

	@Test void testMin()
	{
		final StringField orig = new StringField().toFinal().optional().lengthMin(10);
		assertEquals(true, orig.isFinal());
		assertEquals(false, orig.isMandatory());
		assertNull(orig.getImplicitUniqueConstraint());
		assertEquals(10, orig.getMinimumLength());
		assertEquals(StringField.DEFAULT_MAXIMUM_LENGTH, orig.getMaximumLength());

		final StringField copy = orig.copy();
		assertEquals(true, copy.isFinal());
		assertEquals(false, copy.isMandatory());
		assertNull(copy.getImplicitUniqueConstraint());
		assertEquals(10, copy.getMinimumLength());
		assertEquals(StringField.DEFAULT_MAXIMUM_LENGTH, copy.getMaximumLength());
	}

	@Test void testUnique()
	{
		final StringField orig = new StringField().toFinal().optional().unique().lengthMin(20);
		assertEquals(true, orig.isFinal());
		assertEquals(false, orig.isMandatory());
		assertNotNull(orig.getImplicitUniqueConstraint());
		assertEquals(20, orig.getMinimumLength());
		assertEquals(StringField.DEFAULT_MAXIMUM_LENGTH, orig.getMaximumLength());

		final StringField copy = orig.copy();
		assertEquals(true, copy.isFinal());
		assertEquals(false, copy.isMandatory());
		assertNotNull(copy.getImplicitUniqueConstraint());
		assertEquals(20, copy.getMinimumLength());
		assertEquals(StringField.DEFAULT_MAXIMUM_LENGTH, copy.getMaximumLength());
	}

	@Test void testRange()
	{
		final StringField orig = new StringField().lengthRange(10, 20);
		assertEquals(false, orig.isFinal());
		assertEquals(true, orig.isMandatory());
		assertEquals(10, orig.getMinimumLength());
		assertEquals(20, orig.getMaximumLength());

		final StringField copy = orig.copy();
		assertEquals(false, copy.isFinal());
		assertEquals(true, copy.isMandatory());
		assertEquals(10, copy.getMinimumLength());
		assertEquals(20, copy.getMaximumLength());
	}

	@Test void testIllegalRange()
	{
		assertWrongLength(-1, 20, "minimumLength must not be negative, but was -1");
		assertWrongLength( 0,  0, "maximumLength must be greater zero, but was 0");
		assertWrongLength(20, 10, "maximumLength must be greater or equal minimumLength, but was 10 and 20");
	}

	void assertWrongLength(final int minimumLength, final int maximumLength, final String message)
	{
		try
		{
			new StringField().optional().lengthRange(minimumLength, maximumLength);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(message, e.getMessage());
		}
	}
}
