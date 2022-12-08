package com.evg.simsend.configuration.configurationclasses;

import com.evg.simsend.simsenddata.SimCard;

import java.util.ArrayList;
import java.util.List;

public class GlobalConfiguration {
    public String email = "";
    public String phoneForSendSmsNoBlock = "";
    public List<SimCard> simCards = new ArrayList<>();
}