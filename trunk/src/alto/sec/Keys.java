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

import alto.hash.SHA1;
import alto.io.Authentication;
import alto.io.Code;
import alto.io.Check;
import alto.io.Input;
import alto.io.Message;
import alto.io.Output;
import alto.io.Principal;
import alto.lang.Address;
import alto.lang.Component;
import alto.lang.Sio;
import alto.sec.pkcs.PKCS12;
import alto.sec.x509.X500Name;
import alto.sec.x509.X500Signer;
import alto.sec.x509.X509Certificate;
import alto.sys.FileManager;
import alto.sys.Reference;

import java.util.Set;

/**
 * A {@link alto.io.Principal} is obtained for an identifier in host
 * scope from {@link Keys}.
 * 
 * The {@link Keys} are {@link alto.sys.PSioFile} objects in {@link
 * alto.lang.Sio} format.  Instances of {@link Keys} have the filename
 * extension "keys" under path "/s/" <i>+ identifier</i> in user
 * space.
 * 
 * @author jdp
 */
public class Keys
    extends alto.sys.PSioFile
    implements alto.sys.Destroy,
               alto.io.Keys,
               alto.io.Principal.Actual
{

    public final static class System {
        public final static alto.io.Role ROLE = alto.io.Role.SYSTEM;
        public final static String NAME = ROLE.name();
        public final static String PATH = Keys.Tools.PathTo(NAME);

        public final static boolean IsReferenceTo(Reference ref){

            return (null != ref && ref.isAddressContainerLocal() && ref.getPath().equals(PATH));
        }
        public final static boolean EnterThreadContextTry(){
            return Keys.System.ReferenceTo().enterThreadContextTry();
        }
        public final static Reference ReferenceTo(){
            try {
                return Keys.Tools.ReferenceTo(NAME);
            }
            catch (java.io.IOException exc){
                throw new alto.sys.Error.State(NAME,exc);
            }
        }
    }

    public abstract static class Tools {

        public final static Keys Dereference(String identifier)
            throws java.io.IOException
        {
            if (null != Instance)
                return Instance.dereference(Instance.referenceTo(identifier));
            else
                throw new alto.sys.Error.Bug();
        }
        public final static Keys Dereference(Reference ref)
            throws java.io.IOException
        {
            if (null != Instance)
                return Instance.dereference(ref);
            else
                throw new alto.sys.Error.Bug();
        }
        public final static Keys Dereference(Reference ref, X500Name dn)
            throws java.io.IOException
        {
            if (null != Instance)
                return Instance.dereference(ref,dn);
            else
                throw new alto.sys.Error.Bug();
        }
        public final static Reference ReferenceTo(String identifier)
            throws java.io.IOException
        {
            if (null != Instance)
                return Instance.referenceTo(identifier);
            else
                throw new alto.sys.Error.Bug();
        }
        public final static Reference ReferenceTo(String host, String identifier)
            throws java.io.IOException
        {
            if (null != Instance)
                return Instance.referenceTo(host,identifier);
            else
                throw new alto.sys.Error.Bug();
        }
        public final static Reference ReferenceTo(Component.Host host, String identifier)
            throws java.io.IOException
        {
            if (null != Instance)
                return Instance.referenceTo(host,identifier);
            else
                throw new alto.sys.Error.Bug();
        }
        public final static Reference ReferenceTo(FileManager fm)
            throws java.io.IOException
        {
            if (fm.isDefault())
                return Keys.System.ReferenceTo();
            else {
                return ReferenceTo(fm.getComponentDN(),fm.getDN());
            }
        }
        public final static Keys GetCreate(java.lang.String identifier, X500Name name)
            throws java.io.IOException
        {
            return GetCreate(identifier,name,"RSA");
        }
        public final static Keys GetCreate(java.lang.String identifier,
                                           X500Name name,
                                           java.lang.String alg)
            throws java.io.IOException
        {
            if (null != identifier && null != alg){
                Reference target = ReferenceTo(identifier);

                Keys keys = Dereference(target);
                if (keys.existsStorage()){
                    if (keys.generate(alg))
                        keys.writeMessage();

                    keys.readMessage();
                    return keys;
                }
                else if (null != name){
                    keys.setName(name);
                    if (keys.generate(alg)){

                        keys.writeMessage();

                        return keys;
                    }
                    else
                        throw new alto.sys.Error.Bug("Unable to generate keys for '"+alg+"'.");
                }
                else
                    throw new alto.sys.Error.Argument("Missing 'X500Name name'.");
            }
            else
                return null;
        }
        public final static java.lang.String IdentifierFrom(Reference reference){
            if (null != reference)
                return IdentifierFrom(reference.getPath());
            else
                return null;
        }
        public final static java.lang.String IdentifierFrom(String path){
            if (null != path){
                if (path.startsWith("/s/")){
                    path = path.substring(3);
                    if (path.endsWith(".keys")){
                        path = path.substring(0,(path.length()-".keys".length()));
                        return path;
                    }
                    else
                        return null;
                }
                else if (path.endsWith(".keys")){
                    path = path.substring(0,(path.length()-".keys".length()));
                    return path;
                }
                else
                    return path;//(path is identifier)
            }
            else
                return null;
        }
        public final static String PathTo(Reference reference){
            if (null != reference)
                return PathTo(IdentifierFrom(reference.getPath()));
            else 
                throw new alto.sys.Error.Argument();
        }
        public final static String PathTo(java.lang.String identifier){
            if ("system".equalsIgnoreCase(identifier))
                return SystemPathTo;
            else if (null != identifier){
                String path;
                if (!identifier.startsWith("/s/"))
                    path = alto.io.u.Chbuf.fcat("/s/",identifier);
                else
                    path = identifier;

                if (!path.endsWith(".keys"))
                    path += ".keys";

                return path;
            }
            else 
                throw new alto.sys.Error.Argument();
        }


        protected static Tools Instance;

        /**
         * @see alto.io.Tools
         */
        public final static void SInit(Tools instance){
            if (null == Instance){
                Instance = instance;
                if (instance instanceof alto.sys.Init){
                    ((alto.sys.Init)instance).init();
                }
            }
            else
                throw new alto.sys.Error.State();
        }

        public abstract Keys dereference(Reference ref)
            throws java.io.IOException;

        public abstract Keys dereference(Reference ref, X500Name dn)
            throws java.io.IOException;

        public abstract Reference referenceTo(String identifier);

        public abstract Reference referenceTo(String host, String identifier);

        public abstract Reference referenceTo(Component.Host host, String identifier);
    }


    public final static Keys Application()
        throws java.io.IOException
    {
        FileManager fm = FileManager.Instance();
        Reference reference = fm.getKeysReference();
        return Tools.Dereference(reference);
    }
    /**
     * The "protector" relation: an application is protected by the
     * system, while others are protected by their application.  The
     * default application is the system.  
     * 
     * N.B.  The implementation of this method is very specific case
     * of the general relation described immediately above.  This
     * implementation assumes that the calling application scope
     * (FileManager) is the protector of all keys newly created.
     */
    protected final static Keys ProtectorOf(Keys keys)
        throws java.io.IOException
    {
        if (keys.isSystem())
            return null;
        else {
            FileManager fm = FileManager.Instance();
            if (fm.isDefault())
                return Keys.Tools.Dereference(Keys.System.ReferenceTo());

            else {
                Reference fmKeysReference = fm.getKeysReference();
                if (keys.getReference().equals(fmKeysReference))
                    return Keys.Tools.Dereference(Keys.System.ReferenceTo());
                else
                    return Keys.Tools.Dereference(fmKeysReference);
            }
        }
    }


    protected Key[] list;

    protected Role[] roles;

    protected Auth[] auths;

    protected boolean protectorInit;

    protected Keys protector;

    protected X500Name name;

    protected Authentication scopeAuthentication;
    /**
     * If not null, this instance is a copy that can be popped and
     * destroyed.  The destruction of the copy has no effect on its
     * precursor (i.e., key list is released, not destroyed).
     */
    protected Principal.Actual scopePrincipal;


    /**
     * 
     */
    public Keys(Reference reference)
        throws java.io.IOException
    {
        super(reference);
    }


    public void destroy(){
        this.name = null;
        this.protector = null;
        if (this.isPushed())
            this.list = null;
        else {
            Key[] list = this.list;
            if (null != list){
                for (int cc = 0, count = list.length; cc < count; cc++){
                    list[cc].destroy();
                }
                this.list = null;
            }
        }
        this.scopeAuthentication = null;
        this.scopePrincipal = null;
    }
    public java.lang.String getName(){
        try {
            X500Name name = this.getX500Name();
            return name.getUID();
        }
        catch (java.io.IOException unexpected){
            throw new alto.sys.Error.Bug(unexpected);
        }
    }
    /**
     * This method exists for performance reasons, and its use is
     * trusted for that purpose.  It is called from this class, and
     * from {@link alto.tools.Keys}.
     */
    @Code(Check.SecTrusted)
    public synchronized void setName(X500Name name){
        if (null != name){
            if (null == this.name){
                this.name = name;
            }
        }
    }
    public String getUID(){
        try {
            X500Name name = this.getX500Name();
            return name.getUID();
        }
        catch (java.io.IOException unexpected){
            throw new alto.sys.Error.Bug(unexpected);
        }
    }
    public boolean isSystem(){
        return Keys.System.IsReferenceTo(this.reference);
    }
    public boolean isNotSystem(){
        return (!this.isSystem());
    }
    public boolean isApplication(){
        Keys protector = (Keys)this.getProtector();
        return (null != protector && protector.isSystem());
    }
    public boolean isUser(){
        Keys protector = (Keys)this.getProtector();
        return (null != protector && protector.isApplication());
    }
    public alto.io.Keys getKeys(){
        return this;
    }
    public Principal.Authentic getPrincipal(){
        return this;
    }
    public X500Name getX500Name() throws java.io.IOException {
        X500Name name = this.name;
        if (null == name){
            X509Certificate cert = this.getCertificate("RSA");
            if (null != cert){

                name = cert.getSubjectX500Principal();
                synchronized(this){
                    this.name = name;
                }
            }
            else {
                FileManager fm = FileManager.Instance();
                Reference reference = this.reference;
                if (reference == fm.getKeysReference()){

                    name = fm.createX500Name();
                    synchronized(this){
                        this.name = name;
                    }
                }
                else
                    throw new alto.sys.Error.Bug();
            }
        }
        return name;
    }
    public boolean hasProtector(){
        return (null != this.getProtector());
    }
    public Principal.Actual getProtector(){
        if (!this.protectorInit){
            synchronized(this){
                this.protectorInit = true;
                try {
                    this.protector = ProtectorOf(this);
                }
                catch (java.io.IOException exc){
                    throw new alto.sys.Error.State(exc);
                }
            }
        }
        return this.protector;
    }
    /**
     * @param alg Key type, e.g., "RSA"
     */
    public X509Certificate getCertificate(java.lang.String alg){
        Key key = this.get(alg);
        if (null != key)
            return key.getCertificate();
        else
            return null;
    }
    /**
     * @param alg Key type, e.g., "RSA"
     * @throws java.io.IOException Loading principal
     * @throws java.security.InvalidKeyException Initializing signature engine
     * @throws java.security.NoSuchAlgorithmException Initializing signature engine
     */
    public X500Signer getSigner(java.lang.String alg)
        throws java.io.IOException, 
               java.security.InvalidKeyException, 
               java.security.NoSuchAlgorithmException
    {
        Key key = this.get(alg);
        if (null != key)
            return key.getSigner();
        else
            return null;
    }
    public boolean generate(String alg){
        if (this.has(alg))
            return false;
        else {
            Key key = new Key(this);
            key.generate(alg);
            this.list = Key.List.Add(this.list,key);
            return true;
        }
    }
    public boolean generateRSA(){
        if (this.has("RSA"))
            return false;
        else {
            Key key = new Key(this);
            key.generateRSA();
            this.list = Key.List.Add(this.list,key);
            return true;
        }
    }
    /**
     * Use the first RSA key to enfoam.
     */
    public byte[] enfoamRSA(Address nonce, byte[] msg){
        if (null == msg)
            return null;
        else {
            Key foamer = this.getKeyRSA();
            if (null != foamer)
                return foamer.enfoamRSA(nonce,msg);
            else
                return msg;
        }
    }
    /**
     * Use the first RSA key to defoam.
     */
    public byte[] defoamRSA(Address nonce, byte[] msg){
        if (null == msg)
            return null;
        else {
            Key foamer = this.getKeyRSA();
            if (null != foamer)
                return foamer.defoamRSA(nonce,msg);
            else
                return msg;
        }
    }
    /**
     * Conditional read only when required to load the key set
     */
    public synchronized boolean init(X500Name name)
        throws java.io.IOException
    {
        if (null == this.name){
            this.name = name;
            this.readMessage();
            return true;
        }
        else if (null == this.list && this.reference.existsStorage()){
            this.readMessage();
            return true;
        }
        else
            return false;
    }
    /**
     * Conditional read only when required to load the key set
     */
    public boolean init()
        throws java.io.IOException
    {
        if (null == this.list && this.reference.existsStorage()){
            this.readMessage();
            return true;
        }
        else
            return false;
    }
    protected void authenticateFromRead()
        throws java.io.IOException
    {
    }
    /**
     * Called from the super class on this instance writing itself.
     * 
     * @see alto.io.Message#writeMessage()
     * @see alto.sys.PSioFile#authenticate()
     */
    protected void authenticateForWrite()
        throws java.io.IOException
    {
        if (this.reference.hasCreatedMeta()){
            this.reference.authenticateCreatedMeta(this);
            this.reference.enterThreadContextFromCreatedMetaTry();
        }
        else
            super.authenticateForWrite();
    }
    public void writeMessage(Output out)
        throws java.io.IOException
    {
        Key[] list = this.list;
        if (null != list){
            int cc = 0, count = list.length;
            Sio.Field.WriteInt(count,out);
            for (; cc < count; cc++){
                Key key = list[cc];
                key.sioWrite(out);
            }
        }
        else
            Sio.Field.WriteInt(0,out);

        Auth[] auths = this.auths;
        if (null != auths){
            int cc = 0, count = auths.length;
            Sio.Field.WriteInt(count,out);
            for (; cc < count; cc++){
                Auth auth = auths[cc];
                auth.sioWrite(this,out);//(Protected)
            }
        }
        else
            Sio.Field.WriteInt(0,out);

        Role[] roles = this.roles;
        if (null != roles){
            int cc = 0, count = roles.length;
            Sio.Field.WriteInt(count,out);
            for (; cc < count; cc++){
                Role role = roles[cc];
                role.sioWrite(this,out);//(Protected)
            }
        }
        else
            Sio.Field.WriteInt(0,out);
    }
    public void readMessage(Input in)
        throws java.io.IOException
    {
        int count;
        count = Sio.Field.ReadInt(in);
        if (0 < count){
            Key[] list = new Key[count];
            for (int cc = 0; cc < count; cc++){
                Key key = new Key(this,in);
                list[cc] = key;
            }
            this.list = list;
        }
        count = Sio.Field.ReadInt(in);
        if (0 < count){
            Auth[] list = new Auth[count];
            for (int cc = 0; cc < count; cc++){
                Auth auth = new Auth(this,in);//(Protected)
                list[cc] = auth;
            }
            this.auths = list;
        }
        count = Sio.Field.ReadInt(in);
        if (0 < count){
            Role[] list = new Role[count];
            for (int cc = 0; cc < count; cc++){
                Role role = new Role(this,in);//(Protected)
                list[cc] = role;
            }
            this.roles = list;
        }

        this.authenticateFromRead();
    }
    protected Key getProtectorRSA(){
        Keys protector = (Keys)this.getProtector();
        if (null != protector)
            return protector.getKeyRSA();
        else
            return null;
    }
    /**
     * Use the protector to enfoam using this address as a nonce.
     */
    protected byte[] protectRSA(byte[] msg){
        if (null == msg)
            return null;
        else {
            Key protector = this.getProtectorRSA();
            if (null != protector)
                return protector.enfoamRSA(this.getAddress(),msg);
            else
                return msg;
        }
    }
    /**
     * Use the protector to defoam using this address as a nonce.
     */
    protected byte[] unprotectRSA( byte[] msg){
        if (null == msg)
            return null;
        else {
            Key protector = this.getProtectorRSA();
            if (null != protector)
                return protector.defoamRSA(this.getAddress(),msg);
            else
                return msg;
        }
    }
    protected Key getKeyRSA(){
        Key[] list = this.list;
        if (null != list){
            for (int cc = 0, count = list.length; cc < count; cc++){
                Key key = list[cc];
                if (key.canCreateKeyRSA())
                    return key;
            }
        }
        return null;
    }
    public boolean has(String alg){
        Key[] list = this.list;
        if (null != list){
            for (int cc = 0, count = list.length; cc < count; cc++){
                Key key = list[cc];
                if (alg.equalsIgnoreCase(key.getAlgorithm()))
                    return true;
            }
        }
        return false;
    }
    protected Key get(String alg){
        Key[] list = this.list;
        if (null != list){
            for (int cc = 0, count = list.length; cc < count; cc++){
                Key key = list[cc];
                if (alg.equalsIgnoreCase(key.getAlgorithm()))
                    return key;
            }
        }
        return null;
    }
    public java.security.KeyPair getKeyPair(String alg){
        Key[] list = this.list;
        if (null != list){
            for (int cc = 0, count = list.length; cc < count; cc++){
                Key key = list[cc];
                if (alg.equalsIgnoreCase(key.getAlgorithm()))
                    return key.getKeyPair();
            }
        }
        return null;
    }
    public Auth addAuth(Authentication type){
        if (null != type){
            Auth auth = this.getAuth(type);
            if (null != auth)
                return auth;
            else {
                auth = new Auth(type);
                this.auths = Auth.List.Add(this.auths,auth);
                return auth;
            }
        }
        return null;
    }
    public Auth addAuth(Authentication type, java.lang.String key, java.lang.String secret){
        if (null != type && type.hasAuthKeyAndSecret()){
            Auth auth = this.getAuth(type);
            if (null != auth){

                if (!auth.equalsKeyAndSecret(key,secret))
                    auth.setKeyAndSecret(key,secret);

                return auth;
            }
            else {
                auth = new Auth(type,key,secret);
                this.auths = Auth.List.Add(this.auths,auth);
                return auth;
            }
        }
        else
            throw new alto.sys.Error.Argument();
    }
    public boolean hasAuth(Authentication type){
        Auth[] auths = this.auths;
        if (null != auths){
            for (int cc = 0, count = auths.length; cc < count; cc++){
                Auth auth = auths[cc];
                if (auth.isType(type))
                    return true;
            }
        }
        return false;
    }
    public Auth getAuth(Authentication type){
        Auth[] auths = this.auths;
        if (null != auths){
            for (int cc = 0, count = auths.length; cc < count; cc++){
                Auth auth = auths[cc];
                if (auth.isType(type))
                    return auth;
            }
        }
        return null;
    }
    public boolean addRole(alto.io.Keys from){
        if (null == from || this.inRole(from))
            return false;
        else if (this.equals(from))
            return false;
        else if (from instanceof Keys){
            Role role = new Role( (Keys)from);
            this.roles = Role.List.Add(this.roles,role);
            return true;
        }
        else
            return false;
    }
    public boolean addRole(Principal.Authentic role){
        return this.addRole(role.getKeys());
    }
    public boolean inRole(alto.io.Role role){
        if (null != role)
            try {
                return this.inRole(role.toString());
            }
            catch (java.io.IOException exc){
                throw new alto.sys.Error.State(exc);
            }
        else
            return false;
    }
    public boolean inRole(Set<alto.io.Role> roleset){
        if (null != roleset){
            Role[] roles = this.roles;
            if (null != roles){
                for (int cc = 0, count = roles.length; cc < count; cc++){
                    Role role = roles[cc];
                    if (role.memberOf(roleset))
                        return true;
                }
            }
        }
        return false;
    }
    public boolean inRole(java.lang.String role)
        throws java.io.IOException
    {
        alto.io.Keys r = Keys.Tools.Dereference(role);
        return this.inRole(r);
    }
    public boolean inRole(alto.io.Keys role){
        if (null != role){
            Role r = this.getRole(role);
            return (null != r && r.authenticate(role));
        }
        else
            return false;
    }
    public boolean inRole(alto.io.Principal.Authentic role){
        if (null != role)
            return this.inRole(role.getKeys());
        else
            return false;
    }
    public Role getRole(alto.io.Keys role){
        return this.getRole(role.getUID());
    }
    public Role getRole(java.lang.String uid){
        if (null != uid){
            Role[] roles = this.roles;
            if (null != roles){
                for (int cc = 0, count = roles.length; cc < count; cc++){
                    Role role = roles[cc];
                    if (role.isUID(uid))
                        return role;
                }
            }
        }
        return null;
    }
    public Role[] listRoles(){
        Role[] roles = this.roles;
        if (null != roles){
            int count = roles.length; 
            Role[] list = new Role[count];
            java.lang.System.arraycopy(roles,0,list,0,count);
            return list;
        }
        else
            return null;
    }
    public PKCS12 createPfx(String alg, String passwd)
        throws java.io.IOException, java.security.cert.CertificateException
    {
        Key key = this.get(alg);
        if (null != key)
            return key.createPfx(passwd);
        else
            throw new java.security.cert.CertificateException(alg);
    }
    public byte[] signSHA1WithRSAO(byte[] msg){
        Key key = this.get("RSA");
        if (null != key)
            return key.signSHA1WithRSAO(msg);
        else
            throw new java.lang.IllegalStateException("RSA");
    }
    public byte[] signWithRSAO(SHA1 hash){
        Key key = this.get("RSA");
        if (null != key)
            return key.signWithRSAO(hash);
        else
            throw new java.lang.IllegalStateException("RSA");
    }
    public byte[] signWithRSA(SHA1 hash){
        Key key = this.get("RSA");
        if (null != key)
            return key.signWithRSA(hash);
        else
            throw new java.lang.IllegalStateException("RSA");
    }
    public byte[] createKeyRSA(Address address){
        Key key = this.get("RSA");
        if (null != key)
            return key.createKeyRSA(address);
        else
            throw new java.lang.IllegalStateException("RSA");
    }
    public java.lang.String signWithRSAHex(SHA1 hash){
        byte[] bytes = this.signWithRSA(hash);
        return alto.io.u.Hex.encode(bytes);
    }
    public Authentication getAuthentication(){
        Authentication scope = this.scopeAuthentication;
        if (null != scope)
            return scope;
        else {
            Principal.Actual scopePrincipal = this.scopePrincipal;
            if (null != scopePrincipal)
                return scopePrincipal.getAuthentication();
            else
                return null;
        }
    }
    public boolean isPushed(){
        return (null != this.scopePrincipal)||(null != this.scopeAuthentication);
    }
    public boolean isPushedPrincipal(){
        return (null != this.scopePrincipal);
    }
    public boolean isPushedAuthentication(){
        return (null != this.scopeAuthentication);
    }
    public Principal.Actual copy(Principal.Actual scopePrincipal){
        if (null != scopePrincipal){
            if (scopePrincipal.isSystem())
                throw new alto.sys.UnauthorizedException();
            else {
                try {
                    Keys copy = (Keys)super.clone();
                    copy.scopePrincipal = scopePrincipal;
                    return copy;
                }
                catch (java.lang.CloneNotSupportedException unexpected){
                    throw new alto.sys.Error.Bug(unexpected);
                }
            }
        }
        else
            throw new alto.sys.Error.Argument();
    }
    public Principal.Actual copy(Authentication scopeAuthentication){
        if (null != scopeAuthentication){
            try {
                Keys copy = (Keys)super.clone();
                copy.scopeAuthentication = scopeAuthentication;
                return copy;
            }
            catch (java.lang.CloneNotSupportedException unexpected){
                throw new alto.sys.Error.Bug(unexpected);
            }
        }
        else
            throw new alto.sys.Error.Argument();
    }
    /**
     * Used by the thread context to exit a previously entered scope.
     * 
     * @return Previous scope (or this if none)
     */
    public Principal.Actual pop(){
        Principal.Actual scope = this.scopePrincipal;
        if (null != scope){
            this.destroy();
            return scope;
        }
        else
            return this;
    }
    /**
     * Used by the thread context to enter the scope of the protector.
     * 
     * @return Protector (application / system) scope prepared to pop
     * back into this scope
     */
    public Principal.Actual push(){
        Principal.Actual scope = this.getProtector();
        if (null != scope && scope.isNotSystem()){
            Principal.Actual scopeCopy = scope.copy(this);
            return scopeCopy;
        }
        else
            return this;
    }
    /**
     * Used by the thread context to enter an authenticated scope.
     * 
     * @return Copy of this in authenticated scope
     */
    public Principal.Actual push(Authentication scopeAuthentication){
        if (null != scopeAuthentication){
            Principal.Actual thisCopy = this.copy(scopeAuthentication);
            return thisCopy;
        }
        else
            throw new alto.sys.Error.Bug();
    }
}
