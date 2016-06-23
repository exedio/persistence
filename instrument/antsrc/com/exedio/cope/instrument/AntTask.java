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

package com.exedio.cope.instrument;

import com.exedio.cope.instrument.Params.IntegerTypeSuffix;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

public final class AntTask extends Task
{
	private final ArrayList<DataType> fileSetsOrLists = new ArrayList<>();
	private final Params params = new Params();
	private final ArrayList<Path> resources = new ArrayList<>();

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

	public void setCharset(final String value)
	{
		params.charset = Charset.forName(value);
	}

	public void setTimestampFile(final File value)
	{
		params.timestampFile = value;
	}

	public void addResources(final Path value)
	{
		resources.add(value);
	}

	@Deprecated
	public void setEncoding(final String value)
	{
		System.out.println(
				"<instrument ... uses deprecated attribute encoding=\"" + value + "\", " +
				"use charset=\"" + value + "\" instead.");
		setCharset(value);
	}

	public void setLongJavadoc(final boolean value)
	{
		params.longJavadoc = value;
	}

	public void setFinalArgs(final boolean value)
	{
		params.finalArgs = value;
	}

	public void setNullabilityAnnotations(final boolean value)
	{
		params.nullabilityAnnotations = value;
	}

	public void setSuppressUnusedWarningOnPrivateActivationConstructor(final boolean value)
	{
		params.suppressUnusedWarningOnPrivateActivationConstructor = value;
	}

	public void setSerialVersionUID(final boolean value)
	{
		params.serialVersionUID = value;
	}

	// TODO parameter type IntegerTypeSuffix instead of converting manually
	// is supported by Ant version 1.9.0
	// travis-ci just supports Ant version 1.8.2
	public void setSerialVersionUIDSuffix(final String value)
	{
		params.serialVersionUIDSuffix = IntegerTypeSuffix.valueOf(value);
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

	public void setParenthesesOnEmptyMemberAnnotations(final boolean value)
	{
		params.parenthesesOnEmptyMemberAnnotations = value;
	}

	public void setDeprecatedFullyQualified(final boolean value)
	{
		params.deprecatedFullyQualified = value;
	}

	public void setOverrideOnSeparateLine(final boolean value)
	{
		params.overrideOnSeparateLine = value;
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
			final ArrayList<File> sourcefiles = new ArrayList<>();
			final ArrayList<File> resourceFiles = new ArrayList<>();
			final HashSet<File> sourcefileSet = new HashSet<>();

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
			for (final Path resource: resources)
			{
				if ( params.timestampFile==null )
				{
					throw new BuildException("resources require timestampFile");
				}
				for (final String fileName: resource.list())
				{
					final File file = new File(fileName);
					addRecursively(file, resourceFiles);
				}
			}

			(new Main()).run(sourcefiles, params, resourceFiles);
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

	private void addRecursively(final File fileOrDir, final ArrayList<File> addTo)
	{
		if (!fileOrDir.exists())
		{
			throw new RuntimeException(fileOrDir.getAbsolutePath()+" does not exist");
		}
		if (fileOrDir.isDirectory())
		{
			for (final File entry: fileOrDir.listFiles())
			{
				addRecursively(entry, addTo);
			}
			return;
		}
		if (fileOrDir.isFile())
		{
			addTo.add(fileOrDir);
			return;
		}
		throw new RuntimeException("can't handle "+fileOrDir.getAbsolutePath());
	}
}
