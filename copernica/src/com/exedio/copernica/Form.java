package com.exedio.copernica;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;

import com.exedio.cope.lib.NestingRuntimeException;

abstract class Form
{
	private final HttpServletRequest request;
	private final HashMap multipartContentParameters;

	private final HashMap fieldMap = new HashMap();
	private final ArrayList fieldList = new ArrayList();
	private final ArrayList hiddenFieldList = new ArrayList();
	
	private final ArrayList sectionList = new ArrayList();
	
	Form(final HttpServletRequest request)
	{
		this.request = request;

		if(FileUpload.isMultipartContent(request))
		{
			final DiskFileUpload upload = new DiskFileUpload();
			final int maxSize = 100*1024; // TODO: make this configurable
			upload.setSizeThreshold(maxSize); // TODO: always save to disk
			upload.setSizeMax(maxSize);
			//upload.setRepositoryPath("");
			multipartContentParameters = new HashMap();
			try
			{
				for(Iterator i = upload.parseRequest(request).iterator(); i.hasNext(); )
				{
					final FileItem item = (FileItem)i.next();
					if (item.isFormField())
					{
						final String name = item.getFieldName();
						final String value = item.getString();
						multipartContentParameters.put(name, value);
					}
					else
					{
						final String name = item.getFieldName();
						multipartContentParameters.put(name, item);
					}
				}
			}
			catch(FileUploadException e)
			{
				throw new NestingRuntimeException(e);
			}
		}
		else
		{
			multipartContentParameters = null;
		}
	}
	
	protected final String getParameter(final String name)
	{
		if(multipartContentParameters!=null)
		{
			return (String)multipartContentParameters.get(name);
		}
		else
			return request.getParameter(name);
	}
	
	protected final FileItem getParameterFile(final String name)
	{
		if(multipartContentParameters!=null)
		{
			return (FileItem)multipartContentParameters.get(name);
		}
		else
			return null;
	}
	
	class Field
	{
		public final Object key;
		public final String name;
		public final String value;
		public String error;
		
		Field(final Object key, final String name, final String value, final boolean hidden)
		{
			this.key = key;
			this.name = name;
			this.value = value;
			fieldMap.put(key, this);
			(hidden?hiddenFieldList:fieldList).add(this);
		}
		
		Field(final Object key, final String value, final boolean hidden)
		{
			this.key = key;
			this.name = null;
			this.value = value;
			fieldMap.put(key, this);
			(hidden?hiddenFieldList:fieldList).add(this);
		}
		
		final boolean isReadOnly()
		{
			return name==null;
		}
		
		final String getName()
		{
			if(name==null)
				throw new RuntimeException();
			return name;
		}
		
		final String getValue()
		{
			return value;
		}
		
		final String getError()
		{
			return error;
		}
	}
	
	final List getFields()
	{
		return Collections.unmodifiableList(fieldList);
	}
	
	final List getHiddenFields()
	{
		return Collections.unmodifiableList(hiddenFieldList);
	}
	
	final class Section
	{
		final String id;
		final String name;
		
		Section(final String id, final String name)
		{
			this.id = id;
			this.name = name;
			sectionList.add(this);
		}
	}
	
	final List getSections()
	{
		return Collections.unmodifiableList(sectionList);
	}
	
}
