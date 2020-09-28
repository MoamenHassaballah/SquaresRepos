package com.moaapps.squaresrepos.utils

class Resources<T>(var response:Response, var data:T?, var msg:String?) {
    companion object{
        fun<T> loading():Resources<T>{
            return Resources(Response.LOADING, null, null)
        }

        fun<T> error(msg: String):Resources<T>{
            return Resources(Response.ERROR, null, msg)
        }

        fun<T> success(data: T, msg: String?):Resources<T>{
            return Resources(Response.SUCCESS, data, msg)
        }
    }
}