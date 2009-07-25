/*
 * Copyright (C) 1998, 2009  John Pritchard and the Alto Project Group.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */
package alto.io;

/**
 * <p> URI syntax has been covered by RFCs 1630, 1737, 1738, 1808,
 * 2396 and others. </p>
 * 
 * <h3>Description</h3>
 * 
 * <p> A relative URI has a subset of the parts of an absolute URI.
 * It has neither scheme nor host parts. </p>
 * 
 * <p> An absolute URI has a scheme, optionally followed by one or
 * more of the principal parts including host, path, intern, query,
 * fragment or terminal. 
 * 
 * <dl>
 * 
 * <dt><code>scheme<b>:</b></code></dt>
 * <dd> One or more schemes are recognized as subcomponents of the
 * scheme class of component. </dd>
 * 
 * <dt><code><b>//</b>host<b>/</b></code></dt>
 * <dd> The host class of component may have username, password,
 * hostname and port number subcomponents. </dd>
 * 
 * <dt><code>path</code></dt>
 * <dd> The path class of component is recognized after a scheme
 * without a host, after a host, or as the first component of a
 * relative URI without a scheme. </dd>
 * 
 * <dt><code><b>!</b>intern</code></dt>
 * <dd> The intern class component is a kind of path expression that
 * is typically used immediately after a path expression as in the
 * following example.
 * <pre>
 * scheme:path/filename.zip!inside/zip/file.name
 * </pre>
 * 
 * <dt><code><b>?</b>query</code></dt> <dd> The query class component
 * is typically used as a series of name- value pairs, each of which
 * are parsed as subcomponents.  The query parser maintains the
 * equivalence of '?' and '&amp;' in the usual query string syntax.
 * See also {@link uri$Query}.  </dd>
 * 
 * <dt><code><b>#</b>fragment</code></dt>
 * <dd> The fragment component is a kind of path expression, its
 * subcomponents are parsed as subcomponents of a path expresion. </dd>
 * 
 * <dt><code><b>;</b>terminal</code></dt>
 * <dd> The terminal component is a subclass of query and supports the
 * same syntax for the parsing of its subcomponents. </dd>
 * 
 * </dl>
 * 
 * 
 * <h4>Examples</h4>
 * 
 * <pre>
 * path
 * a:b:c:path
 * a:b:c:path!intern?query
 * scheme://usrn:pass@hostn:pno/path!intern?query#frag;terminal
 * http://www.syntelos.com/src/alto/syx/System.java
 * file:src/alto/syx/System.java
 * </pre>
 * 
 * <h4>Sublanguages</h4>
 * 
 * <h5>Path and Query</h5>
 * 
 * <p> The query and terminal share the same subsyntax, as implemented
 * in query.  The path, intern and fragment share the same subsyntax,
 * as implemented in path. </p>
 * 
 * <h5>URL Code</h5>
 * 
 * <p> The final stage of the parsing process performs {@link
 * Url#decode(String) URL Decoding} on each of the component
 * and subcomponent parts.  This decoding approach has been designed
 * to permit URI structures and other conflicting syntaces (like JVM
 * type signatures) to be URL encoded within URI structures and
 * exposed by the user API for subsequent parsing. </p>
 * 
 * <p> The original input source URI as available from {@link
 * #toString()}.  Likewise the {@link #hashCode()} and {@link
 * #equals(java.lang.Object)} hash key behavior of this class is based
 * on the input source or external representation. </p>
 * 
 * 
 * 
 * @author jdp
 * @since 1.5
 */
public interface Uri {

    public java.lang.String toString();
    public boolean isRelative();
    public boolean isAbsolute();
    public boolean hasScheme();
    public int countScheme();
    public java.lang.String getScheme();
    public java.lang.String getScheme(int idx);
    public java.lang.String getSchemeTail();
    public java.lang.String getSchemeHead();
    public java.lang.String getHostUser();
    public java.lang.String getHostPass();
    public java.lang.String getHostName();
    public java.lang.String getHostPort();
    public boolean hasPath();
    public int countPath();
    public java.lang.String getPath();
    public java.lang.String getPath(int idx);
    public java.lang.String getPathTail();
    public java.lang.String getPathHead();
    public java.lang.String getPathParent();
    public java.lang.String getIntern();
    public boolean hasIntern();
    public int countIntern();
    public java.lang.String getIntern(int idx);
    public java.lang.String getInternTail();
    public java.lang.String getInternHead();
    public boolean hasQuery();
    public boolean hasQuery(java.lang.String key);
    public java.lang.String getQuery();
    public int countQuery();
    public java.lang.String getQuery(int idx);
    public java.lang.String[] getQueryKeys();

    public java.lang.String getQuery(java.lang.String key);
    public boolean hasFragment();
    public int countFragment();
    public java.lang.String getFragment();
    public java.lang.String getFragment(int idx);
    public java.lang.String getFragmentHead();
    public java.lang.String getFragmentTail();
    public int countTerminal();
    public boolean hasTerminal();
    public java.lang.String getTerminal();
    public java.lang.String getTerminal(int idx);
    public java.lang.String getTerminalHead();
    public java.lang.String getTerminalTail();
    public java.lang.String[] getTerminalKeys();
    public java.lang.String getTerminalLookup(java.lang.String key);

}
