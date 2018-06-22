package com.example.adria.fatecdontpad;

public class Tag {

    private String text;


    public Tag (String text){
        setText(text);
    }

    public Tag(){

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
