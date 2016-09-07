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
import static com.exedio.cope.tojunit.Assert.list;

import com.exedio.cope.instrument.WrapperIgnore;
import org.junit.Test;

public class TypeSetModelTest
{
	@Test public void testIt()
	{
		final Type<Item1> type1 = TypesBound.newType(Item1.class);
		final TypeSet typeSet1 = new TypeSet(new Type<?>[]{type1});
		new TypeSet(new Type<?>[]{type1});

		final Model model = new Model((Revisions.Factory)null, new TypeSet[]{typeSet1});
		assertEqualsUnmodifiable(list(type1), model.getTypes());
		assertEqualsUnmodifiable(list(type1), model.getTypesSortedByHierarchy());

		new TypeSet(new Type<?>[]{type1});
	}

	@WrapperIgnore
	static class Item1 extends Item
	{
		private static final long serialVersionUID = 1l;
		private Item1(final ActivationParameters ap) { super(ap); }
	}
}
