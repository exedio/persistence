
package com.exedio.cope.lib.search;

import com.exedio.cope.lib.Database;

public abstract class Condition
{
	public abstract void appendStatement(Database.Statement statment);
}
