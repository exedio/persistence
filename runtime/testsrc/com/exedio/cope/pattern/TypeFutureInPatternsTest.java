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

package com.exedio.cope.pattern;

import static com.exedio.cope.pattern.TypeFutureInPatternsItem.create;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.pattern.TypeFutureInPatternsFeature.TypeItem;
import org.junit.jupiter.api.Test;

public class TypeFutureInPatternsTest extends TestWithEnvironment
{
	public TypeFutureInPatternsTest()
	{
		super(TypeFutureInPatternsModelTest.MODEL);
	}

	@Test void testIt()
	{
		final TypeItem i1 = create(55, null);
		assertEquals(55, i1.getInteger());
		assertEquals(null, i1.getSelf());

		final TypeItem i2 = create(66, i1);
		assertEquals(66, i2.getInteger());
		assertEquals(i1, i2.getSelf());
	}
}
