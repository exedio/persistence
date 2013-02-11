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

import static com.exedio.cope.AbstractRuntimeTest.assertSerializedSame;
import static com.exedio.cope.CopyMultiSourceItem.TYPE;
import static com.exedio.cope.CopyMultiSourceItem.constraintA;
import static com.exedio.cope.CopyMultiSourceItem.constraintB;
import static com.exedio.cope.CopyMultiSourceItem.copy;
import static com.exedio.cope.CopyMultiSourceItem.targetA;
import static com.exedio.cope.CopyMultiSourceItem.targetB;

import java.util.Arrays;

import com.exedio.cope.junit.CopeAssert;

public class CopyMultiTest extends CopeAssert
{
	public static final Model MODEL = new Model(TYPE, CopyMultiTargetItemA.TYPE, CopyMultiTargetItemB.TYPE);

	static
	{
		MODEL.enableSerialization(CopyMultiTest.class, "MODEL");
	}

	public void testIt()
	{
		assertEquals(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				targetA,
				targetB,
				copy,
				constraintA,
				constraintB,
			}), TYPE.getFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				targetA,
				targetB,
				copy,
				constraintA,
				constraintB,
			}), TYPE.getDeclaredFeatures());

		assertEquals(TYPE, targetA.getType());
		assertEquals(TYPE, targetB.getType());
		assertEquals(TYPE, copy.getType());
		assertEquals(TYPE, constraintA.getType());
		assertEquals(TYPE, constraintB.getType());

		assertEquals("targetA", targetA.getName());
		assertEquals("targetB", targetB.getName());
		assertEquals("copy", copy.getName());
		assertEquals("constraintA", constraintA.getName());
		assertEquals("constraintB", constraintB.getName());

		assertEqualsUnmodifiable(list(constraintA, constraintB), TYPE.getDeclaredCopyConstraints());
		assertEqualsUnmodifiable(list(constraintA, constraintB), TYPE.getCopyConstraints());

		assertSame(targetA, constraintA.getTarget());
		assertSame(targetB, constraintB.getTarget());

		assertSame(CopyMultiTargetItemA.copy, constraintA.getTemplate());
		assertSame(CopyMultiTargetItemB.copy, constraintB.getTemplate());

		assertSame(copy, constraintA.getCopy());
		assertSame(copy, constraintB.getCopy());

		assertEquals(null, targetA.getImplicitCopyConstraint());
		assertEquals(null, targetB.getImplicitCopyConstraint());
		assertEquals(null, copy.getImplicitCopyConstraint());

		assertSerializedSame(constraintA, 385);
		assertSerializedSame(constraintB, 385);
	}
}
