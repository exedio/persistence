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

import io.micrometer.core.instrument.Tags;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Pattern;

public final class EnvironmentInfo
{
	final Driver sqlDriver;
	private final String catalog;
	private final Product database;
	private final Product driver;

	EnvironmentInfo(
			final Driver sqlDriver,
			final String catalog,
			final DatabaseMetaData dmd)
		throws SQLException
	{
		this.sqlDriver = sqlDriver;
		this.catalog = catalog;
		database = new Product(
				"database",
				dmd.getDatabaseProductName(),
				dmd.getDatabaseProductVersion(),
				dmd.getDatabaseMajorVersion(),
				dmd.getDatabaseMinorVersion());
		driver = new Product(
				"driver",
				dmd.getDriverName(),
				driverVersionPattern.matcher(dmd.getDriverVersion()).replaceAll("$1"),
				dmd.getDriverMajorVersion(),
				dmd.getDriverMinorVersion());
	}

	@SuppressWarnings("RegExpSimplifiable") // OK: [0-9] is easier to understand than \d
	private static final Pattern driverVersionPattern =
			Pattern.compile("\\b([0-9,a-f]{8})[0-9,a-f]{32}\\b");

	/**
	 * Provides {@link java.sql.Connection#getCatalog()}.
	 */
	public String getCatalog()
	{
		return catalog;
	}

	/**
	 * Provides {@link java.sql.DatabaseMetaData#getDatabaseProductName()}.
	 */
	public String getDatabaseProductName()
	{
		return database.name;
	}

	/**
	 * Provides {@link java.sql.DatabaseMetaData#getDatabaseProductVersion()}.
	 */
	public String getDatabaseProductVersion()
	{
		return database.version;
	}

	/**
	 * Provides {@link java.sql.DatabaseMetaData#getDatabaseMajorVersion()}.
	 */
	public int getDatabaseMajorVersion()
	{
		return database.majorVersion;
	}

	/**
	 * Provides {@link java.sql.DatabaseMetaData#getDatabaseMinorVersion()}.
	 */
	public int getDatabaseMinorVersion()
	{
		return database.minorVersion;
	}

	/**
	 * Provides {@link java.sql.DatabaseMetaData#getDriverName()}.
	 */
	public String getDriverName()
	{
		return driver.name;
	}

	/**
	 * Provides {@link java.sql.DatabaseMetaData#getDriverVersion()}.
	 */
	public String getDriverVersion()
	{
		return driver.version;
	}

	/**
	 * Provides {@link java.sql.DatabaseMetaData#getDriverMajorVersion()}.
	 */
	public int getDriverMajorVersion()
	{
		return driver.majorVersion;
	}

	/**
	 * Provides {@link java.sql.DatabaseMetaData#getDriverMinorVersion()}.
	 */
	public int getDriverMinorVersion()
	{
		return driver.minorVersion;
	}

	/**
	 * Returns
	 * {@link #getDatabaseProductVersion()} with
	 * {@link #getDatabaseMajorVersion()} and
	 * {@link #getDatabaseMinorVersion()}.
	 */
	public String getDatabaseVersionDescription()
	{
		return database.getVersionDescription();
	}

	/**
	 * Returns
	 * {@link #getDriverVersion()} with
	 * {@link #getDriverMajorVersion()} and
	 * {@link #getDriverMinorVersion()}.
	 */
	public String getDriverVersionDescription()
	{
		return driver.getVersionDescription();
	}

	public String getDriverClass()
	{
		return sqlDriver.getClass().getName();
	}

	public Properties asProperties()
	{
		final Properties result = new Properties();
		database.asProperties(result);
		driver  .asProperties(result);
		return result;
	}

	void putRevisionEnvironment(final HashMap<String, String> e)
	{
		database.putRevisionEnvironment(e);
		driver  .putRevisionEnvironment(e);
	}

	Tags tags()
	{
		return Tags.of(
				"catalog", catalog!=null ? catalog : "<null>",
				"databaseName",    database.name,
				"databaseVersion", database.getVersionDescription(),
				"driverName",    driver.name,
				"driverVersion", driver.getVersionDescription(),
				"driverClass", sqlDriver.getClass().getName());
	}

	public boolean isDatabaseVersionAtLeast(final int major, final int minor)
	{
		return database.isVersionAtLeast(major, minor);
	}

	public boolean isDriverVersionAtLeast(final int major, final int minor)
	{
		return driver.isVersionAtLeast(major, minor);
	}

	void requireDatabaseVersionAtLeast(final String name, final int major, final int minor)
	{
		database.requireVersionAtLeast(name, major, minor);
	}

	private static final class Product
	{
		final String product;
		final String name;
		final String version;
		final int majorVersion;
		final int minorVersion;

		Product(
				final String product,
				final String name,
				final String version,
				final int majorVersion,
				final int minorVersion)
		{
			this.product = product;
			this.name = name;
			this.version = version;
			this.majorVersion = majorVersion;
			this.minorVersion = minorVersion;
		}

		String getVersionDescription()
		{
			final String v = "(.*\\D|^)" + majorVersion + "\\." + minorVersion + "(\\D.*|$)";
			if(Pattern.matches(v, version))
				return version;

			return version + ' ' + '(' + majorVersion + '.' + minorVersion + ')';
		}

		void asProperties(final Properties result)
		{
			result.setProperty(product + ".name", name);
			result.setProperty(product + ".version", getVersionDescription());
		}

		void putRevisionEnvironment(final HashMap<String, String> e)
		{
			e.put(product + ".name", name);
			e.put(product + ".version", version);
			e.put(product + ".version.major", String.valueOf(majorVersion));
			e.put(product + ".version.minor", String.valueOf(minorVersion));
		}

		boolean isVersionAtLeast(final int major, final int minor)
		{
			if(major<majorVersion)
				return true;
			else if(major>majorVersion)
				return false;
			else
				return minor<=minorVersion;
		}

		void requireVersionAtLeast(final String name, final int major, final int minor)
		{
			if(!this.name.equals(name))
				throw new IllegalArgumentException(
						"requires " + product + ' ' + name + ", " +
						"but was " + this.name + ' ' + getVersionDescription());
			if(!isVersionAtLeast(major, minor))
				throw new IllegalArgumentException(
						"requires " + product + " version " + major + '.' + minor + " or later, " +
						"but was " + name + ' ' + getVersionDescription());
		}

		@Override
		public String toString()
		{
			return name + ' ' + getVersionDescription();
		}
	}

	@Override
	public String toString()
	{
		return
				database + " " +
				driver + ' ' +
				getDriverClass() + ' ' +
				catalog;
	}
}
