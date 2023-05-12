/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.general;

import java.util.Optional;

/**
 *
 * @author GiaTK
 */
public interface Cache<K,V> {
    boolean set(K key, V value);
    int size();
    boolean isEmpty();
    void clear();
    Optional<V> get(K key);
}
