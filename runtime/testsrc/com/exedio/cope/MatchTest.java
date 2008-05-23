/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

public class MatchTest extends AbstractRuntimeTest
{
	public/*for web.xml*/ static final Model MODEL = new Model(MatchItem.TYPE);

	public MatchTest()
	{
		super(MODEL);
	}
	
	MatchItem item;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new MatchItem());
	}
	
	public void testStrings() throws StringField.LengthViolationException
	{
		item.setText("hallo bello cnallo");
		assertEquals(list(item), item.TYPE.search(new MatchCondition(item.text, "hallo")));
		assertEquals(list(item), item.TYPE.search(new MatchCondition(item.text, "bello")));
		assertEquals(list(item), item.TYPE.search(new MatchCondition(item.text, "cnallo")));
		assertEquals(list(), item.TYPE.search(new MatchCondition(item.text, "zack")));
	}
}
