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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

abstract class AbstractVaultProperties extends Properties
{
	final Service valueService(final String key, final boolean writable)
	{
		final ServiceFactory<VaultService, VaultServiceParameters> constructor =
				valueService(key, VaultService.class, VaultServiceParameters.class);

		return new Service(key, constructor, writable);
	}

	@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_NEEDS_THIS")
	final class Service
	{
		private final ServiceFactory<VaultService, VaultServiceParameters> constructor;
		private final boolean writable;
		private final Properties properties;

		private Service(
				final String key,
				final ServiceFactory<VaultService, VaultServiceParameters> constructor,
				final boolean writable)
		{
			this.constructor = constructor;
			this.writable = writable;

			final VaultServiceProperties ann = constructor.getServiceClass().getAnnotation(VaultServiceProperties.class);
			if(ann==null)
				throw newException(key, "must name a class annotated by " + VaultServiceProperties.class.getName());

			final Factory<?> factory;
			try
			{
				factory = ann.value().newInstance();
			}
			catch(final ReflectiveOperationException e)
			{
				throw newException(key, "must name a class annotated by " + VaultServiceProperties.class.getName(), e);
			}

			this.properties = value(key, factory);
		}

		VaultService newService(final VaultProperties vaultProperties)
		{
			return constructor.newInstance(new VaultServiceParameters(
					vaultProperties, properties, writable));
		}
	}

	AbstractVaultProperties(final Source source)
	{
		super(source);
	}
}
