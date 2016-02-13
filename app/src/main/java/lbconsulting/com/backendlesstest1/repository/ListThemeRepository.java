package lbconsulting.com.backendlesstest1.repository;

import android.content.ContentValues;

import java.util.List;

import lbconsulting.com.backendlesstest1.database.ListTheme;

/**
 * An interface for CRUD operations on a ListTheme
 */
//region Create
public interface ListThemeRepository {
    void insert(ListTheme listTheme);

    //endregion

    //region Read
    ListTheme getListThemeById(long id);

    ListTheme getListThemeByObjectId(String objectId);

    ListTheme getListThemeByUuid(String uuid);

    List<ListTheme> getAllThemes();
    //endregion

    //region Update

    void update(ContentValues contentValues, String selection, String[] selectionArgs) ;
    //endregion

    //region Delete
    void delete(String selection, String[] selectionArgs);
    //endregion


}
