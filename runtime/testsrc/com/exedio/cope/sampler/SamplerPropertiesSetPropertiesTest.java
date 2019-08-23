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

package com.exedio.cope.sampler;

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.TestSources.single;
import static com.exedio.cope.util.Sources.cascade;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.misc.ConnectToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SamplerPropertiesSetPropertiesTest
{
	@Test void test()
	{
		final SamplerProperties p = SamplerProperties.factory().create(cascade(
				single("cope.dialect", "com.exedio.cope.HsqldbDialect"),
				single("cope.connection.url", "blah"),
				single("cope.connection.username", "blah"),
				single("cope.connection.password", "blah")
		));
		assertNull(ConnectToken.getProperties(MODEL));
		p.setProperties(MODEL);
		assertSame(p.getCope(), ConnectToken.getProperties(MODEL));
	}

	@BeforeEach
	@AfterEach
	void tearDown()
	{
		ConnectToken.removePropertiesVoid(MODEL);
	}

	@WrapperType(constructor=NONE, indent=2, comments=false)
	static final class AnItem extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	private static final Model MODEL = new Model(AnItem.TYPE);
}
