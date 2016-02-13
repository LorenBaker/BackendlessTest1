package lbconsulting.com.backendlesstest1.database;


import android.content.Context;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessException;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import lbconsulting.com.backendlesstest1.classes.CommonMethods;
import lbconsulting.com.backendlesstest1.classes.MyLog;

/**
 * Backendless object for an A1List Table.
 */

public class ListTitle {

    private static final String LIST_NOT_LOCK = "listNotLocked";

    private String objectId;
    private boolean checked;
    private boolean forceViewInflation;
    private boolean listLocked;
    private boolean dirty;
    private boolean markedForDeletion;
    private boolean sortAlphabetically;
    private ListTheme listTheme;
    private long manualSortKey;
    private String listLockString;
    private String name;
    private String uuid;
    private Date updated;

    private static final int PAGE_SIZE = 99;


    public ListTitle() {
        // A default constructor is required.
    }

    //region Getters and Setters

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean isChecked) {
        setDirty(true);
        this.checked = isChecked;
    }

    public boolean isForceViewInflation() {
        return forceViewInflation;
    }

    public void setForceViewInflation(boolean isForceViewInflation) {
        setDirty(true);
        this.forceViewInflation = isForceViewInflation;
    }

    public boolean isListLocked() {
        return listLocked;
    }

    public void setListLocked(boolean isListLocked) {
        setDirty(true);
        this.listLocked = isListLocked;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean isListTitleDirty) {
        this.dirty = isListTitleDirty;
    }

    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }

    public void setMarkedForDeletion(boolean isMarkedForDeletion) {
        setDirty(true);
        this.markedForDeletion = isMarkedForDeletion;
    }

    public ListTheme getListTheme() {
        return listTheme;
    }

    public void setTheme(ListTheme listTheme) {
        setDirty(true);
        this.listTheme = listTheme;
    }

    public String getListLockString() {
        return listLockString;
    }

    public void setListLockString(String listLockString) {
        setDirty(true);
        this.listLockString = listLockString;
    }

    public long getManualSortKey() {
        return manualSortKey;
    }

    public void setManualSortKey(long manualSortKey) {
        setDirty(true);
        this.manualSortKey = manualSortKey;
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

    public boolean isSortAlphabetically() {
        return sortAlphabetically;
    }

    public void setSortAlphabetically(boolean isSortListItemsAlphabetically) {
        setDirty(true);
        this.sortAlphabetically = isSortListItemsAlphabetically;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        setDirty(true);
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return getName();
    }

    public static long getNextManualSortKey() {
        // TODO: 2/8/2016 implement method
        return 999999999l;
//        return mManualSortKey;
    }
    //endregion

    public static ListTitle newInstance(String newListName) {
        ListTitle newListTitle = new ListTitle();

        try {
            newListTitle.setName(newListName);
            ListTheme defaultTheme = ListTheme.getDefaultTheme();
            newListTitle.setTheme(defaultTheme);
            String newUuid = UUID.randomUUID().toString();
            // replace uuid "-" with "_" to distinguish it from Backendless objectId
            newUuid = newUuid.replace("-", "_");
            newListTitle.setUuid(newUuid);
            newListTitle.setChecked(false);
            newListTitle.setMarkedForDeletion(false);
            newListTitle.setSortAlphabetically(true);
            newListTitle.setForceViewInflation(false);
            newListTitle.setManualSortKey(getNextManualSortKey());
            newListTitle.setListLocked(false);
            newListTitle.setListLockString(LIST_NOT_LOCK);

        } catch (Exception e) {
            MyLog.e("ListTitle", "newInstance(): Exception: " + e.getMessage());
        }

        return newListTitle;
    }

    public static ListTitle saveToBackendless(ListTitle listTitle) {
        // saveToBackendless object synchronously
        listTitle.setDirty(false);
        ListTitle result = null;
        try {
            result = Backendless.Data.of(ListTitle.class).save(listTitle);
            // TODO: If new listTitle, update SQLite db with objectID
            // TODO: Set dirty flag to false in SQLite db
        } catch (BackendlessException e) {
            listTitle.setDirty(true);
            // TODO: Set dirty flag to true in SQLite db
            MyLog.e("ListTitle", "saveToBackendless(): BackendlessException: " + e.getMessage());
        }
        return result;
    }

    public static void saveToBackendlessAsync(final ListTitle listTitle) {
        // saveToBackendless object asynchronously
        listTitle.setDirty(false);
        Backendless.Persistence.save(listTitle, new AsyncCallback<ListTitle>() {
            public void handleResponse(ListTitle response) {
                // new Contact instance has been saved
                // TODO: If new listTitle, update SQLite db with objectID
                // TODO: Set dirty flag to false in SQLite db
            }

            public void handleFault(BackendlessFault fault) {
                // an error has occurred, the error code can be retrieved with fault.
                listTitle.setDirty(true);
                // TODO: Set dirty flag to true in SQLite db
                MyLog.e("ListTitle", "handleFault(): BackendlessFault: ListTitleName: " + listTitle.getName()
                        + " code: " + fault.getCode() + ": " + fault.getMessage());
            }
        });

    }

    private static List<ListTitle> mListOfListTitles;

    public static List<ListTitle> getListOfListTitles() {
        return mListOfListTitles;
    }

    public static void getAllListTitlesFromBackendlessAsync(final Context context) {
        mListOfListTitles = new ArrayList<>();

        final AsyncCallback<BackendlessCollection<ListTitle>> callback = new AsyncCallback<BackendlessCollection<ListTitle>>() {
            @Override
            public void handleResponse(BackendlessCollection<ListTitle> foundListTitles) {

                List<ListTitle> listOfListTitles = foundListTitles.getData();
                mListOfListTitles.addAll(listOfListTitles);
                int size = foundListTitles.getCurrentPage().size();
                MyLog.i("ListTitle", "getAllListTitlesFromBackendlessAsync(): Loaded " + size + " ListTitles in the current page");

                if (size > 0) {
                    foundListTitles.nextPage(this);
                } else {
                    // all done
                    String msg = "All ListTitles retrieved. Total number of ListTitles = " + mListOfListTitles.size();
                    MyLog.i("ListTitle", "getAllListTitlesFromBackendlessAsync(): " + msg);
                    CommonMethods.showOkDialog(context, "All ListTitles", msg);
//                    latch.countDown();
                }
                // TODO: 2/9/2016 Replace all ListTitles in the SQLite database
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                // an error has occurred, the error code can be retrieved with fault.getCode()
                MyLog.e("ListTitle", "getAllListTitlesFromBackendlessAsync(): BackendlessFault: code: "
                        + fault.getCode() + ": " + fault.getMessage());

            }
        };
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setPageSize(PAGE_SIZE);
        QueryOptions queryOptions = new QueryOptions();
        queryOptions.addSortByOption("nameLowercase ASC");
        dataQuery.setQueryOptions(queryOptions);
        Backendless.Data.of(ListTitle.class).find(dataQuery, callback);
    }
}
