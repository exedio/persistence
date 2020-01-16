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

import static com.exedio.cope.MatchModel.MODEL;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.tojunit.LogRule;
import com.exedio.cope.tojunit.MainRule;
import com.exedio.cope.util.ModificationListener;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class ModificationListenerTest extends TestWithEnvironment
{
	public ModificationListenerTest()
	{
		super(MODEL);
	}

	@SuppressWarnings("deprecation")
	private final LogRule log = new LogRule(ModificationListeners.class);

	final ModificationListener l = (modifiedItems, transaction) -> fail();

	// dead store is needed to assign null for testing garbage collection
	@SuppressFBWarnings("DLS_DEAD_LOCAL_STORE_OF_NULL")
	@Deprecated
	@Test void testIt()
	{
		assertEqualsUnmodifiable(list(), model.getModificationListeners());
		assertEquals(0, model.getModificationListenersCleared());

		try
		{
			model.addModificationListener(l);
			fail();
		}
		catch(final NoSuchMethodError e)
		{
			assertEquals("ModificationListener is no longer supported", e.getMessage());
		}
		assertEqualsUnmodifiable(list(), model.getModificationListeners());
		assertEquals(0, model.getModificationListenersCleared());

		try
		{
			model.addModificationListener(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("listener", e.getMessage());
		}
		assertEqualsUnmodifiable(list(), model.getModificationListeners());
		assertEquals(0, model.getModificationListenersCleared());

		try
		{
			model.removeModificationListener(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("listener", e.getMessage());
		}
		assertEqualsUnmodifiable(list(), model.getModificationListeners());
		assertEquals(0, model.getModificationListenersCleared());

		log.assertEmpty();

		model.removeModificationListener(l);
		assertEqualsUnmodifiable(list(), model.getModificationListeners());
		assertEquals(0, model.getModificationListenersCleared());

		log.assertEmpty();
	}
}
