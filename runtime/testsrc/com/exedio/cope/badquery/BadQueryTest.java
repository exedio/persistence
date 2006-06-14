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
import com.exedio.cope.ItemFunction;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.Type;
import com.exedio.dsmf.SQLRuntimeException;

public class BadQueryTest extends AbstractLibTest
{
	static final Model model = new Model(new Type[]{SuperItem.TYPE, QueryItem.TYPE, SuperContainer.TYPE, SubContainer.TYPE, SubContained.TYPE});
	
	public BadQueryTest()
	{
		super(model);
	}
	
	public void testIt()
	{
		if(hsqldb||oracle)
			return;
		
		final Query<QueryItem> query = QueryItem.TYPE.newQuery(SuperContainer.TYPE.getThis().typeNotIn(SubContainer.TYPE));
		query.join(SuperContainer.TYPE, QueryItem.item.equal(castEvilItem(SuperContainer.TYPE.getThis())));
		query.join(SubContained.TYPE, SubContained.container.equalTarget());
		query.join(SubContainer.TYPE);
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
	
	@SuppressWarnings("unchecked")
	private ItemFunction<SuperItem> castEvilItem(final ItemFunction o)
	{
		return (ItemFunction<SuperItem>)o;
	}
	
	@SuppressWarnings("unchecked")
	private ItemFunction<SuperContainer> castEvilAbstractOrder(final ItemFunction o)
	{
		return (ItemFunction<SuperContainer>)o;
	}	
}
