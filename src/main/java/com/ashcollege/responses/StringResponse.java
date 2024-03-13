package com.ashcollege.responses;

public class StringResponse  extends  BasicResponse{
    private  String string;

    public StringResponse() {
    }

    public StringResponse(boolean success, Integer errorCode , String string) {
        super(success, errorCode);
        this.string=string;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
