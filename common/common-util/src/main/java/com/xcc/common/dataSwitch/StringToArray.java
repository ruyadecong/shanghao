package com.xcc.common.dataSwitch;

import java.util.HashMap;
import java.util.Map;

public class StringToArray {

    public static String[] toArray(String str,char ch){
        int num=0;
        int m,n;
        String str1,str2;
        str1=str;
        n=str.length();

        for (int i = 0; i < n; i++) {
            if (str.charAt(i) == ch){
            num =num +1;
            }
        }

        String standard[] = new String[num+1];

        for (int i = 0; i < num; i++) {
            m = str1.indexOf(ch);
            str2=str1.substring(0,m);
            standard[i]=str2;
            str1=str1.substring(m+1,str1.length());
        }
        standard[num]=str1;

        return standard;
    }

    public static String[] toArray(String str,String chs){
        int num=0;
        int m,n;
        String str1,str2;
        str1=str;
        n=str.length();

        for (int i = 0; i < n; i++) {
            if (str.startsWith(chs,i)){
                num =num +1;
            }
        }

        String standard[] = new String[num+1];

        for (int i = 0; i < num; i++) {
            m = str1.indexOf(chs);
            str2=str1.substring(0,m);
            standard[i]=str2;
            str1=str1.substring(m+3,str1.length());
        }
        standard[num]=str1;

        return standard;
    }



}
