package com.appmagnet.fintaskanyplace.core;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by anmolgupta on 11/11/15.
 */


public class TitleClassifier {

    static String[] titleNames = {"buy", "shop", "purchase", "shopping list", "get", "pick up",
            "pick", "take"};

    public static Boolean isTitleGrocery(String title) {
        if(title == null)
            return false;
        title = title.toLowerCase();
        if(title.contains("grocery") || title.contains("groceries"))
            return true;
        return false;
    }

    public static Boolean isNoteRequired(String title) {
        if(title == null)
            return false;
        ArrayList<String> titleMatchList = new ArrayList<>(Arrays.asList(titleNames));
        title = title.toLowerCase();
        for (String titleName: titleMatchList) {
            if(title.contains(titleName)) {
                return true;
            }
        }
        return false;
    }


}
