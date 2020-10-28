package com.gubadev.soaapp.dto;

public class Score {

    private Integer score;

    private Integer time;

    private String nameGamer;

    private String date;

    public Score(Integer score, Integer time, String nameGamer, String date) {
        this.score = score;
        this.time = time;
        this.nameGamer = nameGamer;
        this.date = date;
    }

    public Integer getScore() {
        return score;
    }

    public Integer getTime() {
        return time;
    }

    public String getNameGamer() {
        return nameGamer;
    }

    public String getDate() {
        return date;
    }
}
