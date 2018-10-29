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

import static com.exedio.cope.tojunit.Assert.assertFails;

import com.exedio.cope.CopyChoiceSimpleTest.Container;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS")
public class CopyChoiceFailureTest
{
	@Test void testBackPointerNameNull()
	{
		final ItemField<?> target = ItemField.create(Container.class);
		assertFails(
				() -> target.choice(null),
				NullPointerException.class, "backPointerName");
	}

	@Test void testBackPointerNameEmpty()
	{
		final ItemField<?> target = ItemField.create(Container.class);
		assertFails(
				() -> target.choice(""),
				IllegalArgumentException.class,
				"backPointerName must not be empty");
	}

	@Test void testTwice()
	{
		final ItemField<?> target = ItemField.create(Container.class).optional().choice("one");
		assertFails(
				() -> target.choice("two"),
				IllegalArgumentException.class,
				"choice already set: one");
	}

	@Test void testFinal()
	{
		final ItemField<?> target = ItemField.create(Container.class).toFinal();
		assertFails(
				() -> target.choice("one"),
				IllegalArgumentException.class,
				"final item field cannot have choice constraint");
	}

	@Test void testMandatory()
	{
		final ItemField<?> target = ItemField.create(Container.class);
		assertFails(
				() -> target.choice("one"),
				IllegalArgumentException.class,
				"mandatory item field cannot have choice constraint");
	}
}
