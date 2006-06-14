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

package com.exedio.cope.badquery;

import com.exedio.cope.AbstractLibTest;
import com.exedio.cope.Join;
import com.exedio.cope.JoinedItemFunction;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.Type;
import com.exedio.dsmf.SQLRuntimeException;

public class BadQueryTest extends AbstractLibTest
{
	static final Model model = new Model(new Type[]{SuperItem.TYPE, QueryItem.TYPE, SuperContainer.TYPE, SubContainer.TYPE});
	
	public BadQueryTest()
	{
		super(model);
	}
	
	public void testIt()
	{
		if(hsqldb||oracle)
			return;
		
		//System.out.println("----------"+model.getDatabaseInfo().getProperty("database.version"));

		{
			// with specifying join
			final Query<QueryItem> query = QueryItem.TYPE.newQuery(null);
			final Join superJoin = query.join(SuperContainer.TYPE);
			query.join(SubContainer.TYPE);
			query.setCondition(new JoinedItemFunction<SuperContainer>(SuperContainer.TYPE.getThis(), superJoin).typeNotIn(SubContainer.TYPE));
			assertContains(query.search());
		}
		
		// without specifying join
		final Query<QueryItem> query = QueryItem.TYPE.newQuery(null);
		final Join superJoin = query.join(SuperContainer.TYPE);
		query.join(SubContainer.TYPE);
		query.setCondition(SuperContainer.TYPE.getThis().typeNotIn(SubContainer.TYPE));
		try
		{
			query.search();
			fail();
		}
		catch(SQLRuntimeException e)
		{
			assertTrue(e.getMessage(), e.getMessage().startsWith("select `QueryItem`.`this` "));
			assertEquals(
					model.getDatabaseInfo().getProperty("database.version").endsWith("(5.0)")
					? "Unknown column 'SuperContainer.class' in 'where clause'"
					: "Unknown table 'SuperContainer' in where clause",
					e.getCause().getMessage());
		}
	}
	
}
