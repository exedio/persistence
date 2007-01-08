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


public class PkSourceSequentialTest extends PkSourceAbstractTest
{
	public PkSourceSequentialTest()
	{
		super(new SequentialPkSource(null));
	}
	
	public void testId2Pk()
			throws NoSuchIDException
	{
		assertIdPk(0, 0);
		assertIdPk(1, 1);
		assertIdPk(2, 2);
		assertIdPk(3, 3);
		assertIdPk(4, 4);

		assertIDFails(-1, "must be positive");
		assertIDFails(Long.MIN_VALUE, "must be positive");

		assertIdPk(2147483643l, 2147483643); // 2^31 - 5
		assertIdPk(2147483644l, 2147483644); // 2^31 - 4
		assertIdPk(2147483645l, 2147483645); // 2^31 - 3
		assertIdPk(2147483646l, 2147483646); // 2^31 - 2
		assertIdPk(2147483647l, 2147483647); // 2^31 - 1
		assertIDFails(2147483648l, "does not fit in 31 bit"); // 2^31
		assertIDFails(2147483649l, "does not fit in 31 bit"); // 2^31 + 1
		assertIDFails(Long.MAX_VALUE, "does not fit in 31 bit");
	}
	
}
