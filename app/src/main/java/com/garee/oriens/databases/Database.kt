package com.garee.oriens.databases

class Database {
    var id:String? = null
    var date:String? = null
    var name :String? = null
    var rate:Int? = 0
    var split:Int? = 0
    var total:Int? = 0
    var value:Int? = null
    constructor(){}

    constructor(
        id:String?,
        date:String?,
        name :String?,
        rate:Int?,
        split:Int?,
        total:Int?,
        value:Int?
    ){
        this.id = id
        this.date = date
        this.name = name
        this.rate = rate
        this.split = split
        this.total = total
        this.value = value

    }
}