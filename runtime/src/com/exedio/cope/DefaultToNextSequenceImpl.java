/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Sequence;

/**
 * Implements the defaultToNext functionality.
 * @see IntegerField#defaultToNext(int)
 *
 * @author Ralf Wiebicke
 */
final class DefaultToNextSequenceImpl implements DefaultToNextImpl
{
	final Model model;
	private final int start;
	private final Database database;
	private final String name;

	DefaultToNextSequenceImpl(final IntegerField field, final int start, final Database database)
	{
		if(!database.driver.supportsSequences())
			throw new RuntimeException("database does not support sequences");
		
		this.model = field.getType().getModel();
		this.start = start;
		this.database = database;
		this.name = database.makeName(field.getType().schemaId + '_' + field.getName() + "_Seq");
	}
	
	public void makeSchema(final Schema schema)
	{
		new Sequence(schema, name, start);
	}

	public int next()
	{
		return database.dialect.nextSequence(database, model.getCurrentTransaction().getConnection(), name);
	}
	
	public void flush()
	{
		// empty
	}
}
