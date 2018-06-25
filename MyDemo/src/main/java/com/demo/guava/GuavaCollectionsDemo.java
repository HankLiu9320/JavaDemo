package com.demo.guava;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class GuavaCollectionsDemo {
    private void testCollections() {
        Map<String, String> map = Maps.newHashMap();
        
        Maps.newConcurrentMap();
        Sets.newHashSet();
        Sets.newConcurrentHashSet();
        
        Set<Integer> sets = Sets.newHashSet(1, 2, 3);
        map = ImmutableMap.of("ON","TRUE","OFF","FALSE");
    }

    public static void main(String[] args) {

    }
}
