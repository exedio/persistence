/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;

public final class AntTask extends Task
{
	private final ArrayList<DataType> fileSetsOrLists = new ArrayList<DataType>();
	private final Params params = new Params();

	public void addFileset(final FileSet value)
	{
		fileSetsOrLists.add(value);
	}

	public void addFilelist(final FileList value)
	{
		fileSetsOrLists.add(value);
	}

	public void setVerify(final boolean value)
	{
		params.verify = value;
	}

	public void setEncoding(final String value)
	{
		params.encoding = value;
	}

	public void setLongJavadoc(final boolean value)
	{
		params.longJavadoc = value;
	}

	public void setFinalArgs(final boolean value)
	{
		params.finalArgs = value;
	}

	public void setSuppressUnusedWarningOnPrivateActivationConstructor(final boolean value)
	{
		params.suppressUnusedWarningOnPrivateActivationConstructor = value;
	}

	public void setSerialVersionUID(final boolean value)
	{
		params.serialVersionUID = value;
	}

	public void setGenericSetValueArray(final boolean value)
	{
		params.genericSetValueArray = value;
	}

	public void setDirectSetValueMap(final boolean value)
	{
		params.directSetValueMap = value;
	}

	public void setHidingWarningSuppressor(final String value)
	{
		params.hidingWarningSuppressor = value;
	}

	public void setVerbose(final boolean value)
	{
		params.verbose = value;
	}

	@Override
	public void execute() throws BuildException
	{
		try
		{
			final Project project = getProject();
			final ArrayList<File> sourcefiles = new ArrayList<File>();
			final HashSet<File> sourcefileSet = new HashSet<File>();

			for(final Object fileSetOrList : fileSetsOrLists)
			{
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
				for(final String fileName : fileNames)
				{
					final File file = new File(dir, fileName);
					if(sourcefileSet.add(file))
						sourcefiles.add(file);
				}
			}

			(new Main()).run(sourcefiles, params);
		}
		catch(final HumanReadableException e)
		{
			throw new BuildException(e.getMessage());
		}
		catch(final IOException e)
		{
			throw new BuildException(e);
		}
	}
}
