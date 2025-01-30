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

package com.exedio.cope.instrument.testmodel;

import com.exedio.cope.StringField;
import com.exedio.cope.pattern.HashAlgorithm;

class AssertionFailedHashAlgorithm implements HashAlgorithm
{
	@Override
	public String getID()
	{
		throw new AssertionError();
	}

	@Override
	public String getDescription()
	{
		throw new AssertionError();
	}

	@Override
	public StringField constrainStorage(final StringField storage)
	{
		throw new AssertionError();
	}

	@Override
	public String hash(final String plainText)
	{
		throw new AssertionError();
	}

	@Override
	public boolean check(final String plainText, final String hash)
	{
		throw new AssertionError();
	}
}
