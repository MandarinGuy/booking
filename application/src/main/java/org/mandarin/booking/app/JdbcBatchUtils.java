package org.mandarin.booking.app;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JdbcBatchUtils {
    private final JdbcTemplate jdbcTemplate;

    public <T> void batchUpdate(String sql,
                                List<T> items,
                                SqlParameterBinder<T> binder,
                                int batchSize) {
        if (items.isEmpty()) {
            return;
        }
        int size = items.size();
        int chunk = max(1, batchSize);
        for (int start = 0; start < size; start += chunk) {// chunk size만큼 slice
            int end = min(size, start + chunk);
            List<T> sub = items.subList(start, end);

            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    binder.bind(ps, sub.get(i));
                }

                @Override
                public int getBatchSize() {
                    return sub.size();
                }
            });
        }
    }

    @FunctionalInterface
    public interface SqlParameterBinder<T> {
        void bind(PreparedStatement ps, T item) throws SQLException;
    }

}
