package com.dfmabbas.reactor.engine

import android.content.Context
import com.dfmabbas.reactor.handler.SecurityLevel
import com.dfmabbas.reactor.security.SecurityController

internal class EngineController(context: Context, db_name: String, security_level: SecurityLevel) {

    private var appContext: Context? = null;
    private var securityController: SecurityController? = null
    private var engineModel: EngineModel? = null

    private var dbName: String? = null;
    private var securityLevel: SecurityLevel? = null

    init {
        if (this.appContext == null) this.appContext = context

        if (securityController == null)
            securityController = SecurityController(appContext?.packageName!!)

        if (this.dbName == null)
            this.dbName = db_name


        if (this.securityLevel == null) this.securityLevel = security_level

        if (engineModel == null) {
            engineModel = EngineModel(appContext!!, this.dbName!!)

            if (!engineModel?.isDatabase()!!) {
                engineModel?.makeDatabase()

                if (!engineModel?.isDocument("string")!!)
                    engineModel?.makeDocument("string")

                if (!engineModel?.isDocument("bool")!!)
                    engineModel?.makeDocument("bool")

                if (!engineModel?.isDocument("int")!!)
                    engineModel?.makeDocument("int")

                if (!engineModel?.isDocument("long")!!)
                    engineModel?.makeDocument("long")

                if (!engineModel?.isDocument("double")!!)
                    engineModel?.makeDocument("double")

                if (!engineModel?.isDocument("float")!!)
                    engineModel?.makeDocument("float")

                if (!engineModel?.isDocument("unk")!!)
                    engineModel?.makeDocument("unk")
            }
        }
    }

    internal fun put(key: String, value: Any): Boolean {
        val corrected = securityController?.encryptValue(key, value.toString(), securityLevel!!)
        return engineModel?.insert(key, corrected!!, value)!!
    }

    internal fun get(key: String, type: Any): Any? {
        if (!engineModel?.isKey(key, type)!!)
            return null

        val value = securityController?.decryptValue(key,
                engineModel?.select(key, type).toString(),
                securityLevel!!)

        return value
    }

    internal fun remove(key: String, type: Any): Boolean {
        if (!engineModel?.isKey(key, type)!!)
            return true

        return engineModel?.delete(key, type)!!
    }

    internal fun clearAll(): Boolean {
        return engineModel?.clearAll()!!;
    }
}