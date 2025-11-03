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

package com.exedio.filevault;

import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.util.Sources;
import java.util.Properties;

public class VaultHttpServiceNoProxyTest extends VaultHttpServiceTest
{
	@Override
	protected Properties getServiceProperties()
	{
		final Properties result = super.getServiceProperties();
		result.setProperty("proxy.mode", "NONE");
		return result;
	}

	@Override
	Source sourcesPut()
	{
		return Sources.EMPTY;
	}
}
