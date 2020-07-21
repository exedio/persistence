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
import static com.exedio.cope.DataModelTest.assertNotSupported;
import static com.exedio.cope.RuntimeAssert.assertCondition;
import static java.util.Arrays.asList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StartsWithConditionTest extends TestWithEnvironment
{
	public StartsWithConditionTest()
	{
		super(DataModelTest.MODEL);
	}

	private DataItem item0, item4, item6, item6x4;

	@BeforeEach final void setUp()
	{
		item0 = new DataItem();
		item4 = new DataItem();
		item6 = new DataItem();
		item6x4 = new DataItem();
		new DataItem(); // is null
		item0.setData(bytes0);
		item4.setData(bytes4);
		item6.setData(bytes6);
		item6x4.setData(bytes6x4);
	}

	@Test void testCondition()
	{
		if(!isSupported(data.startsWithIfSupported(bytes4)))
			return;

		assertCondition(item4, TYPE, data.startsWithIfSupported(bytes4));
		assertCondition(item6, TYPE, data.startsWithIfSupported(bytes6));
		assertCondition(item6, item6x4, TYPE, data.startsWithIfSupported(bytes6x4));
	}

	@Test void testNot()
	{
		if(!isSupported(data.startsWithIfSupported(bytes4).not()))
			return;

		assertCondition(asList(item0, item6, item6x4), TYPE, data.startsWithIfSupported(bytes4  ).not(), data.startsWithIfSupported(bytes4  ));
		assertCondition(asList(item0, item4, item6x4), TYPE, data.startsWithIfSupported(bytes6  ).not(), data.startsWithIfSupported(bytes6  ));
		assertCondition(asList(item0, item4         ), TYPE, data.startsWithIfSupported(bytes6x4).not(), data.startsWithIfSupported(bytes6x4));
	}


	private static boolean isSupported(final Condition condition)
	{
		if(data.getVaultInfo()==null)
			return true;

		assertNotSupported(
				TYPE.newQuery(condition),
				"DataField DataItem.data does not support startsWith as it has vault enabled");
		return false;
	}

	private static final byte[] bytes0  = {};
	private static final byte[] bytes4  = {-86,122,-8,23};
	private static final byte[] bytes6  = {-97,35,-126,86,19,-8};
	private static final byte[] bytes6x4= {-97,35,-126,86};
}
