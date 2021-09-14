package com.yu.lib.common.ui.adapter.multi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.yu.lib.common.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public class MultiRecyclerAdapter<T> extends RecyclerView.Adapter<MultiRecyclerAdapter.BaseViewHolder> {
    private int mCurViewType;
    // 数据的viewType字符串 - 构造类
    private HashMap<String, BaseItemBinder<T>> mViewTypeStrBinderMap;
    // adapter内部的依据注册的viewType转换成的对应的Int Type类型 - 构造类
    private SparseArray<BaseItemBinder<T>> mViewTypeIntStrBinderMap;
    private SparseArray<String> mIntViewTypeStrTypeMap;

    private ItemDataViewTypeConverter<T> mItemDataViewTypeConverter;
    private ArrayList<T> mData;

    private HashMap<Class, HashSet<Integer>> mItemChildClickIdMap;
    private OnItemClickListener<T> mOnItemClickListener;
    private OnItemChildClickListener<T> mOnItemChildClickListener;
    private OnItemLongClickListener<T> mTOnItemLongClickListener;

    private FooterHelper mFooterHelper;
    private boolean isEnableLoadMore;
    private AbsFooterView curFooterView;

    public void setLoadMoreOnly2Bottom(boolean loadMoreOnly2Bottom) {
        if (mFooterHelper != null) {
            mFooterHelper.setLoadMoreOnly2Bottom(loadMoreOnly2Bottom);
        }
    }

    public void setHideNoMoreStr(boolean hideNoMoreStr) {
        if (mFooterHelper != null) {
            mFooterHelper.setHideNoMoreStr(hideNoMoreStr);
        }
    }

    public MultiRecyclerAdapter(ArrayList<T> data) {
        mData = data;
        mViewTypeStrBinderMap = new HashMap<>();
        mViewTypeIntStrBinderMap = new SparseArray<>();
        mIntViewTypeStrTypeMap = new SparseArray<>();
        mItemChildClickIdMap = new HashMap<>();
        mCurViewType = 0;
    }

    public class FooterHelper implements IFooterView {
        private static final int BASE_ITEM_TYPE_FOOTER = 200000;

        private final AbsFooterView mFooterView;

        private boolean mIsLoadMore;
        private boolean mIsNoMore;
        private int mLastVisibleItem;
        private int mTotalItemCount;
        private boolean mLoadMoreOnly2Bottom = true;

        FooterHelper(Context context) {
            mFooterView = new DefaultFooterView(context);
            mIsNoMore = false;
        }

        FooterHelper(Context context, AbsFooterView footerView) {
            mFooterView = footerView;
            mIsNoMore = false;
        }

        private void setLoadMoreOnly2Bottom(boolean loadMoreOnly2Bottom) {
            mLoadMoreOnly2Bottom = loadMoreOnly2Bottom;
        }

        private void setHideNoMoreStr(boolean hideNoMoreStr) {
            if(mFooterView instanceof DefaultFooterView) {
                ((DefaultFooterView) mFooterView).setHideNoMoreStr(hideNoMoreStr);
            }
        }

        private int getItemViewType(int position) {
            if (isFooterView(position)) {
                return BASE_ITEM_TYPE_FOOTER;
            }
            return -1;
        }

        private int getFooterCount() {
            return 1;
        }

        HeaderFooterViewHolder onCreateViewHolder(int viewType) {
            if (viewType == BASE_ITEM_TYPE_FOOTER) {
                StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setFullSpan(true);
                ((View) mFooterView).setLayoutParams(params);
                return new HeaderFooterViewHolder(mFooterView, mFooterView, HeaderFooterViewHolder.class);
            }
            return null;
        }

        private boolean isFooterView(int position) {
            return position >= getTargetItemCount();
        }

        private int getTargetItemCount() {
            return mData == null ? 0 : mData.size();
        }

        @Override
        public void stopLoadMoreFail() {
            mFooterView.stopLoadMoreFail();
            mIsLoadMore = false;
        }

        @Override
        public void stopLoadMoreSuccess() {
            mFooterView.stopLoadMoreSuccess();
            mIsLoadMore = false;
        }

        @Override
        public void stopLoadMoreHideProgress() {
            mFooterView.stopLoadMoreHideProgress();
            mIsLoadMore = false;
        }

        @Override
        public void stopLoadMoreNoMore() {
            mFooterView.stopLoadMoreNoMore();
            mIsLoadMore = false;
            mIsNoMore = true;
        }

        class HeaderFooterViewHolder extends BaseViewHolder {
            private HeaderFooterViewHolder(@NonNull View itemView, View contentView, Class binderClazz) {
                super(itemView, contentView, binderClazz);
            }
        }
    }

    public static class DefaultFooterView extends AbsFooterView {
        protected ProgressBar mProgressBar;
        protected TextView mTextView;
        private boolean mHideNoMoreStr;
        private OnClickListener footerClickListener;

        public DefaultFooterView(@NonNull Context context) {
            super(context);
            init();
        }

        public void setHideNoMoreStr(boolean hideNoMoreStr) {
            mHideNoMoreStr = hideNoMoreStr;
        }

        @SuppressLint("InflateParams")
        private void init() {
            View rootView = LayoutInflater.from(getContext()).inflate(R.layout.common_adapter_view_footer, null);
            addView(rootView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mProgressBar = findViewById(R.id.progress);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mProgressBar.setIndeterminateTintList(ColorStateList.valueOf(0x4C000000));
            }
            mProgressBar.setVisibility(VISIBLE);
            mTextView = findViewById(R.id.text);
        }

        @Override
        public void stopLoadMoreFail() {
            mProgressBar.setVisibility(GONE);
            mTextView.setVisibility(VISIBLE);
            mTextView.setText(R.string.view_type_lib_str_load_more_fail);
            setOnClickListener(mOnClickListener);
        }

        @Override
        public void stopLoadMoreSuccess() {
            mProgressBar.setVisibility(VISIBLE);
            mTextView.setVisibility(GONE);
        }

        @Override
        public void stopLoadMoreHideProgress() {
            mProgressBar.setVisibility(GONE);
            mTextView.setVisibility(GONE);
        }

        @Override
        public void stopLoadMoreNoMore() {
            mProgressBar.setVisibility(GONE);
            mTextView.setVisibility(VISIBLE);
            mTextView.setText(mHideNoMoreStr? "":
                    mTextView.getContext().getResources().getString(R.string.view_type_lib_str_no_more));
            setOnClickListener(null);
        }

        public void setFooterClickListener(OnClickListener footerClickListener) {
            this.footerClickListener = footerClickListener;
        }

        private final OnClickListener mOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (footerClickListener != null) {
                    footerClickListener.onClick(v);
                }
            }
        };
    }

    //接口其实写死了
    public static abstract class AbsFooterView extends FrameLayout implements IFooterView {
        public AbsFooterView(@NonNull Context context) {
            super(context);
        }

        public AbsFooterView(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }
    }

    private interface IFooterView {
        void stopLoadMoreFail();
        void stopLoadMoreSuccess();
        void stopLoadMoreHideProgress();
        void stopLoadMoreNoMore();
    }

    private OnFooterClickListener mOnFooterClickListener;
    private OnLoadMoreListener mOnLoadMoreListener;

    public void setOnFooterClickListener(OnFooterClickListener onFooterClickListener) {
        mOnFooterClickListener = onFooterClickListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    public interface OnFooterClickListener {
        void onFooterClick();
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void setEnableLoadMore(boolean isEnableLoadMore) {
        this.isEnableLoadMore = isEnableLoadMore;
    }

    public void setEnableLoadMore(boolean isEnableLoadMore, AbsFooterView footerView) {
        this.isEnableLoadMore = isEnableLoadMore;
        this.curFooterView = footerView;
    }

    public void stopLoadMoreFail() {
        if (mFooterHelper == null) {
            return;
        }
        mFooterHelper.stopLoadMoreFail();
    }

    public void stopLoadMoreHideProgress() {
        if (mFooterHelper == null) {
            return;
        }
        mFooterHelper.stopLoadMoreHideProgress();
    }

    public void stopLoadMoreSuccess() {
        if (mFooterHelper == null) {
            return;
        }
        mFooterHelper.stopLoadMoreSuccess();
    }

    public void stopLoadMoreNoMore() {
        if (mFooterHelper == null) {
            return;
        }
        mFooterHelper.stopLoadMoreNoMore();
    }

    //重置noMore标示，操蛋的后台设计，sections为空的时候居然前端报错而不是空数据，兼容用
    public void reset() {
        if (mFooterHelper != null) {
            mFooterHelper.mIsNoMore = false;
        }
    }

    public void clear() {
        mCurViewType = 0;
        mViewTypeStrBinderMap.clear();
        mViewTypeIntStrBinderMap.clear();
        mIntViewTypeStrTypeMap.clear();
    }

    public void initConverter(ItemDataViewTypeConverter<T> itemDataViewTypeConverter) {
        mItemDataViewTypeConverter = itemDataViewTypeConverter;
    }

    @NotNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int viewType) {
        BaseViewHolder holder = mFooterHelper == null ? null : mFooterHelper.onCreateViewHolder(viewType);
        if (holder != null) {
            return holder;
        }
        BaseItemBinder binder = mViewTypeIntStrBinderMap.get(viewType);
        if (binder != null) {
            FrameLayout root = (FrameLayout) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.common_adapter_root_item, viewGroup, false);
            View itemView = binder.getView(viewGroup.getContext(), viewType);
            if (itemView == null && binder.getLayoutRes() != 0) {
                itemView = LayoutInflater.from(viewGroup.getContext()).inflate(binder.getLayoutRes(), null);
            }
            if (itemView != null) {
                root.addView(itemView);
            }
            holder = new BaseViewHolder(root, itemView, binder.getClass()).setBinder(binder);
            holder.mAdapter = this;
            binder.onCreateViewHolder(holder);
        } else {
            View view = new View(viewGroup.getContext());
            view.setMinimumHeight(1);
            holder = new BaseViewHolder(view, view, View.class);
        }
        return holder;
    }

    private boolean isFooter(int position) {
        return mFooterHelper != null && mFooterHelper.isFooterView(position);
    }

    @Override
    public void onBindViewHolder(MultiRecyclerAdapter.BaseViewHolder holder, int position) {
        boolean isFooter = isFooter(position);
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            if (isFooter) {
                ((StaggeredGridLayoutManager.LayoutParams) layoutParams).setFullSpan(true);
            } else {
                ((StaggeredGridLayoutManager.LayoutParams) layoutParams).setFullSpan(false);
            }
            holder.itemView.setLayoutParams(layoutParams);
        }

        int type = getItemViewType(position);
        BaseItemBinder<T> itemBinder = mViewTypeIntStrBinderMap.get(type);
        if (itemBinder != null) {
            itemBinder.onBindViewHolder(holder, position, mData.get(position));
        }
    }

    @Override
    public final int getItemViewType(int position) {
        int loadMoreType = mFooterHelper == null ? -1 : mFooterHelper.getItemViewType(position);
        if (loadMoreType != -1) {
            return loadMoreType;
        }
        if (mItemDataViewTypeConverter != null) {
            String type = mItemDataViewTypeConverter.getViewType(position, mData.get(position));
            BaseItemBinder binder = mViewTypeStrBinderMap.get(type);
            if (binder == null) {
                //不支持的type类型
                return -1;
            } else {
                return binder.getViewType();
            }
        } else {
            if (mViewTypeIntStrBinderMap.size() > 0) {
                Iterator<String> iterator = mViewTypeStrBinderMap.keySet().iterator();
                if (iterator.hasNext()) {
                    return Objects.requireNonNull(mViewTypeStrBinderMap.get(iterator.next())).getViewType();
                } else {
                    return -1;
                }
            } else {
                return -1;
            }
        }
    }

    public void updateData(List<T> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void addData(List<T> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public final int getItemCount() {
        return mData == null ? 0 : (mFooterHelper == null ? 0 : mFooterHelper.getFooterCount()) + mData.size();
    }

    public void register(BaseItemBinder<T> itemBinder) {
        if (itemBinder == null) {
            return;
        }
        ViewType viewType = itemBinder.getClass().getAnnotation(ViewType.class);
        if (viewType != null && !TextUtils.isEmpty(viewType.value())) {
            String type = viewType.value();
            register(type, itemBinder);
        } else {
            register("default", itemBinder);
        }
    }

    private void register(String viewType, BaseItemBinder<T> itemBinder) {
        if (mViewTypeStrBinderMap.get(viewType) == null) {
            itemBinder.setViewTypeStr(viewType);
            mCurViewType++;
            itemBinder.setViewType(mCurViewType);
            mViewTypeStrBinderMap.put(viewType, itemBinder);
            mViewTypeIntStrBinderMap.put(mCurViewType, itemBinder);
            mIntViewTypeStrTypeMap.put(mCurViewType, viewType);
        }
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public <B extends BaseItemBinder> void setOnItemChildClickListener(Class<B> clazz, int id, OnItemChildClickListener<T> onItemChildClickListener) {
        mOnItemChildClickListener = onItemChildClickListener;
        HashSet<Integer> set = mItemChildClickIdMap.get(clazz);
        if (set == null) {
            set = new HashSet<>();
        }
        set.add(id);
        mItemChildClickIdMap.put(clazz, set);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<T> TOnItemLongClickListener) {
        mTOnItemLongClickListener = TOnItemLongClickListener;
    }

    public interface OnItemClickListener<T> {
        void onItemClick(View view, T data, int position);
    }

    public interface OnItemChildClickListener<T> {
        void onItemChildClick(Class<BaseItemBinder> clazz, View view, T data, int position);
    }

    public interface OnItemLongClickListener<T> {
        boolean onItemLongClick(View view, T data, int position);
    }

    @Override
    public void onViewRecycled(@NotNull MultiRecyclerAdapter.BaseViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.mBinder != null) {
            holder.mBinder.onViewRecycled(holder);
        }
    }

    /**
     * 处理GridLayoutManager
     */
    @Override
    public void onAttachedToRecyclerView(@NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) manager;
            final GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int itemType = getItemViewType(position);
                    if (itemType == FooterHelper.BASE_ITEM_TYPE_FOOTER) {
                        return gridLayoutManager.getSpanCount();
                    }
                    if (spanSizeLookup != null)
                        return spanSizeLookup.getSpanSize(position);
                    return 1;
                }
            });
        }

        if (isEnableLoadMore) {
            mFooterHelper = new FooterHelper(recyclerView.getContext(), curFooterView);
            if(curFooterView instanceof DefaultFooterView) {
                ((DefaultFooterView)curFooterView).setFooterClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mOnFooterClickListener != null) {
                            mOnFooterClickListener.onFooterClick();
                        }
                    }
                });
            }
            RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    if (manager instanceof StaggeredGridLayoutManager) {
                        StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) manager;
                        int[] lastPositions = new int[layoutManager.getSpanCount()];
                        layoutManager.findLastVisibleItemPositions(lastPositions);
                        mFooterHelper.mLastVisibleItem = findMax(lastPositions);
                    } else if (manager instanceof GridLayoutManager) {
                        mFooterHelper.mLastVisibleItem = ((GridLayoutManager) manager).findLastVisibleItemPosition();
                    } else if (manager instanceof LinearLayoutManager) {
                        LinearLayoutManager layoutManager = (LinearLayoutManager) manager;
                        mFooterHelper.mLastVisibleItem = layoutManager.findLastVisibleItemPosition();
                    } else {
                        return;
                    }
                    mFooterHelper.mTotalItemCount = manager.getItemCount();
                    if (mFooterHelper.mLoadMoreOnly2Bottom) {
                        if (!recyclerView.canScrollVertically(1) && recyclerView.canScrollVertically(-1)
                                && mFooterHelper.mLastVisibleItem == mFooterHelper.mTotalItemCount - 1 && !mFooterHelper.mIsLoadMore && !mFooterHelper.mIsNoMore) {
                            mFooterHelper.mIsLoadMore = true;
                            if (mOnLoadMoreListener != null) {
                                mOnLoadMoreListener.onLoadMore();
                            }
                        }
                    } else {
                        if (mFooterHelper.mLastVisibleItem >= mFooterHelper.mTotalItemCount - 7 && !mFooterHelper.mIsLoadMore && !mFooterHelper.mIsNoMore) {
                            mFooterHelper.mIsLoadMore = true;
                            if (mOnLoadMoreListener != null) {
                                mOnLoadMoreListener.onLoadMore();
                            }
                        }
                    }
                }

                private int findMax(int[] lastPositions) {
                    int max = lastPositions[0];
                    for (int value : lastPositions) {
                        if (value > max) {
                            max = value;
                        }
                    }
                    return max;
                }
            };
            recyclerView.addOnScrollListener(onScrollListener);
        }
    }

    @Override
    public void onViewAttachedToWindow(@NotNull MultiRecyclerAdapter.BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder.mBinder != null) {
            holder.mBinder.onViewAttachedToWindow(holder);
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NotNull MultiRecyclerAdapter.BaseViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder.mBinder != null) {
            holder.mBinder.onViewDetachedFromWindow(holder);
        }
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {
        private MultiRecyclerAdapter mAdapter;
        //view 缓存map
        private SparseArray<View> mViewMap;
        private View mContentView;

        private Object mTag;
        private HashMap<String, Object> mTagMap;
        private BaseItemBinder mBinder;

        BaseViewHolder setBinder(BaseItemBinder binder) {
            mBinder = binder;
            return this;
        }

        public MultiRecyclerAdapter getAdapter() {
            return mAdapter;
        }

        public void setTag(Object tag) {
            this.mTag = tag;
        }

        public Object getTag() {
            return mTag;
        }

        public void setTag(String key, Object tag) {
            if (mTagMap == null) {
                mTagMap = new HashMap<>();
            }
            mTagMap.put(key, tag);
        }

        public <T> T getTag(String key) {
            return (T) (mTagMap == null ? null : mTagMap.get(key));
        }

        @SuppressWarnings("unchecked")
        private BaseViewHolder(@NonNull View itemView, View contentView, final Class binderClazz) {
            super(itemView);
            mViewMap = new SparseArray<>();
            mContentView = contentView;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            HashSet<Integer> set = mItemChildClickIdMap.get(binderClazz);
            if (set != null) {
                for (Integer id : set) {
                    View v = itemView.findViewById(id);
                    if (v != null) {
                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mOnItemChildClickListener != null) {
                                    mOnItemChildClickListener.onItemChildClick(binderClazz, v, mData.get(getLayoutPosition()), getLayoutPosition());
                                }
                            }
                        });
                    }
                }
            }
        }

        public <V extends View> V getView(int id) {
            V view = (V) mViewMap.get(id);
            if (view == null) {
                view = itemView.findViewById(id);
                mViewMap.put(id, view);
            }
            return view;
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, mData.get(getLayoutPosition()), getLayoutPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mTOnItemLongClickListener != null) {
                return mTOnItemLongClickListener.onItemLongClick(v, mData.get(getLayoutPosition()), getLayoutPosition());
            }
            return true;
        }

        public int getAdapterItemCount() {
            return getItemCount();
        }

        public View getContentView() {
            return mContentView;
        }

        public BaseItemBinder getBinder() {
            return mBinder;
        }
    }

    public BaseItemBinder getViewTypeBinder(int viewType) {
        return mViewTypeIntStrBinderMap.get(viewType);
    }

    public String getStrViewType(int viewType) {
        return mIntViewTypeStrTypeMap.get(viewType);
    }

    public BaseItemBinder getViewTypeBinder(String viewType) {
        return mViewTypeStrBinderMap.get(viewType);
    }

    public HashSet<BaseItemBinder<T>> getAllBinders() {
        HashSet<BaseItemBinder<T>> set = new HashSet<>();
        for (Map.Entry<String, BaseItemBinder<T>> entry : mViewTypeStrBinderMap.entrySet()) {
            if (entry.getValue() != null) {
                set.add(entry.getValue());
            }
        }
        return set;
    }

    public ArrayList<T> getData() {
        return mData;
    }

    protected HashMap<String, BaseItemBinder<T>> getViewTypeStrBinderMap() {
        return mViewTypeStrBinderMap;
    }
}