package lbconsulting.com.backendlesstest1.database;

import android.content.Context;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import lbconsulting.com.backendlesstest1.classes.CommonMethods;
import lbconsulting.com.backendlesstest1.classes.MyLog;

/**
 * Backendless object for an A1List Theme.
 */

public class ListTheme {

    private String objectId;
    private String name;
    private int startColor; // int
    private int endColor;
    private int textColor; // int
    private float textSize; //float
    private float horizontalPaddingInDp; //float dp. Need to convert to float px
    private float verticalPaddingInDp; //float dp. Need to convert to float px
    private boolean dirty;
    private boolean bold;
    private boolean checked;
    private boolean defaultTheme;
    private boolean markedForDeletion;
    private boolean transparent;
    private String uuid;
    private Date updated;
    private Date created;

    private static final int PAGE_SIZE = 99;

    public ListTheme() {
        // A default constructor is required.
    }


    //region Getters and Setters

    public int getEndColor() {
        return endColor;
    }

    public void setEndColor(int endColor) {
        setDirty(true);
        this.endColor = endColor;
    }

    public float getHorizontalPaddingInDp() {
        return horizontalPaddingInDp;
    }

    public void setHorizontalPaddingInDp(float horizontalPaddingInDp) {
        setDirty(true);
        this.horizontalPaddingInDp = horizontalPaddingInDp;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean isThemeDirty) {
        this.dirty = isThemeDirty;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean isBold) {
        setDirty(true);
        this.bold = isBold;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean isChecked) {
        setDirty(true);
        this.checked = isChecked;
    }

    public boolean isDefaultTheme() {
        return defaultTheme;
    }

    public void setDefaultTheme(boolean isDefaultTheme) {
        setDirty(true);
        this.defaultTheme = isDefaultTheme;
    }

    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }

    public void setMarkedForDeletion(boolean isMarkedForDeletion) {
        setDirty(true);
        this.markedForDeletion = isMarkedForDeletion;
    }

    public boolean isTransparent() {
        return transparent;
    }

    public void setTransparent(boolean isTransparent) {
        setDirty(true);
        this.transparent = isTransparent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        setDirty(true);
        this.name = name;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public int getStartColor() {
        return startColor;
    }

    public void setStartColor(int startColor) {
        setDirty(true);
        this.startColor = startColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        setDirty(true);
        this.textColor = textColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        setDirty(true);
        this.textSize = textSize;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        setDirty(true);
        this.uuid = uuid;
    }

    public float getVerticalPaddingInDp() {
        return verticalPaddingInDp;
    }

    public void setVerticalPaddingInDp(float verticalPaddingInDp) {
        setDirty(true);
        this.verticalPaddingInDp = verticalPaddingInDp;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

 /*   private float horizontalPaddingInDp; //float dp. Need to convert to float px
    private float verticalPaddingInDp; //float dp. Need to convert to float px
    private boolean dirty;
    private boolean bold;
    private boolean checked;
    private boolean defaultTheme;
    private boolean markedForDeletion;
    private boolean transparent;
    private String uuid;
    private Date updated;
    private Date created;*/

    @Override
    public String toString() {
        DateFormat  mDateFormat = DateFormat.getDateTimeInstance();
        String result = "Name: " + getName() +"\n"
                + "ObjectId = " + getObjectId() + "\n"
                + "Uuid = " + getUuid() + "\n"
                + "Start Color = " + getStartColor() + "\n"
                + "End Color = " + getEndColor() + "\n"
                + "Text Color = " + getTextColor() + "\n"
                + "Text Size = " + getTextSize() + "\n"

                + "horizontalPaddingInDp = " + getHorizontalPaddingInDp() + "\n"
                + "verticalPaddingInDp = " + getVerticalPaddingInDp() + "\n"
                + "dirty = " + isDirty() + "\n"
                + "bold = " + isBold() + "\n"
                + "checked = " + isChecked() + "\n"
                + "defaultTheme = " + isDefaultTheme() + "\n"
                + "markedForDeletion = " + isMarkedForDeletion() + "\n"
                + "transparent = " + isTransparent() + "\n"
                + "date/time modified = " + mDateFormat.format(getUpdated()) +"\n\n"

                ;


        return result;
    }


    //endregion

    public static ListTheme newInstance(String newThemeName,
                                             int startColor, int endColor,
                                             int textColor, float textSize,
                                             float horizontalPaddingInDp, float verticalPaddingInDp,
                                             boolean isBold, boolean isTransparent, boolean isDefaultTheme) {

        ListTheme newTheme = new ListTheme();
        newTheme.setName(newThemeName);
        newTheme.setStartColor(startColor);
        newTheme.setEndColor(endColor);
        newTheme.setTextColor(textColor);
        newTheme.setTextSize(textSize);
        newTheme.setHorizontalPaddingInDp(horizontalPaddingInDp);
        newTheme.setVerticalPaddingInDp(verticalPaddingInDp);
        newTheme.setBold(isBold);
        newTheme.setChecked(false);
        newTheme.setDefaultTheme(isDefaultTheme);
        newTheme.setMarkedForDeletion(false);
        newTheme.setTransparent(isTransparent);
        String newUuid = UUID.randomUUID().toString();
        // replace uuid "-" with "_" to distinguish it from Backendless objectId
        newUuid = newUuid.replace("-", "_");
        newTheme.setUuid(newUuid);

        return newTheme;
    }

    private static int index = 0;

    public static ListTheme getDefaultTheme() {

//        List<ListTheme> listOfTheme = getListOfTheme();
        // TODO: implement getDefaultTheme
        ListTheme Theme = getListOfTheme().get(index);
        index++;
        if (index == getListOfTheme().size()) {
            index = 0;
        }
        return Theme;

    }

    private static List<ListTheme> mListOfTheme;

    public static List<ListTheme> getListOfTheme() {
        return mListOfTheme;
    }

    public static void getAllThemeFromBackendlessAsync(final Context context) {
        mListOfTheme = new ArrayList<>();
//        long startTime = System.currentTimeMillis();

        final AsyncCallback<BackendlessCollection<ListTheme>> callback = new AsyncCallback<BackendlessCollection<ListTheme>>() {
            @Override
            public void handleResponse(BackendlessCollection<ListTheme> foundTheme) {

                List<ListTheme> listOfTheme = foundTheme.getData();
                mListOfTheme.addAll(listOfTheme);
                int size = foundTheme.getCurrentPage().size();
                MyLog.i("ListTheme", "getAllListTitlesFromBackendlessAsync(): Loaded " + size + " Theme in the current page");

                if (size > 0) {
                    foundTheme.nextPage(this);
                } else {
                    // all done
                    String msg = "All Theme retrieved. Total number of Theme = " + mListOfTheme.size();
                    MyLog.i("ListTheme", "getAllListTitlesFromBackendlessAsync(): " + msg);
                    CommonMethods.showOkDialog(context, "All Theme", msg);
//                    latch.countDown();
                }
                // TODO: 2/9/2016 Replace all Theme in the SQLite database
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                // an error has occurred, the error code can be retrieved with fault.getCode()
                MyLog.e("ListTheme", "getAllThemeFromBackendlessAsync(): BackendlessFault: code: "
                        + fault.getCode() + ": " + fault.getMessage());
            }
        };
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setPageSize(PAGE_SIZE);
        QueryOptions queryOptions = new QueryOptions();
        queryOptions.addSortByOption("nameLowercase ASC");
        dataQuery.setQueryOptions(queryOptions);
        Backendless.Data.of(ListTheme.class).find(dataQuery, callback);
    }
}
