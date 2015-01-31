/*
 * Copyright (C) 2000  Ralf Wiebicke
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

final class Tags
{
	static boolean has(final String doccomment, final String tagname)
	{
		if(doccomment==null)
			return false;

		final String s = '@' + tagname;
		final int pos = doccomment.indexOf(s);
		if(pos<0)
			return false;
		if(pos+s.length()==doccomment.length())
			return true;
		return Character.isWhitespace(doccomment.charAt(pos+s.length()));
	}

	/**
	 * @param tagname the tag name without the '@' prefix
	 * @return the first line following the tag
	 */
	static String getLine(final String doccomment, final String tagname)
	{
		if(doccomment==null)
			return null;

		final String s = '@' + tagname + ' ';
		int start = doccomment.indexOf(s);
		if (start < 0)
			return null;
		start += s.length();

		int end;
		li : for (end = start; end < doccomment.length(); end++)
		{
			switch (doccomment.charAt(end))
			{
				case '\n' :
				case '\r' :
				case '*' :
					break li;
			}
		}
		final String result = doccomment.substring(start, end).trim();
		//System.out.println("doctag:>"+tagname+"< >"+docComment.substring(start, end)+"<");
		return result;
	}

	private Tags()
	{
		// prevent instantiation
	}
}
