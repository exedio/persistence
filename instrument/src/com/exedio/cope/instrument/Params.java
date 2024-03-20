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

import static com.exedio.cope.util.Check.requireNonNegative;
import static java.util.Objects.requireNonNull;

import com.exedio.cope.util.CharSet;
import java.io.File;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class Params
{
	List<File> sourceDirectories;
	/** in {@link #sourceDirectories}, the files to look at for understanding source code, but not instrument */
	List<File> ignoreFiles;
	boolean verify = false;
	Charset charset = StandardCharsets.US_ASCII;
	private final List<Method> generateDeprecateds = new ArrayList<>();
	private final List<Method> disabledWraps = new ArrayList<>();

	private int maxwarns = 10000;

	void setMaxwarns(final int value)
	{
		maxwarns = requireNonNegative(value, "maxwarns");
	}

	String getMaxwarns()
	{
		return String.valueOf(maxwarns);
	}

	GeneratedAnnotationRetention generatedAnnotationRetention = GeneratedAnnotationRetention.SOURCE;

	enum GeneratedAnnotationRetention
	{
		/**
		 * Corresponds to {@link java.lang.annotation.RetentionPolicy#SOURCE}
		 */
		SOURCE(Generated.class),

		/**
		 * Corresponds to {@link java.lang.annotation.RetentionPolicy#CLASS}
		 */
		@SuppressWarnings("unused") // OK: accessed by ant via reflection
		CLASS(GeneratedClass.class);

		final Class<? extends Annotation> clazz;

		GeneratedAnnotationRetention(final Class<? extends Annotation> clazz)
		{
			this.clazz = requireNonNull(clazz);
		}
	}

	boolean nullabilityAnnotations = false;
	IntegerTypeSuffix serialVersionUIDSuffix = IntegerTypeSuffix.small;
	boolean directSetValueMap = true;
	boolean finalMethodInFinalClass = true;
	boolean useConstantForEmptySetValuesArray = true;
	boolean serialAnnotation = false;
	String introCommentOneTime = null;
	boolean verbose = false;
	File buildDirectory = null;
	private File timestampFile = null;
	final List<File> classpath = new ArrayList<>();
	final List<File> resources = new ArrayList<>();
	private Function<File,Boolean> fileFilter = f->true;

	List<File> getSourceDirectories()
	{
		if (sourceDirectories==null) throw new RuntimeException("sourceDirectories not set");
		return Collections.unmodifiableList(sourceDirectories);
	}

	List<File> getAllJavaSourceFiles()
	{
		final List<File> javaSourceFiles = new ArrayList<>();
		for (final File sourceDirectory : getSourceDirectories())
		{
			collectFiles(javaSourceFiles, sourceDirectory);
		}
		return javaSourceFiles;
	}

	List<File> getJavaSourceFilesExcludingIgnored()
	{
		if (ignoreFiles==null) throw new RuntimeException("ignoreFiles not set");
		final List<File> result = getAllJavaSourceFiles();
		result.removeAll(ignoreFiles);
		return result;
	}

	void setFileFilter(final Function<File, Boolean> fileFilter)
	{
		this.fileFilter = fileFilter;
	}

	private void collectFiles(final List<File> collectInto, final File fileOrDir)
	{
		if (!fileFilter.apply(fileOrDir))
		{
			return;
		}
		if (!fileOrDir.exists())
		{
			throw new RuntimeException(fileOrDir.getAbsolutePath()+" does not exist");
		}
		else if (fileOrDir.isDirectory())
		{
			//noinspection DataFlowIssue OK: checks isDirectory before calling listFiles
			for (final File child: fileOrDir.listFiles())
			{
				collectFiles(collectInto, child);
			}
		}
		else if (fileOrDir.getName().endsWith(".java"))
		{
			collectInto.add(fileOrDir);
		}
	}

	void setTimestampFile(final File timestampFile)
	{
		this.timestampFile = timestampFile;
	}

	File getTimestampFile()
	{
		if (timestampFile!=null)
			return timestampFile;
		else if (buildDirectory!=null)
			return new File(buildDirectory, "instrument.timestamp");
		else
			throw new RuntimeException("neither timestampFile nor buildDirectory set");
	}

	void addGenerateDeprecated(final String s) throws HumanReadableException
	{
		generateDeprecateds.add(new Method(s));
	}

	List<Method> getGenerateDeprecateds()
	{
		return Collections.unmodifiableList(generateDeprecateds);
	}

	void addDisabledWrap(final String s) throws HumanReadableException
	{
		disabledWraps.add(new Method(s));
	}

	List<Method> getDisabledWraps()
	{
		return Collections.unmodifiableList(disabledWraps);
	}

	private static final String[] NO_PARAMETERS = new String[0];

	static final class Method
	{
		private final String originalLine;
		final String className;
		final String methodName;
		final String[] parameterTypes;

		Method(final String s) throws HumanReadableException
		{
			for (int i=0; i<s.length(); i++)
			{
				if (Character.isWhitespace(s.charAt(i))) throw new HumanReadableException("<generateDeprecated> must not contain space or newline");
			}
			final Pattern pattern = Pattern.compile("([^#]*)#([^\\\\]*)\\((.*)\\)");
			final Matcher matcher = pattern.matcher(s);
			if (!matcher.matches()) throw new HumanReadableException("invalid <generateDeprecated> syntax in "+s);
			this.originalLine = s;
			this.className = matcher.group(1);
			this.methodName = matcher.group(2);
			this.parameterTypes = matcher.group(3).isEmpty() ? NO_PARAMETERS : matcher.group(3).split(",");
		}

		@Override
		public String toString()
		{
			return originalLine;
		}
	}

	static final class Suppressor
	{
		private final TreeSet<String> set = new TreeSet<>();
		private final SortedSet<String> unmodifiable = Collections.unmodifiableSortedSet(set);

		void add(final String s) throws HumanReadableException
		{
			if(CHARSET.indexOfNotContains(s)>=0)
				throw new HumanReadableException("invalid character in <suppressWarnings>, allowed is " + CHARSET);
			set.add(s);
		}

		private static final CharSet CHARSET = new CharSet('-', '-', '0', '9', 'A', 'Z', 'a', 'z');

		SortedSet<String> get()
		{
			return unmodifiable;
		}
	}

	final Suppressor suppressWarningsType = new Suppressor();
	final Suppressor suppressWarningsConstructor = new Suppressor();
	final Suppressor suppressWarningsWrapper = new Suppressor();
}
