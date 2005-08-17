/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.pattern;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.Feature;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.NoSuchIDException;
import com.exedio.cope.Transaction;
import com.exedio.cope.Type;
import com.exedio.cope.util.ServletUtil;


/**
 * A servlet providing access to the data of cope data attributes.
 * 
 * In order to use it, you have to deploy the servlet in your <code>web.xml</code>,
 * providing the name of the cope model via an init-parameter.
 * Typically, your <code>web.xml</code> would contain a snippet like this:  
 *  
 * <pre>
 * &lt;servlet&gt;
 *    &lt;servlet-name&gt;data&lt;/servlet-name&gt;
 *    &lt;servlet-class&gt;com.exedio.cope.pattern.DataServlet&lt;/servlet-class&gt;
 *    &lt;init-param&gt;
 *       &lt;param-name&gt;model&lt;/param-name&gt;
 *       &lt;param-value&gt;{@link com.exedio.cope.Model com.bigbusiness.shop.Main#model}&lt;/param-value&gt;
 *    &lt;/init-param&gt;
 * &lt;/servlet&gt;
 * &lt;servlet-mapping&gt;
 *    &lt;servlet-name&gt;data&lt;/servlet-name&gt;
 *    &lt;url-pattern&gt;/data/*&lt;/url-pattern&gt;
 * &lt;/servlet-mapping&gt;
 * </pre>
 * 
 * @author Ralf Wiebicke
 */
public class DataServlet extends HttpServlet
{
	Model model = null;
	final HashMap pathes = new HashMap();
	
	public final void init()
	{
		if(model!=null)
		{
			System.out.println("reinvokation of jspInit");
			return;
		}
		
		try
		{
			model = ServletUtil.getModel(getServletConfig());
			for(Iterator i = model.getTypes().iterator(); i.hasNext(); )
			{
				final Type type = (Type)i.next();
				for(Iterator j = type.getDeclaredFeatures().iterator(); j.hasNext(); )
				{
					final Feature feature = (Feature)j.next();
					if(feature instanceof HttpEntity)
					{
						final String path = '/'+type.getID()+'/'+feature.getName();
						pathes.put(path, new Path(path, (HttpEntity)feature));
					}
				}
			}
		}
		catch(RuntimeException e)
		{
			e.printStackTrace();
			throw e;
		}
	}

	protected final void doGet(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws ServletException, IOException
	{
		if("/statistics".equals(request.getPathInfo()))
		{
			serveDirectory(request, response);
			return;
		}

		if(serveContent(request, response))
			return;
		
		serveError(request, response);
	}
		
	protected final void serveDirectory(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws ServletException, IOException
	{
		response.setContentType("text/html");
		
		final String prefix = request.getContextPath()+request.getServletPath();
		
		final OutputStream out = response.getOutputStream();
		
		final PrintStream p = new PrintStream(out);
		p.println("<html>");
		p.println("<head><title>cope data servlet</title><head>");
		p.println("<body>");
		p.println("<ol>");
		
		for(Iterator i = pathes.values().iterator(); i.hasNext(); )
			((Path)i.next()).printStatistics(prefix, p);
		
		p.println("</ol>");
		p.println("</body>");
		p.println("</html>");
		
		out.close();
	}
	
	protected final void serveError(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws ServletException, IOException
	{
		response.setStatus(response.SC_NOT_FOUND);
		response.setContentType("text/html");
		
		final PrintStream out = new PrintStream(response.getOutputStream());
		
		out.print("<html>\n" +
				"<head><title>Not Found</title><head>\n" +
				"<body>\n" +
				"<h1>Not Found</h1>\n" +
				"The requested URL was not found on this server.\n" +
				"</body>\n" +
				"</html>\n");
		
		out.close();
	}
	
	/**
	 * Sets the offset, the Expires http header is set into the future.
	 * Together with a http reverse proxy this ensures,
	 * that for that time no request for that data will reach the servlet.
	 * This may reduce the load on the server.
	 * 
	 * TODO: make this configurable, at best per DataAttribute.
	 */
	private static final long EXPIRES_OFFSET = 1000 * 5; // 5 seconds
	
	private static final String REQUEST_IF_MODIFIED_SINCE = "If-Modified-Since";
	private static final String RESPONSE_EXPIRES = "Expires";
	private static final String RESPONSE_LAST_MODIFIED = "Last-Modified";
	private static final String RESPONSE_CONTENT_LENGTH = "Content-Length";
	
	protected final boolean serveContent(
			final HttpServletRequest request,
			final HttpServletResponse response)
		throws ServletException, IOException
	{
		final String pathInfo = request.getPathInfo();
		//System.out.println("pathInfo="+pathInfo);
		if(pathInfo==null)
			return false;

		final int trailingSlash = pathInfo.lastIndexOf('/');
		if(trailingSlash<=0 && // null is leading slash, which is not allowed
			trailingSlash>=pathInfo.length()-1)
			return false;

		final String attributeString = pathInfo.substring(0, trailingSlash);
		//System.out.println("attributeString="+attributeString);

		final Path path = (Path)pathes.get(attributeString);
		if(path==null)
			return false;
		else
			return path.serveContent(request, response, pathInfo, trailingSlash);
	}
	
	private final class Path
	{
		final String path;
		final HttpEntity entity;
		
		Path(final String path, final HttpEntity entity)
		{
			this.path = path;
			this.entity = entity;
		}

		boolean serveContent(
				final HttpServletRequest request, final HttpServletResponse response,
				final String pathInfo, final int trailingSlash)
			throws ServletException, IOException
		{
			//System.out.println("entity="+entity);

			final int dotAfterSlash = pathInfo.indexOf('.', trailingSlash);
			//System.out.println("trailingDot="+trailingDot);

			final String pkString =
				(dotAfterSlash>=0)
				? pathInfo.substring(trailingSlash+1, dotAfterSlash)
				: pathInfo.substring(trailingSlash+1);
			//System.out.println("pkString="+pkString);

			final String id = entity.getType().getID() + '.' + pkString;
			//System.out.println("ID="+id);
			try
			{
				model.startTransaction("DataServlet");
				final Item item = model.findByID(id);
				//System.out.println("item="+item);

				final String mimeMajor = entity.getMimeMajor(item);
				//System.out.println("mimeMajor="+mimeMajor);
				if(mimeMajor!=null)
				{
					final String mimeMinor = entity.getMimeMinor(item);
					//System.out.println("mimeMinor="+mimeMinor);
					response.setContentType(mimeMajor+'/'+mimeMinor);

					final long lastModified = entity.getDataLastModified(item);
					//System.out.println("lastModified="+formatHttpDate(lastModified));
					response.setDateHeader(RESPONSE_LAST_MODIFIED, lastModified);

					final long now = System.currentTimeMillis();
					response.setDateHeader(RESPONSE_EXPIRES, now+EXPIRES_OFFSET);
					
					final long ifModifiedSince = request.getDateHeader(REQUEST_IF_MODIFIED_SINCE);
					//System.out.println("ifModifiedSince="+request.getHeader(REQUEST_IF_MODIFIED_SINCE));
					//System.out.println("ifModifiedSince="+ifModifiedSince);
					
					if(ifModifiedSince>=0 && ifModifiedSince>=lastModified)
					{
						//System.out.println("not modified");
						response.setStatus(response.SC_NOT_MODIFIED);
						
						System.out.println(request.getMethod()+' '+request.getProtocol()+" IMS="+format(ifModifiedSince)+"  LM="+format(lastModified)+"  NOT modified");
					}
					else
					{
						final long contentLength = entity.getDataLength(item);
						//System.out.println("contentLength="+String.valueOf(contentLength));
						response.setHeader(RESPONSE_CONTENT_LENGTH, String.valueOf(contentLength));
						//response.setHeader("Cache-Control", "public");
		
						System.out.println(request.getMethod()+' '+request.getProtocol()+" IMS="+format(ifModifiedSince)+"  LM="+format(lastModified)+"  modified: "+contentLength);

						ServletOutputStream out = null;
						InputStream in = null;
						try
						{
							out = response.getOutputStream();
							in = entity.getData(item);
		
							final byte[] buffer = new byte[Math.max((int)contentLength, 50*1024)];
							for(int len = in.read(buffer); len != -1; len = in.read(buffer))
								out.write(buffer, 0, len);
						}
						finally
						{
							if(in!=null)
								in.close();
							if(out!=null)
								out.close();
						}
					}
					Transaction.commit();
					return true;
				}
				else
				{
					Transaction.commit();
					return false;
				}
			}
			catch(NoSuchIDException e)
			{
				return false;
			}
			finally
			{
				Transaction.rollbackIfNotCommitted();
			}
		}

		protected final void printStatistics(final String prefix, final PrintStream p)
		{
			p.println("<li><a href=\""+prefix+path+"/0\">"+entity+"</a>");
		}
		
	}
	
	final String format(final long date)
	{
		final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		return df.format(new Date(date));
	}
	
}
