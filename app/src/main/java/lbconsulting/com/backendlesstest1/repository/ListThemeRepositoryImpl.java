package lbconsulting.com.backendlesstest1.repository;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessException;
import com.backendless.exceptions.BackendlessFault;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lbconsulting.com.backendlesstest1.classes.CommonMethods;
import lbconsulting.com.backendlesstest1.classes.MyLog;
import lbconsulting.com.backendlesstest1.database.ListTheme;
import lbconsulting.com.backendlesstest1.sqlite.Backendless_Test1_ContentProvider;
import lbconsulting.com.backendlesstest1.sqlite.ListThemeSqlTable;

/**
 * This class provided CRUD operations for ListTheme
 */
public class ListThemeRepositoryImpl implements ListThemeRepository {

    Context mContext;
    DateFormat mDateFormat;

    public ListThemeRepositoryImpl(Context context) {
        // private constructor
        this.mContext = context;
        mDateFormat = DateFormat.getDateTimeInstance();
    }

    // CRUD operations

    //region Create
    @Override
    public void insert(ListTheme listTheme) {
        // insert new listTheme into SQLite db
        long newThemeSqlId = -1;
        listTheme.setDirty(true);

        Uri uri = ListThemeSqlTable.CONTENT_URI;
        ContentResolver cr = mContext.getContentResolver();
        ContentValues cv = new ContentValues();
        cv.put(ListThemeSqlTable.COL_OBJECT_ID, listTheme.getObjectId());
        cv.put(ListThemeSqlTable.COL_NAME, listTheme.getName());
        cv.put(ListThemeSqlTable.COL_START_COLOR, listTheme.getStartColor());
        cv.put(ListThemeSqlTable.COL_END_COLOR, listTheme.getEndColor());
        cv.put(ListThemeSqlTable.COL_TEXT_COLOR, listTheme.getTextColor());
        cv.put(ListThemeSqlTable.COL_TEXT_SIZE, listTheme.getTextSize());
        cv.put(ListThemeSqlTable.COL_HORIZONTAL_PADDING_IN_DP, listTheme.getHorizontalPaddingInDp());
        cv.put(ListThemeSqlTable.COL_VERTICAL_PADDING_IN_DP, listTheme.getVerticalPaddingInDp());
        cv.put(ListThemeSqlTable.COL_THEME_DIRTY, listTheme.isDirty());
        cv.put(ListThemeSqlTable.COL_BOLD, listTheme.isBold());
        cv.put(ListThemeSqlTable.COL_CHECKED, listTheme.isChecked());
        cv.put(ListThemeSqlTable.COL_DEFAULT_THEME, listTheme.isDefaultTheme());
        cv.put(ListThemeSqlTable.COL_MARKED_FOR_DELETION, listTheme.isMarkedForDeletion());
        cv.put(ListThemeSqlTable.COL_TRANSPARENT, listTheme.isTransparent());
        cv.put(ListThemeSqlTable.COL_UUID, listTheme.getUuid());
        Date updatedDateTime = listTheme.getUpdated();
        if (updatedDateTime != null) {
            cv.put(ListThemeSqlTable.COL_UPDATED, updatedDateTime.getTime());
        }

        Uri newRaceUri = cr.insert(uri, cv);
        if (newRaceUri != null) {
            newThemeSqlId = Long.parseLong(newRaceUri.getLastPathSegment());
        }

        if (newThemeSqlId > -1) {
            // successfully saved new ListTheme to the SQLite db
            MyLog.i("ListThemeRepositoryImpl", "insert(): successfully saved \"" + listTheme.getName() + "\" to the SQLite db.");

            // if the network is available ... save new listTheme to Backendless
            // if Backendless save is successful ... set dirty flag to false in the SQLite db
            if (CommonMethods.isNetworkAvailable()) {
                saveToBackendlessAsync(newRaceUri, listTheme, true);
                // TODO: send message to Backendless to notify other devices of the new ListTheme
            }

        } else {
            // failed to crate listTheme in the SQLite db
            MyLog.e("ListThemeRepositoryImpl", "insert(): Failed to save \"" + listTheme.getName() + "\" to the SQLite db.");
        }

        return;
    }

    // TODO: send create message to other devices
    private ListTheme saveToBackendless(ListTheme listTheme) {
        // saveToBackendless object synchronously
        ListTheme result = null;
        try {
            result = Backendless.Data.of(ListTheme.class).save(listTheme);
            // TODO: If new listTheme, update SQLite db with objectID
            // TODO: Set dirty flag to false in SQLite db
        } catch (BackendlessException e) {
            listTheme.setDirty(true);
            // TODO: Set dirty flag to true in SQLite db
            MyLog.e("ListTheme", "saveToBackendless(): BackendlessException: " + e.getMessage());
        }
        return result;
    }

    private void saveToBackendlessAsync(final Uri uri, final ListTheme listTheme, final boolean isNew) {
        // save object to Backendless asynchronously

        Backendless.Persistence.save(listTheme, new AsyncCallback<ListTheme>() {
            public void handleResponse(ListTheme response) {
                // new ListTheme instance has been saved to Backendless
                MyLog.i("ListThemeRepositoryImpl", "saveToBackendlessAsync(): successfully saved \"" + listTheme.getName() + "\" to Backendless.");
                // If a new ListTheme, update SQLite db with objectID, dirty to false, and updated date and time
                if (isNew) {
                    ContentResolver cr = mContext.getContentResolver();
                    ContentValues cv = new ContentValues();
                    cv.put(ListThemeSqlTable.COL_OBJECT_ID, response.getObjectId());
                    cv.put(ListThemeSqlTable.COL_THEME_DIRTY, 0);

                    Date updatedDate = response.getUpdated();
                    if (updatedDate == null) {
                        updatedDate = response.getCreated();
                    }
                    if (updatedDate != null) {
                        long updated = updatedDate.getTime();
                        cv.put(ListThemeSqlTable.COL_UPDATED, updated);
                    }

                    Backendless_Test1_ContentProvider.setDisableNotifyChange(true);
                    int numberOfRecordsUpdated = cr.update(uri, cv, null, null);
                    Backendless_Test1_ContentProvider.setDisableNotifyChange(false);

                    if (numberOfRecordsUpdated > 0) {
                        MyLog.i("ListThemeRepositoryImpl", "saveToBackendlessAsync():successfully updated "
                                + response.getName() + " in the SQLite db: "
                                + " set objectId = " + response.getObjectId()
                                + ", date/time modified = " + mDateFormat.format(updatedDate)
                                + " and, " + ListThemeSqlTable.COL_THEME_DIRTY + " = false.");
                    } else {
                        MyLog.e("ListThemeRepositoryImpl", "saveToBackendlessAsync():FAILED to update objectId, date/time modified, and "
                                + ListThemeSqlTable.COL_THEME_DIRTY + " field for \"" + response.getName() + "\" in the SQLite db.");

                    }
                }
            }

            public void handleFault(BackendlessFault fault) {
                // an error has occurred, the error code can be retrieved with fault.
                MyLog.e("ListThemeRepositoryImpl", "saveToBackendlessAsync(): FAILED to saved \"" + listTheme.getName() + "\" to Backendless. Fault message: "
                        + fault.getMessage());


                // Set dirty flag to true in SQLite db
                ContentResolver cr = mContext.getContentResolver();
                ContentValues cv = new ContentValues();
                cv.put(ListThemeSqlTable.COL_THEME_DIRTY, true);
                Backendless_Test1_ContentProvider.setDisableNotifyChange(true);
                int numberOfRecordsUpdated = cr.update(uri, cv, null, null);
                Backendless_Test1_ContentProvider.setDisableNotifyChange(false);

                if (numberOfRecordsUpdated > 0) {
                    String msg = "saveToBackendlessAsync():successfully updated "
                            + ListThemeSqlTable.COL_THEME_DIRTY
                            + " to true in SQLite db "
                            + ListThemeSqlTable.TABLE_LIST_THEMES + ".";
                    MyLog.i("ListThemeRepositoryImpl", msg);

                } else {
                    String msg = "saveToBackendlessAsync():FAILED to updated "
                            + ListThemeSqlTable.COL_THEME_DIRTY
                            + " to true in SQLite db "
                            + ListThemeSqlTable.TABLE_LIST_THEMES + ".";
                    MyLog.i("ListThemeRepositoryImpl", msg);
                }
            }
        });

    }

    //endregion

    //region Read
    @Override
    public ListTheme getListThemeById(long id) {
        ListTheme foundListTheme = null;
        Cursor cursor = null;
        try {
            cursor = getThemeCursorById(mContext, id);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                foundListTheme = ListThemeFromCursor(cursor);
            }
        } catch (Exception e) {
            MyLog.e("ListThemeRepositoryImpl", "getListThemeById(): Exception: " + e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return foundListTheme;
    }

    private ListTheme ListThemeFromCursor(Cursor cursor) {
        ListTheme listTheme = new ListTheme();
        listTheme.setObjectId(cursor.getString(cursor.getColumnIndexOrThrow(ListThemeSqlTable.COL_OBJECT_ID)));
        listTheme.setName(cursor.getString(cursor.getColumnIndexOrThrow(ListThemeSqlTable.COL_NAME)));
        listTheme.setStartColor(cursor.getInt(cursor.getColumnIndexOrThrow(ListThemeSqlTable.COL_START_COLOR)));
        listTheme.setEndColor(cursor.getInt(cursor.getColumnIndexOrThrow(ListThemeSqlTable.COL_END_COLOR)));
        listTheme.setTextColor(cursor.getInt(cursor.getColumnIndexOrThrow(ListThemeSqlTable.COL_TEXT_COLOR)));
        listTheme.setTextSize(cursor.getFloat(cursor.getColumnIndexOrThrow(ListThemeSqlTable.COL_TEXT_SIZE)));
        listTheme.setHorizontalPaddingInDp(cursor.getFloat(cursor.getColumnIndexOrThrow(ListThemeSqlTable.COL_HORIZONTAL_PADDING_IN_DP)));
        listTheme.setVerticalPaddingInDp(cursor.getFloat(cursor.getColumnIndexOrThrow(ListThemeSqlTable.COL_VERTICAL_PADDING_IN_DP)));
        listTheme.setDirty(cursor.getLong(cursor.getColumnIndexOrThrow(ListThemeSqlTable.COL_THEME_DIRTY)) > 0);
        listTheme.setBold(cursor.getLong(cursor.getColumnIndexOrThrow(ListThemeSqlTable.COL_BOLD)) > 0);
        listTheme.setChecked(cursor.getLong(cursor.getColumnIndexOrThrow(ListThemeSqlTable.COL_CHECKED)) > 0);
        listTheme.setDefaultTheme(cursor.getLong(cursor.getColumnIndexOrThrow(ListThemeSqlTable.COL_DEFAULT_THEME)) > 0);
        listTheme.setMarkedForDeletion(cursor.getLong(cursor.getColumnIndexOrThrow(ListThemeSqlTable.COL_MARKED_FOR_DELETION)) > 0);
        listTheme.setTransparent(cursor.getLong(cursor.getColumnIndexOrThrow(ListThemeSqlTable.COL_TRANSPARENT)) > 0);
        listTheme.setUuid(cursor.getString(cursor.getColumnIndexOrThrow(ListThemeSqlTable.COL_UUID)));
        long dateMillis = cursor.getLong(cursor.getColumnIndexOrThrow(ListThemeSqlTable.COL_UPDATED));
        Date updated = new Date(dateMillis);
        listTheme.setUpdated(updated);

        return listTheme;
    }

    private Cursor getThemeCursorById(Context context, long themeId) {
        Cursor cursor = null;
        Uri uri = ListThemeSqlTable.CONTENT_URI;
        String[] projection = ListThemeSqlTable.PROJECTION_ALL;
        String selection = ListThemeSqlTable.COL_ID + " = ?";
        String selectionArgs[] = new String[]{String.valueOf(themeId)};
        String sortOrder = null;

        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("ListThemeRepositoryImpl", "getThemeCursorById(): Exception: " + e.getMessage());
        }
        return cursor;
    }

    private Cursor getThemeCursorByObjectId(Context context, String objectID) {
        Cursor cursor = null;
        Uri uri = ListThemeSqlTable.CONTENT_URI;
        String[] projection = ListThemeSqlTable.PROJECTION_ALL;
        String selection = ListThemeSqlTable.COL_OBJECT_ID + " = ?";
        String selectionArgs[] = new String[]{objectID};
        String sortOrder = null;

        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("ListThemeRepositoryImpl", "getThemeCursorByObjectId(): Exception: " + e.getMessage());
        }
        return cursor;
    }

    private Cursor getThemeCursorByUuid(Context context, String uuid) {
        Cursor cursor = null;
        Uri uri = ListThemeSqlTable.CONTENT_URI;
        String[] projection = ListThemeSqlTable.PROJECTION_ALL;
        String selection = ListThemeSqlTable.COL_UUID + " = ?";
        String selectionArgs[] = new String[]{uuid};
        String sortOrder = null;

        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("ListThemeRepositoryImpl", "getThemeCursorByUuid(): Exception: " + e.getMessage());
        }
        return cursor;
    }

    @Override
    public ListTheme getListThemeByObjectId(String objectId) {
        ListTheme foundListTheme = null;
        Cursor cursor = getThemeCursorByObjectId(mContext, objectId);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            foundListTheme = ListThemeFromCursor(cursor);
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return foundListTheme;
    }

    @Override
    public ListTheme getListThemeByUuid(String uuid) {
        // done
        ListTheme foundListTheme = null;
        Cursor cursor = null;
        try {
            cursor = getThemeCursorByUuid(mContext, uuid);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                foundListTheme = ListThemeFromCursor(cursor);
            }
        } catch (Exception e) {
            MyLog.e("ListThemeRepositoryImpl", "getListThemeByUuid(): Exception: " + e.getMessage());

        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return foundListTheme;
    }

    @Override
    public List<ListTheme> getAllThemes() {
        // done
        List<ListTheme> listThemes = new ArrayList<ListTheme>();
        ListTheme listTheme;
        Cursor cursor = null;
        try {
            cursor = getAllThemesCursor(mContext);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    listTheme = ListThemeFromCursor(cursor);
                    listThemes.add(listTheme);
                }
            }
        } catch (Exception e) {
            MyLog.e("ListThemeRepositoryImpl", "getAllThemes(): Exception: " + e.getMessage());

        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return listThemes;
    }

    private Cursor getAllThemesCursor(Context context) {
        Cursor cursor = null;
        Uri uri = ListThemeSqlTable.CONTENT_URI;
        String[] projection = ListThemeSqlTable.PROJECTION_ALL;
        String selection = ListThemeSqlTable.COL_MARKED_FOR_DELETION + " = ?";
        String selectionArgs[] = new String[]{"0"};
        String sortOrder = ListThemeSqlTable.SORT_ORDER_NAME_ASC;

        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("ListThemeRepositoryImpl", "getAllThemesCursor(): Exception: " + e.getMessage());
        }
        return cursor;

    }
    //endregion

    //region Update
    @Override
    public void update(ContentValues contentValues, String selection, String[] selectionArgs) {
        Uri uri = ListThemeSqlTable.CONTENT_URI;
        ContentResolver cr = mContext.getContentResolver();
        int numberOfRecordsUpdated = cr.update(uri, contentValues, selection, selectionArgs);
        if (numberOfRecordsUpdated < 1) {
            MyLog.e("ListThemeRepositoryImpl", "update(): Error trying to update SQLite db: " + contentValues.toString());
        }
    }

    // TODO: update to Backendless
    // TODO: Send update message to other devices
    public void update(String uuid, ContentValues contentValues) {
        String selection = ListThemeSqlTable.COL_UUID + " = '" + uuid + "'";
        String[] selectionArgs = null;
        update(contentValues, selection, selectionArgs);
    }

    public void update(String uuid, String FieldName, boolean value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FieldName, value);
        update(uuid, contentValues);
    }

    public void update(String uuid, String FieldName, float value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FieldName, value);
        update(uuid, contentValues);
    }

    public void update(String uuid, String FieldName, int value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FieldName, value);
        update(uuid, contentValues);
    }

    public void update(String uuid, String FieldName, long value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FieldName, value);
        update(uuid, contentValues);
    }

    public void update(String uuid, String FieldName, String value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FieldName, value);
        update(uuid, contentValues);
    }

    public void toggle(String uuid, String FieldName) {
        ListTheme currentListTheme = getListThemeByUuid(uuid);
        if (currentListTheme == null) {
            MyLog.e("ListThemeRepositoryImpl", "toggle(): Unable to find current ListTheme for uuid =  " + uuid);
            return;
        }

        boolean currentValue;
        ContentValues cv = new ContentValues();
        String selection = ListThemeSqlTable.COL_UUID + " = '" + uuid + "'";
        String[] selectionArgs = null;

        if (FieldName.equals(ListThemeSqlTable.COL_BOLD)) {
            currentValue = currentListTheme.isBold();
            cv.put(ListThemeSqlTable.COL_BOLD, !currentValue);
            update(cv, selection, selectionArgs);

        } else if (FieldName.equals(ListThemeSqlTable.COL_CHECKED)) {
            currentValue = currentListTheme.isChecked();
            cv.put(ListThemeSqlTable.COL_CHECKED, !currentValue);
            update(cv, selection, selectionArgs);

        } else if (FieldName.equals(ListThemeSqlTable.COL_DEFAULT_THEME)) {
            currentValue = currentListTheme.isDefaultTheme();
            cv.put(ListThemeSqlTable.COL_DEFAULT_THEME, !currentValue);
            update(cv, selection, selectionArgs);

        } else if (FieldName.equals(ListThemeSqlTable.COL_THEME_DIRTY)) {
            currentValue = currentListTheme.isDirty();
            cv.put(ListThemeSqlTable.COL_THEME_DIRTY, !currentValue);
            update(cv, selection, selectionArgs);

        } else if (FieldName.equals(ListThemeSqlTable.COL_MARKED_FOR_DELETION)) {
            currentValue = currentListTheme.isMarkedForDeletion();
            cv.put(ListThemeSqlTable.COL_MARKED_FOR_DELETION, !currentValue);
            update(cv, selection, selectionArgs);

        } else if (FieldName.equals(ListThemeSqlTable.COL_TRANSPARENT)) {
            currentValue = currentListTheme.isTransparent();
            cv.put(ListThemeSqlTable.COL_TRANSPARENT, !currentValue);
            update(cv, selection, selectionArgs);

        } else {
            MyLog.e("ListThemeRepositoryImpl", "toggle(): Unknown Field Name!");
        }
    }
// endregion

    //region Delete
    @Override
    public void delete(String selection, String[] selectionArgs) {
        // done
        Uri uri = ListThemeSqlTable.CONTENT_URI;
        ContentResolver cr = mContext.getContentResolver();
        int numberOfRecordsDeleted = cr.delete(uri, selection, selectionArgs);
        if (numberOfRecordsDeleted < 1) {
            MyLog.e("ListThemeRepositoryImpl", "delete(): Nothing deleted while trying to delete SQLite db object with " + selection);
        }
    }

    // TODO: delete to Backendless
    // TODO: send delete message to other devices
    public void delete(String uuid) {
        // done
        String selection = ListThemeSqlTable.COL_UUID + " = '" + uuid + "'";
        delete(selection, null);
    }
    //endregion
}
