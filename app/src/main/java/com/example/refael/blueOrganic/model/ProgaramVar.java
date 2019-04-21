package com.example.refael.blueOrganic.model;

import java.util.ArrayList;

public class ProgaramVar {

     String programname;
     int starthour;
     int startmin;
     int duration;
     ArrayList<String> selection = new ArrayList<String>();

    public String getProgramname() {
        return programname;
    }

    public void setProgramname(String programname) {
        this.programname = programname;
    }

    public int getStarthour() {
        return starthour;
    }

    public void setStarthour(int starthour) {
        this.starthour = starthour;
    }

    public int getStartmin() {
        return startmin;
    }

    public void setStartmin(int startmin) {
        this.startmin = startmin;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public ArrayList<String> getSelection() {
        return selection;
    }

    public void setSelection(ArrayList<String> selection) {
        this.selection = selection;
    }

    @Override
        public String toString() {
            return
                    "Progaram Name :" + programname + "\n" +
                    "Time:" + starthour + ":" + startmin + "   Dutation : " + duration + "\n"
                    + selection;
    }

    public ProgaramVar() {
    }

    public ProgaramVar(String programname, int starthour, int startmin, int duration, ArrayList<String> selection) {
        this.programname = programname;
        this.starthour = starthour;
        this.startmin = startmin;
        this.duration = duration;
        this.selection = selection;
    }
}

