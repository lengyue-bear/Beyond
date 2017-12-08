package com.gank.tab.paginglist;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.trident.beyond.adapter.BaseViewPagerAdapter;
import com.trident.beyond.fragment.BaseViewPagerFragment;
import com.trident.beyond.model.BaseModel;
import com.trident.beyond.model.TabData;
import com.trident.beyond.view.BaseView;
import com.trident.beyond.viewmodel.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android_ls on 2017/1/3.
 */

public class GanKPagingListTabFragment extends BaseViewPagerFragment<BaseModel, BaseView, BaseViewModel> {

    public static GanKPagingListTabFragment newInstance() {
        GanKPagingListTabFragment fragment = new GanKPagingListTabFragment();
        return fragment;
    }

    @Override
    public void rebindActionBar() {
        mPageFragmentHost.toggleActionBar(true);
        mPageFragmentHost.setActionBarTitle("ViewPager Tab List（带分页）");
        mPageFragmentHost.displayActionBarBack(false);
    }

    @Override
    protected List<TabData> getTabData() {
        List<TabData> tabDataList = new ArrayList<>();
        TabData tabData2 = new TabData();
        tabData2.title = "精选";
        tabDataList.add(tabData2);

        TabData tabData3 = new TabData();
        tabData3.title = "往期";
        tabDataList.add(tabData3);

        return tabDataList;
    }

    @Override
    protected BaseViewPagerAdapter createAdapter(Context context, List<TabData> tabDataList) {
        return new GanKPagingListAdapter(context, tabDataList);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showTabView();
        showTabIndicator();
    }

    @Override
    public void loadData(boolean pullToRefresh) {

    }

    @Override
    public void setData(BaseModel data) {

    }

    @Override
    protected BaseViewModel createViewModel() {
        return new BaseViewModel();
    }

}