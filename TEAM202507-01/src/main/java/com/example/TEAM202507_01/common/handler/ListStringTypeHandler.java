package com.example.TEAM202507_01.common.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import java.sql.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// List<String> <-> DB Varchar 변환기
public class ListStringTypeHandler extends BaseTypeHandler<List<String>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        // List -> String (콤마로 연결)
        // 예: ["짜장", "짬뽕"] -> "짜장,짬뽕"
        String joined = String.join(",", parameter);
        ps.setString(i, joined);
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return convertToList(rs.getString(columnName));
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return convertToList(rs.getString(columnIndex));
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return convertToList(cs.getString(columnIndex));
    }

    // String -> List (콤마로 분리)
    private List<String> convertToList(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(dbData.split(","))
                .map(String::trim) // 공백 제거
                .collect(Collectors.toList());
    }
}