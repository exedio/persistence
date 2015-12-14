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

import com.exedio.cope.junit.CopeAssert;
import org.junit.Test;

public class PostgresqlDoubleTest extends CopeAssert
{
	static String format(final double number)
	{
		return PostgresqlFormat.format(number);
	}

	@Test public void testIt()
	{
		assertEquals( "1.7976931348623157E308", Double.toString( Double.MAX_VALUE));
		assertEquals("-1.7976931348623157E308", Double.toString(-Double.MAX_VALUE));
		assertEquals( "11.0", format( 11d));
		assertEquals("-11.0", format(-11d));
		assertEquals( "12.0", format( 12d));
		assertEquals("-12.0", format(-12d));
		assertEquals( "11.4", format( 11.4d));
		assertEquals("-11.4", format(-11.4d));
		assertEquals( "11.456", format( 11.456d));
		assertEquals("-11.456", format(-11.456d));
		assertEquals( "0.456", format( 0.456d));
		assertEquals("-0.456", format(-0.456d));
		assertEquals( "0.0", format( 0d));
		assertEquals("-0.0", format(-0d));
		assertEquals( "1.0", format( 1d));
		assertEquals("-1.0", format(-1d));
		assertEquals(
				"179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
				format(Double.MAX_VALUE));
		assertEquals(
				"-179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
				format(-Double.MAX_VALUE));
	}
}
