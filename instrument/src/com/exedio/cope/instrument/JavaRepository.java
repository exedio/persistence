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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class JavaRepository
{
	enum Stage
	{
		BUILD,
		BETWEEN,
		GENERATE
	}

	private Stage stage = Stage.BUILD;

	private final ArrayList<JavaFile> files = new ArrayList<>();
	private final HashMap<String, JavaClass> javaClassByCanonicalName = new HashMap<>();

	private final HashMap<JavaClass, LocalCopeType> copeTypeByJavaClass = new HashMap<>();

	void endBuildStage()
	{
		if (stage!=Stage.BUILD) throw new RuntimeException();
		stage = Stage.BETWEEN;

		// TODO put this into a new class CopeType
		for(final JavaClass javaClass : javaClassByCanonicalName.values())
		{
			if(javaClass.isInterface())
				continue;

			if(javaClass.kind!=null)
			{
				add( new LocalCopeType(javaClass, javaClass.kind) );
			}
		}

		stage = Stage.GENERATE;

		for(final LocalCopeType ct : copeTypeByJavaClass.values())
			ct.endBuildStage();
	}

	void assertBuildStage()
	{
		assertStage(Stage.BUILD);
	}

	void assertNotBuildStage()
	{
		assertStage(Stage.BETWEEN, Stage.GENERATE);
	}

	void assertNotGenerateStage()
	{
		assertStage(Stage.BUILD, Stage.BETWEEN);
	}

	void assertGenerateStage()
	{
		assertStage(Stage.GENERATE);
	}

	private void assertStage(final Stage expected)
	{
		if (stage!=expected)
			throw new RuntimeException("expected stage "+expected+" but was "+stage);
	}

	private void assertStage(final Stage expectedA, final Stage expectedB)
	{
		if (stage!=expectedA && stage!=expectedB)
			throw new RuntimeException("expected stage "+expectedA+" or "+expectedB+" but was "+stage);
	}

	void add(final JavaFile file)
	{
		assertBuildStage();
		files.add(file);
	}

	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // method is not public
	List<JavaFile> getFiles()
	{
		assertGenerateStage();
		return files;
	}

	void add(final JavaClass javaClass)
	{
		assertBuildStage();
		if(javaClassByCanonicalName.put(javaClass.getCanonicalName(), javaClass)!=null)
			throw new RuntimeException(javaClass.getCanonicalName());
	}

	JavaClass getJavaClass(final String name)
	{
		return javaClassByCanonicalName.get(name);
	}

	private void add(final LocalCopeType copeType)
	{
		assertStage(Stage.BETWEEN);

		if(copeTypeByJavaClass.putIfAbsent(copeType.javaClass, copeType)!=null)
			throw new RuntimeException(copeType.javaClass.getFullName());
		//System.out.println("--------- put cope type: "+name);
	}

	@Nonnull
	LocalCopeType getCopeType(final String className)
	{
		final JavaClass javaClass = requireNonNull(
				getJavaClass(className), className
		);
		final LocalCopeType copeType = getCopeType(javaClass);
		return requireNonNull(copeType, javaClass::toString);
	}

	/**
	 * @return null for JavaClasses where the class doesn't have a super-class annotated with {@link WrapType}
	 */
	@Nullable
	LocalCopeType getCopeType(final JavaClass javaClass)
	{
		assertNotBuildStage();
		return copeTypeByJavaClass.get(javaClass);
	}
}
