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

import static java.util.Objects.requireNonNull;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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

	public GenerateDeprecated createGenerateDeprecated()
	{
		return new GenerateDeprecated();
	}

	public DisableWrap createDisableWrap()
	{
		return new DisableWrap();
	}

	public Suppressors createSuppressWarnings()
	{
		return new Suppressors();
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
		getProject().log("instrument parameter timestampFile is deprecated - no longer required");
		params.setTimestampFile(value);
	}

	public void setBuildDirectory(final File value)
	{
		params.buildDirectory = value;
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

	public void setJavaxAnnotationGenerated(final boolean value)
	{
		params.javaxAnnotationGenerated = value;
	}

	public void setNullabilityAnnotations(final boolean value)
	{
		params.nullabilityAnnotations = value;
	}

	public void setSerialVersionUIDSuffix(final IntegerTypeSuffix value)
	{
		params.serialVersionUIDSuffix = value;
	}

	public void setDirectSetValueMap(final boolean value)
	{
		params.directSetValueMap = value;
	}

	public void setPublicConstructorInAbstractClass(final boolean value)
	{
		throwNoLongerSupported("publicConstructorInAbstractClass", false);
	}

	public void setPrivateMethodFinal(final boolean value)
	{
		throwNoLongerSupported("privateMethodFinal", false);
	}

	public void setFinalMethodInFinalClass(final boolean value)
	{
		params.finalMethodInFinalClass = value;
	}

	public void setWildcardClass(final boolean value)
	{
		throwNoLongerSupported("wildcardClass", true);
	}

	public void setWildcardClassFullyQualified(final boolean value)
	{
		throwNoLongerSupported("wildcardClassFullyQualified", false);
	}

	public void setConvertTT2Code(final boolean value)
	{
		throwNoLongerSupported("convertTT2Code", true);
	}

	public void setGenericConstructorOneline(final boolean value)
	{
		throwNoLongerSupported("genericConstructorOneline", true);
	}

	public void setVerbose(final boolean value)
	{
		params.verbose = value;
	}

	private static void throwNoLongerSupported(final String attribute, final boolean defaultValue)
	{
		throw new BuildException(
				"Attribute " + attribute + " is no longer supported. " +
				"The instrumentor now behaves as specified by the default (" + defaultValue + ") of the attribute.");
	}

	@Override
	public void execute() throws BuildException
	{
		if (params.sourceDirectories==null)
		{
			throw new BuildException("'dir' required");
		}
		if (params.buildDirectory==null)
		{
			final String targetName = getOwningTarget().getName();
			final File buildRoot = new File(getProject().getBaseDir(), "build");
			final File instrumentDir = new File(buildRoot, "instrument");
			params.buildDirectory = new File(instrumentDir, targetName);
		}
		checkBuildDirectoryIsUnique();
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

			final File buildFile = getProject().resolveFile(getLocation().getFileName());
			params.resources.add(buildFile);

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

	private void checkBuildDirectoryIsUnique() throws BuildException
	{
		final String propKey = "instrument_buildguard_"+requireNonNull(params.buildDirectory).getAbsolutePath();
		final String existingValue = getProject().getProperty(propKey);
		if (existingValue==null)
		{
			getProject().setProperty(propKey, getLocation().toString());
		}
		else if (!existingValue.equals(getLocation().toString()))
		{
			throw new BuildException(
				"<instrument> calls at '"+existingValue+"' and '"+getLocation()+"' share the same buildDirectory. " +
				"This happens if buildDirectory is explicitly set to the same value, or there are two <instrument> calls " +
				"in the same ant target that use the default buildDirectory. Please configure unique buildDirectories."
			);
		}
	}

	private boolean isInSourceFiles(final File file)
	{
		if (params.sourceDirectories==null) throw new BuildException("'dir' not set");
		File check = file;
		while (check!=null)
		{
			if (params.getSourceDirectories().contains(check))
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

	@SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE") // OK: checks isDirectory before calling listFiles
	private static void addRecursively(final File fileOrDir, final List<File> addTo, final boolean expandDirectories)
	{
		if (!fileOrDir.exists())
		{
			throw new RuntimeException(fileOrDir.getAbsolutePath()+" does not exist");
		}
		if (expandDirectories && fileOrDir.isDirectory())
		{
			//noinspection ConstantConditions OK: checks isDirectory before calling listFiles
			for (final File entry: fileOrDir.listFiles())
			{
				//noinspection ConstantConditions OK: seems to be bug in inspection
				addRecursively(entry, addTo, expandDirectories);
			}
			return;
		}
		addTo.add(fileOrDir);
	}

	public static final class Ignore
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

	public final class GenerateDeprecated
	{
		public void addText(final String text)
		{
			try
			{
				params.addGenerateDeprecated(getProject().replaceProperties(text));
			}
			catch (final HumanReadableException e)
			{
				throw new BuildException(e.getMessage());
			}
		}
	}

	public final class DisableWrap
	{
		public void addText(final String text)
		{
			try
			{
				params.addDisabledWrap(getProject().replaceProperties(text));
			}
			catch (final HumanReadableException e)
			{
				throw new BuildException(e.getMessage());
			}
		}
	}

	public final class Suppressors
	{
		public Suppressor createConstructor()
		{
			return new Suppressor(params.suppressWarningsConstructor);
		}

		public Suppressor createWrapper()
		{
			return new Suppressor(params.suppressWarningsWrapper);
		}
	}

	public static final class Suppressor
	{
		final Params.Suppressor back;

		Suppressor(final Params.Suppressor back)
		{
			this.back = back;
		}

		public void addText(final String text)
		{
			try
			{
				back.add(text);
			}
			catch (final HumanReadableException e)
			{
				throw new BuildException(e.getMessage());
			}
		}
	}
}
