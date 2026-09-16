package com.dicoding.moviecatalogue.core.data.source.local.room

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.dicoding.moviecatalogue.core.data.source.local.entity.MovieEntity
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.io.File

@Database(
    entities = [MovieEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MovieDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao

    companion object {
        private const val TAG = "MovieDatabase"
        private const val DB_NAME = "Movie.db"

        @Volatile
        private var INSTANCE: MovieDatabase? = null

        fun getInstance(context: Context): MovieDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context): MovieDatabase {
            // If an existing unencrypted database exists, delete it so SQLCipher
            // can create a fresh encrypted one. All data will be re-fetched from network.
            val dbFile: File = context.getDatabasePath(DB_NAME)
            if (dbFile.exists() && !isEncryptedDatabase(dbFile)) {
                Log.w(TAG, "Found unencrypted database — deleting to migrate to SQLCipher.")
                dbFile.delete()
                // Also delete associated WAL/SHM files
                File(dbFile.path + "-wal").delete()
                File(dbFile.path + "-shm").delete()
                File(dbFile.path + "-journal").delete()
            }

            val passphrase: ByteArray =
                SQLiteDatabase.getBytes("mc_s3cur3_p@ssphr4s3".toCharArray())
            val factory: SupportSQLiteOpenHelper.Factory = SupportFactory(passphrase)

            return Room.databaseBuilder(
                context.applicationContext,
                MovieDatabase::class.java,
                DB_NAME
            )
                .openHelperFactory(factory)
                .fallbackToDestructiveMigration()
                .build()
        }

        /**
         * A SQLCipher-encrypted database starts with a 1024-byte salt header,
         * NOT the standard SQLite magic bytes "SQLite format 3\000".
         * If the file starts with that magic string, it is plaintext.
         */
        private fun isEncryptedDatabase(dbFile: File): Boolean {
            if (!dbFile.exists() || dbFile.length() < 16) return false
            return try {
                val header = ByteArray(16)
                dbFile.inputStream().use { it.read(header) }
                val sqliteMagic = "SQLite format 3\u0000"
                val headerStr = String(header, Charsets.ISO_8859_1)
                !headerStr.startsWith(sqliteMagic)
            } catch (e: Exception) {
                // If we can't read, assume it's fine (will crash on open if not)
                true
            }
        }
    }
}
