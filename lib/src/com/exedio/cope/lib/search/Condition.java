
package com.exedio.cope.lib.search;

import com.exedio.cope.lib.Statement;
import com.exedio.cope.lib.Type;

public abstract class Condition
{
	public abstract void appendStatement(Statement statment);
	
	public abstract void check(final Type type);
}
