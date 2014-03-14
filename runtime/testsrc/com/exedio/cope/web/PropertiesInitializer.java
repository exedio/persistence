/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package com.exedio.cope.web;

import static com.exedio.cope.misc.ConnectToken.removeProperties;
import static com.exedio.cope.misc.ConnectToken.setProperties;
import static com.exedio.cope.misc.ServletUtil.getConnectProperties;

import com.exedio.cope.CacheIsolationTest;
import com.exedio.cope.ConnectProperties;
import com.exedio.cope.CopyModelTest;
import com.exedio.cope.DataModelTest;
import com.exedio.cope.DayFieldTest;
import com.exedio.cope.DefaultToModelTest;
import com.exedio.cope.DeleteTest;
import com.exedio.cope.HiddenFeatureTest;
import com.exedio.cope.HierarchyTest;
import com.exedio.cope.InstanceOfModelTest;
import com.exedio.cope.IntegerModelTest;
import com.exedio.cope.MatchTest;
import com.exedio.cope.Model;
import com.exedio.cope.NameTest;
import com.exedio.cope.badquery.BadQueryTest;
import com.exedio.cope.pattern.DispatcherModelTest;
import com.exedio.cope.pattern.HashTest;
import com.exedio.cope.pattern.LimitedListFieldModelTest;
import com.exedio.cope.pattern.ListFieldTest;
import com.exedio.cope.pattern.MD5Test;
import com.exedio.cope.sampler.Stuff;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Initializes connect properties for all models in runtime/web.xml.
 */
public final class PropertiesInitializer implements ServletContextListener
{
	private static final Model[] models()
	{
		return new Model[]{
				IntegerModelTest.MODEL,
				DeleteTest.MODEL,
				DayFieldTest.MODEL,
				DataModelTest.MODEL,
				HashTest.MODEL,
				MD5Test.MODEL,
				ListFieldTest.MODEL,
				LimitedListFieldModelTest.MODEL,
				CacheIsolationTest.MODEL,
				NameTest.MODEL,
				MatchTest.MODEL,
				HierarchyTest.MODEL,
				InstanceOfModelTest.MODEL,
				HiddenFeatureTest.MODEL,
				DispatcherModelTest.MODEL,
				CopyModelTest.MODEL,
				DefaultToModelTest.MODEL,
				Stuff.samplerModel,
				com.exedio.cope.mxsampler.Stuff.samplerModel,
				BadQueryTest.MODEL,
		};
	}

	@Override
	public void contextInitialized(final ServletContextEvent sce)
	{
		final ConnectProperties properties = getConnectProperties(sce.getServletContext());
		for(final Model model : models())
			setProperties(model, properties);
	}

	@Override
	public void contextDestroyed(final ServletContextEvent sce)
	{
		for(final Model model : models())
			removeProperties(model);
	}
}
