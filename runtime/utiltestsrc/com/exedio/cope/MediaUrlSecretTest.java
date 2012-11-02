/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.util.Properties.getSource;

import java.util.Properties;

import junit.framework.TestCase;

import com.exedio.cope.pattern.Media;
import com.exedio.cope.pattern.MediaPath;

public class MediaUrlSecretTest extends TestCase
{
	public void testOff()
	{
		final ConnectProperties props = props(null);
		model.connect(props);

		assertEquals(false, MediaPath.isUrlGuessingPreventedSecurely(model.getConnectProperties()));
		assertEquals(false, AnItem.media.isUrlGuessingPreventedSecurely(model.getConnectProperties()));
	}

	public void testOn()
	{
		final ConnectProperties props = props("1234567890");
		model.connect(props);

		assertEquals(true, MediaPath.isUrlGuessingPreventedSecurely(props));
		assertEquals(true, AnItem.media.isUrlGuessingPreventedSecurely(props));
	}

	public void testTooShort()
	{
		final ConnectProperties props = props("123456789");
		model.connect(props);

		assertEquals(false, MediaPath.isUrlGuessingPreventedSecurely(props));
		assertEquals(false, AnItem.media.isUrlGuessingPreventedSecurely(props));
	}

	public void testEmpty()
	{
		final ConnectProperties props = props("");
		model.connect(props);

		assertEquals(false, MediaPath.isUrlGuessingPreventedSecurely(props));
		assertEquals(false, AnItem.media.isUrlGuessingPreventedSecurely(props));
	}

	private static ConnectProperties props(final String secret)
	{
		final Properties source = new Properties();
		source.setProperty("connection.url", "jdbc:hsqldb:mem:MediaUrlSecretTest");
		source.setProperty("connection.user", "sa");
		source.setProperty("connection.password", "");
		final Properties context = new Properties();
		if(secret!=null)
			context.setProperty("media.url.secret", secret);
		return new ConnectProperties(
				getSource(source , "MediaUrlSecretTestSource" ),
				getSource(context, "MediaUrlSecretTestContext"));
	}

	@Override
	protected void tearDown() throws Exception
	{
		model.disconnect();
		super.tearDown();
	}

	static class AnItem extends Item
	{
		static final Media media = new Media();
		static final Type<AnItem> TYPE = TypesBound.newType(AnItem.class);

		private static final long serialVersionUID = 1l;
		private AnItem(final ActivationParameters ap) { super(ap); }
	}

	private static final Model model = new Model(AnItem.TYPE);
}
