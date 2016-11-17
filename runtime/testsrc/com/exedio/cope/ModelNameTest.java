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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.exedio.cope.instrument.WrapperType;
import org.junit.Test;

public class ModelNameTest
{
	private static final Model ANONYMOUS = Model.builder().add(Anonymous.TYPE).build();

	@Test public void testAnonymous()
	{
		assertTrue(ANONYMOUS.toString(), ANONYMOUS.toString().startsWith(Model.class.getName() + '@'));

		ANONYMOUS.enableSerialization(ModelNameTest.class, "ANONYMOUS");
		assertEquals(ModelNameTest.class.getName() + "#ANONYMOUS", ANONYMOUS.toString());
	}


	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	static class Anonymous extends Item
	{
		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<Anonymous> TYPE = com.exedio.cope.TypesBound.newType(Anonymous.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		protected Anonymous(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}
