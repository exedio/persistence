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

import static com.exedio.cope.pattern.EnumMapFieldDefaultItem.TYPE;
import static com.exedio.cope.pattern.EnumMapFieldDefaultItem.text;
import static com.exedio.cope.pattern.EnumMapFieldItem.Language.DE;
import static com.exedio.cope.pattern.EnumMapFieldItem.Language.EN;
import static com.exedio.cope.pattern.EnumMapFieldItem.Language.PL;
import static com.exedio.cope.pattern.EnumMapFieldItem.Language.SUBCLASS;
import static org.junit.Assert.assertEquals;

import com.exedio.cope.Model;
import org.junit.Test;

public class EnumMapFieldDefaultTest
{
	@SuppressWarnings("unused")
	private static final Model MODEL = new Model(TYPE);

	@Test public void testModel()
	{
		assertEquals("defaultDE", text.getField(DE).getDefaultConstant());
		assertEquals("defaultEN", text.getField(EN).getDefaultConstant());
		assertEquals("defaultPL", text.getField(PL).getDefaultConstant());
		assertEquals("defaultSUBCLASS", text.getField(SUBCLASS).getDefaultConstant());
		assertEquals(false, text.isFinal());
		assertEquals(true,  text.isMandatory());
		assertEquals(false, text.isInitial());
	}
}
