package com.example.repairbrain20;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserData {

    User user = null;
    String uid,lastly_noted_change,lastly_noted_side_effect,lastly_relapsed,next_step;
    Map<String, Map<String,List<Integer>>> accuracy_plot = new HashMap<>();
    Map<String,Map<String,String>> lastly_opened = new HashMap<>();
    Map<String,Map<String,String>> negative_effects;
    List<String> negative_effects_list;
    int last_accuracy_percent;
    UserData()
    {

    }

    UserData(User user)
    {
        this.user = user;
        this.uid = user.username;
        lastly_noted_change = lastly_noted_side_effect = lastly_relapsed = next_step = "Not Found";
    }
}
