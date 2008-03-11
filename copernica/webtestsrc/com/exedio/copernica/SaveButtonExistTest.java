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

package com.exedio.copernica;


public class SaveButtonExistTest extends AbstractWebTest
{

	public void testSaveButtonExists()
	{
		beginAt("copernica.jsp");
		assertTitleEquals("Copernica");

		clickLinkWithText("String Item");
		assertTitleEquals("String Item");
		
		final String SAVE = "SAVE";
		clickLinkWithText("StringItem.1");
		assertTitleEquals("StringItem.1");
		assertSubmitButtonPresent(SAVE);

		clickLinkWithText("Collision Item1");
		assertTitleEquals("Collision Item1");
		
		clickLinkWithText("EmptyItem.1");
		assertTitleEquals("EmptyItem.1");
		assertSubmitButtonNotPresent(SAVE);
	}
}
