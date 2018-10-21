package io.github.dhananjaytrivedi.wikepediasearch.Model;

public class WikiResult {
    String pageID;
    String title;
    String imageURL;
    String description;


    public WikiResult() {
    }

    public String getPageID() {
        return pageID;
    }

    public void setPageID(String pageID) {
        this.pageID = pageID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this)
            return true;
        if (!(o instanceof WikiResult))
            return false;

        return (this.pageID.equals(((WikiResult) o).pageID));
    }
}
