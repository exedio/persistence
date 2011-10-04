/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

final class VersionDatabaseMetaData extends DummyDatabaseMetaData
{
	private final int databaseMajor;
	private final int databaseMinor;
	private final int driverMajor;
	private final int driverMinor;

	VersionDatabaseMetaData(
			final int databaseMajor,
			final int databaseMinor,
			final int driverMajor,
			final int driverMinor)
	{
		this.databaseMajor = databaseMajor;
		this.databaseMinor = databaseMinor;
		this.driverMajor = driverMajor;
		this.driverMinor = driverMinor;
	}

	@Override
	public String getDatabaseProductName()
	{
		return "getDatabaseProductName";
	}

	@Override
	public String getDatabaseProductVersion()
	{
		return "getDatabaseProductVersion";
	}

	@Override
	public int getDatabaseMajorVersion()
	{
		return databaseMajor;
	}

	@Override
	public int getDatabaseMinorVersion()
	{
		return databaseMinor;
	}

	@Override
	public String getDriverName()
	{
		return "getDriverName";
	}

	@Override
	public String getDriverVersion()
	{
		return "getDriverVersion";
	}

	@Override
	public int getDriverMajorVersion()
	{
		return driverMajor;
	}

	@Override
	public int getDriverMinorVersion()
	{
		return driverMinor;
	}
}
