package io.renren.entity.mongo;


import io.renren.entity.TableEntity;

import java.util.List;
import java.util.Map;

/**
 * mysql一张表只需要一個表資料和列名資料
 * 但是mongo一张表可能需要多個實體類  所以单独用一個bean封装
 *
 * @author gxz
 * @date 2020/5/10 0:14
 */
public class MongoGeneratorEntity {
    /***表資料**/
    private Map<String, String> tableInfo;
    /***主類的列名資料**/
    private List<Map<String, String>> columns;


    public TableEntity toTableEntity() {
        TableEntity tableEntity = new TableEntity();
        Map<String, String> tableInfo = this.tableInfo;
        tableEntity.setTableName(tableInfo.get("tableName"));
        tableEntity.setComments("");
        return tableEntity;
    }


    public Map<String, String> getTableInfo() {
        return tableInfo;
    }

    public MongoGeneratorEntity setTableInfo(Map<String, String> tableInfo) {
        this.tableInfo = tableInfo;
        return this;
    }

    public List<Map<String, String>> getColumns() {
        return columns;
    }

    public MongoGeneratorEntity setColumns(List<Map<String, String>> columns) {
        this.columns = columns;
        return this;
    }

}
