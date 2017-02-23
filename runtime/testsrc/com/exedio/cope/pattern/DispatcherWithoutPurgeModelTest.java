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
import static com.exedio.cope.pattern.DispatcherWithoutPurgeItem.TYPE;
import static com.exedio.cope.pattern.DispatcherWithoutPurgeItem.body;
import static com.exedio.cope.pattern.DispatcherWithoutPurgeItem.dispatchCountCommitted;
import static com.exedio.cope.pattern.DispatcherWithoutPurgeItem.purgeToTarget;
import static com.exedio.cope.pattern.DispatcherWithoutPurgeItem.toTarget;
import static com.exedio.cope.pattern.DispatcherWithoutPurgeItem.toTargetRunParent;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.util.JobContexts;
import com.exedio.cope.util.Sources;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import org.junit.Test;

public class DispatcherWithoutPurgeModelTest
{
	public static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(DispatcherWithoutPurgeModelTest.class, "MODEL");
	}

	private static final Type<?> runType = toTarget.getRunType();

	@SuppressFBWarnings("RC_REF_COMPARISON_BAD_PRACTICE_BOOLEAN")
	@Test public void testModel()
	{
		assertEqualsUnmodifiable(list(
				TYPE,
				runType
			), MODEL.getTypes());
		assertEqualsUnmodifiable(list(
				TYPE,
				runType
			), MODEL.getTypesSortedByHierarchy());
		assertEquals(DispatcherWithoutPurgeItem.class, TYPE.getJavaClass());
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
				toTarget.getPending()
			), TYPE.getFeatures());
		assertEquals(null, toTarget.getNoPurge());
		assertEquals(null, toTarget.getUnpend());
		assertEquals(null, toTarget.getUnpendSuccess());
		assertEquals(null, toTarget.getUnpendDate());
		assertEquals(null, toTarget.getUnpendUnison());

		assertEqualsUnmodifiable(list(
				runType.getThis(),
				toTargetRunParent(),
				toTarget.getRunDate(),
				toTarget.getRunRuns(),
				toTarget.getRunElapsed(),
				toTarget.getRunResult(),
				toTarget.getRunFailure()
			), runType.getFeatures());

		assertEquals(TYPE, toTarget.getType());
		assertEquals("toTarget", toTarget.getName());

		assertSame(TYPE, toTarget.getPending().getType());
		assertSame("toTarget-pending", toTarget.getPending().getName());
		assertSame(toTarget, toTarget.getPending().getPattern());
		assertSame(Boolean.TRUE, toTarget.getPending().getDefaultConstant());

		assertEquals("DispatcherWithoutPurgeItem-toTarget-Run", runType.getID());
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

		assertSame(DispatcherWithoutPurgeItem.class, toTargetRunParent().getValueClass());
		assertSame(TYPE, toTargetRunParent().getValueType());

		assertSame(toTargetRunParent(), toTarget.getRunRuns().getContainer());
		assertSame(toTarget.getRunDate(), toTarget.getRunRuns().getOrder());
	}

	@Test public void testComputed()
	{
		assertFalse(toTarget.getPending().isAnnotationPresent(Computed.class));
		assertTrue (toTarget.getRunType().isAnnotationPresent(Computed.class));
	}

	@Test public void testSerialize()
	{
		assertSerializedSame(toTarget, 415);
	}

	@Test public void testPurgePropertiesNull()
	{
		try
		{
			purgeToTarget(null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("properties", e.getMessage());
		}
	}

	@Test public void testPurgeContextNull()
	{
		final DispatcherPurgeProperties properties =
				DispatcherPurgeProperties.factory().delayDaysDefault(5).create(Sources.EMPTY);

		try
		{
			purgeToTarget(properties, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("ctx", e.getMessage());
		}
	}

	@Test public void testPurge()
	{
		final DispatcherPurgeProperties properties =
				DispatcherPurgeProperties.factory().delayDaysDefault(5).create(Sources.EMPTY);

		try
		{
			purgeToTarget(properties, JobContexts.EMPTY);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"purge has been disabled for Dispatcher DispatcherWithoutPurgeItem.toTarget " +
					"by method withoutPurge()",
					e.getMessage());
		}
	}
}
