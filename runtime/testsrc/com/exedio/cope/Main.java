/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

package com.exedio.cope;

import java.util.Enumeration;
import java.util.HashMap;

import junit.framework.Test;
import junit.framework.TestSuite;

public class Main
{

	private static final void tearDown(final Model model, final Properties properties)
	{
		//System.out.println("teardown " + model.getTypes());
		model.connect(properties);
		model.tearDownDatabase();
	}
	
	private static final void collectModels(final TestSuite suite, final HashMap<Model, Properties> models)
	{
		for(Enumeration e = suite.tests(); e.hasMoreElements(); )
		{
			final Test test = (Test)e.nextElement();

			if(test instanceof com.exedio.cope.junit.CopeTest)
			{
				final com.exedio.cope.junit.CopeTest copeTest = (com.exedio.cope.junit.CopeTest)test;
				final Model model = copeTest.model;
				if(!models.containsKey(model))
					models.put(model, copeTest.getProperties());
			}
			else if(test instanceof TestSuite)
				collectModels((TestSuite)test, models);
		}
	}
	
	public static void main(String[] args)
	{
		final HashMap<Model, Properties> models = new HashMap<Model, Properties>();
		collectModels(PackageTest.suite(), models);
		for(final Model m : models.keySet())
			tearDown(m, models.get(m));
	}
}
