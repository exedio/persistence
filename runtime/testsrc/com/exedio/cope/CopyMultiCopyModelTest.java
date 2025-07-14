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

import static com.exedio.cope.CopyMultiCopySource.TYPE;
import static com.exedio.cope.CopyMultiCopySource.copyA;
import static com.exedio.cope.CopyMultiCopySource.copyB;
import static com.exedio.cope.CopyMultiCopySource.target;
import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class CopyMultiCopyModelTest
{
	public static final Model MODEL = new Model(TYPE, CopyMultiCopyTarget.TYPE);

	static
	{
		MODEL.enableSerialization(CopyMultiCopyModelTest.class, "MODEL");
	}

	static final CopyConstraint constraintA = (CopyConstraint)TYPE.getFeature("copyACopyFromtarget");
	static final CopyConstraint constraintB = (CopyConstraint)TYPE.getFeature("copyBCopyFromtarget");

	@Test void testIt()
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
		assertEquals(CopyMultiCopyTarget.TYPE, CopyMultiCopyTarget.copyA.getType());
		assertEquals(CopyMultiCopyTarget.TYPE, CopyMultiCopyTarget.copyB.getType());

		assertEquals("copyA", copyA.getName());
		assertEquals("copyB", copyB.getName());
		assertEquals("target", target.getName());
		assertEquals("copyACopyFromtarget", constraintA.getName());
		assertEquals("copyBCopyFromtarget", constraintB.getName());
		assertEquals("copyA", CopyMultiCopyTarget.copyA.getName());
		assertEquals("copyB", CopyMultiCopyTarget.copyB.getName());

		assertEqualsUnmodifiable(list(constraintA, constraintB), TYPE.getDeclaredCopyConstraints());
		assertEqualsUnmodifiable(list(constraintA, constraintB), TYPE.getCopyConstraints());
		assertEqualsUnmodifiable(list(), CopyMultiCopyTarget.TYPE.getDeclaredCopyConstraints());
		assertEqualsUnmodifiable(list(), CopyMultiCopyTarget.TYPE.getCopyConstraints());

		assertSame(target, constraintA.getTarget());
		assertSame(target, constraintB.getTarget());

		assertSame(CopyMultiCopyTarget.copyA, constraintA.getTemplate());
		assertSame(CopyMultiCopyTarget.copyB, constraintB.getTemplate());

		assertEquals(false, constraintA.isChoice());
		assertEquals(false, constraintB.isChoice());

		assertSame(copyA, constraintA.getCopyField());
		assertSame(copyB, constraintB.getCopyField());

		assertSame(copyA, constraintA.getCopyFunction());
		assertSame(copyB, constraintB.getCopyFunction());

		assertSerializedSame(constraintA, 410);
		assertSerializedSame(constraintB, 410);
	}
}
