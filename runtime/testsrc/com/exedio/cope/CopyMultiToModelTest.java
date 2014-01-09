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

import static com.exedio.cope.CopyMultiToSourceItem.TYPE;
import static com.exedio.cope.CopyMultiToSourceItem.copyA;
import static com.exedio.cope.CopyMultiToSourceItem.copyB;
import static com.exedio.cope.CopyMultiToSourceItem.target;
import static com.exedio.cope.RuntimeAssert.assertSerializedSame;

import com.exedio.cope.junit.CopeAssert;
import java.util.Arrays;

public class CopyMultiToModelTest extends CopeAssert
{
	public static final Model MODEL = new Model(TYPE, CopyMultiToTargetItem.TYPE);

	static
	{
		MODEL.enableSerialization(CopyMultiToModelTest.class, "MODEL");
	}

	static final CopyConstraint constraintA = (CopyConstraint)TYPE.getFeature("copyACopyFromtarget");
	static final CopyConstraint constraintB = (CopyConstraint)TYPE.getFeature("copyBCopyFromtarget");

	public void testIt()
	{
		assertEquals(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				copyA,
				copyB,
				target,
				constraintA,
				constraintB,
			}), TYPE.getFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				copyA,
				copyB,
				target,
				constraintA,
				constraintB,
			}), TYPE.getDeclaredFeatures());

		assertEquals(TYPE, copyA.getType());
		assertEquals(TYPE, copyB.getType());
		assertEquals(TYPE, target.getType());
		assertEquals(TYPE, constraintA.getType());
		assertEquals(TYPE, constraintB.getType());
		assertEquals(CopyMultiToTargetItem.TYPE, CopyMultiToTargetItem.copyA.getType());
		assertEquals(CopyMultiToTargetItem.TYPE, CopyMultiToTargetItem.copyB.getType());

		assertEquals("copyA", copyA.getName());
		assertEquals("copyB", copyB.getName());
		assertEquals("target", target.getName());
		assertEquals("copyACopyFromtarget", constraintA.getName());
		assertEquals("copyBCopyFromtarget", constraintB.getName());
		assertEquals("copyA", CopyMultiToTargetItem.copyA.getName());
		assertEquals("copyB", CopyMultiToTargetItem.copyB.getName());

		assertEqualsUnmodifiable(list(constraintA, constraintB), TYPE.getDeclaredCopyConstraints());
		assertEqualsUnmodifiable(list(constraintA, constraintB), TYPE.getCopyConstraints());
		assertEqualsUnmodifiable(list(), CopyMultiToTargetItem.TYPE.getDeclaredCopyConstraints());
		assertEqualsUnmodifiable(list(), CopyMultiToTargetItem.TYPE.getCopyConstraints());

		assertSame(target, constraintA.getTarget());
		assertSame(target, constraintB.getTarget());

		assertSame(CopyMultiToTargetItem.copyA, constraintA.getTemplate());
		assertSame(CopyMultiToTargetItem.copyB, constraintB.getTemplate());

		assertSame(copyA, constraintA.getCopy());
		assertSame(copyB, constraintB.getCopy());

		assertSerializedSame(constraintA, 402);
		assertSerializedSame(constraintB, 402);
	}

	@SuppressWarnings("deprecation")
	public void testDeprecated()
	{
		assertEqualsUnmodifiable(list(), copyA.getImplicitCopyConstraints());
		assertEqualsUnmodifiable(list(), copyB.getImplicitCopyConstraints());
		assertEqualsUnmodifiable(list(), target.getImplicitCopyConstraints());
		assertEqualsUnmodifiable(list(), CopyMultiToTargetItem.copyA.getImplicitCopyConstraints());
		assertEqualsUnmodifiable(list(), CopyMultiToTargetItem.copyB.getImplicitCopyConstraints());

		assertEquals(null, target.getImplicitCopyConstraint());
		assertEquals(null, copyA.getImplicitCopyConstraint());
		assertEquals(null, copyB.getImplicitCopyConstraint());
		assertEquals(null, CopyMultiToTargetItem.copyA.getImplicitCopyConstraint());
		assertEquals(null, CopyMultiToTargetItem.copyB.getImplicitCopyConstraint());
	}
}
