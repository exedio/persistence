package com.exedio.cope.instrument;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import javax.annotation.processing.RoundEnvironment;

/**
 * helper class to construct a {@link JavaRepository} from instrument testsrc path
 */
final class JavaRepositoryBuilder
{
	@Nullable
	private List<File> files;

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
		final File testsrc = new File("instrument/testsrc");
		if (!testsrc.isDirectory())
			throw new RuntimeException();
		params.sourceDirectories = Collections.singletonList(testsrc);
		params.ignoreFiles = Collections.emptyList();
		params.classpath.add(new File("lib/exedio-cope-util.jar"));
		params.classpath.add(new File("lib/junit-jupiter-api.jar"));
		params.classpath.add(new File("lib/apiguardian-api.jar"));
		params.classpath.add(new File("lib/jsr305.jar"));
		params.classpath.add(new File("build/classes/instrument/src"));
		params.classpath.add(new File("build/classes/instrument/testfeaturesrc"));
		params.classpath.add(new File("build/classes/instrument/annosrc"));
		params.classpath.add(new File("build/classes/instrument/testlibsrc"));
		params.classpath.add(new File("build/classes/runtime/src"));
		params.classpath.add(new File("build/classes/runtime/patternsrc"));
		params.classpath.add(new File("build/classes/runtime/servletsrc"));
		params.classpath.add(new File("build/classes/runtime/testsrc"));
		final JavaRepository repository = new JavaRepository();
		final InterimProcessor interimProcessor = new InterimProcessor(params);
		final FillRepositoryProcessor fillRepositoryProcessor = new FillRepositoryProcessor(repository, interimProcessor);
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
