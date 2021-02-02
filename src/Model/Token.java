package Model;

import java.io.Serializable;

public class Token implements Serializable {

    private volatile boolean token;

    public Token(){
        token = true;
    }

    public Token(boolean value){
        token = value;
    }

    public boolean getTokenValue() {
        return token;
    }

    public void setTokenValue(boolean token){
        this.token = token;
    }
}
