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

import static com.exedio.cope.JoinFunctionItem.TYPE;
import static com.exedio.cope.JoinFunctionItem.integer;
import static com.exedio.cope.JoinFunctionItemSingle.name;
import static com.exedio.cope.tojunit.Assert.assertContains;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JoinFunctionTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(TYPE, JoinFunctionItemSingle.TYPE);

	public JoinFunctionTest()
	{
		super(MODEL);
	}

	private JoinFunctionItemSingle single;

	@BeforeEach final void setUp()
	{
		single = new JoinFunctionItemSingle("single");
		new JoinFunctionItem("a1", Integer.valueOf(1));
		new JoinFunctionItem("a2", Integer.valueOf(2));
		new JoinFunctionItem("b1", Integer.valueOf(1));
		new JoinFunctionItem("b3", Integer.valueOf(3));
	}

	@Test void testIt()
	{
		{
			final Query<JoinFunctionItemSingle> q = JoinFunctionItemSingle.TYPE.newQuery(null);
			final Join j1 = q.join(TYPE, name.is("single"));
			final Join j2 = q.join(TYPE, name.is("single"));
			q.setCondition(
					integer.bind(j1).
						plus(
					integer.bind(j2)).
							greaterOrEqual(6));
			assertContains(single, q.search());
			// TODO let j1 be the principal type of query
		}
	}

}
