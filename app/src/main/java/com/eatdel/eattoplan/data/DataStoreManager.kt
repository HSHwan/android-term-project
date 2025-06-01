package com.eatdel.eattoplan.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object PreferencesKeys {
    val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
}

class DataStoreManager(private val context: Context) {
    private val Context.dataStore by preferencesDataStore("user_prefs")

    suspend fun setLoginState(isLoggedIn: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.IS_LOGGED_IN] = isLoggedIn
        }
    }

    val loginStateFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[PreferencesKeys.IS_LOGGED_IN] ?: false }
}
