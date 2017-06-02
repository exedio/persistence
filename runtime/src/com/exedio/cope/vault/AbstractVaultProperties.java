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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

abstract class AbstractVaultProperties extends Properties
{
	Service valueService(final String key, final boolean writable)
	{
		final Constructor<? extends VaultService> constructor =
				valueConstructor(key, VaultService.class, VaultServiceParameters.class);

		return new Service(key, constructor, writable);
	}

	@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_NEEDS_THIS")
	final class Service
	{
		private final Constructor<? extends VaultService> constructor;
		private final boolean writable;
		private final Properties properties;

		private Service(
				final String key,
				final Constructor<? extends VaultService> constructor,
				final boolean writable)
		{
			this.constructor = constructor;
			this.writable = writable;

			final VaultServiceProperties ann = constructor.getDeclaringClass().getAnnotation(VaultServiceProperties.class);
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
			try
			{
				return constructor.newInstance(new VaultServiceParameters(
						vaultProperties, properties, writable));
			}
			catch(final ReflectiveOperationException e)
			{
				throw new RuntimeException(constructor.toGenericString(), e);
			}
		}
	}

	AbstractVaultProperties(final Source source)
	{
		super(source);
	}

	// copied from ConnectProperties
	private <T> Constructor<? extends T> valueConstructor( // TODO move into framework
			final String key,
			final Class<T> superclass,
			final Class<?> parameterType)
	{
		final String name = value(key, (String)null);

		final Class<?> classRaw;
		try
		{
			classRaw = Class.forName(name);
		}
		catch(final ClassNotFoundException e)
		{
			throw newException(key, "must name a class, but was '" + name + '\'', e);
		}

		if(Modifier.isAbstract(classRaw.getModifiers()))
			throw newException(key,
					"must name a non-abstract class, " +
					"but was " + classRaw.getName());

		if(!superclass.isAssignableFrom(classRaw))
			throw newException(key,
					"must name a subclass of " + superclass.getName() + ", " +
					"but was " + classRaw.getName());

		final Class<? extends T> clazz = classRaw.asSubclass(superclass);
		try
		{
			return clazz.getDeclaredConstructor(parameterType);
		}
		catch(final NoSuchMethodException e)
		{
			throw newException(key,
					"must name a class with a constructor with parameter " + parameterType.getName() + ", " +
					"but was " + classRaw.getName(), e);
		}
	}
}
