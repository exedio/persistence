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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.Assert.fail;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.pattern.MediaPath;
import com.exedio.cope.util.IllegalPropertiesException;
import com.exedio.cope.util.Sources;
import java.util.Properties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class MediaUrlSecretTest
{
	@Test void testOff()
	{
		final ConnectProperties props = props(null);
		model.connect(props);

		assertEquals(false, MediaPath.isUrlGuessingPreventedSecurely(props));
		assertEquals(null, props.getMediaUrlSecret());
	}

	@Test void testOn()
	{
		final ConnectProperties props = props("1234567890");
		model.connect(props);

		assertEquals(true, MediaPath.isUrlGuessingPreventedSecurely(props));
		assertEquals("1234567890", props.getMediaUrlSecret());
	}

	@Test void testTooShort()
	{
		try
		{
			props("123456789");
			fail();
		}
		catch(final IllegalPropertiesException e)
		{
			assertEquals(
					"property media.url.secret in MediaUrlSecretTestSource " +
					"must have at least 10 characters, " +
					"but was '123456789' with just 9 characters",
					e.getMessage());
		}
	}

	@Test void testEmpty()
	{
		final ConnectProperties props = props("");
		model.connect(props);

		assertEquals(false, MediaPath.isUrlGuessingPreventedSecurely(props));
		assertEquals(null, props.getMediaUrlSecret());
	}

	private static ConnectProperties props(final String secret)
	{
		final Properties source = new Properties();
		source.setProperty("connection.url", "jdbc:hsqldb:mem:MediaUrlSecretTest");
		source.setProperty("connection.username", "sa");
		source.setProperty("connection.password", "");
		if(secret!=null)
			source.setProperty("media.url.secret", secret);
		return ConnectProperties.create(
				Sources.view(source, "MediaUrlSecretTestSource"));
	}

	@SuppressWarnings("static-method")
	@AfterEach public final void tearDown()
	{
		if(model.isConnected())
			model.disconnect();
	}

	@WrapperIgnore
	static final class AnItem extends Item
	{
		static final Type<AnItem> TYPE = TypesBound.newType(AnItem.class);
		private static final long serialVersionUID = 1l;
		private AnItem(final ActivationParameters ap) { super(ap); }
	}

	private static final Model model = new Model(AnItem.TYPE);
}
