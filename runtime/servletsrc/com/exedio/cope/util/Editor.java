/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.util;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.exedio.cope.Cope;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.NoSuchIDException;
import com.exedio.cope.StringField;
import com.exedio.cope.pattern.MapField;
import com.exedio.cope.pattern.Media;
import com.exedio.cope.pattern.MediaFilter;
import com.exedio.cops.Cop;
import com.exedio.cops.CopsServlet;

public abstract class Editor implements Filter
{
	private final Model model;
	
	/**
	 * Subclasses must define a public no-args constructor
	 * providing the model.
	 */
	protected Editor(final Model model)
	{
		if(model==null)
			throw new NullPointerException("model was null in " + getClass().getName());
		
		this.model = model;
	}
	
	private ConnectToken connectToken = null;
	
	public final void init(final FilterConfig config)
	{
		connectToken = ServletUtil.connect(model, config, getClass().getName());
	}
	
	public final void destroy()
	{
		connectToken.returnIt();
		connectToken = null;
	}
	
	/**
	 * If you want persistent sessions,
	 * the make implementors of this interface serializable.
	 */
	public interface Login
	{
		String getName();
	}
	
	protected abstract Login login(String user, String password);
	
	public final void doFilter(
			final ServletRequest servletRequest,
			final ServletResponse response,
			final FilterChain chain) throws IOException, ServletException
	{
		if(!(servletRequest instanceof HttpServletRequest))
		{
			chain.doFilter(servletRequest, response);
			return;
		}
		
		final HttpServletRequest request = (HttpServletRequest)servletRequest;
		
		if(LOGIN_URL_PATH_INFO.equals(request.getPathInfo()))
		{
			servletRequest.setCharacterEncoding(CopsServlet.ENCODING);
			final HttpServletResponse httpResponse = (HttpServletResponse)response;
			final HttpSession httpSession = request.getSession(true);
			final Object session = httpSession.getAttribute(SESSION);
			
			if(session==null)
				doLogin(request, httpSession, httpResponse);
			else
				doBar(request, httpSession, httpResponse, (Session)session);
			
			return;
		}

		final HttpSession httpSession = request.getSession(false);
		if(httpSession!=null)
		{
			final Object session = httpSession.getAttribute(SESSION);
			if(session!=null)
			{
				try
				{
					tls.set(new TL(request, (HttpServletResponse)response, (Session)session));
					chain.doFilter(request, response);
				}
				finally
				{
					tls.remove();
				}
			}
			else
				chain.doFilter(request, response);
		}
		else
		{
			chain.doFilter(request, response);
		}
	}
	
	static final String AVOID_COLLISION = "contentEditorBar823658617";
	static final String REFERER = "referer";
	static final String TOGGLE_BORDERS = "borders";
	static final String LOGOUT = "logout";
	static final String SAVE_FEATURE = "feature";
	static final String SAVE_ITEM    = "item";
	static final String SAVE_TEXT    = "text";
	static final String SAVE_FILE    = "file";
	
	@SuppressWarnings("deprecation")
	private static final boolean isMultipartContent(final HttpServletRequest request)
	{
		return ServletFileUpload.isMultipartContent(request);
	}
	
	private final void doBar(
			final HttpServletRequest request,
			final HttpSession httpSession,
			final HttpServletResponse response,
			final Session session)
	throws IOException
	{
		if(Cop.isPost(request))
		{
			final String referer;
			
			if(isMultipartContent(request))
			{
				final HashMap<String, String> fields = new HashMap<String, String>();
				final HashMap<String, FileItem> files = new HashMap<String, FileItem>();
				final FileItemFactory factory = new DiskFileItemFactory();
				final ServletFileUpload upload = new ServletFileUpload(factory);
				upload.setHeaderEncoding(CopsServlet.ENCODING);
				try
				{
					for(Iterator<?> i = upload.parseRequest(request).iterator(); i.hasNext(); )
					{
						final FileItem item = (FileItem)i.next();
						if(item.isFormField())
							fields.put(item.getFieldName(), item.getString(CopsServlet.ENCODING));
						else
							files.put(item.getFieldName(), item);
					}
				}
				catch(FileUploadException e)
				{
					throw new RuntimeException(e);
				}
				
				final String featureID = fields.get(SAVE_FEATURE);
				if(featureID!=null)
				{
					final Media feature = (Media)model.findFeatureByID(featureID);
					if(feature==null)
						throw new NullPointerException(featureID);
					
					final String itemID    = fields.get(SAVE_ITEM);
					if(itemID==null)
						throw new NullPointerException(itemID);
					
					final FileItem file = files.get(SAVE_FILE);
				
					try
					{
						model.startTransaction(getClass().getName() + "#saveFile");
						
						final Item item = model.findByID(itemID);
	
						// TODO use more efficient setter with File or byte[]
						feature.set(item, file.getInputStream(), file.getContentType());
						
						model.commit();
					}
					catch(NoSuchIDException e)
					{
						throw new RuntimeException(e);
					}
					finally
					{
						model.rollbackIfNotCommitted();
					}
				}
				
				referer = fields.get(REFERER);
			}
			else // isMultipartContent
			{
				if(request.getParameter(TOGGLE_BORDERS)!=null)
				{
					session.borders = !session.borders;
				}
				else if(request.getParameter(LOGOUT)!=null)
				{
					httpSession.removeAttribute(SESSION);
				}
				
				final String featureID = request.getParameter(SAVE_FEATURE);
				if(featureID!=null)
				{
					final StringField feature = (StringField)model.findFeatureByID(featureID);
					if(feature==null)
						throw new NullPointerException(featureID);
					
					final String itemID    = request.getParameter(SAVE_ITEM);
					if(itemID==null)
						throw new NullPointerException(itemID);
					
					final String value = request.getParameter(SAVE_TEXT);
				
					try
					{
						model.startTransaction(getClass().getName() + "#saveText");
						
						final Item item = model.findByID(itemID);
	
						String v = value;
						if("".equals(v))
							v = null;
						feature.set(item, v);
						
						model.commit();
					}
					catch(NoSuchIDException e)
					{
						throw new RuntimeException(e);
					}
					finally
					{
						model.rollbackIfNotCommitted();
					}
				}
				
				referer = request.getParameter(REFERER);
			}
			
			if(referer!=null)
				response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + request.getServletPath() + referer));
		}
	}
	
	static final String LOGIN_URL = "contentEditorLogin.html";
	private static final String LOGIN_URL_PATH_INFO = '/' + LOGIN_URL;
	static final String LOGIN = "login";
	static final String LOGIN_USER = "user";
	static final String LOGIN_PASSWORD = "password";
	
	private final void doLogin(
			final HttpServletRequest request,
			final HttpSession httpSession,
			final HttpServletResponse response)
	throws IOException
	{
		assert httpSession!=null;
		PrintStream out = null;
		try
		{
			response.setContentType("text/html; charset="+CopsServlet.ENCODING);
			if(Cop.isPost(request) && request.getParameter(LOGIN)!=null)
			{
				final String user = request.getParameter(LOGIN_USER);
				final String password = request.getParameter(LOGIN_PASSWORD);
				try
				{
					model.startTransaction(getClass().getName() + "#login");
					final Login login = login(user, password);
					if(login!=null)
					{
						final String name = login.getName();
						httpSession.setAttribute(SESSION, new Session(login, name));
						response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + request.getServletPath() + '/'));
					}
					else
					{
						out = new PrintStream(response.getOutputStream(), false, CopsServlet.ENCODING);
						Editor_Jspm.writeLogin(out, response, user);
					}
					model.commit();
				}
				finally
				{
					model.rollbackIfNotCommitted();
				}
			}
			else
			{
				out = new PrintStream(response.getOutputStream(), false, CopsServlet.ENCODING);
				Editor_Jspm.writeLogin(out, response, null);
			}
		}
		finally
		{
			if(out!=null)
				out.close();
		}
	}
	
	private static final String SESSION = Session.class.getCanonicalName();
	
	static final class Session implements Serializable // for session persistence
	{
		private static final long serialVersionUID = 1l;
		
		final Login login;
		final String loginName;
		boolean borders = false;
		
		Session(final Login login, final String loginName)
		{
			this.login = login;
			this.loginName = loginName;
			assert login!=null;
		}
		
		@Override
		public String toString()
		{
			// must not call login#getName() here,
			// because this may require a transaction,
			// which may not be present,
			// especially when this method is called by lamdba probe.
			return
				(loginName!=null ? ('"' + loginName + '"') : login.getClass().getName()) +
				" borders=" + (borders ? "on" : "off");
		}
	}
	
	private static final class TL
	{
		final HttpServletRequest request;
		final HttpServletResponse response;
		final Session session;
		
		TL(final HttpServletRequest request, final HttpServletResponse response, final Session session)
		{
			this.request = request;
			this.response = response;
			this.session = session;
			
			assert request!=null;
			assert response!=null;
			assert session!=null;
		}
	}
	
	private static final ThreadLocal<TL> tls = new ThreadLocal<TL>();
	
	public static final boolean isActive()
	{
		return tls.get()!=null;
	}
	
	@SuppressWarnings("cast") // OK: for eclipse because of the javac bug
	private static final <K> Item getItem(final MapField<K, String> map, final K key, final Item item)
	{
		return
				(Item)map.getRelationType().searchSingletonStrict( // cast is needed because of a bug in javac
						map.getKey().equal(key).and(
						Cope.equalAndCast(map.getParent(item.getCopeType().getJavaClass()), item)));
	}
	
	public static final <K> String edit(final String content, final MapField<K, String> feature, final Item item, final K key)
	{
		final TL tl = tls.get();
		if(tl==null || !tl.session.borders)
			return content;
		
		return edit(
				tl, false,
				content,
				(StringField)feature.getValue(),
				getItem(feature, key, item));
	}
	
	public static final <K> String editBlock(final String content, final MapField<K, String> feature, final Item item, final K key)
	{
		final TL tl = tls.get();
		if(tl==null || !tl.session.borders)
			return content;
		
		return edit(
				tl, true,
				content,
				(StringField)feature.getValue(),
				getItem(feature, key, item));
	}
	
	public static final String edit(final String content, final StringField feature, final Item item)
	{
		final TL tl = tls.get();
		if(tl==null || !tl.session.borders)
			return content;
		
		return edit(tl, false, content, feature, item);
	}
	
	static final String EDIT_METHOD = AVOID_COLLISION + "edit";
	
	private static final String edit(final TL tl, final boolean block, final String content, final StringField feature, final Item item)
	{
		assert tl.session.borders;
		assert feature!=null;
		assert item!=null;
		assert !feature.isFinal();
		assert feature.getType().isAssignableFrom(item.getCopeType()) : item.getCopeID()+'-'+feature.getID();
		
		final String tag = block ? "div" : "span";
		final StringBuilder bf = new StringBuilder();
		bf.append('<').
			append(tag).
			append(
				" class=\"contentEditorLink\"" +
				" onclick=\"" +
					EDIT_METHOD + "(this,'").
						append(feature.getID()).
						append("','").
						append(item.getCopeID()).
						append("','").
						append(block ? Cop.encodeXml(feature.get(item)).replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r") : Cop.encodeXml(feature.get(item))).		
					append("');return false;\"").
			append('>').
			append(content).
			append("</").
			append(tag).
			append('>');
		
		return bf.toString();
	}
	
	public static final String edit(final Media feature, final Item item)
	{
		final TL tl = tls.get();
		if(tl==null || !tl.session.borders)
			return "";
		
		assert feature!=null;
		assert item!=null;
		assert !feature.isFinal();
		assert feature.getType().isAssignableFrom(item.getCopeType()) : item.getCopeID()+'-'+feature.getID();
		
		final StringBuilder bf = new StringBuilder();
		bf.append(
				" class=\"contentEditorLink\"" +
				" onclick=\"" +
					EDIT_METHOD + "(this,'").
						append(feature.getID()).
						append("','").
						append(item.getCopeID()).
						append("','").
						append(Cop.encodeXml(feature.getURL(item))).		
					append("');return false;\"");
		
		return bf.toString();
	}
	
	public static final String edit(final MediaFilter feature, final Item item)
	{
		final TL tl = tls.get();
		if(tl==null || !tl.session.borders)
			return "";
		
		return edit(feature.getSource(), item);
	}
	
	public static final void writeBar(final PrintStream out)
	{
		final TL tl = tls.get();
		if(tl==null)
			return;
		
		final HttpServletRequest request = tl.request;
		final String queryString = request.getQueryString();
		Editor_Jspm.writeBar(out,
				tl.response.encodeURL(request.getContextPath() + request.getServletPath() + LOGIN_URL_PATH_INFO),
				queryString!=null ? (request.getPathInfo() + '?' + request.getQueryString()) : request.getPathInfo(),
				tl.session.borders,
				tl.session.login.getName());
	}
}
