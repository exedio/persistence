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

import static com.exedio.cope.MatchItem.TYPE;
import static com.exedio.cope.MatchItem.text;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
public class MatchTest extends TestWithEnvironment
{
	public static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(MatchTest.class, "MODEL");
	}

	public MatchTest()
	{
		super(MODEL);
	}

	MatchItem item;

	@BeforeEach final void setUp()
	{
		item = new MatchItem();
	}

	@Test void testStrings() throws StringLengthViolationException
	{
		item.setText("hallo bello cnallo");
		assertEquals(list(item), TYPE.search(new MatchCondition(text, "hallo")));
		assertEquals(list(item), TYPE.search(new MatchCondition(text, "bello")));
		assertEquals(list(item), TYPE.search(new MatchCondition(text, "cnallo")));
		assertEquals(list(), TYPE.search(new MatchCondition(text, "zack")));
	}
}
