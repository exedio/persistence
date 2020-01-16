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

package com.exedio.cope.web;

import static com.exedio.cope.misc.ConnectToken.removePropertiesVoid;
import static com.exedio.cope.misc.ConnectToken.setProperties;
import static com.exedio.cope.misc.ServletUtil.getConnectProperties;

import com.exedio.cope.CacheIsolationTest;
import com.exedio.cope.ConnectProperties;
import com.exedio.cope.CopySimpleModelTest;
import com.exedio.cope.DataModelTest;
import com.exedio.cope.DayFieldTest;
import com.exedio.cope.DefaultToModelTest;
import com.exedio.cope.DeleteTest;
import com.exedio.cope.HiddenFeatureTest;
import com.exedio.cope.HierarchyTest;
import com.exedio.cope.InstanceOfModelTest;
import com.exedio.cope.IntegerModelTest;
import com.exedio.cope.MatchModel;
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
	private static Model[] models()
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
				MatchModel.MODEL,
				HierarchyTest.MODEL,
				InstanceOfModelTest.MODEL,
				HiddenFeatureTest.MODEL,
				DispatcherModelTest.MODEL,
				CopySimpleModelTest.MODEL,
				DefaultToModelTest.MODEL,
				Stuff.samplerModel,
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
			removePropertiesVoid(model);
	}
}
