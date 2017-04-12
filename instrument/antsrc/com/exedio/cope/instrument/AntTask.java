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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

public final class AntTask extends Task
{
	private final Params params = new Params();
	private Ignore ignore;

	public void setDir(final Path path)
	{
		if (params.sourceDirectories!=null)
		{
			throw new BuildException("'dir' already specified");
		}
		params.sourceDirectories=new ArrayList<>();
		for (final String entry: path.list())
		{
			final File file=getProject().resolveFile(entry);
			if (!file.isDirectory())
			{
				throw new BuildException("'dir' must be directories: "+file.getAbsolutePath());
			}
			params.sourceDirectories.add(file);
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

	public void addConfiguredResources(final Path value)
	{
		// get_Configured_Resources means we get called _after_ the resources have been set up by ant
		pathToFiles(value, params.resources, true);
	}

	public void addConfiguredClasspath(final Path value)
	{
		// get_Configured_Classpath means we get called _after_ the classpath has been set up by ant
		pathToFiles(value, params.classpath, false);
	}

	@Deprecated
	public void setEncoding(final String value)
	{
		System.out.println(
				"<instrument ... uses deprecated attribute encoding=\"" + value + "\", " +
				"use charset=\"" + value + "\" instead.");
		setCharset(value);
	}

	public void setConfigByTags(final ConfigurationByJavadocTags value)
	{
		params.configByTags = value;
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
		params.serialVersionUIDEnabled = value;
	}

	public void setSerialVersionUIDSuffix(final IntegerTypeSuffix value)
	{
		params.serialVersionUIDSuffix = value;
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

	public void setHintFormat(final HintFormat value)
	{
		params.hintFormat = value;
	}

	public void setVerbose(final boolean value)
	{
		params.verbose = value;
	}

	@Override
	public void execute() throws BuildException
	{
		if (params.sourceDirectories==null)
		{
			throw new BuildException("'dir' required");
		}
		final List<File> javaSourceFiles = params.getAllJavaSourceFiles();
		if (javaSourceFiles.isEmpty())
		{
			throw new BuildException("no java files in 'dir'");
		}
		try
		{
			if (ignore==null)
			{
				params.ignoreFiles = Collections.emptyList();
			}
			else
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
					final List<File> ignoreFiles = new ArrayList<>(javaSourceFiles);
					ignoreFiles.removeAll(listedFiles);
					params.ignoreFiles = ignoreFiles;
				}
				else
				{
					params.ignoreFiles = listedFiles;
				}
			}
			if (params.timestampFile==null && !params.resources.isEmpty())
			{
				throw new BuildException("resources require timestampFile");
			}
			if (params.hintFormat!=HintFormat.forTags)
			{
				final StringBuilder invalidParameters=new StringBuilder();
				if (params.longJavadoc!=true)
					invalidParameters.append("longJavadoc=\"true\" ");
				if (params.finalArgs!=true)
					invalidParameters.append("finalArgs=\"true\" ");
				if (params.genericSetValueArray!=true)
					invalidParameters.append("genericSetValueArray=\"true\" ");
				if (params.parenthesesOnEmptyMemberAnnotations!=false)
					invalidParameters.append("parenthesesOnEmptyMemberAnnotations=\"false\" ");
				if (params.deprecatedFullyQualified!=true)
					invalidParameters.append("deprecatedFullyQualified=\"true\" ");
				if (params.overrideOnSeparateLine!=true)
					invalidParameters.append("overrideOnSeparateLine=\"true\" ");
				if (invalidParameters.length()!=0)
					throw new BuildException(
						"unsupported <instrument> arguments for hintFormat other than \"forTags\" - please use:"+System.lineSeparator()+"\t"+invalidParameters+System.lineSeparator()+
						"(these are the defaults, so you can also drop the arguments completely)"
					);
			}

			if (params.hintFormat==HintFormat.forTags && params.configByTags!=ConfigurationByJavadocTags.support)
			{
				System.out.println("<instrument ... uses deprecated combination of hintFormat and configByTags - use hintFormat=\"forAnnotations\" instead.");
			}
			final File buildFile = getProject().resolveFile(getLocation().getFileName());
			params.resources.add(buildFile);

			if (params.configByTags==ConfigurationByJavadocTags.convertToAnnotations)
			{
				ConvertTagsToAnnotations.convert(params);
				throw new HumanReadableException("convertToAnnotations - stopping build");
			}
			new Main().run(params);
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
		if (params.sourceDirectories==null) throw new BuildException("'dir' not set");
		File check = file;
		while (check!=null)
		{
			if (params.sourceDirectories.contains(check))
				return true;
			check = check.getParentFile();
		}
		return false;
	}

	private void pathToFiles(final Path resource, final List<File> addTo, final boolean expandDirectories)
	{
		for (final String fileName: resource.list())
		{
			final File file = getProject().resolveFile(fileName);
			addRecursively(file, addTo, expandDirectories);
		}
	}

	private void addRecursively(final File fileOrDir, final List<File> addTo, final boolean expandDirectories)
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
