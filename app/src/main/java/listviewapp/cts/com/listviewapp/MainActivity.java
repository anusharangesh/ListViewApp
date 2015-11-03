package listviewapp.cts.com.listviewapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {

    private final ArrayList<String> imageViewUrl = new ArrayList<String>();
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler = new Handler();
    private final Runnable refreshContent = new Runnable() {


        public void run() {
            try {
                if (swipeRefreshLayout.isRefreshing()) {
                    // Re-run the verification after 1 second
                    handler.postDelayed(this, 1000);
                } else {
                    // Stop the animation after the data is fully loaded
                    swipeRefreshLayout.setRefreshing(false);
                    fetchData();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private ArrayList<String> titleArray = new ArrayList<String>();
    private ArrayList<String> desArray = new ArrayList<String>();
    private String actionTitle;
    private CustomListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        fetchData();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handler.post(refreshContent);
            }

        });
    }

    private void fetchData() {

        String serverURL = "https://dl.dropboxusercontent.com/u/746330/facts.json";

        // Use AsyncTask execute Method To Prevent ANR Problem
        new LongOperation().execute(serverURL);
    }

    public class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            HttpClient httpClient = new DefaultHttpClient();

            HttpGet httpGet;
            String projectResult = "";
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setFollowRedirects(true);
                httpGet = new HttpGet(String.valueOf(url));
                connection.setInstanceFollowRedirects(true);

                try {
                    HttpResponse httpResponse = httpClient.execute(httpGet);

                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(
                                    httpResponse.getEntity().getContent(), "UTF-8"),
                            2 * 1024);
                    String result = null;
                    StringBuilder builder = new StringBuilder();
                    while ((result = bufferedReader.readLine()) != null) {
                        builder.append(result);
                    }
                    projectResult = builder.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return projectResult;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            try {

                JSONObject jsonResponse = new JSONObject(s);
                JSONArray output = jsonResponse.getJSONArray("rows");
                actionTitle = jsonResponse.getString("title");
                for (int i = 0; i < output.length(); i++) {
                    JSONObject proj = output.getJSONObject(i);
                    titleArray.add(proj.getString("title"));
                    desArray.add(proj.getString("description"));
                    imageViewUrl.add(proj.getString("imageHref"));

                }

                adapter = new CustomListAdapter(MainActivity.this, titleArray, desArray, imageViewUrl);
                listView.setAdapter(adapter);
                getSupportActionBar().setTitle(actionTitle);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
