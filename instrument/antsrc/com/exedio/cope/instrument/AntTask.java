/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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
import java.util.HashSet;
import java.util.Iterator;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;

public final class AntTask extends Task
{
	private final ArrayList<DataType> fileSetsOrLists = new ArrayList<DataType>();
	private boolean longJavadoc = true;
	private boolean finalArgs = false;
	private boolean createDeprecated = false;
	private boolean verbose = true;

	public void addFileset(final FileSet fileSet)
	{
		fileSetsOrLists.add(fileSet);
	}

	public void addFilelist(final FileList fileList)
	{
		fileSetsOrLists.add(fileList);
	}
	
	public void setLongJavadoc(final boolean longJavadoc)
	{
		this.longJavadoc = longJavadoc;
	}

	public void setFinalArgs(final boolean finalArgs)
	{
		this.finalArgs = finalArgs;
	}

	public void setCreateDeprecated(final boolean createDeprecated)
	{
		this.createDeprecated = createDeprecated;
	}

	public void setVerbose(final boolean verbose)
	{
		this.verbose = verbose;
	}

	@Override
   public void execute() throws BuildException
	{
		try
		{
			final Project project = getProject();
			final ArrayList<File> sourcefiles = new ArrayList<File>();
			final HashSet<File> sourcefileSet = new HashSet<File>();
			
			for(final Iterator i = fileSetsOrLists.iterator(); i.hasNext(); )
			{
				final Object fileSetOrList = i.next();
				final File dir;
				final String[] fileNames;
				
				if(fileSetOrList instanceof FileSet)
				{
					final FileSet fileSet = (FileSet)fileSetOrList;
					dir = fileSet.getDir(project);
					fileNames = fileSet.getDirectoryScanner(project).getIncludedFiles();
				}
				else
				{
					final FileList fileList = (FileList)fileSetOrList;
					dir = fileList.getDir(project);
					fileNames = fileList.getFiles(project);
				}
				for(int j = 0; j<fileNames.length; j++)
				{
					final File file = new File(dir, fileNames[j]);
					if(sourcefileSet.add(file))
						sourcefiles.add(file);
				}
			}

			(new Main()).run(sourcefiles, new Params(longJavadoc, finalArgs, createDeprecated, verbose));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new BuildException(e);
		}
		catch(AssertionError e)
		{
			e.printStackTrace();
			throw new BuildException(e);
		}
	}

}
