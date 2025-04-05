package com.ifellow.bookstore.dao.implementations;

import com.ifellow.bookstore.dao.interfaces.SaleDao;
import com.ifellow.bookstore.datasource.DataSource;
import com.ifellow.bookstore.model.Sale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
public class SaleDaoImpl implements SaleDao {

    private final DataSource dataSource;

    public SaleDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void add(Sale sale) {
        dataSource.getSales().add(sale);
    }
}
