
package com.exedio.cope.lib.database;

import com.exedio.cope.lib.Database;

public class OracleDatabase extends Database
{

	public String protectName(final String name)
	{
		return '"' + name + '"';
	}
}
