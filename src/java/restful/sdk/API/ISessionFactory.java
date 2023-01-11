package restful.sdk.API;

public interface ISessionFactory {

    //auth/login
    IServerSession getServerSession() throws Throwable;

    //auth/login
    IUserSession newUserSession(String username, String password) throws Throwable;
}
