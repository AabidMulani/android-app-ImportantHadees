package in.abmulani.importanthadees.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by AABID on 09-11-2014.
 */
public class HadeesResponse {

    @SerializedName("row_count")
    @Expose
    private String rowCount;
    @Expose
    private String title;
    @Expose
    private String description;
    @Expose
    private String reference;
    @SerializedName("created_on")
    @Expose
    private String createdOn;

    public String getRowCount() {
        return rowCount;
    }

    public void setRowCount(String rowCount) {
        this.rowCount = rowCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }
}
