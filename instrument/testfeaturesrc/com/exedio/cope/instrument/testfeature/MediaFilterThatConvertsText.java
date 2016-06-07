package com.exedio.cope.instrument.testfeature;

import com.exedio.cope.Item;
import com.exedio.cope.pattern.Media;
import com.exedio.cope.pattern.MediaFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MediaFilterThatConvertsText extends MediaFilter
{
	private static final long serialVersionUID=1L;

	private static final String TEXT_PLAIN="text/plain";

	public MediaFilterThatConvertsText(Media source)
	{
		super(source);
	}

	@Override
	public Set<String> getSupportedSourceContentTypes()
	{
		return Collections.singleton(TEXT_PLAIN);
	}

	@Override
	public String getContentType(Item item)
	{
		return TEXT_PLAIN.equals(getSource().getContentType(item))?"image/jpeg":null;
	}

	@Override
	public void doGetAndCommit(HttpServletRequest request, HttpServletResponse response, Item item)
		throws IOException, NotFound
	{
		throw new RuntimeException("not implemented");
	}
}
