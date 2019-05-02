package com.example.asus.seniorassistant;

import java.util.Map;

public class NoteModel {
    public String noteTitle;
    public String noteTime;
    public String image;

    public NoteModel(){}

    public NoteModel(String noteTitle, String noteTime, String image) {
        this.noteTitle = noteTitle;
        this.noteTime = noteTime;
        this.image = image;
    }

    public String getNoteImage() {
        return image;
    }

    public void setNoteImage(String noteImage) {
        this.image = noteImage;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
