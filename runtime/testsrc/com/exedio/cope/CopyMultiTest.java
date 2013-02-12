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
		final CopyConstraint constraintA = (CopyConstraint)TYPE.getFeature("copyCopyFromtargetA");
		final CopyConstraint constraintB = (CopyConstraint)TYPE.getFeature("copyCopyFromtargetB");

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
		assertEquals(CopyMultiTargetItemA.TYPE, CopyMultiTargetItemA.copy.getType());
		assertEquals(CopyMultiTargetItemB.TYPE, CopyMultiTargetItemB.copy.getType());

		assertEquals("targetA", targetA.getName());
		assertEquals("targetB", targetB.getName());
		assertEquals("copy", copy.getName());
		assertEquals("copyCopyFromtargetA", constraintA.getName());
		assertEquals("copyCopyFromtargetB", constraintB.getName());
		assertEquals("copy", CopyMultiTargetItemA.copy.getName());
		assertEquals("copy", CopyMultiTargetItemB.copy.getName());

		assertEqualsUnmodifiable(list(constraintA, constraintB), TYPE.getDeclaredCopyConstraints());
		assertEqualsUnmodifiable(list(constraintA, constraintB), TYPE.getCopyConstraints());
		assertEqualsUnmodifiable(list(), CopyMultiTargetItemA.TYPE.getDeclaredCopyConstraints());
		assertEqualsUnmodifiable(list(), CopyMultiTargetItemA.TYPE.getCopyConstraints());
		assertEqualsUnmodifiable(list(), CopyMultiTargetItemB.TYPE.getDeclaredCopyConstraints());
		assertEqualsUnmodifiable(list(), CopyMultiTargetItemB.TYPE.getCopyConstraints());

		assertSame(targetA, constraintA.getTarget());
		assertSame(targetB, constraintB.getTarget());

		assertSame(CopyMultiTargetItemA.copy, constraintA.getTemplate());
		assertSame(CopyMultiTargetItemB.copy, constraintB.getTemplate());

		assertSame(copy, constraintA.getCopy());
		assertSame(copy, constraintB.getCopy());

		assertEqualsUnmodifiable(list(), targetA.getImplicitCopyConstraints());
		assertEqualsUnmodifiable(list(), targetB.getImplicitCopyConstraints());
		assertEqualsUnmodifiable(list(constraintA, constraintB), copy.getImplicitCopyConstraints());
		assertEqualsUnmodifiable(list(), CopyMultiTargetItemA.copy.getImplicitCopyConstraints());
		assertEqualsUnmodifiable(list(), CopyMultiTargetItemB.copy.getImplicitCopyConstraints());

		assertSerializedSame(constraintA, 393);
		assertSerializedSame(constraintB, 393);
	}

	@SuppressWarnings("deprecation")
	public void testDeprecated()
	{
		assertEquals(null, targetA.getImplicitCopyConstraint());
		assertEquals(null, targetB.getImplicitCopyConstraint());
		try
		{
			copy.getImplicitCopyConstraint();
			fail();
		}
		catch(final RuntimeException e)
		{
			assertEquals(
					"[CopyMultiSourceItem.copyCopyFromtargetA, CopyMultiSourceItem.copyCopyFromtargetB]",
					e.getMessage());
		}
		assertEquals(null, CopyMultiTargetItemA.copy.getImplicitCopyConstraint());
		assertEquals(null, CopyMultiTargetItemB.copy.getImplicitCopyConstraint());
	}
}
