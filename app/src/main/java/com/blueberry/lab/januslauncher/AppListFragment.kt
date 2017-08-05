package com.blueberry.lab.januslauncher

import android.app.ListFragment
import android.app.LoaderManager
import android.content.Loader
import android.os.Bundle

/**
 * Created by jojo on 8/4/17.
 */

class AppListFragment : ListFragment(), LoaderManager.LoaderCallbacks<Array<AppModel>> {
    lateinit var mAdapter : AppListAdapter

    override fun onLoaderReset(p0: Loader<Array<AppModel>>?) {
        mAdapter.clear()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setEmptyText("LOADING APPLICATIONS...")

        mAdapter = AppListAdapter(activity)

        listAdapter = mAdapter

        setListShown(false)

        loaderManager.initLoader(0, null, this)
    }

    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<Array<AppModel>> {
        return AppsLoader(activity)
    }

    override fun onLoadFinished(loader: Loader<Array<AppModel>>?, apps: Array<AppModel>?) {
        mAdapter.setData(apps)

        if (isResumed) setListShown(true)
        else setListShownNoAnimation(true)

    }
}