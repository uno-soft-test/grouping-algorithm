package ru.unosoft.grouping.util;

/**
 * Реализация структуры данных UnionFind
 */
public class UnionFind {

    private final int[] parent;

    private final int[] rank;

    /**
     * Создаёт структуру UnionFind с указанным количеством элементов.
     *
     * @param size Количество элементов в структуре.
     */
    public UnionFind(int size) {
        parent = new int[size];
        rank = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i;
            rank[i] = 1;
        }
    }

    /**
     * Находит корень группы, к которой принадлежит элемент x, с применением сжатия пути.
     *
     * @param x Элемент, для которого нужно найти корень группы.
     * @return Корень группы элемента x.
     */
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    /**
     * Объединяет группы двух элементов x и y, если они ещё не в одной группе.
     *
     * @param x Первый элемент для объединения.
     * @param y Второй элемент для объединения.
     */
    public void union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        if (rootX == rootY) return;
        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }
    }
}
