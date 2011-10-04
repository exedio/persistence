/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.AbstractRuntimeTest.assertSerializedSame;

import java.util.List;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.misc.Computed;

public class DispatcherModelTest extends CopeAssert
{
	public static final Model MODEL = new Model(DispatcherItem.TYPE);

	static
	{
		MODEL.enableSerialization(DispatcherModelTest.class, "MODEL");
	}

	DispatcherItem item;

	public void testIt()
	{
		final Type<?> runType = item.toTarget.getRunType();

		assertEqualsUnmodifiable(list(
				item.TYPE,
				runType
			), MODEL.getTypes());
		assertEqualsUnmodifiable(list(
				item.TYPE,
				runType
			), MODEL.getTypesSortedByHierarchy());
		assertEquals(DispatcherItem.class, item.TYPE.getJavaClass());
		assertEquals(true, item.TYPE.isBound());
		assertEquals(null, item.TYPE.getPattern());

		final List<PartOf> partOfs = PartOf.getPartOfs(DispatcherItem.TYPE);
		assertEquals(1, partOfs.size());
		final PartOf partOf = partOfs.get(0);
		assertSame(runType, partOf.getType());
		assertEquals(DispatcherItem.TYPE, partOf.getContainer().getValueType());
		assertEqualsUnmodifiable(list(DispatcherItem.toTarget.getRunType()), DispatcherItem.toTarget.getSourceTypes());
		assertEquals(list(partOf), PartOf.getPartOfs(DispatcherItem.toTarget));

		assertEqualsUnmodifiable(list(
				item.TYPE.getThis(),
				item.body,
				item.dispatchCountCommitted,
				item.toTarget,
				item.toTarget.getPending()
			), item.TYPE.getFeatures());
		assertEqualsUnmodifiable(list(
				runType.getThis(),
				item.toTargetRunParent(),
				item.toTarget.getRunDate(),
				item.toTarget.getRunRuns(),
				item.toTarget.getRunElapsed(),
				item.toTarget.getRunSuccess(),
				item.toTarget.getRunFailure()
			), runType.getFeatures());

		assertEquals(item.TYPE, item.toTarget.getType());
		assertEquals("toTarget", item.toTarget.getName());

		assertSame(item.TYPE, item.toTarget.getPending().getType());
		assertSame("toTarget-pending", item.toTarget.getPending().getName());
		assertSame(item.toTarget, item.toTarget.getPending().getPattern());
		assertSame(Boolean.TRUE, item.toTarget.getPending().getDefaultConstant());

		assertEquals("DispatcherItem-toTarget-Run", runType.getID());
		assertEquals(Dispatcher.Run.class, runType.getJavaClass());
		assertEquals(false, runType.isBound());
		assertSame(DispatcherItem.toTarget, runType.getPattern());
		assertEquals(null, runType.getSupertype());
		assertEqualsUnmodifiable(list(), runType.getSubtypes());
		assertEquals(false, runType.isAbstract());
		assertEquals(Item.class, runType.getThis().getValueClass().getSuperclass());
		assertEquals(runType, runType.getThis().getValueType());
		assertEquals(MODEL, runType.getModel());

		assertEquals(runType, item.toTargetRunParent().getType());
		assertEquals(runType, item.toTarget.getRunDate().getType());
		assertEquals(runType, item.toTarget.getRunFailure().getType());

		assertEquals("parent", item.toTargetRunParent().getName());
		assertEquals("date", item.toTarget.getRunDate().getName());
		assertEquals("failure", item.toTarget.getRunFailure().getName());

		assertSame(DispatcherItem.class, item.toTargetRunParent().getValueClass());
		assertSame(DispatcherItem.TYPE, item.toTargetRunParent().getValueType());

		assertSame(item.toTargetRunParent(), item.toTarget.getRunRuns().getContainer());
		assertSame(item.toTarget.getRunDate(), item.toTarget.getRunRuns().getOrder());

		assertFalse(item.toTarget.getPending().isAnnotationPresent(Computed.class));
		assertTrue (item.toTarget.getRunType().isAnnotationPresent(Computed.class));

		assertSerializedSame(item.toTarget, 391);

		assertSame(Boolean.FALSE, new Dispatcher().defaultPendingTo(false).getPending().getDefaultConstant());
		try
		{
			DispatcherNoneItem.newTypeAccessible(DispatcherNoneItem.class);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals(
					"type of DispatcherNoneItem.wrong must implement " + Dispatchable.class +
					", but was " + DispatcherNoneItem.class.getName(),
					e.getMessage());
		}
	}
}
