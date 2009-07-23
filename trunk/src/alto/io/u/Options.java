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
package alto.io.u ;


/**
 * <p> Parse main options and directives into lookup map.  Names are
 * prefixed with a hyphen, exclusively. </p>
 * 
 * <p> Formally, for each input string in the options list, a lookup
 * entry is constructed according to the following syntax.
 * 
 * <pre>
 * String = &lt;an input token&gt;
 * Name = String &lt;starting with hyphen ('-')&gt;
 * Value = String &lt;not starting with hyphen ('-')&gt;
 * Entry = (Name, Value) | (String, Boolean.TRUE)
 * </pre>
 * </p>
 * 
 * <p> A value has no leading hyphen, and a name may or name not have
 * a leading hyphen.  A name, value pair is mapped (as a pair of java
 * strings) if and only if the name starts with hyphen and then next
 * token following the name does not start with a hyphen.  Otherwise a
 * token is mapped as a key to a java boolean true (object). </p>
 * 
 * </p> A token is mapped to a java boolean object when it has no
 * value.  In this case, when the {@link #getOption(java.lang.String)}
 * method is called it will return the string "true". </p>
 * 
 * <h3>Groups</h3>
 * 
 * <p> A grouping feature is intended to provide for command line
 * options employing specific overrides and general defaults.  A
 * specific name modifies a general name by concatenating with a group
 * name.  A group is used programmatically in parsing options, and the
 * specific case is employed using the same code as employed for
 * parsing general options.  This pattern enables the most convenient
 * programming for known groups.  </p>
 * 
 * <p> For example, a specific override of a general default is shown
 * in the case of the specific name "-o1:g1" and the general name
 * "-o1".  When the group "g1" is used, the value returned by the name
 * "-o1" is that of the specific name. 
 * 
 * <pre>
 *   Command -o1 v1 -o1:g1 g1v2 -o2 v3
 * </pre>
 * 
 * <pre>
 *  options.useGroup(null);
 *  options.getOption("-o1") = "v1";
 *  options.useGroup("g1");
 *  options.getOption("-o1") = "g1v2";
 *  options.getOption("-o2") = "v3";
 * </pre>
 * </p>
 * 
 * <p> The following example demonstrates a practical application.
 * 
 * <pre>
 *   Server -port:http 80 -port:https 443 -port:sip 5060 
 * </pre>
 * 
 * <pre>
 *  options.useGroup("http");
 *  options.getOption("-port") = "80";
 *  options.useGroup("https");
 *  options.getOption("-port") = "443";
 *  options.useGroup("sip");
 *  options.getOption("-port") = "5060";
 * </pre>
 * </p>
 * 
 * <p> Using the same declaration as in the previous example, the
 * inner loop programmatic pattern gets a value and the application
 * driven by the outer loop.
 * 
 * <pre>
 *  options.getOption("-port") = "80";
 *  options.getGroup() = "http";
 * </pre>
 * </p>
 *
 * @author jdp
 * @since 1.2
 */
public class Options
    extends Objmap
{

    abstract static class Name {

        final static class Parse 
            extends Name
        {

            final String source, name, group;

            Parse(String name){
                super();
                this.source = name;
                if ('-' == name.charAt(0)){
                    int idx = name.indexOf(':');
                    if (0 < idx){
                        this.name = name.substring(0,idx);
                        this.group = name.substring(idx+1);
                        return;
                    }
                }
                this.name = name;
                this.group = null;
            }

            public boolean hasGroup(){
                return (null != this.group);
            }
            public boolean hasNotGroup(){
                return (null == this.group);
            }
        }

        final static class Use
            extends Name
        {

            final String source, name, group;

            Use(String group, String name){
                super();
                if (null == group){
                    this.source = name;
                    this.name = name;
                    this.group = null;
                }
                else {
                    this.source = name+':'+group;
                    this.name = name;
                    this.group = group;
                }
            }

            public boolean hasGroup(){
                return (null != this.group);
            }
            public boolean hasNotGroup(){
                return (null == this.group);
            }
        }


        public abstract boolean hasGroup();
        public abstract boolean hasNotGroup();
    }


    private String usegroup;


    public Options(String[] options){
        super();
        for (int cc = 0, count = options.length, next; cc < count; cc++){
            String name = options[cc];
            if ('-' == name.charAt(0)){
                next = (cc+1);
                if (next < count){
                    String value = options[next];
                    if ('-' != value.charAt(0)){
                        this.put(name,value);
                        cc = next;
                        continue;
                    }
                }
                this.put(name,Boolean.TRUE);
                continue;
            }
            else {
                this.put(name,Boolean.TRUE);
                continue;
            }
        }
    }


    public int countOptionsStartingWith(String prefix){
        int re = 0;
        for (int cc = 0, count = this.size(); cc < count; cc++){
            String key = (String)this.key(cc);
            if (key.startsWith(prefix))
                re += 1;
        }
        return re;
    }
    public boolean wantsHelp(){
        return (this.hasOption("-?")||this.hasOption("-h")||this.hasOption("-help"));
    }
    /**
     * @param name Option name, including any hyphen prefix. 
     * @return True when present, otherwise false.
     */
    public boolean hasOption(String name){
        if (null != this.get(name))
            return true;
        else if (null != this.usegroup){
            Name.Use use = new Name.Use(this.usegroup,name);
            return (null != this.get(use.source));
        }
        else
            return false;
    }
    /**
     * @param idx Option index, counting from zero.
     * @return True when present, otherwise false.
     */
    public boolean hasOption(int idx){
        return (null != this.value(idx));
    }
    /**
     * @param name Option name, including any hyphen prefix. 
     * @return One of String or Boolean, may be null when not found.
     */
    public Object getOptionObject(String name){
        Name.Use use = new Name.Use(this.usegroup,name);
        Object value = (Object)this.get(use.source);
        if (null == value && use.hasGroup())
            return this.get(name);
        else
            return value;
    }
    /**
     * @param name Option name, including any hyphen prefix. 
     * @return May be null when not found.
     */
    public String getOption(String name){
        Object value = this.getOptionObject(name);
        if (null == value)
            return null;
        else if (value instanceof String)
            return (String)value;
        else 
            return value.toString();
    }
    /**
     * @param name Option index, counting from zero.
     * @return May be null when not found.
     */
    public String getOption(int idx){
        return (String)this.key(idx);
    }
    /**
     * @param name Option name, including any hyphen prefix. 
     * @return May be null when not found.
     */
    public Boolean getOptionBoolean(String name)
        throws java.lang.NumberFormatException
    {
        Object value = this.getOptionObject(name);
        if (null == value)
            return null;
        else if (value instanceof Boolean)
            return (Boolean)value;
        else {
            String string = (String)value;
            if (string.equalsIgnoreCase("true"))
                return Boolean.TRUE;
            else
                return Boolean.FALSE;
        }
    }
    /**
     * @param name Option index, counting from zero.
     * @return May be null when not found.
     */
    public Boolean getOptionBoolean(int idx)
        throws java.lang.NumberFormatException
    {
        String value = this.getOption(idx);
        if (null == value)
            return null;
        else {
            String string = (String)value;
            if (string.equalsIgnoreCase("true"))
                return Boolean.TRUE;
            else
                return Boolean.FALSE;
        }
    }
    /**
     * @param name Option name, including any hyphen prefix.
     * @return False when not found.
     */
    public boolean getOptionBool(String name){
        Boolean value = this.getOptionBoolean(name);
        if (null != value)
            return value.booleanValue();
        else
            return false;
    }
    /**
     * @param name Option index, counting from zero.
     * @return False when not found.
     */
    public boolean getOptionBool(int idx){
        Boolean value = this.getOptionBoolean(idx);
        if (null != value)
            return value.booleanValue();
        else
            return false;
    }
    /**
     * @param name Option name, including any hyphen prefix. 
     * @return May be null when not found.
     * @exception java.lang.NumberFormatException Value fails to parse
     * from the integer decode method.
     */
    public Integer getOptionInteger(String name)
        throws java.lang.NumberFormatException
    {
        String string = this.getOption(name);
        if (null != string)
            return Integer.decode(string);
        else
            return null;
    }
    /**
     * @param name Option index, counting from zero.
     * @return May be null when not found.
     * @exception java.lang.NumberFormatException Value fails to parse
     * from the integer decode method.
     */
    public Integer getOptionInteger(int idx)
        throws java.lang.NumberFormatException
    {
        String string = this.getOption(idx);
        if (null != string)
            return Integer.decode(string);
        else
            return null;
    }
    /**
     * @param name Option name, including any hyphen prefix. 
     * @return Integer value
     * @exception java.lang.NumberFormatException Value fails to parse
     * from the integer decode method.
     * @exception java.lang.IllegalArgumentException For option not found.
     */
    public int getOptionInt(String name)
        throws java.lang.NumberFormatException,
               java.lang.IllegalArgumentException
    {
        Integer opt = this.getOptionInteger(name);
        if (null == opt)
            throw new IllegalArgumentException("Option not found.");
        else
            return opt.intValue();
    }
    /**
     * @param name Option index, counting from zero.
     * @return Integer value
     * @exception java.lang.NumberFormatException Value fails to parse
     * from the integer decode method.
     * @exception java.lang.IllegalArgumentException For option not found.
     */
    public int getOptionInt(int idx)
        throws java.lang.NumberFormatException,
               java.lang.IllegalArgumentException
    {
        Integer opt = this.getOptionInteger(idx);
        if (null == opt)
            throw new IllegalArgumentException("Option not found.");
        else
            return opt.intValue();
    }
    /**
     * @param name Option name, including any hyphen prefix. 
     * @param def Alternative return value when this option is not
     * found.
     * @return Integer value
     * @exception java.lang.NumberFormatException Value fails to parse
     * from the integer decode method.
     */
    public int getOptionInt(String name, int def)
        throws java.lang.NumberFormatException,
               java.lang.IllegalArgumentException
    {
        Integer opt = this.getOptionInteger(name);
        if (null == opt)
            return def;
        else
            return opt.intValue();
    }
    /**
     * @param name Option index, counting from zero.
     * @param def Alternative return value when this option is not
     * found.
     * @return Integer value
     * @exception java.lang.NumberFormatException Value fails to parse
     * from the integer decode method.
     */
    public int getOptionInt(int idx, int def)
        throws java.lang.NumberFormatException,
               java.lang.IllegalArgumentException
    {
        Integer opt = this.getOptionInteger(idx);
        if (null == opt)
            return def;
        else
            return opt.intValue();
    }
    /**
     * @param name Option name, including hyphen prefix. 
     * @return May be null when not found.
     */
    public java.io.File getOptionFile(String name){
        Object value = this.getOptionObject(name);
        if (null == value)
            return null;
        else if (value instanceof Boolean)
            return null;
        else {
            String string = (String)value;
            return new java.io.File(string);
        }
    }
    /**
     * @param name Option index, counting from zero.
     * @return May be null when not found.
     */
    public java.io.File getOptionFile(int idx){
        String value = this.getOption(idx);
        if (null == value)
            return null;
        else 
            return new java.io.File(value);
    }
    /**
     * @param name Option name, including hyphen prefix. 
     * @return May be null when not found.
     */
    public java.net.URL getOptionUrl(String name)
        throws java.net.MalformedURLException
    {
        Object value = this.getOptionObject(name);
        if (null == value)
            return null;
        else if (value instanceof Boolean)
            return null;
        else {
            String string = (String)value;
            return new java.net.URL(string);
        }
    }
    /**
     * @param name Option index, counting from zero.
     * @return May be null when not found.
     */
    public java.net.URL getOptionUrl(int idx)
        throws java.net.MalformedURLException
    {
        String value = this.getOption(idx);
        if (null == value)
            return null;
        else 
            return new java.net.URL(value);
    }
    public void useGroup(String group){
        this.usegroup = group;
    }
    public String getGroup(){
        return this.usegroup;
    }

    final static class Debug {

        final java.io.File file;
        final String[] argv;
        
        public Debug(String[] argv){
            super();
            int len = argv.length;
            if (0 < len){
                this.file = new java.io.File(argv[0]);
                if (this.file.isFile() && this.file.canRead()){
                    len -= 1;
                    if (0 < len){
                        String[] copier = new String[len];
                        System.arraycopy(argv,1,copier,0,len);
                        this.argv = copier;
                    }
                    else
                        throw new IllegalArgumentException("Usage: file-in options+ ");
                }
                else
                    throw new IllegalArgumentException("Error file not found '"+this.file+"'.\nUsage: file-in options+ ");
            }
            else
                throw new IllegalArgumentException("Usage: file-in options+ ");
        }

        public java.io.FileInputStream openInputStream()
            throws java.io.IOException
        {
            return new java.io.FileInputStream(this.file);
        }
    }

    public static void main(String[] argv){
        Debug dbg = new Debug(argv);
        Options opts = new Options(dbg.argv);
        java.io.DataInputStream in = null;
        try {
            in = new java.io.DataInputStream(dbg.openInputStream());
            String name, value;
            while (null != (name = in.readLine())){
                if (name.startsWith("use ")){
                    if (4 < name.length()){
                        value = name.substring(4).trim();
                        if (0 < value.length())
                            opts.useGroup(value);
                        else
                            opts.useGroup(null);
                    }
                    else
                        opts.useGroup(null);
                }
                else {
                    value = opts.getOption(name);
                    System.out.println(value);
                }
            }
            System.exit(0);
        }
        catch (java.io.IOException exc){
            exc.printStackTrace();
        }
        finally {
            if (null != in){
                try {
                    in.close();
                }
                catch (java.io.IOException ignore){
                }
            }
        }
    }

}
