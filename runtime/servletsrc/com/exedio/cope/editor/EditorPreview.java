/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.editor;

import java.text.DateFormat;
import java.util.Locale;

import com.exedio.cope.DateField;
import com.exedio.cope.Item;
import com.exedio.cope.StringField;

public final class EditorPreview extends Item
{
	static final StringField user = new StringField().toFinal();
	static final StringField name = new StringField().toFinal().optional();
	static final DateField date = new DateField().toFinal().defaultToNow();
	
	String getDate()
	{
		return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault()).format(date.get(this));
	}
	
	String getAuthor()
	{
		final String name = EditorPreview.name.get(this);
		return name!=null ? name : EditorPreview.user.get(this);
	}
	
	EditorPreview(
				java.lang.String user,
				java.lang.String name)
	{
		this(new com.exedio.cope.SetValue[]{
			EditorPreview.user.map(user),
			EditorPreview.name.map(name)});
	}
	
	private EditorPreview(com.exedio.cope.SetValue... setValues)
	{
		super(setValues);
	}
	
	@SuppressWarnings("unused") private EditorPreview(com.exedio.cope.util.ReactivationConstructorDummy d,int pk)
	{
		super(d,pk);
	}
	
	private static final long serialVersionUID = 1l;

	public static final com.exedio.cope.Type<EditorPreview> TYPE = newType(EditorPreview.class);
}
