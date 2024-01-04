package com.example.organize3.data.email


import com.example.organize3.Searchable
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId


class EmailAccount: RealmObject, Searchable{
    @PrimaryKey
    var _id: ObjectId = ObjectId.invoke()
    var ownerId: String = ""
    var title: String = ""
    var email: String = ""
    var password: String = ""
    var remarks: String = ""
    var isArchived: Int = 0
    override fun doesMatchSearchQuery(query: String): Boolean {
        return title.contains(query.trim(), ignoreCase = true)
    }
}