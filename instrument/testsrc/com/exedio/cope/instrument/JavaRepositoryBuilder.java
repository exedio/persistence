package com.exedio.cope.instrument;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import javax.annotation.processing.RoundEnvironment;

/**
 * helper class to construct a {@link JavaRepository} from instrument testsrc path
 */
final class JavaRepositoryBuilder
{
	@Nullable
	private List<File> files;

	private List<File> srcDirs = List.of(new File("instrument/testsrc"));

	private List<File> classpath = List.of(
			new File("lib/exedio-cope-util.jar"),
			new File("lib/junit-jupiter-api.jar"),
			new File("lib/apiguardian-api.jar"),
			new File("lib/jsr305.jar"),
			new File("build/classes/instrument/src"),
			new File("build/classes/instrument/testfeaturesrc"),
			new File("build/classes/instrument/annosrc"),
			new File("build/classes/instrument/testlibsrc"),
			new File("build/classes/runtime/src"),
			new File("build/classes/runtime/patternsrc"),
			new File("build/classes/runtime/servletsrc"),
			new File("build/classes/runtime/testsrc")
	);

	private Charset charset = StandardCharsets.US_ASCII;

	/** @param clazz sources for this class must be available in instrument/testsrc; inner classes are not supported */
	JavaRepositoryBuilder filter(final Class<?> clazz)
	{
		return filter(new File("instrument/testsrc/"+clazz.getName().replace(".", "/")+".java"));
	}

	JavaRepositoryBuilder filter(final File file)
	{
		if (!file.isFile())
			throw new RuntimeException("not a file: "+file);
		if (files==null)
			files = new ArrayList<>();
		files.add(file);
		return this;
	}

	JavaRepositoryBuilder charset(final Charset charset)
	{
		this.charset = charset;
		return this;
	}

	JavaRepositoryBuilder withSrcDir(final File srcDir)
	{
		srcDirs = Stream.concat(srcDirs.stream(), Stream.of(srcDir)).collect(Collectors.toList());
		return this;
	}

	JavaRepositoryBuilder withClasspath(final File classpathEntry)
	{
		classpath = Stream.concat(classpath.stream(), Stream.of(classpathEntry)).collect(Collectors.toList());
		return this;
	}

	private static boolean equalOrParent(final File f, final File g)
	{
		//noinspection TailRecursion
		return f.equals(g) || (f.getParentFile()!=null && equalOrParent(f.getParentFile(), g));
	}

	void buildAndRun(final Consumer<JavaRepository> test) throws HumanReadableException, IOException
	{
		final Params params = new Params();
		if (files!=null)
			params.setFileFilter(
					checkedFile -> files.stream().anyMatch( whitelistedFile -> equalOrParent(whitelistedFile, checkedFile) )
			);
		final Path temp = Files.createTempDirectory(getClass().getSimpleName());
		params.buildDirectory = new File(temp.toFile(), "build");
		for (final File testsrc : srcDirs)
		{
			if (!testsrc.isDirectory())
				throw new RuntimeException();
		}
		params.sourceDirectories = srcDirs;
		params.charset = charset;
		params.ignoreFiles = Collections.emptyList();
		params.classpath.addAll(classpath);
		final JavaRepository repository = new JavaRepository();
		final InterimProcessor interimProcessor = new InterimProcessor(params);
		final FillRepositoryProcessor fillRepositoryProcessor = new FillRepositoryProcessor(params, repository, interimProcessor);
		new JavacRunner(
				interimProcessor,
				fillRepositoryProcessor,
				new JavacProcessor()
				{
					@Override
					void processInternal(final RoundEnvironment roundEnv)
					{
						test.accept(repository);
					}
				}
		).run(params);
	}
}
