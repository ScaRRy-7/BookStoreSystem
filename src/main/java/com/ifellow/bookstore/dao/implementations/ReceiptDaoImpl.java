package com.ifellow.bookstore.dao.implementations;

import com.ifellow.bookstore.dao.interfaces.ReceiptDao;
import com.ifellow.bookstore.datasource.DataSource;
import com.ifellow.bookstore.model.Receipt;
import org.springframework.stereotype.Repository;

@Repository
public class ReceiptDaoImpl implements ReceiptDao {

    private final DataSource dataSource;

    public ReceiptDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void add(Receipt receipt) {
        dataSource.getReceipts().add(receipt);
    }
}
