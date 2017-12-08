package com.trident.beyond.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;

import com.anbetter.log.MLog;
import com.trident.beyond.R;
import com.trident.beyond.adapter.BaseViewPagerAdapter;
import com.trident.beyond.host.BinderFragment;
import com.trident.beyond.host.PageFragmentHost;
import com.trident.beyond.host.PageTabHost;
import com.trident.beyond.core.MvvmBaseFragment;
import com.trident.beyond.core.MvvmBaseView;
import com.trident.beyond.core.MvvmBaseViewModel;
import com.trident.beyond.model.TabData;
import com.trident.beyond.widgets.viewpagertab.VPTabContainer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 如果你的Fragment里面，View容器是ViewPager，那么我建议让你的Fragment继承自该类，可以提升你的工作效率。
 *
 * Created by android_ls on 16/6/3.
 */
public abstract class BaseViewPagerFragment<M, V extends MvvmBaseView<M>, VM extends MvvmBaseViewModel<M, V>>
        extends MvvmBaseFragment<M, V, VM> implements BinderFragment, ViewPager.OnPageChangeListener, PageTabHost {

    private static final String TAB_LAYOUT_INSTANCE_STATES = "TabLayoutInstanceStates";
    private static final String CURRENT_SELECTED_ITEM = "CurrentSelectedItem";
    private static final String TAG = "BaseViewPagerFragment";

    protected Context mContext;
    protected PageFragmentHost mPageFragmentHost;
    protected Handler mHandler;

    protected ViewPager mViewPager;
    protected VPTabContainer mTabContainer;
    protected BaseViewPagerAdapter mTabbedAdapter;

    protected List<TabData> mTabDataList;
    protected int mRestoreSelectedPanel;
    protected Bundle mSavedInstanceState;

    public BaseViewPagerFragment() {
        mSavedInstanceState = new Bundle();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * 包含ViewPager的布局
     *
     * @return
     */
    protected int getLayoutRes() {
        return R.layout.view_page_fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        mTabContainer = (VPTabContainer) view.findViewById(R.id.pager_tab_container);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mPageFragmentHost == null) {
            mContext = getActivity();
            mPageFragmentHost = (PageFragmentHost) getActivity();
            mHandler = new Handler();
        }
        rebindActionBar();
        if (savedInstanceState != null) {
            mSavedInstanceState = savedInstanceState;
        }
    }

    protected void showTabView() {
        if (mTabDataList == null) {
            mTabDataList = getTabData();
        }

        if (mSavedInstanceState != null) {
            restoreState();
        }

        mTabbedAdapter = createAdapter(mContext, mTabDataList);
        mViewPager.setAdapter(mTabbedAdapter);
        mViewPager.addOnPageChangeListener(this);
    }

    /**
     * @return 获取几个TAB 相关信息
     */
    protected abstract List<TabData> getTabData();

    protected abstract BaseViewPagerAdapter createAdapter(Context context, List<TabData> tabDataList);

    protected void showTabIndicator() {
        mTabContainer.setTabTextColors(ContextCompat.getColor(mContext, R.color.darkgrey),
                ContextCompat.getColor(mContext, R.color.bolo_red));
        mTabContainer.setSelectedIndicatorColor(
                ContextCompat.getColor(mContext, R.color.bolo_red));
        mTabContainer.setViewPager(mViewPager);

        mTabContainer.onPageSelected(mRestoreSelectedPanel);
        mViewPager.setCurrentItem(mRestoreSelectedPanel);
        mTabContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        recordState();
        outState.putAll(mSavedInstanceState);
        super.onSaveInstanceState(outState);
    }

    protected void recordState() {
        if(mViewPager == null || mTabDataList == null || mTabDataList.size() == 0) {
            return;
        }

        ArrayList<Bundle> instanceStates = new ArrayList<>();
        for (TabData tabData : mTabDataList) {
            if(tabData.instanceState != null) {
                instanceStates.add(tabData.instanceState);
            }
        }
        mSavedInstanceState.putParcelableArrayList(TAB_LAYOUT_INSTANCE_STATES, instanceStates);
        mSavedInstanceState.putInt(CURRENT_SELECTED_ITEM, mViewPager.getCurrentItem());
    }

    protected void restoreState() {
        if(mViewPager == null || mTabDataList == null || mTabDataList.size() == 0) {
            return;
        }

        if (mSavedInstanceState.containsKey(TAB_LAYOUT_INSTANCE_STATES)) {
            List<Bundle> restoreScrollPositions = mSavedInstanceState.getParcelableArrayList(TAB_LAYOUT_INSTANCE_STATES);
            if (restoreScrollPositions != null && restoreScrollPositions.size() > 0
                    && restoreScrollPositions.size() == mTabDataList.size()) {
                for (int i = 0; i < restoreScrollPositions.size(); i++) {
                    TabData tabData = mTabDataList.get(i);
                    tabData.instanceState = restoreScrollPositions.get(i);
                }
            }
        }
        mRestoreSelectedPanel = mSavedInstanceState.getInt(CURRENT_SELECTED_ITEM, 0);
    }

    @Override
    public void rebindActionBar() {
        mPageFragmentHost.toggleActionBar(false);
    }

    public boolean onBackPressed() {
        return false;
    }

    @Override
    public boolean onMenuBackClick(MenuItem item) {
        return onBackPressed();
    }

    @Override
    public void onDestroyView() {
        if (mTabContainer != null) {
            mTabContainer.recycler();
            mTabContainer = null;
        }

        if (mViewPager != null) {
            mViewPager.removeOnPageChangeListener(this);
            mViewPager.setAdapter(null);
            mViewPager = null;
        }

        if (mTabbedAdapter != null) {
            mTabbedAdapter.recycler();
            mTabbedAdapter = null;
        }

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mContext = null;
        mPageFragmentHost = null;
        super.onDestroy();
    }

    /**
     * 该方法请慎重调用，执行过后会清空如下数据及状态：
     * 1、当前列表中item滚动的位置
     * 2、当前选中的Page
     * 3、ViewPager中所有Page的数据
     *
     * 推荐在子类的{@link #onDestroy()}中调用，示例如下
     *
     *  @Override
        public void onDestroy() {
            super.onDestroy();
            // 重置当前Page的所有状态
            resetInstanceState();
        }
     */
    protected void resetInstanceState() {
        mRestoreSelectedPanel = 0;
        mTabDataList = null;
    }

    /**
     * 指定当前选中那个Tab，推荐在子类的{@link #***onActivityCreated(@Nullable Bundle savedInstanceState)}中调用，示例如下
     *
     * @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
     * super.onActivityCreated(savedInstanceState);
     * // 指定当前选中那个Tab
     * setCurrentPage();
     * }
     */
    protected void setCurrentPage() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mViewPager != null) {
                    mViewPager.setCurrentItem(mRestoreSelectedPanel, true);
                }
            }
        });
    }

    /**
     * 切换当前选中的Tab
     *
     * @param position Tab的下标
     */
    public void switchToTab(int position) {
        if (isAdded() && mViewPager != null && mTabContainer != null
                && position < mTabbedAdapter.getCount()) {
            mViewPager.setCurrentItem(position);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        MLog.i(TAG, "onPageSelected position = " + position);
        mRestoreSelectedPanel = position;
        if (position < getTabData().size()) {
            for (int i = 0; i < getTabData().size(); i++) {
                if (i == position) {
                    getTabData().get(position).current = 1;
                } else {
                    getTabData().get(position).current = 0;
                }
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void showBannerTips(String message) {
        if (mPageFragmentHost != null) {
            mPageFragmentHost.showGlobalBannerTips(message);
        }
    }

    @Override
    public boolean onKeyDownEvent(int code) {
        return false;
    }

    protected String getErrorMessage(Throwable error, boolean pullToRefresh) {
        return error.getMessage();
    }

    @Override
    public void dismissProgressDialog() {

    }

    /**
     * 重写onDetach()回调方法，是为了解决在Fragment彼此替换时，系统报的如下Exception
     * java.lang.IllegalStateException: No host
     * at android.support.v4.app.FragmentManagerImpl.moveToState(FragmentManager.java:1235)
     */
    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}