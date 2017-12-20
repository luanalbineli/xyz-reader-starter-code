package com.example.xyzreader.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.util.ImageLoaderUtils;
import com.example.xyzreader.util.SplitTextUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link ArticleListActivity} in two-pane mode (on
 * tablets) or a {@link ArticleDetailActivity} on handsets.
 */
public class ArticleDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ArticleDetailFragment";

    public static final String ARG_ITEM_ID = "item_id";

    private static final int NUMBER_OF_CHARACTERS_TO_LOAD = 3000;

    private Cursor mCursor;
    private long mItemId;

    @BindView(R.id.article_title)
    TextView titleView;

    @BindView(R.id.article_body)
    TextView bodyView;

    @BindView(R.id.article_date)
    TextView articleDate;

    @BindView(R.id.article_author)
    TextView authorView;

    @BindView(R.id.background)
    SimpleDraweeView backgroundView;

    @BindView(R.id.detail_toolbar)
    Toolbar toolbar;

    @BindView(R.id.share_fab)
    FloatingActionButton fabView;

    @BindView(R.id.load_more)
    Button loadMoreView;

    @BindView(R.id.movie_detail_container)
    NestedScrollView movieDetailContainer;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayoutView;

    private String[] mBodySplitted;
    private int mBodyIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getLong(ARG_ITEM_ID);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // In support library r8, calling initLoader for a fragment in a FragmentPagerAdapter in
        // the fragment's onCreate may cause the same LoaderManager to be dealt to multiple
        // fragments because their mIndex is -1 (haven't been added to the activity yet). Thus,
        // we do this in onActivityCreated.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView of the article: " + mItemId);
        return inflater.inflate(R.layout.fragment_article_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        bindViews();
    }

    private void bindViews() {
        if (mCursor == null) {
            return;
        }

        collapsingToolbarLayoutView.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        String photoUrl = mCursor.getString(ArticleLoader.Query.PHOTO_URL);
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        ImageLoaderUtils.loadImage(backgroundView, photoUrl, metrics.widthPixels);

        String title = mCursor.getString(ArticleLoader.Query.TITLE);
        titleView.setText(title);

        configureToolbar(title);

        String date = DateUtils.getRelativeTimeSpanString(
                mCursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_ALL).toString();
        articleDate.setText(date);

        String author = mCursor.getString(ArticleLoader.Query.AUTHOR);
        authorView.setText(author);

        final Spanned body = Html.fromHtml(mCursor.getString(ArticleLoader.Query.BODY).replaceAll("(\r\n|\n)", "<br />"));
        mBodySplitted = SplitTextUtils.splitTextIntoParts(body.toString(), NUMBER_OF_CHARACTERS_TO_LOAD);
        bodyView.setText(mBodySplitted[mBodyIndex = 0]);

        /*loadMoreView.setPaintFlags(loadMoreView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        loadMoreView.setVisibility(View.VISIBLE);*/

        configureFABBehaviour(body);
    }

    @OnClick(R.id.load_more)
    public void onLoadMoreClick() {
        Log.d(TAG, "Current index: " + mBodyIndex + " | appendText: " + mBodySplitted[mBodyIndex + 1]);
        bodyView.append(mBodySplitted[++mBodyIndex]);
        if (mBodyIndex == mBodySplitted.length - 1) {
            loadMoreView.setVisibility(View.GONE);
        }
    }

    private void configureToolbar(String title) {
        Drawable backIcon = ContextCompat.getDrawable(getActivity(), R.drawable.arrow_left);
        toolbar.setTitle(title);
        toolbar.setNavigationIcon(backIcon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

    }

    private void configureFABBehaviour(final Spanned body) {
        fabView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(body.toString())
                        .getIntent(), getString(R.string.action_share)));
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "onCreateLoader of the article: " + mItemId);
        return ArticleLoader.newInstanceForItemId(getActivity(), mItemId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d(TAG, "onLoadFinished of the article: " + mItemId + " | isAdded: " + isAdded());
        if (!isAdded()) {
            if (cursor != null) {
                cursor.close();
            }
            return;
        }

        mCursor = cursor;
        if (mCursor != null && !mCursor.moveToFirst()) {
            Log.e(TAG, "Error reading item detail cursor");
            mCursor.close();
            mCursor = null;
        }

        bindViews();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        bindViews();
    }

    public static ArticleDetailFragment newInstance(long itemId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_ITEM_ID, itemId);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }
}
