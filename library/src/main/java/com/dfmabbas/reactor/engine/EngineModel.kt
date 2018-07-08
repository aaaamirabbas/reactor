package com.dfmabbas.reactor.engine

import android.content.Context
import com.dfmabbas.reactor.engine.helper.FileHandler
import com.dfmabbas.reactor.engine.helper.getPath
import com.dfmabbas.reactor.engine.helper.getType
import org.json.JSONObject
import java.io.File

internal class EngineModel(context: Context, db_name: String) {

    private var appContext: Context? = null
    private var fileHandler: FileHandler? = null
    private var dbName: String? = null

    init {
        if (this.appContext == null) this.appContext = context
        if (this.dbName == null) this.dbName = db_name
        if (this.fileHandler == null) this.fileHandler = FileHandler(context)
    }

    internal fun makeDatabase(): Boolean {
        return File(appContext?.getPath() + dbName).mkdir()
    }

    internal fun makeDocument(name: String): Boolean {
        val file = File(appContext?.getPath() + "$dbName/$name.json")
        if (file.createNewFile()) {
            return fileHandler?.writeJSON(file, "{}")!!
        }

        return false
    }

    internal fun isDatabase(): Boolean {
        return File(appContext?.getPath() + dbName).exists()
    }

    internal fun isDocument(name: String): Boolean {
        return File(appContext?.getPath() + "$dbName/$name.json").exists()
    }

    internal fun insert(key: String, value: String, type: Any): Boolean {
        val file = getFile(type)
        val jsonObject = JSONObject(fileHandler?.readJSON(file))
        jsonObject.putOpt(key, value)

        return fileHandler?.writeJSON(file, jsonObject.toString())!!
    }

    internal fun update(key: String, value: String, type: Any): Boolean {
        val file = getFile(type)
        val jsonObject = JSONObject(fileHandler?.readJSON(file))
        if (fileHandler?.writeJSON(file, jsonObject.toString())!!)
            return true

        return false
    }

    internal fun delete(key: String, type: Any): Boolean {
        val file = getFile(type)
        val jsonObject = JSONObject(fileHandler?.readJSON(file))
        jsonObject.remove(key)

        return fileHandler?.writeJSON(file, jsonObject.toString())!!
    }

    internal fun select(key: String, type: Any): Any {
        val file = getFile(type)
        val jsonObject = JSONObject(fileHandler?.readJSON(file))

        return jsonObject.opt(key)
    }

    internal fun isKey(key: String, type: Any): Boolean {
        val file = getFile(type)
        val jsonObject = JSONObject(fileHandler?.readJSON(file))

        return jsonObject.has(key)
    }

    internal fun clearAll(): Boolean {
        return File(appContext?.getPath() + dbName).delete()
    }

    private fun getFile(type: Any): File {
        val name = type.getType()
        return File(appContext?.getPath() + "$dbName/$name.json")
    }
}