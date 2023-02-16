package com.hushaorui.common;

/**
 * 带有id的节点
 * @param <ID> id
 * @param <V> 数据
 */
public class CommonNode<ID, V> implements Comparable<CommonNode<ID, V>> {
    private CommonNode<ID, V> pre;
    private ID id;
    private V value;
    private CommonNode<ID, V> next;

    public CommonNode(ID id, V value) {
        this.id = id;
        this.value = value;
    }

    public CommonNode(CommonNode<ID, V> pre, ID id, V value, CommonNode<ID, V> next) {
        this.pre = pre;
        this.id = id;
        this.value = value;
        this.next = next;
    }

    public CommonNode<ID, V> getPre() {
        return pre;
    }

    public void setPre(CommonNode<ID, V> pre) {
        this.pre = pre;
    }

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public CommonNode<ID, V> getNext() {
        return next;
    }

    public void setNext(CommonNode<ID, V> next) {
        this.next = next;
    }

    @Override
    public int compareTo(CommonNode<ID, V> node) {
        V value = node.getValue();
        if (value instanceof Comparable) {
            Comparable comparable1 = (Comparable) this.value;
            Comparable comparable2 = (Comparable) value;
            return comparable1.compareTo(comparable2);
        }
        return 0;
    }
}
