package com.example.latienda

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore

object ImageController {
    fun SelectPhotoFromGallery(activity: Activity, code: Int) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        activity.startActivityForResult(intent, code)
    }
}