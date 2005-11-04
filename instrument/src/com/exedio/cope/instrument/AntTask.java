/*
 * Copyright (C) 2004-2005 exedio GmbH (www.exedio.com)
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package com.exedio.cope.instrument;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;

public final class AntTask extends Task
{
	private final ArrayList fileSets = new ArrayList();
	private final ArrayList fileLists = new ArrayList();
	private boolean verbose = true;

	public void addFileset(final FileSet fileSet)
	{
		fileSets.add(fileSet);
	}

	public void addFilelist(final FileList fileList)
	{
		fileLists.add(fileList);
	}
	
	public void setVerbose(final boolean verbose)
	{
		this.verbose = verbose;
	}

   public void execute() throws BuildException
	{
		try
		{
			final ArrayList sourcefiles = new ArrayList();
			
			for(final Iterator i = fileSets.iterator(); i.hasNext(); )
			{
				final FileSet fileSet = (FileSet)i.next();
				final DirectoryScanner directoryScanner = fileSet.getDirectoryScanner(getProject());
				final File dir = fileSet.getDir(getProject());
				final String[] fileNames = directoryScanner.getIncludedFiles();
				for(int j = 0; j<fileNames.length; j++)
					sourcefiles.add(new File(dir, fileNames[j]));
			}
			for(final Iterator i = fileLists.iterator(); i.hasNext(); )
			{
				final FileList fileList = (FileList)i.next();
				final File dir = fileList.getDir(getProject());
				final String[] fileNames = fileList.getFiles(getProject());
				for(int j = 0; j<fileNames.length; j++)
					sourcefiles.add(new File(dir, fileNames[j]));
			}

			(new Main()).run(sourcefiles, verbose);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new BuildException(e);
		}
	}

}
