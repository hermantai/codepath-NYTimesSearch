package com.gmail.htaihm.nytimessearch.articlesearch;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gmail.htaihm.nytimessearch.R;
import com.gmail.htaihm.nytimessearch.model.Article;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ArticleArrayAdapter extends ArrayAdapter<Article> {
    public ArticleArrayAdapter(Context context, List<Article> articles) {
        super(context, android.R.layout.simple_list_item_1, articles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get the data item for position
        Article article = this.getItem(position);

        // check to see if existing view being reused
        // not using a recycled view -> inflate the layout
        ViewHolder vh;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_article_result, parent, false);
            vh = new ViewHolder();
            vh.bindView(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        vh.bindItem(article);
        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.ivImage) ImageView mIvImage;
        @Bind(R.id.tvTitle) TextView mTvTitle;

        private void bindView(View v) {
            ButterKnife.bind(this, v);
        }

        private void bindItem(Article article) {
            // clear not recycled image from convertView from last time
            mIvImage.setImageResource(0);
            mTvTitle.setText(article.getHeadline());

            // populate the thumbnail image
            // remote download the image in the background

            String thumbnail = article.getThumbnail();

            if (!TextUtils.isEmpty(thumbnail)) {
                Glide.with(getContext())
                        .load(thumbnail)
                        .into(mIvImage);
            }
        }
    }
}
