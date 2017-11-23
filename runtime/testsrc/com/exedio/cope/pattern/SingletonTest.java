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

import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.pattern.SingletonItem.TYPE;
import static com.exedio.cope.pattern.SingletonItem.booleanField;
import static com.exedio.cope.pattern.SingletonItem.einzigartig;
import static com.exedio.cope.pattern.SingletonItem.integer;
import static com.exedio.cope.pattern.SingletonItem.integer55;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.misc.Computed;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class SingletonTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(SingletonTest.class, "MODEL");
	}

	public SingletonTest()
	{
		super(MODEL);
	}

	@Test void testIt()
	{
		// test model
		assertEquals(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				integer,
				integer55,
				booleanField,
				einzigartig,
				einzigartig.getSource(),
				einzigartig.getSource().getImplicitUniqueConstraint(),
			}), TYPE.getFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				integer,
				integer55,
				booleanField,
				einzigartig,
				einzigartig.getSource(),
				einzigartig.getSource().getImplicitUniqueConstraint(),
			}), TYPE.getDeclaredFeatures());

		assertEquals(TYPE, einzigartig.getSource().getType());
		assertEquals(TYPE, einzigartig.getType());
		assertEquals("einzigartig-once",einzigartig.getSource().getName());
		assertEquals("einzigartig",     einzigartig.getName());

		assertEquals(einzigartig, einzigartig.getSource().getPattern());

		assertFalse(einzigartig.            isAnnotationPresent(Computed.class));
		assertTrue (einzigartig.getSource().isAnnotationPresent(Computed.class));

		assertSerializedSame(einzigartig, 387);

		// test persistence
		assertEquals("einzigartig_once", SchemaInfo.getColumnName(einzigartig.getSource()));

		assertEquals(list(), TYPE.search());

		final SingletonItem theOne = SingletonItem.instance();
		assertEquals(list(theOne), TYPE.search());
		assertEquals(null, theOne.getInteger());
		assertEquals(55, theOne.getInteger55());
		assertEquals(true, theOne.getBooleanField());
		assertEquals(theOne, SingletonItem.instance());
	}
}
