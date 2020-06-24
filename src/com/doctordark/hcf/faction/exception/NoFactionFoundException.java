package com.doctordark.hcf.faction.exception;

public class NoFactionFoundException extends RuntimeException{

    public NoFactionFoundException(String name){
        super("No faction found: " + name);
    }
}
