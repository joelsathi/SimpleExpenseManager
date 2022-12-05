package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentMemoryAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentMemoryTransactionDAO;


public class PersistentExpenseManager extends ExpenseManager {

    public PersistentExpenseManager(Context context) {
        super(context);
        setup(context);
    }

    @Override
    public void setup(Context context) {
        TransactionDAO persistentTransactionDAO = new PersistentMemoryTransactionDAO(context);
        setTransactionsDAO(persistentTransactionDAO);

        AccountDAO persistentAccountDAO = new PersistentMemoryAccountDAO(context);
        setAccountsDAO(persistentAccountDAO);
    }
}
