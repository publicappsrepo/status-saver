
package com.appsease.status.saver.model

import android.os.Environment

enum class SaveLocation(internal val videoDir: String, internal val imageDir: String) {
    DCIM(Environment.DIRECTORY_DCIM, Environment.DIRECTORY_DCIM),
    ByFileType(Environment.DIRECTORY_MOVIES, Environment.DIRECTORY_PICTURES);
}