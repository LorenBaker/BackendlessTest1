package lbconsulting.com.backendlesstest1.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lbconsulting.com.backendlesstest1.R;
import lbconsulting.com.backendlesstest1.classes.CommonMethods;
import lbconsulting.com.backendlesstest1.classes.MyLog;
import lbconsulting.com.backendlesstest1.database.ListItem;
import lbconsulting.com.backendlesstest1.database.ListTheme;
import lbconsulting.com.backendlesstest1.database.ListTitle;
import lbconsulting.com.backendlesstest1.repository.ListThemeRepositoryImpl;
import lbconsulting.com.backendlesstest1.sqlite.ListThemeSqlTable;

public class TestDataActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;

    private EditText txtSqlTableName;
    private EditText txtObjectUuid;
    private EditText txtFieldName;
    private EditText txtFieldValue;
    private TextView tvResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyLog.i("TestDataActivity", "onCreate");
        setContentView(R.layout.activity_test_data);

        mContext = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button btnUploadThemes = (Button) findViewById(R.id.btnUploadThemes);
        Button btnRetrieveThemes = (Button) findViewById(R.id.btnRetrieveThemes);
        Button btnRunTest = (Button) findViewById(R.id.btnRunTest);
/*        Button btnUploadListTitles = (Button) findViewById(R.id.btnUploadListTitles);
        Button btnRetrieveListTitles = (Button) findViewById(R.id.btnRetrieveListTitles);
        Button btnUploadListItems = (Button) findViewById(R.id.btnUploadListItems);
        Button btnRetrieveListItems = (Button) findViewById(R.id.btnRetrieveListItems);*/

        btnUploadThemes.setOnClickListener(this);
        btnRetrieveThemes.setOnClickListener(this);
        btnRunTest.setOnClickListener(this);
/*        btnUploadListTitles.setOnClickListener(this);
        btnRetrieveListTitles.setOnClickListener(this);
        btnUploadListItems.setOnClickListener(this);
        btnRetrieveListItems.setOnClickListener(this);*/

        txtSqlTableName = (EditText) findViewById(R.id.txtSqlTableName);
        txtObjectUuid = (EditText) findViewById(R.id.txtObjectUuid);
        txtFieldName = (EditText) findViewById(R.id.txtFieldName);
        txtFieldValue = (EditText) findViewById(R.id.txtFieldValue);
        tvResults = (TextView) findViewById(R.id.tvResults);

        prepareTest();

    }


    private static void showOkDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // set dialog title and message
        alertDialogBuilder
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnOK = alertDialog.getButton(Dialog.BUTTON_POSITIVE);
                btnOK.setTextSize(18);
            }
        });

        // show it
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnUploadThemes:
                uploadTestTestThemes();
                break;

            case R.id.btnRetrieveThemes:
                retrieveListThemesFromBackendless();
                break;

            case R.id.btnRunTest:
                runTest();
                break;

/*            case R.id.btnUploadListTitles:
//                Toast.makeText(this, "btnUploadListTitles clicked.", Toast.LENGTH_SHORT).show();
                uploadTestListTitles();
                break;

            case R.id.btnRetrieveListTitles:
//                Toast.makeText(this, "btnRetrieveListTitles clicked.", Toast.LENGTH_SHORT).show();
                retrieveListTitles();
                break;

            case R.id.btnUploadListItems:
//                Toast.makeText(this, "btnUploadListItems clicked.", Toast.LENGTH_SHORT).show();
                uploadTestListItems();
                break;

            case R.id.btnRetrieveListItems:
//                Toast.makeText(this, "btnRetrieveListItems clicked.", Toast.LENGTH_SHORT).show();
                retrieveListItems();
                break;*/
        }
    }


    private void prepareTest() {
        txtSqlTableName.setText(ListThemeSqlTable.TABLE_LIST_THEMES);
        txtObjectUuid.setText("799b2c1a_3dbf_4106_a04f_99f9ed47a1c5");

    }

    private void runTest() {
        ListThemeRepositoryImpl listThemeRepositoryImpl = new ListThemeRepositoryImpl(this);
        String objectUuid = txtObjectUuid.getText().toString().trim();
        listThemeRepositoryImpl.delete(objectUuid);

        List<ListTheme> listThemes = listThemeRepositoryImpl.getAllThemes();
        StringBuilder sb = new StringBuilder();
        for (ListTheme lisTheme : listThemes) {
            sb.append(lisTheme.toString());
        }

        tvResults.setText("Results:\n" + "Number of ListThemes = " + listThemes.size() + "\n\n" + sb.toString());
    }


    private void uploadTestTestThemes() {
        if (!CommonMethods.isNetworkAvailable()) {
            showOkDialog(this, "Network Not Available", "Unable to upload ListThemes");
            return;
        }

        ListThemeRepositoryImpl listThemeRepositoryImpl = new ListThemeRepositoryImpl(this);

        ListTheme newListTheme;
        int newListThemeCount = 0;
        newListTheme = ListTheme.newInstance("Genoa",
                Color.parseColor("#4c898e"), Color.parseColor("#125156"),
                ContextCompat.getColor(this, R.color.white),
                17f, 10f, 10f, false, false, true);
        listThemeRepositoryImpl.insert(newListTheme);
        newListThemeCount++;

        newListTheme = ListTheme.newInstance("Opal",
                Color.parseColor("#cbdcd4"), Color.parseColor("#91a69d"),
                ContextCompat.getColor(this, R.color.black),
                17f, 10f, 10f, false, false, false);
        listThemeRepositoryImpl.insert(newListTheme);
        newListThemeCount++;

        newListTheme = ListTheme.newInstance("Shades of Blue",
                -5777934, -10841921,
                ContextCompat.getColor(this, R.color.black),
                17f, 10f, 10f, false, false, false);
        listThemeRepositoryImpl.insert(newListTheme);
        newListThemeCount++;

        newListTheme = ListTheme.newInstance("Off White",
                ContextCompat.getColor(this, R.color.white), -2436147,
                ContextCompat.getColor(this, R.color.black),
                17f, 10f, 10f, false, true, false);
        listThemeRepositoryImpl.insert(newListTheme);
        newListThemeCount++;

        newListTheme = ListTheme.newInstance("Whiskey",
                Color.parseColor("#e9ac6d"), Color.parseColor("#ad7940"),
                ContextCompat.getColor(this, R.color.white),
                17f, 10f, 10f, false, false, false);
        listThemeRepositoryImpl.insert(newListTheme);
        newListThemeCount++;

        newListTheme = ListTheme.newInstance("Shakespeare",
                Color.parseColor("#73c5d3"), Color.parseColor("#308d9e"),
                ContextCompat.getColor(this, R.color.white),
                17f, 10f, 10f, false, false, false);
        listThemeRepositoryImpl.insert(newListTheme);
        newListThemeCount++;

        newListTheme = ListTheme.newInstance("Sorbus",
                Color.parseColor("#f0725b"), Color.parseColor("#bc3c21"),
                ContextCompat.getColor(this, R.color.white),
                17f, 10f, 10f, false, false, false);
        listThemeRepositoryImpl.insert(newListTheme);
        newListThemeCount++;

        newListTheme = ListTheme.newInstance("Dark Khaki",
                Color.parseColor("#ced285"), Color.parseColor("#9b9f55"),
                ContextCompat.getColor(this, R.color.white),
                17f, 10f, 10f, false, false, false);
        listThemeRepositoryImpl.insert(newListTheme);
        newListThemeCount++;

        newListTheme = ListTheme.newInstance("Lemon Chiffon",
                Color.parseColor("#fdfcdd"), Color.parseColor("#e3e2ac"),
                ContextCompat.getColor(this, R.color.black),
                17f, 10f, 10f, false, false, false);
        listThemeRepositoryImpl.insert(newListTheme);
        newListThemeCount++;

        newListTheme = ListTheme.newInstance("Paprika",
                Color.parseColor("#994552"), Color.parseColor("#5f0c16"),
                ContextCompat.getColor(this, R.color.white),
                17f, 10f, 10f, false, false, false);
        listThemeRepositoryImpl.insert(newListTheme);
        newListThemeCount++;

        newListTheme = ListTheme.newInstance("Medium Wood",
                Color.parseColor("#bfaa75"), Color.parseColor("#8a7246"),
                ContextCompat.getColor(this, R.color.white),
                17f, 10f, 10f, false, false, false);
        listThemeRepositoryImpl.insert(newListTheme);
        newListThemeCount++;

        newListTheme = ListTheme.newInstance("Breaker Bay",
                Color.parseColor("#6d8b93"), Color.parseColor("#31535c"),
                ContextCompat.getColor(this, R.color.white),
                17f, 10f, 10f, false, false, false);
        listThemeRepositoryImpl.insert(newListTheme);
        newListThemeCount++;

        newListTheme = ListTheme.newInstance("Sandrift",
                Color.parseColor("#cbb59d"), Color.parseColor("#92806c"),
                ContextCompat.getColor(this, R.color.white),
                17f, 10f, 10f, false, false, false);
        listThemeRepositoryImpl.insert(newListTheme);
        newListThemeCount++;

        newListTheme = ListTheme.newInstance("Pale Brown",
                Color.parseColor("#ac956c"), Color.parseColor("#705c39"),
                ContextCompat.getColor(this, R.color.white),
                17f, 10f, 10f, false, false, false);
        listThemeRepositoryImpl.insert(newListTheme);
        newListThemeCount++;

        newListTheme = ListTheme.newInstance("Seagull",
                Color.parseColor("#94dcea"), Color.parseColor("#4ea0ab"),
                ContextCompat.getColor(this, R.color.black),
                17f, 10f, 10f, false, false, false);
        listThemeRepositoryImpl.insert(newListTheme);
        newListThemeCount++;

        newListTheme = ListTheme.newInstance("Beige",
                Color.parseColor("#fefefe"), Color.parseColor("#d3d8c2"),
                ContextCompat.getColor(this, R.color.black),
                17f, 10f, 10f, false, false, false);
        listThemeRepositoryImpl.insert(newListTheme);
        newListThemeCount++;

        newListTheme = ListTheme.newInstance("Orange",
                Color.parseColor("#ff6c52"), Color.parseColor("#e0341e"),
                ContextCompat.getColor(this, R.color.white),
                17f, 10f, 10f, false, false, false);
        listThemeRepositoryImpl.insert(newListTheme);
        newListThemeCount++;

        newListTheme = ListTheme.newInstance("Arsenic",
                Color.parseColor("#545c67"), Color.parseColor("#1d242c"),
                ContextCompat.getColor(this, R.color.white),
                17f, 10f, 10f, false, false, false);
        listThemeRepositoryImpl.insert(newListTheme);
        newListThemeCount++;

        newListTheme = ListTheme.newInstance("Acapulco",
                Color.parseColor("#8dbab3"), Color.parseColor("#58857e"),
                ContextCompat.getColor(this, R.color.white),
                17f, 10f, 10f, false, false, false);
        listThemeRepositoryImpl.insert(newListTheme);
        newListThemeCount++;

        String resultMessage = "Requested " + newListThemeCount + " Themes asyncSave to Backendless.";
        MyLog.i("uploadTestTestThemes", resultMessage);
        showOkDialog(this, "Saved Themes", resultMessage);
    }

    private void retrieveListThemesFromBackendless() {
//        ListTheme.getAllAttributesFromBackendlessAsync(this);
    }

    private void uploadTestListTitles() {

        String[] listTitleNames = getResources().getStringArray(R.array.list_title_names);

        int count = 0;
        ListTitle newListTitle;
        for (String listTitleName : listTitleNames) {
            newListTitle = ListTitle.newInstance(listTitleName);
            ListTitle.saveToBackendlessAsync(newListTitle);
            count++;
        }

        String resultMessage = "Requested " + count + " ListTitles asyncSave to Backendless.";
        MyLog.i("uploadTestListTitles", resultMessage);
        showOkDialog(this, "Saved ListTitles", resultMessage);
    }

    private void retrieveListTitles() {
        ListTitle.getAllListTitlesFromBackendlessAsync(this);
    }

    private void uploadTestListItems() {

        String[] list1ItemNames = getResources().getStringArray(R.array.list_1_items);
        String[] list2ItemNames = getResources().getStringArray(R.array.list_2_items);
        String[] list3ItemNames = getResources().getStringArray(R.array.list_3_items);
        String[] list4ItemNames = getResources().getStringArray(R.array.list_4_items);

        ArrayList<String[]> itemNames = new ArrayList<>();
        itemNames.add(list1ItemNames);
        itemNames.add(list2ItemNames);
        itemNames.add(list3ItemNames);
        itemNames.add(list4ItemNames);

        ArrayList<ListItem> listItems = new ArrayList<>();
        List<ListTitle> listTitles = ListTitle.getListOfListTitles();

        int listTitleIndex = 0;
        int count = 1;
        for (ListTitle listTitle : listTitles) {

            for (String listItemName : itemNames.get(listTitleIndex)) {
                ListItem newListItem = ListItem.newInstance(listItemName, listTitle);
                MyLog.i("uploadTestListItems", "Adding ListItem \"" + listItemName
                        + "\" to \"" + listTitle.getName() + "\".");
                listItems.add(newListItem);
                count++;
            }

            listTitleIndex++;
        }

        count = 0;
        for (ListItem item : listItems) {
            ListItem.saveToBackendlessAsync(item);
            count++;
        }

        String resultMessage = "Requested " + count + " ListItems asyncSave to Backendless.";
        MyLog.i("uploadTestListTitles", resultMessage);
        showOkDialog(this, "Saved ListItems", resultMessage);

    }

    private void retrieveListItems() {
        ListItem.getAllListItemsFromBackendlessAsync(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLog.i("TestDataActivity", "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyLog.i("TestDataActivity", "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyLog.i("TestDataActivity", "onResume");
    }
}

