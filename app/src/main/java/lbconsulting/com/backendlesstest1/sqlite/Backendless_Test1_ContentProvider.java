package lbconsulting.com.backendlesstest1.sqlite;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import lbconsulting.com.backendlesstest1.classes.MyLog;


public class Backendless_Test1_ContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.lbconsulting.backendless.test1";

    private Backendless_Test1_DatabaseHelper database = null;

    // UriMatcher switch constants
    private static final int LIST_ITEMS_MULTI_ROWS = 10;
    private static final int LIST_ITEMS_SINGLE_ROW = 11;

    private static final int LIST_TITLES_MULTI_ROWS = 20;
    private static final int LIST_TITLES_SINGLE_ROW = 21;

    private static final int LIST_ATTRIBUTES_MULTI_ROWS = 30;
    private static final int LIST_ATTRIBUTES_SINGLE_ROW = 31;


    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static boolean disableNotifyChange = false;
    private static boolean disableBackendlessUpdate = false;

    public static void setDisableBackendlessUpdate(boolean disableBackendlessUpdate) {
        Backendless_Test1_ContentProvider.disableBackendlessUpdate = disableBackendlessUpdate;
    }

    public static void setDisableNotifyChange(boolean disableNotifyChange) {
        Backendless_Test1_ContentProvider.disableNotifyChange = disableNotifyChange;
    }

    static {
        sURIMatcher.addURI(AUTHORITY, ListItemsSqlTable.CONTENT_PATH, LIST_ITEMS_MULTI_ROWS);
        sURIMatcher.addURI(AUTHORITY, ListItemsSqlTable.CONTENT_PATH + "/#", LIST_ITEMS_SINGLE_ROW);

        sURIMatcher.addURI(AUTHORITY, ListTitlesSqlTable.CONTENT_PATH, LIST_TITLES_MULTI_ROWS);
        sURIMatcher.addURI(AUTHORITY, ListTitlesSqlTable.CONTENT_PATH + "/#", LIST_TITLES_SINGLE_ROW);

        sURIMatcher.addURI(AUTHORITY, ListThemeSqlTable.CONTENT_PATH, LIST_ATTRIBUTES_MULTI_ROWS);
        sURIMatcher.addURI(AUTHORITY, ListThemeSqlTable.CONTENT_PATH + "/#", LIST_ATTRIBUTES_SINGLE_ROW);

    }

    @Override
    public boolean onCreate() {
        MyLog.i("Backendless_Test1_ContentProvider", "onCreate");

        // Construct the underlying database
        // Defer opening the database until you need to perform
        // a query or other transaction.
        database = new Backendless_Test1_DatabaseHelper(getContext());
        return true;
    }

	/*	A content provider is created when its hosting process is created, and remains around for as long as the process
        does, so there is no need to close the database -- it will get closed as part of the kernel cleaning up the
		process's resources when the process is killed. 
	*/

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        String rowId;
        int deleteCount;

        // Open a WritableDatabase database to support the delete transaction
        SQLiteDatabase db = database.getWritableDatabase();

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {

            case LIST_ITEMS_MULTI_ROWS:
                // To return the number of deleted items you must specify a where clause.
                // To delete all rows and return a value pass in "1".
                if (selection == null) {
                    selection = "1";
                }
                // Perform the deletion
                deleteCount = db.delete(ListItemsSqlTable.TABLE_LIST_ITEMS, selection, selectionArgs);
                break;

            case LIST_ITEMS_SINGLE_ROW:
                // Limit deletion to a single row
                rowId = uri.getLastPathSegment();
                selection = ListItemsSqlTable.COL_ID + "=" + rowId;
                // Perform the deletion
                deleteCount = db.delete(ListItemsSqlTable.TABLE_LIST_ITEMS, selection, selectionArgs);
                break;

            case LIST_TITLES_MULTI_ROWS:
                // To return the number of deleted items you must specify a where clause.
                // To delete all rows and return a value pass in "1".
                if (selection == null) {
                    selection = "1";
                }
                // Perform the deletion
                deleteCount = db.delete(ListTitlesSqlTable.TABLE_LIST_TITLES, selection, selectionArgs);
                break;

            case LIST_TITLES_SINGLE_ROW:
                // Limit deletion to a single row
                rowId = uri.getLastPathSegment();
                selection = ListTitlesSqlTable.COL_ID + "=" + rowId;
                // Perform the deletion
                deleteCount = db.delete(ListTitlesSqlTable.TABLE_LIST_TITLES, selection, selectionArgs);
                break;

            case LIST_ATTRIBUTES_MULTI_ROWS:
                // To return the number of deleted items you must specify a where clause.
                // To delete all rows and return a value pass in "1".
                if (selection == null) {
                    selection = "1";
                }
                // Perform the deletion
                deleteCount = db.delete(ListThemeSqlTable.TABLE_LIST_THEMES, selection, selectionArgs);
                break;

            case LIST_ATTRIBUTES_SINGLE_ROW:
                // Limit deletion to a single row
                rowId = uri.getLastPathSegment();
                selection = ListThemeSqlTable.COL_ID + "=" + rowId;
                // Perform the deletion
                deleteCount = db.delete(ListThemeSqlTable.TABLE_LIST_THEMES, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Method delete: Unknown URI: " + uri);
        }

        if(!disableNotifyChange) {
            if (getContext() != null && getContext().getContentResolver() != null) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }

        if(!disableBackendlessUpdate){
            // TODO: First set marked for deletion flag, then delete from backendless, then delete from db
            // TODO: Make change in Backendless
        }
        return deleteCount;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {

            case LIST_ITEMS_MULTI_ROWS:
                return ListItemsSqlTable.CONTENT_TYPE;
            case LIST_ITEMS_SINGLE_ROW:
                return ListItemsSqlTable.CONTENT_ITEM_TYPE;

            case LIST_TITLES_MULTI_ROWS:
                return ListTitlesSqlTable.CONTENT_TYPE;
            case LIST_TITLES_SINGLE_ROW:
                return ListTitlesSqlTable.CONTENT_ITEM_TYPE;

            case LIST_ATTRIBUTES_MULTI_ROWS:
                return ListThemeSqlTable.CONTENT_TYPE;
            case LIST_ATTRIBUTES_SINGLE_ROW:
                return ListThemeSqlTable.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Method getType. Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        SQLiteDatabase db = null;
        long newRowId;
        String nullColumnHack = null;

        // Open a WritableDatabase database to support the insert transaction
        db = database.getWritableDatabase();

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {

            case LIST_ITEMS_MULTI_ROWS:
                newRowId = db.insertOrThrow(ListItemsSqlTable.TABLE_LIST_ITEMS, nullColumnHack, values);
                if (newRowId > 0) {
                    // Construct and return the URI of the newly inserted row.
                    Uri newRowUri = ContentUris.withAppendedId(ListItemsSqlTable.CONTENT_URI, newRowId);

                    if (!disableNotifyChange) {
                        if (getContext() != null && getContext().getContentResolver() != null) {
                            getContext().getContentResolver().notifyChange(ListItemsSqlTable.CONTENT_URI, null);
                        }
                    }
                    if (!disableBackendlessUpdate) {
                        // TODO: Make change in Backendless
                    }
                    return newRowUri;
                }
                return null;

            case LIST_ITEMS_SINGLE_ROW:
                throw new IllegalArgumentException(
                        "Illegal URI: Cannot insert a new row with a single row URI. " + uri);

            case LIST_TITLES_MULTI_ROWS:
                newRowId = db.insertOrThrow(ListTitlesSqlTable.TABLE_LIST_TITLES, nullColumnHack, values);
                if (newRowId > 0) {
                    // Construct and return the URI of the newly inserted row.
                    Uri newRowUri = ContentUris.withAppendedId(ListTitlesSqlTable.CONTENT_URI, newRowId);
                    if (!disableNotifyChange) {
                        if (getContext() != null && getContext().getContentResolver() != null) {
                            getContext().getContentResolver().notifyChange(ListTitlesSqlTable.CONTENT_URI, null);
                        }
                    }

                    if (!disableBackendlessUpdate) {
                        // TODO: Make change in Backendless
                    }
                    return newRowUri;
                }
                return null;

            case LIST_TITLES_SINGLE_ROW:
                throw new IllegalArgumentException(
                        "Illegal URI: Cannot insert a new row with a single row URI. " + uri);

            case LIST_ATTRIBUTES_MULTI_ROWS:
                newRowId = db.insertOrThrow(ListThemeSqlTable.TABLE_LIST_THEMES, nullColumnHack, values);
                if (newRowId > 0) {
                    // Construct and return the URI of the newly inserted row.
                    Uri newRowUri = ContentUris.withAppendedId(ListThemeSqlTable.CONTENT_URI, newRowId);
                    if (!disableNotifyChange) {
                        if (getContext() != null && getContext().getContentResolver() != null) {
                            getContext().getContentResolver().notifyChange(ListThemeSqlTable.CONTENT_URI, null);
                        }
                    }

                    if (!disableBackendlessUpdate) {
                        // TODO: Make change in Backendless
                    }
                    return newRowUri;
                }
                return null;

            case LIST_ATTRIBUTES_SINGLE_ROW:
                throw new IllegalArgumentException(
                        "Illegal URI: Cannot insert a new row with a single row URI. " + uri);

            default:
                throw new IllegalArgumentException("Method insert: Unknown URI:" + uri);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // Using SQLiteQueryBuilder
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {

            case LIST_ITEMS_MULTI_ROWS:
                queryBuilder.setTables(ListItemsSqlTable.TABLE_LIST_ITEMS);
                break;

            case LIST_ITEMS_SINGLE_ROW:
                queryBuilder.setTables(ListItemsSqlTable.TABLE_LIST_ITEMS);
                queryBuilder.appendWhere(ListItemsSqlTable.COL_ID + "=" + uri.getLastPathSegment());
                break;

            case LIST_TITLES_MULTI_ROWS:
                queryBuilder.setTables(ListTitlesSqlTable.TABLE_LIST_TITLES);
                break;

            case LIST_TITLES_SINGLE_ROW:
                queryBuilder.setTables(ListTitlesSqlTable.TABLE_LIST_TITLES);
                queryBuilder.appendWhere(ListTitlesSqlTable.COL_ID + "=" + uri.getLastPathSegment());
                break;

            case LIST_ATTRIBUTES_MULTI_ROWS:
                queryBuilder.setTables(ListThemeSqlTable.TABLE_LIST_THEMES);
                break;

            case LIST_ATTRIBUTES_SINGLE_ROW:
                queryBuilder.setTables(ListThemeSqlTable.TABLE_LIST_THEMES);
                queryBuilder.appendWhere(ListThemeSqlTable.COL_ID + "=" + uri.getLastPathSegment());
                break;


            default:
                throw new IllegalArgumentException("Method query. Unknown URI:" + uri);
        }

        // Execute the query on the database
        SQLiteDatabase db = null;
        try {
            db = database.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = database.getReadableDatabase();
        }

        if (null != db) {
            String groupBy = null;
            String having = null;
            Cursor cursor = null;
            try {
                cursor = queryBuilder.query(db, projection, selection, selectionArgs, groupBy, having, sortOrder);
            } catch (Exception e) {
                MyLog.e("Backendless_Test1_ContentProvider", "Exception error in query.");
                e.printStackTrace();
            }

            if (null != cursor && getContext() != null && getContext().getContentResolver() != null) {
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
            }
            return cursor;
        }
        return null;
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String rowID;
        int updateCount = 0;

        // Open a WritableDatabase database to support the update transaction
        SQLiteDatabase db = database.getWritableDatabase();

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {

            case LIST_ITEMS_MULTI_ROWS:
                updateCount = db.update(ListItemsSqlTable.TABLE_LIST_ITEMS, values, selection, selectionArgs);
                break;

            case LIST_ITEMS_SINGLE_ROW:
                rowID = uri.getLastPathSegment();
                selection = ListItemsSqlTable.COL_ID + "=" + rowID;
                updateCount = db.update(ListItemsSqlTable.TABLE_LIST_ITEMS, values, selection, selectionArgs);
                break;

            case LIST_TITLES_MULTI_ROWS:
                updateCount = db.update(ListTitlesSqlTable.TABLE_LIST_TITLES, values, selection, selectionArgs);
                break;

            case LIST_TITLES_SINGLE_ROW:
                rowID = uri.getLastPathSegment();
                selection = ListTitlesSqlTable.COL_ID + "=" + rowID;
                updateCount = db.update(ListTitlesSqlTable.TABLE_LIST_TITLES, values, selection, selectionArgs);
                break;

            case LIST_ATTRIBUTES_MULTI_ROWS:
                updateCount = db.update(ListThemeSqlTable.TABLE_LIST_THEMES, values, selection,
                        selectionArgs);
                break;

            case LIST_ATTRIBUTES_SINGLE_ROW:
                rowID = uri.getLastPathSegment();
                selection = ListThemeSqlTable.COL_ID + "=" + rowID;
                updateCount = db.update(ListThemeSqlTable.TABLE_LIST_THEMES, values, selection,
                        selectionArgs);
                break;


            default:
                throw new IllegalArgumentException("Method update: Unknown URI: " + uri);
        }
        if (!disableNotifyChange) {
            if (getContext() != null && getContext().getContentResolver() != null) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }

        if (!disableBackendlessUpdate) {
            // TODO: Make change in Backendless
        }
        return updateCount;
    }

    /**
     * A test package can call this to get a handle to the database underlying HW311ContentProvider, so it can insert
     * test data into the database. The test case class is responsible for instantiating the provider in a test context;
     * {@link android.test.ProviderTestCase2} does this during the call to setUp()
     *
     * @return a handle to the database helper object for the provider's data.
     */
    public Backendless_Test1_DatabaseHelper getOpenHelperForTest() {
        return database;
    }
}
