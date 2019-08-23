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

import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static java.lang.Thread.NORM_PRIORITY;
import static java.lang.Thread.State.TERMINATED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class ThreadSwarmTest
{
	@Test void test() throws InterruptedException
	{
		final ThreadSwarm swarm = new ThreadSwarm(
				() -> {},
				"swarmName",
				new ThreadSwarmProperties(cascade(
						single("initial", 3),
						single("max", 5),
						single("priority.set", true),
						single("priority.value", NORM_PRIORITY+1)
				)));
		final ArrayList<ThreadController> controllers = new ArrayList<>();
		swarm.addThreadControllers(controllers);
		assertEquals(5, controllers.size());

		final ThreadController controller1 = controllers.get(0);
		assertIt("swarmName 1/5", NORM_PRIORITY+1, null, false, controller1);
		final ThreadController controller2 = controllers.get(1);
		assertIt("swarmName 2/5", NORM_PRIORITY+1, null, false, controller2);
		final ThreadController controller3 = controllers.get(2);
		assertIt("swarmName 3/5", NORM_PRIORITY+1, null, false, controller3);
		final ThreadController controller4 = controllers.get(3);
		assertIt("swarmName 4/5", NORM_PRIORITY+1, null, false, controller4);
		final ThreadController controller5 = controllers.get(4);
		assertIt("swarmName 5/5", NORM_PRIORITY+1, null, false, controller5);

		swarm.start();
		swarm.join();
		final ArrayList<ThreadController> controllers2 = new ArrayList<>();
		swarm.addThreadControllers(controllers2);
		assertEquals(controllers2, controllers);

		assertIt("swarmName 1/5", NORM_PRIORITY+1, TERMINATED, true, controller1);
		assertIt("swarmName 2/5", NORM_PRIORITY+1, TERMINATED, true, controller2);
		assertIt("swarmName 3/5", NORM_PRIORITY+1, TERMINATED, true, controller3);
		assertIt("swarmName 4/5", NORM_PRIORITY+1, null, false, controller4);
		assertIt("swarmName 5/5", NORM_PRIORITY+1, null, false, controller5);

		controller4.restart();
		controller4.join();
		assertIt("swarmName 4/5", NORM_PRIORITY+1, TERMINATED, true, controller4);
	}

	private static void assertIt(
			final String name,
			final int priority,
			final Thread.State state,
			final boolean stackTrace,
			final ThreadController actual)
	{
		assertEquals(name, actual.getName());
		assertEquals(true, actual.isDaemon());
		assertEquals(priority, actual.getPriority());
		assertEquals(state, actual.getState());
		if(stackTrace)
			assertNotNull(actual.getStackTrace());
		else
			assertNull(actual.getStackTrace());
	}
}
