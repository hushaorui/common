package com.hushaorui.common;

import java.util.Set;

public interface TransIdStringData<Data1 extends LongIdInfoIF, Data2 extends LongIdInfoIF> extends LongIdInfoIF, Comparable<Data1> {

    Set<Data2> getDataSet();
    void setDataSet(Set<Data2> set);
    String getIds();
    void setIds(String ids);

    default int compareTo(Data1 o) {
        return this.getId().compareTo(o.getId());
    }
}
