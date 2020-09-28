package com.moaapps.squaresrepos.ui.interfaces

import com.moaapps.squaresrepos.pojo.Repo

interface OnRepoClickedListener {
    fun onLongClick(repo: Repo)
}