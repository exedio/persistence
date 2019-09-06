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
import static com.exedio.cope.DataItem.TYPE;
import static com.exedio.cope.DataItem.data;
import static com.exedio.cope.DataItem.data10;
import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.tojunit.EqualsAssert.assertNotEqualsAndHash;
import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings({
		"RV_RETURN_VALUE_IGNORED_INFERRED",
		"NP_NONNULL_PARAM_VIOLATION",
		"NP_NULL_PARAM_DEREF_NONVIRTUAL",
		"NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS"})
public class DataModelTest
{
	public static final Model MODEL = new Model(TYPE, DataSubItem.TYPE);

	static
	{
		MODEL.enableSerialization(DataModelTest.class, "MODEL");
	}

	@Test void testMin() throws MandatoryViolationException
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

	@Test void testMinLeftNegative() throws MandatoryViolationException
	{
		assertFails(
				() -> min(-1, -1),
				IllegalArgumentException.class,
				"i must not be negative, but was -1");
	}

	@Test void testMinRightNegative() throws MandatoryViolationException
	{
		assertFails(
				() -> min(0, -1),
				IllegalArgumentException.class,
				"l must not be negative, but was -1");
	}

	@Test void testData() throws MandatoryViolationException
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

		assertFails(
				() -> new DataField().lengthMax(0),
				IllegalArgumentException.class,
				"maximumLength must be greater zero, but was 0");
		assertFails(
				() -> new DataField().lengthMax(-10),
				IllegalArgumentException.class,
				"maximumLength must be greater zero, but was -10");

		// condition startsWith
		assertEqualsAndHash(data.startsWithIfSupported(bytes4), data.startsWithIfSupported(bytes4));
		assertNotEqualsAndHash(
				data.startsWithIfSupported(bytes4),
				data.startsWithIfSupported(bytes6),
				data.startsWithIfSupported(bytes6x4),
				data10.startsWithIfSupported(bytes4));
		assertEquals("DataItem.data startsWith 'aa7af817'", data.startsWithIfSupported(bytes4).toString());
	}

	@Test void testSinkNullStream()
	{
		final DataItem item = TYPE.activate(567);
		assertFails(
				() -> item.getData((OutputStream)null),
				NullPointerException.class,
				"sink");
	}

	@Test void testSinkNullPath()
	{
		final DataItem item = TYPE.activate(567);
		assertFails(
				() -> item.getData((Path)null),
				NullPointerException.class,
				"sink");
	}

	@Test void testSinkNullFile()
	{
		final DataItem item = TYPE.activate(567);
		assertFails(
				() -> item.getData((File)null),
				NullPointerException.class,
				"sink");
	}

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testStartsWithFieldNullConstructor()
	{
		assertFails(
				() -> new StartsWithCondition(null, null),
				NullPointerException.class,
				"field");
	}

	@SuppressWarnings("deprecation") // OK: testing deprecated API
	@Test void testStartsWithValueNullConstructor()
	{
		assertFails(
				() -> new StartsWithCondition(data, null),
				NullPointerException.class,
				"value");
	}

	@Test void testStartsWithValueNull()
	{
		assertFails(
				() -> data.startsWithIfSupported(null),
				NullPointerException.class,
				"value");
	}

	@Test void testStartsWithValueEmpty()
	{
		// TODO treat as to isNotNull
		assertFails(
				() -> data.startsWithIfSupported(bytes0),
				IllegalArgumentException.class,
				"value must not be empty");
	}

	private static final byte[] bytes0  = {};
	private static final byte[] bytes4  = {-86,122,-8,23};
	private static final byte[] bytes6  = {-97,35,-126,86,19,-8};
	private static final byte[] bytes6x4= {-97,35,-126,86};


	static void assertNotSupported(
			final Query<?> query,
			final String message)
	{
		assertFails(
				query::search,
				UnsupportedQueryException.class, message);
		assertFails(
				query::total,
				UnsupportedQueryException.class, message);
		assertFails(
				query::exists,
				UnsupportedQueryException.class, message);
		assertFails(
				() -> SchemaInfo.search(query),
				UnsupportedQueryException.class, message);
		assertFails(
				() -> SchemaInfo.total(query),
				UnsupportedQueryException.class, message);
		assertFails(
				() -> SchemaInfo.exists(query),
				UnsupportedQueryException.class, message);
	}
}
