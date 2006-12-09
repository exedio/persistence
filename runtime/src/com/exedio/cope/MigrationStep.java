/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import com.exedio.dsmf.Driver;

final class MigrationStep // TODO make public when migration has matured
{
	final int version;
	final String comment;
	final Body body;
	
	MigrationStep(final int version, final String comment, final Body body) // TODO make public when migration has matured
	{
		this.version = version;
		this.comment = comment;
		this.body = body;
		
		if(version<0)
			throw new IllegalArgumentException("version must not be negative");
		if(comment==null)
			throw new NullPointerException("comment must not be null");
		if(body==null)
			throw new NullPointerException("body must not be null");
	}
	
	static abstract class Body // TODO make public when migration has matured
	{
		private final Object lock = new Object();
		private Driver driver;
		
		final String[] getPatch(final Driver driver)
		{
			assert driver!=null;
			
			synchronized(lock)
			{
				assert this.driver==null;
				
				try
				{
					this.driver = driver;
					return execute();
				}
				finally
				{
					this.driver = null;
				}
			}
		}
		
		public final String protectName(final String name)
		{
			return driver.protectName(name);
		}
		
		public final String renameColumn(final String tableName, final String oldColumnName, final String newColumnName, final String columnType)
		{
			return driver.renameColumn(tableName, oldColumnName, newColumnName, columnType);
		}
		
		public final String createColumn(final String tableName, final String columnName, final String columnType)
		{
			return driver.createColumn(tableName, columnName, columnType);
		}
		
		public final String modifyColumn(final String tableName, final String columnName, final String newColumnType)
		{
			return driver.modifyColumn(tableName, columnName, newColumnType);
		}

		public final String dropPrimaryKeyConstraint(final String tableName, final String constraintName)
		{
			return driver.dropPrimaryKeyConstraint(tableName, constraintName);
		}
		
		public final String dropForeignKeyConstraint(final String tableName, final String constraintName)
		{
			return driver.dropForeignKeyConstraint(tableName, constraintName);
		}
		
		public final String dropUniqueConstraint(final String tableName, final String constraintName)
		{
			return driver.dropUniqueConstraint(tableName, constraintName);
		}
		
		public abstract String[] execute();
	}
	
	@Override
	public String toString()
	{
		return "MS" + version + ':' + comment;
	}
}
