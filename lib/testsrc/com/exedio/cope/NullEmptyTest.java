/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.testmodel.StringItem;

/**
 * Test, whether database converts empty strings to null,
 * and how the framework hides such behaviour from the user.
 * @author Ralf Wiebicke
 */
public class NullEmptyTest extends TestmodelTest
{
	StringItem item;
	boolean supports;
	String emptyString;

	public void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item = new StringItem("NullEmptyTest"));

		supports = model.supportsEmptyStrings();
		emptyString = supports ? "" : null;
	}

	public void testNullEmpty()
			throws IntegrityViolationException, MandatoryViolationException
	{
		assertEquals(null, item.getAny());

		item.setAny("");
		assertEquals(emptyString, item.getAny());
		restartTransaction();
		assertEquals(emptyString, item.getAny());

		item.setAny(null);
		assertEquals(null, item.getAny());
		restartTransaction();
		assertEquals(null, item.getAny());
		
		try
		{
			item.setMandatory("");
			if(supports)
				assertEquals("", item.getMandatory());
			else
				fail();
		}
		catch(MandatoryViolationException e)
		{
			assertTrue(!supports);
			assertEquals(item.mandatory, e.getMandatoryAttribute());
			assertEquals(item, e.getItem());
		}
		
		final StringItem item2 = new StringItem("", false);
		deleteOnTearDown(item2);
		assertEquals(emptyString, item2.getAny());
		restartTransaction();
		assertEquals(emptyString, item2.getAny());
		
		StringItem item3 = null;
		try
		{
			item3 = new StringItem("", 0.0);
			deleteOnTearDown(item3);
			if(supports)
				assertEquals("", item3.getMandatory());
			else
				fail();
		}
		catch(MandatoryViolationException e)
		{
			assertTrue(!supports);
			assertEquals(item3.mandatory, e.getMandatoryAttribute());
			assertEquals(item3, e.getItem());
		}
	}

}
