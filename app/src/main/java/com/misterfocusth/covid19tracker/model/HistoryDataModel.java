package com.misterfocusth.covid19tracker.model;

public class HistoryDataModel {

    public String day, date, newConfirmed, newRecovered, newDeaths, confirmed, recovered, deaths;

    public HistoryDataModel() {
    }

    public HistoryDataModel(String day, String date, String newConfirmed, String newRecovered, String newDeaths, String confirmed, String recovered, String deaths) {
        this.day = day;
        this.date = date;
        this.newConfirmed = newConfirmed;
        this.newRecovered = newRecovered;
        this.newDeaths = newDeaths;
        this.confirmed = confirmed;
        this.recovered = recovered;
        this.deaths = deaths;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNewConfirmed() {
        return newConfirmed;
    }

    public void setNewConfirmed(String newConfirmed) {
        this.newConfirmed = newConfirmed;
    }

    public String getNewRecovered() {
        return newRecovered;
    }

    public void setNewRecovered(String newRecovered) {
        this.newRecovered = newRecovered;
    }

    public String getNewDeaths() {
        return newDeaths;
    }

    public void setNewDeaths(String newDeaths) {
        this.newDeaths = newDeaths;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }

    public String getRecovered() {
        return recovered;
    }

    public void setRecovered(String recovered) {
        this.recovered = recovered;
    }

    public String getDeaths() {
        return deaths;
    }

    public void setDeaths(String deaths) {
        this.deaths = deaths;
    }
}

//data class HistoryDataModel(
//        val day: String,
//        val date: String,
//        val newConfirmed: String,
//        val newRecovered: String,
//        val newDeaths: String,
//        val confirmed: String,
//        val recovered: String,
//        val deaths: String
//        ) {
//        }
//
////@SerializedName("Date") val date: String,
////@SerializedName("NewConfirmed") val newConfirmed: String,
////@SerializedName("NewRecovered") val newRecovered: String,
////@SerializedName("NewDeaths") val newDeaths: String,
////@SerializedName("Confirmed") val confirmed: String,
////@SerializedName("Recovered") val recovered: String,
////@SerializedName("Deaths") val deaths: String
