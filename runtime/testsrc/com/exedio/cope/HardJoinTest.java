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
		q.join(b.TYPE, a.a1.equal(b.b1));
		assertEquals(list(), q.search());
		b.setB1(10);
		assertEquals(list(a), q.search());
	}
	
	public void test12()
	{
		if(hsqldb||mysql5||oracle||postgresql) // TODO
			return;
		
		//System.out.println("------------------------test12---------------------");
						
		final Query<HardJoinA3Item> q = a.TYPE.newQuery();
		q.join(b.TYPE, a.a1.equal(b.b2));
		assertEquals(list(), q.search());
		b.setB2(10);
		assertEquals(list(a), q.search());
	}
	
	public void test13()
	{
		if(hsqldb||mysql5||oracle||postgresql) // TODO
			return;
		
		//System.out.println("------------------------test13---------------------");
						
		final Query<HardJoinA3Item> q = a.TYPE.newQuery();
		q.join(b.TYPE, a.a1.equal(b.b3));
		assertEquals(list(), q.search());
		b.setB3(10);
		assertEquals(list(a), q.search());
	}
	
	public void test21()
	{
		if(hsqldb||mysql5||oracle||postgresql) // TODO
			return;
		
		//System.out.println("------------------------test21---------------------");
						
		final Query<HardJoinA3Item> q = a.TYPE.newQuery();
		q.join(b.TYPE, a.a2.equal(b.b1));
		assertEquals(list(), q.search());
		b.setB1(11);
		assertEquals(list(a), q.search());
	}
	
	public void test22()
	{
		if(hsqldb||mysql5||oracle||postgresql) // TODO
			return;
		
		//System.out.println("------------------------test22---------------------");
						
		final Query<HardJoinA3Item> q = a.TYPE.newQuery();
		q.join(b.TYPE, a.a2.equal(b.b2));
		assertEquals(list(), q.search());
		b.setB2(11);
		assertEquals(list(a), q.search());
	}
	
	public void test23()
	{
		if(hsqldb||mysql5||oracle||postgresql) // TODO
			return;
		
		//System.out.println("------------------------test23---------------------");
						
		final Query<HardJoinA3Item> q = a.TYPE.newQuery();
		q.join(b.TYPE, a.a2.equal(b.b3));
		assertEquals(list(), q.search());
		b.setB3(11);
		assertEquals(list(a), q.search());
	}
	
	public void test31()
	{
		if(hsqldb||mysql5||oracle||postgresql) // TODO
			return;
		
		//System.out.println("------------------------test31---------------------");
						
		final Query<HardJoinA3Item> q = a.TYPE.newQuery();
		q.join(b.TYPE, a.a3.equal(b.b1));
		assertEquals(list(), q.search());
		b.setB1(12);
		assertEquals(list(a), q.search());
	}
	
	public void test32()
	{
		if(hsqldb||mysql5||oracle||postgresql) // TODO
			return;
		
		//System.out.println("------------------------test32---------------------");
						
		final Query<HardJoinA3Item> q = a.TYPE.newQuery();
		q.join(b.TYPE, a.a3.equal(b.b2));
		assertEquals(list(), q.search());
		b.setB2(12);
		assertEquals(list(a), q.search());
	}
	
	public void test33()
	{
		if(hsqldb||mysql5||oracle||postgresql) // TODO
			return;
		
		//System.out.println("------------------------test33---------------------");
						
		final Query<HardJoinA3Item> q = a.TYPE.newQuery();
		q.join(b.TYPE, a.a3.equal(b.b3));
		assertEquals(list(), q.search());
		b.setB3(12);
		assertEquals(list(a), q.search());
	}
	
	public void test12x1()
	{
		if(hsqldb||mysql5||oracle||postgresql) // TODO
			return;
		
		//System.out.println("------------------------test12x1---------------------");
						
		final Query<HardJoinA3Item> q = a.TYPE.newQuery();
		q.join(b.TYPE, a.a1.equal(b.b1).and(a.a2.equal(b.b1)));
		assertEquals(list(), q.search());
		a.setA1(20);
		assertEquals(list(), q.search());
		a.setA2(20);
		assertEquals(list(a), q.search());
	}
	
	public void test13x1()
	{
		if(hsqldb||mysql5||oracle||postgresql) // TODO
			return;
		
		//System.out.println("------------------------test13x1---------------------");
						
		final Query<HardJoinA3Item> q = a.TYPE.newQuery();
		q.join(b.TYPE, a.a1.equal(b.b1).and(a.a3.equal(b.b1)));
		assertEquals(list(), q.search());
		a.setA1(20);
		assertEquals(list(), q.search());
		a.setA3(20);
		assertEquals(list(a), q.search());
	}
	
	public void test23x1()
	{
		if(hsqldb||mysql5||oracle||postgresql) // TODO
			return;
		
		//System.out.println("------------------------test23x1---------------------");
						
		final Query<HardJoinA3Item> q = a.TYPE.newQuery();
		q.join(b.TYPE, a.a2.equal(b.b1).and(a.a3.equal(b.b1)));
		assertEquals(list(), q.search());
		a.setA2(20);
		assertEquals(list(), q.search());
		a.setA3(20);
		assertEquals(list(a), q.search());
	}
	
	public void test12x2()
	{
		if(hsqldb||mysql5||oracle||postgresql) // TODO
			return;
		
		//System.out.println("------------------------test12x2---------------------");
						
		final Query<HardJoinA3Item> q = a.TYPE.newQuery();
		q.join(b.TYPE, a.a1.equal(b.b2).and(a.a2.equal(b.b2)));
		assertEquals(list(), q.search());
		a.setA1(21);
		assertEquals(list(), q.search());
		a.setA2(21);
		assertEquals(list(a), q.search());
	}
	
	public void test13x2()
	{
		if(hsqldb||mysql5||oracle||postgresql) // TODO
			return;
		
		//System.out.println("------------------------test13x2---------------------");
						
		final Query<HardJoinA3Item> q = a.TYPE.newQuery();
		q.join(b.TYPE, a.a1.equal(b.b2).and(a.a3.equal(b.b2)));
		assertEquals(list(), q.search());
		a.setA1(21);
		assertEquals(list(), q.search());
		a.setA3(21);
		assertEquals(list(a), q.search());
	}
	
	public void test23x2()
	{
		if(hsqldb||mysql5||oracle||postgresql) // TODO
			return;
		
		//System.out.println("------------------------test23x2---------------------");
						
		final Query<HardJoinA3Item> q = a.TYPE.newQuery();
		q.join(b.TYPE, a.a2.equal(b.b2).and(a.a3.equal(b.b2)));
		assertEquals(list(), q.search());
		a.setA2(21);
		assertEquals(list(), q.search());
		a.setA3(21);
		assertEquals(list(a), q.search());
	}
	
	public void test12x3()
	{
		if(hsqldb||mysql5||oracle||postgresql) // TODO
			return;
		
		//System.out.println("------------------------test12x3---------------------");
						
		final Query<HardJoinA3Item> q = a.TYPE.newQuery();
		q.join(b.TYPE, a.a1.equal(b.b3).and(a.a2.equal(b.b3)));
		assertEquals(list(), q.search());
		a.setA1(22);
		assertEquals(list(), q.search());
		a.setA2(22);
		assertEquals(list(a), q.search());
	}
	
	public void test13x3()
	{
		if(hsqldb||mysql5||oracle||postgresql) // TODO
			return;
		
		//System.out.println("------------------------test13x3---------------------");
						
		final Query<HardJoinA3Item> q = a.TYPE.newQuery();
		q.join(b.TYPE, a.a1.equal(b.b3).and(a.a3.equal(b.b3)));
		assertEquals(list(), q.search());
		a.setA1(22);
		assertEquals(list(), q.search());
		a.setA3(22);
		assertEquals(list(a), q.search());
	}
	
	public void test23x3()
	{
		if(hsqldb||mysql5||oracle||postgresql) // TODO
			return;
		
		//System.out.println("------------------------test23x3---------------------");
						
		final Query<HardJoinA3Item> q = a.TYPE.newQuery();
		q.join(b.TYPE, a.a2.equal(b.b3).and(a.a3.equal(b.b3)));
		assertEquals(list(), q.search());
		a.setA2(22);
		assertEquals(list(), q.search());
		a.setA3(22);
		assertEquals(list(a), q.search());
	}
}
