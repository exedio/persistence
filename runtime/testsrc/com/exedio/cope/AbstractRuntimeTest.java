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

import static com.exedio.cope.util.StrictFile.delete;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class AbstractRuntimeTest
{
	private AbstractRuntimeTest()
	{
	}

	       static final Integer i0 = Integer.valueOf(0);
	public static final Integer i1 = Integer.valueOf(1);
	public static final Integer i2 = Integer.valueOf(2);
	public static final Integer i3 = Integer.valueOf(3);
	public static final Integer i4 = Integer.valueOf(4);
	public static final Integer i5 = Integer.valueOf(5);
	       static final Integer i7 = Integer.valueOf(7);
	public static final Integer i8 = Integer.valueOf(8);
	public static final Integer i9 = Integer.valueOf(9);

	static final Long l0 = Long.valueOf(0l);
	static final Long l1 = Long.valueOf(1l);
	static final Long l2 = Long.valueOf(2l);
	static final Long l3 = Long.valueOf(3l);
	static final Long l4 = Long.valueOf(4l);
	static final Long l5 = Long.valueOf(5l);
	static final Long l6 = Long.valueOf(6l);
	static final Long l7 = Long.valueOf(7l);
	static final Long l8 = Long.valueOf(8l);
	static final Long l9 = Long.valueOf(9l);
	static final Long l10= Long.valueOf(10l);
	static final Long l18= Long.valueOf(18l);

	static final Double d1 = Double.valueOf(1.1);
	static final Double d2 = Double.valueOf(2.2);
	static final Double d3 = Double.valueOf(3.3);
	static final Double d4 = Double.valueOf(4.4);
	static final Double d5 = Double.valueOf(5.5);
	static final Double d6 = Double.valueOf(6.6);
	static final Double d7 = Double.valueOf(7.7);
	static final Double d8 = Double.valueOf(8.8);

	public static void assertEqualContent(final byte[] expectedData, final File actualFile) throws IOException
	{
		if(expectedData==null)
			assertFalse(actualFile.exists());
		else
		{
			assertTrue(actualFile.exists());
			assertEquals(expectedData.length, actualFile.length());

			final byte[] actualData = new byte[expectedData.length];
			try(FileInputStream in = new FileInputStream(actualFile))
			{
				assertEquals(expectedData.length, in.read(actualData));
			}

			for(int i = 0; i<expectedData.length; i++)
				assertEquals(expectedData[i], actualData[i]);

			delete(actualFile);
		}
	}

	static void assertDelete(final Item item) throws IntegrityViolationException
	{
		assertTrue(item.existsCopeItem());
		item.deleteCopeItem();
		assertTrue(!item.existsCopeItem());
	}

	static void assertDeleteFails(final Item item, final ItemField<?> attribute)
	{
		assertDeleteFails(item, attribute, item);
	}

	static void assertDeleteFails(final Item item, final ItemField<?> attribute, final int referrers)
	{
		assertDeleteFails(item, attribute, item, referrers);
	}

	static void assertDeleteFails(final Item item, final ItemField<?> attribute, final Item itemToBeDeleted)
	{
		assertDeleteFails(item, attribute, itemToBeDeleted, 1);
	}

	static void assertDeleteFails(final Item item, final ItemField<?> attribute, final Item itemToBeDeleted, final int referrers)
	{
		try
		{
			item.deleteCopeItem();
			fail();
		}
		catch(final IntegrityViolationException e)
		{
			assertSame(attribute, e.getFeature());
			assertSame(attribute, e.getFeature());
			assertEquals(itemToBeDeleted, e.getItem());
			assertEquals(
					"integrity violation " +
					"on deletion of " + itemToBeDeleted.getCopeID() +
					" because of " + e.getFeature() +
					" referring to " + referrers + " item(s)",
					e.getMessage());
		}
		assertTrue(item.existsCopeItem());
	}

	public static <I extends Item> I activate(final Type<I> type, final long pk)
	{
		return type.activate(pk);
	}

	public static void assertTestAnnotationNull(final Type<?> ae)
	{
		assertFalse(ae.isAnnotationPresent(TestAnnotation.class));
		assertNull(ae.getAnnotation(TestAnnotation.class));
	}

	public static void assertTestAnnotationNull(final Feature ae)
	{
		assertFalse(ae.isAnnotationPresent(TestAnnotation.class));
		assertNull(ae.getAnnotation(TestAnnotation.class));
	}

	public static void assertTestAnnotation2Null(final Type<?> ae)
	{
		assertFalse(ae.isAnnotationPresent(TestAnnotation2.class));
		assertEquals(null, ae.getAnnotation(TestAnnotation2.class));
	}

	public static void assertTestAnnotation2Null(final Feature ae)
	{
		assertFalse(ae.isAnnotationPresent(TestAnnotation2.class));
		assertNull(ae.getAnnotation(TestAnnotation2.class));
	}

	public static void assertTestAnnotation(final String value, final Type<?> ae)
	{
		assertTrue(ae.isAnnotationPresent(TestAnnotation.class));
		assertEquals(value, ae.getAnnotation(TestAnnotation.class).value());
	}

	public static void assertTestAnnotation(final String value, final Feature ae)
	{
		assertTrue(ae.isAnnotationPresent(TestAnnotation.class));
		assertEquals(value, ae.getAnnotation(TestAnnotation.class).value());
	}

	public static void assertTestAnnotation2(final Feature ae)
	{
		assertTrue(ae.isAnnotationPresent(TestAnnotation2.class));
		assertNotNull(ae.getAnnotation(TestAnnotation2.class));
	}
}
