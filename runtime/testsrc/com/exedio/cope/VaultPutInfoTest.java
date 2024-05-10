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

import static com.exedio.cope.instrument.Visibility.NONE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.vault.VaultPutInfo;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.junit.jupiter.api.Test;

public class VaultPutInfoTest
{
	@SuppressWarnings("deprecation") // OK: tests deprecated API
	@Test void testDefault() throws UnknownHostException
	{
		final VaultPutInfo info = new VaultPutInfo() {};

		assertEquals(null, info.getField());
		assertEquals(null, info.getFieldString());
		assertEquals(null, info.getItem());
		assertEquals(null, info.getItemString());
		assertEquals(InetAddress.getLocalHost().getHostName(), info.getOrigin());
		assertEquals(InetAddress.getLocalHost().getHostName(), com.exedio.cope.vault.VaultPutInfo.getOriginDefault());
	}

	@Test void testStandard()
	{
		final MyItem item = MyItem.TYPE.activate(55);
		@SuppressWarnings("deprecation") // OK: tests deprecated API
		final VaultPutInfo info = new VaultPutInfo()
		{
			@Override
			public DataField getField()
			{
				return MyItem.field;
			}
			@Override
			public Item getItem()
			{
				return item;
			}
		};

		assertSame(MyItem.field, info.getField());
		assertEquals("MyItem.field", info.getFieldString());
		assertSame(item, info.getItem());
		assertEquals("MyItem-55", info.getItemString());
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class MyItem extends Item
	{
		@WrapperIgnore
		static final DataField field = new DataField();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class,MyItem::new);

		@com.exedio.cope.instrument.Generated
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
