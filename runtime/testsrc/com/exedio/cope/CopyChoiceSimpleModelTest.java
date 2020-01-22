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

import com.exedio.cope.CopyChoiceSimpleTest.Container;
import com.exedio.cope.CopyChoiceSimpleTest.Part;
import org.junit.jupiter.api.Test;

public class CopyChoiceSimpleModelTest
{
	@Test void testModel()
	{
		final CopyConstraint constraint = Container.choice.getChoice();

		assertSame(Container.choice, constraint.getTarget());
		assertEquals(true, constraint.isChoice());
		assertFails(constraint::getCopyField, IllegalArgumentException.class, "Container.choiceChoiceparent is choice");
		assertSame(Container.TYPE.getThis(), constraint.getCopyFunction());
		assertSame(Part.parent, constraint.getTemplate());

		assertEqualsUnmodifiable(asList(constraint), Container.TYPE.getCopyConstraints());
		assertEqualsUnmodifiable(asList(constraint), Container.TYPE.getDeclaredCopyConstraints());
		assertEqualsUnmodifiable(asList(), Part.TYPE.getCopyConstraints());
		assertEqualsUnmodifiable(asList(), Part.TYPE.getDeclaredCopyConstraints());

		assertSame(null, Part.parent.getChoice());

		assertSame(Container.TYPE, constraint.getType());
		assertEquals("choiceChoiceparent", constraint.getName());
		assertEquals("Container.choiceChoiceparent", constraint.getID());
	}

	@Deprecated // OK: testing deprecated API
	@Test void testGetCopy()
	{
		final CopyConstraint constraint = Container.choice.getChoice();

		assertFails(constraint::getCopy, IllegalArgumentException.class, "Container.choiceChoiceparent is choice");
	}

	@SuppressWarnings("unused") // makes sure model is loaded
	private static final Model MODEL = CopyChoiceSimpleTest.MODEL;
}
