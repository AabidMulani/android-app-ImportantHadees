package in.abmulani.importanthadees.database;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;

import com.orm.SugarRecord;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import in.abmulani.importanthadees.models.HadeesResponse;

/**
 * Created by AABID on 10-11-2014.
 */
public class HadeesTable extends SugarRecord<HadeesTable> implements Parcelable {

    private String rowCount;
    private String title;
    private String description;
    private String reference;
    private String createdOn;
    private boolean read;

    public HadeesTable() {
        super();
    }

    public HadeesTable(HadeesResponse hadeesResponse) {
        super();
        this.rowCount = hadeesResponse.getRowCount();
        this.title = Html.fromHtml(hadeesResponse.getTitle()).toString();
        this.description = Html.fromHtml(hadeesResponse.getDescription()).toString();
        this.reference = Html.fromHtml(hadeesResponse.getReference()).toString();
        this.createdOn = hadeesResponse.getCreatedOn();
        this.read = false;
    }

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

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public static void storeAllThisHadees(List<HadeesResponse> list) {
        for (HadeesResponse response : list) {
            if (!hasThisRowCount(response.getRowCount())) {
                HadeesTable table = new HadeesTable(response);
                table.save();
            }
        }
    }

    public static boolean hasThisRowCount(String rowCount) {
        List<HadeesTable> tableList = HadeesTable.find(HadeesTable.class, "row_count = ?", rowCount);
        if (tableList != null) {
            if (tableList.size() > 0) {
                return true;
            }
        }
        return false;
    }

    public static List<HadeesTable> getAllHadees() {
        List<HadeesTable> tableList = HadeesTable.listAll(HadeesTable.class);
        if (tableList != null) {
            if (tableList.size() > 0) {
                Collections.sort(tableList, hadeesTableComparator);
                return tableList;
            }
        }
        return null;
    }

    public static void setThisAsRead(String rowCount, boolean isRead) {
        List<HadeesTable> tableList = HadeesTable.find(HadeesTable.class, "row_count = ?", rowCount);
        if (tableList != null) {
            if (tableList.size() > 0) {
                tableList.get(0).setRead(isRead);
                tableList.get(0).save();
            }
        }
    }

    private static Comparator<HadeesTable> hadeesTableComparator = new Comparator<HadeesTable>() {
        @Override
        public int compare(HadeesTable object1, HadeesTable object2) {
            return object2.getRowCount().compareTo(object1.getRowCount());
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.rowCount);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.reference);
        dest.writeString(this.createdOn);
        dest.writeByte(read ? (byte) 1 : (byte) 0);
    }

    private HadeesTable(Parcel in) {
        this.rowCount = in.readString();
        this.title = in.readString();
        this.description = in.readString();
        this.reference = in.readString();
        this.createdOn = in.readString();
        this.read = in.readByte() != 0;
    }

    public static final Parcelable.Creator<HadeesTable> CREATOR = new Parcelable.Creator<HadeesTable>() {
        public HadeesTable createFromParcel(Parcel source) {
            return new HadeesTable(source);
        }

        public HadeesTable[] newArray(int size) {
            return new HadeesTable[size];
        }
    };
}
