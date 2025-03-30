package com.ifellow.bookstore.dao.implementations;

import com.ifellow.bookstore.dao.interfaces.SaleDao;
import com.ifellow.bookstore.datasource.DataSource;
import com.ifellow.bookstore.model.Sale;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SaleDaoImpl implements SaleDao {

    private final DataSource dataSource;

    @Override
    public void add(Sale sale) {
        dataSource.getSales().add(sale);
    }
}
