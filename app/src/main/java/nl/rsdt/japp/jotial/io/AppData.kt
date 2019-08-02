package nl.rsdt.japp.jotial.io

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import nl.rsdt.japp.application.Japp
import nl.rsdt.japp.application.JappPreferences
import java.io.*
import java.lang.reflect.Type

/**
 * @author Dingenis Sieger Sinke
 * @version 1.0
 * @since 29-1-2016
 * Class that handles all saving and loading operations.
 */
object AppData {

    /**
     * The directory the AppData object should operate in.
     */
    private var fDir: File? = null

    /**
     * Initializes the AppData object.
     *
     * @param dir The directory the AppData object should operate in.
     */
    fun initialize(dir: File) {
        fDir = dir
    }

    /**
     * Checks if the save exists.
     *
     * @param filename The name of the file where the save should be.
     */
    fun hasSave(filename: String): Boolean {
        return File(fDir, filename).exists()
    }

    /**
     * Saves a object in Json format.
     *
     * @param object The object that should be saved.
     * @param filename The name of the file where the object should be saved on.
     */
    fun saveObjectAsJson(`object`: Any, filename: String) {
        SaveTask(`object`, filename).run()
    }

    /**
     * Saves a object in Json format in the background.
     *
     * @param object The object that should be saved.
     * @param filename The name of the file where the object should be saved on.
     */
    fun saveObjectAsJsonInBackground(`object`: Any?, filename: String) {
        Thread(SaveTask(`object`, filename)).start()
    }

    /**
     * Saves a Drawable in a file.
     *
     * @param drawable The Drawable that should be saved.
     * @param filename The name of the file where the Drawable should be saved on.
     */
    fun saveDrawable(drawable: Drawable, filename: String) {
        SaveDrawableTask(drawable, filename).run()
    }

    /**
     * Saves a Drawable in a file in the background.
     *
     * @param drawable The Drawable that should be saved.
     * @param filename The name of the file where the Drawable should be saved on.
     */
    fun saveDrawableInBackground(drawable: Drawable, filename: String) {
        Thread(SaveDrawableTask(drawable, filename)).start()
    }

    fun delete(filename: String): Boolean {
        val file = File(fDir, filename)
        return file.delete()
    }

    /**
     * Gets a object out of the save.
     *
     * @param filename The name of the file where the object is stored.
     * @param type The type of the object.
     */
    fun <T> getObject(filename: String, type: Type): T? {
        if (!JappPreferences.loadOldData()) {
            return null
        }
        if (hasSave(filename)) {
            try {
                val file = File(fDir, filename)
                if (file.exists()) {
                    val jsonReader = JsonReader(FileReader(file))
                    jsonReader.isLenient = true
                    return Gson().fromJson<T>(jsonReader, type)
                }
                return null
            } catch (e: Exception) {
                Log.e("AppData", e.toString(), e)
            }

        } else {
            Log.e("AppData", "File was not found")
        }

        return null
    }

    /**
     * Gets the Drawable out of the save.
     *
     * @param filename The name of the file where the Drawable is stored.
     */
    fun getDrawable(filename: String): Drawable? {
        try {
            val file = File(fDir, filename)
            return BitmapDrawable(Japp.appResources, BitmapFactory.decodeStream(FileInputStream(file)))
        } catch (e: Exception) {
            Log.e("AppData", e.toString(), e)
        }

        return null
    }

    fun clear() {
        /**
         * Clear data files.
         */
        val dir = fDir
        if (dir != null){
            if (dir.exists() && dir.isDirectory) {
                val children = dir.list()
                for (i in children.indices) {
                    File(dir, children[i]).delete()
                }
            }
        }
    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 29-1-2016
     * Class for saving a object.
     */
    class SaveTask
    /**
     * Initializes a new instance of SaveTask.
     */
    (
            /**
             * The object that is going to be saved.
             */
            private val `object`: Any?,
            /**
             * The name of the file where the object is going to be saved.
             */
            private val filename: String) : Runnable {

        override fun run() {
            try {
                val file = File(fDir, filename)
                val fileWriter = FileWriter(file)
                fileWriter.write(Gson().toJson(`object`))
                fileWriter.flush()
                fileWriter.close()
            } catch (e: Exception) {
                Log.e("AppData", "Error occured", e)
            }

        }
    }

    /**
     * @author Dingenis Sieger Sinke
     * @version 1.0
     * @since 12-2-2016
     * Class for saving a drawable.
     */
    class SaveDrawableTask
    /**
     * Initializes a new instance of SaveDrawableTask.
     *
     * @param drawable The Drawable to save.
     * @param filename The name of the file where you want to save the Drawable.
     */
    (
            /**
             * The Drawable to save.
             */
            private val drawable: Drawable,
            /**
             * The name of the file where the Drawable should be saved.
             */
            private val filename: String) : Runnable {

        override
                /**
                 * Saved the Drawable to the given file.
                 */
        fun run() {
            val fos: FileOutputStream
            try {
                fos = FileOutputStream(File(fDir, filename))
                (drawable as BitmapDrawable).bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

}
