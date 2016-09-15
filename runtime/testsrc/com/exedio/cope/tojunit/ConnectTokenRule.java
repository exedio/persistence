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

package com.exedio.cope.tojunit;

import static java.util.Objects.requireNonNull;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Model;
import com.exedio.cope.misc.ConnectToken;
import org.junit.rules.ExternalResource;

public final class ConnectTokenRule extends ExternalResource
{
	private final Model model;

	public ConnectTokenRule(final Model model)
	{
		this.model = requireNonNull(model, "model");
	}

	public void set(final ConnectProperties properties)
	{
		before.assertCalled();
		ConnectToken.setProperties(model, properties);
	}

	public ConnectProperties remove()
	{
		before.assertCalled();
		return ConnectToken.removeProperties(model);
	}


	private final BeforeCall before = new BeforeCall();

	@Override
	protected void before()
	{
		before.onCall();
	}

	@Override
	protected void after()
	{
		ConnectToken.removeProperties(model);
		if(model.isConnected())
			model.disconnect();
	}
}
