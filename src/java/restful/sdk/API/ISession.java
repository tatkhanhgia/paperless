package restful.sdk.API;

public interface ISession {
    
    boolean close() throws Throwable;
    void login() throws Throwable;
    
}
