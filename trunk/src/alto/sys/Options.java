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
 * <p> Parse main options and directives into lookup map.  Options are
 * prefixed with a hyphen and may have a non- hyphen- prefixed
 * argument.  Directives have no argument and are mapped to a boolean
 * true.  </p>
 * 
 * @author jdp
 * @since 1.2
 */
public final class Options
    extends java.util.Properties
{
    public final static Options Instance = new Options();

    private final static String[] HELP = {
        "-?", "-h", "-help", "--h", "--help"
    };
    private final static String[] DEBUG = {
        "-d", "-debug", "--d", "--debug"
    };
    private final static String Server = "server";
    private final static String[] StraryEmpty = new java.lang.String[0];


    public static class List {

        public final static String[] Add(String[] list, String c){
            if (null == c)
                return list;
            else if (null == list)
                return new String[]{c};
            else {
                int len = list.length;
                String[] copier = new String[len+1];
                System.arraycopy(list,0,copier,0,len);
                copier[len] = c;
                return copier;
            }
        }
    }


    private String directive = Server;

    private String[] arguments = StraryEmpty;


    private Options(){
        super();
    }


    public Options init(String[] options){
        if (null != options){
            boolean args = false;
            for (int cc = 0, count = options.length, next; cc < count; cc++){
                String name = options[cc];
                if ('-' == name.charAt(0)){
                    next = (cc+1);
                    if (next < count){
                        String value = options[next];
                        if ('-' != value.charAt(0)){

                            if (null != this.directive)
                                this.arguments = List.Add(this.arguments,name);

                            this.put(name,value);
                            cc = next;
                            continue;
                        }
                    }
                    if (args)
                        this.arguments = List.Add(this.arguments,name);

                    this.put(name,Boolean.TRUE);
                    continue;
                }
                else {
                    if (args)
                        this.arguments = List.Add(this.arguments,name);

                    else {
                        args = true;
                        this.directive = name;
                    }

                    this.put(name,Boolean.TRUE);
                    continue;
                }
            }
        }
        return this;
    }


    public String getDirective(){
        return this.directive;
    }
    public String[] getArguments(){
        return this.arguments;
    }
    public boolean wantsHelp(){
        return this.hasOption(HELP);
    }
    public boolean hasDebug(){
        return this.hasOption(HELP);
    }
    /**
     * @param name Option name, including any hyphen prefix. 
     * @return True when present, otherwise false.
     */
    public boolean hasOption(String name){
        return (null != this.get(name));
    }
    public boolean hasOption(String[] set){
        if (null != set){
            for (String name : set){
                if (null != this.get(name))
                    return true;
            }
        }
        return false;
    }
    public Object getOption(String name){
        if (null != name)
            return this.get(name);
        else
            return null;
    }
    public Object getOption(String[] set){
        if (null != set){
            for (String name : set){
                Object value = this.get(name);
                if (null != value)
                    return value;
            }
        }
        return null;
    }
    public Boolean getOptionBoolean(String name)
        throws java.lang.NumberFormatException
    {
        Object value = this.getOption(name);
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
    public Boolean getOptionBoolean(String[] set)
        throws java.lang.NumberFormatException
    {
        Object value = this.getOption(set);
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
    public boolean getOptionBool(String name){
        Boolean value = this.getOptionBoolean(name);
        if (null != value)
            return value.booleanValue();
        else
            return false;
    }
    public boolean getOptionBool(String[] set){
        Boolean value = this.getOptionBoolean(set);
        if (null != value)
            return value.booleanValue();
        else
            return false;
    }
    public Integer getOptionInteger(String name)
        throws java.lang.NumberFormatException
    {
        Object object = this.getOption(name);
        if (object instanceof String)
            return Integer.decode((String)object);
        else
            return null;
    }
    public Integer getOptionInteger(String[] set)
        throws java.lang.NumberFormatException
    {
        Object object = this.getOption(set);
        if (object instanceof String)
            return Integer.decode((String)object);
        else
            return null;
    }
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
    public int getOptionInt(String[] set)
        throws java.lang.NumberFormatException,
               java.lang.IllegalArgumentException
    {
        Integer opt = this.getOptionInteger(set);
        if (null == opt)
            throw new IllegalArgumentException("Option not found.");
        else
            return opt.intValue();
    }
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
    public int getOptionInt(String[] set, int def)
        throws java.lang.NumberFormatException,
               java.lang.IllegalArgumentException
    {
        Integer opt = this.getOptionInteger(set);
        if (null == opt)
            return def;
        else
            return opt.intValue();
    }
    public java.io.File getOptionFile(String name){
        Object value = this.getOption(name);
        if (null == value)
            return null;
        else if (value instanceof Boolean)
            return null;
        else {
            String string = (String)value;
            return new java.io.File(string);
        }
    }
    public java.io.File getOptionFile(String[] set){
        Object value = this.getOption(set);
        if (null == value)
            return null;
        else if (value instanceof Boolean)
            return null;
        else {
            String string = (String)value;
            return new java.io.File(string);
        }
    }
    public java.net.URL getOptionUrl(String name)
        throws java.net.MalformedURLException
    {
        Object value = this.getOption(name);
        if (null == value)
            return null;
        else if (value instanceof Boolean)
            return null;
        else {
            String string = (String)value;
            return new java.net.URL(string);
        }
    }
    public java.net.URL getOptionUrl(String[] set)
        throws java.net.MalformedURLException
    {
        Object value = this.getOption(set);
        if (null == value)
            return null;
        else if (value instanceof Boolean)
            return null;
        else {
            String string = (String)value;
            return new java.net.URL(string);
        }
    }
}
