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

package com.exedio.cope.pattern;

import static com.exedio.cope.pattern.EnumSingleton.create;
import static com.exedio.cope.tojunit.Assert.assertFails;

import com.exedio.cope.EnumSingleTest.AnEnum;
import org.junit.jupiter.api.Test;

public class EnumSingletonSingleTest
{
	@Test void testOk()
	{
		assertFails(
				() -> create(AnEnum.class),
				IllegalArgumentException.class,
				"Redundant field on a valueClass with one enum constant only " +
				"(" + AnEnum.class.getName() + ") is probably a mistake. " +
				"You may call method createEvenIfRedundant if you are sure this is ok.");
	}
}
