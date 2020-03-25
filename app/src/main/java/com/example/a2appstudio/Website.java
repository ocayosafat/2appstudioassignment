package com.example.a2appstudio;

public class Website {
    private String Title;
    private String Link;
    private String Image;

    public Website() {
    }

    public Website(String title, String link, String image) {
        Title = title;
        Link = link;
        Image = image;
    }

    // Getter

    public String getTitle() {
        return Title;
    }

    public String getLink() {
        return Link;
    }

    public String getImage() {
        return Image;
    }

    // Setter

    public void setTitle(String title) {
        Title = title;
    }

    public void setLink(String link) {
        Link = link;
    }

    public void setImage(String image) {
        Image = image;
    }
}
