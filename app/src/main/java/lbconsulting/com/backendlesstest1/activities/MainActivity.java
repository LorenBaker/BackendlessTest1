package lbconsulting.com.backendlesstest1.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.Subscription;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.Message;
import com.backendless.messaging.MessageStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lbconsulting.com.backendlesstest1.R;
import lbconsulting.com.backendlesstest1.classes.CommonMethods;
import lbconsulting.com.backendlesstest1.classes.CsvParser;
import lbconsulting.com.backendlesstest1.classes.MyLog;
import lbconsulting.com.backendlesstest1.classes.MySettings;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton mFab;
    private Button btnSendMessages;
    private Button btnClearMessages;
    private Button btnStartTestDataActivity;
    private TextView tvMessagesReceived;

    private Subscription subscription;

    private String MESSAGE_CHANNEL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("MainActivity", "onCreate()");
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "Floating action button clicked.";
                CommonMethods.showSnackbar(view, message, Snackbar.LENGTH_LONG);
            }
        });

        btnSendMessages = (Button) findViewById(R.id.btnSendMessages);
        btnClearMessages = (Button) findViewById(R.id.btnClearMessages);
        btnStartTestDataActivity = (Button) findViewById(R.id.btnStartTestDataActivity);
        tvMessagesReceived = (TextView) findViewById(R.id.tvMessagesReceived);

        btnSendMessages.setOnClickListener(this);
        btnClearMessages.setOnClickListener(this);
        btnStartTestDataActivity.setOnClickListener(this);

        MESSAGE_CHANNEL = MySettings.getActiveUserID();



        Backendless.Messaging.subscribe(MESSAGE_CHANNEL,
                new AsyncCallback<List<Message>>() {
                    @Override
                    public void handleResponse(List<Message> response) {
                        for (Message message : response) {
                            String csvDataString = message.getData().toString();
                            MessagePayload payload = new MessagePayload(csvDataString);
                            tvMessagesReceived.setText(tvMessagesReceived.getText() + "\n\n" + payload.toString());
                        }
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        MyLog.e("MainActivity", "Backendless.Messaging.subscribe()MessageCallback: BackendlessFault: " + fault.getMessage());
                    }
                }, new AsyncCallback<Subscription>() {

                    @Override
                    public void handleResponse(Subscription response) {
                        MyLog.i("MainActivity", "Backendless.Messaging.subscribe()SubscriptionCallback: response");
                        subscription = response;
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        MyLog.e("MainActivity", "Backendless.Messaging.subscribe()SubscriptionCallback: BackendlessFault: " + fault.getMessage());

                    }
                }
        );

        showActiveUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyLog.i("MainActivity", "onResume()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLog.i("MainActivity", "onDestroy()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyLog.i("MainActivity", "onPause()");
    }

    private void showActiveUser() {
        String activeUserNameAndEmail = MySettings.getActiveUserNameAndEmail();
        String msg = "User: " + activeUserNameAndEmail;
        if (activeUserNameAndEmail.isEmpty()) {
            msg = "ERROR: user not available!";
        }
        CommonMethods.showSnackbar(mFab, msg, Snackbar.LENGTH_LONG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_actvity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_who_is_logged_in) {
            showActiveUser();
            return true;

        } else if (id == R.id.action_change_my_password) {
            final String activeUserEmail = MySettings.getActiveUserEmail();
            CommonMethods.changePasswordRequest(this, activeUserEmail);
            return true;

        } else if (id == R.id.action_logout) {
            logoutUser();
            return true;

        } else if (id == R.id.action_settings) {
            Toast.makeText(this, "action_settings clicked", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void logoutUser() {
        // TODO: 2/7/2016 Figure out how to logoutUser if they uninstall the app
        if (CommonMethods.isNetworkAvailable()) {
            Backendless.UserService.logout(new AsyncCallback<Void>() {
                public void handleResponse(Void response) {
                    // user has been logged out.
                    String msg = "User logged out.";
                    MyLog.i("MainActivity", "logoutUser(): " + msg);
                    CommonMethods.showSnackbar(mFab, msg, Snackbar.LENGTH_LONG);
                    MySettings.resetActiveUserAndEmail();
                    MESSAGE_CHANNEL = MySettings.NOT_AVAILABLE;
                    startLoginActivity();
                }

                public void handleFault(BackendlessFault e) {
                    // something went wrong and logout failed, to get the error code call fault.getCode()
                    MyLog.e("MainActivity", "logoutUser(): BackendlessFault: Code = " + e.getCode() + ": " + e.getMessage());
                }
            });
        } else {
            String msg = "Unable to log user. Network is not available";
            MyLog.i("MainActivity", "logoutUser(): " + msg);
            CommonMethods.showSnackbar(mFab, msg, Snackbar.LENGTH_LONG);
        }
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, BackendlessLoginActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnSendMessages:
                Toast.makeText(this, "btnSendMessages clicked", Toast.LENGTH_SHORT).show();
                new SendMessagesAsyncTask().execute();
                break;

            case R.id.btnClearMessages:
                tvMessagesReceived.setText(getResources().getString(R.string.tvMessagesReceived_text) + "\n\n");
                break;

            case R.id.btnStartTestDataActivity:
//                Toast.makeText(this, "btnStartTestDataActivity clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, TestDataActivity.class);
                startActivity(intent);
                break;

        }
    }



/*    Backendless.Messaging.subscribe(new AsyncCallback<List<Message>>()

    {
        @Override
        public void handleResponse (List < Message > messages)
        {
            Iterator<Message> messageIterator = messages.iterator();

            while (messageIterator.hasNext()) {
                Message message = messageIterator.next();
                System.out.println("Received message - " + message.getData());
            }
        }

        @Override
        public void handleFault (BackendlessFault backendlessFault)
        {
            System.out.println("Server reported an error " + backendlessFault.getMessage());
        }
    }

    );*/

    public class SendMessagesAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyLog.i("UpAndDownloadDataAsyncTask", "onPreExecute()");
        }

        @Override
        protected Void doInBackground(Void... params) {
            MyLog.i("UpAndDownloadDataAsyncTask", "doInBackground()");
            sendMessages();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            MyLog.i("UpAndDownloadDataAsyncTask", "onPostExecute()");
        }
    }

    private void sendMessages() {

        if (MESSAGE_CHANNEL.equals(MySettings.NOT_AVAILABLE)) {
            MyLog.e("MainActivity", "sendMessages(): Unable to send messages. Message Channel is not available.");
            return;
        }

        String tableName = "SampleTable";
        String mAction = "3";
        for (int i = 1; i < 11; i++) {
            MessagePayload messagePayload = new MessagePayload(mAction, tableName, UUID.randomUUID().toString());

            Backendless.Messaging.publish(MESSAGE_CHANNEL, messagePayload.toCsvString(), new AsyncCallback<MessageStatus>() {
                @Override
                public void handleResponse(MessageStatus messageStatus) {
                    MyLog.i("MainActivity", "sendMessages(): Message published - " + messageStatus.getMessageId());
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    MyLog.e("MainActivity", "sendMessages(): BackendlessFault: " + backendlessFault.getMessage());
                }
            });

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                MyLog.e("MainActivity", "sendMessages(): InterruptedException: " + e.getMessage());
            }
        }
    }

    private class MessagePayload {
        private String mCreationTime;
        private String mAction;
        private String mTableName;
        private String mObjectUuid;

        public MessagePayload(String action, String tableName, String objectUuid) {
            this.mAction = action;
            this.mTableName = tableName;
            this.mObjectUuid = objectUuid;
            mCreationTime = String.valueOf(System.currentTimeMillis());
        }

        public MessagePayload(String csvDataString) {
            ArrayList<ArrayList<String>> records = CsvParser.CreateRecordAndFieldLists(csvDataString);
            if (records.size() > 0) {
                // load the first (and only) record.
                ArrayList<String> record = records.get(0);
                this.mAction = record.get(0);
                this.mTableName = record.get(1);
                this.mObjectUuid = record.get(2);
                mCreationTime = record.get(3);
            } else{
                MyLog.e("MessagePayload", "MessagePayload(): Unable to create MessagePayload. No data records found!");
            }
        }

        @Override
        public String toString() {
            return String.valueOf(mAction + ": " + mTableName + ": " + mObjectUuid + "\n>> " + mCreationTime);
        }

        public String toCsvString() {
            ArrayList<String> payload = new ArrayList<>();
            payload.add(mAction);
            payload.add(mTableName);
            payload.add(mObjectUuid);
            payload.add(mCreationTime);
            return CsvParser.toCSVString(payload);
        }
    }
}
