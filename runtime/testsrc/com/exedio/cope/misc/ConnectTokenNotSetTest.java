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

package com.exedio.cope.misc;

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.misc.ConnectToken.getTokens;
import static com.exedio.cope.misc.ConnectToken.issue;
import static com.exedio.cope.misc.ConnectToken.issueIfConnected;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.instrument.WrapperType;
import org.junit.jupiter.api.Test;

public class ConnectTokenNotSetTest
{
	private static final Model model = new Model(AnItem.TYPE);

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class AnItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static
	{
		model.enableSerialization(ConnectTokenNotSetTest.class, "model");
	}

	@Test void testIt()
	{
		assertFalse(model.isConnected());

		final String message =
			"No properties set for model " +
			"com.exedio.cope.misc.ConnectTokenNotSetTest#model, " +
			"use ConnectToken.setProperties.";

		try
		{
			//noinspection resource
			issue(model, "tokenNameIssue");
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(message, e.getMessage());
		}
		assertFalse(model.isConnected());

		try
		{
			//noinspection resource
			issueIfConnected(model, "tokenNameIssueIfConnected");
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(message, e.getMessage());
		}
		assertFalse(model.isConnected());

		try
		{
			getTokens(model);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(message, e.getMessage());
		}
		assertFalse(model.isConnected());
	}
}
