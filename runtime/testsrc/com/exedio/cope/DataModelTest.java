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

import static com.exedio.cope.DataField.DEFAULT_LENGTH;
import static com.exedio.cope.DataField.min;
import static com.exedio.cope.DataField.toValue;
import static com.exedio.cope.DataItem.TYPE;
import static com.exedio.cope.DataItem.data;
import static com.exedio.cope.DataItem.data10;
import static com.exedio.cope.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.EqualsAssert.assertNotEqualsAndHash;
import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.ByteArrayInputStream;
import java.io.File;
import org.junit.Test;

public class DataModelTest
{
	public static final Model MODEL = new Model(TYPE, DataSubItem.TYPE);

	static
	{
		MODEL.enableSerialization(DataModelTest.class, "MODEL");
	}

	@Test public void testMin() throws MandatoryViolationException
	{
		assertEquals(0, min(0, 0l));
		assertEquals(0, min(Integer.MAX_VALUE, 0l));
		assertEquals(0, min(0, Long.MAX_VALUE));
		assertEquals(4, min(5, 4l));
		assertEquals(5, min(5, 5l));
		assertEquals(5, min(5, 6l));
		assertEquals(5, min(5, Integer.MAX_VALUE));
		assertEquals(5, min(5, Long.MAX_VALUE));
		assertEquals(Integer.MAX_VALUE, min(Integer.MAX_VALUE, Long.MAX_VALUE));
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_INFERRED")
	@Test public void testMinLeftNegative() throws MandatoryViolationException
	{
		try
		{
			min(-1, -1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("i must not be negative, but was -1", e.getMessage());
		}
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_INFERRED")
	@Test public void testMinRightNegative() throws MandatoryViolationException
	{
		try
		{
			min(0, -1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("l must not be negative, but was -1", e.getMessage());
		}
	}

	@Test public void testValueToString() throws MandatoryViolationException
	{
		assertEquals("DataField.Value:aa7af817", toValue(bytes4).toString());
		assertEquals("DataField.Value:9f13f82382aa7a5613f8", toValue(bytes10).toString());
		assertEquals("DataField.Value:169f13f82382aa7a5613...(11)", toValue(bytes11).toString());
		assertEquals("DataField.Value:ca47aa7af817e968c12c...(21)", toValue(bytes21).toString());
		final ByteArrayInputStream testBaos = new ByteArrayInputStream(bytes4);
		assertEquals("DataField.Value:"+testBaos.toString(), toValue(testBaos).toString());
		assertEquals("DataField.Value:hallo.txt", toValue(new File("hallo.txt")).toString());
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_INFERRED")
	@Test public void testData() throws MandatoryViolationException
	{
		assertEquals(TYPE, data.getType());
		assertEquals("data", data.getName());
		assertEquals(false, data.isMandatory());
		assertEquals(null, data.getPattern());
		assertEquals(DEFAULT_LENGTH, data.getMaximumLength());
		assertEquals(DataField.Value.class, data.getValueClass());

		assertEquals(TYPE, data10.getType());
		assertEquals("data10", data10.getName());
		assertEquals(false, data10.isMandatory());
		assertEquals(null, data10.getPattern());
		assertEquals(10, data10.getMaximumLength());
		assertEquals(DataField.Value.class, data10.getValueClass());

		assertSerializedSame(data  , 367);
		assertSerializedSame(data10, 369);

		try
		{
			new DataField().lengthMax(0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("maximum length must be greater zero, but was 0.", e.getMessage());
		}
		try
		{
			new DataField().lengthMax(-10);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("maximum length must be greater zero, but was -10.", e.getMessage());
		}

		// condition startsWith
		assertEqualsAndHash(data.startsWith(bytes4), data.startsWith(bytes4));
		assertNotEqualsAndHash(
				data.startsWith(bytes4),
				data.startsWith(bytes6),
				data.startsWith(bytes6x4),
				data10.startsWith(bytes4));
		assertEquals("DataItem.data startsWith 'aa7af817'", data.startsWith(bytes4).toString());
		try
		{
			new StartsWithCondition(null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("field", e.getMessage());
		}
		try
		{
			new StartsWithCondition(data, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("value", e.getMessage());
		}
		try
		{
			data.startsWith(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("value", e.getMessage());
		}
		// TODO treat as to isNotNull
		try
		{
			data.startsWith(bytes0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("value must not be empty", e.getMessage());
		}
	}

	private static final byte[] bytes0  = {};
	private static final byte[] bytes4  = {-86,122,-8,23};
	private static final byte[] bytes6  = {-97,35,-126,86,19,-8};
	private static final byte[] bytes6x4= {-97,35,-126,86};
	private static final byte[] bytes10 = {-97,19,-8,35,-126,-86,122,86,19,-8};
	private static final byte[] bytes11 = {22,-97,19,-8,35,-126,-86,122,86,19,-8};
	private static final byte[] bytes21 = {-54,71,-86,122,-8,23,-23,104,-63,44,23,19,-45,-63,23,71,-23,19,-45,71,-23};
}
