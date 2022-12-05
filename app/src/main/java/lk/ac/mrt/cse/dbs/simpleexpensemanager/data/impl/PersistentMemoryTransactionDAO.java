package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentMemoryTransactionDAO implements TransactionDAO {
    private final DBHelper helper;
    private SQLiteDatabase myDB;

    public PersistentMemoryTransactionDAO(Context context) {
        this.helper = new DBHelper(context);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        myDB = helper.getWritableDatabase();

        Cursor cursor = myDB.query("userInfo",
                new String[] {"balance"},
                "accountNo = ?",
                new String[] {accountNo},
                null, null, null);

        cursor.moveToFirst();
        double curBalance = cursor.getDouble(cursor.getColumnIndex("balance"));

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        ContentValues contentValues = new ContentValues();
        contentValues.put("accountNo", accountNo);
        contentValues.put("expenseType", String.valueOf(expenseType));
        contentValues.put("transactionDate", dateFormat.format(date));
        contentValues.put("amount", amount);

        if ( ! (expenseType == ExpenseType.EXPENSE && curBalance < amount) ){
            myDB.insert("Transactions", null, contentValues);
        }

        cursor.close();
        myDB.close();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() throws ParseException {
        myDB = helper.getReadableDatabase();

        String[] projection = {"accountNo", "expenseType", "transactionDate", "amount"};

        Cursor cursor = myDB.query("Transactions", projection, null, null, null, null, null);

        List<Transaction> transactions = new ArrayList<Transaction>();

        while (cursor.moveToNext()){
            String accNo = cursor.getString(cursor.getColumnIndex(projection[0]));
            String expenseType = cursor.getString(cursor.getColumnIndex(projection[1]));
            String transDate = cursor.getString(cursor.getColumnIndex(projection[2]));
            double amount = cursor.getDouble(cursor.getColumnIndex(projection[3]));

            Date curDate = new SimpleDateFormat("dd-MM-yyyy").parse(transDate);
            ExpenseType curExpense = ExpenseType.valueOf(expenseType);

            Transaction transaction = new Transaction(curDate, accNo, curExpense, amount);
            transactions.add(transaction);
        }

        cursor.close();
        myDB.close();
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) throws ParseException {
        List<Transaction> transactions = getAllTransactionLogs();

        int length = transactions.size();

        if (length <= limit){
            return transactions;
        }

        return transactions.subList(length - limit, length);
    }
}
