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
package alto.sec;

import alto.io.u.Objmap;
import alto.sec.x509.AVA;

/**
 * The following DN keywords are recognized, only one of each.
 * <pre>
 * CN            Common Name (First Last)
 * C             Country
 * L             Locality (City or Town)
 * S             State
 * O             Org
 * OU            Org Unit
 * T             Title
 * STREET        Street
 * DC            Domain Component (Domain Name, e.g. "syntelos")
 * DNQ           Domain Name Qualifier (TLD, e.g., "org")
 * SURNAME       Surname
 * GIVENNAME     Givenname
 * INITIALS      Initials
 * EMAIL         Email Address
 * UID           System User Identifier
 * </pre>
 * 
 */
public class DN 
    extends alto.io.u.Intmap
{

    public final static String KeywordCN        = "CN";
    public final static String KeywordC         = "C";
    public final static String KeywordL         = "L";
    public final static String KeywordS         = "S";
    public final static String KeywordO         = "O";
    public final static String KeywordOU        = "OU";
    public final static String KeywordT         = "T";
    public final static String KeywordSTREET    = "STREET";
    public final static String KeywordDC        = "DC";
    public final static String KeywordDNQ       = "DNQ";
    public final static String KeywordSURNAME   = "SURNAME";
    public final static String KeywordGIVENNAME = "GIVENNAME";
    public final static String KeywordINITIALS  = "INITIALS";
    public final static String KeywordEMAIL     = "EMAIL";
    public final static String KeywordUID       = "UID";

    public final static int MarkCN        =   0x01;
    public final static int MarkC         =   0x02;
    public final static int MarkL         =   0x04;
    public final static int MarkS         =   0x08;
    public final static int MarkO         =   0x10;
    public final static int MarkOU        =   0x20;
    public final static int MarkT         =   0x40;
    public final static int MarkSTREET    =   0x80;
    public final static int MarkDC        =  0x100;
    public final static int MarkDNQ       =  0x200;
    public final static int MarkSURNAME   =  0x400;
    public final static int MarkGIVENNAME =  0x800;
    public final static int MarkINITIALS  = 0x1000;
    public final static int MarkEMAIL     = 0x2000;
    public final static int MarkUID       = 0x4000;

    private final static Objmap MarkMap = new Objmap();
    static {
        MarkMap.put(KeywordCN,        new Integer(MarkCN));
        MarkMap.put(KeywordC,         new Integer(MarkC));
        MarkMap.put(KeywordL,         new Integer(MarkL));
        MarkMap.put(KeywordS,         new Integer(MarkS));
        MarkMap.put(KeywordO,         new Integer(MarkO));
        MarkMap.put(KeywordOU,        new Integer(MarkOU));
        MarkMap.put(KeywordT,         new Integer(MarkT));
        MarkMap.put(KeywordSTREET,    new Integer(MarkSTREET));
        MarkMap.put(KeywordDC,        new Integer(MarkDC));
        MarkMap.put(KeywordDNQ,       new Integer(MarkDNQ));
        MarkMap.put(KeywordSURNAME,   new Integer(MarkSURNAME));
        MarkMap.put(KeywordGIVENNAME, new Integer(MarkGIVENNAME));
        MarkMap.put(KeywordINITIALS,  new Integer(MarkINITIALS));
        MarkMap.put(KeywordEMAIL,     new Integer(MarkEMAIL));
        MarkMap.put(KeywordUID,       new Integer(MarkUID));
    }

    public final static int GetMark(String key){
        Integer mark = (Integer)MarkMap.get(key);
        if (null != mark)
            return mark.intValue();
        else
            return 0;
    }
    public final static String GetName(int mark){
        switch (mark){
        case MarkCN:
            return KeywordCN;
        case MarkC:
            return KeywordC;
        case MarkL:
            return KeywordL;
        case MarkS:
            return KeywordS;
        case MarkO:
            return KeywordO;
        case MarkOU:
            return KeywordOU;
        case MarkT:
            return KeywordT;
        case MarkSTREET:
            return KeywordSTREET;
        case MarkDC:
            return KeywordDC;
        case MarkDNQ:
            return KeywordDNQ;
        case MarkSURNAME:
            return KeywordSURNAME;
        case MarkGIVENNAME:
            return KeywordGIVENNAME;
        case MarkINITIALS:
            return KeywordINITIALS;
        case MarkEMAIL:
            return KeywordEMAIL;
        case MarkUID:
            return KeywordUID;
        default:
            return null;
        }
    }
    public final static int SetMark(int flags, int flag){
        return (flags | flag);
    }
    public final static boolean HasMark(int flags, int flag){
        return (0 < (flags & flag));
    }


    public DN(){
        super();
    }


    public final void updateFromFileManager(){
        this.update(alto.sys.FileManager.Instance());
    }
    public final void update(alto.sys.FileManager fm){
        if (null != fm){
            this.putDC(fm.getDC());
            this.putDNQ(fm.getDNQ());
        }
    }
    public final boolean put(String key, String value){
        int mark = GetMark(key);
        if (0 < mark){
            this.put(mark,value);
            return true;
        }
        else
            return false;
    }
    public final boolean has(String key){
        int mark = GetMark(key);
        if (0 < mark)
            return this.containsKey(mark);
        else
            return false;
    }
    public final String get(String key){
        int mark = GetMark(key);
        if (0 < mark)
            return (String)this.get(mark);
        else
            return null;
    }
    public final void buildCN(){
        if (this.hasNotCN()){
            String title   = this.getT();
            String givname = this.getGIVENNAME();
            String initial = this.getINITIALS();
            String surname = this.getSURNAME();
            if (null != givname){
                if (null != initial){
                    if (null != surname){
                        if (null != title)
                            this.putCN(title+' '+givname+' '+initial+' '+surname);
                        else
                            this.putCN(givname+' '+initial+' '+surname);
                    }
                    else {
                        if (null != title)
                            this.putCN(title+' '+givname+' '+initial);
                        else
                            this.putCN(givname+' '+initial);
                    }
                }
                else if (null != surname){
                    if (null != title)
                        this.putCN(title+' '+givname+' '+surname);
                    else
                        this.putCN(givname+' '+surname);
                }
                else {
                    if (null != title)
                        this.putCN(title+' '+givname);
                    else
                        this.putCN(givname);
                }
            }
            else if (null != initial){
                if (null != surname){
                    if (null != title)
                        this.putCN(title+' '+initial+' '+surname);
                    else
                        this.putCN(initial+' '+surname);
                }
            }
            else if (null != surname){
                if (null != title)
                    this.putCN(title+' '+surname);
                else
                    this.putCN(surname);
            }
        }
    }
    public final String toString(){
        this.buildCN();
        StringBuilder strbuf = new StringBuilder();
        for (int cc = 0, count = this.size(); cc < count; cc++){
            int mark = this.key(cc);
            String name = GetName(mark);
            String value = (String)this.get(mark);
            if (null != name && null != value){
                if (0 < strbuf.length())
                    strbuf.append(',');
                strbuf.append(name);
                strbuf.append('=');
                strbuf.append(AVA.Escape(value));
            }
        }
        return strbuf.toString();
    }
    public final boolean putCN(String value){
        if (null != value){
            super.put(MarkCN,value);
            return true;
        }
        else
            return false;
    }
    public boolean hasCN(){
        return super.containsKey(MarkCN);
    }
    public boolean hasNotCN(){
        return (!super.containsKey(MarkCN));
    }
    public String getCN(){
        return (String)super.get(MarkCN);
    }
    public final boolean putC(String value){
        if (null != value){
            super.put(MarkC,value);
            return true;
        }
        else
            return false;
    }
    public boolean hasC(){
        return super.containsKey(MarkC);
    }
    public boolean hasNotC(){
        return (!super.containsKey(MarkC));
    }
    public String getC(){
        return (String)super.get(MarkC);
    }
    public final boolean putL(String value){
        if (null != value){
            super.put(MarkL,value);
            return true;
        }
        else
            return false;
    }
    public boolean hasL(){
        return super.containsKey(MarkL);
    }
    public boolean hasNotL(){
        return (!super.containsKey(MarkL));
    }
    public String getL(){
        return (String)super.get(MarkL);
    }
    public final boolean putS(String value){
        if (null != value){
            super.put(MarkS,value);
            return true;
        }
        else
            return false;
    }
    public boolean hasS(){
        return super.containsKey(MarkS);
    }
    public boolean hasNotS(){
        return (!super.containsKey(MarkS));
    }
    public String getS(){
        return (String)super.get(MarkS);
    }
    public final boolean putO(String value){
        if (null != value){
            super.put(MarkO,value);
            return true;
        }
        else
            return false;
    }
    public boolean hasO(){
        return super.containsKey(MarkO);
    }
    public boolean hasNotO(){
        return (!super.containsKey(MarkO));
    }
    public String getO(){
        return (String)super.get(MarkO);
    }
    public final boolean putOU(String value){
        if (null != value){
            super.put(MarkOU,value);
            return true;
        }
        else
            return false;
    }
    public boolean hasOU(){
        return super.containsKey(MarkOU);
    }
    public boolean hasNotOU(){
        return (!super.containsKey(MarkOU));
    }
    public String getOU(){
        return (String)super.get(MarkOU);
    }
    public final boolean putT(String value){
        if (null != value){
            super.put(MarkT,value);
            return true;
        }
        else
            return false;
    }
    public boolean hasT(){
        return super.containsKey(MarkT);
    }
    public boolean hasNotT(){
        return (!super.containsKey(MarkT));
    }
    public String getT(){
        return (String)super.get(MarkT);
    }
    public final boolean putSTREET(String value){
        if (null != value){
            super.put(MarkSTREET,value);
            return true;
        }
        else
            return false;
    }
    public boolean hasSTREET(){
        return super.containsKey(MarkSTREET);
    }
    public boolean hasNotSTREET(){
        return (!super.containsKey(MarkSTREET));
    }
    public String getSTREET(){
        return (String)super.get(MarkSTREET);
    }
    public final boolean putDC(String value){
        if (null != value){
            super.put(MarkDC,value);
            return true;
        }
        else
            return false;
    }
    public boolean hasDC(){
        return super.containsKey(MarkDC);
    }
    public boolean hasNotDC(){
        return (!super.containsKey(MarkDC));
    }
    public String getDC(){
        return (String)super.get(MarkDC);
    }
    public final boolean putDNQ(String value){
        if (null != value){
            super.put(MarkDNQ,value);
            return true;
        }
        else
            return false;
    }
    public boolean hasDNQ(){
        return super.containsKey(MarkDNQ);
    }
    public boolean hasNotDNQ(){
        return (!super.containsKey(MarkDNQ));
    }
    public String getDNQ(){
        return (String)super.get(MarkDNQ);
    }
    public final boolean putSURNAME(String value){
        if (null != value){
            super.put(MarkSURNAME,value);
            return true;
        }
        else
            return false;
    }
    public boolean hasSURNAME(){
        return super.containsKey(MarkSURNAME);
    }
    public boolean hasNotSURNAME(){
        return (!super.containsKey(MarkSURNAME));
    }
    public String getSURNAME(){
        return (String)super.get(MarkSURNAME);
    }
    public final boolean putGIVENNAME(String value){
        if (null != value){
            super.put(MarkGIVENNAME,value);
            return true;
        }
        else
            return false;
    }
    public boolean hasGIVENNAME(){
        return super.containsKey(MarkGIVENNAME);
    }
    public boolean hasNotGIVENNAME(){
        return (!super.containsKey(MarkGIVENNAME));
    }
    public String getGIVENNAME(){
        return (String)super.get(MarkGIVENNAME);
    }
    public final boolean putINITIALS(String value){
        if (null != value){
            super.put(MarkINITIALS,value);
            return true;
        }
        else
            return false;
    }
    public boolean hasINITIALS(){
        return super.containsKey(MarkINITIALS);
    }
    public boolean hasNotINITIALS(){
        return (!super.containsKey(MarkINITIALS));
    }
    public String getINITIALS(){
        return (String)super.get(MarkINITIALS);
    }
    public final boolean putEMAIL(String value){
        if (null != value){
            super.put(MarkEMAIL,value);
            return true;
        }
        else
            return false;
    }
    public boolean hasEMAIL(){
        return super.containsKey(MarkEMAIL);
    }
    public boolean hasNotEMAIL(){
        return (!super.containsKey(MarkEMAIL));
    }
    public String getEMAIL(){
        return (String)super.get(MarkEMAIL);
    }
    public final boolean putUID(String value){
        if (null != value){
            super.put(MarkUID,value);
            return true;
        }
        else
            return false;
    }
    public boolean hasUID(){
        return super.containsKey(MarkUID);
    }
    public boolean hasNotUID(){
        return (!super.containsKey(MarkUID));
    }
    public String getUID(){
        return (String)super.get(MarkUID);
    }

}
