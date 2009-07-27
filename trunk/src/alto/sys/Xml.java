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
package alto.sys;

/**
 * <p> A component providing XML I/O operators to itself and its
 * users. </p>
 * 
 * @see Html
 * @author jdp
 * @since 1.5
 */
public interface Xml 
    extends IO
{
    /**
     * <p> Convenience interface combining Xml and Properties
     * Child. </p>
     */
    public interface Child
        extends Xml
    {}

    /**
     * @return A reusable DOM Implementation Registry instance in this
     * context.
     */
    public org.w3c.dom.bootstrap.DOMImplementationRegistry getDOMImplementationRegistry();
    /**
     * @param reg A reusable DOM Implementation Registry instance for
     * this context.
     */
    public void setDOMImplementationRegistry(org.w3c.dom.bootstrap.DOMImplementationRegistry reg);
    /**
     * @return A reusable DOM/LS Implementation instance for this
     * context defaults to the basic "XML 3.0" feature set.
     */
    public org.w3c.dom.ls.DOMImplementationLS getDOMImplementation();
    /**
     * @return Convenient cast of get dom implementation to base interface.
     */
    public org.w3c.dom.DOMImplementation getDOMImplementation2();

    public java.lang.String getDOMImplementationFeatures();

    public void setDOMImplementationFeatures(java.lang.String features);
    /**
     * @param impl A reusable DOM/LS Implementation instance for this
     * context.
     */
    public void setDOMImplementation(org.w3c.dom.ls.DOMImplementationLS impl);
    /**
     * @param features Refer to {@link org.w3c.dom.bootstrap.DOMImplementationRegistry}
     * @return A new DOM Implementation instance for the requested
     * feature set.
     */
    public org.w3c.dom.DOMImplementation createDOMImplementation(java.lang.String features);
    /**
     * @return New document 
     */
    public org.w3c.dom.Document createDocument();
    /**
     * @param ns Document element namespace
     * @param qn Document element qualified name (prefix and local part)
     * @return New document with document element
     */
    public org.w3c.dom.Document createDocument(java.lang.String ns, java.lang.String qn);
    /**
     * @param qn Document type qualified name
     * @param pid Document type identifier 
     * @param sid Document type URI
     * @return New document with document type
     */
    public org.w3c.dom.Document createDocument(java.lang.String qn, java.lang.String pid, java.lang.String sid);
    /**
     * <p> Read doc from stream and close it. </p>
     * @see Xml$Tools#ReadDocument
     */
    public org.w3c.dom.Document readDocument(IO.Source url) 
        throws org.w3c.dom.DOMException, org.w3c.dom.ls.LSException;
    /**
     * <p> Read doc from stream and close it. </p>
     * @see Xml$Tools#ReadDocument
     */
    public org.w3c.dom.Document readDocument(IO.Source url, java.io.InputStream in) 
        throws org.w3c.dom.DOMException, org.w3c.dom.ls.LSException;
    /**
     * <p> Write doc to stream and close it. </p>
     * @see Xml$Tools#WriteDocument
     */
    public void writeDocument(org.w3c.dom.Document doc, IO.Target url) 
        throws org.w3c.dom.DOMException, org.w3c.dom.ls.LSException;
    /**
     * <p> Write doc to stream and close it. </p>
     * @see Xml$Tools#WriteDocument
     */
    public void writeDocument(org.w3c.dom.Document doc, IO.Target url, java.io.OutputStream out) 
        throws org.w3c.dom.DOMException, org.w3c.dom.ls.LSException;
    /**
     * @param doc Node set to traverse
     * @param show Node type filter
     */
    public org.w3c.dom.traversal.NodeIterator createNodeIterator(org.w3c.dom.Document doc, int show)
        throws org.w3c.dom.DOMException;
    /**
     * <p> Traverse all nodes with filter. </p>
     * @param doc Node set to traverse
     * @param filter Node set traversal filter
     */
    public org.w3c.dom.traversal.NodeIterator createNodeIterator(org.w3c.dom.Document doc, 
                                                                 org.w3c.dom.traversal.NodeFilter filter)
        throws org.w3c.dom.DOMException;
    /**
     * <p> Traverse elements with filter. </p>
     * @param doc Node set to traverse
     */
    public org.w3c.dom.traversal.NodeIterator createNodeIterator(org.w3c.dom.Document doc)
        throws org.w3c.dom.DOMException;

    /**
     * <p> Common semantics expected of all implementors. </p>
     */
    public abstract static class Tools 
        extends Properties.Tools
        implements Xml
    {
        /**
         * @param ch Character test
         * @return Character is letter, colon or underscore as defined
         * by XML 1.1 for the first character of an XML identifier
         * (element name).
         */
        public final static boolean IsXmlIdentifierStart(char ch){
            if ('a' <= ch && 'z' >= ch)
                return true;
            else if ('A' <= ch && 'Z' >= ch)
                return true;
            else if (':' == ch || '_' == ch)
                return true;
            else if (0xC0 <= ch && 0xD6 >= ch)
                return true;
            else if (0xD8 <= ch && 0xF6 >= ch)
                return true;
            else if (0xF8 <= ch && 0x2FF >= ch)
                return true;
            else if (0x370 <= ch && 0x37D >= ch) 
                return true;
            else if (0x37F <= ch && 0x1FFF >= ch) 
                return true;
            else if (0x200C <= ch && 0x200D >= ch)
                return true;
            else if (0x2070 <= ch && 0x218F >= ch)
                return true;
            else if (0x2C00 <= ch && 0x2FEF >= ch)
                return true;
            else if (0x3001 <= ch && 0xD7FF >= ch)
                return true;
            else if (0xF900 <= ch && 0xFDCF >= ch)
                return true;
            else if (0xFDF0 <= ch && 0xFFFD >= ch)
                return true;
            else if (0x10000 <= ch && 0xEFFFF >= ch)
                return true;
            else
                return false;
        }
        /**
         * @param ch Character test
         * @return Character is a letter, number, colon, underscore,
         * hyphen, or dot as defined by XML 1.1 for element name
         * characters.
         */
        public final static boolean IsXmlIdentifier(char ch){
            if (IsXmlIdentifierStart(ch))
                return true;
            else if ('-' == ch || '.' == ch)
                return true;
            else if ('0' <= ch && '9' >= ch)
                return true;
            else if (0xB7 == ch)
                return true;
            else if (0x0300 <= ch && 0x036F >= ch)
                return true;
            else if (0x203F <= ch && 0x2040 >= ch)
                return true;
            else
                return false;
        }
        public final static java.lang.String ValueOf(org.w3c.dom.Node node){
            if (null == node)
                return null;
            else {
                switch (node.getNodeType()){
                case org.w3c.dom.Node.ELEMENT_NODE:
                    return ValueOf(node.getFirstChild());
                    
                case org.w3c.dom.Node.ATTRIBUTE_NODE:
                case org.w3c.dom.Node.TEXT_NODE:
                case org.w3c.dom.Node.CDATA_SECTION_NODE:
                    return node.getNodeValue();

                default:
                    return null;
                }
            }
        }
        public final static java.lang.Integer ValueOfInteger(org.w3c.dom.Node node){
            if (null == node)
                return null;
            else {
                java.lang.String string = ValueOf(node);
                if (null == string)
                    return null;
                else 
                    return new java.lang.Integer(string);
            }
        }
        public final static org.w3c.dom.Element GetElementByName(org.w3c.dom.Node node, String name){
            if (null == node)
                return null;
            else {
                org.w3c.dom.NodeList list = null;
                int type = node.getNodeType();
                if (org.w3c.dom.Node.DOCUMENT_NODE == type)
                    list = ((org.w3c.dom.Document)node).getElementsByTagName(name);

                else if (org.w3c.dom.Node.ELEMENT_NODE == type)
                    list = ((org.w3c.dom.Element)node).getElementsByTagName(name);
                else
                    return null;
                //
                if (null != list && 0 < list.getLength())
                    return (org.w3c.dom.Element)list.item(0);
                else
                    return null;
            }
        }
        public final static org.w3c.dom.Element GetChildElementByName(org.w3c.dom.Node node, String name){
            if (null == node)
                return null;
            else {
                org.w3c.dom.NodeList children = node.getChildNodes();
                if (null != children){
                    org.w3c.dom.Node child;
                    for (int cc = 0, count = children.getLength(); cc < count; cc++){
                        child = children.item(cc);
                        if (org.w3c.dom.Node.ELEMENT_NODE == child.getNodeType()){
                            if (name.equals(child.getLocalName()))
                                return (org.w3c.dom.Element)child;
                        }
                    }
                }
                return null;
            }
        }
        public final static org.w3c.dom.Element GetCreateChildElementByName(org.w3c.dom.Node node, String name){
            org.w3c.dom.Element child = GetChildElementByName(node,name);
            if (null != child)
                return child;
            else {
                org.w3c.dom.Document doc = node.getOwnerDocument();
                org.w3c.dom.Element elem = doc.createElement(name);
                node.appendChild(elem);
                return elem;
            }
        }
        public final static void SetValueText(org.w3c.dom.Element node, java.lang.Object value){
            SetValueText(node,value.toString());
        }
        public final static void SetValueText(org.w3c.dom.Element node, java.lang.String value){
            org.w3c.dom.Node test = node.getFirstChild();
            if (null == test){
                org.w3c.dom.Document doc = node.getOwnerDocument();
                org.w3c.dom.Text text = doc.createTextNode(value);
                node.appendChild(text);
            }
            else {
                int typet = test.getNodeType();
                if (org.w3c.dom.Node.TEXT_NODE == typet){
                    node.setNodeValue(value);
                }
                else if (org.w3c.dom.Node.CDATA_SECTION_NODE == typet){
                    node.setNodeValue(value);
                }
                else {
                    org.w3c.dom.Document doc = node.getOwnerDocument();
                    org.w3c.dom.Text text = doc.createTextNode(value);
                    node.appendChild(text);
                }
            }
        }
        public final static void SetCreateChildValueText(org.w3c.dom.Element node, java.lang.String childname, java.lang.Object value){
            SetCreateChildValueText(node,childname,value.toString());
        }
        public final static void SetCreateChildValueText(org.w3c.dom.Element node, java.lang.String childname, java.lang.String value){
            org.w3c.dom.Element child = GetCreateChildElementByName(node,childname);
            SetValueText(child,value);
        }
        public final static void SetChildValueText(org.w3c.dom.Element node, java.lang.String childname, int value){
            SetChildValueText(node,childname,String.valueOf(value));
        }
        public final static void SetChildValueText(org.w3c.dom.Element node, java.lang.String childname, java.lang.Object value){
            SetChildValueText(node,childname,value.toString());
        }
        public final static void SetChildValueText(org.w3c.dom.Element node, java.lang.String childname, java.lang.String value){
            org.w3c.dom.Element child = GetChildElementByName(node,childname);
            if (null == child)
                throw new java.lang.IllegalStateException("Missing required element '"+childname+"', child of '"+node.getNodeName()+"'.");
            else
                SetValueText(child,value);
        }
        /*
         */
        protected static org.w3c.dom.bootstrap.DOMImplementationRegistry DomImplReg;
        protected static org.w3c.dom.ls.DOMImplementationLS DomImpl;
        protected static java.lang.String DomImplFeatures = "XML 3.0";


        public final static org.w3c.dom.bootstrap.DOMImplementationRegistry GetDOMImplementationRegistry(){
            if (null == DomImplReg){
                try {
                    DomImplReg = org.w3c.dom.bootstrap.DOMImplementationRegistry.newInstance();
                }
                catch (java.lang.Throwable exc){
                    throw new alto.sys.Error.Bug(exc);
                }
            }
            return DomImplReg;
        }
        public final static void SetDOMImplementationRegistry(org.w3c.dom.bootstrap.DOMImplementationRegistry reg){
            DomImplReg = reg;
        }
        public final static org.w3c.dom.ls.DOMImplementationLS GetDOMImplementation(){
            if (null == DomImpl){
                java.lang.String features = GetDOMImplementationFeatures();
                org.w3c.dom.DOMImplementation test = CreateDOMImplementation(features);
                if (test instanceof org.w3c.dom.ls.DOMImplementationLS)
                    DomImpl = (org.w3c.dom.ls.DOMImplementationLS)test;
                else
                    throw new alto.sys.Error.Bug("DOM not LS compatible for features '"+features+"'.");
            }
            return DomImpl;
        }
        public final static org.w3c.dom.DOMImplementation GetDOMImplementation2(){
            return (org.w3c.dom.DOMImplementation)GetDOMImplementation();
        }
        public final static java.lang.String GetDOMImplementationFeatures(){
            return DomImplFeatures;
        }
        public final static void SetDOMImplementationFeatures(java.lang.String features){
            DomImpl = null;
            DomImplFeatures = features;
        }
        public final static void SetDOMImplementation(org.w3c.dom.ls.DOMImplementationLS impl){
            DomImpl = impl;
        }
        public final static org.w3c.dom.DOMImplementation CreateDOMImplementation(java.lang.String features){
            org.w3c.dom.DOMImplementation test = GetDOMImplementationRegistry().getDOMImplementation(features);
            if (null == test)
                throw new alto.sys.Error.Bug(features);
            else
                return test;
        }
        public final static org.w3c.dom.Document CreateDocument(){

            return GetDOMImplementation2().createDocument(null,null,null);
        }
        public final static org.w3c.dom.Document CreateDocument(java.lang.String ns, java.lang.String qn){

            return GetDOMImplementation2().createDocument(ns,qn,null);
        }
        public final static org.w3c.dom.Document CreateDocument(java.lang.String qn, java.lang.String pid, java.lang.String sid){

            org.w3c.dom.DOMImplementation dom = GetDOMImplementation2();
            org.w3c.dom.DocumentType type = dom.createDocumentType(qn,pid,sid);
            return dom.createDocument(null,null,type);
        }
        /**
         * @param ele Detach 'ele' from its current document 
         * @return A new document containing only ele.
         */
        public final static org.w3c.dom.Document Export(org.w3c.dom.Element ele) 
            throws org.w3c.dom.DOMException
        {
            org.w3c.dom.Document export = CreateDocument();
            if (null == export.adoptNode(ele)){
                ele = (org.w3c.dom.Element)export.importNode(ele,true);
            }
            export.appendChild(ele);
            return export;
        }
        public final static org.w3c.dom.Document ReadDocument(IO.Source url) 
            throws org.w3c.dom.DOMException, org.w3c.dom.ls.LSException
        {
            return ReadDocument(null,url);
        }
        /**
         * @see Xml#readDocument
         */
        public final static org.w3c.dom.Document ReadDocument(Xml cx, IO.Source url) 
            throws org.w3c.dom.DOMException, org.w3c.dom.ls.LSException
        {
            boolean close = false;
            java.io.InputStream in = null;
            try {
                in = url.getInputStream();
                if (null == in){
                    close = true;
                    in = url.openInputStream();
                }
                return ReadDocument(cx, url, in);
            }
            catch (java.io.IOException exc){
                org.w3c.dom.ls.LSException lsx = new org.w3c.dom.ls.LSException(org.w3c.dom.ls.LSException.PARSE_ERR,"Error reading input from '"+url.toString()+"'.");
                lsx.initCause(exc);
                throw lsx;
            }
            finally {
                if (close && null != in){
                    try {
                        in.close();
                    }
                    catch (java.io.IOException ignore){
                    }
                    finally {
                        in = null;
                    }
                }
            }
        }
        /**
         * @see Xml#readDocument
         */
        public final static org.w3c.dom.Document ReadDocument(Xml cx, IO.Source url, java.io.InputStream in) 
            throws org.w3c.dom.DOMException, org.w3c.dom.ls.LSException
        {
            org.w3c.dom.ls.DOMImplementationLS impl;
            if (null != cx)
                impl = cx.getDOMImplementation();
            else
                impl = GetDOMImplementation();
            org.w3c.dom.ls.LSInput input = impl.createLSInput();
            input.setByteStream(in);
            if (null != url)
                input.setSystemId(url.getUri().toString());
            org.w3c.dom.ls.LSParser parser = impl.createLSParser(org.w3c.dom.ls.DOMImplementationLS.MODE_SYNCHRONOUS,null);
            org.w3c.dom.DOMConfiguration config = parser.getDomConfig();
            //config.setParameter("resource-resolver",alto.dom.io.ResourceResolver.Instance);
            org.w3c.dom.Document re = parser.parse(input);
            return re;
        }
        public final static void WriteDocument(org.w3c.dom.Document doc, IO.Target target)
            throws org.w3c.dom.DOMException, org.w3c.dom.ls.LSException
        {
            WriteDocument(null,doc,target);
        }
        /**
         * @see Xml#writeDocument
         */
        public final static void WriteDocument(Xml cx, org.w3c.dom.Document doc, IO.Target target)
            throws org.w3c.dom.DOMException, org.w3c.dom.ls.LSException
        {
            boolean close = false;
            java.io.OutputStream out = null;
            try {
                out = target.getOutputStream();
                if (null == out){
                    close = true;
                    out = target.openOutputStream();
                }
                WriteDocument(cx,doc,target,out);
            }
            catch (java.io.IOException exc){
                org.w3c.dom.ls.LSException lsx = new org.w3c.dom.ls.LSException(org.w3c.dom.ls.LSException.PARSE_ERR,"Error writing output from '"+target.toString()+"'.");
                lsx.initCause(exc);
                throw lsx;
            }
            finally {
                if (close && null != out){
                    try {
                        out.close();
                    }
                    catch (java.io.IOException ignore){
                    }
                    finally {
                        out = null;
                    }
                }
            }
        }
        /**
         * @see Xml#writeDocument
         */
        public final static void WriteDocument(Xml cx, org.w3c.dom.Document doc, IO.Target target, java.io.OutputStream out)
            throws org.w3c.dom.DOMException, org.w3c.dom.ls.LSException
        {
            org.w3c.dom.ls.DOMImplementationLS impl;
            if (null != cx)
                impl = cx.getDOMImplementation();
            else
                impl = GetDOMImplementation();

            org.w3c.dom.ls.LSOutput output = impl.createLSOutput();
            output.setByteStream(out);
            if (null != target){
                alto.io.Uri uri = target.getUri();
                if (null != uri)
                    output.setSystemId(uri.toString());
            }
            org.w3c.dom.ls.LSSerializer writer = impl.createLSSerializer();
            writer.setNewLine("\r\n");
            writer.write(doc,output);
        }
        public final static org.w3c.dom.traversal.NodeIterator CreateNodeIterator(org.w3c.dom.Document doc, 
                                                                                  int show, 
                                                                                  org.w3c.dom.traversal.NodeFilter filter)
            throws org.w3c.dom.DOMException
        {
            if (null == doc)
                throw new Error.Bug("Null document.");
            else if (doc instanceof org.w3c.dom.traversal.DocumentTraversal){
                org.w3c.dom.traversal.DocumentTraversal doctr = 
                    (org.w3c.dom.traversal.DocumentTraversal)doc;
                return doctr.createNodeIterator(doc,show,filter,true);
            }
            else
                throw new org.w3c.dom.DOMException(org.w3c.dom.DOMException.NOT_SUPPORTED_ERR,"org.w3c.dom.traversal.DocumentTraversal");
        }


        /**
         * 
         */
        protected Tools(){
            super();
        }


        public void close() throws java.io.IOException {
        }
        public org.w3c.dom.bootstrap.DOMImplementationRegistry getDOMImplementationRegistry(){
            if (null == DomImplReg){
                try {
                    DomImplReg = org.w3c.dom.bootstrap.DOMImplementationRegistry.newInstance();
                }
                catch (java.lang.Throwable exc){
                    throw new alto.sys.Error.Bug(exc);
                }
            }
            return DomImplReg;
        }
        public void setDOMImplementationRegistry(org.w3c.dom.bootstrap.DOMImplementationRegistry reg){

            DomImplReg = reg;
        }
        public org.w3c.dom.ls.DOMImplementationLS getDOMImplementation(){

            if (null == DomImpl){
                java.lang.String features = this.getDOMImplementationFeatures();
                org.w3c.dom.DOMImplementation test = this.createDOMImplementation(features);
                if (test instanceof org.w3c.dom.ls.DOMImplementationLS)
                    DomImpl = (org.w3c.dom.ls.DOMImplementationLS)test;
                else
                    throw new alto.sys.Error.Bug("DOM not LS compatible for features '"+features+"'.");
            }
            return DomImpl;
        }
        public org.w3c.dom.DOMImplementation getDOMImplementation2(){
            return (org.w3c.dom.DOMImplementation)this.getDOMImplementation();
        }
        public java.lang.String getDOMImplementationFeatures(){
            return DomImplFeatures;
        }
        public void setDOMImplementationFeatures(java.lang.String features){
            DomImpl = null;
            DomImplFeatures = features;
        }
        public void setDOMImplementation(org.w3c.dom.ls.DOMImplementationLS impl){
            DomImpl = impl;
        }
        public org.w3c.dom.DOMImplementation createDOMImplementation(java.lang.String features){
            org.w3c.dom.DOMImplementation test = this.getDOMImplementationRegistry().getDOMImplementation(features);
            if (null == test)
                throw new alto.sys.Error.Bug(features);
            else
                return test;
        }
        public org.w3c.dom.Document createDocument(){

            return this.getDOMImplementation2().createDocument(null,null,null);
        }
        public org.w3c.dom.Document createDocument(java.lang.String ns, java.lang.String qn){

            return this.getDOMImplementation2().createDocument(ns,qn,null);
        }
        public org.w3c.dom.Document createDocument(java.lang.String qn, java.lang.String pid, java.lang.String sid){

            org.w3c.dom.DOMImplementation dom = this.getDOMImplementation2();
            org.w3c.dom.DocumentType type = dom.createDocumentType(qn,pid,sid);
            return dom.createDocument(null,null,type);
        }
        public org.w3c.dom.Document readDocument(alto.sys.IO.Source source) 
            throws org.w3c.dom.DOMException, org.w3c.dom.ls.LSException
        {
            return alto.sys.Xml.Tools.ReadDocument(this,source);
        }
        public org.w3c.dom.Document readDocument(alto.sys.IO.Source source, java.io.InputStream in) 
            throws org.w3c.dom.DOMException, org.w3c.dom.ls.LSException
        {
            return alto.sys.Xml.Tools.ReadDocument(this,source,in);
        }
        public void writeDocument(org.w3c.dom.Document doc, alto.sys.IO.Target target) 
            throws org.w3c.dom.DOMException, org.w3c.dom.ls.LSException
        {
            alto.sys.Xml.Tools.WriteDocument(this,doc,target);
        }
        public void writeDocument(org.w3c.dom.Document doc, alto.sys.IO.Target target, java.io.OutputStream out) 
            throws org.w3c.dom.DOMException, org.w3c.dom.ls.LSException
        {
            alto.sys.Xml.Tools.WriteDocument(this,doc,target,out);
        }
        public org.w3c.dom.traversal.NodeIterator createNodeIterator(org.w3c.dom.Document doc, int show)
            throws org.w3c.dom.DOMException
        {
            return alto.sys.Xml.Tools.CreateNodeIterator(doc,show,null);
        }
        public org.w3c.dom.traversal.NodeIterator createNodeIterator(org.w3c.dom.Document doc, 
                                                                     org.w3c.dom.traversal.NodeFilter filter)
            throws org.w3c.dom.DOMException
        {
            return alto.sys.Xml.Tools.CreateNodeIterator(doc,org.w3c.dom.traversal.NodeFilter.SHOW_ALL,filter);
        }
        public org.w3c.dom.traversal.NodeIterator createNodeIterator(org.w3c.dom.Document doc)
            throws org.w3c.dom.DOMException
        {
            return alto.sys.Xml.Tools.CreateNodeIterator(doc,org.w3c.dom.traversal.NodeFilter.SHOW_ELEMENT,null);
        }

    }
}
