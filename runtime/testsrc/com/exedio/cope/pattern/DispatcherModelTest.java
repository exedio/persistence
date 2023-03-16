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

import static com.exedio.cope.PrometheusMeterRegistrar.getMeters;
import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.SchemaInfo.getColumnValue;
import static com.exedio.cope.pattern.Dispatcher.create;
import static com.exedio.cope.pattern.Dispatcher.createWithSession;
import static com.exedio.cope.pattern.DispatcherItem.TYPE;
import static com.exedio.cope.pattern.DispatcherItem.body;
import static com.exedio.cope.pattern.DispatcherItem.dispatchCountCommitted;
import static com.exedio.cope.pattern.DispatcherItem.purgeToTarget;
import static com.exedio.cope.pattern.DispatcherItem.toTarget;
import static com.exedio.cope.pattern.DispatcherItem.toTargetRunParent;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.list;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.Condition;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.misc.LocalizationKeys;
import com.exedio.cope.pattern.Dispatcher.Result;
import com.exedio.cope.util.EmptyJobContext;
import com.exedio.cope.util.Sources;
import java.io.ByteArrayOutputStream;
import java.util.List;
import org.junit.jupiter.api.Test;

public class DispatcherModelTest
{
	public static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(DispatcherModelTest.class, "MODEL");
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
		assertEquals(DispatcherItem.class, TYPE.getJavaClass());
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
				toTarget.getRunRemaining(),
				toTarget.getRunLimit(),
				toTarget.getRunResult(),
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

		assertSame(DispatcherItem.class, toTargetRunParent().getValueClass());
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
		assertSerializedSame(toTarget, 391);
	}

	@Test void testDefaultPendingTo()
	{
		assertSame(Boolean.FALSE, create(i -> {}).defaultPendingTo(false).getPending().getDefaultConstant());
	}

	@Test void testCreateFinalFailureListenerNull()
	{
		final Dispatcher d =
				create(i -> { throw new Exception(); }, null);
		assertEquals(true, d.supportsPurge()); // fixes idea inspection Method can be void
	}

	@Test void testCreateTargetNull()
	{
		assertFails(
				() -> create(null),
				NullPointerException.class,
				"target");
	}

	@Test void testCreateTargetNullFinalFailureListener()
	{
		assertFails(
				() -> create(null, null),
				NullPointerException.class,
				"target");
	}

	@Test void testCreateFinalFailureListenerNullSessioned()
	{
		final Dispatcher d =
				createWithSession(ByteArrayOutputStream::new, (s,i) -> { throw new Exception(); }, null);
		assertEquals(true, d.supportsPurge()); // fixes idea inspection Method can be void
	}

	@Test void testCreateTargetNullSessioned()
	{
		assertFails(
				() -> createWithSession(ByteArrayOutputStream::new, null),
				NullPointerException.class,
				"target");
	}

	@Test void testCreateTargetNullSessionedFinalFailureListener()
	{
		assertFails(
				() -> createWithSession(ByteArrayOutputStream::new, null, null),
				NullPointerException.class,
				"target");
	}

	@Test void testDispatchParentClassWrong()
	{
		try
		{
			toTarget.dispatch(HashItem.class, new Dispatcher.Config(), new EmptyJobContext());
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("parentClass requires " + DispatcherItem.class.getName() + ", but was " + HashItem.class.getName(), e.getMessage());
		}
	}

	@Test void testDispatchConfigNull()
	{
		try
		{
			toTarget.dispatch(DispatcherItem.class, null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("config", e.getMessage());
		}
	}

	@Test void testDispatchContextNull()
	{
		try
		{
			toTarget.dispatch(DispatcherItem.class, new Dispatcher.Config(), null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("ctx", e.getMessage());
		}
	}

	@Test void testPurgePropertiesNull()
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

	@Test void testPurgeContextNull()
	{
		final DispatcherPurgeProperties properties =
				DispatcherPurgeProperties.factory().retainDaysDefault(5).create(Sources.EMPTY);

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

	@Test void testPurgeRestrictedPropertiesNull()
	{
		try
		{
			purgeToTarget(null, null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("properties", e.getMessage());
		}
	}

	@Test void testPurgeRestrictedRestrictionNull()
	{
		final DispatcherPurgeProperties properties =
				DispatcherPurgeProperties.factory().retainDaysDefault(5).create(Sources.EMPTY);

		try
		{
			purgeToTarget(properties, null, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("restriction", e.getMessage());
		}
	}

	@Test void testPurgeRestrictedContextNull()
	{
		final DispatcherPurgeProperties properties =
				DispatcherPurgeProperties.factory().retainDaysDefault(5).create(Sources.EMPTY);

		try
		{
			purgeToTarget(properties, Condition.FALSE, null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("ctx", e.getMessage());
		}
	}

	@Test void testResult()
	{
		@SuppressWarnings("deprecation") // OK: testing deprecated api
		final Result failure = Result.failure;

		assertEquals(false, Result.transientFailure.isSuccess());
		assertEquals(false, Result.finalFailure.isSuccess());
		assertEquals(false, Result.immediateFinalFailure.isSuccess());
		assertEquals(false, failure.isSuccess());
		assertEquals(true, Result.success.isSuccess());
	}

	@Test void testEnumSchemaResult()
	{
		@SuppressWarnings("deprecation") // OK: testing deprecated API
		final Result deprecatedFailure = Result.failure;
		assertEquals(
				asList(Result.transientFailure, Result.finalFailure, Result.immediateFinalFailure, deprecatedFailure, Result.success),
				asList(Dispatcher.Result.values()));
		assertEquals(-20, getColumnValue(Result.transientFailure ));
		assertEquals(-10, getColumnValue(Result.finalFailure ));
		assertEquals( -5, getColumnValue(Result.immediateFinalFailure ));
		assertEquals(  0, getColumnValue(deprecatedFailure ));
		assertEquals(  1, getColumnValue(Result.success ));
	}

	@Test void testMeters()
	{
		assertEquals(asList(
				"dispatch result=failure",
				"dispatch result=success",
				"probe",
				"purge"),
				getMeters(toTarget));
	}

	/**
	 * @see com.exedio.cope.LocalizationKeysPatternTest#testVerbose()
	 */
	@Test public void testLocalizationKeys()
	{
		assertEquals(List.of(
				"com.exedio.cope.pattern.DispatcherItem",
				"DispatcherItem"),
				TYPE.getLocalizationKeys());
		assertEquals(List.of(
				"com.exedio.cope.pattern.DispatcherItem",
				"DispatcherItem"),
				LocalizationKeys.get(DispatcherItem.class));
		assertEquals(List.of(
				"com.exedio.cope.pattern.DispatcherItem.toTarget",
				"DispatcherItem.toTarget",
				"toTarget"),
				toTarget.getLocalizationKeys());
		assertEquals(List.of(
				"com.exedio.cope.pattern.DispatcherItem.toTarget.pending",
				"DispatcherItem.toTarget.pending",
				"com.exedio.cope.pattern.Dispatcher.pending",
				"Dispatcher.pending",
				"pending"),
				toTarget.getPending().getLocalizationKeys());
		assertEquals(List.of(
				"com.exedio.cope.pattern.DispatcherItem.toTarget.unpend.success",
				"DispatcherItem.toTarget.unpend.success",
				"com.exedio.cope.pattern.Dispatcher.Unpend.success",
				"Dispatcher.Unpend.success",
				"success"),
				toTarget.getUnpendSuccess().getLocalizationKeys());
		assertEquals(List.of(
				"com.exedio.cope.pattern.Dispatcher.Unpend",
				"Dispatcher.Unpend"),
				LocalizationKeys.get(toTarget.getUnpend().getValueClass()));
		assertEquals(List.of(
				"com.exedio.cope.pattern.Dispatcher.Run",
				"Dispatcher.Run"),
				toTarget.getRunType().getLocalizationKeys());
		assertEquals(List.of(
				"com.exedio.cope.pattern.Dispatcher.Run.elapsed",
				"Dispatcher.Run.elapsed",
				"elapsed"),
				toTarget.getRunElapsed().getLocalizationKeys());
	}
}
