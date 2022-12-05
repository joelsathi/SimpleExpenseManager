package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import static lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType.EXPENSE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentMemoryAccountDAO implements AccountDAO {
    private final DBHelper helper;
    private SQLiteDatabase myDB;

    public PersistentMemoryAccountDAO(Context context) {
        this.helper = new DBHelper(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        myDB = helper.getReadableDatabase();

        // The columns we need to project
        String[] projection = {"accountNo"};

        //The basic purpose of a cursor is to point to a single row
        // of the result fetched by the query.
        // We load the row pointed by the cursor object.
        // By using cursor we can save lot of ram and memory.
        Cursor cursor = myDB.query("userInfo", projection, null, null, null, null, null);

        // The list to store the account numbers and return
        List<String> accountNumbers = new ArrayList<String>();

        // The cursor will terminate after the last row of the query
        while (cursor.moveToNext()){
            // Get each accNum and add it to the list
            String accNum = cursor.getString(cursor.getColumnIndex("accountNo"));
            accountNumbers.add(accNum);
        }

        cursor.close();
        myDB.close();
        return accountNumbers;
    }

    @Override
    public List<Account> getAccountsList() {
        myDB = helper.getReadableDatabase();

        String[] projection = {"accountNo", "bankName", "accountHolderName", "balance"};

        Cursor cursor = myDB.query("userInfo", projection, null, null, null, null, null);

        List<Account> accounts = new ArrayList<Account>();

        while (cursor.moveToNext()){
            String accNo = cursor.getString(cursor.getColumnIndex(projection[0]));
            String bankName = cursor.getString(cursor.getColumnIndex(projection[1]));
            String accHolder = cursor.getString(cursor.getColumnIndex(projection[2]));
            double balance = cursor.getDouble(cursor.getColumnIndex(projection[3]));

            Account newAccount = new Account(accNo, bankName, accHolder, balance);
            accounts.add(newAccount);
        }

        cursor.close();
        myDB.close();
        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        myDB = helper.getReadableDatabase();

        String[] projection = {"accountNo", "bankName", "accountHolderName", "balance"};

        String selection = "accountNo = ?";
        String[] selectionArgs = {accountNo};

        Cursor cursor = myDB.query("userInfo", projection, selection, selectionArgs, null, null, null);

        if (cursor == null){
            cursor.close();
            myDB.close();
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        else{
            cursor.moveToFirst();
            String accNo = cursor.getString(cursor.getColumnIndex(projection[0]));
            String bankName = cursor.getString(cursor.getColumnIndex(projection[1]));
            String accHolder = cursor.getString(cursor.getColumnIndex(projection[2]));
            double balance = cursor.getDouble(cursor.getColumnIndex(projection[3]));

            Account newAccount = new Account(accNo, bankName, accHolder, balance);
            cursor.close();
            myDB.close();
            return  newAccount;
        }
    }

    @Override
    public void addAccount(Account account) {
        myDB = helper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("accountNo", account.getAccountNo());
        contentValues.put("bankName", account.getBankName());
        contentValues.put("accountHolderName", account.getAccountHolderName());
        contentValues.put("balance", account.getBalance());

        myDB.insert("UserInfo", null, contentValues);
        myDB.close();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        // If there is no such account
        myDB = helper.getWritableDatabase();
        Cursor cursor = myDB.query("accountNo",
                new String[] {"accountNo", "bankName", "accountHolderName", "balance"},
                "accountNo = ?",
                new String[] {accountNo},
                null, null, null);

        if (cursor == null){
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }

        // When the accountNo is present, then delete it
        myDB.delete("UserInfo", "accountNo = ?", new String[] {accountNo});
        cursor.close();
        myDB.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        myDB = helper.getWritableDatabase();

        Cursor cursor = myDB.query("userInfo",
                new String[] {"balance"},
                "accountNo = ?",
                new String[] {accountNo},
                null, null, null);

        double curBalance;
        if (cursor.moveToFirst()){
            curBalance = cursor.getDouble(cursor.getColumnIndex("balance"));
        }
        else{
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }

        ContentValues contentValues = new ContentValues();

        if (expenseType == EXPENSE){
            curBalance -= amount;
        }
        else{
            curBalance += amount;
        }

        if (curBalance > 0){
            contentValues.put("balance", curBalance);
            myDB.update("UserInfo", contentValues, "accountNo = ?", new String[] {accountNo});
        }

        cursor.close();
        myDB.close();
    }
}
