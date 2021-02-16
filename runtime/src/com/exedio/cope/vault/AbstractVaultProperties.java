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

package com.exedio.cope.vault;

import com.exedio.cope.util.Properties;
import com.exedio.cope.util.ServiceFactory;

abstract class AbstractVaultProperties extends Properties
{
	final Service valueService(final String key, final boolean writable)
	{
		final ServiceFactory<VaultService, VaultServiceParameters> factory =
				valueService(key, VaultService.class, VaultServiceParameters.class);

		return new Service(factory, writable);
	}

	static final class Service
	{
		private final ServiceFactory<VaultService, VaultServiceParameters> factory;
		private final boolean writable;

		private Service(
				final ServiceFactory<VaultService, VaultServiceParameters> factory,
				final boolean writable)
		{
			this.factory = factory;
			this.writable = writable;
		}

		VaultService newService(final VaultProperties vaultProperties, final String serviceKey)
		{
			return factory.newInstance(new VaultServiceParameters(
					vaultProperties, serviceKey, writable));
		}

		Class<? extends VaultService> getServiceClass()
		{
			return factory.getServiceClass();
		}
	}

	AbstractVaultProperties(final Source source)
	{
		super(source);
	}
}
