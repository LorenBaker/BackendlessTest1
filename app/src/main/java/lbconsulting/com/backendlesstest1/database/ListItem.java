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
 * Backendless object for an A1List Item.
 */


public class ListItem {

    private String objectId;
    private String name;
    private ListTitle listTitle;
    private ListTheme listAttributes;
    private long manualSortKey;
    private boolean checked;
    private boolean favorite;
    private boolean dirty;
    private boolean markedForDeletion;
    private boolean struckOut;
    private String uuid;
    private Date updated;

    private static final int PAGE_SIZE = 99;

    public ListItem() {
        // A default constructor is required.
    }

    public static long getNextManualSortKey() {
        // TODO: implement getNextManualSortKey
        return 999999999l;
//        return mManualSortKey;
    }

    //region Getters and Setters
    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        setDirty(true);
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
        setDirty(true);
    }

    public ListTheme getListAttributes() {
        return listAttributes;
    }

    public void setListTheme(ListTheme listAttributes) {
        this.listAttributes = listAttributes;
        setDirty(true);
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public ListTitle getListTitle() {
        return listTitle;
    }

    public void setListTitle(ListTitle listTitle) {
        this.listTitle = listTitle;
        setDirty(true);
    }

    public long getManualSortKey() {
        return manualSortKey;
    }

    public void setManualSortKey(long manualSortKey) {
        this.manualSortKey = manualSortKey;
        setDirty(true);
    }

    public boolean isMarkedForDeletion() {
        return markedForDeletion;
    }

    public void setMarkedForDeletion(boolean markedForDeletion) {
        this.markedForDeletion = markedForDeletion;
        setDirty(true);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        setDirty(true);
    }


    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public boolean isStruckOut() {
        return struckOut;
    }

    public void setStruckOut(boolean struckOut) {
        this.struckOut = struckOut;
        setDirty(true);
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
        this.uuid = uuid;
        setDirty(true);
    }

    @Override
    public String toString() {
        return getName();
    }
    //endregion

    public static ListItem newInstance(String newItemName, ListTitle listTitle) {

        ListItem newListItem = new ListItem();
        String newUuid = UUID.randomUUID().toString();
        // replace uuid "-" with "_" to distinguish it from Backendless objectId
        newUuid = newUuid.replace("-", "_");
        newListItem.setUuid(newUuid);
        newListItem.setName(newItemName);
        newListItem.setListTitle(listTitle);
        newListItem.setListTheme(listTitle.getListTheme());
        newListItem.setManualSortKey(getNextManualSortKey());
        newListItem.setFavorite(false);
        newListItem.setChecked(false);
        newListItem.setMarkedForDeletion(false);
        newListItem.setStruckOut(false);

        return newListItem;
    }

    public static ListItem saveToBackendless(ListItem listItem) {
        // saveToBackendless object synchronously
        listItem.setDirty(false);
        ListItem result = null;
        try {
            result = Backendless.Data.of(ListItem.class).save(listItem);
            // TODO: If new listItem, update SQLite db with objectID
            // TODO: Set dirty flag to false in SQLite db
        } catch (BackendlessException e) {
            listItem.setDirty(true);
            // TODO: Set dirty flag to true in SQLite db
            MyLog.e("ListItem", "saveToBackendless(): BackendlessException: " + e.getMessage());
        }
        return result;
    }

    public static void saveToBackendlessAsync(final ListItem listItem) {
        // saveToBackendless object asynchronously
        listItem.setDirty(false);
        Backendless.Persistence.save(listItem, new AsyncCallback<ListItem>() {
            public void handleResponse(ListItem response) {
                // new ListItem instance has been saved
                // TODO: If new listItem, update SQLite db with objectID
                // TODO: Set dirty flag to false in SQLite db
            }

            public void handleFault(BackendlessFault fault) {
                // an error has occurred, the error code can be retrieved with fault.
                listItem.setDirty(true);
                // TODO: Set dirty flag to true in SQLite db
                MyLog.e("ListItem", "handleFault(): BackendlessFault: ListItemName: " + listItem.getName()
                        + " code: " + fault.getCode() + ": " + fault.getMessage());
            }
        });

    }

    private static List<ListItem> mListOfListItems;

    public static List<ListItem> getListOfListItems() {
        return mListOfListItems;
    }

    public static void getAllListItemsFromBackendlessAsync(final Context context) {
        mListOfListItems = new ArrayList<>();

        final AsyncCallback<BackendlessCollection<ListItem>> callback = new AsyncCallback<BackendlessCollection<ListItem>>() {
            @Override
            public void handleResponse(BackendlessCollection<ListItem> foundListItems) {

                List<ListItem> listOfListItems = foundListItems.getData();
                mListOfListItems.addAll(listOfListItems);
                int size = foundListItems.getCurrentPage().size();
                MyLog.i("ListItem", "getAllListItemsFromBackendlessAsync(): Loaded " + size + " ListItems in the current page");

                if (size > 0) {
                    foundListItems.nextPage(this);
                } else {
                    // all done
                    String msg = "All ListItems retrieved. Total number of ListItems = " + mListOfListItems.size();
                    MyLog.i("ListItem", "getAllListItemsFromBackendlessAsync(): " + msg);
                    CommonMethods.showOkDialog(context, "All ListItems", msg);
//                    latch.countDown();
                }
                // TODO: 2/9/2016 Replace all ListItems in the SQLite database
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                // an error has occurred, the error code can be retrieved with fault.getCode()
                MyLog.e("ListItem", "getAllListItemsFromBackendlessAsync(): BackendlessFault: code: "
                        + fault.getCode() + ": " + fault.getMessage());

            }
        };
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setPageSize(PAGE_SIZE);
        QueryOptions queryOptions = new QueryOptions();
        queryOptions.addSortByOption("nameLowercase ASC");
        dataQuery.setQueryOptions(queryOptions);
        Backendless.Data.of(ListItem.class).find(dataQuery, callback);
    }
}
