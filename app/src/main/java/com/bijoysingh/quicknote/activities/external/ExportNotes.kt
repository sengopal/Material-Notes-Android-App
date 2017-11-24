package com.bijoysingh.quicknote.activities.external

import android.Manifest
import android.content.Context
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import com.bijoysingh.quicknote.R
import com.bijoysingh.quicknote.activities.MainActivity
import com.bijoysingh.quicknote.activities.sheets.ExportNotesBottomSheet
import com.bijoysingh.quicknote.database.Note
import com.github.bijoysingh.starter.json.SafeJson
import com.github.bijoysingh.starter.util.PermissionManager
import com.github.bijoysingh.starter.util.ToastHelper
import java.io.File
import java.io.FileOutputStream


fun getNotesForExport(context: Context): String {
  val notes = Note.db(context).all
  val exportableNotes = ArrayList<String>()
  for (note in notes) {
    exportableNotes.add(ExportableNote(note).toBase64String())
  }
  val mapping = HashMap<String, ArrayList<String>>()
  mapping[ExportableNote.KEY_NOTES] = exportableNotes
  val json = SafeJson(mapping)
  return json.toString()
}

fun getStoragePermissionManager(activity: AppCompatActivity): PermissionManager {
  val manager = PermissionManager(activity as MainActivity)
  manager.setPermissions(arrayOf(
      Manifest.permission.WRITE_EXTERNAL_STORAGE,
      Manifest.permission.READ_EXTERNAL_STORAGE))
  return manager
}

fun saveFile(context: Context, text: String): Boolean {
  val folder = createFolder()
  if (folder == null) {
    return false
  }

  val file = File(folder.path + "/" + ExportNotesBottomSheet.FILE_NAME + ".txt")
  var stream: FileOutputStream? = null
  var successful = false
  try {
    stream = FileOutputStream(file, false)
    stream.write(text.toByteArray())
    stream.flush()
    successful = true
  } catch (exception: Exception) {
    // Failed
  } finally {
    try {
      if (stream != null) {
        stream.close()
      }
    } catch (exception: Exception) {
      // Failed
    }
  }
  return successful
}

fun createFolder(): File? {
  val folder = File(Environment.getExternalStorageDirectory(), ExportNotesBottomSheet.MATERIAL_NOTES_FOLDER)
  if (!folder.exists() && !folder.mkdirs()) {
    return null
  }

  return folder
}