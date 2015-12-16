package com.promlert.qrcodegenerator;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.promlert.qrcodegenerator.db.QRCodeDAO;

import net.glxn.qrgen.android.QRCode;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int IMAGE_WIDTH_IN_PIXEL = 500;
    private static final int IMAGE_HEIGHT_IN_PIXEL = 500;

    private ArrayList<QRCodeDAO.QrItem> mQrItemList = new ArrayList<>();
    private QrCodeListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText urlEditText = (EditText) findViewById(R.id.url_edit_text);
        final ListView qrCodeListView = (ListView) findViewById(R.id.qr_code_list_view);

        mAdapter = new QrCodeListAdapter(this, R.layout.qr_item_layout, mQrItemList);
        qrCodeListView.setAdapter(mAdapter);
        updateListView();

        Button testButton = (Button) findViewById(R.id.test_button);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlString = urlEditText.getText().toString();
                ByteArrayOutputStream qrCodeBOS = QRCode
                        .from(urlString)
                        .withSize(IMAGE_WIDTH_IN_PIXEL, IMAGE_HEIGHT_IN_PIXEL)
                        .stream();

                QRCodeDAO db = new QRCodeDAO(MainActivity.this);
                if (db.insert(urlString, qrCodeBOS.toByteArray()) > -1) {
                    Toast.makeText(MainActivity.this, "QR Code saved.", Toast.LENGTH_SHORT).show();
                    updateListView();
                }
            }
        });
    }

    private void updateListView() {
        QRCodeDAO db = new QRCodeDAO(this);
        ArrayList<QRCodeDAO.QrItem> itemList = db.readAll();

        mQrItemList.clear();
        for (QRCodeDAO.QrItem item : itemList) {
            mQrItemList.add(item);
        }
        mAdapter.notifyDataSetChanged();
    }

    private static class QrCodeListAdapter extends ArrayAdapter<QRCodeDAO.QrItem> {

        private static final String TAG = "MainActivity.QrCodeListAdapter";

        private Context mContext;
        private int mLayoutResId;
        private ArrayList<QRCodeDAO.QrItem> mQrItemList;

        public QrCodeListAdapter(Context context, int layoutResId, ArrayList<QRCodeDAO.QrItem> qrItemList) {
            super(context, layoutResId, qrItemList);
            mContext = context;
            mLayoutResId = layoutResId;
            mQrItemList = qrItemList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemLayout = convertView;
            ViewHolder holder;

            if (itemLayout == null) {
                itemLayout = View.inflate(mContext, mLayoutResId, null);

                ImageView qrCodeImageView = (ImageView) itemLayout.findViewById(R.id.qr_code_image_view);
                TextView qrCodeTextView = (TextView) itemLayout.findViewById(R.id.qr_code_text_view);

                holder = new ViewHolder(qrCodeImageView, qrCodeTextView);
                itemLayout.setTag(holder);
            }

            holder = (ViewHolder) itemLayout.getTag();
            holder.qrCodeImageView.setImageBitmap(mQrItemList.get(position).qrCodeBitmap);
            holder.qrCodeTextView.setText(mQrItemList.get(position).text);

            return itemLayout;
        }

        private static class ViewHolder {
            public final ImageView qrCodeImageView;
            public final TextView qrCodeTextView;

            public ViewHolder(ImageView qrCodeImageView, TextView qrCodeTextView) {
                this.qrCodeImageView = qrCodeImageView;
                this.qrCodeTextView = qrCodeTextView;
            }
        }
    }
}
