package com.evg.simsend.configuration.configurationclasses;

import com.evg.simsend.simsenddata.Operator;
import com.evg.simsend.simsenddata.SimCard;

import java.util.ArrayList;
import java.util.List;

public class GlobalConfiguration {
    public String email = "test@test.com";
    public String emailFrom = "test@test.com";
    public String phoneForSendSmsNoBlock = "+79991111111";
    public List<SimCard> simCards = new ArrayList<>();

    public GlobalConfiguration(){
        simCards.add(SimCard.init("+79991111111", Operator.MTS, "89700010050360032119"));
        simCards.add(SimCard.init("+79992222222", Operator.MTS, "89700010050360033119"));
    }
}