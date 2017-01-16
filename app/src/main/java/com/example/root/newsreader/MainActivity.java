package com.example.root.newsreader;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MainActivity extends AppCompatActivity implements OnItemClickListener {
    private static final String urlstr = "https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty";
    private static final String HACKERS_NEWS_FRONT = "https://hacker-news.firebaseio.com/v0/item/";
    private static final String HACKERS_NEWS_END = ".json?print=pretty";
    URL url;
    HttpURLConnection urlConnection;
    InputStream in;
    HttpAsyncTask mHttpAsyncTask;
    JSONObject mjson;

    private ListView mListView;
    private List<Map<String, String>> mInfo;
    private SimpleAdapter mSimpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.listView1);
        mInfo = new ArrayList<Map<String, String>>();
        String[] ContentItem = new String[] { "title", "score", "by", "descendants" };
        int[] TextViewID = new int[] { R.id.tvTitle, R.id.tvScore, R.id.tvBy, R.id.tvDescendants };
        mSimpleAdapter = new SimpleAdapter(MainActivity.this, mInfo, R.layout.listview, ContentItem, TextViewID);
        mListView.setAdapter(mSimpleAdapter);
        mListView.setOnItemClickListener(this);

        mHttpAsyncTask = new HttpAsyncTask();
        mHttpAsyncTask.execute(urlstr);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mHttpAsyncTask != null && mHttpAsyncTask.getStatus() != AsyncTask.Status.FINISHED) {
            mHttpAsyncTask.cancel(true);
            mHttpAsyncTask = null;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String url = mInfo.get(position).get("url");
        Intent intent = new Intent(MainActivity.this, WebActivity.class);
        intent.putExtra("url", url);
        startActivityForResult(intent, 1);
    }

    public class HttpAsyncTask extends AsyncTask<String,Map<String, String>,String> {

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                url = new URL(params[0]);
                if (url != null) {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    in = urlConnection.getInputStream();
                    InputStreamReader readerNews = new InputStreamReader(in);
                    int newssize = readerNews.read();

                    while (newssize != -1) {
                        char current = (char) newssize;
                        result += current;
                        newssize = readerNews.read();
                    }

                    String[] idx = result.trim().replace("[", "").replace("]", "").replace(" ", "").split(",");

                    for (int i = 0; i < idx.length; i++) {
                        String resultNews = "";
                        URL urlNews = new URL(HACKERS_NEWS_FRONT + idx[i] + HACKERS_NEWS_END);
                        HttpURLConnection urlConnectionNews = (HttpURLConnection) urlNews.openConnection();
                        InputStream NewsdData = urlConnectionNews.getInputStream();

                        newssize = NewsdData.read();
                        while (newssize != -1) {
                            char current = (char) newssize;
                            resultNews += current;
                            newssize = NewsdData.read();
                        }
                        try {
                            mjson = new JSONObject(resultNews);
                            String url = "", title = "", score = "", by = "", descendants = "";
                            if (resultNews.contains("url"))
                                url = mjson.getString("url");
                            if (resultNews.contains("title"))
                                title = mjson.getString("title");
                            if (resultNews.contains("score"))
                                score = mjson.getString("score");
                            if (resultNews.contains("by"))
                                by = mjson.getString("by");
                            if (resultNews.contains("descendants"))
                                descendants = mjson.getString("descendants");
                            Map<String, String> item1 = new HashMap<String,String>();
                            Log.i("test", "url: " + url);
                            item1.put("url", url);
                            item1.put("title", title);
                            item1.put("score", score + " points");
                            item1.put("by", "by : " + by);
                            item1.put("descendants", "| descendants : " + descendants);
                            publishProgress(item1);
                        } catch (Exception e) {
                            // TODO: handle exception
                            e.printStackTrace();
                        }
                    }
                }
                //return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }


        @Override
        protected void onProgressUpdate(Map<String, String>... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
            mInfo.add(values[0]);
            mSimpleAdapter.notifyDataSetChanged();
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}
