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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

final class JavaRepository
{
	enum Stage
	{
		BUILD,
		BETWEEN,
		GENERATE
	}

	Stage stage = Stage.BUILD;

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
				//noinspection ResultOfObjectAllocationIgnored OK: constructor registers at parent
				new LocalCopeType(javaClass, javaClass.kind);
			}
		}

		stage = Stage.GENERATE;

		for(final LocalCopeType ct : copeTypeByJavaClass.values())
			ct.endBuildStage();
	}

	boolean isBuildStage()
	{
		return stage==Stage.BUILD;
	}

	boolean isGenerateStage()
	{
		return stage==Stage.GENERATE;
	}

	void add(final JavaFile file)
	{
		assert stage==Stage.BUILD;
		files.add(file);
	}

	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType") // method is not public
	List<JavaFile> getFiles()
	{
		assert stage==Stage.GENERATE;
		return files;
	}

	void add(final JavaClass javaClass)
	{
		assert stage==Stage.BUILD;

		if(javaClassByCanonicalName.put(javaClass.getCanonicalName(), javaClass)!=null)
			throw new RuntimeException(javaClass.getCanonicalName());
	}

	JavaClass getJavaClass(final String name)
	{
		return javaClassByCanonicalName.get(name);
	}

	void add(final LocalCopeType copeType)
	{
		assert stage==Stage.BETWEEN;

		if(copeTypeByJavaClass.putIfAbsent(copeType.javaClass, copeType)!=null)
			throw new RuntimeException(copeType.javaClass.getFullName());
		//System.out.println("--------- put cope type: "+name);
	}

	LocalCopeType getCopeType(final String className)
	{
		assert stage==Stage.BETWEEN || stage==Stage.GENERATE;

		final JavaClass javaClass = getJavaClass(className);
		if(javaClass==null)
			throw new RuntimeException("no java class for "+className);

		final LocalCopeType result = copeTypeByJavaClass.get(javaClass);
		if(result==null)
			throw new RuntimeException("no cope type for "+className);

		return result;
	}
}
