package com.github.solarbeam.cleanzone;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmedPatient { //TODO Generic, T는 도시
    static private HashMap<String, Boolean> safezone = new HashMap<>();
    private Calendar confirmedDate;
    private String homeDistrict;
    private ArrayList<CleanZone> cleanZoneList;

    ConfirmedPatient(Calendar date, String home, ArrayList<CleanZone> list){
        this.confirmedDate = date;
        this.homeDistrict = home;
        this.cleanZoneList = list;
    }

    //TODO Elements 2 CleanZone
    ConfirmedPatient(String date, String home, Elements list){
        this.confirmedDate = string2CalendarFormatter(date);
        this.homeDistrict = home;
        this.cleanZoneList = null;
    }

    public static ArrayList<ConfirmedPatient> getConfirmedPatient(Elements doc){
        ArrayList<ConfirmedPatient> patientlist = new ArrayList<>();
        int idxDate = -1;
        int idxHome = -1;
        int idxType = -1;
        {
            ArrayList<String> tablehead = new ArrayList<>();
            Elements th = doc.select("div[class=list_head] ul li");
            for(Element h : th) tablehead.add(h.text());
            for(int i=0; i<tablehead.size(); i++){
                if(tablehead.get(i).equals("확진일자")) idxDate = i;
                else if(tablehead.get(i).equals("거주지")) idxHome = i;
                else if(tablehead.get(i).equals("감염경로")) idxType = i;
            }
        }

        Elements d = doc.select("ul[class$=active]");
        for(Element patient : d){
            Elements info = patient.select("li>span");
            ArrayList<String> plist = new ArrayList<>();
            for(Element i : info) plist.add(i.text());
            if(plist.get(idxType).equals("해외유입")) continue;
            Elements result = patient.select("li[class=result]");
            ConfirmedPatient cp = new ConfirmedPatient(plist.get(idxDate), plist.get(idxHome), result);
            if((Calendar.getInstance().getTimeInMillis() - cp.confirmedDate.getTimeInMillis())/(24*60*60*1000) > 7) break;
            else patientlist.add(cp);
        }

        return patientlist;
    }

    public static HashMap<String, Boolean> getSafeZone(ArrayList<ConfirmedPatient> cpList, String city){
        HashMap<String, Boolean> zonelist = new HashMap<String, Boolean>();
        if(city.equals("부산")){
            //"중구","서구","동구","영도구","부산진구","동래구","남구","북구", "해운대구", "사하구", "금정구","강서구","연제구","수영구","사상구","기장군","기타"
            zonelist.put("중구", true);
            zonelist.put("서구", true);
            zonelist.put("동구", true);
            zonelist.put("영도구", true);
            zonelist.put("부산진구", true);
            zonelist.put("동래구", true);
            zonelist.put("남구", true);
            zonelist.put("북구", true);
            zonelist.put("해운대구", true);
            zonelist.put("사하구", true);
            zonelist.put("금정구", true);
            zonelist.put("강서구", true);
            zonelist.put("연제구", true);
            zonelist.put("수영구", true);
            zonelist.put("사상구", true);
            zonelist.put("기장군", true);
        }

        for(ConfirmedPatient cp : cpList){
            //System.out.println(cp.toString());
            if(zonelist.containsKey(cp.homeDistrict)) zonelist.put(cp.homeDistrict, false);
            //else System.out.println(cp.toString()); //TODO HASH KEY 일치하지 않는 경우의 처리*/
        }

        return zonelist;
    }

    @Override
    public String toString() {
        return (confirmedDate.get(Calendar.MONTH)+1) + "." + (confirmedDate.get(Calendar.DATE)+1) + ". " + homeDistrict;
    }

    private Calendar string2CalendarFormatter(String str){
        Calendar c = Calendar.getInstance();
        String[] slist = str.split("\\.");
        if(slist.length < 2) return c;
        else if(slist[0].trim()=="" || slist[1].trim()=="") return c; //TODO 예외처리좀 깔끔하게 해보자
        c.set(Calendar.MONTH, Integer.parseInt(slist[0].trim()) - 1);
        c.set(Calendar.DATE, Integer.parseInt(slist[1].trim()) - 1);
        return c;
    }
}
