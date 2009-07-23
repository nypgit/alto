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
package alto.lang;

import alto.io.Code;
import alto.io.Check;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * <p> Internet date-time reading and writing.  (Because known
 * alternatives are buggy and slow).  </p>
 * 
 * @author jdp
 * @since 1.5
 */
public final class Date
    extends java.lang.Object
{
    public final static long Seconds = 1000L;
    public final static long Minutes = (60L * Seconds);
    public final static long Hours = (60L * Minutes);
    public final static long Days = (24L * Hours);
    public final static long Years = (365L * Days);

    public final static java.util.Date Future(long project){
        long time = (java.lang.System.currentTimeMillis() + project);
        return new java.util.Date(time);
    }


    public final static TimeZone UTC = TimeZone.getTimeZone("GMT");
    public final static Locale LOCALE_EN = Locale.ENGLISH;
    public final static Symbols SYMBOLS_EN = new Symbols(LOCALE_EN);
    /**
     * <p> Index weekdays and months to calendar field values for
     * lookup. </p>
     * 
     * @author jdp
     */
    public final static class Symbols 
        extends DateFormatSymbols
    {
        private final static alto.io.u.Lobjmap cache = new alto.io.u.Lobjmap();
        static {
            cache.put(Date.LOCALE_EN,Date.SYMBOLS_EN);
        }
        public final static Symbols Instance(Locale locale){
            if (null == locale || Date.LOCALE_EN.equals(locale))
                return Date.SYMBOLS_EN;
            else {
                cache.lockReadEnter();
                try {
                    Symbols symbols = (Symbols)cache.get(locale);
                    if (null != symbols)
                        return symbols;
                }
                finally {
                    cache.lockReadExit();
                }
                cache.lockWriteEnter();
                try {
                    Symbols symbols = (Symbols)cache.get(locale);
                    if (null != symbols)
                        return symbols;
                    else {
                        symbols = new Symbols(locale);
                        cache.put(locale,symbols);
                        return symbols;
                    }
                }
                finally {
                    cache.lockWriteExit();
                }
            }
        }

        private final alto.io.u.Objmap weekdays = new alto.io.u.Objmap(31);
        private final alto.io.u.Objmap months = new alto.io.u.Objmap(31);
        private final Locale locale;

        public Symbols(Locale locale){
            super(locale);
            this.locale = locale;
            java.lang.String symbol, list[];
            list = this.getMonths();
            for (int cc = 0, count = list.length; cc < count; cc++){
                symbol = list[cc];
                if (null != symbol)
                    this.months.put(symbol,new Integer(cc));
            }
            list = this.getShortMonths();
            for (int cc = 0, count = list.length; cc < count; cc++){
                symbol = list[cc];
                if (null != symbol)
                    this.months.put(symbol,new Integer(cc));
            }
            list = this.getWeekdays();
            for (int cc = 0, count = list.length; cc < count; cc++){
                symbol = list[cc];
                if (null != symbol)
                    this.weekdays.put(symbol,new Integer(cc));
            }
            list = this.getShortWeekdays();
            for (int cc = 0, count = list.length; cc < count; cc++){
                symbol = list[cc];
                if (null != symbol)
                    this.weekdays.put(symbol,new Integer(cc));
            }
        }


        public Locale getLocale(){
            return this.locale;
        }
        public java.lang.String getCalendarWeekday(int idx){
            if (-1 < idx){
                java.lang.String[] list = this.getShortWeekdays();
                if (idx < list.length)
                    return list[idx];
            }
            throw new IllegalArgumentException(java.lang.String.valueOf(idx));
        }
        public int getCalendarWeekday(java.lang.String symbol){
            Integer re = (Integer)this.weekdays.get(symbol);
            if (null == re){
                if (this == Date.SYMBOLS_EN)
                    return -1;
                else
                    return Date.SYMBOLS_EN.getCalendarWeekday(symbol);
            }
            else
                return re.intValue();
        }
        public java.lang.String getCalendarMonth(int idx){
            if (-1 < idx){
                java.lang.String[] list = this.getShortMonths();
                if (idx < list.length)
                    return list[idx];
            }
            throw new IllegalArgumentException(java.lang.String.valueOf(idx));
        }
        public int getCalendarMonth(java.lang.String symbol){
            Integer re = (Integer)this.months.get(symbol);
            if (null == re){
                if (this == Date.SYMBOLS_EN)
                    return -1;
                else
                    return Date.SYMBOLS_EN.getCalendarMonth(symbol);
            }
            else
                return re.intValue();
        }
    }
    
    public final static java.lang.String ToString(){
        return ToString(java.lang.System.currentTimeMillis());
    }
    public final static java.lang.String ToString(long utc){
        GregorianCalendar calendar = new GregorianCalendar(UTC,LOCALE_EN);
        calendar.setTimeInMillis(utc);
        return ToString(calendar,SYMBOLS_EN);
    }
    public final static java.lang.String ToString(long utc, Locale locale){
        if (null == locale)
            return ToString(utc);
        else {
            GregorianCalendar calendar = new GregorianCalendar(UTC,locale);
            calendar.setTimeInMillis(utc);
            Symbols symbols = Date.Symbols.Instance(locale);
            return ToString(calendar,symbols);
        }
    }
    private final static java.lang.String ToString(GregorianCalendar cal, Symbols symbols){
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        TimeZone timezone = cal.getTimeZone();

        java.lang.StringBuilder strbuf = new java.lang.StringBuilder();
        java.lang.String day = symbols.getCalendarWeekday(dayOfWeek);
        strbuf.append(day);
        strbuf.append(", ");
        if (10 > dayOfMonth)
            strbuf.append('0');
        strbuf.append(dayOfMonth);
        strbuf.append(' ');
        java.lang.String monthShort = symbols.getCalendarMonth(month);
        strbuf.append(monthShort);
        strbuf.append(' ');
        strbuf.append(year);
        strbuf.append(' ');
        if (10 > hour)
            strbuf.append('0');
        strbuf.append(hour);
        strbuf.append(':');
        if (10 > minute)
            strbuf.append('0');
        strbuf.append(minute);
        strbuf.append(':');
        if (10 > second)
            strbuf.append('0');
        strbuf.append(second);
        strbuf.append(' ');
        strbuf.append(timezone.getID());
        return strbuf.toString();
    }
    public final static long Parse(java.lang.String string){
        return Parse(string,SYMBOLS_EN);
    }
    public final static long Parse(java.lang.String string, Locale locale){
        if (null == locale)
            return Parse(string);
        else {
            Symbols symbols = Symbols.Instance(locale);
            return Parse(string,symbols);
        }
    }
    private final static long Parse(java.lang.String string, Symbols symbols){
        if (null == string)
            return -1;
        else {
            //            
            int year = -1, month = -1, date = -1, hour = -1, minute = -1, second = -1;
            TimeZone timezone;
            //
            java.util.StringTokenizer strtok = new java.util.StringTokenizer(string," \t");
            switch (strtok.countTokens()){
            case 4:
                //       Sunday, 06-Nov-94 08:49:37 GMT   ; RFC 850, obsoleted by RFC 1036
                {
                    strtok.nextToken();//dayOfWeek
                    java.lang.String tokDmy = strtok.nextToken();
                    int[] dmy = null;
                    try {
                        dmy = DMY(symbols,tokDmy);
                        if (null == dmy)
                            return -1;
                    }
                    catch (java.lang.NumberFormatException nfx){
                        return -1;
                    }
                    date = dmy[0];
                    month = dmy[1];
                    year = dmy[3];
                    java.lang.String tokHms = strtok.nextToken();
                    int[] hms = null;
                    try {
                        hms = HMS(tokHms);
                        if (null == hms)
                            return -1;
                    }
                    catch (java.lang.NumberFormatException nfx){
                        return -1;
                    }
                    hour = hms[0];
                    minute = hms[1];
                    second = hms[2];
                    timezone = TZ(strtok.nextToken());
                }
                break;
            case 5:
                //       Sun Nov  6 08:49:37 1994         ; ANSI C's asctime() format
                {
                    strtok.nextToken();//dayOfWeek
                    java.lang.String tokMonth = strtok.nextToken();
                    month = symbols.getCalendarMonth(tokMonth);
                    if (0 > month)
                        return -1;
                    else {
                        java.lang.String tokDate = strtok.nextToken();
                        try {
                            date = INT(tokDate);
                        }
                        catch (java.lang.NumberFormatException nfx){
                            return -1;
                        }
                        java.lang.String tokHMS = strtok.nextToken();
                        int[] hms = null;
                        try {
                            hms = HMS(tokHMS);
                            if (null == hms)
                                return -1;
                        }
                        catch (java.lang.NumberFormatException nfx){
                            return -1;
                        }
                        hour = hms[0];
                        minute = hms[1];
                        second = hms[2];
                        java.lang.String tokYear = strtok.nextToken();
                        try {
                            year = YYYY(INT(tokYear));
                        }
                        catch (java.lang.NumberFormatException nfx){
                            return -1;
                        }
                    }
                }
            case 6:
                //       Sun, 06 Nov 1994 08:49:37 GMT    ; RFC 822, updated by RFC 1123
                {
                    strtok.nextToken();//dayOfWeek
                    date = INT(strtok.nextToken());
                    java.lang.String tokMonth = strtok.nextToken();
                    month = symbols.getCalendarMonth(tokMonth);
                    if (0 > month)
                        return -1;
                    else {
                        java.lang.String tokYear = strtok.nextToken();
                        try {
                            year = YYYY(INT(tokYear));
                        }
                        catch (java.lang.NumberFormatException exc){
                            return -1;
                        }
                        java.lang.String tokHms = strtok.nextToken();
                        int[] hms = null;
                        try {
                            hms = HMS(tokHms);
                            if (null == hms)
                                return -1;
                        }
                        catch (java.lang.NumberFormatException nfx){
                            return -1;
                        }
                        hour = hms[0];
                        minute = hms[1];
                        second = hms[2];
                        timezone = TZ(strtok.nextToken());
                    }
                }
                break;
            default:
                return -1;
            }
            Calendar calendar = new GregorianCalendar(timezone,LOCALE_EN);
            calendar.set(year,month,date,hour,minute,second);
            return calendar.getTimeInMillis();
        }
    }
    /**
     * For values less than 50, add 2000, otherwise for values less
     * than 100 add 1900.
     * 
     * @param year Integer parsed from string, may be a two digit year
     * (less than 100).
     * @return A four digit year. 
     */
    public final static int YYYY(int year){
        if (100 > year){
            if (50 > year)
                return (2000+year);
            else
                return (1900+year);
        }
        else
            return year;
    }
    public final static int INT(java.lang.String string){
        return Integer.parseInt(string);
    }
    /**
     * Parse <code>"##-(##|Month.Symbol)-(####|##)"</code>. 
     * @return An array of three integers in {D,M,Y} order.
     */
    public final static int[] DMY(Symbols symbols, java.lang.String tokDmy){
        int idx1 = tokDmy.indexOf('-');
        if (0 < idx1){
            java.lang.String tokDate = tokDmy.substring(0,idx1);
            idx1 += 1;
            int idx2 = tokDmy.indexOf('-',idx1);
            if (0 < idx2){
                java.lang.String tokMonth = tokDmy.substring(idx1,idx2);
                idx2 += 1;
                java.lang.String tokYear = tokDmy.substring(idx2);
                //
                int[] dmy = new int[3];
                dmy[0] = Integer.parseInt(tokDate);
                try {
                    dmy[1] = (Integer.parseInt(tokMonth)-1);
                }
                catch (java.lang.NumberFormatException exc){
                    int test = symbols.getCalendarMonth(tokMonth);
                    if (-1 < test)
                        dmy[1] = test;
                    else
                        return null;
                }
                dmy[2] = YYYY(Integer.parseInt(tokYear));
                return dmy;
            }
        }
        return null;
    }
    /**
     * Parse three integers from <code>"HH:MM:SS"</code>.
     * @return An array of three integers in <i>{H, M, S}</i> order,
     * or null for syntax failure.
     */
    public final static int[] HMS(java.lang.String hmsTok){
        if (8 <= hmsTok.length()){
            if (':' == hmsTok.charAt(2)){
                if (':' == hmsTok.charAt(5)){
                    java.lang.String tokH = hmsTok.substring(0,2);
                    java.lang.String tokM = hmsTok.substring(3,5);
                    java.lang.String tokS = hmsTok.substring(6,8);
                    int[] hms = new int[3];
                    hms[0] = Integer.parseInt(tokH);
                    hms[1] = Integer.parseInt(tokM);
                    hms[2] = Integer.parseInt(tokS);
                    return hms;
                }
            }
        }
        return null;
    }
    public final static TimeZone TZ(java.lang.String string){
        if (null == string || 1 > string.length())
            return UTC;
        else {
            int strlen = string.length();
            if ('(' == string.charAt(0)){
                if (')' == string.charAt(strlen-1)){
                    string = string.substring(1,(strlen-2));
                }
                else
                    string = string.substring(1);
            }
            else if (')' == string.charAt(strlen-1))
                string = string.substring(0,(strlen-1));
        }
        //
        if ("GMT".equals(string))
            return UTC;
        else
            return TimeZone.getTimeZone(string);
    }


}
