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

import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.pattern.DispatcherWithoutRemainingItem.TYPE;
import static com.exedio.cope.pattern.DispatcherWithoutRemainingItem.body;
import static com.exedio.cope.pattern.DispatcherWithoutRemainingItem.dispatchCountCommitted;
import static com.exedio.cope.pattern.DispatcherWithoutRemainingItem.toTarget;
import static com.exedio.cope.pattern.DispatcherWithoutRemainingItem.toTargetRunParent;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.misc.Computed;
import java.util.List;
import org.junit.jupiter.api.Test;

public class DispatcherWithoutRemainingModelTest
{
	public static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(DispatcherWithoutRemainingModelTest.class, "MODEL");
	}

	private static final Type<?> runType = toTarget.getRunType();

	@Test void testModel()
	{
		assertEqualsUnmodifiable(list(
				TYPE,
				runType
			), MODEL.getTypes());
		assertEqualsUnmodifiable(list(
				TYPE,
				runType
			), MODEL.getTypesSortedByHierarchy());
		assertEquals(DispatcherWithoutRemainingItem.class, TYPE.getJavaClass());
		assertEquals(true, TYPE.isBound());
		assertEquals(null, TYPE.getPattern());

		final List<PartOf<?>> partOfs = PartOf.getPartOfs(TYPE);
		assertEquals(1, partOfs.size());
		final PartOf<?> partOf = partOfs.get(0);
		assertSame(runType, partOf.getType());
		assertEquals(TYPE, partOf.getContainer().getValueType());
		assertEqualsUnmodifiable(list(toTarget.getRunType()), toTarget.getSourceTypes());
		assertEquals(list(partOf), PartOf.getPartOfs(toTarget));

		assertEqualsUnmodifiable(list(
				TYPE.getThis(),
				body,
				dispatchCountCommitted,
				toTarget,
				toTarget.getPending(),
				toTarget.getNoPurge(),
				toTarget.getUnpend(),
				toTarget.getUnpendSuccess(),
				toTarget.getUnpendDate(),
				toTarget.getUnpendUnison()
			), TYPE.getFeatures());
		assertEqualsUnmodifiable(list(
				runType.getThis(),
				toTargetRunParent(),
				toTarget.getRunDate(),
				toTarget.getRunRuns(),
				toTarget.getRunElapsed(),
				toTarget.getRunResult(),
				toTarget.getRunFailure()
			), runType.getFeatures());
		assertEquals(null, toTarget.getRunRemaining());
		assertEquals(null, toTarget.getRunLimit());

		assertEquals(TYPE, toTarget.getType());
		assertEquals("toTarget", toTarget.getName());

		assertSame(TYPE, toTarget.getPending().getType());
		assertSame("toTarget-pending", toTarget.getPending().getName());
		assertSame(toTarget, toTarget.getPending().getPattern());
		assertSame(Boolean.TRUE, toTarget.getPending().getDefaultConstant());

		assertEquals("DispatcherWithoutRemainingItem-toTarget-Run", runType.getID());
		assertEquals(Dispatcher.Run.class, runType.getJavaClass());
		assertEquals(false, runType.isBound());
		assertSame(toTarget, runType.getPattern());
		assertEquals(null, runType.getSupertype());
		assertEqualsUnmodifiable(list(), runType.getSubtypes());
		assertEquals(false, runType.isAbstract());
		assertEquals(Item.class, runType.getThis().getValueClass().getSuperclass());
		assertEquals(runType, runType.getThis().getValueType());
		assertEquals(MODEL, runType.getModel());

		assertEquals(runType, toTargetRunParent().getType());
		assertEquals(runType, toTarget.getRunDate().getType());
		assertEquals(runType, toTarget.getRunFailure().getType());

		assertEquals("parent", toTargetRunParent().getName());
		assertEquals("date", toTarget.getRunDate().getName());
		assertEquals("failure", toTarget.getRunFailure().getName());

		assertSame(DispatcherWithoutRemainingItem.class, toTargetRunParent().getValueClass());
		assertSame(TYPE, toTargetRunParent().getValueType());

		assertSame(toTargetRunParent(), toTarget.getRunRuns().getContainer());
		assertEquals(asList(PartOf.orderBy(toTarget.getRunDate())), toTarget.getRunRuns().getOrders());
	}

	@Test void testComputed()
	{
		assertFalse(toTarget.getPending().isAnnotationPresent(Computed.class));
		assertFalse(toTarget.getNoPurge().isAnnotationPresent(Computed.class));
		assertTrue (toTarget.getUnpendSuccess().isAnnotationPresent(Computed.class));
		assertTrue (toTarget.getUnpendDate().isAnnotationPresent(Computed.class));
		assertTrue (toTarget.getRunType().isAnnotationPresent(Computed.class));
	}

	@Test void testSerialize()
	{
		assertSerializedSame(toTarget, 423);
	}
}
