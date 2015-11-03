package listviewapp.cts.com.listviewapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;


public class CustomListAdapter extends BaseAdapter {


    private static LayoutInflater inflater = null;
    private Context context;
    private ArrayList<String> title = new ArrayList<String>();
    private ArrayList<String> desc = new ArrayList<String>();
    private ArrayList<String> img = new ArrayList<String>();
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;


    public CustomListAdapter(Context context, ArrayList<String> title, ArrayList<String> desc, ArrayList<String> img) {
        super();
        this.context = context;
        this.title = title;
        this.desc = desc;
        this.img = img;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRequestQueue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(100);

            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }

            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });

    }


    @Override
    public int getCount() {

        return title.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_item, null);
            viewHolder.title = (TextView) convertView.findViewById(R.id.titleText);
            viewHolder.description = (TextView) convertView.findViewById(R.id.description);
            viewHolder.image = (NetworkImageView) convertView.findViewById(R.id.image);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(title.get(position));
        viewHolder.description.setText(desc.get(position));
        try {
            if (img.get(position) != null) {

                viewHolder.image.setImageUrl(img.get(position), mImageLoader);
                viewHolder.image.setDefaultImageResId(R.drawable.ic_launcher);
            }


        } catch (Exception e) {
            Log.i("error is", "e");

        }


        return convertView;
    }

    private class ViewHolder {
        private TextView title, description;
        private NetworkImageView image;

    }


}
