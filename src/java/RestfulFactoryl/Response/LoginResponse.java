package RestfulFactoryl.Response;

public class LoginResponse extends Response {

    private String accessToken;
    private String refreshToken;
    private int remainingCounter;
    private int tempLockoutDuration;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public int getRemainingCounter() {
        return remainingCounter;
    }

    public void setRemainingCounter(int remainingCounter) {
        this.remainingCounter = remainingCounter;
    }

    public int getTempLockoutDuration() {
        return tempLockoutDuration;
    }

    public void setTempLockoutDuration(int tempLockoutDuration) {
        this.tempLockoutDuration = tempLockoutDuration;
    }
}
