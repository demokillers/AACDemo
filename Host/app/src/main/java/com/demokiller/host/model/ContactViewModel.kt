package com.demokiller.host.model

import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.demokiller.host.database.DatabaseUtil

class ContactViewModel : ViewModel() {
    /*private val mFactory = DatabaseUtil.getInstance().contactDao().getContactByUid()
    val mContact = LivePagedListBuilder(mFactory, PagedList.Config.Builder()
            .setPageSize(2)              // 分页加载的数量
            .setInitialLoadSizeHint(2)   // 初次加载的数量
            .setPrefetchDistance(1)      // 预取数据的距离
            .setEnablePlaceholders(false) // 是否启用占位符
            .build()).build()*/
}
