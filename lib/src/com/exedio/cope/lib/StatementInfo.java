
package com.exedio.cope.lib;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public final class StatementInfo
{
	final ArrayList childs = new ArrayList();
	final String text;
	
	StatementInfo(final String text)
	{
		this.text = text;
	}
	
	public String getText()
	{
		return text;
	}
	
	public Collection getChilds()
	{
		return Collections.unmodifiableList(childs);
	}
	
	void addChild(final StatementInfo newChild)
	{
		childs.add(newChild);
	}
	
	void print(final PrintStream o)
	{
		printInternal(o, 0);
	}

	void printInternal(final PrintStream o, int level)
	{
		for(int i=0; i<level; i++)
			o.print("  ");
		o.println(text);
		level++;
		for(Iterator i = childs.iterator(); i.hasNext(); )
			((StatementInfo)i.next()).printInternal(o, level);
	}

}
