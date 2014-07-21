/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.instanceOfQuery;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Join;
import com.exedio.cope.Model;
import com.exedio.cope.Query;

public class InstanceOfQueryTest extends AbstractRuntimeTest
{
	private static final Model MODEL = new Model(
			IoqSourceSuper.TYPE, IoqSourceSubA.TYPE, WIoqSourceSubB.TYPE,
			IoqTargetSuper.TYPE, IoqTargetSub.TYPE
	);

	public InstanceOfQueryTest()
	{
		super(MODEL);
	}

	public void testIt()
	{
		final Query<IoqSourceSubA> query = IoqSourceSubA.TYPE.newQuery();
		final Join joinB = query.join(WIoqSourceSubB.TYPE);
		joinB.setCondition(WIoqSourceSubB.brother.bind(joinB).equalTarget());

		final Join targetB = query.join(IoqTargetSub.TYPE);
		targetB.setCondition(IoqSourceSuper.ref.equal(IoqTargetSub.TYPE.getThis().bind(targetB)));

		query.setCondition(
				IoqSourceSuper.ref.bind(targetB).instanceOf(IoqTargetSub.TYPE)
		);

		try
		{
			query.search();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"feature IoqSourceSuper#refType is ambiguous, " +
					"use Function#bind (deprecated): " + query,
					e.getMessage());
		}
	}
}
