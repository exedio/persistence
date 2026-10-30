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
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperIgnore;
import java.io.Serial;
import org.junit.jupiter.api.Test;

public class ValueClassTest
{
	@Test void testFinal()
	{
		final var vc = ValueClass.of(Number.class);
		assertEquals(true, vc.isFinal());
		assertEquals(Number.class, vc.get());

		@SuppressWarnings({"unchecked", "rawtypes"}) // OK: test bad API usage
		final Class<Number> classToBeSet = (Class)Integer.class;
		assertFails(
				() -> vc.setFinally(classToBeSet),
				IllegalStateException.class,
				"already final");
		assertEquals(true, vc.isFinal());
		assertEquals(Number.class, vc.get());
	}

	@Test void testPreliminaryItem()
	{
		final ValueClass<Item> vc = ValueClass.preliminaryItem(PreliminaryItem.class);
		assertEquals(false, vc.isFinal());
		assertEquals(PreliminaryItem.class, vc.get());

		@SuppressWarnings({"unchecked", "rawtypes"}) // OK: test bad API usage
		final Class<Item> classToBeSet = (Class)FinalItem.class;
		vc.setFinally(classToBeSet);
		assertEquals(true, vc.isFinal());
		assertEquals(FinalItem.class, vc.get());

		@SuppressWarnings({"unchecked", "rawtypes"}) // OK: test bad API usage
		final Class<Item> classToBeSet2 = (Class)Double.class;
		assertFails(
				() -> vc.setFinally(classToBeSet2),
				IllegalStateException.class,
				"already final");
		assertEquals(true, vc.isFinal());
		assertEquals(FinalItem.class, vc.get());
	}

	@WrapperIgnore
	abstract static class PreliminaryItem extends Item
	{
		@Serial private static final long serialVersionUID = 1l;
	}

	@WrapperIgnore
	static final class FinalItem extends PreliminaryItem
	{
		@Serial private static final long serialVersionUID = 1l;
	}
}
