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

import static com.exedio.cope.SchemaInfo.getUpdateCounterColumnName;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public final class FinalTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(FinalSuperItem.TYPE, FinalSubItem.TYPE, FinalSubNoneItem.TYPE);

	public FinalTest()
	{
		super(MODEL);
	}

	@Test void test()
	{
		new FinalSubItem(1, 11);
		new FinalSubNoneItem(2, 22);

		restartTransaction();
		model.clearCache();

		final FinalSubItem item1c = FinalSubItem.TYPE.searchSingleton(null);
		assertEquals(1, item1c.getSuperInt());
		assertEquals(11, item1c.getSubInt());

		final FinalSubNoneItem item2c = FinalSubNoneItem.TYPE.searchSingleton(null);
		assertEquals(2, item2c.getSuperInt());
		assertEquals(22, item2c.getSubIntNone());
	}

	@Test void testUpdateCounter()
	{
		assertEquals(synthetic("catch", "FinalSuperItem"), getUpdateCounterColumnName(FinalSuperItem.TYPE));
		assertEquals(synthetic("catch", "FinalSubNoneItem"), getUpdateCounterColumnName(FinalSubNoneItem.TYPE));

		assertFails(
				() -> getUpdateCounterColumnName(FinalSubItem.TYPE),
				IllegalArgumentException.class,
				"no update counter for FinalSubItem");
	}
}
