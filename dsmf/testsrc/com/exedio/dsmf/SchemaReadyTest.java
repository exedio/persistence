/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

package com.exedio.dsmf;

public abstract class SchemaReadyTest extends SchemaTest
{
	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		final Schema schema = getSchema();
		schema.tearDown();
		schema.create();
	}

	@Override
	public void tearDown() throws Exception
	{
		getSchema().tearDown();
		
		super.tearDown();
	}

	protected abstract Schema getSchema();

	protected final Schema getVerifiedSchema()
	{
		final Schema result = getSchema();
		result.verify();
		return result;
	}

}
