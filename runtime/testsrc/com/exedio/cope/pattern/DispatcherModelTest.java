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
import static com.exedio.cope.pattern.DispatcherItem.TYPE;
import static com.exedio.cope.pattern.DispatcherItem.body;
import static com.exedio.cope.pattern.DispatcherItem.dispatchCountCommitted;
import static com.exedio.cope.pattern.DispatcherItem.toTarget;
import static com.exedio.cope.pattern.DispatcherItem.toTargetRunParent;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.junit.CopeAssert;
import com.exedio.cope.misc.Computed;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;

public class DispatcherModelTest extends CopeAssert
{
	public static final Model MODEL = new Model(DispatcherItem.TYPE);

	static
	{
		MODEL.enableSerialization(DispatcherModelTest.class, "MODEL");
	}

	@SuppressFBWarnings("RC_REF_COMPARISON_BAD_PRACTICE_BOOLEAN")
	public void testIt()
	{
		final Type<?> runType = toTarget.getRunType();

		assertEqualsUnmodifiable(list(
				TYPE,
				runType
			), MODEL.getTypes());
		assertEqualsUnmodifiable(list(
				TYPE,
				runType
			), MODEL.getTypesSortedByHierarchy());
		assertEquals(DispatcherItem.class, TYPE.getJavaClass());
		assertEquals(true, TYPE.isBound());
		assertEquals(null, TYPE.getPattern());

		final List<PartOf<?>> partOfs = PartOf.getPartOfs(DispatcherItem.TYPE);
		assertEquals(1, partOfs.size());
		final PartOf<?> partOf = partOfs.get(0);
		assertSame(runType, partOf.getType());
		assertEquals(DispatcherItem.TYPE, partOf.getContainer().getValueType());
		assertEqualsUnmodifiable(list(DispatcherItem.toTarget.getRunType()), DispatcherItem.toTarget.getSourceTypes());
		assertEquals(list(partOf), PartOf.getPartOfs(DispatcherItem.toTarget));

		assertEqualsUnmodifiable(list(
				TYPE.getThis(),
				body,
				dispatchCountCommitted,
				toTarget,
				toTarget.getPending()
			), TYPE.getFeatures());
		assertEqualsUnmodifiable(list(
				runType.getThis(),
				toTargetRunParent(),
				toTarget.getRunDate(),
				toTarget.getRunRuns(),
				toTarget.getRunElapsed(),
				toTarget.getRunSuccess(),
				toTarget.getRunFailure()
			), runType.getFeatures());

		assertEquals(TYPE, toTarget.getType());
		assertEquals("toTarget", toTarget.getName());

		assertSame(TYPE, toTarget.getPending().getType());
		assertSame("toTarget-pending", toTarget.getPending().getName());
		assertSame(toTarget, toTarget.getPending().getPattern());
		assertSame(Boolean.TRUE, toTarget.getPending().getDefaultConstant());

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

		assertEquals(runType, toTargetRunParent().getType());
		assertEquals(runType, toTarget.getRunDate().getType());
		assertEquals(runType, toTarget.getRunFailure().getType());

		assertEquals("parent", toTargetRunParent().getName());
		assertEquals("date", toTarget.getRunDate().getName());
		assertEquals("failure", toTarget.getRunFailure().getName());

		assertSame(DispatcherItem.class, toTargetRunParent().getValueClass());
		assertSame(DispatcherItem.TYPE, toTargetRunParent().getValueType());

		assertSame(toTargetRunParent(), toTarget.getRunRuns().getContainer());
		assertSame(toTarget.getRunDate(), toTarget.getRunRuns().getOrder());

		assertFalse(toTarget.getPending().isAnnotationPresent(Computed.class));
		assertTrue (toTarget.getRunType().isAnnotationPresent(Computed.class));

		assertSerializedSame(toTarget, 391);

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
