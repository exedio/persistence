
package com.exedio.cope.lib.search;

import com.exedio.cope.lib.Attribute;
import com.exedio.cope.lib.StringAttribute;
import com.exedio.cope.lib.database.Database;

public abstract class Condition
{
	public abstract void appendSQL(Database database, StringBuffer bf);
}
