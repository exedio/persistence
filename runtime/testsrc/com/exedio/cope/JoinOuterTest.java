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

import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;

import com.exedio.cope.testmodel.PointerItem;
import com.exedio.cope.testmodel.PointerTargetItem;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JoinOuterTest extends TestmodelTest
{
	PointerItem leftJoined;
	PointerItem leftLonely;
	PointerTargetItem rightJoined;
	@SuppressFBWarnings("URF_UNREAD_FIELD")
	PointerTargetItem rightLonely;

	@BeforeEach public final void setUp()
	{
		rightLonely = new PointerTargetItem("right");
		rightJoined = new PointerTargetItem("joined");
		leftJoined = new PointerItem("joined", rightJoined);
		leftLonely = new PointerItem("left", rightJoined);
	}

	@Test void testJoin()
	{
		{
			final Query<PointerItem> query = PointerItem.TYPE.newQuery(null);
			assertEqualsUnmodifiable(list(), query.getJoins());
			final Join join = query.join(PointerTargetItem.TYPE, PointerItem.code.equal(PointerTargetItem.code));
			assertEqualsUnmodifiable(list(join), query.getJoins());
			assertContains(leftJoined, query.search());
		}
		{
			final Query<PointerItem> query = PointerItem.TYPE.newQuery(null);
			assertEqualsUnmodifiable(list(), query.getJoins());
			final Join join = query.join(PointerTargetItem.TYPE, PointerItem.code.toUpperCase().equal(PointerTargetItem.code.toUpperCase()));
			assertEqualsUnmodifiable(list(join), query.getJoins());
			assertContains(leftJoined, query.search());
		}
		{
			final Query<PointerItem> query = PointerItem.TYPE.newQuery(null);
			assertEqualsUnmodifiable(list(), query.getJoins());
			final Join join = query.join(PointerTargetItem.TYPE, PointerItem.code.toUpperCase().equal(PointerTargetItem.code));
			assertEqualsUnmodifiable(list(join), query.getJoins());
			assertContains(query.search());
		}
		{
			final Query<PointerItem> query = PointerItem.TYPE.newQuery(null);
			assertEqualsUnmodifiable(list(), query.getJoins());
			final Join join = query.joinOuterLeft(PointerTargetItem.TYPE, PointerItem.code.equal(PointerTargetItem.code));
			assertEqualsUnmodifiable(list(join), query.getJoins());
			assertContains(leftJoined, leftLonely, query.search());
		}
		{
			final Query<PointerItem> query = PointerItem.TYPE.newQuery(null);
			assertEqualsUnmodifiable(list(), query.getJoins());
			final Join join = query.joinOuterRight(PointerTargetItem.TYPE, PointerItem.code.equal(PointerTargetItem.code));
			assertEqualsUnmodifiable(list(join), query.getJoins());
			assertContains(leftJoined, null, query.search());
		}
	}

}
