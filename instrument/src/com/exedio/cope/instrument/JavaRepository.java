package com.exedio.cope.instrument;

import java.util.ArrayList;
import java.util.List;

public final class JavaRepository
{
	private final ArrayList files = new ArrayList();

	void add(final JavaFile file)
	{
		files.add(file);
	}
	
	final List getFiles()
	{
		return files;
	}

}
