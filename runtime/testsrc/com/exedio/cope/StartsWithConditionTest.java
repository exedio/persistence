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

import static com.exedio.cope.DataItem.TYPE;
import static com.exedio.cope.DataItem.data;
import static com.exedio.cope.RuntimeAssert.assertCondition;

public class StartsWithConditionTest extends AbstractRuntimeModelTest
{
	public StartsWithConditionTest()
	{
		super(DataModelTest.MODEL);
	}

	private DataItem item;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		item = new DataItem();
	}

	public void testCondition()
	{
		assertCondition(TYPE, data.startsWith(bytes4));
		assertCondition(TYPE, data.startsWith(bytes6));
		assertCondition(TYPE, data.startsWith(bytes6x4));

		item.setData(bytes4);
		assertCondition(item, TYPE, data.startsWith(bytes4));
		assertCondition(TYPE, data.startsWith(bytes6));
		assertCondition(TYPE, data.startsWith(bytes6x4));

		item.setData(bytes6);
		assertCondition(TYPE, data.startsWith(bytes4));
		assertCondition(item, TYPE, data.startsWith(bytes6));
		assertCondition(item, TYPE, data.startsWith(bytes6x4));

		item.setData(bytes0);
		assertCondition(TYPE, data.startsWith(bytes4));
		assertCondition(TYPE, data.startsWith(bytes6));
		assertCondition(TYPE, data.startsWith(bytes6x4));
	}

	private static final byte[] bytes0  = {};
	private static final byte[] bytes4  = {-86,122,-8,23};
	private static final byte[] bytes6  = {-97,35,-126,86,19,-8};
	private static final byte[] bytes6x4= {-97,35,-126,86};
}
