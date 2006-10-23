/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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


public class HardJoinTest extends AbstractLibTest
{
	public HardJoinTest()
	{
		super(Main.hardJoinModel);
	}
	
	private HardJoinA3Item a;
	private HardJoinB3Item b;
	boolean mysql5;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		
		deleteOnTearDown(a = new HardJoinA3Item("a", 10, 11, 12));
		deleteOnTearDown(b = new HardJoinB3Item("b", 20, 21, 22));
		mysql5 = mysql && ((String)model.getDatabaseInfo().get("database.version")).endsWith("(5.0)");
	}
	
	public void test11()
	{
		if(hsqldb||mysql5||oracle||postgresql) // TODO
			return;
		
		//System.out.println("------------------------test11---------------------");
						
		final Query<HardJoinA3Item> q = a.TYPE.newQuery();
		q.join(b.TYPE, b.b1.equal(a.a1));
		assertEquals(list(), q.search());
		b.setB1(10);
		assertEquals(list(a), q.search());
	}
}
