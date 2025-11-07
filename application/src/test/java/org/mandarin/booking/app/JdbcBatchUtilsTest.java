package org.mandarin.booking.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mandarin.booking.app.JdbcBatchUtils.SqlParameterBinder;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

class JdbcBatchUtilsTest {
    private final JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    private final JdbcBatchUtils utils = new JdbcBatchUtils(jdbcTemplate);

    @Test
    @DisplayName("빈 목록이면 batchUpdate 호출 안 함")
    void noOpOnEmptyItems() {
        // Arrange
        var emptyItems = List.of();

        // Act
        utils.batchUpdate("SQL", emptyItems, (ps, item) -> {
        }, 100);

        // Assert
        verify(jdbcTemplate, never()).batchUpdate(eq("SQL"), any(BatchPreparedStatementSetter.class));
    }

    @Test
    @DisplayName("batchSize에 맞춰 청크 호출 및 바인더 순서 보장")
    void chunkingAndBinderInvocationOrder() throws Exception {
        // Arrange
        var sql = "INSERT_SQL";
        List<String> items = List.of("A", "B", "C", "D", "E");
        List<String> bound = new ArrayList<>();

        SqlParameterBinder<@NonNull String> binder = (PreparedStatement ps, String item) -> bound.add(item);

        // Act
        utils.batchUpdate(sql, items, binder, 2); // 2,2,1로 분할되어 3회 호출

        // Assert
        ArgumentCaptor<BatchPreparedStatementSetter> setterCaptor = ArgumentCaptor.forClass(
                BatchPreparedStatementSetter.class);
        verify(jdbcTemplate, times(3)).batchUpdate(eq(sql), setterCaptor.capture());

        List<BatchPreparedStatementSetter> setters = setterCaptor.getAllValues();
        assertThat(setters).hasSize(3);

        PreparedStatement ps = mock(PreparedStatement.class);

        for (BatchPreparedStatementSetter s : setters) {
            int size = s.getBatchSize();
            for (int i = 0; i < size; i++) {
                s.setValues(ps, i);
            }
        }

        // 바인딩된 순서는 입력 순서와 동일해야 한다
        assertThat(bound).containsExactlyElementsOf(items);
    }

    @Test
    @DisplayName("batchSize <= 0이면 1씩 분할")
    void nonPositiveBatchSizeFallsBackToOne() throws Exception {
        // Arrange
        var sql = "UPSERT_SQL";
        List<Integer> items = List.of(1, 2, 3);
        List<Integer> bound = new ArrayList<>();

        // Act
        utils.batchUpdate(sql, items, (ps, item) -> bound.add(item), 0);

        // Assert
        ArgumentCaptor<BatchPreparedStatementSetter> setterCaptor = ArgumentCaptor.forClass(
                BatchPreparedStatementSetter.class);
        verify(jdbcTemplate, times(3)).batchUpdate(eq(sql), setterCaptor.capture());

        PreparedStatement ps = mock(PreparedStatement.class);
        for (BatchPreparedStatementSetter s : setterCaptor.getAllValues()) {
            assertThat(s.getBatchSize()).isEqualTo(1);
            s.setValues(ps, 0);
        }

        assertThat(bound).containsExactly(1, 2, 3);
    }
}

