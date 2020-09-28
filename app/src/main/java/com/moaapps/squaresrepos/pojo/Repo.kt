package com.moaapps.squaresrepos.pojo

data class Repo(
    var name: String, var description: String, var owner: String,
    var repoUrl: String, var ownerUrl: String, var fork:Boolean?
) {
}