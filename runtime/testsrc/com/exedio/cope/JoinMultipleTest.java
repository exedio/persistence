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

import static com.exedio.cope.testmodel.PointerItem.pointer;
import static com.exedio.cope.testmodel.PointerItem.pointer2;
import static com.exedio.cope.testmodel.PointerTargetItem.code;
import static com.exedio.cope.testmodel.PointerTargetItem.num1;
import static com.exedio.cope.testmodel.PointerTargetItem.num2;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.testmodel.PointerItem;
import com.exedio.cope.testmodel.PointerTargetItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JoinMultipleTest extends TestmodelTest
{
	PointerItem source;
	PointerTargetItem target1;
	PointerTargetItem target2;

	@BeforeEach final void setUp()
	{
		target1 = new PointerTargetItem("target1");
		target2 = new PointerTargetItem("target2");
		source = new PointerItem("source", target1);
		source.setPointer2(target2);
	}

	@Test void testMultipleJoin()
	{
		{
			final Query<PointerItem> query = PointerItem.TYPE.newQuery(null);
			assertEqualsUnmodifiable(list(), query.getJoins());

			final Join join1 = query.join(PointerTargetItem.TYPE);
			assertEqualsUnmodifiable(list(join1), query.getJoins());
			join1.setCondition(pointer.isTarget(join1));
			assertEqualsUnmodifiable(asList(source), query.search());

			final Join join2 = query.join(PointerTargetItem.TYPE);
			assertEqualsUnmodifiable(list(join1, join2), query.getJoins());
			join2.setCondition(pointer2.isTarget(join2));
			assertEqualsUnmodifiable(list(source), query.search());

			query.setCondition(code.is("target1").bind(join1));
			assertEqualsUnmodifiable(list(source), query.search());
		}
		{
			// test using BindItemFunction
			final Query<PointerItem> query = PointerItem.TYPE.newQuery(null);
			assertEqualsUnmodifiable(list(), query.getJoins());

			final Join join1 = query.join(PointerTargetItem.TYPE);
			assertEqualsUnmodifiable(list(join1), query.getJoins());
			join1.setCondition(pointer.is(PointerTargetItem.TYPE.getThis().bind(join1)));
			assertEqualsUnmodifiable(list(source), query.search());

			final Join join2 = query.join(PointerTargetItem.TYPE);
			assertEqualsUnmodifiable(list(join1, join2), query.getJoins());
			join2.setCondition(pointer2.is(PointerTargetItem.TYPE.getThis().bind(join2)));
			assertEqualsUnmodifiable(list(source), query.search());

			query.setCondition(code.is("target1").bind(join1));
			assertEqualsUnmodifiable(list(source), query.search());

			assertEquals(
					"select this from PointerItem " +
					"join PointerTargetItem p1 on pointer=p1.PointerTargetItem.this " +
					"join PointerTargetItem p2 on pointer2=p2.PointerTargetItem.this " +
					"where p1.PointerTargetItem.code='target1'",
					query.toString());

			// TODO test attributes with wrong join
			// TODO test when join is falsely null
			// TODO test with functions on joined types
		}
		{
			final Query<PointerItem> query = PointerItem.TYPE.newQuery(null);
			final Join join1 = query.join(PointerTargetItem.TYPE);
			query.setCondition((num1.plus(num2)).greater(2));
			assertEquals("select this from PointerItem join PointerTargetItem p1 where plus(PointerTargetItem.num1,PointerTargetItem.num2)>'2'", query.toString());
			query.setCondition((num1.plus(num2)).bind(join1).greater(2));
			assertEquals("select this from PointerItem join PointerTargetItem p1 where plus(p1.PointerTargetItem.num1,p1.PointerTargetItem.num2)>'2'", query.toString());
		}
	}
}
