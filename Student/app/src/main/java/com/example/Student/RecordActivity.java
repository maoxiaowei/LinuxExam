package com.example.Student;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.Student.bean.StudentBean;
import com.example.Student.database.MyHelper;
import com.example.Student.utils.HttpUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView back;
    EditText name;
    EditText author;
    ImageView delete;
    ImageView save;
    MyHelper mSQLiteHelper;
    TextView book;
    String id;
    SQLiteDatabase db;
    RecordActivity.MHandler mHandler;
    static Boolean addFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        back = (ImageView) findViewById(R.id.book_back);
        name = (EditText) findViewById(R.id.book_name);
        author = (EditText) findViewById(R.id.book_author);
        delete = (ImageView) findViewById(R.id.delete);
        save = (ImageView) findViewById(R.id.save);
        book = (TextView) findViewById(R.id.book);

        back.setOnClickListener(this);
        delete.setOnClickListener(this);
        save.setOnClickListener(this);
        mHandler = new RecordActivity.MHandler();
        initData();
    }

    protected void initData() {
        mSQLiteHelper = new MyHelper(this);
        book.setText("添加记录");

        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getStringExtra("id");
            if (id != null) {
                book.setText("修改记录");
                name.setText(intent.getStringExtra("name"));
                author.setText(intent.getStringExtra("author"));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.book_back:// 后退按钮
                finish();
                break;
            case R.id.delete:// 清空按钮
                name.setText("");
                author.setText("");
                break;
            case R.id.save:
                // db = mSQLiteHelper.getWritableDatabase();
                // 获取输入内容
                String bookName = name.getText().toString().trim();
                String bookAuthor = author.getText().toString().trim();
                StudentBean studentBean = new StudentBean();
                studentBean.setId(id);
                studentBean.setName(bookName);
                studentBean.setAge(bookAuthor);

                // 向数据库中添加内容
                if (id != null) {
                    if (name.length() > 0) {
                        httpUpdate(studentBean);
                    } else {
                        showToast("修改书名不能为空");
                    }
                } else { // 添加记录界面的保存操作
                    // 向数据库中添加数据
                    if (name.length() > 0) {
                        httpAdd(studentBean);
                    } else {
                        showToast("填写书名不能为空");
                    }
                }
                // db.close();
                break;
        }
    }

    // 增加
    public void httpAdd(StudentBean req) {
        httpAddOrUpdate("add", req);
    }

    // 更新
    public void httpUpdate(StudentBean req) {
        httpAddOrUpdate("update", req);
    }

    private void httpAddOrUpdate(String action, StudentBean req) {
        Call call = new OkHttpClient().newCall(HttpUtils.postRequestBuilder(action, req));

        // andriod不能使用同步调用
        // 开启异步线程访问网络
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    // 错误处理
                    showToast("更新失败");
                    return;
                }

                // 线程内不能直接操作主线程的view，需要借助MQ
                Message msg = new Message();
                msg.obj = response.body().string();

                switch (action) {
                    case "add":
                        msg.what = HttpUtils.MSG_CREATE_OK;
                        msg.obj = req;
                        break;
                    case "update":
                        msg.what = HttpUtils.MSG_UPDATE_OK;
                        msg.obj = req;
                        break;
                }

                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, IOException e) {

            }
        });
    }

    public void showToast(String message) {
        Toast.makeText(RecordActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 事件捕获
     */
    class MHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case HttpUtils.MSG_CREATE_OK:
                case HttpUtils.MSG_UPDATE_OK:
                    if (msg.obj != null) {
                        showToast("更新成功");
                        setResult(2);
                        finish();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
