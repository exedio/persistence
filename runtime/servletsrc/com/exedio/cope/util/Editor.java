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

package com.exedio.cope.util;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
import com.exedio.cope.Feature;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.NoSuchIDException;
import com.exedio.cope.StringField;
import com.exedio.cope.pattern.History;
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
	 * If you want persistent http sessions,
	 * make implementions of this interface serializable.
	 */
	public interface Login
	{
		String getName();
	}
	
	protected abstract Login login(String user, String password);
	
	@SuppressWarnings("unused")
	protected String getBorderButtonURL(HttpServletRequest request, HttpServletResponse response, boolean bordersEnabled)
	{
		return null;
	}
	
	@SuppressWarnings("unused")
	protected String getCloseButtonURL(HttpServletRequest request, HttpServletResponse response)
	{
		return null;
	}
	
	@SuppressWarnings("unused")
	protected String getPreviousPositionButtonURL(HttpServletRequest request, HttpServletResponse response)
	{
		return null;
	}
	
	public final void doFilter(
			final ServletRequest servletRequest,
			final ServletResponse servletResponse,
			final FilterChain chain)
	throws IOException, ServletException
	{
		if(!(servletRequest instanceof HttpServletRequest))
		{
			chain.doFilter(servletRequest, servletResponse);
			return;
		}
		
		final HttpServletRequest request = (HttpServletRequest)servletRequest;
		
		if(LOGIN_URL_PATH_INFO.equals(request.getPathInfo()))
		{
			servletRequest.setCharacterEncoding(CopsServlet.ENCODING);
			final HttpServletResponse httpResponse = (HttpServletResponse)servletResponse;
			final HttpSession httpSession = request.getSession(true);
			final Object session = httpSession.getAttribute(SESSION);
			
			if(session==null)
				doLogin(request, httpSession, httpResponse);
			else
			{
				if(request.getParameter(PREVIEW_OVERVIEW)!=null)
					doPreviewOverview(request, httpResponse, (Session)session);
				else
					doBar(request, httpSession, httpResponse, (Session)session);
			}
			
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
					tls.set(new TL(this, request, (HttpServletResponse)servletResponse, (Session)session));
					chain.doFilter(request, servletResponse);
				}
				finally
				{
					tls.remove();
				}
			}
			else
				chain.doFilter(request, servletResponse);
		}
		else
		{
			chain.doFilter(request, servletResponse);
		}
	}
	
	private static final void redirectHome(
			final HttpServletRequest request,
			final HttpServletResponse response)
	throws IOException
	{
		response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + request.getServletPath() + '/'));
	}
	
	static final String AVOID_COLLISION = "contentEditorBar823658617";
	static final String REFERER = "referer";
	private static final String BORDERS_ON  = "bordersOn";
	private static final String BORDERS_OFF = "bordersOff";
	static final String CLOSE = "close";
	static final String SAVE_FEATURE = "feature";
	static final String SAVE_ITEM    = "item";
	static final String SAVE_TEXT    = "text";
	static final String SAVE_FILE    = "file";
	static final String SAVE_ITEM_FROM = "itemPrevious";
	static final String PREVIEW = "preview";
	
	private static final String CLOSE_IMAGE       = CLOSE       + ".x";
	private static final String BORDERS_ON_IMAGE  = BORDERS_ON  + ".x";
	private static final String BORDERS_OFF_IMAGE = BORDERS_OFF + ".x";
	
	@SuppressWarnings("deprecation")
	private static final boolean isMultipartContent(final HttpServletRequest request)
	{
		return ServletFileUpload.isMultipartContent(request);
	}
	
	static final String PREVIEW_OVERVIEW = "po";
	static final String PREVIEW_SAVE = "prevsave";
	
	static final class Proposal
	{
		final String oldValue;
		final String newValue;
		
		Proposal(final String oldValue, final String newValue)
		{
			this.oldValue = oldValue;
			this.newValue = newValue;
		}
	}
	
	private final void doPreviewOverview(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final Session session)
	throws IOException
	{
		if(Cop.isPost(request))
		{
			if(request.getParameter(PREVIEW_SAVE)!=null)
			{
				final Map<Session.Preview, String> previews = session.getPreviewsModifiable();
				try
				{
					model.startTransaction(getClass().getName() + "#saveProposals");
					for(final Iterator<Map.Entry<Session.Preview, String>> i = previews.entrySet().iterator(); i.hasNext(); )
					{
						final Map.Entry<Session.Preview, String> e = i.next();
						final Session.Preview p = e.getKey();
						p.save(model, e.getValue());
						i.remove();
					}
					// TODO maintain history
					model.commit();
				}
				finally
				{
					model.rollbackIfNotCommitted();
				}
			}
		}
		
		PrintStream out = null;
		try
		{
			final Map<Session.Preview, String> previews = session.getPreviews();
			final ArrayList<Proposal> proposals = new ArrayList<Proposal>();
			try
			{
				model.startTransaction(getClass().getName() + "#proposal");
				for(final Map.Entry<Session.Preview, String> e : previews.entrySet())
					proposals.add(new Proposal(e.getKey().getOldValue(model), e.getValue()));
				model.commit();
			}
			finally
			{
				model.rollbackIfNotCommitted();
			}
			
			response.setContentType("text/html; charset="+CopsServlet.ENCODING);
			response.addHeader("Cache-Control", "no-cache");
			response.addHeader("Cache-Control", "no-store");
			response.addHeader("Cache-Control", "max-age=0");
			response.addHeader("Cache-Control", "must-revalidate");
			response.setHeader("Pragma", "no-cache");
			response.setDateHeader("Expires", System.currentTimeMillis());
			out = new PrintStream(response.getOutputStream(), false, CopsServlet.ENCODING);
			Editor_Jspm.writePreviewOverview(out, response, proposals);
		}
		finally
		{
			if(out!=null)
				out.close();
		}
	}
	
	private final void doBar(
			final HttpServletRequest request,
			final HttpSession httpSession,
			final HttpServletResponse response,
			final Session session)
	throws IOException
	{
		if(!Cop.isPost(request))
		{
			redirectHome(request, response);
			return;
		}
		
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
			if(featureID==null)
				throw new NullPointerException();
			
			final Media feature = (Media)model.getFeature(featureID);
			if(feature==null)
				throw new NullPointerException(featureID);
			
			final String itemID = fields.get(SAVE_ITEM);
			if(itemID==null)
				throw new NullPointerException();
			
			final FileItem file = files.get(SAVE_FILE);
		
			try
			{
				model.startTransaction(getClass().getName() + "#saveFile(" + featureID + ',' + itemID + ')');
				
				final Item item = model.getItem(itemID);

				for(final History history : History.getHistories(item.getCopeType()))
				{
					final History.Event event = history.createEvent(item, session.getHistoryAuthor(), false);
					event.createFeature(
							feature, feature.getName(),
							feature.isNull(item) ? null : ("file type=" + feature.getContentType(item) + " size=" + feature.getLength(item)),
							"file name=" + file.getName() + " type=" + file.getContentType() + " size=" + file.getSize());
				}
				
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
			
			referer = fields.get(REFERER);
		}
		else // isMultipartContent
		{
			if(request.getParameter(BORDERS_ON)!=null || request.getParameter(BORDERS_ON_IMAGE)!=null)
			{
				session.borders = true;
			}
			else if(request.getParameter(BORDERS_OFF)!=null || request.getParameter(BORDERS_OFF_IMAGE)!=null)
			{
				session.borders = false;
			}
			else if(request.getParameter(CLOSE)!=null || request.getParameter(CLOSE_IMAGE)!=null)
			{
				httpSession.removeAttribute(SESSION);
			}
			else
			{
				final String featureID = request.getParameter(SAVE_FEATURE);
				if(featureID==null)
					throw new NullPointerException();
				
				final Feature featureO = model.getFeature(featureID);
				if(featureO==null)
					throw new NullPointerException(featureID);
				
				final String itemID = request.getParameter(SAVE_ITEM);
				if(itemID==null)
					throw new NullPointerException();
				
				if(featureO instanceof StringField)
				{
					final StringField feature = (StringField)featureO;
					final String value = request.getParameter(SAVE_TEXT);
				
					try
					{
						model.startTransaction(getClass().getName() + "#saveText(" + featureID + ',' + itemID + ')');
						
						final Item item = model.getItem(itemID);
	
						if(request.getParameter(PREVIEW)!=null)
						{
							session.setPreview(value, feature, item);
						}
						else
						{
							String v = value;
							if("".equals(v))
								v = null;
							for(final History history : History.getHistories(item.getCopeType()))
							{
								final History.Event event = history.createEvent(item, session.getHistoryAuthor(), false);
								event.createFeature(feature, feature.getName(), feature.get(item), v);
							}
							feature.set(item, v);
							session.notifySaved(feature, item);
						}
						
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
				else
				{
					final IntegerField feature = (IntegerField)featureO;
					final String itemIDFrom = request.getParameter(SAVE_ITEM_FROM);
					if(itemIDFrom==null)
						throw new NullPointerException();
					
					try
					{
						model.startTransaction(getClass().getName() + "#savePosition(" + featureID + ',' + itemIDFrom +  + ',' + itemID + ')');
						
						final Item itemFrom = model.getItem(itemIDFrom);
						final Item itemTo   = model.getItem(itemID);
	
						final Integer positionFrom = feature.get(itemFrom);
						final Integer positionTo   = feature.get(itemTo);
						feature.set(itemFrom, feature.getMinimum());
						feature.set(itemTo,   positionFrom);
						feature.set(itemFrom, positionTo);
						
						for(final History history : History.getHistories(itemFrom.getCopeType()))
						{
							final History.Event event = history.createEvent(itemFrom, session.getHistoryAuthor(), false);
							event.createFeature(feature, feature.getName(), positionFrom, positionTo);
						}
						for(final History history : History.getHistories(itemTo.getCopeType()))
						{
							final History.Event event = history.createEvent(itemTo, session.getHistoryAuthor(), false);
							event.createFeature(feature, feature.getName(), positionTo, positionFrom);
						}
						
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
			}
			
			referer = request.getParameter(REFERER);
		}
		
		if(referer!=null)
			response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + request.getServletPath() + referer));
	}
	
	static final String LOGIN_URL = "contentEditorLogin.html";
	private static final String LOGIN_URL_PATH_INFO = '/' + LOGIN_URL;
	static final String LOGIN_SUBMIT   = "login.submit";
	static final String LOGIN_USER     = "login.user";
	static final String LOGIN_PASSWORD = "login.password";
	
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
			if(Cop.isPost(request) && request.getParameter(LOGIN_SUBMIT)!=null)
			{
				final String user = request.getParameter(LOGIN_USER);
				final String password = request.getParameter(LOGIN_PASSWORD);
				try
				{
					model.startTransaction(getClass().getName() + "#login");
					final Login login = login(user, password);
					if(login!=null)
					{
						httpSession.setAttribute(SESSION, new Session(user, login, login.getName()));
						redirectHome(request, response);
					}
					else
					{
						out = new PrintStream(response.getOutputStream(), false, CopsServlet.ENCODING);
						Editor_Jspm.writeLogin(out, response, Editor.class.getPackage(), user);
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
				Editor_Jspm.writeLogin(out, response, Editor.class.getPackage(), null);
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
		
		private final String user;
		final Login login;
		final String loginName;
		boolean borders = false;
		private final HashMap<Preview, String> previews = new HashMap<Preview, String>();
		
		Session(final String user, final Login login, final String loginName)
		{
			this.user = user;
			this.login = login;
			this.loginName = loginName;
			assert user!=null;
			assert login!=null;
		}
		
		private static final class Preview implements Serializable // for session persistence
		{
			private static final long serialVersionUID = 1l;
			
			private final String feature;
			private final Item item;
			
			Preview(final StringField feature, final Item item)
			{
				this.feature = feature.getID(); // id is serializable
				this.item = item;
				
				assert feature!=null;
				assert item!=null;
			}
			
			String getOldValue(final Model model)
			{
				return ((StringField)model.getFeature(feature)).get(item);
			}
			
			void save(final Model model, final String value)
			{
				((StringField)model.getFeature(feature)).set(item, value);
			}
			
			@Override
			public int hashCode()
			{
				return feature.hashCode() ^ item.hashCode();
			}
			
			@Override
			public boolean equals(final Object other)
			{
				if(!(other instanceof Preview))
					return false;
				
				final Preview o = (Preview)other;
				return feature.equals(o.feature) && item.equals(o.item);
			}
		}
		
		int getPreviewNumber()
		{
			return previews.size();
		}
		
		String getPreview(final StringField feature, final Item item)
		{
			if(previews.isEmpty()) // shortcut
				return null;
			
			return previews.get(new Preview(feature, item));
		}
		
		Map<Preview, String> getPreviews()
		{
			return Collections.unmodifiableMap(previews);
		}
		
		HashMap<Preview, String> getPreviewsModifiable()
		{
			return previews;
		}
		
		void setPreview(final String content, final StringField feature, final Item item)
		{
			previews.put(new Preview(feature, item), content);
		}
		
		void notifySaved(final StringField feature, final Item item)
		{
			previews.remove(new Preview(feature, item));
		}
		
		String getHistoryAuthor()
		{
			return (loginName!=null ? loginName : user) + " (CCE)";
		}
		
		@Override
		public String toString()
		{
			final StringBuilder bf = new StringBuilder();
			
			// must not call login#getName() here,
			// because this may require a transaction,
			// which may not be present,
			// especially when this method is called by lamdba probe.
			if(loginName!=null)
				bf.append('"').append(loginName).append('"');
			else
				bf.append(user);
			
			if(borders)
				bf.append(" bordered");
			
			final int previewNumber = previews.size();
			if(previewNumber>0)
			{
				bf.append(" *");
				if(previewNumber>1)
					bf.append(previewNumber);
			}
			
			return bf.toString();
		}
	}
	
	private static final class TL
	{
		final Editor filter;
		final HttpServletRequest request;
		final HttpServletResponse response;
		final Session session;
		private HashMap<IntegerField, Item> positionItems = null;
		
		TL(
				final Editor filter,
				final HttpServletRequest request,
				final HttpServletResponse response,
				final Session session)
		{
			this.filter = filter;
			this.request = request;
			this.response = response;
			this.session = session;
			
			assert filter!=null;
			assert request!=null;
			assert response!=null;
			assert session!=null;
		}
		
		Item registerPositionItem(final IntegerField feature, final Item item)
		{
			final Integer next = feature.get(item);
			if(next==null)
				return null;
			
			if(positionItems==null)
				positionItems = new HashMap<IntegerField, Item>();
			
			final Item result = positionItems.put(feature, item);
			if(result==null)
				return null;
			
			final Integer previous = feature.get(result);
			return (previous!=null && previous.intValue()<next.intValue()) ? result : null;
		}
	}
	
	private static final ThreadLocal<TL> tls = new ThreadLocal<TL>();
	
	public static final boolean isLoggedIn()
	{
		return tls.get()!=null;
	}
	
	public static final Login getLogin()
	{
		final TL tl = tls.get();
		return tl!=null ? tl.session.login : null;
	}
	
	@SuppressWarnings("cast") // OK: for eclipse because of the javac bug
	private static final <K> Item getItem(final MapField<K, String> feature, final K key, final Item item)
	{
		return
				(Item)feature.getRelationType().searchSingletonStrict( // cast is needed because of a bug in javac
						feature.getKey().equal(key).and(
						Cope.equalAndCast(feature.getParent(item.getCopeType().getJavaClass()), item)));
	}
	
	public static final <K> String edit(final String content, final MapField<K, String> feature, final Item item, final K key)
	{
		final TL tl = tls.get();
		if(tl==null)
			return content;
		
		checkEdit(feature, item);
		
		return edit(
				tl,
				content,
				(StringField)feature.getValue(),
				getItem(feature, key, item));
	}
	
	public static final String edit(final String content, final StringField feature, final Item item)
	{
		final TL tl = tls.get();
		if(tl==null)
			return content;
		
		return edit(tl, content, feature, item);
	}
	
	static final String EDIT_METHOD_LINE = AVOID_COLLISION + "line";
	static final String EDIT_METHOD_FILE = AVOID_COLLISION + "file";
	static final String EDIT_METHOD_AREA = AVOID_COLLISION + "area";
	
	private static final String edit(final TL tl, final String content, final StringField feature, final Item item)
	{
		checkEdit(feature, item);
		if(feature.isFinal())
			throw new IllegalArgumentException("feature " + feature.getID() + " must not be final");
		
		if(!tl.session.borders)
		{
			final String preview = tl.session.getPreview(feature, item);
			return (preview!=null) ? preview : content;
		}
		
		final boolean block = feature.getMaximumLength()>StringField.DEFAULT_LENGTH;
		final String savedContent = feature.get(item);
		final String pageContent;
		final String editorContent;
		final boolean previewAllowed;
		if(content!=null ? content.equals(savedContent) : (savedContent==null))
		{
			previewAllowed = true;
			final String preview = tl.session.getPreview(feature, item);
			if(preview!=null)
				pageContent = editorContent = preview;
			else
				pageContent = editorContent = savedContent; // equals content anyway
		}
		else
		{
			previewAllowed = false;
			pageContent = content;
			editorContent = savedContent;
		}
		
		final String tag = block ? "div" : "span";
		final String editorContentEncoded = Cop.encodeXml(editorContent);
		final StringBuilder bf = new StringBuilder();
		bf.append('<').
			append(tag).
			append(
				" class=\"contentEditorLink\"" +
				" onclick=\"" +
					"return " + (block ? EDIT_METHOD_AREA : EDIT_METHOD_LINE) + "(this,'").
						append(feature.getID()).
						append("','").
						append(item.getCopeID()).
						append("','").
						append(block ? editorContentEncoded.replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r") : editorContentEncoded).		
					append("'," + previewAllowed + ");\"").
			append('>').
			append(pageContent).
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
		
		checkEdit(feature, item);
		if(feature.isFinal())
			throw new IllegalArgumentException("feature " + feature.getID() + " must not be final");
		
		final StringBuilder bf = new StringBuilder();
		bf.append(
				" class=\"contentEditorLink\"" +
				" onclick=\"" +
					"return " + EDIT_METHOD_FILE + "(this,'").
						append(feature.getID()).
						append("','").
						append(item.getCopeID()).
						append("','").
						append(Cop.encodeXml(feature.getURL(item))).		
					append("');\"");
		
		return bf.toString();
	}
	
	public static final String edit(final MediaFilter feature, final Item item)
	{
		final TL tl = tls.get();
		if(tl==null || !tl.session.borders)
			return "";
		
		checkEdit(feature, item);
		
		return edit(feature.getSource(), item);
	}
	
	public static final String edit(final IntegerField feature, final Item item)
	{
		final TL tl = tls.get();
		if(tl==null || !tl.session.borders)
			return "";
		
		checkEdit(feature, item);
		if(feature.isFinal())
			throw new IllegalArgumentException("feature " + feature.getID() + " must not be final");
		
		final Item previousItem = tl.registerPositionItem(feature, item);
		if(previousItem==null)
			return "";
		
		final HttpServletRequest request = tl.request;
		final String buttonURL = tl.filter.getPreviousPositionButtonURL(request, tl.response);
		return
			"<form action=\"" + action(request, tl.response) + "\" method=\"POST\" class=\"contentEditorPosition\">" +
				"<input type=\"hidden\" name=\"" + REFERER        + "\" value=\"" + referer(request)         + "\">" +
				"<input type=\"hidden\" name=\"" + SAVE_FEATURE   + "\" value=\"" + feature.getID()          + "\">" +
				"<input type=\"hidden\" name=\"" + SAVE_ITEM_FROM + "\" value=\"" + previousItem.getCopeID() + "\">" +
				"<input type=\"hidden\" name=\"" + SAVE_ITEM      + "\" value=\"" + item.getCopeID()         + "\">" +
				(
					buttonURL!=null
					? ("<input type=\"image\" src=\"" + buttonURL + "\" alt=\"Swap with previous item\">")
					: ("<input type=\"submit\" value=\"Up\">")
				) +
			"</form>";
	}
	
	private static final void checkEdit(final Feature feature, final Item item)
	{
		if(feature==null)
			throw new NullPointerException("feature must not be null");
		if(item==null)
			throw new NullPointerException("item must not be null");
		if(!feature.getType().isAssignableFrom(item.getCopeType()))
			throw new IllegalArgumentException("item " + item.getCopeID() + " does not belong to type of feature " + feature.getID());
	}
	
	interface Out
	{
		void print(String s);
		void print(int i);
	}
	
	public static final void writeBar(final PrintStream out)
	{
		final TL tl = tls.get();
		if(tl==null)
			return;
		
		writeBar(tl, new Out(){
			public void print(final String s) { out.print(s); }
			public void print(final int    i) { out.print(i); }
		});
	}
	
	public static final void writeBar(final StringBuilder out)
	{
		final TL tl = tls.get();
		if(tl==null)
			return;
		
		writeBar(tl, new Out(){
			public void print(final String s) { out.append(s); }
			public void print(final int    i) { out.append(i); }
		});
	}
	
	private static final void writeBar(final TL tl, final Out out)
	{
		final HttpServletRequest request = tl.request;
		Editor_Jspm.writeBar(out,
				action(request, tl.response),
				referer(request),
				tl.session.borders,
				tl.session.borders ? BORDERS_OFF : BORDERS_ON,
				tl.filter.getBorderButtonURL(request, tl.response, tl.session.borders),
				tl.filter.getCloseButtonURL(request, tl.response),
				tl.session.getPreviewNumber(),
				tl.session.loginName);
	}
	
	private static final String action(final HttpServletRequest request, final HttpServletResponse response)
	{
		return response.encodeURL(request.getContextPath() + request.getServletPath() + LOGIN_URL_PATH_INFO);
	}
	
	private static final String referer(final HttpServletRequest request)
	{
		final String queryString = request.getQueryString();
		return queryString!=null ? (request.getPathInfo() + '?' + request.getQueryString()) : request.getPathInfo();
	}
	
	// ------------------- deprecated stuff -------------------
	
	/**
	 * @deprecated Use {@link #isLoggedIn()} instead
	 */
	@Deprecated
	public static final boolean isActive()
	{
		return isLoggedIn();
	}
	
	/**
	 * @deprecated use {@link #edit(String, MapField, Item, Object)} instead.
	 */
	@Deprecated
	public static final <K> String editBlock(final String content, final MapField<K, String> feature, final Item item, final K key)
	{
		return edit(content, feature, item, key);
	}
}
