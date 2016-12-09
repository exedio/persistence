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

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.Model;

public final class ModelMain
{
	public static void main(final String[] args)
	{
		if(args.length!=2)
			throw new RuntimeException("must have two arguments, model and action");

		final Model model = ModelByString.get(args[0]);
		model.connect(new ConnectProperties(ConnectProperties.getDefaultPropertyFile()));
		try
		{
			final String action = args[1];
			if("create".equals(action))
				model.createSchema();
			else if("drop".equals(action))
				model.dropSchema();
			else if("tearDown".equals(action))
				model.tearDownSchema();
			else
				throw new RuntimeException("illegal action, must be 'create', 'drop', or 'tearDown'");
		}
		finally
		{
			model.disconnect();
		}
	}

	private ModelMain()
	{
		// prevent instantiation
	}
}
