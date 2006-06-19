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


public class PolymorphQueryCacheInvalidationTest extends AbstractLibTest
{
	public PolymorphQueryCacheInvalidationTest()
	{
		super(Main.typeInConditionModel);
	}
	
	TypeInConditionAItem itema;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(itema = new TypeInConditionAItem("itema"));
	}
	
	public void testIt()
	{
		final Query q = itema.TYPE.newQuery(null);
		assertContains(itema, q.search());
		
		final TypeInConditionB1Item itemb1a = new TypeInConditionB1Item("itemb1a");
		deleteOnTearDown(itemb1a);

		restartTransaction();
		
		assertContains(itema, itemb1a, q.search());
		
		final TypeInConditionB1Item itemb1b = new TypeInConditionB1Item("itemb1b");
		deleteOnTearDown(itemb1b);

		restartTransaction();
		
		if(model.getProperties().getCacheQueryLimit()==0)
			assertContains(itema, itemb1a, itemb1b, q.search());
		else
			assertContains(itema, itemb1a, /* TODO SOON itemb1b,*/ q.search());
	}

}
