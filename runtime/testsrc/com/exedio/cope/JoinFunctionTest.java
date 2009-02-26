/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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


public class JoinFunctionTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(JoinFunctionItem.TYPE, JoinFunctionItemSingle.TYPE);

	public JoinFunctionTest()
	{
		super(MODEL);
	}
	
	private JoinFunctionItemSingle single;
	private JoinFunctionItem a1;
	@SuppressWarnings("unused") // OK: is an item not to be found by searches
	private JoinFunctionItem a2;
	@SuppressWarnings("unused") // OK: is an item not to be found by searches
	private JoinFunctionItem b1;
	@SuppressWarnings("unused") // OK: is an item not to be found by searches
	private JoinFunctionItem b3;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		
		single = deleteOnTearDown(new JoinFunctionItemSingle("single"));
		a1 = deleteOnTearDown(new JoinFunctionItem("a1", Integer.valueOf(1)));
		a2 = deleteOnTearDown(new JoinFunctionItem("a2", Integer.valueOf(2)));
		b1 = deleteOnTearDown(new JoinFunctionItem("b1", Integer.valueOf(1)));
		b3 = deleteOnTearDown(new JoinFunctionItem("b3", Integer.valueOf(3)));
	}
	
	public void testIt()
	{
		{
			final Query<JoinFunctionItemSingle> q = single.TYPE.newQuery(null);
			final Join j1 = q.join(a1.TYPE, single.name.equal("single"));
			final Join j2 = q.join(a1.TYPE, single.name.equal("single"));
			q.setCondition(
					a1.integer.bind(j1).
						plus(
					a1.integer.bind(j2)).
							greaterOrEqual(6));
			assertContains(single, q.search());
			// TODO let j1 be the principal type of query
		}
	}

}
