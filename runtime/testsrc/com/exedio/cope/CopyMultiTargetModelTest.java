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

import static com.exedio.cope.CopyMultiTargetSource.TYPE;
import static com.exedio.cope.CopyMultiTargetSource.copy;
import static com.exedio.cope.CopyMultiTargetSource.targetA;
import static com.exedio.cope.CopyMultiTargetSource.targetB;
import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class CopyMultiTargetModelTest
{
	public static final Model MODEL = new Model(TYPE, CopyMultiTargetA.TYPE, CopyMultiTargetB.TYPE);

	static
	{
		MODEL.enableSerialization(CopyMultiTargetModelTest.class, "MODEL");
	}

	static final CopyConstraint constraintA = (CopyConstraint)TYPE.getFeature("copyCopyFromtargetA");
	static final CopyConstraint constraintB = (CopyConstraint)TYPE.getFeature("copyCopyFromtargetB");

	@Test void testIt()
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
		assertEquals(CopyMultiTargetA.TYPE, CopyMultiTargetA.copy.getType());
		assertEquals(CopyMultiTargetB.TYPE, CopyMultiTargetB.copy.getType());

		assertEquals("targetA", targetA.getName());
		assertEquals("targetB", targetB.getName());
		assertEquals("copy", copy.getName());
		assertEquals("copyCopyFromtargetA", constraintA.getName());
		assertEquals("copyCopyFromtargetB", constraintB.getName());
		assertEquals("copy", CopyMultiTargetA.copy.getName());
		assertEquals("copy", CopyMultiTargetB.copy.getName());

		assertEqualsUnmodifiable(list(constraintA, constraintB), TYPE.getDeclaredCopyConstraints());
		assertEqualsUnmodifiable(list(constraintA, constraintB), TYPE.getCopyConstraints());
		assertEqualsUnmodifiable(list(), CopyMultiTargetA.TYPE.getDeclaredCopyConstraints());
		assertEqualsUnmodifiable(list(), CopyMultiTargetA.TYPE.getCopyConstraints());
		assertEqualsUnmodifiable(list(), CopyMultiTargetB.TYPE.getDeclaredCopyConstraints());
		assertEqualsUnmodifiable(list(), CopyMultiTargetB.TYPE.getCopyConstraints());

		assertSame(targetA, constraintA.getTarget());
		assertSame(targetB, constraintB.getTarget());

		assertSame(CopyMultiTargetA.copy, constraintA.getTemplate());
		assertSame(CopyMultiTargetB.copy, constraintB.getTemplate());

		assertEquals(false, constraintA.isChoice());
		assertEquals(false, constraintB.isChoice());

		assertSame(copy, constraintA.getCopyField());
		assertSame(copy, constraintB.getCopyField());

		assertSame(copy, constraintA.getCopyFunction());
		assertSame(copy, constraintB.getCopyFunction());

		assertSerializedSame(constraintA, 406);
		assertSerializedSame(constraintB, 406);
	}
}
