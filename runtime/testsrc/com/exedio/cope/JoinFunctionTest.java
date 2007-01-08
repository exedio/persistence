/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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


public class JoinFunctionTest extends AbstractLibTest
{
	static final Model MODEL = new Model(JoinFunctionItem.TYPE, JoinFunctionItemSingle.TYPE);

	public JoinFunctionTest()
	{
		super(MODEL);
	}
	
	private JoinFunctionItemSingle single;
	private JoinFunctionItem a1;
	private JoinFunctionItem a2;
	private JoinFunctionItem b1;
	private JoinFunctionItem b3;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		
		deleteOnTearDown(single = new JoinFunctionItemSingle("single"));
		deleteOnTearDown(a1 = new JoinFunctionItem("a1", Integer.valueOf(1)));
		deleteOnTearDown(a2 = new JoinFunctionItem("a2", Integer.valueOf(2)));
		deleteOnTearDown(b1 = new JoinFunctionItem("b1", Integer.valueOf(1)));
		deleteOnTearDown(b3 = new JoinFunctionItem("b3", Integer.valueOf(3)));
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
