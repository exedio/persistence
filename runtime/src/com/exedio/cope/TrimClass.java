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

/**
 * MySQL maximum length is 64:
 * <a href="https://dev.mysql.com/doc/refman/8.0/en/identifier-length.html">...</a>
 * MySQL does support check constraints only since version 8.
 * <p>
 * PostgreSQL maximum length is 63:
 * <a href="https://www.postgresql.org/docs/9.6/sql-syntax-lexical.html#SQL-SYNTAX-IDENTIFIERS">...</a>
 */
enum TrimClass
{
	legacy(25),
	standard(60); // on MySQL a primary key constraint does not have a name

	@SuppressWarnings("NonSerializableFieldInSerializableClass") // OK: enum is singleton
	final Trimmer trimmer;

	TrimClass(final int maxLength)
	{
		trimmer = new Trimmer(maxLength);
	}
}
