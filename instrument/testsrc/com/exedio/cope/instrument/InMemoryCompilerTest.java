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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public class InMemoryCompilerTest
{
	private static Object invokeStatic(final ClassLoader cl, final String className, final String methodName)
	{
		try
		{
			final Class<?> clazz = cl.loadClass(className);
			final Method method = clazz.getMethod(methodName);
			return method.invoke(null);
		}
		catch (final ReflectiveOperationException e)
		{
			throw new RuntimeException(e);
		}
	}

	private static Object invoke(final ClassLoader cl, final String className, final String methodName)
	{
		try
		{
			final Class<?> clazz = cl.loadClass(className);
			final Method method = clazz.getMethod(methodName);
			return method.invoke(clazz.getConstructor().newInstance());
		}
		catch (final ReflectiveOperationException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Test
	public void sameClassTwoCompilers() throws InMemoryCompiler.CompileException
	{
		final Path path = Paths.get("HelloWorld.java");
		final InMemoryCompiler compilerA = new InMemoryCompiler();
		compilerA.addJavaFile(path, "public class HelloWorld { public static String message() { return \"Hello World A\"; } }");
		final ClassLoader clA = compilerA.compile(JavacRunner.getJavaCompiler(new Params()), "");
		final InMemoryCompiler compilerB = new InMemoryCompiler();
		compilerB.addJavaFile(path, "public class HelloWorld { public static String message() { return \"Hello World B\"; } }");
		final ClassLoader clB = compilerB.compile(JavacRunner.getJavaCompiler(new Params()), "");
		assertEquals("Hello World A", invokeStatic(clA, "HelloWorld", "message"));
		assertEquals("Hello World B", invokeStatic(clB, "HelloWorld", "message"));
	}

	@Test
	public void twoClasses() throws InMemoryCompiler.CompileException
	{
		final InMemoryCompiler compiler = new InMemoryCompiler();
		compiler.addJavaFile(
			Paths.get("FirstClass.java"),
			"public class FirstClass { " +
				"public static String message() { return new SecondClass().message(); } "+
			"}"
		);
		compiler.addJavaFile(
			Paths.get("SecondClass.java"),
			"class SecondClass { " +
				"String message() { return \"from second\"; } "+
			"}"
		);
		final ClassLoader cl = compiler.compile(JavacRunner.getJavaCompiler(new Params()), "");
		assertEquals("from second", invokeStatic(cl, "FirstClass", "message"));
	}

	@Test
	public void callParent() throws InMemoryCompiler.CompileException
	{
		final InMemoryCompiler compiler = new InMemoryCompiler();
		compiler.addJavaFile(
			Paths.get("FirstClass.java"),
			"public class FirstClass { " +
				"public static String message() { return "+getClass().getName()+".calledFromInMemory(); } "+
			"}"
		);
		final ClassLoader cl = compiler.compile(JavacRunner.getJavaCompiler(new Params()), "build/classes/instrument/testsrc");
		assertEquals("from parent", invokeStatic(cl, "FirstClass", "message"));
	}

	/** called from InMemory class in test {@link #callParent()} */
	@SuppressWarnings("unused") // OK: called by in-memory-compiled code
	public static final String calledFromInMemory()
	{
		return "from parent";
	}

	@Test
	public void doesntCompile()
	{
		final InMemoryCompiler compiler = new InMemoryCompiler();
		compiler.addJavaFile(Paths.get("HelloWorld.java"), "clazz HelloWorld { }");
		final Params params = new Params();
		try
		{
			compiler.compile(JavacRunner.getJavaCompiler(params), "");
			fail();
		}
		catch (final InMemoryCompiler.CompileException e)
		{
			assertEquals(1, e.getDiagnostics().size());
			assertEquals(1, e.getDiagnostics().get(0).getLineNumber());
		}
	}

	@Test
	public void duplicatePath() throws InMemoryCompiler.CompileException
	{
		final Path path = Paths.get("HelloWorld.java");
		final InMemoryCompiler compiler = new InMemoryCompiler();
		compiler.addJavaFile(path, "public class HelloWorld { public int version() { return 1; } }");
		try
		{
			compiler.addJavaFile(path, "public class HelloWorld { public int version() { return 2; } }");
			fail();
		}
		catch (final IllegalArgumentException e)
		{
			assertEquals("there already is a java file for HelloWorld.java; cope instrumentor does not support two top-level classes in one source file", e.getMessage());
		}
		assertEquals(1, invoke(compiler.compile(JavacRunner.getJavaCompiler(new Params()), ""), "HelloWorld", "version"));
	}

	@Test
	public void unnormalizedPath() throws InMemoryCompiler.CompileException
	{
		final InMemoryCompiler compiler = new InMemoryCompiler();
		compiler.addJavaFile(Paths.get("HelloWorld.java"), "public class HelloWorld { public int version() { return 1; } }");
		try
		{
			compiler.addJavaFile(Paths.get("foo/../HelloWorld.java"), "public class HelloWorld { public int version() { return 2; } }");
			fail();
		}
		catch (final IllegalArgumentException e)
		{
			assertEquals("path contains redundant elements", e.getMessage());
		}
		assertEquals(1, invoke(compiler.compile(JavacRunner.getJavaCompiler(new Params()), ""), "HelloWorld", "version"));
	}
}
