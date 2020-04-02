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

package com.exedio.cope.instrument.testfeature;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;

@WrapFeature
public final class OptionFeature
{
	@Wrap(order=10)
	public void simple(@SuppressWarnings("unused") final Item item)
	{
		throw new RuntimeException();
	}

	public OptionFeature fail()
	{
		// fail if run during instrumentation:
		try
		{
			Class.forName("com.exedio.cope.instrument.testmodel.DontInstrument");
		}
		catch (final ClassNotFoundException e)
		{
			throw new RuntimeException("this method must not be called during instrumentation", e);
		}
		return this;
	}
}
