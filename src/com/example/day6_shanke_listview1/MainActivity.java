package com.example.day6_shanke_listview1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

    // Model:模型层，即数据源（使用集合来存储）,内容为新闻的标题
    private ArrayList<String> mDataList;
    private Vector<String> mUrlList; //存储新闻连接

    // View：视图层，即滑动列表
    private ListView mListView;

    // Model：模型层，即适配器
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getData();
    }


    /**
     * 访问服务器，并获得服务器返回的API数据
     */
    private void getData() {

        new Thread() {

            public void run() {
                String site = "http://v.juhe.cn/toutiao/index?type=tiyu&key=5465c4c5d60f72c3d756a9f1a9b8437d";
                try {
                    URL url = new URL(site);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    int httpCode = conn.getResponseCode();
                    if (httpCode == 200) {
                        // 字节输入流
                        InputStream is = conn.getInputStream();
                        // 字节流→字符流
                        InputStreamReader isr = new InputStreamReader(is);
                        // 转换为缓冲流提高效率
                        BufferedReader br = new BufferedReader(isr);

                        // "水盆"
                        String buffer = new String();

                        // 存储已经读取的字符
                        String readStr = new String();

                        // 循环读取
                        while ((buffer = br.readLine()) != null) {
                            // 将刚读取的字符串拼接到之前读取的字符串上
                            readStr += buffer;
                        }

                        // 关闭流
                        br.close();
                        isr.close();
                        is.close();

                        // 解析JSON数据
                        JSONObject object = new JSONObject(readStr);
                        JSONObject result = object.getJSONObject("result");
                        JSONArray data = result.getJSONArray("data"); // 拿到新闻数组
                        
                        //实例化数据源
                        mDataList = new ArrayList<String>();
                        mUrlList = new Vector<String>();
                        
                        for (int i = 0; i < data.length(); i++) { // 遍历新闻数组
                            JSONObject item = data.getJSONObject(i); // 获得第i个新闻对象
                            // 解析新闻的内容
                            String link = item.getString("url");
                            String title = item.getString("title");
                            // 添加数据到数据源中（使用Vector效果更高）
                            mDataList.add(title);
                            mUrlList.add(link);

                            Log.d("MainActivity", title);
                            Log.d("MainActivity", link);
                            Log.d("MainActivity", " ");

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    initAdapter();
                                    initList();
                                }
                            });
                        }

                    } else {
                        Log.d("MainActivity", "http code is not 200,is " + httpCode);
                    }

                } catch (MalformedURLException e) {
                    Log.d("MainActivity", e.toString());
                } catch (IOException e) {
                    Log.d("MainActivity", e.toString());
                } catch (JSONException e) {
                    Log.d("MainActivity", e.toString());
                }
            }

        }.start();

    }

    /**
     * 初始化视图层
     */
    private void initList() {
        mListView = (ListView) findViewById(R.id.list_view);
        // 补充V和P的关联
        mListView.setAdapter(mAdapter);
        // 设置单个Item的点击事件监听器
        mListView.setOnItemClickListener(new OnItemClickListener() {

            // 参数一：点击的Item所在的容器控件对象
            // 参数二：单个Item布局的最外层的布局/控件
            // 参数三：点击的item的序号（重要）
            // 参数四：id，通常等同于参数三
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获得点击的这条新闻对应的连接
                String url = mUrlList.get(position);
                //跳转传值
                Intent intent = new Intent(MainActivity.this,NewsActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });
    }

    private void initAdapter() {
        // 参数一：当前的类名.this
        // 参数二：包含TextView的布局文件的ID
        // 参数三：数据源
        mAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.item_layout, mDataList);
    }
}

