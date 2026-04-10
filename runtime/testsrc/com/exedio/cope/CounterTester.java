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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.micrometer.core.instrument.Counter;
import java.util.function.Predicate;
import java.util.function.Supplier;

final class CounterTester
{
	private final Supplier<Counter> supplier;
	private final Predicate<ConnectProperties> present;

	CounterTester(
			final Supplier<Counter> supplier,
			final Predicate<ConnectProperties> present)
	{
		this.supplier = supplier;
		this.present = present;
	}

	private boolean setUpSucceeded = false;
	private Model model;
	private boolean presentOnSetUp = false;
	private Counter counter;
	private double last;

	void setUp(final Model model)
	{
		assertFalse(setUpSucceeded, "setUpSucceeded");

		this.model = model;
		presentOnSetUp = present.test(model.getConnectProperties());
		if(presentOnSetUp)
		{
			this.counter = supplier.get();
			last = this.counter.count();
		}
		else
		{
			assertThrows(PrometheusMeterRegistrar.NotFound.class, supplier::get);
		}

		setUpSucceeded = true;
	}

	void assertCount(final int expected, final String message)
	{
		assertTrue(setUpSucceeded, "setUpSucceeded");
		assertEquals(presentOnSetUp, present.test(model.getConnectProperties()));

		if(presentOnSetUp)
		{
			final double actual = counter.count();
			assertEquals(expected, actual - last, message);
			last  = actual;
		}
		else
		{
			assertThrows(PrometheusMeterRegistrar.NotFound.class, supplier::get);
		}
	}

	double get()
	{
		assertTrue(setUpSucceeded, "setUpSucceeded");
		assertEquals(presentOnSetUp, present.test(model.getConnectProperties()));

		if(presentOnSetUp)
		{
			return counter.count();
		}
		else
		{
			assertThrows(PrometheusMeterRegistrar.NotFound.class, supplier::get);
			return 0.0;
		}
	}
}
