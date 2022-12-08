package com.evg.simsend.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonPrettyFactory {

    public Gson create() {
        return new GsonBuilder().setPrettyPrinting().create();
    }
}
