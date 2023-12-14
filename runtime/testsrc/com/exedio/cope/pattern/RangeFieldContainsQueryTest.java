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

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.IntegerField;
import org.junit.jupiter.api.Test;

public class RangeFieldContainsQueryTest
{
	@Test void testMandatory()
	{
		final RangeField<Integer> f = RangeField.create(new IntegerField());
		assertEquals(true, f.getFrom().isMandatory());
		assertEquals(true, f.getTo  ().isMandatory());
		assertEquals("("+
				f.getFrom()+"<='66'" +
				" and " +
				f.getTo  ()+">='66'" +
				")",
				f.contains(66).toString());
	}
	@Test void testOptional()
	{
		final RangeField<Integer> f = RangeField.create(new IntegerField().optional());
		assertEquals(false, f.getFrom().isMandatory());
		assertEquals(false, f.getTo  ().isMandatory());
		assertEquals("(" +
				"("+f.getFrom()+" is null or "+f.getFrom()+"<='66')" +
				" and " +
				"("+f.getTo  ()+" is null or "+f.getTo  ()+">='66')" +
				")",
				f.contains(66).toString());
	}
}
