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

import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;

@WrapFeature
public abstract class OrderFeatureSuper
{
	@SuppressWarnings("static-method")
	@Wrap(order=10)
	public void super10()
	{
		throw new RuntimeException();
	}

	@SuppressWarnings("static-method")
	@Wrap(order=20)
	public void super20()
	{
		throw new RuntimeException();
	}

	@SuppressWarnings("static-method")
	@Wrap(order=30)
	public void super30()
	{
		throw new RuntimeException();
	}
}