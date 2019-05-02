package com.example.asus.seniorassistant;

public class AudioNoteModel {
    public String noteTitle;
    public String noteTime;
    public String duration;

    public AudioNoteModel(){}

    public AudioNoteModel(String noteTitle, String noteTime, String duration) {
        this.noteTitle = noteTitle;
        this.noteTime = noteTime;
        this.duration = duration;


    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteTime() {
        return noteTime;
    }

    public void setNoteTime(String noteTime) {
        this.noteTime = noteTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
