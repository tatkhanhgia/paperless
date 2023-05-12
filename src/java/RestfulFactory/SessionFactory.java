package RestfulFactory;

import restful.sdk.API.IServerSession;
import restful.sdk.API.ISessionFactory;
import restful.sdk.API.IUserSession;
import restful.sdk.API.Property;
import RestfulFactory.ServerSession;
import java.util.HashMap;

public class SessionFactory implements ISessionFactory {

    private static HashMap<String, ISessionFactory> listServerSession = new HashMap<String, ISessionFactory>();

    private IServerSession serverSession;

    private Property prop;
    private String lang;

    private SessionFactory(Property prop, String lang) {
        this.prop = prop;
        this.lang = lang;
    }

    public static ISessionFactory getInstance(Property prop, String lang) throws Throwable {
        String key = prop.getBaseUrl() + prop.getRelyingParty();
        if (listServerSession.containsKey(key)) {
            return listServerSession.get(key);
        }
        ISessionFactory factory = new SessionFactory(prop, lang);
        ((SessionFactory) factory).serverSession = new ServerSession(prop, lang);

        listServerSession.put(key, factory);
        return factory;
    }

    public IServerSession getServerSession() {
        //ServerSession serverSession = new ServerSession(this.prop, this.lang);
        return serverSession;
    }

    public IUserSession newUserSession(String username, String password) throws Throwable {
        UserSession userSession = new UserSession(prop, lang, username, password);
        return userSession;
    }

}
