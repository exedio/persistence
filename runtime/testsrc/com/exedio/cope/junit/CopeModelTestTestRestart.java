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

package com.exedio.cope.junit;

import static com.exedio.cope.junit.JUnitTestItem.nextSequence;
import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

public class CopeModelTestTestRestart extends CopeModelTestTest
{
	@Test public void testRestart()
	{
		doTest();
		model.commit();
		model.startTransaction("tx2:" + CopeModelTestTestRestart.class.getName());

		final JUnitTestItem i4 = new JUnitTestItem(104);
		assertEquals("JUnitTestItem-4", i4.getCopeID());
		assertEquals(1004, i4.getNext());
		assertEquals(2004, nextSequence());
	}
}
