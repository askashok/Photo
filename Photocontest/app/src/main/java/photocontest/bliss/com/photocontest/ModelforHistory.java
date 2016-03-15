package photocontest.bliss.com.photocontest;

import java.util.ArrayList;

/**
 * Created by BLT0059 on 6/15/2015.
 */
public class ModelforHistory {

String dates;
String path_urls;
    String compare_date;
    String date_text;
boolean isHeader=false;
ArrayList<String> urls;
    ArrayList<String> date_list;

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public ArrayList<String> getUrls() {
        return urls;
    }

    public void setUrls(ArrayList<String> urls) {
        this.urls = urls;
    }

    public String getPath_urls() {
        return path_urls;
    }

    public void setPath_urls(String path_urls) {
        this.path_urls = path_urls;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean isHeader) {
        this.isHeader = isHeader;
    }

    public String getCompare_date() {
        return compare_date;
    }

    public void setCompare_date(String compare_date) {
        this.compare_date = compare_date;
    }

    public ArrayList<String> getDate_list() {
        return date_list;
    }

    public void setDate_list(ArrayList<String> date_list) {
        this.date_list = date_list;
    }

    public String getDate_text() {
        return date_text;
    }

    public void setDate_text(String date_text) {
        this.date_text = date_text;
    }
}
