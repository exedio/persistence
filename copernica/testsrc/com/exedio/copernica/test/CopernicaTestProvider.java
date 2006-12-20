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
package com.exedio.copernica.test;

import java.util.Arrays;

import com.exedio.cope.Field;
import com.exedio.cope.Model;
import com.exedio.cope.testmodel.AttributeItem;
import com.exedio.cope.testmodel.Main;
import com.exedio.copernica.TransientCopernicaProvider;
import com.exedio.copernica.TransientLanguage;
import com.exedio.copernica.TransientSection;
import com.exedio.copernica.TransientUser;

public class CopernicaTestProvider extends TransientCopernicaProvider
{
	
	private final Model model;
	
	public CopernicaTestProvider()
	{
		this.model = Main.model;
		final TransientLanguage de = new TransientLanguage("de", "leer", "ein", "aus");
		final TransientLanguage en = new TransientLanguage("en", "empty", "on", "off");

		de.putName(de, "Deutsch");
		de.putName(en, "German");
		en.putName(de, "Englisch");
		en.putName(en, "English");

		setTransientLanguages(
			new TransientLanguage[]{
				de,
				en,
			}
		);
		
		final TransientUser admin = new TransientUser("admin", "nimda", "Sir Administrator");
		final TransientUser user = new TransientUser("user", "resu", "Mister User");
		setTransientUsers(
			new TransientUser[]{
				admin,
				user,
			}
		);
		
		setSections(AttributeItem.TYPE,
			Arrays.asList(new Field[]{AttributeItem.someString, AttributeItem.someNotNullString}),
			Arrays.asList(new TransientSection[]
			{
				new TransientSection("numbers", new Field[] {
						AttributeItem.someInteger,
						AttributeItem.someLong,
						AttributeItem.someDouble,
						AttributeItem.someNotNullInteger,
						AttributeItem.someNotNullLong,
						AttributeItem.someNotNullDouble,
						}),
				new TransientSection("data", new Field[]{
						AttributeItem.someEnum,
						AttributeItem.someNotNullEnum,
						AttributeItem.someData.getBody(), // TODO should specify someData here
						}),
				new TransientSection("other", new Field[]{
						AttributeItem.someDate,
						AttributeItem.someBoolean,
						AttributeItem.someNotNullBoolean,
						AttributeItem.someItem,
						AttributeItem.someNotNullItem,
						}),
			})
		);
	}

	public Model getModel()
	{
		return model;
	}

	public boolean requiresAuthorization()
	{
		return true;
	}
}
