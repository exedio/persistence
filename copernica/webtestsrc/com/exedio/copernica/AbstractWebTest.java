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

package com.exedio.copernica;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sourceforge.jwebunit.TestContext;
import net.sourceforge.jwebunit.WebTestCase;

import org.xml.sax.SAXException;

import com.meterware.httpunit.Button;
import com.meterware.httpunit.WebForm;

public class AbstractWebTest extends WebTestCase
{
	protected static final String SAVE_BUTTON = "Save";

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		final TestContext ctx = getTestContext();
		ctx.setBaseUrl("http://127.0.0.1:8080/copetest-hsqldb/");
		ctx.setAuthorization("admin", "nimda");
		beginAt("console/schema.html");
		submit("CREATE");
		beginAt("init.jsp");
		submit("INIT");
	}
	
	@Override
	public void tearDown() throws Exception
	{
		beginAt("console/schema.html");
		submit("DROP");
		super.tearDown();
	}
	
	protected final void assertFormElementEqualsWithLabel(final String formElementLabel, final String expectedValue)
	{
		final String formElementName = getDialog().getFormElementNameForLabel(formElementLabel);
		assertNotNull("no form element with label "+formElementLabel, formElementName);
		assertFormElementEquals(formElementName, expectedValue);
	}
	
	protected final void submitWithValue(final String buttonValue) throws SAXException
	{
		final Button buttonWithValue = null;
		for(final WebForm form : getDialog().getResponse().getForms())
		{
			for(final Button button : form.getButtons())
			{
				if(button.getValue().equals(buttonValue))
				{
					if(buttonWithValue!=null)
						fail("there is more than one button with value "+buttonValue);
					else
					{
						submit(button.getName());
						return;
					}
				}
			}
		}
		fail("there is no button with value "+buttonValue);
	}
	
	
	// ----------------------------------- adapted from CopeAssert
	
	private static final String DATE_FORMAT_FULL = "dd.MM.yyyy HH:mm:ss.SSS";

	public final static void assertWithinHttpDate(final Date expectedBefore, final Date expectedAfter, final Date actual)
	{
		final long resolution = 1000;
		final long leftTolerance = 995;
		final Date expectedBeforeFloor = new Date(((expectedBefore.getTime()-leftTolerance) / resolution) * resolution);
		final Date expectedAfterCeil   = new Date(((expectedAfter.getTime() / resolution) * resolution) + resolution);

		final SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_FULL);
		final String message =
			"expected date within " + df.format(expectedBeforeFloor) + " (" + df.format(expectedBefore) + ")" +
			" and " + df.format(expectedAfterCeil) + " (" + df.format(expectedAfter) + ")" +
			", but was " + df.format(actual);

		assertTrue(message, !expectedBeforeFloor.after(actual));
		assertTrue(message, !expectedAfterCeil.before(actual));
	}

	public final static void assertWithin(final Date expectedBefore, final Date expectedAfter, final Date actual)
	{
		final SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_FULL);
		final String message =
			"expected date within " + df.format(expectedBefore) +
			" and " + df.format(expectedAfter) +
			", but was " + df.format(actual);

		assertTrue(message, !expectedBefore.after(actual));
		assertTrue(message, !expectedAfter.before(actual));
	}
}
