package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "AccountDetails.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase myDB) {
        myDB.execSQL("CREATE TABLE userInfo( accountNo TEXT PRIMARY KEY, bankName TEXT NOT NULL, accountHolderName TEXT NOT NULL, balance REAL NOT NULL)");
        myDB.execSQL("CREATE TABLE Transactions( ID INTEGER PRIMARY KEY AUTOINCREMENT, accountNo TEXT, expenseType TEXT, transactionDate DATE, amount REAL, FOREIGN KEY (accountNo) REFERENCES userInfo(accountNo) ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase myDB, int i, int i1) {
        myDB.execSQL("DROP TABLE IF EXISTS UserInfo");
        myDB.execSQL("DROP TABLE IF EXISTS Transactions");

        // Create the database again with new tables
        onCreate(myDB);
    }
}
