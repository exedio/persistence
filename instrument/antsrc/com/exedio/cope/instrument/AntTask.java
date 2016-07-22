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

import com.exedio.cope.instrument.Params.ConfigurationByJavadocTags;
import com.exedio.cope.instrument.Params.IntegerTypeSuffix;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

public final class AntTask extends Task
{
	private final Params params = new Params();
	private final ArrayList<Path> resources = new ArrayList<>();
	private final ArrayList<Path> classpath = new ArrayList<>();
	private Ignore ignore;

	public void setDir(final Path path)
	{
		if (params.sourceFiles!=null)
		{
			throw new BuildException("'dir' already specified");
		}
		params.sourceFiles=new ArrayList<>();
		for (final String entry: path.list())
		{
			final File file=getProject().resolveFile(entry);
			if (!file.isDirectory())
			{
				throw new BuildException("'dir' must be directories: "+file.getAbsolutePath());
			}
			collectFiles(params.sourceFiles, file, true, true);
		}
		if (params.sourceFiles.isEmpty())
		{
			throw new BuildException("no java files in 'dir'");
		}
	}

	public Ignore createIgnore()
	{
		if (ignore!=null) throw new BuildException("duplicate <[dont]ignore> declaration");
		ignore=new Ignore(false);
		return ignore;
	}

	public Ignore createDontignore()
	{
		if (ignore!=null) throw new BuildException("duplicate <[dont]ignore> declaration");
		ignore=new Ignore(true);
		return ignore;
	}

	public void setVerify(final boolean value)
	{
		params.verify = value;
	}

	public void setCharset(final String value)
	{
		params.charset = Charset.forName(value);
	}

	public void setMaxwarns(final int value)
	{
		params.setMaxwarns(value);
	}

	public void setTimestampFile(final File value)
	{
		params.timestampFile = value;
	}

	public void addResources(final Path value)
	{
		resources.add(value);
	}

	public void addClasspath(final Path value)
	{
		classpath.add(value);
	}

	@Deprecated
	public void setEncoding(final String value)
	{
		System.out.println(
				"<instrument ... uses deprecated attribute encoding=\"" + value + "\", " +
				"use charset=\"" + value + "\" instead.");
		setCharset(value);
	}

	// TODO parameter type ConfigurationByJavadocTags instead of converting manually
	// is supported by Ant version 1.9.0
	// travis-ci just supports Ant version 1.8.2
	public void setConfigByTags(final String value)
	{
		params.configByTags = ConfigurationByJavadocTags.valueOf(value);
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
			final ArrayList<File> resourceFiles = new ArrayList<>();
			final ArrayList<File> classpathFiles = new ArrayList<>();

			if (ignore!=null)
			{
				final List<File> listedFiles = new ArrayList<>();
				for (final FileSet fileSet: ignore.fileSets)
				{
					final DirectoryScanner ds=fileSet.getDirectoryScanner(getProject());
					for (final String path: ds.getIncludedFiles())
					{
						final File file = new File(ds.getBasedir(), path);
						if (!file.isFile())
						{
							throw new RuntimeException("ignore list entry is not a file: "+file.getAbsolutePath());
						}
						if (!isInSourceFiles(file))
						{
							throw new BuildException("ignore list entry is not in 'dir': "+file.getAbsolutePath());
						}
						listedFiles.add(file);
					}
				}
				if (ignore.dontIgnore)
				{
					if (params.sourceFiles==null) throw new RuntimeException();
					final List<File> ignoreFiles = new ArrayList<>(params.sourceFiles);
					ignoreFiles.removeAll(listedFiles);
					params.ignoreFiles.addAll(ignoreFiles);
				}
				else
				{
					params.ignoreFiles.addAll(listedFiles);
				}
			}
			if (params.timestampFile==null && !resources.isEmpty())
			{
				throw new BuildException("resources require timestampFile");
			}
			final File buildFile = getProject().resolveFile(getLocation().getFileName());
			resourceFiles.add(buildFile);
			pathsToFiles(resources, resourceFiles, true);
			pathsToFiles(classpath, classpathFiles, false);

			if (params.configByTags==ConfigurationByJavadocTags.convertToAnnotations)
			{
				ConvertTagsToAnnotations.convert(params, classpathFiles);
				throw new HumanReadableException("convertToAnnotations - stopping build");
			}
			(new Main()).run(params, classpathFiles, resourceFiles);
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

	private boolean isInSourceFiles(final File file)
	{
		if ( params.sourceFiles==null ) throw new BuildException("'dir' not set");
		return params.sourceFiles.contains(file);
	}

	private void pathsToFiles(final ArrayList<Path> paths, final ArrayList<File> addTo, final boolean expandDirectories)
	{
		for (final Path resource: paths)
		{
			for (final String fileName: resource.list())
			{
				final File file = getProject().resolveFile(fileName);
				addRecursively(file, addTo, expandDirectories);
			}
		}
	}

	private void addRecursively(final File fileOrDir, final ArrayList<File> addTo, final boolean expandDirectories)
	{
		if (!fileOrDir.exists())
		{
			throw new RuntimeException(fileOrDir.getAbsolutePath()+" does not exist");
		}
		if (expandDirectories && fileOrDir.isDirectory())
		{
			for (final File entry: fileOrDir.listFiles())
			{
				addRecursively(entry, addTo, expandDirectories);
			}
			return;
		}
		addTo.add(fileOrDir);
	}

	private static void collectFiles(final List<File> collectInto, final File fileOrDir, final boolean expandDirectories, final boolean onlyJava)
	{
		if (!fileOrDir.exists())
		{
			throw new RuntimeException(fileOrDir.getAbsolutePath()+" does not exist");
		}
		else if (expandDirectories && fileOrDir.isDirectory())
		{
			for (final File child: fileOrDir.listFiles())
			{
				collectFiles(collectInto, child, expandDirectories, onlyJava);
			}
		}
		else if (!onlyJava || fileOrDir.isDirectory() || fileOrDir.getName().endsWith(".java"))
		{
			collectInto.add(fileOrDir);
		}
	}

	public final static class Ignore
	{
		final boolean dontIgnore;
		final List<FileSet> fileSets = new ArrayList<>();

		Ignore(final boolean dontIgnore)
		{
			this.dontIgnore=dontIgnore;
		}

		public void addFileset(final FileSet fileSet)
		{
			fileSets.add(fileSet);
		}
	}
}
