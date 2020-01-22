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

import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.CopyChoiceMultiTest.Container;
import com.exedio.cope.CopyChoiceMultiTest.PartA;
import com.exedio.cope.CopyChoiceMultiTest.PartB;
import org.junit.jupiter.api.Test;

public class CopyChoiceMultiModelTest
{
	@Test void testModel()
	{
		final CopyConstraint constraintA = Container.choiceA.getChoice();
		final CopyConstraint constraintB = Container.choiceB.getChoice();

		assertSame(Container.choiceA, constraintA.getTarget());
		assertSame(Container.choiceB, constraintB.getTarget());
		assertEquals(true, constraintA.isChoice());
		assertEquals(true, constraintB.isChoice());
		assertFails(constraintA::getCopyField, IllegalArgumentException.class, "Container.choiceAChoiceparent is choice");
		assertFails(constraintB::getCopyField, IllegalArgumentException.class, "Container.choiceBChoiceparent is choice");
		assertSame(Container.TYPE.getThis(), constraintA.getCopyFunction());
		assertSame(Container.TYPE.getThis(), constraintB.getCopyFunction());
		assertSame(PartA.parent, constraintA.getTemplate());
		assertSame(PartB.parent, constraintB.getTemplate());

		assertEqualsUnmodifiable(asList(constraintA, constraintB), Container.TYPE.getCopyConstraints());
		assertEqualsUnmodifiable(asList(constraintA, constraintB), Container.TYPE.getDeclaredCopyConstraints());
		assertEqualsUnmodifiable(asList(), PartA.TYPE.getCopyConstraints());
		assertEqualsUnmodifiable(asList(), PartA.TYPE.getDeclaredCopyConstraints());
		assertEqualsUnmodifiable(asList(), PartB.TYPE.getCopyConstraints());
		assertEqualsUnmodifiable(asList(), PartB.TYPE.getDeclaredCopyConstraints());

		assertSame(null, PartA.parent.getChoice());
		assertSame(null, PartB.parent.getChoice());

		assertSame(Container.TYPE, constraintA.getType());
		assertSame(Container.TYPE, constraintB.getType());
		assertEquals("choiceAChoiceparent", constraintA.getName());
		assertEquals("choiceBChoiceparent", constraintB.getName());
		assertEquals("Container.choiceAChoiceparent", constraintA.getID());
		assertEquals("Container.choiceBChoiceparent", constraintB.getID());
	}

	@Deprecated // OK: testing deprecated API
	@Test void testGetCopy()
	{
		final CopyConstraint constraintA = Container.choiceA.getChoice();
		final CopyConstraint constraintB = Container.choiceB.getChoice();

		assertFails(constraintA::getCopy, IllegalArgumentException.class, "Container.choiceAChoiceparent is choice");
		assertFails(constraintB::getCopy, IllegalArgumentException.class, "Container.choiceBChoiceparent is choice");
	}

	@SuppressWarnings("unused") // makes sure model is loaded
	private static final Model MODEL = CopyChoiceMultiTest.MODEL;
}
