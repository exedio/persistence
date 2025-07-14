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

import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.UniqueDoubleItem.TYPE;
import static com.exedio.cope.UniqueDoubleItem.constraint;
import static com.exedio.cope.UniqueDoubleItem.integer;
import static com.exedio.cope.UniqueDoubleItem.string;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class UniqueDoubleModelTest
{
	static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(UniqueDoubleModelTest.class, "MODEL");
	}

	@Test void test()
	{
		assertEqualsUnmodifiable(list(TYPE.getThis(), string, integer, constraint), TYPE.getDeclaredFeatures());
		assertEqualsUnmodifiable(list(TYPE.getThis(), string, integer, constraint), TYPE.getFeatures());
		assertEquals("constraint", constraint.getName());
		assertEquals(TYPE, constraint.getType());
		assertEqualsUnmodifiable(list(string, integer), constraint.getFields());
		assertEqualsUnmodifiable(list(constraint), string.getUniqueConstraints());
		assertEqualsUnmodifiable(list(constraint), integer.getUniqueConstraints());

		assertSerializedSame(constraint, 397);
	}
}
